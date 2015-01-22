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
package org.nmdp.ngs.range.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import org.junit.Test;

/**
 * Abstract unit test for implementations of RangeTree.
 */
public abstract class AbstractRangeTreeTest {
    protected static final int N = 1000;
    protected static final Range<Integer> empty = Range.closedOpen(1, 1);
    protected static final Range<Integer> singleton = Range.singleton(1);
    protected static final Range<Integer> miss = Range.singleton(-1);
    protected static final Range<Integer> closed = Range.closed(1, 100);
    protected static final Range<Integer> open = Range.open(1, 100);
    protected static final Range<Integer> closedOpen = Range.closedOpen(1, 100);
    protected static final Range<Integer> openClosed = Range.openClosed(1, 100);
    protected static final List<Range<Integer>> dense = Lists.newArrayListWithExpectedSize(N);
    protected static final List<Range<Integer>> sparse = Lists.newArrayListWithExpectedSize(N);

    static
    {
        for (int i = 0; i < N; i++) {
            dense.add(Range.closed(i, i + 100));
            sparse.add(Range.closed(i * 100, i * 100 + 100));
        }
    }

    /**
     * Create and return a new instance of an implementation of RangeTree to test.
     *
     * @param ranges variable number of ranges
     * @return a new instance of an implementation of RangeTree to test
     */
    protected abstract <C extends Comparable> RangeTree<C> create(Range<C>... ranges);

    /**
     * Create and return a new instance of an implementation of RangeTree to test.
     *
     * @param ranges list of ranges
     * @return a new instance of an implementation of RangeTree to test
     */
    protected abstract <C extends Comparable> RangeTree<C> create(List<Range<C>> ranges);


    @Test
    public void testCreateEmpty() {
        assertNotNull(create(Collections.<Range<Integer>>emptyList()));
    }

    @Test
    public void testCreateEmptyRange() {
        assertNotNull(create(empty));
    }

    @Test
    public void testCreateSingleton() {
        assertNotNull(create(singleton));
    }

    @Test
    public void testCreateSparse() {
        assertNotNull(create(sparse));
    }

    @Test
    public void testCreateDense() {
        assertNotNull(create(dense));
    }

    @Test
    public void testSizeEmpty() {
        assertEquals(0, create(Collections.<Range<Integer>>emptyList()).size());
    }

    @Test
    public void testSizeEmptyRange() {
        assertEquals(1, create(empty).size());
    }

    @Test
    public void testSizeSingeton() {
        assertEquals(1, create(singleton).size());
    }

    @Test
    public void testSizeSparse() {
        assertEquals(sparse.size(), create(sparse).size());
    }

    @Test
    public void testSizeDense() {
        assertEquals(dense.size(), create(dense).size());
    }

    @Test
    public void testIsEmptyEmpty() {
        assertTrue(create(Collections.<Range<Integer>>emptyList()).isEmpty());
    }

    @Test
    public void testIsEmptyEmptyRange() {
        assertFalse(create(empty).isEmpty());
    }

    @Test
    public void testIsEmptySingleton() {
        assertFalse(create(singleton).isEmpty());
    }

    @Test
    public void testIsEmptySparse() {
        assertFalse(create(sparse).isEmpty());
    }

    @Test
    public void testIsEmptyDense() {
        assertFalse(create(dense).isEmpty());
    }

    @Test
    public void testContainsEmpty() {
        assertFalse(create(empty).contains(0));
    }

    @Test
    public void testContainsSingletonMiss() {
        assertFalse(create(singleton).contains(0));
    }

    @Test
    public void testContainsSingletonHit() {
        assertTrue(create(singleton).contains(1));
    }

    @Test
    public void testContainsSparse() {
        assertTrue(create(sparse).contains(4));
    }

    @Test
    public void testContainsDense() {
        assertTrue(create(dense).contains(4));
    }

    @Test
    public void testCountEmpty() {
        assertEquals(0, create(empty).count(0));
    }

    @Test
    public void testCountSingletonMiss() {
        assertEquals(0, create(singleton).count(0));
    }

    @Test
    public void testCountSingletonHit() {
        assertEquals(1, create(singleton).count(1));
    }

    @Test
    public void testCountDense() {
        assertEquals(5, create(dense).count(4));
    }

    @Test
    public void testCountSparse() {
        assertEquals(1, create(sparse).count(4));
    }

    @Test
    public void testQueryEmpty() {
        assertTrue(Iterables.isEmpty(create(empty).query(0)));
    }

