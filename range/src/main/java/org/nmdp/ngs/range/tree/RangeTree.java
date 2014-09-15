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

import java.util.Set;

import com.google.common.collect.Range;

/**
 * Range tree.
 *
 * @param <C> range endpoint type
 */
public interface RangeTree<C extends Comparable> {

    /**
     * Return the number of ranges in this range tree.
     *
     * @return the number of ranges in this range tree
     */
    int size();

    /**
     * Return true if the number of ranges in this range tree is zero.
     *
     * @return true if the number of ranges in this range tree is zero
     */
    boolean isEmpty();

    /**
     * Return true if the specified location intersects with any ranges in this range tree.
     *
     * @param location location to intersect
     * @return true if the specified location intersects with any ranges in this range tree
     */
    boolean contains(C location);

    /**
     * Return the number of ranges in this range tree at the specified location.
     *
     * @param location location
     * @return the number of ranges in this range tree at the specified location
     */
    int count(C location);

    /**
     * Return the ranges in this range tree at the specified location, if any.
     *
     * @param location location
     * @return the ranges in this range tree at the specified location, if any
     */
    Iterable<Range<C>> query(C location);

    /**
     * Return the number of ranges in this range tree that intersect the specified query range.
     *
     * @param query range to intersect, must not be null
     * @return the number of ranges in this range tree that intersect the specified query range
     */
    int count(Range<C> query);

    /**
     * Return the ranges in this range tree that intersect the specified query range, if any.
     *
     * @param query range to intersect, must not be null
     * @return the ranges in this range tree that intersect the specified query range, if any
     */
    Iterable<Range<C>> intersect(Range<C> query);

    /**
     * Return true if the specified query range intersects with any ranges in this range tree.
     *
     * @param query range to intersect, must not be null
     * @return true if the specified query range intersects with any ranges in this range tree
     */
    boolean intersects(Range<C> query);

    /**
     * Return the intersection of the ranges in this range tree with the specified query list of
     * ranges as intersecting pairs of ranges, if any.
     *
     * @param query list of ranges to intersect, must not be null
     * @return the intersection of the ranges in this range tree with the specified query list of
     *    ranges as intersecting pairs of ranges, if any
     */
    Iterable<Set<Range<C>>> intersect(Iterable<Range<C>> query);

    /**
     * Return true if any range in the specified query list of ranges intersects
     * with any ranges in this range tree.
     *
     * @param query list of ranges to intersect, must not be null
     * @return true if any range in the specified query list of ranges intersects
     *    with any ranges in this range tree
     */
    boolean intersects(Iterable<Range<C>> query);

    /*

       Additional queries:

       Iterable<Range<C>> closest(Range<C> query);
       Iterable<Set<Range<C>>> closest(Iterable<Range<C>> query);

       bedtools closest options:
         -s Force strandedness. That is, find the closest feature in B overlaps A on the
              same strand. By default, this is disabled.
         -d In addition to the closest feature in B, report its distance to A as an extra
              column. The reported distance for overlapping features will be 0.
         -t How ties for closest feature should be handled. This occurs when two features
              in B have exactly the same overlap with a feature in A. By default, all such
              features in B are reported.

              Here are the other choices controlling how ties are handled:
                all- Report all ties (default).
                first- Report the first tie that occurred in the B file.
                last- Report the last tie that occurred in the B file.

       see
       http://bedtools.readthedocs.org/en/latest/content/tools/closest.html

     */
}
