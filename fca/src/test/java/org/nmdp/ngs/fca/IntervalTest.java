/*

    ngs-fca  Formal concept analysis for genomics.
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
package org.nmdp.ngs.fca;

import com.google.common.collect.Range;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

public final class IntervalTest {
    Interval<Integer> a, b, c, x, y, z;
    
    @Before
    public void testSetup() {
        a = new Interval<>(1, Range.closed(2, 4));
        b = new Interval<>(1, Range.closed(6, 8));
    }
    
    @Test
    public void testBefore() {
        assertTrue(a.before(b));
        assertFalse(b.before(a));
        assertFalse(Interval.MAGIC.before(a));
        assertFalse(a.before(Interval.MAGIC));
    }
    
    @Test
    public void testAfter() {
        assertTrue(b.after(a));
        assertFalse(a.after(b));
        assertFalse(Interval.MAGIC.before(a));
        assertFalse(a.before(Interval.MAGIC));
    }
    
    @Test
    public void testOverlaps() {
        assertTrue(a.overlaps(Interval.MAGIC));
        assertTrue(Interval.MAGIC.overlaps(a));
    }
}