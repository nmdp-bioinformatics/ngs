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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.List;

import java.util.concurrent.Callable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.hml.HmlReader;

import org.nmdp.ngs.hml.jaxb.Haploid;
import org.nmdp.ngs.hml.jaxb.AlleleAssignment;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Extract expected allele assignments in haploid elements from a file in HML format.
 */
public final class ExtractExpectedHaploids implements Callable<Integer> {
    private final File inputHmlFile;
    private final File outputFile;
    private static final String USAGE = "ngs-extract-expected-haploids [args]";


    /**
     * Extract expected allele assignments in haploid elements from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     * @param outputFile output interpretation file, if any
     */
    public ExtractExpectedHaploids(final File inputHmlFile, final File outputFile) {
        this.inputHmlFile = inputHmlFile;
        this.outputFile = outputFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputHmlFile);
            writer = writer(outputFile);

            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String sampleId = sample.getId();
                for (Typing typing : sample.getTyping()) {
                    String geneFamily = typing.getGeneFamily();
                    for (AlleleAssignment alleleAssignment : typing.getAlleleAssignment()) {
                        String alleleDb = alleleAssignment.getAlleleDb();
                        String alleleVersion = alleleAssignment.getAlleleVersion();

                        ListMultimap<String, Haploid> haploidsByLocus = ArrayListMultimap.create();
                        for (Object child : alleleAssignment.getPropertyAndHaploidAndGenotypeList()) {
                            if (child instanceof Haploid) {
                                Haploid haploid = (Haploid) child;
                                haploidsByLocus.put(haploid.getLocus(), haploid);
                            }
                        }
                        for (String locus : haploidsByLocus.keySet()) {
                            List<Haploid> haploids = haploidsByLocus.get(locus);

                            StringBuilder sb = new StringBuilder();
                            sb.append(sampleId);
                            sb.append("\t");
                            sb.append(locus);
                            sb.append("\t");
                            sb.append(geneFamily);
                            sb.append("\t");
                            sb.append(alleleDb == null ? "" : alleleDb);
                            sb.append("\t");
                            sb.append(alleleVersion == null ? "" : alleleVersion);
                            sb.append("\t");

                            // note only the first two haploids per locus are considered
                            sb.append(toGenotype(haploids.get(0), haploids.size() > 1 ? haploids.get(1) : null));
                            writer.println(sb.toString());
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
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    static String toGenotype(final Haploid haploid0, final Haploid haploid1) {
        StringBuilder sb = new StringBuilder();
        sb.append(haploid0.getLocus());
        sb.append("*");
        sb.append(haploid0.getType());
        if (haploid1 != null) {
            sb.append("+");
            sb.append(haploid1.getLocus());
            sb.append("*");
            sb.append(haploid1.getType());
        }
        return sb.toString();
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
        FileArgument outputFile = new FileArgument("o", "output-file", "output allele assignment file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputHmlFile, outputFile);
        CommandLine commandLine = new CommandLine(args);

        ExtractExpectedHaploids extractExpectedHaploids = null;
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
            extractExpectedHaploids = new ExtractExpectedHaploids(inputHmlFile.getValue(), outputFile.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractExpectedHaploids.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
