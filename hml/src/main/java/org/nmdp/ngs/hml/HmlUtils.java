/*

    ngs-hml  Mapping for HML XSDs.
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
package org.nmdp.ngs.hml;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Hmlid;
import org.nmdp.ngs.hml.jaxb.Sequence;

/**
 * Static utility methods for creating HML model classes.
 */
public final class HmlUtils {

    /**
     * Create and return a new HML Sequence from the specified sequence.
     *
     * @param sequence sequence, must not be null, and alphabet must be DNA
     * @return a new HML Sequence created from the specified sequence.
     */
    public static Sequence createSequence(final org.biojava.bio.seq.Sequence sequence) {
        checkNotNull(sequence);

        Sequence s = new Sequence();
        if (!DNATools.getDNA().equals(sequence.getAlphabet())) {
            throw new IllegalArgumentException("alphabet must be DNA");
        }
        s.setValue(sequence.seqString());
        return s;
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified reader in FASTA format.
     *
     * @param reader reader to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified reader in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createSequences(final BufferedReader reader) throws IOException {
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
    public static Iterable<Sequence> createSequences(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return createSequences(reader);
        }
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified URL in FASTA format.
     *
     * @param url URL to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified URL in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createSequences(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return createSequences(reader);
        }
    }

    /**
     * Create and return zero or more DNA HML Sequences read from the specified input stream in FASTA format.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more DNA HML Sequences read from the specified input stream in FASTA format
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<Sequence> createSequences(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return createSequences(reader);
        }
    }

    /**
     * Return the Hmlid element from the specified HML document, if any.
     *
     * @param hml HML document, must not be null
     */
    public static Hmlid getHmlid(final Hml hml) {
        checkNotNull(hml);
        return hml.getHmlid();
    }

    /**
     * Convert the specified HML Sequence element into a DNA symbol list.
     *
     * @param sequence HML Sequence element, must not be null
     * @return the specified HML Sequence element converted into a DNA symbol list
     * @throws IllegalSymbolException if an illegal symbol is found
     */
    public static SymbolList toDnaSymbolList(final Sequence sequence) throws IllegalSymbolException {
        checkNotNull(sequence);
        return DNATools.createDNA(sequence.getValue().replaceAll("\\s+", ""));
    }
}
