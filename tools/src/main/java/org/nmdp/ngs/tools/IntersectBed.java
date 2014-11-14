/*

    ngs-range  Guava ranges for genomics.
    Copyright (c) 2014 National Marrow Donor Program (NMDP)

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.ngs.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.github.davidmoten.rtree.RTree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.nmdp.ngs.align.BedListener;
import org.nmdp.ngs.align.BedReader;
import org.nmdp.ngs.align.BedRecord;
import org.nmdp.ngs.align.BedWriter;

import org.nmdp.ngs.range.tree.RangeList;
import org.nmdp.ngs.range.tree.RangeTree;
import org.nmdp.ngs.range.tree.CenteredRangeTree;

import org.nmdp.ngs.range.rtree.RangeGeometries;

import rx.Observable;

/**
 * Similar to bedtools2 intersect.
 */
public final class IntersectBed implements Runnable {
    private final File aInputFile;
    private final File bInputFile;
    private final File outputFile;
    private final Strategy strategy;
    private static final String DEFAULT_STRATEGY = "range-set";
    private static final String USAGE = "ngs-intersect-bed -b b.bed.gz [args]";

    public IntersectBed(final File aInputFile, final File bInputFile, final File outputFile, final Strategy strategy) {
        checkNotNull(bInputFile);
        checkNotNull(strategy);
        this.aInputFile = aInputFile;
        this.bInputFile = bInputFile;
        this.outputFile = outputFile;
        this.strategy = strategy;
    }


    @Override
    public void run() {
        BufferedReader a = null;
        BufferedReader b = null;
        PrintWriter writer = null;

        try {
            a = reader(aInputFile);
            b = reader(bInputFile);
            writer = writer(outputFile);

            strategy.intersectBed(a, b, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        finally {
            try {
                a.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                b.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    /**
     * Intersect BED strategy.
     */
    interface Strategy {
        void intersectBed(BufferedReader a, BufferedReader b, PrintWriter writer) throws IOException;
    }

    /** Map of strategies keyed by name. */
    private static final Map<String, Strategy> strategies = ImmutableMap.<String, Strategy>builder()
        .put("range-list", new RangeListStrategy())
        .put("range-set", new RangeSetStrategy())
        .put("centered-range-tree", new CenteredRangeTreeStrategy())
        .put("r-tree", new RangeRTreeStrategy())
        .put("r-star-tree", new RangeRStarTreeStrategy())
        .build();

    /** Range list strategy. */
    private static final class RangeListStrategy implements Strategy {
        @Override
        public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter writer) throws IOException {
            // read all of b into memory, group by chromosome
            final ListMultimap<String, BedRecord> ref = ArrayListMultimap.create();
            BedReader.stream(b, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    ref.put(rec.chrom(), rec);
                    return true;
                }
            });

            // calculate coverage range trees
            final Map<String, RangeTree<Long>> coverage = Maps.newHashMap();
            for (String chr : ref.keySet()) {
                List<BedRecord> records = ref.get(chr);

                List<Range<Long>> ranges = Lists.newArrayListWithExpectedSize(records.size());
                for (BedRecord rec : records) {
                    ranges.add(rec.toRange());
                }

                RangeTree<Long> rangeTree = RangeList.create(ranges);
                coverage.put(chr, rangeTree);
            }
            ref.clear();

            // stream records from a, compare to b
            final PrintWriter w = writer;
            BedReader.stream(a, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (coverage.containsKey(chr) && !coverage.get(chr).intersects(rec.toRange())) {
                        BedWriter.write(rec, w);
                    }
                    return true;
                }
            });
        }
    }

