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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.hml.HmlUtils.createSequence;
import static org.nmdp.ngs.hml.HmlUtils.createSequences;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;

import org.junit.Test;

import org.nmdp.ngs.hml.jaxb.Sequence;

/**
 * Unit test for HmlUtils.
 */
public final class HmlUtilsTest {

    @Test(expected=NullPointerException.class)
    public void testCreateSequenceNullBiojavaSequence() throws Exception {
        createSequence((org.biojava.bio.seq.Sequence) null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateSequenceInvalidBiojavaAlphabet() throws Exception {
        createSequence(ProteinTools.createProteinSequence("adef", "foo"));
    }

    @Test
    public void testCreateSequenceBiojavaDna() throws Exception {
        Sequence dna = createSequence(DNATools.createDNASequence("actg", "foo"));
        assertEquals("actg", dna.getValue());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateSequenceBiojavaRna() throws Exception {
        createSequence(RNATools.createRNASequence("acug", "foo"));
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequencesNullReader() throws Exception {
        createSequences((BufferedReader) null);
    }

    @Test
    public void testCreateSequences() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(">foo\n");
        sb.append("actg\n");
        sb.append(">bar\n");
        sb.append("gtca\n");
        int count = 0;
        for (Sequence sequence : createSequences(new BufferedReader(new StringReader(sb.toString())))) {
            assertTrue("actg".equals(sequence.getValue()) || "gtca".equals(sequence.getValue()));
            count++;
        }
        assertEquals(2, count);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequencesNullFile() throws Exception {
        createSequences((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequencesNullURL() throws Exception {
        createSequences((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequencesNullInputStream() throws Exception {
        createSequences((InputStream) null);
    }
}
