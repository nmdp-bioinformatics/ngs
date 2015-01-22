/*

    ngs-range  Guava ranges for genomics.
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
package org.nmdp.ngs.range;

import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.range.Ranges.center;

import java.math.BigInteger;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import org.junit.Test;

/**
 * Unit test for Ranges.
 */
public final class RangesTest {

    @Test(expected=NullPointerException.class)
    public void testCenterNull() {
        center(null);
    }

    @Test(expected=IllegalStateException.class)
    public void testCenterNoBounds() {
        center(Range.<Integer>all());
    }

    @Test
    public void testCenterNoLowerBound() {
        assertEquals(Long.valueOf(42L), center(Range.upTo(42L, BoundType.CLOSED)));
    }

    @Test
    public void testCenterNoUpperBound() {
        assertEquals(Long.valueOf(42L), center(Range.downTo(42L, BoundType.CLOSED)));
    }

    @Test
    public void testCenterInteger() {
        assertEquals(Integer.valueOf(10), center(Range.closed(0, 20)));
    }

    @Test
    public void testCenterLong() {
        assertEquals(Long.valueOf(10L), center(Range.closed(0L, 20L)));
    }

    @Test
    public void testCenterBigInteger() {
        assertEquals(BigInteger.valueOf(10L), center(Range.closed(BigInteger.valueOf(0L), BigInteger.valueOf(20L))));
    }
}