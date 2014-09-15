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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.List;

import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.StreamListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.reads.paired.PairedEndAdapter;
import org.nmdp.ngs.reads.paired.PairedEndFastqReader;

/**
 * Convert first and second sequence files in FASTQ format to interleaved FASTQ format.
 */
public final class InterleaveFastq implements Runnable {
    private final File firstFastqFile;
    private final File secondFastqFile;
    private final File pairedFile;
    private final File unpairedFile;
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "java InterleaveFastq -1 foo_1.fq.gz -2 foo_2.fq.gz --paired foo.paired.fq.gz --unpaired foo.unpaired.fq.gz\n\n   Note:  the contents of both FASTQ files are read into RAM.\n   Increase RAM to the JVM using e.g. -Xms2g -Xmx8g if necessary.";


    /**
     * Convert first and second sequence files in FASTQ format to interleaved FASTQ format.
     *
     * @param firstFastqFile first FASTQ input file, must not be null
     * @param secondFastqFile second FASTQ input file, must not be null
     * @param pairedFile output interleaved paired FASTQ file, must not be null
     * @param unpairedFile output interleaved unpaired FASTQ file, must not be null
     */
    public InterleaveFastq(final File firstFastqFile, final File secondFastqFile, final File pairedFile, final File unpairedFile) {
        checkNotNull(firstFastqFile);
        checkNotNull(secondFastqFile);
        checkNotNull(pairedFile);
        checkNotNull(unpairedFile);
        this.firstFastqFile = firstFastqFile;
        this.secondFastqFile = secondFastqFile;
        this.pairedFile = pairedFile;
        this.unpairedFile = unpairedFile;
    }


    @Override
    public void run() {
        BufferedReader firstReader = null;
        BufferedReader secondReader = null;
        PrintWriter pairedWriter = null;
        PrintWriter unpairedWriter = null;
        try {
            firstReader = reader(firstFastqFile);
            secondReader = reader(secondFastqFile);
            pairedWriter = writer(pairedFile);
            unpairedWriter = writer(unpairedFile);

            final PrintWriter pw = pairedWriter;
            final PrintWriter uw = unpairedWriter;

            final FastqWriter pairedFastqWriter = new SangerFastqWriter();
            final FastqWriter unpairedFastqWriter = new SangerFastqWriter();

            PairedEndFastqReader.readPaired(firstReader, secondReader, new PairedEndAdapter() {
                    @Override
                    public void paired(final Fastq left, final Fastq right) {
                        try {
                            pairedFastqWriter.append(pw, left);
                            pairedFastqWriter.append(pw, right);
                        }
                        catch (IOException e) {
                            throw new RuntimeException("could not write paired end reads", e);
                        }
                    }

                    @Override
                    public void unpaired(final Fastq unpaired) {
                        try {
                            unpairedFastqWriter.append(uw, unpaired);
                        }
                        catch (IOException e) {
                            throw new RuntimeException("could not write unpaired read", e);
                        }
                    }
                });
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            System.exit(2);
        }
        finally {
            try {
                firstReader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                secondReader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                pairedWriter.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                unpairedWriter.close();
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
        Switch help = new Switch("h", "help", "display help message");
        FileArgument firstFastqFile = new FileArgument("1", "first-fastq-file", "first FASTQ input file", true);
        FileArgument secondFastqFile = new FileArgument("2", "second-fastq-file", "second FASTQ input file", true);
        FileArgument pairedFile = new FileArgument("p", "paired-file", "output interleaved paired FASTQ file", true);
        FileArgument unpairedFile = new FileArgument("u", "unpaired-file", "output interleaved unpaired FASTQ file", true);

        ArgumentList arguments = new ArgumentList(help, firstFastqFile, secondFastqFile, pairedFile, unpairedFile);
        CommandLine commandLine = new CommandLine(args);
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(-2);
            }
            new InterleaveFastq(firstFastqFile.getValue(), secondFastqFile.getValue(), pairedFile.getValue(), unpairedFile.getValue()).run();
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