    /** Range set strategy. */
    private static final class RangeSetStrategy implements Strategy {
        @Override
        public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter writer) throws IOException {
            // calculate coverage range sets by chromosome
            final Map<String, RangeSet<Long>> coverage = Maps.newHashMap();
            BedReader.stream(b, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (!coverage.containsKey(chr)) {
                        RangeSet<Long> rangeSet = TreeRangeSet.create();
                        coverage.put(chr, rangeSet);
                    }
                    coverage.get(chr).add(rec.toRange());
                    return true;
                }
            });

            // stream records from a, compare to b
            final PrintWriter w = writer;
            BedReader.stream(a, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (coverage.containsKey(chr) && coverage.get(chr).subRangeSet(rec.toRange()).isEmpty()) {
                        BedWriter.write(rec, w);
                    }
                    return true;
                }
            });
        }
    }

    /** Centered range tree strategy. */
    private static final class CenteredRangeTreeStrategy implements Strategy {
        @Override
        public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter writer) throws IOException {
            // read all of b into memory, group by chromosome
            final ListMultimap<String, BedRecord> ref = ArrayListMultimap.create();
            BedReader.stream(b, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    ref.put(rec.chrom(), rec);
                    return true;
                }
            });

            // calculate coverage range trees
            final Map<String, RangeTree<Long>> coverage = Maps.newHashMap();
            for (String chr : ref.keySet()) {
                List<BedRecord> records = ref.get(chr);

                List<Range<Long>> ranges = Lists.newArrayListWithExpectedSize(records.size());
                for (BedRecord rec : records) {
                    ranges.add(rec.toRange());
                }

                RangeTree<Long> rangeTree = CenteredRangeTree.create(ranges);
                coverage.put(chr, rangeTree);
            }
            ref.clear();

            // stream records from a, compare to b
            final PrintWriter w = writer;
            BedReader.stream(a, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (coverage.containsKey(chr) && !coverage.get(chr).intersects(rec.toRange())) {
                        BedWriter.write(rec, w);
                    }
                    return true;
                }
            });
        }
    }

    /** Range R-Tree strategy. */
    private static final class RangeRTreeStrategy implements Strategy {
        @Override
        public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter writer) throws IOException {
            // read all of b into memory, group by chromosome
            final Object key = new Object();
            final Map<String, RTree<Object>> coverage = Maps.newHashMap();
            BedReader.stream(b, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (!coverage.containsKey(chr)) {
                        RTree rtree = RTree.maxChildren(12).create();
                        coverage.put(chr, rtree);
                    }
                    coverage.put(chr, coverage.get(chr).add(key, RangeGeometries.range(rec.toRange())));
                    return true;
                }
            });

            // stream records from a, compare to b
            final PrintWriter w = writer;
            BedReader.stream(a, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (coverage.containsKey(chr) && isEmpty(coverage.get(chr).search(RangeGeometries.range(rec.toRange())))) {
                        BedWriter.write(rec, w);
                    }
                    return true;
                }
            });
        }
    }

    /** Range R*Tree strategy. */
    private static final class RangeRStarTreeStrategy implements Strategy {
        @Override
        public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter writer) throws IOException {
            // read all of b into memory, group by chromosome
            final Object key = new Object();
            final Map<String, RTree<Object>> coverage = Maps.newHashMap();
            BedReader.stream(b, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (!coverage.containsKey(chr)) {
                        RTree rtree = RTree.star().maxChildren(12).create();
                        coverage.put(chr, rtree);
                    }
                    coverage.put(chr, coverage.get(chr).add(key, RangeGeometries.range(rec.toRange())));
                    return true;
                }
            });

            // stream records from a, compare to b
            final PrintWriter w = writer;
            BedReader.stream(a, new BedListener() {
                @Override
                public boolean record(final BedRecord rec) {
                    String chr = rec.chrom();
                    if (coverage.containsKey(chr) && isEmpty(coverage.get(chr).search(RangeGeometries.range(rec.toRange())))) {
                        BedWriter.write(rec, w);
                    }
                    return true;
                }
            });
        }
    }

    private static <T> boolean isEmpty(final Observable<T> observable) {
        return observable.isEmpty().toBlocking().first();
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch help = new Switch("h", "help", "display help message");
        FileArgument aInputFile = new FileArgument("a", "a-input-file", "A input BED file, default stdin", false);
        FileArgument bInputFile = new FileArgument("b", "b-input-file", "B input BED file", true);
        FileArgument outputFile = new FileArgument("o", "output-file", "output BED file, default stdout", false);
        StringArgument strategy = new StringArgument("s", "strategy", "strategy { range-list, range-set, centered-range-tree, r-tree, r-star-tree }, default range-set", false);

        ArgumentList arguments = new ArgumentList(help, aInputFile, bInputFile, outputFile, strategy);
        CommandLine commandLine = new CommandLine(args);
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            new IntersectBed(aInputFile.getValue(), bInputFile.getValue(), outputFile.getValue(), strategies.get(strategy.getValue(DEFAULT_STRATEGY))).run();
        }
        catch (CommandLineParseException | NullPointerException e) {
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
