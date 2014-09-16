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
package org.nmdp.ngs.range.tree;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import org.nmdp.ngs.range.Ranges;

/**
 * Range list.
 *
 * @param <C> range endpoint type
 */
public final class RangeList<C extends Comparable> extends AbstractRangeTree<C> {
    /** List of ranges. */
    private final List<Range<C>> ranges;


    /**
     * Create a new range list with the specified ranges.
     *
     * @param ranges ranges, must not be null
     */
    private RangeList(final Iterable<Range<C>> ranges) {
        checkNotNull(ranges);
        this.ranges = ImmutableList.copyOf(ranges);
    }


    @Override
    public int size() {
        return ranges.size();
    }

    @Override
    public boolean isEmpty() {
        return ranges.isEmpty();
    }

    @Override
    public Iterable<Range<C>> intersect(final Range<C> query) {
        checkNotNull(query);
        List<Range<C>> result = Lists.newLinkedList();
        for (Range<C> range : ranges) {
            if (Ranges.intersect(range, query)) {
                result.add(range);
            }
        }
        return result;
    }


    /**
     * Create and return a new range tree from the specified ranges.
     *
     * @param <C> range endpoint type
     * @param ranges ranges, must not be null
     * @return a new range tree from the specified ranges
     */
    public static <C extends Comparable> RangeTree<C> create(final Iterable<Range<C>> ranges) {
        return new RangeList<C>(ranges);
    }
}
