/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;

import com.google.common.base.Splitter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * Utility to run genewise via the command line.
 */
public final class Genewise {
    /** Split on spaces. */
    private static final Splitter SPLITTER = Splitter.on(' ').trimResults().omitEmptyStrings();


    /**
     * Private no-arg constructor.
     */
    private Genewise() {
        // empty
    }


    /**
     * Return the exons predicted from the alignment of the specified amino acid HMM file in HMMER2 format against
     * the specified genomic DNA sequence file in FASTA format.
     *
     * @param aminoAcidHmm2File amino acid HMM file in HMMER2 format, must not be null
     * @param genomicDnaFastaFile genomic DNA sequence file in FASTA format, must not be null
     * @return zero or more exons predicted from the alignment of the specified amino acid HMM file in HMMER2 format against
     *    the specified genomic DNA sequence file in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<GenewiseExon> genewiseExons(final File aminoAcidHmm2File, final File genomicDnaFastaFile) throws IOException {
        checkNotNull(aminoAcidHmm2File);
        checkNotNull(genomicDnaFastaFile);

        File genewiseResult = File.createTempFile("genewise", ".txt");

        ProcessBuilder genewise = new ProcessBuilder("genewise",
                                                     "-hmmer", "-tfor", "-genes", "-nosplice_gtag",
                                                     aminoAcidHmm2File.getPath(), genomicDnaFastaFile.getPath());

        genewise.redirectErrorStream(true);
        genewise.redirectOutput(ProcessBuilder.Redirect.to(genewiseResult));

        Process genewiseProcess = genewise.start();
        try {
            genewiseProcess.waitFor();
        }
        catch (InterruptedException e) {
            // ignore
        }

        int lineNumber = 0;
        BufferedReader reader = null;
        List<GenewiseExon> exons = Lists.newLinkedList();
        try {
            reader = new BufferedReader(new FileReader(genewiseResult));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("  Exon")) {
                    List<String> tokens = SPLITTER.splitToList(line);
                    if (tokens.size() < 5) {
                        throw new IOException("invalid genewise genes format at line number " + lineNumber + ", line " + line);
                    }
                    try {
                        long start = Long.parseLong(tokens.get(1));
                        long end = Long.parseLong(tokens.get(2));
                        if (start > end) {
                            throw new IOException("invalid genewise exon at line number " + lineNumber + ", start > end");
                        }
                        int phase = Integer.parseInt(tokens.get(4));
                        exons.add(new GenewiseExon(start, end, phase));
                    }
                    catch (NumberFormatException e) {
                        throw new IOException("invalid genewise exon at line number " + lineNumber + ", caught " + e.getMessage());
                    }
                }
                lineNumber++;
            }
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // empty
            }
            try {
                genewiseResult.delete();
            }
            catch (Exception e) {
                // empty
            }
        }
        return ImmutableList.copyOf(exons);
    }
}
