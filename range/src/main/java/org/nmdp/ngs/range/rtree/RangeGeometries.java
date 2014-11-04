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

import static com.google.common.base.Preconditions.checkNotNull;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Geometries for use in building R-Trees and R*-Trees from Guava ranges.
 *
 * <pre>
 * import static org.nmdp.ngs.range.rtree.RangeGeometries.*;
 *
 * RTree&lt;String&gt; rtree = RTree.create();
 * rtree = rtree.add("foo", closed(10, 20));
 * rtree = rtree.add("bar", closedOpen(14, 28));
 * rtree = rtree.add("baz", open(18, 36));
 *
 * for (String result : rtree.search(singleton(20)).toBlocking().toIterable()) {
 *   // ...
 * }
 * </pre>
 */
public final class RangeGeometries {

    /**
     * Private no-arg constructor.
     */
    private RangeGeometries() {
        // empty
    }


    /**
     * Create and return a new point geometry from the specified singleton value.
     *
     * @param value singleton value, must not be null
     * @return a new point geometry from the specified singleton value
     */
    public static <N extends Number & Comparable<? super N>> Point singleton(final N value) {
        checkNotNull(value);
        return Geometries.point(value.doubleValue(), 0.5d);
    }

    /**
     * Create and return a new rectangle geometry from the specified closed range <code>[lower..upper]</code>.
     *
     * @param lower lower endpoint, must not be null
     * @param upper upper endpoint, must not be null
     * @return a new rectangle geometry from the specified closed range
     */
    public static <N extends Number & Comparable<? super N>> Rectangle closed(final N lower, final N upper) {
        checkNotNull(lower);
        checkNotNull(upper);
        return range(Range.closed(lower, upper));
    }

    /**
     * Create and return a new rectangle geometry from the specified closed open range <code>[lower..upper)</code>.
     *
     * @param lower lower endpoint, must not be null
     * @param upper upper endpoint, must not be null
     * @return a new rectangle geometry from the specified closed range
     */
    public static <N extends Number & Comparable<? super N>> Rectangle closedOpen(final N lower, final N upper) {
        checkNotNull(lower);
        checkNotNull(upper);
        return range(Range.closedOpen(lower, upper));
    }

    /**
     * Create and return a new rectangle geometry from the specified open closed range <code>(lower..upper]</code>
     *
     * @param lower lower endpoint, must not be null
     * @param upper upper endpoint, must not be null
     * @return a new rectangle geometry from the specified closed range
     */
    public static <N extends Number & Comparable<? super N>> Rectangle openClosed(final N lower, final N upper) {
        checkNotNull(lower);
        checkNotNull(upper);
        return range(Range.openClosed(lower, upper));
    }
    
    /**
     * Create and return a new rectangle geometry from the specified open range <code>(lower..upper)</code>.
     *
     * @param lower lower endpoint, must not be null
     * @param upper upper endpoint, must not be null
     * @return a new rectangle geometry from the specified closed range
     */
    public static <N extends Number & Comparable<? super N>> Rectangle open(final N lower, final N upper) {
        checkNotNull(lower);
        checkNotNull(upper);
        return range(Range.open(lower, upper));
    }

    /**
     * Create and return a new rectangle geometry from the specified range.
     *
     * @param range range, must not be null, must not be empty, and must have lower and upper bounds
     * @return a new rectangle geometry from the specified range
     */
    public static <N extends Number & Comparable<? super N>> Rectangle range(final Range<N> range) {
        checkNotNull(range);
        if (range.isEmpty()) {
            throw new IllegalArgumentException("range must not be empty");
        }
        if (!range.hasLowerBound() || !range.hasUpperBound()) {
            throw new IllegalArgumentException("range must have lower and upper bounds");
        }
        Number lowerEndpoint = range.lowerEndpoint();
        BoundType lowerBoundType = range.lowerBoundType();
        Number upperEndpoint = range.upperEndpoint();
        BoundType upperBoundType = range.upperBoundType();

        /*

          Since we are representing genomic coordinate systems, the expectation is
          that endpoints are instance of Integer, Long, or BigInteger; thus for open
          lower and upper bounds we can safely add or substract 1.0 respectively.

          Then by convention a rectangle with y1 0.0 and height of 1.0 is used.

          closed(10, 20) --> (10.0, 0.0, 20.0, 1.0)
          closedOpen(10, 20) --> (10.0, 0.0, 19.0, 1.0)
          openClosed(10, 20) --> (11.0, 0.0, 20.0, 1.0)
          open(10, 20) --> (11.0, 0.0, 19.0, 1.0);

          closed(10, 11) --> (10.0, 0.0, 11.0, 1.0)
          closedOpen(10, 11) --> (10.0, 0.0, 10.0, 1.0)
          openClosed(10, 11) --> (11.0, 0.0, 11.0, 1.0)
          open(10, 11) --> empty, throw exception

          closed(10, 10) --> (10.0, 0.0, 10.0, 1.0)
          closedOpen(10, 10) --> empty, throw exception
          openClosed(10, 10) --> empty, throw exception
          open(10, 10) --> empty, throw exception

        */
        double x1 = lowerBoundType == BoundType.OPEN ? lowerEndpoint.doubleValue() + 1.0d : lowerEndpoint.doubleValue();
        double y1 = 0.0d;
        double x2 = upperBoundType == BoundType.OPEN ? upperEndpoint.doubleValue() - 1.0d : upperEndpoint.doubleValue();
        double y2 = 1.0d;
        return Geometries.rectangle(x1, y1, x2, y2);
    }
}
