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

import java.util.HashMap;
import java.util.Map;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

/**
 * Liftover BED file.
 */
public final class LiftoverBed implements Runnable {
    private final File refBedFile;
    private final File sourceBedFile;
    private final File targetBedFile;
    private static final String USAGE = "ngs-liftover-bed -r ref.bed.gz [args]";


    /**
     * Liftover BED file.
     *
     * @param refBedFile reference BED file, must not be null
     * @param sourceBedFile source BED file, if any
     * @param targetBedFile target BED file, if any
     */
    public LiftoverBed(final File refBedFile, final File sourceBedFile, final File targetBedFile) {
        checkNotNull(refBedFile);
        this.refBedFile = refBedFile;
        this.sourceBedFile = sourceBedFile;
        this.targetBedFile = targetBedFile;
    }


    @Override
    public void run() {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(sourceBedFile);
            writer = writer(targetBedFile);

            Map<String, BedRecord> ref = readRefFile();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                BedRecord source = BedRecord.parse(line);
                BedRecord target = ref.get(source.chrom());

                if (target != null) {
                    writer.print(target.chrom());
                    writer.print("\t");
                    writer.print(target.start() + source.start());
                    writer.print("\t");
                    writer.print(target.start() + source.start() + (source.end() - source.start()));
                    writer.print("\t");
                    writer.print(source.name());
                    if (source.score() != null) {
                        writer.print("\t");
                        writer.print(source.score());
                    }
                    writer.print("\n");
                }
                // else warn
            }
        }
        catch (IOException e) {
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

    private Map<String, BedRecord> readRefFile() throws IOException {
        BufferedReader reader = null;
        Map<String, BedRecord> ref = new HashMap<String, BedRecord>(10000);
        try {
            reader = reader(refBedFile);

            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }

                BedRecord rec = BedRecord.parse(line);
                BedRecord prev = ref.put(rec.name(), rec);
                if (prev != null) {
                    // warn non-unique mapping
                }
            }
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // empty
            }
        }
        return ref;
    }

    private static final class BedRecord {
        private final String chrom;
        private final int start;
        private final int end;
        private final String name;
        private final String score;

        private BedRecord(final String chrom, final int start, final int end, final String name, final String score) {
            this.chrom = chrom;
            this.start = start;
            this.end = end;
            this.name = name;
            this.score = score;
        }

        String chrom() {
            return chrom;
        }

        int start() {
            return start;
        }

        int end() {
            return end;
        }

        String name() {
            return name;
        }

        String score() {
            return score;
        }

        static BedRecord parse(final String line) {
            String[] tokens = line.split("\\s+");
            String chrom = tokens[0];
            int start = Integer.parseInt(tokens[1]);
            int end = Integer.parseInt(tokens[2]);
            String name = tokens[3];
            String score = (tokens.length > 4) ? tokens[4] : null;
            return new BedRecord(chrom, start, end, name, score);
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
        FileArgument refBedFile = new FileArgument("r", "ref-bed-file", "reference BED file", true);
        FileArgument sourceBedFile = new FileArgument("i", "source-bed-file", "input BED file, default stdin", false);
        FileArgument targetBedFile = new FileArgument("o", "target-bed-file", "output BED file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, refBedFile, sourceBedFile, targetBedFile);
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
            new LiftoverBed(refBedFile.getValue(), sourceBedFile.getValue(), targetBedFile.getValue()).run();
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
    }
}
