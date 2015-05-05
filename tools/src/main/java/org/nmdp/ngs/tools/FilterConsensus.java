/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.Callable;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.DoubleArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.nmdp.ngs.align.BedListener;
import org.nmdp.ngs.align.BedReader;
import org.nmdp.ngs.align.BedRecord;

import org.nmdp.ngs.feature.Allele;
import org.nmdp.ngs.feature.AlleleException;
import org.nmdp.ngs.feature.Locus;

import org.nmdp.ngs.feature.parser.FeatureParser;
import org.nmdp.ngs.feature.parser.ParseException;

import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.CigarElement;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

/**
 * Filter consensus sequences into subregions of research or clinical interest.
 */
public final class FilterConsensus implements Callable<Integer> {
    private final File bamFile;
    private final File bedFile;
    private final File outputFile;
    private final String gene;
    private final boolean cdna;
    private final boolean removeGaps;
    private final double minimumBreadth;
    private final int expectedPloidy;
    static final double DEFAULT_MINIMUM_BREADTH = 0.5d;
    static final int DEFAULT_EXPECTED_PLOIDY = 2;
    static final String USAGE = "ngs-filter-consensus -i input.bam -x genome.bed -g HLA-A [args]";


    /**
     * Filter consensus sequences into subregions of research or clinical interest.
     *
     * @param bamFile input BAM file of consensus sequences, must not be null
     * @param bedFile input BED file of genomic regions, must not be null
     * @param outputFile output FASTA file, if any
     * @param gene gene, must not be null
     * @param cdna cdna, true to output cdna from the same contig (phased consensus sequence)
     *    in FASTA format (for interpretation)
     * @param removeGaps remove alignment gaps in the filtered consensus sequence
     * @param minimumBreadth minimum breadth, must be in range [0.0 - 1.0]
     * @param expectedPloidy expected ploidy, must be &gt; 0
     */
    public FilterConsensus(final File bamFile,
                           final File bedFile,
                           final File outputFile,
                           final String gene,
                           final boolean cdna,
                           final boolean removeGaps,
                           final double minimumBreadth,
                           final int expectedPloidy) {
        checkNotNull(bamFile);
        checkNotNull(bedFile);
        checkNotNull(gene);
        checkArgument(minimumBreadth >= 0.0d && minimumBreadth <= 1.0d, "minimum breadth must be in range [0.0 - 1.0]");
        checkArgument(expectedPloidy > 0, "expected ploidy must be > 0");
        this.bamFile = bamFile;
        this.bedFile = bedFile;
        this.outputFile = outputFile;
        this.gene = gene;
        this.cdna = cdna;
        this.removeGaps = removeGaps;
        this.minimumBreadth = minimumBreadth;
        this.expectedPloidy = expectedPloidy;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(outputFile);

            // map of region alleles from BED-formatted genomic regions file keyed by "exon" as integer
            Map<Integer, Allele> exons = readBedFile(bedFile);

            // todo:  refactor this block into separate static methods
            // map of overlapping alignment alleles from BAM-formatted consensus sequence file keyed by "exon" as integer
            ListMultimap<Integer, Allele> regions = ArrayListMultimap.create();

            for (SAMRecord record : new SAMFileReader(bamFile)) {
                List<Edit> edits = cigarToEditList(record);

                String referenceName = record.getReferenceName();
                int start = record.getAlignmentStart();
                int end = record.getAlignmentEnd();
                Locus alignment = new Locus(referenceName, start, end);
                SymbolList sequence = DNATools.createDNA(record.getReadString());

                for (Edit edit : edits) {
                    sequence.edit(edit);
                }

                String name = record.getReadName();
                Allele contig = Allele.builder()
                    .withContig(referenceName)
                    .withStart(start)
                    .withEnd(end)
                    .withSequence(sequence)
                    .build();

                for (Map.Entry<Integer, Allele> entry : exons.entrySet()) {
                    int index = entry.getKey();
                    Allele exon = entry.getValue();

                    String chr = exon.getContig();
                    int min = exon.getMin();
                    int max = exon.getMax();
                    String range = chr + ":" + min + "-" + max;

                    if (alignment.overlaps(exon)) {
                        Allele xover = exon.doubleCrossover(contig);

                        Allele clipped = xover.leftHardClip("-").rightHardClip("-");
                        clipped.setName(">" + name + "|gene=" + gene + "|exon=" + index + "|location=" + range + "|" + (max - min));
                        regions.put(index, clipped);

                        int offset = 0;
                        for (Edit edit : edits) {
                            if (edit.replacement.equals(SymbolList.EMPTY_LIST) && exon.contains(edit.pos)) {
                                exon.sequence.edit(new Edit(edit.pos + offset, 0, edit.replacement));
                                offset += edit.length;
                            }
                        }
                    }
                }
            }

            // todo: improve this data structure
            Map<String, ListMultimap<Integer, Allele>> contigs = new HashMap<String, ListMultimap<Integer, Allele>>();

            for (int index : regions.keySet()) {
                for (Allele allele : regions.get(index)) {
                    int sequenceLength = allele.sequence.seqString().length();
                    List<String> fields = Splitter.on("|").splitToList(allele.getName());

                    Double locusLength = Double.parseDouble(fields.get(fields.size() - 1));

                    if (sequenceLength/locusLength >= minimumBreadth) {
                        if (!contigs.containsKey(fields.get(0))) {
                            contigs.put(fields.get(0), ArrayListMultimap.<Integer, Allele>create());
                        }
                        contigs.get(fields.get(0)).put(index, allele);

                        if (!cdna) {
                            writer.println(allele.getName() + "|" + sequenceLength);
                            writer.println(allele.sequence.seqString().toUpperCase());
                        }
                    }
                }
            }

            if (cdna) {
                Map<String, String> cdnas = new HashMap<String, String>();
                for (String contig : contigs.keySet()) {
                    StringBuilder sb = new StringBuilder();

                    for (int index : contigs.get(contig).keySet()) {
                        List<Allele> list = contigs.get(contig).get(index);
                        Collections.sort(list, new Comparator<Allele>() {
                                @Override
                                public int compare(final Allele first, final Allele second) {
                                    return first.sequence.seqString().length() - second.sequence.seqString().length();
                                }
                            });
                        Allele best = list.get(0);
                        sb.append(best.sequence.seqString());
                    }

                    String cdnaSequence = sb.toString();
                    if (removeGaps) {
                        cdnaSequence = cdnaSequence.replaceAll("-", "");
                    }

                    // todo:  use strand from genomic region file
                    if (!(gene.equals("HLA-A") || gene.equals("HLA-DPB1"))) {
                        cdnaSequence = DNATools.reverseComplement(DNATools.createDNA(cdnaSequence)).seqString();
                    }

                    cdnas.put(contig, cdnaSequence.toUpperCase());
                }
                
                List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(cdnas.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                    @Override
                    public int compare(final Map.Entry<String, String> first, final Map.Entry<String, String> second) {
                        return second.getValue().length() - first.getValue().length();
                    }
                });
                
                for (Map.Entry<String, String> entry : list.subList(0, expectedPloidy > list.size() ? list.size() : expectedPloidy)) {
                  writer.println(entry.getKey() + "\n" + entry.getValue());
                }
            }

