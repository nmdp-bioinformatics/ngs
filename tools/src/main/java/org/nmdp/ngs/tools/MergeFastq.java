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

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.StreamListener;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.FileListArgument;

/**
 * Merge two or more files in FASTQ format.
 */
public final class MergeFastq implements Runnable {
    private final List<File> inputFastqFiles;
    private final File outputFastqFile;
    private final FastqWriter fastqWriter = new SangerFastqWriter();
    private static final String USAGE = "java MergeFastq -i foo_1.fq,bar_1.fq -o merged_1.fq";


    /**
     * Merge two or more files in FASTQ format.
     *
     * @param inputFastqFiles list of FASTQ input files, must not be null
     * @param outputFastqFile FASTQ output file, if any
     */
    public MergeFastq(final List<File> inputFastqFiles, final File outputFastqFile) {
        checkNotNull(inputFastqFiles);
        this.inputFastqFiles = ImmutableList.copyOf(inputFastqFiles);
        this.outputFastqFile = outputFastqFile;
    }


    @Override
    public void run() {
        PrintWriter writer = null;
        try {
            writer = writer(outputFastqFile);

            Append append = new Append(fastqWriter, writer);
            SangerFastqReader fastqReader = new SangerFastqReader();
            for (File inputFastqFile : inputFastqFiles) {
                fastqReader.stream(reader(inputFastqFile), append);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
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
     * Append.
     */
    private static final class Append implements StreamListener {
        private final FastqWriter fastqWriter;
        private final PrintWriter writer;

        private Append(final FastqWriter fastqWriter, final PrintWriter writer) {
            this.fastqWriter = fastqWriter;
            this.writer = writer;
        }

        @Override
        public void fastq(final Fastq fastq) {
            try {
                fastqWriter.append(writer, fastq);
            }
            catch (IOException e) {
                // todo:  rethrow?
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
        FileListArgument inputFastqFiles = new FileListArgument("i", "input-fastq-files", "list of FASTQ input files", true);
        FileArgument outputFastqFile = new FileArgument("o", "output-fastq-file", "FASTQ output file, default stdout", false);

        ArgumentList arguments = new ArgumentList(help, inputFastqFiles, outputFastqFile);
        CommandLine commandLine = new CommandLine(args);
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(-2);
            }
            new MergeFastq(inputFastqFiles.getValue(), outputFastqFile.getValue()).run();
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
