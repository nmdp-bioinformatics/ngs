/*

    ngs-hml  Mapping for HML XSDs.
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
package org.nmdp.ngs.hml;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import org.biojava.bio.seq.io.SeqIOTools;

import org.nmdp.ngs.feature.Locus;

import org.nmdp.ngs.hml.jaxb.Sequence;
import org.nmdp.ngs.hml.jaxb.TargetedRegion;

/**
 * Static utility methods for creating HML model classes.
 */
public final class HmlUtils {

    /**
     * Create and return a new HML Sequence from the specified sequence.
     *
     * @param sequence sequence, must not be null, and alphabet must be one of { DNA, RNA }
     * @return a new HML Sequence created from the specified sequence.
     */
    public static Sequence createSequence(final org.biojava.bio.seq.Sequence sequence) {
        checkNotNull(sequence);

        Sequence s = new Sequence();
        if (DNATools.getDNA().equals(sequence.getAlphabet())) {
            s.setAlphabet("DNA");
        }
        else if (RNATools.getRNA().equals(sequence.getAlphabet())) {
            s.setAlphabet("RNA");
        }
        else {
            throw new IllegalArgumentException("alphabet must be one of { DNA, RNA }");
        }
        s.setValue(sequence.seqString());
        return s;
    }

    /**
     * Create and return a new HML Sequence with the specified alphabet and sequence.
     *
     * @param alphabet alphabet, must be one of { DNA, RNA }
     * @param sequence sequence, must not be null
     * @return a new HML Sequence with the specified alphabet and sequence
     * @throws IllegalSymbolException if <code>sequence</code> contains an illegal symbol
     */
    public static Sequence createSequence(final String alphabet, final String sequence) throws IllegalSymbolException {
        checkNotNull(alphabet);
        checkNotNull(sequence);

        Sequence s = new Sequence();
        s.setAlphabet(alphabet);

        SymbolList symbolList = null;
        if ("dna".equalsIgnoreCase(alphabet)) {
            symbolList = DNATools.createDNA(sequence);
        }
        else if ("rna".equalsIgnoreCase(alphabet)) {
            symbolList = RNATools.createRNA(sequence);
        }
        else {
            throw new IllegalArgumentException("alphabet must be one of { DNA, RNA }");
        }
        s.setValue(symbolList.seqString());
        return s;
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified reader in FASTA format.
     *
     * @param reader reader to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified reader in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createDnaSequences(final BufferedReader reader) throws IOException {
        checkNotNull(reader);
        List<Sequence> sequences = new ArrayList<Sequence>();
        for (SequenceIterator it = SeqIOTools.readFastaDNA(reader); it.hasNext(); ) {
            try {
                sequences.add(createSequence(it.nextSequence()));
            }
            catch (BioException e) {
                throw new IOException("could not read DNA sequences", e);
            }
        }
        return sequences;
    }


    /**
     * Create and return zero or more DNA HML Sequences read from the specified file in FASTA format.
     *
     * @param file file to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified file in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createDnaSequences(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return createDnaSequences(reader);
        }
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified URL in FASTA format.
     *
     * @param url URL to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified URL in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createDnaSequences(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return createDnaSequences(reader);
        }
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified input stream in FASTA format.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified input stream in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createDnaSequences(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return createDnaSequences(reader);
        }
    }


    /**
     * Create and return zero or more RNA HML Sequences read from the specified reader in FASTA format.
     *
     * @param reader reader to read from, must not be null
     * @return zero or more RNA HML Sequences read from the specified reader in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createRnaSequences(final BufferedReader reader) throws IOException {
        checkNotNull(reader);
        List<Sequence> sequences = new ArrayList<Sequence>();
        for (SequenceIterator it = SeqIOTools.readFastaRNA(reader); it.hasNext(); ) {
            try {
                sequences.add(createSequence(it.nextSequence()));
            }
            catch (BioException e) {
                throw new IOException("could not read RNA sequences", e);
            }
        }
        return sequences;
    }

    /**
     * Create and return zero or more RNA HML Sequences read from the specified file in FASTA format.
     *
     * @param file file to read from, must not be null
     * @return zero or more RNA HML Sequences read from the specified file in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createRnaSequences(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return createRnaSequences(reader);
        }
    }

    /**
     * Create and return zero or more RNA HML Sequences read from the specified URL in FASTA format.
     *
     * @param url URL to read from, must not be null
     * @return zero or more RNA HML Sequences read from the specified URL in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createRnaSequences(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return createRnaSequences(reader);
        }
    }

    /**
     * Create and return zero or more RNA HML Sequences read from the specified input stream in FASTA format.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more RNA HML Sequences read from the specified input stream in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createRnaSequences(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return createRnaSequences(reader);
        }
    }


    /**
     * Create and return a new targeted region.
     *
     * @param assembly assembly, must not be null, should be a major or minor
     *    version of the GRC human assembly, e.g. GRCh38
     * @param contig contig, must not be null, should be a contig present in
     *    the specified assembly
     * @param start start, 0-based coordinate system, closed-open range, must
     *    be at least <code>0L</code>
     * @param end end, 0-based coordinate system, closed-open range, must
     *    be at least <code>0L</code>
     * @return a new targeted region
     */
    public static TargetedRegion createTargetedRegion(final String assembly,
                                                      final String contig,
                                                      final long start,
                                                      final long end) {
        checkNotNull(assembly);
        checkNotNull(contig);
        checkArgument(start >= 0L, "start must be at least 0L");
        checkArgument(end >= 0L, "end must be at least 0L");

        TargetedRegion targetedRegion = new TargetedRegion();
        targetedRegion.setAssembly(assembly);
        targetedRegion.setContig(contig);
        targetedRegion.setStart(start);
        targetedRegion.setEnd(end);
        return targetedRegion;
    }

    /**
     * Create and return a new targeted region.
     *
     * @param assembly assembly, must not be null, should be a major or minor
     *    version of the GRC human assembly, e.g. GRCh38
     * @param contig contig, must not be null, should be a contig present in
     *    the specified assembly
     * @param start start, 0-based coordinate system, closed-open range, must
     *    be at least <code>0L</code>
     * @param end end, 0-based coordinate system, closed-open range, must
     *    be at least <code>0L</code>
     * @param strand strand, if provided must be one of { 1, -1, +, - }
     * @param id id
     * @param description description
     * @return a new targeted region
     */
    public static TargetedRegion createTargetedRegion(final String assembly,
                                                      final String contig,
                                                      final long start,
                                                      final long end,
                                                      final String strand,
                                                      final String id,
                                                      final String description) {
        checkNotNull(assembly);
        checkNotNull(contig);
        checkArgument(start >= 0L, "start must be at least 0L");
        checkArgument(end >= 0L, "end must be at least 0L");

        if (strand != null) {
            checkArgument("1".equals(strand)
                          || "-1".equals(strand)
                          || "+".equals(strand)
                          || "-".equals(strand), "if provided, strand must be one of { 1, -1, +, - }");
        }

        TargetedRegion targetedRegion = new TargetedRegion();
        targetedRegion.setAssembly(assembly);
        targetedRegion.setContig(contig);
        targetedRegion.setStart(start);
        targetedRegion.setEnd(end);
        targetedRegion.setStrand(strand);
        targetedRegion.setId(id);
        targetedRegion.setDescription(description);
        return targetedRegion;
    }

    /**
     * Create and return a new targeted region with the specified assembly and locus.
     *
     * @param assembly assembly, must not be null, should be a major or minor
     *    version of the GRC human assembly, e.g. GRCh38
     * @param locus locus, must not be null
     * @return a new targeted region with the specified assembly and locus
     */
    public static TargetedRegion createTargetedRegion(final String assembly, final Locus locus) {
        checkNotNull(assembly);
        checkNotNull(locus);
        return createTargetedRegion(assembly, locus.getContig(), locus.getStart() - 1L, locus.getEnd());
    }
}
