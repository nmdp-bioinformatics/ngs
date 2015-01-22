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

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigInteger;

import com.google.common.collect.BoundType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;

/**
 * Utility methods on ranges.
 */
public final class Ranges {

    /**
     * Private no-arg constructor.
     */
    private Ranges() {
        // empty
    }

    /**
     * Return the center of the specified range.
     *
     * @param <C> range endpoint type
     * @param range range, must not be null
     * @return the center of the specified range
     */
    public static <C extends Comparable> C center(final Range<C> range) {
        checkNotNull(range);
        if (!range.hasLowerBound() && !range.hasUpperBound()) {
            throw new IllegalStateException("cannot find the center of a range without bounds");
        }
        if (!range.hasLowerBound()) {
            return range.upperEndpoint();
        }
        if (!range.hasUpperBound()) {
            return range.lowerEndpoint();
        }
        C lowerEndpoint = range.lowerEndpoint();
        C upperEndpoint = range.upperEndpoint();

        if (upperEndpoint instanceof Integer) {
            Integer upper = (Integer) upperEndpoint;
            Integer lower = (Integer) lowerEndpoint;
            return (C) Integer.valueOf((upper.intValue() + lower.intValue()) / 2);
        }
        if (upperEndpoint instanceof Long) {
            Long upper = (Long) upperEndpoint;
            Long lower = (Long) lowerEndpoint;
            return (C) Long.valueOf((upper.longValue() + lower.longValue()) / 2L);
        }
        if (upperEndpoint instanceof BigInteger) {
            BigInteger upper = (BigInteger) upperEndpoint;
            BigInteger lower = (BigInteger) lowerEndpoint;
            BigInteger two = BigInteger.valueOf(2L);
            return (C) upper.subtract(lower).divide(two);
        }

        // todo:  could potentially calculate the center of any range with a discrete domain
        throw new IllegalStateException("cannot find the center of a range whose endpoint type is not Integer, Long, or BigInteger");
    }

    /**
     * Return true if the specified ranges intersect.
     *
     * @param <C> range endpoint type
     * @param range0 first range, must not be null
     * @param range1 second range, must not be null
     * @return true if the specified ranges intersect
     */
    public static <C extends Comparable> boolean intersect(final Range<C> range0, final Range<C> range1) {
        checkNotNull(range0);
        checkNotNull(range1);
        return range0.isConnected(range1) && !range0.intersection(range1).isEmpty();
    }

    /**
     * Return true if the specified range is strictly less than the specified value.
     *
     * @param <C> range endpoint type
     * @param range range, must not be null
     * @param value value, must not be null
     * @return true if the specified range is strictly less than the specified value
     */
    public static <C extends Comparable> boolean isLessThan(final Range<C> range, final C value) {
        checkNotNull(range);
        checkNotNull(value);

        if (!range.hasUpperBound()) {
            return false;
        }
        if (range.upperBoundType() == BoundType.OPEN && range.upperEndpoint().equals(value)) {
            return true;
        }
        return range.upperEndpoint().compareTo(value) < 0;
    }

    /**
     * Return true if the specified range is strictly greater than the specified value.
     *
     * @param <C> range endpoint type
     * @param range range, must not be null
     * @param value value, must not be null
     * @return true if the specified range is strictly greater than the specified value
     */
    public static <C extends Comparable> boolean isGreaterThan(final Range<C> range, final C value) {
        checkNotNull(range);
        checkNotNull(value);

        if (!range.hasLowerBound()) {
            return false;
        }
        if (range.lowerBoundType() == BoundType.OPEN && range.lowerEndpoint().equals(value)) {
            return true;
        }
        return range.lowerEndpoint().compareTo(value) > 0;
    }


    /**
     * Return an ordering by lower endpoint over ranges.
     *
     * @param <C> range endpoint type
     * @return an ordering by lower endpoint over ranges
     */
    public static <C extends Comparable> Ordering<Range<C>> orderingByLowerEndpoint() {
        return new Ordering<Range<C>>() {
            @Override
            public int compare(final Range<C> left, final Range<C> right) {
                return ComparisonChain.start()
                    .compare(left.hasLowerBound(), right.hasLowerBound())
                    .compare(left.lowerEndpoint(), right.lowerEndpoint())
                    .result();
            }
        };
    }

    /**
     * Return a reverse ordering by lower endpoint over ranges.
     *
     * @param <C> range endpoint type
     * @return a reverse ordering by lower endpoint over ranges
     */
    public static <C extends Comparable> Ordering<Range<C>> reverseOrderingByLowerEndpoint() {
        Ordering<Range<C>> orderingByLowerEndpoint = orderingByLowerEndpoint();
        return orderingByLowerEndpoint.reverse();
    }

    /**
     * Return an ordering by upper endpoint over ranges.
     *
     * @param <C> range endpoint type
     * @return an ordering by upper endpoint over ranges
     */
    public static <C extends Comparable> Ordering<Range<C>> orderingByUpperEndpoint() {
        return new Ordering<Range<C>>() {
            @Override
            public int compare(final Range<C> left, final Range<C> right) {
                return ComparisonChain.start()
                    .compare(left.hasUpperBound(), right.hasUpperBound())
                    .compare(left.upperEndpoint(), right.upperEndpoint())
                    .result();
            }
        };
    }

    /**
     * Return a reverse ordering by upper endpoint over ranges.
     *
     * @param <C> range endpoint type
     * @return a reverse ordering by upper endpoint over ranges
     */
    public static <C extends Comparable> Ordering<Range<C>> reverseOrderingByUpperEndpoint() {
        Ordering<Range<C>> orderingByUpperEndpoint = orderingByUpperEndpoint();
        return orderingByUpperEndpoint.reverse();
    }
}
