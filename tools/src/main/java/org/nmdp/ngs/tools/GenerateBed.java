/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.DoubleArgument;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.nmdp.ngs.align.BedRecord;
import org.nmdp.ngs.align.BedWriter;

/**
 * Generate records in BED format.
 */
public final class GenerateBed implements Callable<Integer> {
    private final File bedFile;
    private final int n;
    private final int size;
    private final String chrom;
    private final RandomGenerator random;
    private final RealDistribution length;
    private final IntegerDistribution location;
    private static final int DEFAULT_N = 1000;
    private static final int DEFAULT_SIZE = 100000;
    private static final String DEFAULT_CHROM = "1";
    private static final double NO_VARIATION = 1.0E-100;
    private static final double DEFAULT_MEAN_LENGTH = 100.0d;
    private static final double DEFAULT_LENGTH_VARIATION = 10.0d;
    private static final String USAGE = "ngs-generate-bed [args]";


    /**
     * Generate records in BED format.
     *
     * @param bedFile output BED file, if any
     * @param n number of BED records to generate, must be at least zero
     * @param size chromosome size, must be at least zero
     * @param chrom chromosome name, must not be null
     * @param random random generator, must not be null
     * @param length length distribution, must not be null
     */
    public GenerateBed(final File bedFile, final int n, final int size, final String chrom, final RandomGenerator random, final RealDistribution length) {
        checkArgument(n >= 0, "n must be at least zero");
        checkArgument(size >= 0, "size must be at least zero");
        checkNotNull(chrom);
        checkNotNull(random);
        checkNotNull(length);
        this.bedFile = bedFile;
        this.n = n;
        this.size = size;
        this.chrom = chrom;
        this.random = random;
        this.length = length;

        int flanking = (int) (length.getNumericalMean() + length.getNumericalVariance());
        location = new UniformIntegerDistribution(this.random, 1 - flanking, size + flanking);
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(bedFile);

            for (int i = 0; i < n; i++) {
                // sample location and length
                long start = (long) location.sample();
                long end = start + 1L + (long) length.sample();

                // trim to chrom ends
                long adjStart = Math.min((long) (size - 1), Math.max(0L, start));
                long adjEnd = Math.max(adjStart + 1L, Math.min((long) (size - 1), end));

                // and write
                BedWriter.write(new BedRecord(chrom, adjStart, adjEnd, "record" + i, "0", "+"), writer);
            }

            return 0;
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument bedFile = new FileArgument("o", "bed-file", "output BED file, default stdout", false);
        IntegerArgument n = new IntegerArgument("n", "n", "number of BED records to generate, default " + DEFAULT_N, false);
        IntegerArgument size = new IntegerArgument("s", "size", "chromosome size, default " + DEFAULT_SIZE, false);
        StringArgument chrom = new StringArgument("c", "chrom", "chromosome name, default " + DEFAULT_CHROM, false);
        DoubleArgument meanLength = new DoubleArgument("l", "mean-length", "mean length, default " + DEFAULT_MEAN_LENGTH, false);
        DoubleArgument lengthVariation = new DoubleArgument("v", "length-variation", "length variation, default " + DEFAULT_LENGTH_VARIATION, false);
        IntegerArgument seed = new IntegerArgument("z", "seed", "random number seed, default relates to current time", false);

        ArgumentList arguments = new ArgumentList(about, help, bedFile, n, size, chrom, meanLength, lengthVariation, seed);
        CommandLine commandLine = new CommandLine(args);

        GenerateBed generateBed = null;
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

            RandomGenerator random = seed.wasFound() ? new MersenneTwister(seed.getValue()) : new MersenneTwister();
            double lv = Math.max(NO_VARIATION, lengthVariation.getValue(DEFAULT_LENGTH_VARIATION));
            RealDistribution length = new NormalDistribution(random, meanLength.getValue(DEFAULT_MEAN_LENGTH), lv, NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);

            generateBed = new GenerateBed(bedFile.getValue(), n.getValue(DEFAULT_N), size.getValue(DEFAULT_SIZE), chrom.getValue(DEFAULT_CHROM), random, length);
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(generateBed.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
