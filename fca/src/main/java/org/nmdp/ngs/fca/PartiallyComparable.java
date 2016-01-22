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

import com.google.common.collect.DiscreteDomain;

/**
 * Partially ordered sets.
 * @param <C> 
 * @param <P> 
 */
public interface PartiallyComparable<C extends Comparable, P> {

    /**
     * Enumerated partial orders that extends natural (complete) orders for
     * comparable objects.
     * @see <a href="https://en.wikipedia.org/wiki/Partially_ordered_set">
     * partial order</a>
     */
    public static enum Order {

        /**
         * Equivalent to a return value of -1 for comparable objects. For example, 0
         * is less than 1 and, likewise, {0} is less than (a subset of) {0, 1}.
         */
        LESS,

        /**
         * Equivalent to a return value of 1 for comparable objects. For example, 1
         * is greater than 0 and, likewise, {0, 1} is greater than (a superset of)
         * {0}.
         */
        GREATER,

        /**
         * Equivalent to a return value of 0 for comparable objects. For example, 1
         * is equal to 1 and, likewise, {1} is equal to {1}.
         */
        EQUAL,

        /**
         * No equivalent for comparable objects. For example, {0} and {1} are
         * disjoint and therefore non-comparable.
         */
        NONCOMPARABLE;

        /**
         * Partial ordering direction.
         */
        public static enum Direction {
            /**
             * Forward partial ordering direction.
             */
            FORWARD,

            /**
             * Reverse partial ordering direction.
             */
            REVERSE
        }

        public boolean g() {
            return greater();
        }

        public boolean greater() {
            return this.equals(GREATER);
        }

        public boolean greaterOrEqual() {
            return gte();
        }

        public boolean gte() {
            return this.equals(GREATER) || this.equals(EQUAL);
        }

        public boolean l() {
            return less();
        }

        public boolean less() {
            return this.equals(LESS);
        }

        public boolean lessOrEqual() {
            return lte();
        }

        public boolean lte() {
            return this.equals(LESS) || this.equals(EQUAL);
        }
    }

    /**
     * Determine the partial ordering.
     *
     * @param that partially ordered object
     * @return partial ordering
     */
    Order relation(P that); // todo: <? extends T> ?
    
    P intersect(P that);

    P union(P that);

    long measure(DiscreteDomain<C> domain);
}
