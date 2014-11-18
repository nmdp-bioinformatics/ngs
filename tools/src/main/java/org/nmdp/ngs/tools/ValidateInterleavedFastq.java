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

import java.io.BufferedReader;
import java.io.File;

import java.util.concurrent.Callable;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.reads.paired.PairedEndAdapter;
import org.nmdp.ngs.reads.paired.PairedEndFastqReader;

/**
 * Validate a file in interleaved FASTQ format.
 */
public final class ValidateInterleavedFastq implements Callable<Integer> {
    private final File inputFastqFile;
    private static final String USAGE = "ngs-validate-interleaved-fastq [args]";


    /**
     * Validate a file in interleaved FASTQ format.
     *
     * @param inputFastqFile input interleaved FASTQ file, if any
     */
    public ValidateInterleavedFastq(final File inputFastqFile) {
        this.inputFastqFile = inputFastqFile;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        try {
            reader = reader(inputFastqFile);
            PairedEndFastqReader.streamInterleaved((Readable) reader, new PairedEndAdapter());

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
        FileArgument inputFastqFile = new FileArgument("i", "input-fastq-file", "input interleaved FASTQ file, default stdin", false);

        ArgumentList arguments = new ArgumentList(about, help, inputFastqFile);
        CommandLine commandLine = new CommandLine(args);

        ValidateInterleavedFastq validateInterleavedFastq = null;
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
            validateInterleavedFastq = new ValidateInterleavedFastq(inputFastqFile.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(validateInterleavedFastq.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
