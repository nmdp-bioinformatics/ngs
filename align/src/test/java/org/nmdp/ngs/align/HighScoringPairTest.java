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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.nmdp.ngs.align.HighScoringPair.valueOf;

import org.junit.Test;

/**
 * Unit test for HighScoringPair.
 */
public final class HighScoringPairTest {

    @Test
    public void testConstructor() {
        HighScoringPair highScoringPair = new HighScoringPair("source", "target", 99.0d, 100L, 1, 2, 1L, 100L, 2L, 101L, 0.1d, 1.0d);
        assertEquals("source", highScoringPair.source());
        assertEquals("target", highScoringPair.target());
        assertEquals(99.0d, highScoringPair.percentIdentity(), 0.1d);
        assertEquals(100L, highScoringPair.alignmentLength());
        assertEquals(1, highScoringPair.mismatches());
        assertEquals(2, highScoringPair.gapOpens());
        assertEquals(1L, highScoringPair.sourceStart());
        assertEquals(100L, highScoringPair.sourceEnd());
        assertEquals(2L, highScoringPair.targetStart());
        assertEquals(101L, highScoringPair.targetEnd());
        assertEquals(0.1d, highScoringPair.evalue(), 0.1d);
        assertEquals(1.0d, highScoringPair.bitScore(), 0.1d);
        assertNotNull(highScoringPair.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testValueOfNull() {
        valueOf(null);
    }        

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalid() {
        valueOf("not\tenough\tcolumns");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfIllegalNumber() {
        valueOf("source\ttarget\t99.0\t100\t1\t2\t1\t100\t2\t101\t0.1\tnot-a-number");
    }

    @Test
    public void testValueOf() {
        HighScoringPair highScoringPair = valueOf("source\ttarget\t99.0\t100\t1\t2\t1\t100\t2\t101\t0.1\t1.0");
        assertEquals("source", highScoringPair.source());
        assertEquals("target", highScoringPair.target());
        assertEquals(99.0d, highScoringPair.percentIdentity(), 0.1d);
        assertEquals(100L, highScoringPair.alignmentLength());
        assertEquals(1, highScoringPair.mismatches());
        assertEquals(2, highScoringPair.gapOpens());
        assertEquals(1L, highScoringPair.sourceStart());
        assertEquals(100L, highScoringPair.sourceEnd());
        assertEquals(2L, highScoringPair.targetStart());
        assertEquals(101L, highScoringPair.targetEnd());
        assertEquals(0.1d, highScoringPair.evalue(), 0.1d);
        assertEquals(1.0d, highScoringPair.bitScore(), 0.1d);
        assertNotNull(highScoringPair.toString());
    }
}
