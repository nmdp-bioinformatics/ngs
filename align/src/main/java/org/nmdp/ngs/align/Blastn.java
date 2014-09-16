/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Sources.charSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.nio.charset.Charset;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import com.google.common.io.Files;

/**
 * Utility to run blastn via the command line.
 */
public final class Blastn {

    /**
     * Private no-arg constructor.
     */
    private Blastn() {
        // empty
    }

    /**
     * Return the high-scoring segment pairs (HSPs) from blastn of the source and target sequence files in FASTA format.
     *
     * @param sourceFile source sequence file in FASTA format, must not be null
     * @param targetFile target sequence file in FASTA format, must not be null
     * @return zero or more high-scoring segment pairs (HSPs) from blastn of the source and target sequence files
     *    in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<HighScoringPair> blastn(final File sourceFile, final File targetFile) throws IOException {
        checkNotNull(sourceFile);
        checkNotNull(targetFile);

        File blastResult = File.createTempFile("blastn", ".txt");

        // blastn can't handle compressed files, so copy source and target to temp files, decompressing if necessary
        File sourceFileCopy = File.createTempFile("sourceFile", ".fa");
        charSource(sourceFile).copyTo(Files.asCharSink(sourceFileCopy, Charset.forName("UTF-8")));

        File targetFileCopy = File.createTempFile("targetFile", ".fa");
        charSource(targetFile).copyTo(Files.asCharSink(targetFileCopy, Charset.forName("UTF-8")));

        ProcessBuilder blastn = new ProcessBuilder("blastn",
                                                   "-subject", sourceFileCopy.getPath(),
                                                   "-query", targetFileCopy.getPath(),
                                                   "-outfmt", "6",
                                                   "-out", blastResult.getPath());
        Process blastnProcess = blastn.start();
        try {
            blastnProcess.waitFor();
        }
        catch (InterruptedException e) {
            // ignore
        }

        BufferedReader reader = null;
        List<HighScoringPair> hsps = Lists.newLinkedList();
        try {
            reader = new BufferedReader(new FileReader(blastResult));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] tokens = line.split("\t");
                if (tokens.length == 12) {
                    String source = tokens[0].trim();
                    String target = tokens[1].trim();
                    double percentIdentity = Double.parseDouble(tokens[2].trim());
                    long alignmentLength = Long.parseLong(tokens[3].trim());
                    int mismatches = Integer.parseInt(tokens[4].trim());
                    int gapOpens = Integer.parseInt(tokens[5].trim());
                    long sourceStart = Long.parseLong(tokens[6].trim());
                    long sourceEnd = Long.parseLong(tokens[7].trim());
                    long targetStart = Long.parseLong(tokens[8].trim());
                    long targetEnd = Long.parseLong(tokens[9].trim());
                    double evalue = Double.parseDouble(tokens[10].trim());
                    double bitScore = Double.parseDouble(tokens[11].trim());

                    hsps.add(new HighScoringPair(source, target, percentIdentity, alignmentLength, mismatches, gapOpens, sourceStart, sourceEnd, targetStart, targetEnd, evalue, bitScore));
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
            sourceFileCopy.delete();
            targetFileCopy.delete();
        }
        return ImmutableList.copyOf(hsps);
    }
}
