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

import java.nio.file.Paths;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringArgument;

/**
 * Split sequences in FASTA format and write to separate files in FASTA format.
 */
@SuppressWarnings("deprecation")
public final class SplitFasta implements Runnable {
    private final File fastaFile;
    private final String outputFilePrefix;
    private final String outputFileExtension;
    private final File outputDirectory;
    private static final int LINE_WIDTH = 60;
    private static final String DEFAULT_OUTPUT_FILE_PREFIX = "";
    private static final String DEFAULT_OUTPUT_FILE_EXTENSION = "fq.gz";
    private static final File DEFAULT_OUTPUT_DIRECTORY = Paths.get(".").toFile();
    private static final String USAGE = "ngs-split-fasta [args]";


    /**
     * Split sequences in FASTA format and write to separate files in FASTA format.
     *
     * @param fastaFile input FASTA file, if any
     * @param outputFilePrefix output file prefix, must not be null
     * @param outputFileExtension output file extension, must not be null
     * @param outputDirectory output directory, must not be null
     */
    public SplitFasta(final File fastaFile, final String outputFilePrefix, final String outputFileExtension, final File outputDirectory) {
        checkNotNull(outputFilePrefix);
        checkNotNull(outputFileExtension);
        checkNotNull(outputDirectory);
        this.fastaFile = fastaFile;
        this.outputFilePrefix = outputFilePrefix;
        this.outputFileExtension = outputFileExtension;
        this.outputDirectory = outputDirectory;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        try {
            reader = reader(fastaFile);
            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();

                PrintWriter writer = null;
                try {
                    // special case for IMGT/HLA _gen.fasta reference sequences; remove HLA: from HLA:HLA00001
                    String accession = sequence.getName().replace("HLA:", "");
                    writer = writer(new File(outputDirectory, outputFilePrefix + accession + "." + outputFileExtension));

                    // adapted from biojava FastaFormat since using Writer requires bending over backward
                    writer.print(">");
                    writer.println(describeSequence(sequence));

                    int length = sequence.length();
                    for (int position = 1; position < length; position += LINE_WIDTH) {
                        int end = Math.min(position + LINE_WIDTH - 1, length);
                        writer.println(sequence.subStr(position, end));
                    }
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
        }
        catch (BioException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    private static String describeSequence(final Sequence sequence) {
        return (sequence.getAnnotation().containsProperty("description_line")) ? (String) sequence.getAnnotation().getProperty("description_line") : sequence.getName();
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument fastaFile = new FileArgument("i", "fasta-file", "input FASTA file, default stdin", false);
        StringArgument outputFilePrefix = new StringArgument("p", "output-file-prefix", "output file prefix, default \"\"", false);
        StringArgument outputFileExtension = new StringArgument("x", "output-file-extension", "output file extension, default " + DEFAULT_OUTPUT_FILE_EXTENSION, false);
        FileArgument outputDirectory = new FileArgument("d", "output-directory", "output directory, default .", false);

        ArgumentList arguments = new ArgumentList(about, help, fastaFile, outputFilePrefix, outputFileExtension, outputDirectory);
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
            new SplitFasta(fastaFile.getValue(), outputFilePrefix.getValue(DEFAULT_OUTPUT_FILE_PREFIX), outputFileExtension.getValue(DEFAULT_OUTPUT_FILE_EXTENSION), outputDirectory.getValue(DEFAULT_OUTPUT_DIRECTORY)).run();
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
