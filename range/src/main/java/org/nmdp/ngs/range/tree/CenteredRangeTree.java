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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;

import org.nmdp.ngs.range.Ranges;

/**
 * Centered range tree.
 *
 * @param <C> range endpoint type
 */
public final class CenteredRangeTree<C extends Comparable> extends AbstractRangeTree<C> {
    /** Cached size. */
    private final int size;

    /** Root node, if any. */
    private final Node root;


    /**
     * Create a new centered range tree with the specified ranges.
     *
     * @param ranges ranges, must not be null
     */
    private CenteredRangeTree(final Iterable<Range<C>> ranges) {
        checkNotNull(ranges);
        // O(n) hit to cache size
        size = Iterables.size(ranges);
        root = createNode(ranges);
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterable<Range<C>> intersect(final Range<C> range) {
        checkNotNull(range);
        List<Range<C>> result = Lists.newLinkedList();
        Set<Node> visited = Sets.newHashSet();
        depthFirstSearch(range, root, result, visited);
        return result;
    }

    /**
     * Create and return a new node for the specified ranges.
     *
     * @param ranges ranges
     * @return a new node for the specified ranges
     */
    private Node createNode(final Iterable<Range<C>> ranges) {
        Range<C> span = Iterables.getFirst(ranges, null);
        if (span == null) {
            return null;
        }
        for (Range<C> range : ranges) {
            checkNotNull(range, "ranges must not contain null ranges");
            span = range.span(span);
        }
        if (span.isEmpty()) {
            return null;
        }
        C center = Ranges.center(span);
        List<Range<C>> left = Lists.newArrayList();
        List<Range<C>> right = Lists.newArrayList();
        List<Range<C>> overlap = Lists.newArrayList();
        for (Range<C> range : ranges) {
            if (Ranges.isLessThan(range, center)) {
                left.add(range);
            }
            else if (Ranges.isGreaterThan(range, center)) {
                right.add(range);
            }
            else {
                overlap.add(range);
            }
        }
        return new Node(center, createNode(left), createNode(right), overlap);
    }

    /**
     * Depth first search.
     *
     * @param query query range
     * @param node node
     * @param result list of matching ranges
     * @param visited set of visited nodes
     */
    private void depthFirstSearch(final Range<C> query, final Node node, final List<Range<C>> result, final Set<Node> visited) {
        if (node == null || visited.contains(node) || query.isEmpty()) {
            return;
        }
        if (node.left() != null && Ranges.isLessThan(query, node.center())) {
            depthFirstSearch(query, node.left(), result, visited);
        }
        else if (node.right() != null && Ranges.isGreaterThan(query, node.center())) {
            depthFirstSearch(query, node.right(), result, visited);
        }
        if (Ranges.isGreaterThan(query, node.center())) {
            for (Range<C> range : node.overlapByUpperEndpoint()) {
                if (Ranges.intersect(range, query)) {
                    result.add(range);
                }
                if (Ranges.isGreaterThan(query, range.upperEndpoint())) {
                    break;
                }
            }
        }
        else if (Ranges.isLessThan(query, node.center())) {
            for (Range<C> range : node.overlapByLowerEndpoint()) {
                if (Ranges.intersect(range, query)) {
                    result.add(range);
                }
                if (Ranges.isLessThan(query, range.lowerEndpoint())) {
                    break;
                }
            }
        }
        else {
            result.addAll(node.overlapByLowerEndpoint());
        }
        visited.add(node);
    }

    /**
     * Node.
     */
    private class Node {
        /** Center. */
        private final C center;

        /** Left node, if any. */
        private final Node left;

        /** Right node, if any. */
        private final Node right;

        /** List of overlapping ranges ordered by lower endpoint. */
        private final List<Range<C>> overlapByLowerEndpoint;

        /** List of overlapping ranges ordered by upper endpoint. */
        private final List<Range<C>> overlapByUpperEndpoint;


        /**
         * Create a new node.
         *
         * @param center center
         * @param left left node, if any
         * @param right right node, if any
         * @param overlap list of overlapping nodes
         */
        Node(final C center, final Node left, final Node right, final List<Range<C>> overlap) {
            this.center = center;
            this.left = left;
            this.right = right;
            overlapByLowerEndpoint = Lists.newArrayList(overlap);
            overlapByUpperEndpoint = Lists.newArrayList(overlap);
            Ordering<Range<C>> orderingByLowerEndpoint = Ranges.orderingByLowerEndpoint();
            Ordering<Range<C>> reverseOrderingByUpperEndpoint = Ranges.reverseOrderingByUpperEndpoint();
            Collections.sort(overlapByLowerEndpoint, orderingByLowerEndpoint);
            Collections.sort(overlapByUpperEndpoint, reverseOrderingByUpperEndpoint);
        }


        /**
         * Return the center.
         *
         * @return the center
         */
        C center() {
            return center;
        }

        /**
         * Return the left node, if any.
         *
         * @return the left node or <code>null</code> if no such node exists
         */
        Node left() {
            return left;
        }

        /**
         * Return the right node, if any.
         *
         * @return the right node or <code>null</code> if no such node exists
         */
        Node right() {
            return right;
        }

        /**
         * Return the list of overlapping ranges ordered by lower endpoint.
         *
         * @return the list of overlapping ranges ordered by lower endpoint
         */
        List<Range<C>> overlapByLowerEndpoint() {
            return overlapByLowerEndpoint;
        }

        /**
         * Return the list of overlapping ranges ordered by upper endpoint.
         *
         * @return the list of overlapping ranges ordered by upper endpoint
         */
        List<Range<C>> overlapByUpperEndpoint() {
            return overlapByUpperEndpoint;
        }
    }


    /**
     * Create and return a new range tree from the specified ranges.
     *
     * @param <C> range endpoint type
     * @param ranges ranges, must not be null
     * @return a new range tree from the specified ranges
     */
    public static <C extends Comparable> RangeTree<C> create(final Iterable<Range<C>> ranges) {
        return new CenteredRangeTree<C>(ranges);
    }
}
