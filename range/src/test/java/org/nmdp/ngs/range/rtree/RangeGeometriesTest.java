/*

    ngs-range  Guava ranges for genomics.
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
package org.nmdp.ngs.range.rtree;

import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.range.rtree.RangeGeometries.closed;
import static org.nmdp.ngs.range.rtree.RangeGeometries.closedOpen;
import static org.nmdp.ngs.range.rtree.RangeGeometries.open;
import static org.nmdp.ngs.range.rtree.RangeGeometries.openClosed;
import static org.nmdp.ngs.range.rtree.RangeGeometries.range;
import static org.nmdp.ngs.range.rtree.RangeGeometries.singleton;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;

import com.github.davidmoten.rtree.geometry.Geometries;

import com.google.common.collect.Range;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for RangeGeometries.
 */
public final class RangeGeometriesTest {

    @Test(expected=NullPointerException.class)
    public void testSingletonNull() {
        singleton(null);
    }

    @Test
    public void testSingleton() {
        assertEquals(Geometries.point(42.0d, 0.5d), singleton(42L));
    }

    @Test(expected=NullPointerException.class)
    public void testClosedNullLower() {
        closed(null, 42L);
    }

    @Test(expected=NullPointerException.class)
    public void testClosedNullUpper() {
        closed(24L, null);
    }

    @Test
    public void testClosed() {
        assertEquals(Geometries.rectangle(24.0d, 0.0d, 42.0d, 1.0d), closed(24L, 42L));
    }

    @Test(expected=NullPointerException.class)
    public void testClosedOpenNullLower() {
        closedOpen(null, 42L);
    }

    @Test(expected=NullPointerException.class)
    public void testClosedOpenNullUpper() {
        closedOpen(24L, null);
    }

    @Test
    public void testClosedOpen() {
        assertEquals(Geometries.rectangle(24.0d, 0.0d, 41.0d, 1.0d), closedOpen(24L, 42L));
    }

    @Test(expected=NullPointerException.class)
    public void testOpenClosedNullLower() {
        openClosed(null, 42L);
    }

    @Test(expected=NullPointerException.class)
    public void testOpenClosedNullUpper() {
        openClosed(24L, null);
    }

    @Test
    public void testOpenClosed() {
        assertEquals(Geometries.rectangle(25.0d, 0.0d, 42.0d, 1.0d), openClosed(24L, 42L));
    }

    @Test(expected=NullPointerException.class)
    public void testOpenNullLower() {
        open(null, 42L);
    }

    @Test(expected=NullPointerException.class)
    public void testOpenNullUpper() {
        open(24L, null);
    }

    @Test
    public void testOpen() {
        assertEquals(Geometries.rectangle(25.0d, 0.0d, 41.0d, 1.0d), open(24L, 42L));
    }

    @Test(expected=NullPointerException.class)
    public void testRangeNullRange() {
        range(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRangeEmptyRange() {
        range(Range.closedOpen(42L, 42L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRangeNoLowerBound() {
        range(Range.lessThan(42L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testRangeNoUpperBound() {
        range(Range.greaterThan(42L));
    }

    @Test
    public void testRange() {
        assertEquals(Geometries.rectangle(42.0d, 0.0d, 42.0d, 1.0d), range(Range.singleton(42L)));
        assertEquals(Geometries.rectangle(24.0d, 0.0d, 42.0d, 1.0d), range(Range.closed(24L, 42L)));
        assertEquals(Geometries.rectangle(24.0d, 0.0d, 41.0d, 1.0d), range(Range.closedOpen(24L, 42L)));
        assertEquals(Geometries.rectangle(25.0d, 0.0d, 42.0d, 1.0d), range(Range.openClosed(24L, 42L)));
        assertEquals(Geometries.rectangle(25.0d, 0.0d, 41.0d, 1.0d), range(Range.open(24L, 42L)));
    }

    @Test
    public void testRTree() {
        RTree<String> rtree = RTree.create();
        rtree = rtree.add("foo", closed(10, 20));
        rtree = rtree.add("bar", closedOpen(14, 28));
        rtree = rtree.add("baz", open(18, 36));

        int count = 0;
        // todo:  https://github.com/davidmoten/rtree/issues/10
        //for (Entry<String> result : rtree.search(singleton(20)).toBlocking().toIterable()) {
        for (Entry<String> result : rtree.search(range(Range.singleton(20))).toBlocking().toIterable()) {
            count++;
        }
        assertEquals(3, count);
    }
}
              
