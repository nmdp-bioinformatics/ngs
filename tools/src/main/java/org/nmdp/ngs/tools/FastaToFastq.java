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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Iterator;

import org.biojava.bio.BioException;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;
import org.biojava.bio.program.fastq.FastqVariant;
import org.biojava.bio.program.fastq.FastqWriter;
import org.biojava.bio.program.fastq.SangerFastqWriter;

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
import org.dishevelled.commandline.argument.IntegerArgument;

/**
 * Convert sequences in FASTA format to FASTQ format.
 */
@SuppressWarnings("deprecation")
public final class FastaToFastq implements Runnable {
    private final int quality;
    private final File fastaFile;
    private final File fastqFile;
    public static final int DEFAULT_QUALITY = 40;
    private static final String USAGE = "java FastaToFastq [args]";


    /**
     * Convert sequences in FASTA format to FASTQ format.
     *
     * @param fastaFile input FASTA file, if any
     * @param fastqFile output FASTQ file, if any
     * @param quality quality, must be in the range [0..93]
     */
    public FastaToFastq(final File fastaFile, final File fastqFile, final int quality) {
        checkArgument((quality > -1) && (quality < 94), "quality must be in the range [0..93]");
        this.quality = quality;
        this.fastaFile = fastaFile;
        this.fastqFile = fastqFile;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(fastaFile);
            writer = writer(fastqFile);
            FastqWriter fastqWriter = new SangerFastqWriter();

            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();

                StringBuilder seq = new StringBuilder();
                StringBuilder qual = new StringBuilder();
                for (Iterator<Symbol> i = sequence.iterator(); i.hasNext(); ) {
                    Symbol symbol = i.next();
                    seq.append(DNATools.dnaToken(symbol));
                    qual.append(FastqVariant.FASTQ_SANGER.quality(quality));
                }

                Fastq fastq = new FastqBuilder()
                    .withVariant(FastqVariant.FASTQ_SANGER)
                    .withDescription(sequence.getName())
                    .withSequence(seq.toString())
                    .withQuality(qual.toString())
                    .build();

                fastqWriter.append(writer, fastq);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        catch (BioException e) {
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
        Switch help = new Switch("h", "help", "display help message");
        FileArgument fastaFile = new FileArgument("i", "fasta-file", "input FASTA file, default stdin", false);
        FileArgument fastqFile = new FileArgument("o", "fastq-file", "output FASTQ file, default stdout", false);
        IntegerArgument quality = new IntegerArgument("q", "quality", "quality score for FASTQ, [0..93], default " + DEFAULT_QUALITY, false);

        ArgumentList arguments = new ArgumentList(help, fastaFile, fastqFile, quality);
        CommandLine commandLine = new CommandLine(args);
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(-2);
            }
            new FastaToFastq(fastaFile.getValue(), fastqFile.getValue(), quality.getValue(DEFAULT_QUALITY)).run();
        }
        catch (CommandLineParseException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
    }
}
