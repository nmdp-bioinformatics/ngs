/*

    ngs-feature  Features.
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
package org.nmdp.ngs.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.nmdp.ngs.feature.Allele.builder;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;

import org.biojava.bio.seq.impl.SimpleSequence;

import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SymbolList;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Allele.
 */
public final class AlleleTest {
    Allele allele1 = null,
           allele2 = null,
           allele3 = null,
           allele4 = null,
           allele5 = null,
           temp = null;

    @Before
    public void setUp() {
        try {
            allele1 = builder()
                        .withContig("chr6")
                        .withStart(5)
                        .withEnd(20)
                        .build();

            allele2 = builder()
                        .withContig("chr6")
                        .withStart(10)
                        .withEnd(15)
                        .withSequence(DNATools.createDNASequence("AAAAA", ""))
                        .build();

            allele3 = builder()
                        .withContig("chr6")
                        .withStart(5)
                        .withEnd(15)
                        .build();

            allele4 = builder()
                        .withContig("chr6")
                        .withStart(10)
                        .withEnd(20)
                        .withSequence(DNATools.createDNASequence("AAAAATTTTT", ""))
                        .build();

            allele5 = builder()
                        .withContig("chr6")
                        .withStart(5)
                        .withEnd(15)
                        .withSequence(DNATools.createDNASequence("CCCCCAAAAA", ""))
                        .build();
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testConstructor() {
        Allele allele = null;
        allele = new Allele("", "chr6", 29942488, 29943994, SymbolList.EMPTY_LIST, Allele.Lesion.UNKNOWN);
    }

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }
    
    @Test
    public void testBuilderSubstitution() throws IllegalSymbolException, AlleleException {
        temp = builder()
            .withContig("chr6")
            .withStart(1)
            .withEnd(2)
            .withSequence(DNATools.createDNA("A"))
            .withLesion(Allele.Lesion.SUBSTITUTION)
            .build();

        assertEquals(temp.getContig(), "chr6");
        assertEquals(temp.getStart(), 1);
        assertEquals(temp.getEnd(), 2);
        assertEquals(temp.sequence.seqString(), "a");
        assertEquals(temp.lesion, Allele.Lesion.SUBSTITUTION);

        temp = builder()
            .withContig("chr6")
            .withStart(1)
            .withEnd(2)
            .withLesion(Allele.Lesion.SUBSTITUTION)
            .build();

        assertEquals(temp.sequence.seqString(), "-");
    }

    @Test
    public void testDoubleCrossover() throws AlleleException {
        try {
            temp = allele1.doubleCrossover(allele2);
            assertEquals(temp.getStart(), 5);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "-----aaaaa-----");

            temp = allele2.doubleCrossover(allele1);
            assertEquals(temp.getStart(), 10);
            assertEquals(temp.getEnd(), 15);
            assertEquals(temp.sequence.seqString(), "-----");

            temp = allele3.doubleCrossover(allele4);
            assertEquals(temp.getStart(), 5);
            assertEquals(temp.getEnd(), 15);
            assertEquals(temp.sequence.seqString(), "-----aaaaa");

            temp = allele4.doubleCrossover(allele3);
            assertEquals(temp.getStart(), 10);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "-----ttttt");

            temp = allele4.doubleCrossover(allele4);
            assertEquals(temp.getStart(), 10);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "aaaaattttt");
        }
        catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testMerge() {
        try {
            temp = allele5.merge(allele4, 0);
            assertEquals(temp.getStart(), 5);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "cccccaaaaattttt");

            temp = allele4.merge(allele5, 0);
            assertEquals(temp.getStart(), 5);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "cccccaaaaattttt");        

            temp = allele2.merge(allele4, 0);
            assertEquals(temp.getStart(), 10);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "aaaaattttt");

            temp = allele2.merge(allele4, 5);
            assertEquals(temp.getStart(), 10);
            assertEquals(temp.getEnd(), 20);
            assertEquals(temp.sequence.seqString(), "aaaaattttt");

            temp = allele2.merge(allele4, 6);
            assertTrue(temp.isEmpty());
        }
        catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testLeftHardClip() throws IllegalSymbolException, IndexOutOfBoundsException, IllegalAlphabetException, AlleleException {
        temp = allele4.leftHardClip("a");
        assertEquals(temp.getStart(), 15);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "ttttt");

        temp = allele4.leftHardClip("aa");
        assertEquals(temp.getStart(), 14);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "attttt");

        temp = allele4.leftHardClip("aaaaa");
        assertEquals(temp.getStart(), 15);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "ttttt");

        temp = allele4.leftHardClip("aaaaat");
        assertEquals(temp.getStart(), 16);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "tttt");

        temp = allele4.leftHardClip("ttttt");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "aaaaattttt");
    }
    
    @Test
    public void testRightHardClip() throws IllegalAlphabetException, AlleleException, IllegalSymbolException {
        temp = allele4.rightHardClip("t");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 15);
        assertEquals(temp.sequence.seqString(), "aaaaa");

        temp = allele4.rightHardClip("tt");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 16);
        assertEquals(temp.sequence.seqString(), "aaaaat");

        temp = allele4.rightHardClip("ttttt");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 15);
        assertEquals(temp.sequence.seqString(), "aaaaa");

        temp = allele4.rightHardClip("attttt");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 14);
        assertEquals(temp.sequence.seqString(), "aaaa");

        temp = allele4.rightHardClip("aaaaa");
        assertEquals(temp.getStart(), 10);
        assertEquals(temp.getEnd(), 20);
        assertEquals(temp.sequence.seqString(), "aaaaattttt");
    }
}
