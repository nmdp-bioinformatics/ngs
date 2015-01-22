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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.align.BedRecord.valueOf;

import com.google.common.collect.Range;

import org.junit.Test;

import org.nmdp.ngs.align.BedRecord.Format;

/**
 * Unit test for BedRecord.
 */
public final class BedRecordTest {

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooShort() {
        valueOf("chr1\t11873");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooLong() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,\thi mom");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfStartNumberFormatException() {
        valueOf("chr1\tnot a number\t14409");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfEndNumberFormatException() {
        valueOf("chr1\t11873\tnot a number");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfStartLessThanZero() {
        valueOf("chr1\t-1\t14409");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfEndLessThanZero() {
        valueOf("chr1\t11873\t-1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfEndLessThanStart() {
        valueOf("chr1\t11873\t11870");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickStartLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t-1\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickEndLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t-1\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfThickEndLessThanThickStart() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11872\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfInvalidStrand() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfBlockCountLessThanZero() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t-1\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfBlockSizesNumberFormatException() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,not a number,1189,\t0,739,1347,");
    }

    @Test(expected=NumberFormatException.class)
    public void testValueOfBlockStartsNumberFormatException() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,not a number,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooManyBlockSizes() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,42,\t0,739,1347,");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testValueOfTooManyBlockStarts() {
        valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,42,");
    }

    @Test
    public void testEquals() {
        BedRecord record1 = valueOf("chr1\t11873\t14409");
        BedRecord record2 = valueOf("chr1\t11873\t14409\tuc001aaa.3");
        assertTrue(record1.equals(record1));
        assertTrue(record2.equals(record2));
        assertFalse(record1.equals(record2));
        assertFalse(record2.equals(record1));
        assertFalse(record1.equals(new Object()));
    }

    @Test
    public void testValueOfBED3() {
        BedRecord record = valueOf("chr1\t11873\t14409");
        assertEquals("chr1", record.chrom());
        assertEquals(11873L, record.start());
        assertEquals(14409L, record.end());
        assertEquals(Format.BED3, record.format());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409", record.toString());
    }

    @Test
    public void testValueOfBED4() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3");
        assertEquals("chr1", record.chrom());
        assertEquals(11873L, record.start());
        assertEquals(14409L, record.end());
        assertEquals("uc001aaa.3", record.name());
        assertEquals(Format.BED4, record.format());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3", record.toString());
    }

    @Test
    public void testValueOfBED5() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0");
        assertEquals("chr1", record.chrom());
        assertEquals(11873L, record.start());
        assertEquals(14409L, record.end());
        assertEquals("uc001aaa.3", record.name());
        assertEquals("0", record.score());
        assertEquals(Format.BED5, record.format());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0", record.toString());
    }

    @Test
    public void testValueOfBED6() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+");
        assertEquals("chr1", record.chrom());
        assertEquals(11873L, record.start());
        assertEquals(14409L, record.end());
        assertEquals("uc001aaa.3", record.name());
        assertEquals("0", record.score());
        assertEquals("+", record.strand());
        assertEquals(Format.BED6, record.format());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+", record.toString());
    }

    @Test
    public void testValueOfBED12() {
        BedRecord record = valueOf("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
        assertEquals("chr1", record.chrom());
        assertEquals(11873L, record.start());
        assertEquals(14409L, record.end());
        assertEquals("uc001aaa.3", record.name());
        assertEquals("0", record.score());
        assertEquals("+", record.strand());
        assertEquals(11873L, record.thickStart());
        assertEquals(11873L, record.thickEnd());
        assertEquals("0", record.itemRgb());
        assertEquals(3, record.blockCount());
        assertEquals(3, record.blockSizes().length);
        assertEquals(354L, record.blockSizes()[0]);
        assertEquals(109L, record.blockSizes()[1]);
        assertEquals(1189L, record.blockSizes()[2]);
        assertEquals(3, record.blockStarts().length);
        assertEquals(0L, record.blockStarts()[0]);
        assertEquals(739L, record.blockStarts()[1]);
        assertEquals(1347L, record.blockStarts()[2]);
        assertEquals(Format.BED12, record.format());
        assertEquals(Range.closedOpen(11873L, 14409L), record.toRange());
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189\t0,739,1347", record.toString());
    }
}
