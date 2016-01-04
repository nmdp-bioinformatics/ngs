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

import com.google.common.collect.ImmutableRangeSet;
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
        b = new Interval<>(1, Range.closed(8, 10));
        c = new Interval<>(1, Range.closed(2, 10));
        x = new Interval<>(1, Range.closed(5, 7));
        y = new Interval<>(1, Range.open(4, 8));
    }
    
    @Test
    public void testRelation() {
        assertTrue(a.relation(a) == Partial.Order.EQUAL);
        assertTrue(a.relation(c).lte());
        assertTrue(c.relation(a).gte());
        assertTrue(a.relation(b) == Partial.Order.NONCOMPARABLE);
        assertTrue(a.relation(Interval.MAGIC).lte());
        assertTrue(Interval.MAGIC.relation(a).gte());
    }
    
    @Test
    public void testIntersect() {
        assertEquals(a.intersect(c), a);
        assertEquals(c.intersect(a), a);
        assertEquals(a.intersect(a), a);
    }
    
    @Test
    public void testMinus() {
        Range range;
        ImmutableRangeSet.Builder builder;
        Interval.Difference difference;
        
        difference = c.minus(a);
        range = Range.openClosed(4, 10);
        builder = ImmutableRangeSet.builder().add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = a.minus(a);
        builder = ImmutableRangeSet.builder(); 
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = a.minus(b);
        range = a.toRange();
        builder = ImmutableRangeSet.builder().add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = b.minus(a);
        range = b.toRange();
        builder = ImmutableRangeSet.builder().add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = c.minus(x);
        range = Range.closedOpen(2, 5);
        builder = ImmutableRangeSet.builder().add(range);
        range = Range.openClosed(7, 10);
        builder.add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = x.minus(c);
        builder = ImmutableRangeSet.builder();
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
    }
    
    @Test
    public void testComplement() {
        Range range;
        ImmutableRangeSet.Builder builder;
        Interval.Difference difference;
        
        difference = a.complement();
        range = Range.lessThan(2);
        builder = ImmutableRangeSet.builder().add(range);
        range = Range.greaterThan(4);
        builder.add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 1);
        
        difference = Interval.MAGIC.complement();
        builder = ImmutableRangeSet.builder();
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 0);
        
        difference = Interval.NULL.complement();
        range = Range.all();
        builder = ImmutableRangeSet.builder().add(range);
        assertEquals(difference.getRanges(), builder.build());
        assertEquals(difference.getDimension(), 0);
    }
    
    @Test
    public void testAhead() {
        assertEquals(a.ahead(), new Interval<>(1, Range.greaterThan(4)));
        assertEquals(a.intersect(b).ahead(), new Interval<>(1, Range.all()));
        assertEquals(Interval.MAGIC.ahead(), Interval.NULL);
        assertEquals(Interval.NULL.ahead(), Interval.MAGIC);
    }
    
    @Test
    public void testBehind() {
        assertEquals(a.behind(), new Interval<>(1, Range.lessThan(2)));
        assertEquals(a.intersect(b).behind(), new Interval<>(1, Range.all()));
        assertEquals(Interval.MAGIC.behind(), Interval.NULL);
        assertEquals(Interval.NULL.behind(), Interval.MAGIC);
    }
    
    @Test
    public void testGap() {
        assertEquals(a.gap(b), y);
        assertEquals(b.gap(a), y);
        assertEquals(a.gap(a), Interval.NULL);
        assertEquals(a.gap(Interval.MAGIC), Interval.NULL);
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
    public void testBetween() {
        assertTrue(x.between(a, b));
        assertFalse(x.between(a, c));
    }
    
    @Test
    public void testOverlaps() {
        assertTrue(a.overlaps(Interval.MAGIC));
        assertTrue(Interval.MAGIC.overlaps(a));
        assertTrue(a.overlaps(a));
    }
    
    @Test
    public void testThen() {
        assertTrue(a.then(a));
        assertTrue(a.then(Interval.MAGIC));
        assertFalse(Interval.MAGIC.then(a));
    }
    
    @Test
    public void testStarts() {
        assertFalse(a.starts(a));
        assertTrue(a.starts(c));
        assertFalse(c.starts(a));
        assertFalse(a.starts(Interval.MAGIC));
    }
    
    @Test
    public void testEnds() {
        assertFalse(a.ends(a));
        assertTrue(b.ends(c));
        assertFalse(c.ends(b));
        assertFalse(b.ends(Interval.MAGIC));
    }
    
    @Test
    public void testEquals() {
        assertTrue(a.equals(a));
        assertTrue(a.equals(new Interval<>(1, Range.closed(2, 4))));
        assertFalse(a.equals(b));
        assertFalse(a.equals(Interval.NULL));
        assertFalse(a.equals(Interval.MAGIC));
    }
}