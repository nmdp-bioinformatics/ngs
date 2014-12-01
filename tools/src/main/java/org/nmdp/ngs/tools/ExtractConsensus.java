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

import java.util.concurrent.Callable;

import javax.xml.bind.JAXBElement;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.SymbolList;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.hml.HmlReader;

import org.nmdp.ngs.hml.jaxb.ConsensusSequence;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Region;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.SbtNgs;
import org.nmdp.ngs.hml.jaxb.Sequence;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Extract consensus sequences from a file in HML format.
 */
public final class ExtractConsensus implements Callable<Integer> {
    private final File inputHmlFile;
    private static final String USAGE = "ngs-extract-consensus [args]";


    /**
     * Extract consensus sequences from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     */
    public ExtractConsensus(final File inputHmlFile) {
        this.inputHmlFile = inputHmlFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputHmlFile);
            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String id = sample.getId();

                for (Typing typing : sample.getTyping()) {
                    for (JAXBElement<?> element : typing.getTypingMethod()) {
                        if (element.getValue() instanceof SbtNgs) {
                            SbtNgs sbtNgs = (SbtNgs) element.getValue();
                            String locus = sbtNgs.getLocus();
                            for (ConsensusSequence consensusSequence : sbtNgs.getConsensusSequence()) {
                                Region region = consensusSequence.getRegion();

                                int count = 0;
                                try (PrintWriter writer = writer(new File(id + "_" + locus + ".fa.gz"))) {
                                    for (Sequence sequence : consensusSequence.getSequence()) {
                                        StringBuilder sb = new StringBuilder(1200);
                                        sb.append(">");
                                        sb.append(id);
                                        sb.append(" ");
                                        sb.append(locus);
                                        sb.append(" ");
                                        sb.append(count);
                                        sb.append("\n");

                                        // todo: move to HlaUtils
                                        SymbolList symbolList = DNATools.createDNA(sequence.getValue().replaceAll("\\s+", ""));
                                        sb.append(symbolList.seqString());
                                        writer.println(sb.toString());

                                        count++;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return 0;
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


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument inputHmlFile = new FileArgument("i", "input-hml-file", "input HML file, default stdin", false);

        ArgumentList arguments = new ArgumentList(about, help, inputHmlFile);
        CommandLine commandLine = new CommandLine(args);

        ExtractConsensus extractConsensus = null;
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
            extractConsensus = new ExtractConsensus(inputHmlFile.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractConsensus.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
