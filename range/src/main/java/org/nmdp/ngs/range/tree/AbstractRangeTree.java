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
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

/**
 * Abstract implementation of range tree.  Most methods will need
 * to be overridden to improve performance.
 *
 * @param <C> range endpoint type
 */
public abstract class AbstractRangeTree<C extends Comparable> implements RangeTree<C> {

    @Override
    public int size() {
        return count(Range.<C>all());
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(final C location) {
        return count(location) > 0;
    }

    @Override
    public int count(final C location) {
        return count(Range.singleton(location));
    }

    @Override
    public Iterable<Range<C>> query(final C location) {
        return intersect(Range.singleton(location));
    }

    @Override
    public int count(final Range<C> query) {
        return Iterables.size(intersect(query));
    }

    @Override
    public boolean intersects(final Range<C> query) {
        return count(query) > 0;
    }

    @Override
    public Iterable<Set<Range<C>>> intersect(final Iterable<Range<C>> query) {
        checkNotNull(query);
        List<Set<Range<C>>> result = Lists.newLinkedList();
        for (Range<C> range0 : query) {
            for (Range<C> range1 : intersect(range0)) {
                result.add(ImmutableSet.of(range0, range1));
            }
        }
        return result;
    }

    @Override
    public boolean intersects(final Iterable<Range<C>> query) {
        checkNotNull(query);
        for (Range<C> range0 : query) {
            for (Range<C> range1 : intersect(range0)) {
                return true;
            }
        }
        return false;
    }
}
