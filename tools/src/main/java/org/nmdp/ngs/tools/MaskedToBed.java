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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.biojava.bio.symbol.Symbol;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.align.BedRecord;
import org.nmdp.ngs.align.BedWriter;

/**
 * Convert hard-masked regions in a FASTA file to BED format.
 */
@SuppressWarnings("deprecation")
public final class MaskedToBed implements Runnable {
    private final File fastaFile;
    private final File bedFile;
    private static final String USAGE = "ngs-masked-to-bed [args]";


    /**
     * Convert hard-masked regions in a FASTA file to BED format.
     *
     * @param fastaFile input hard-masked FASTA file, if any
     * @param bedFile output BED file, if any
     */
    public MaskedToBed(final File fastaFile, final File bedFile) {
        this.fastaFile = fastaFile;
        this.bedFile = bedFile;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(fastaFile);
            writer = writer(bedFile);

            for (SequenceIterator iter = SeqIOTools.readFastaDNA(reader); iter.hasNext(); ) {
                Sequence sequence = iter.nextSequence();
                String ref = sequence.getName();

                int start = -1;
                int end = -1;
                for (int i = 1; i < sequence.length(); i++) {
                    Symbol symbol = sequence.symbolAt(i);
                    if (DNATools.n().equals(symbol)) {
                        if (start < 0) {
                            start = i;
                            end = i;
                        }
                        else {
                            end = i;
                        }
                    }
                    else {
                        if (start > 0) {
                            if (end > start) {
                                BedWriter.write(new BedRecord(ref, start - 1, end, "hard-mask"), writer);
                            }
                            start = -1;
                            end = -1;
                        }
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
                // empty
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
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
        FileArgument fastaFile = new FileArgument("i", "fasta-file", "input hard-masked FASTA file, default stdin", false);
        FileArgument bedFile = new FileArgument("o", "bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, fastaFile, bedFile);
        CommandLine commandLine = new CommandLine(args);
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            new MaskedToBed(fastaFile.getValue(), bedFile.getValue()).run();
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
