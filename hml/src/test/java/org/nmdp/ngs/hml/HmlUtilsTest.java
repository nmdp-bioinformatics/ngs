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
import static org.nmdp.ngs.hml.HmlUtils.createDnaSequences;
import static org.nmdp.ngs.hml.HmlUtils.createRnaSequences;
import static org.nmdp.ngs.hml.HmlUtils.createRegion;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.StringReader;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.RNATools;

import org.junit.Test;

import org.nmdp.ngs.feature.Locus;

import org.nmdp.ngs.hml.jaxb.Sequence;
import org.nmdp.ngs.hml.jaxb.Region;

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
        assertEquals("DNA", dna.getAlphabet());
        assertEquals("actg", dna.getValue());
    }

    @Test
    public void testCreateSequenceBiojavaRna() throws Exception {
        Sequence rna = createSequence(RNATools.createRNASequence("acug", "foo"));
        assertEquals("RNA", rna.getAlphabet());
        assertEquals("acug", rna.getValue());
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequenceNullAlphabet() throws Exception {
        createSequence(null, "actg");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateSequenceInvalidAlphabet() throws Exception {
        createSequence("aa", "actg");
    }

    @Test(expected=NullPointerException.class)
    public void testCreateSequenceNullSequence() throws Exception {
        createSequence("dna", null);
    }

    @Test
    public void testCreateSequenceDna() throws Exception {
        Sequence dna = createSequence("dna", "actg");
        assertEquals("DNA", dna.getAlphabet());
        assertEquals("actg", dna.getValue());
    }

    @Test
    public void testCreateSequenceRna() throws Exception {
        Sequence rna = createSequence("rna", "acug");
        assertEquals("RNA", rna.getAlphabet());
        assertEquals("acug", rna.getValue());
    }

    @Test(expected=NullPointerException.class)
    public void testCreateDnaSequencesNullReader() throws Exception {
        createDnaSequences((BufferedReader) null);
    }

    @Test
    public void testCreateDnaSequences() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(">foo\n");
        sb.append("actg\n");
        sb.append(">bar\n");
        sb.append("gtca\n");
        int count = 0;
        for (Sequence sequence : createDnaSequences(new BufferedReader(new StringReader(sb.toString())))) {
            assertEquals("DNA", sequence.getAlphabet());
            assertTrue("actg".equals(sequence.getValue()) || "gtca".equals(sequence.getValue()));
            count++;
        }
        assertEquals(2, count);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateDnaSequencesNullFile() throws Exception {
        createDnaSequences((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateDnaSequencesNullURL() throws Exception {
        createDnaSequences((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateDnaSequencesNullInputStream() throws Exception {
        createDnaSequences((InputStream) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRnaSequencesNullReader() throws Exception {
        createRnaSequences((BufferedReader) null);
    }

    @Test
    public void testCreateRnaSequences() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(">foo\n");
        sb.append("acug\n");
        sb.append(">bar\n");
        sb.append("guca\n");
        int count = 0;
        for (Sequence sequence : createRnaSequences(new BufferedReader(new StringReader(sb.toString())))) {
            assertEquals("RNA", sequence.getAlphabet());
            assertTrue("acug".equals(sequence.getValue()) || "guca".equals(sequence.getValue()));
            count++;
        }
        assertEquals(2, count);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRnaSequencesNullFile() throws Exception {
        createRnaSequences((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRnaSequencesNullURL() throws Exception {
        createRnaSequences((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRnaSequencesNullInputStream() throws Exception {
        createRnaSequences((InputStream) null);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionNullAssembly() throws Exception {
        createRegion(null, "6", 2L, 42L);
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionNullContig() throws Exception {
        createRegion("GRCh38", null, 2L, 42L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateRegionInvalidStart() throws Exception {
        createRegion("GRCh38", "6", -1L, 42L);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateRegionInvalidEnd() throws Exception {
        createRegion("GRCh38", "6", 2L, -1L);
    }

    @Test
    public void testCreateRegion() throws Exception {
        Region region = createRegion("GRCh38", "6", 2L, 42L);
        assertEquals("GRCh38", region.getAssembly());
        assertEquals("6", region.getContig());
        assertEquals(2L, region.getStart());
        assertEquals(42L, region.getEnd());
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionExtNullAssembly() throws Exception {
        createRegion(null, "6", 2L, 42L, "1", "id", "description");
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionExtNullContig() throws Exception {
        createRegion("GRCh38", null, 2L, 42L, "1", "id", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateRegionExtInvalidStart() throws Exception {
        createRegion("GRCh38", "6", -1L, 42L, "1", "id", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateRegionExtInvalidEnd() throws Exception {
        createRegion("GRCh38", "6", 2L, -1L, "1", "id", "description");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCreateRegionExtInvalidStrand() throws Exception {
        createRegion("GRCh38", "6", 2L, 42L, "invalid", "id", "description");
    }

    @Test
    public void testCreateRegionExt() throws Exception {
        Region region = createRegion("GRCh38", "6", 2L, 42L, "1", "id", "description");
        assertEquals("GRCh38", region.getAssembly());
        assertEquals("6", region.getContig());
        assertEquals(2L, region.getStart());
        assertEquals(42L, region.getEnd());
        assertEquals("1", region.getStrand());
        assertEquals("id", region.getId());
        assertEquals("description", region.getDescription());
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionLocusNullAssembly() throws Exception {
        // note Locus uses 1-based, fully closed ranges
        createRegion(null, new Locus("6", 3, 42));
    }

    @Test(expected=NullPointerException.class)
    public void testCreateRegionLocusNullLocus() throws Exception {
        createRegion("GRCh38", (Locus) null);
    }

    @Test
    public void testCreateRegionLocus() throws Exception {
        // note Locus uses 1-based, fully closed ranges
        Region region = createRegion("GRCh38", new Locus("6", 3, 42));
        assertEquals("GRCh38", region.getAssembly());
        assertEquals("6", region.getContig());
        assertEquals(2L, region.getStart());
        assertEquals(42L, region.getEnd());
    }
}