    @Test
    public void testQuerySingletonMiss() {
        assertTrue(Iterables.isEmpty(create(singleton).query(0)));
    }

    @Test
    public void testQuerySingletonHit() {
        assertEquals(1, Iterables.size(create(singleton).query(1)));
    }

    @Test
    public void testQueryDense() {
        assertEquals(5, Iterables.size(create(dense).query(4)));
    }

    @Test
    public void testQuerySparse() {
        assertEquals(1, Iterables.size(create(sparse).query(4)));
    }


    @Test(expected=NullPointerException.class)
    public void testCountNullRange() {
        create(sparse).count((Range<Integer>) null);
    }

    @Test
    public void testCountEmptyEmptyRange() {
        assertEquals(0, create(empty).count(empty));
    }

    @Test
    public void testCountEmptySingletonRange() {
        assertEquals(0, create(empty).count(singleton));
    }

    @Test
    public void testCountEmptyClosedRange() {
        assertEquals(0, create(empty).count(closed));
    }

    @Test
    public void testCountEmptyOpenRange() {
        assertEquals(0, create(empty).count(open));
    }

    @Test
    public void testCountEmptyClosedOpenRange() {
        assertEquals(0, create(empty).count(closedOpen));
    }

    @Test
    public void testCountEmptyOpenClosedRange() {
        assertEquals(0, create(empty).count(openClosed));
    }

    @Test
    public void testCountSingletonEmptyRange() {
        assertEquals(0, create(singleton).count(empty));
    }

    @Test
    public void testCountSingletonSingletonRangeMiss() {
        assertEquals(0, create(singleton).count(miss));
    }

    @Test
    public void testCountSingletonSingletonRangeHit() {
        assertEquals(1, create(singleton).count(singleton));
    }

    @Test
    public void testCountSingletonClosedRange() {
        assertEquals(1, create(singleton).count(closed));
    }

    @Test
    public void testCountSingletonOpenRange() {
        assertEquals(0, create(singleton).count(open));
    }

    @Test
    public void testCountSingletonClosedOpenRange() {
        assertEquals(1, create(singleton).count(closedOpen));
    }

    @Test
    public void testCountSingletonOpenClosedRange() {
        assertEquals(0, create(singleton).count(openClosed));
    }


    @Test
    public void testCountSparseEmptyRange() {
        assertEquals(0, create(sparse).count(empty));
    }

    @Test
    public void testCountSparseSingletonRangeMiss() {
        assertEquals(0, create(sparse).count(miss));
    }

    @Test
    public void testCountSparseSingletonRangeHit() {
        assertEquals(1, create(sparse).count(singleton));
    }

    @Test
    public void testCountSparseClosedRange() {
        assertEquals(2, create(sparse).count(closed));
    }

    @Test
    public void testCountSparseOpenRange() {
        assertEquals(1, create(sparse).count(open));
    }

    @Test
    public void testCountSparseClosedOpenRange() {
        assertEquals(1, create(sparse).count(closedOpen));
    }

    @Test
    public void testCountSparseOpenClosedRange() {
        assertEquals(2, create(sparse).count(openClosed));
    }


    @Test
    public void testCountDenseEmptyRange() {
        assertEquals(0, create(dense).count(empty));
    }

    @Test
    public void testCountDenseSingletonRangeMiss() {
        assertEquals(0, create(dense).count(miss));
    }

    @Test
    public void testCountDenseSingletonRangeHit() {
        assertEquals(2, create(dense).count(singleton));
    }

    @Test
    public void testCountDenseClosedRange() {
        assertEquals(101, create(dense).count(closed));
    }

    @Test
    public void testCountDenseOpenRange() {
        assertEquals(100, create(dense).count(open));
    }

    @Test
    public void testCountDenseClosedOpenRange() {
        assertEquals(100, create(dense).count(closedOpen));
    }

    @Test
    public void testCountDenseOpenClosedRange() {
        assertEquals(101, create(dense).count(openClosed));
    }


    @Test(expected=NullPointerException.class)
    public void testIntersectNullRange() {
        create(sparse).intersect((Range<Integer>) null);
    }