            return 0;
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
            }
        }
    }


    static List<Edit> cigarToEditList(final SAMRecord record) throws IllegalSymbolException {
        int position = 0;
        List<Edit> edits = new ArrayList<Edit>();
        for (CigarElement element : record.getCigar().getCigarElements()) {
            position += element.getLength();

            String operator = element.getOperator().toString();
            if ("I".equals(operator) || "S".equals(operator)) {
                edits.add(new Edit(position - element.getLength() + 1, element.getLength(), SymbolList.EMPTY_LIST));
                position -= element.getLength();
            }
            else if ("D".equals(operator)) {
                SymbolList replace = DNATools.createDNA(Joiner.on("").join(Collections.nCopies(element.getLength(), "-")));
                edits.add(new Edit(position + element.getLength() - 1, 0, replace));
            }
        }
        return edits;
    }

    static Map<Integer, Allele> readBedFile(final File bedFile) throws IOException {
        BufferedReader reader = null;
        final Map<Integer, Allele> exons = new HashMap<Integer, Allele>();
        try {
            reader = reader(bedFile);
            BedReader.stream(reader, new BedListener() {
                    @Override
                     public boolean record(final BedRecord rec) {
                        try {
                            int exon = Integer.parseInt(rec.name());
                            Locus locus = FeatureParser.parseLocus(rec.chrom() + ":" + rec.start() + "-" + rec.end());
                            exons.put(exon, Allele.builder()
                                      .withContig(locus.getContig())
                                      .withStart(locus.getMin())
                                      .withEnd(locus.getMax())
                                      .build());
                        }
                        catch (AlleleException | NumberFormatException | NullPointerException | ParseException e) {
                            throw new ConversionException(rec, e);
                        }
                        return true;
                    }
                });
        }
        catch (ConversionException e) {
            throw new IOException("could not convert BED record \"" + e.rec() + "\" to allele", e.getCause());
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
        return exons;
    }

    private static class ConversionException extends RuntimeException {
        private final BedRecord rec;

        private ConversionException(final BedRecord rec, final Exception cause) {
            super(cause);
            this.rec = rec;
        }

        private BedRecord rec() {
            return rec;
        }
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument bamFile = new FileArgument("i", "bam-file", "input BAM file of consensus sequences", true);
        FileArgument bedFile = new FileArgument("x", "bed-file", "input BED file of genomic regions", true);
        FileArgument outputFile = new FileArgument("o", "output-file", "output FASTA file, default stdout", false);
        StringArgument gene = new StringArgument("g", "gene", "gene name, written to the FASTA headers", true);
        Switch cdna = new Switch("c", "cdna", "output cDNA from the same contig (phased consensus sequence) in FASTA format");
        Switch removeGaps = new Switch("r", "remove-gaps", "remove alignment gaps in the filtered consensus sequence");
        DoubleArgument minimumBreadth = new DoubleArgument("b", "minimum-breadth-of-coverage", "filter contigs less than minimum, default " + DEFAULT_MINIMUM_BREADTH, false);
        IntegerArgument expectedPloidy = new IntegerArgument("p", "expected-ploidy", "filter contigs more than expected ploidy, default " + DEFAULT_EXPECTED_PLOIDY, false);

        ArgumentList arguments = new ArgumentList(about, help, bamFile, bedFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy);
        CommandLine commandLine = new CommandLine(args);

        FilterConsensus filterConsensus = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }

            if (!bamFile.getValue().exists()) {
                throw new IllegalArgumentException("-i, --bam-file must be a file that exists");
            }
            if (!bedFile.getValue().exists()) {
                throw new IllegalArgumentException("-x, --bed-file must be a file that exists");
            }

            filterConsensus = new FilterConsensus(bamFile.getValue(),
                                                  bedFile.getValue(),
                                                  outputFile.getValue(),
                                                  gene.getValue(),
                                                  cdna.wasFound(),
                                                  removeGaps.wasFound(),
                                                  minimumBreadth.getValue(DEFAULT_MINIMUM_BREADTH),
                                                  expectedPloidy.getValue(DEFAULT_EXPECTED_PLOIDY));
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(filterConsensus.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
