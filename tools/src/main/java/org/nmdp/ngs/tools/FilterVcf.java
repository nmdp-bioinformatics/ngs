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

import java.util.ArrayList;
import java.util.List;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringListArgument;

import org.nmdp.ngs.variant.vcf.VcfHeader;
import org.nmdp.ngs.variant.vcf.VcfReader;
import org.nmdp.ngs.variant.vcf.VcfRecord;
import org.nmdp.ngs.variant.vcf.VcfSample;
import org.nmdp.ngs.variant.vcf.VcfWriter;
import org.nmdp.ngs.variant.vcf.VcfStreamAdapter;

/**
 * Filter variants in VCF format.
 */
public final class FilterVcf implements Runnable {
    private final Filter filter;
    private final File inputVcfFile;
    private final File outputVcfFile;
    private static final String USAGE = "ngs-filter-vcf -s rs149201999 -i input.vcf.gz -o output.vcf.gz";


    /**
     * Filter variants in VCF format.
     *
     * @param filter filter, must not be null
     * @param inputVcfFile input VCF file, if any
     * @param outputVcfFile output VCF file, if any
     */
    public FilterVcf(final Filter filter, final File inputVcfFile, final File outputVcfFile) {
        checkNotNull(filter);
        this.filter = filter;
        this.inputVcfFile = inputVcfFile;
        this.outputVcfFile = outputVcfFile;
    }


    @Override
    public void run() {
        PrintWriter writer = null;
        try {
            writer = writer(outputVcfFile);

            final PrintWriter w = writer;
            VcfReader.stream(reader(inputVcfFile), new VcfStreamAdapter() {
                    private boolean wroteSamples = false;
                    private List<VcfSample> samples = new ArrayList<VcfSample>();

                    @Override
                    public void header(final VcfHeader header) {
                        VcfWriter.writeHeader(header, w);
                    }

                    @Override
                    public void sample(final VcfSample sample) {
                        samples.add(sample);
                    }

                    @Override
                    public void record(final VcfRecord record) {
                        // write out samples
                        if (!wroteSamples) {
                            VcfWriter.writeColumnHeader(samples, w);
                            wroteSamples = true;
                        }

                        // write out record
                        if (filter.accept(record)) {
                            VcfWriter.writeRecord(samples, record, w);
                        }
                    }
                });
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // empty
            }
        }
    }

    interface Filter {
        boolean accept(VcfRecord record);
    }

    static final class IdFilter implements Filter {
        private final List<String> ids;

        IdFilter(final List<String> ids) {
            checkNotNull(ids);
            this.ids = ids;
        }

        @Override
        public boolean accept(final VcfRecord record) {
            for (String id : record.getId()) {
                if (ids.contains(id)) {
                    return true;
                }
            }
            return false;
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
        StringListArgument snpIdFilter = new StringListArgument("s", "snp-ids", "filter by snp id", true);
        FileArgument inputVcfFile = new FileArgument("i", "input-vcf-file", "input VCF file, default stdin", false);
        FileArgument outputVcfFile = new FileArgument("o", "output-vcf-file", "output VCF file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, snpIdFilter, inputVcfFile, outputVcfFile);
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
            new FilterVcf(new IdFilter(snpIdFilter.getValue()), inputVcfFile.getValue(), outputVcfFile.getValue()).run();
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