    @Test
    public void testIntersectEmptyEmptyRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(empty)));
    }

    @Test
    public void testIntersectEmptySingletonRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(singleton)));
    }

    @Test
    public void testIntersectEmptyClosedRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(closed)));
    }

    @Test
    public void testIntersectEmptyOpenRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(open)));
    }

    @Test
    public void testIntersectEmptyClosedOpenRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(closedOpen)));
    }

    @Test
    public void testIntersectEmptyOpenClosedRange() {
        assertTrue(Iterables.isEmpty(create(empty).intersect(openClosed)));
    }

    @Test
    public void testIntersectSingletonEmptyRange() {
        assertTrue(Iterables.isEmpty(create(singleton).intersect(empty)));
    }

    @Test
    public void testIntersectSingletonSingletonRangeMiss() {
        assertTrue(Iterables.isEmpty(create(singleton).intersect(miss)));
    }

    @Test
    public void testIntersectSingletonSingletonRangeHit() {
        assertEquals(1, Iterables.size(create(singleton).intersect(singleton)));
    }

    @Test
    public void testIntersectSingletonClosedRange() {
        assertEquals(1, Iterables.size(create(singleton).intersect(closed)));
    }

    @Test
    public void testIntersectSingletonOpenRange() {
        assertEquals(0, Iterables.size(create(singleton).intersect(open)));
    }

    @Test
    public void testIntersectSingletonClosedOpenRange() {
        assertEquals(1, Iterables.size(create(singleton).intersect(closedOpen)));
    }

    @Test
    public void testIntersectSingletonOpenClosedRange() {
        assertEquals(0, Iterables.size(create(singleton).intersect(openClosed)));
    }


    @Test
    public void testIntersectSparseEmptyRange() {
        assertTrue(Iterables.isEmpty(create(sparse).intersect(empty)));
    }

    @Test
    public void testIntersectSparseSingletonRangeMiss() {
        assertTrue(Iterables.isEmpty(create(sparse).intersect(miss)));
    }

    @Test
    public void testIntersectSparseSingletonRangeHit() {
        assertEquals(1, Iterables.size(create(sparse).intersect(singleton)));
    }

    @Test
    public void testIntersectSparseClosedRange() {
        assertEquals(2, Iterables.size(create(sparse).intersect(closed)));
    }

    @Test
    public void testIntersectSparseOpenRange() {
        assertEquals(1, Iterables.size(create(sparse).intersect(open)));
    }

    @Test
    public void testIntersectSparseClosedOpenRange() {
        assertEquals(1, Iterables.size(create(sparse).intersect(closedOpen)));
    }

    @Test
    public void testIntersectSparseOpenClosedRange() {
        assertEquals(2, Iterables.size(create(sparse).intersect(openClosed)));
    }


    @Test
    public void testIntersectDenseEmptyRange() {
        assertTrue(Iterables.isEmpty(create(dense).intersect(empty)));
    }

    @Test
    public void testIntersectDenseSingletonRangeMiss() {
        assertTrue(Iterables.isEmpty(create(dense).intersect(miss)));
    }

    @Test
    public void testIntersectDenseSingletonRangeHit() {
        assertEquals(2, Iterables.size(create(dense).intersect(singleton)));
    }

    @Test
    public void testIntersectDenseClosedRange() {
        assertEquals(101, Iterables.size(create(dense).intersect(closed)));
    }

    @Test
    public void testIntersectDenseOpenRange() {
        assertEquals(100, Iterables.size(create(dense).intersect(open)));
    }

    @Test
    public void testIntersectDenseClosedOpenRange() {
        assertEquals(100, Iterables.size(create(dense).intersect(closedOpen)));
    }

    @Test
    public void testIntersectDenseOpenClosedRange() {
        assertEquals(101, Iterables.size(create(dense).intersect(openClosed)));
    }


    @Test(expected=NullPointerException.class)
    public void testIntersectsNullRange() {
        create(sparse).intersects((Range<Integer>) null);
    }

    @Test(expected=NullPointerException.class)
    public void testIntersectRanges() {
        create(sparse).intersect((Iterable<Range<Integer>>) null);
    }

    @Test(expected=NullPointerException.class)
    public void testIntersectsNullRanges() {
        create(sparse).intersects((Iterable<Range<Integer>>) null);
    }

    @Test
    public void testCountDuplicateRanges() {
        assertEquals(4, create(closed, closed, closed, closed).count(closed));
    }


    // methods for benchmarking
    // todo:  move to external maven module, use caliper maven plugin?
    protected static final int M = 10;

    private void countMany(final RangeTree<Integer> tree) {
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                assertTrue(tree.count(j) > -1);
                assertNotNull(tree.query(j));
            }
        }
    }

    @Test
    public void testCreateOnceCountManyEmpty() {
        countMany(create(empty));
    }

    @Test
    public void testCreateOnceCountManySparse() {
        countMany(create(sparse));
    }

    @Test
    public void testCreateOnceCountManyDense() {
        countMany(create(dense));
    }
}
