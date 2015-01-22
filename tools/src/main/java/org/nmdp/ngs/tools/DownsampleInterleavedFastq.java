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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.concurrent.Callable;

import org.apache.commons.math3.distribution.BinomialDistribution;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.DoubleArgument;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;

import org.nmdp.ngs.reads.paired.PairedEndAdapter;
import org.nmdp.ngs.reads.paired.PairedEndFastqReader;

/**
 * Downsample sequences from a file in interleaved FASTQ format.
 */
public final class DownsampleInterleavedFastq implements Callable<Integer> {
    private final File inputFastqFile;
    private final File outputFastqFile;
    private final BinomialDistribution distribution;
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "ngs-downsample-interleaved-fastq -p 0.5 [args]";


    /**
     * Downsample sequences from a file in interleaved FASTQ format.
     *
     * @param inputFastqFile input interleaved FASTQ file, if any
     * @param outputFastqFile output interleaved FASTQ file, if any
     * @param distribution binomial distribution, must not be null
     */
    public DownsampleInterleavedFastq(final File inputFastqFile, final File outputFastqFile, final BinomialDistribution distribution) {
        checkNotNull(distribution);
        this.inputFastqFile = inputFastqFile;
        this.outputFastqFile = outputFastqFile;
        this.distribution = distribution;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastqFile);
            writer = writer(outputFastqFile);

            final PrintWriter w = writer;
            PairedEndFastqReader.streamInterleaved(reader, new PairedEndAdapter() {
                    @Override
                    public void paired(final Fastq left, final Fastq right) {
                        if (distribution.sample() > 0) {
                            try {
                                fastqWriter.append(w, left);
                                fastqWriter.append(w, right);
                            }
                            catch (IOException e) {
                                throw new RuntimeException("could not write FASTQ", e);
                            }
                        }
                    }
                });

            return 0;
        }
        finally {
            try {
                reader.close();
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
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputFastqFile = new FileArgument("i", "input-fastq-file", "input interleaved FASTQ file, default stdin", false);
        FileArgument outputFastqFile = new FileArgument("o", "output-fastq-file", "output interleaved FASTQ file, default stdout", false);
        DoubleArgument probability = new DoubleArgument("p", "probability", "probability a FASTQ record will be removed, [0.0-1.0]", true);
        IntegerArgument seed = new IntegerArgument("z", "seed", "random number seed, default relates to current time", false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastqFile, outputFastqFile, probability, seed);
        CommandLine commandLine = new CommandLine(args);

        DownsampleInterleavedFastq downsampleInterleavedFastq = null;
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
            BinomialDistribution distribution = new BinomialDistribution(random, 1, probability.getValue());

            downsampleInterleavedFastq = new DownsampleInterleavedFastq(inputFastqFile.getValue(), outputFastqFile.getValue(), distribution);
        }
        catch (CommandLineParseException e) {
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
        catch (IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(downsampleInterleavedFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
