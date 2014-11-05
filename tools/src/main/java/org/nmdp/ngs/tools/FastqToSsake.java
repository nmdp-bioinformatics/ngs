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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Collections;
import java.util.List;

import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.StreamListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;

/**
 * Convert sequences in FASTQ format to SSAKE import format.
 */
public final class FastqToSsake implements Runnable {
    private final int insertSize;
    private final File firstFastqFile;
    private final File secondFastqFile;
    private final File ssakeFile;
    private final File unpairedFile;
    public static final int DEFAULT_INSERT_SIZE = 500;
    private static final Pattern LEFT = Pattern.compile("^.* 1.*$");
    private static final Pattern RIGHT = Pattern.compile("^.* 2.*$");
    private static final String USAGE = "ngs-fastq-to-ssake -1 foo_1.fq.gz -2 foo_2.fq.gz [args]\n\n   Note:  the contents of both FASTQ files are read into RAM.\n   Increase RAM to the JVM using e.g. -Xms2g -Xmx8g if necessary.";


    /**
     * Convert sequences in FASTQ format to SSAKE import format.
     *
     * @param firstFastqFile first FASTQ input file, must not be null
     * @param secondFastqFile second FASTQ input file, must not be null
     * @param ssakeFile output SSAKE file, if any
     * @param insertSize insert size, must be at least 0
     * @param unpairedFile file to write unpaired read names to, if any
     */
    public FastqToSsake(final File firstFastqFile, final File secondFastqFile, final File ssakeFile, final int insertSize, final File unpairedFile) {
        checkNotNull(firstFastqFile);
        checkNotNull(secondFastqFile);
        checkArgument((insertSize > -1), "insertSize must be at least 0");
        this.insertSize = insertSize;
        this.firstFastqFile = firstFastqFile;
        this.secondFastqFile = secondFastqFile;
        this.ssakeFile = ssakeFile;
        this.unpairedFile = unpairedFile;
    }


    @Override
    public void run() {
        PrintWriter ssakeWriter = null;
        PrintWriter unpairedWriter = null;
        try {
            ssakeWriter = writer(ssakeFile);
            unpairedWriter = (unpairedFile == null) ? null : writer(unpairedFile);

            // read both FASTQ files into RAM (ick)
            final List<Fastq> reads = Lists.newArrayList();
            SangerFastqReader fastqReader = new SangerFastqReader();
            fastqReader.stream(reader(firstFastqFile), new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        reads.add(fastq);
                    }
                });
            fastqReader.stream(reader(secondFastqFile), new StreamListener() {
                    @Override
                    public void fastq(final Fastq fastq) {
                        reads.add(fastq);
                    }
                });

            // .. and sort by description
            Collections.sort(reads, new Ordering<Fastq>() {
                    @Override
                    public int compare(final Fastq left, final Fastq right) {
                        return left.getDescription().compareTo(right.getDescription());
                    }
                });

            for (int i = 0, size = reads.size(); i < size; ) {
                Fastq left = reads.get(i);
                if ((i + 1) == size) {
                    if (unpairedWriter != null) {
                        unpairedWriter.println(left.getDescription());
                    }
                    break;
                }
                Fastq right = reads.get(i + 1);

                if (isLeft(left)) {
                    if (isRight(right)) {
                        // write paired reads to SSAKE-hacked-up version of FASTA format
                        StringBuilder sb = new StringBuilder(512);
                        sb.append(">");
                        sb.append(left.getDescription());
                        sb.append(":");
                        sb.append(right.getDescription());
                        sb.append(":");
                        sb.append(insertSize);
                        sb.append("\n");
                        sb.append(left.getSequence());
                        sb.append(":");
                        sb.append(right.getSequence());
                        ssakeWriter.println(sb.toString());
                        i += 2;
                    }
                    else {
                        if (unpairedWriter != null) {
                            unpairedWriter.println(right.getDescription());
                        }
                        i++;
                    }
                }
                else {
                    if (unpairedWriter != null) {
                        unpairedWriter.println(left.getDescription());
                    }
                    i++;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        finally {
            try {
                ssakeWriter.close();
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

    static boolean isLeft(final Fastq fastq) {
        return LEFT.matcher(fastq.getDescription()).matches();
    }

    static boolean isRight(final Fastq fastq) {
        return RIGHT.matcher(fastq.getDescription()).matches();
    }

    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument firstFastqFile = new FileArgument("1", "first-fastq-file", "first FASTQ input file", true);
        FileArgument secondFastqFile = new FileArgument("2", "second-fastq-file", "second FASTQ input file", true);
        FileArgument ssakeFile = new FileArgument("o", "ssake-file", "output SSAKE file, default stdout", false);
        FileArgument unpairedFile = new FileArgument("u", "unpaired-file", "write unpaired read names to file", false);
        IntegerArgument insertSize = new IntegerArgument("s", "insert-size", "insert size, must be at least zero, default " + DEFAULT_INSERT_SIZE, false);

        ArgumentList arguments = new ArgumentList(about, help, firstFastqFile, secondFastqFile, ssakeFile, insertSize, unpairedFile);
        CommandLine commandLine = new CommandLine(args);
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
            new FastqToSsake(firstFastqFile.getValue(), secondFastqFile.getValue(), ssakeFile.getValue(), insertSize.getValue(DEFAULT_INSERT_SIZE), unpairedFile.getValue()).run();
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
    }
}
