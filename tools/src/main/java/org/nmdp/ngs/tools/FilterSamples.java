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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.concurrent.Callable;

import com.google.common.io.CharStreams;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.hml.HmlReader;
import org.nmdp.ngs.hml.HmlWriter;

import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;

/**
 * Filter samples from a file in HML format.
 */
public final class FilterSamples implements Callable<Integer> {
    private final File inputHmlFile;
    private final File inputSampleIdFile;
    private final File outputHmlFile;
    private static final String USAGE = "ngs-filter-samples -i hml.xml.gz -s sample-ids.txt.gz -o filtered.xml.gz";


    /**
     * Filter samples from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     * @param inputSampleIdFile input file of sample ids, must not be null
     * @param outputHmlFile output HML file, if an
     */
    public FilterSamples(final File inputHmlFile, final File inputSampleIdFile, final File outputHmlFile) {
        checkNotNull(inputSampleIdFile);
        this.inputHmlFile = inputHmlFile;
        this.inputSampleIdFile = inputSampleIdFile;
        this.outputHmlFile = outputHmlFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputHmlFile);
            writer = writer(outputHmlFile);

            Hml hml = HmlReader.read(reader);
            Set<String> sampleIds = readSampleIds(inputSampleIdFile);
            List<Sample> toRemove = new ArrayList<Sample>();
            for (Sample sample : hml.getSample()) {
                if (!sampleIds.contains(sample.getId())) {
                    toRemove.add(sample);
                }
            }
            hml.getSample().removeAll(toRemove);
            if (!hml.getSample().isEmpty()) {
                HmlWriter.write(hml, writer);
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

    static Set<String> readSampleIds(final File file) throws IOException {
        BufferedReader reader = null;
        Set<String> sampleIds = new HashSet<String>();
        try {
            reader = reader(file);
            sampleIds.addAll(CharStreams.readLines(reader));
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
        return sampleIds;
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
        FileArgument inputSampleIdFile = new FileArgument("s", "input-sample-id-file", "input sample id file, one per line", true);
        FileArgument outputHmlFile = new FileArgument("o", "output-hml-file", "output HML file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, inputHmlFile, inputSampleIdFile, outputHmlFile);
        CommandLine commandLine = new CommandLine(args);

        FilterSamples filterSamples = null;
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
            filterSamples = new FilterSamples(inputHmlFile.getValue(), inputSampleIdFile.getValue(), outputHmlFile.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(filterSamples.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
