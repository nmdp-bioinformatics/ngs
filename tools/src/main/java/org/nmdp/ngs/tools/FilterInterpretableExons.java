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

import static org.nmdp.ngs.align.Genewise.genewiseExons;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

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

import org.nmdp.ngs.align.GenewiseExon;

/**
 * Filter interpretable exons from assembly consensus sequences.
 */
public final class FilterInterpretableExons implements Runnable {
    private final File aminoAcidHmm2File;
    private final File inputFastaFile;
    private final File outputFastaFile;
    private static final String USAGE = "ngs-filter-interpretable-exons -m A_prot.hmm2 -i consensus.fa.gz -o filtered.fa.gz";


    /**
     * Filter interpretable exons from assembly consensus sequences.
     *
     * @param aminoAcidHmm2File amino acid HMM file in HMMER2 format, must not be null
     * @param inputFastaFile input FASTA file, if any
     * @param outputFastaFile output FASTA file, if any
     */
    public FilterInterpretableExons(final File aminoAcidHmm2File, final File inputFastaFile, final File outputFastaFile) {
        checkNotNull(aminoAcidHmm2File);
        this.aminoAcidHmm2File = aminoAcidHmm2File;
        this.inputFastaFile = inputFastaFile;
        this.outputFastaFile = outputFastaFile;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputFastaFile);
            writer = writer(outputFastaFile);

            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                writer.print(">" + sequence.getName());

                StringBuilder sb = new StringBuilder();
                File tmp = writeSequenceToTempFile(sequence);
                for (GenewiseExon exon : genewiseExons(aminoAcidHmm2File, tmp)) {
                    writer.print(":" + exon);
                    // int precision should be ok here
                    sb.append(sequence.subStr((int) exon.start(), (int) exon.end()));
                }
                writer.print("\n");
                writer.println(sb.toString());
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
     * Write the specified sequence to a temporary file in FASTA format.
     *
     * @param sequence sequence to write
     * @return temporary file in FASTA format
     * @throws IOException if an I/O error occurs
     */
    static File writeSequenceToTempFile(final Sequence sequence) throws IOException {
        File tmp = File.createTempFile(sequence.getName() + "-", ".fa");
        try (FileOutputStream outputStream = new FileOutputStream(tmp)) {
            SeqIOTools.writeFasta(outputStream, sequence);
        }
        return tmp;
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument aminoAcidHmm2File = new FileArgument("m", "amino-acid-hmm2-file", "amino acid HMM file in HMMER2 format", true);
        FileArgument inputFastaFile = new FileArgument("i", "input-fasta-file", "input FASTA file, default stdin", false);
        FileArgument outputFastaFile = new FileArgument("o", "output-fasta-file", "output FASTA file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, aminoAcidHmm2File, inputFastaFile, outputFastaFile);
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
            new FilterInterpretableExons(aminoAcidHmm2File.getValue(), inputFastaFile.getValue(), outputFastaFile.getValue()).run();
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
        catch (NullPointerException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
