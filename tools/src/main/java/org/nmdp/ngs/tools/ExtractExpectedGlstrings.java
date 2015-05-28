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
import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.hml.HmlReader;

import org.nmdp.ngs.hml.jaxb.Glstring;
import org.nmdp.ngs.hml.jaxb.AlleleAssignment;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Extract expected allele assignments in GL String format from a file in HML format.
 */
public final class ExtractExpectedGlstrings implements Callable<Integer> {
    private final File inputHmlFile;
    private final File outputFile;
    private final OkHttpClient client = new OkHttpClient();
    private static final String USAGE = "ngs-extract-expected-glstrings [args]";


    /**
     * Extract expected allele assignments in GL String format from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     * @param outputFile output interpretation file, if any
     */
    public ExtractExpectedGlstrings(final File inputHmlFile, final File outputFile) {
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
                        for (Object child : alleleAssignment.getPropertyAndHaploidAndGenotypeList()) {
                            if (child instanceof Glstring) {
                                Glstring glstring = (Glstring) child;

                                StringBuilder sb = new StringBuilder();
                                sb.append(sampleId);
                                // locus is not available for glstring elements
                                sb.append("\t\t");
                                sb.append(geneFamily);
                                sb.append("\t");
                                sb.append(alleleDb == null ? "" : alleleDb);
                                sb.append("\t");
                                sb.append(alleleVersion == null ? "" : alleleVersion);
                                sb.append("\t");

                                // prefer uri if both are specified
                                if (glstring.getUri() != null) {
                                    sb.append(getGlstring(glstring.getUri()));
                                }
                                else if (glstring.getValue() != null) {
                                    sb.append(glstring.getValue().replaceAll("\\s+", ""));
                                }
                                writer.println(sb.toString());
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
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    String getGlstring(final String uri) throws IOException {
        Request request = new Request.Builder().url(uri).addHeader("Accept", "text/plain").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
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

        ExtractExpectedGlstrings extractExpectedGlstrings = null;
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
            extractExpectedGlstrings = new ExtractExpectedGlstrings(inputHmlFile.getValue(), outputFile.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractExpectedGlstrings.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
