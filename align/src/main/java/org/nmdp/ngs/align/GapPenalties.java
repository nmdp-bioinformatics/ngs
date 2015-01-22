/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import javax.annotation.concurrent.Immutable;

/**
 * Gap penalties.
 */
@Immutable
public final class GapPenalties {
    private final short match;
    private final short replace;
    private final short insert;
    private final short delete;
    private final short extend;


    private GapPenalties(final short match,
                         final short replace,
                         final short insert,
                         final short delete,
                         final short extend) {
        this.match = match;
        this.replace = replace;
        this.insert = insert;
        this.delete = delete;
        this.extend = extend;
    }


    /**
     * Return the match penalty.
     *
     * @return the match penalty
     */
    public short match() {
        return match;
    }

    /**
     * Return the replace penalty.
     *
     * @return the replace penalty
     */
    public short replace() {
        return replace;
    }

    /**
     * Return the insert penalty.
     *
     * @return the insert penalty
     */
    public short insert() {
        return insert;
    }

    /**
     * Return the delete penalty.
     *
     * @return the delete penalty
     */
    public short delete() {
        return delete;
    }

    /**
     * Return the extend penalty.
     *
     * @return the extend penalty
     */
    public short extend() {
        return extend;
    }

    /**
     * Create and return a new gap penalties builder.
     *
     * @return a new gap penalties builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Create and return a new gap penalties with the specified penalties.
     *
     * @param match match penalty
     * @param replace replace penalty
     * @param insert insert penalty
     * @param delete delete penalty
     * @param extend extend penalty
     * @return a new gap penalties with the specified penalties
     */
    public static GapPenalties create(final int match,
                                      final int replace,
                                      final int insert,
                                      final int delete,
                                      final int extend) {
        return new GapPenalties((short) match, (short) replace, (short) insert, (short) delete, (short) extend);
    }

    /**
     * Gap penalties builder.
     */
    public static final class Builder {
        private int match;
        private int replace;
        private int insert;
        private int delete;
        private int extend;

        /**
         * Return this gap penalties builder configured with the specified match penalty.
         *
         * @param match match penalty
         * @return this gap penalties builder configured with the specified match penalty
         */
        public Builder withMatch(final int match) {
            this.match = match;
            return this;
        }

        /**
         * Return this gap penalties builder configured with the specified replace penalty.
         *
         * @param replace replace penalty
         * @return this gap penalties builder configured with the specified replace penalty
         */
        public Builder withReplace(final int replace) {
            this.replace = replace;
            return this;
        }

        /**
         * Return this gap penalties builder configured with the specified insert penalty.
         *
         * @param insert insert penalty
         * @return this gap penalties builder configured with the specified insert penalty
         */
        public Builder withInsert(final int insert) {
            this.insert = insert;
            return this;
        }

        /**
         * Return this gap penalties builder configured with the specified delete penalty.
         *
         * @param delete delete penalty
         * @return this gap penalties builder configured with the specified delete penalty
         */
        public Builder withDelete(final int delete) {
            this.delete = delete;
            return this;
        }

        /**
         * Return this gap penalties builder configured with the specified extend penalty.
         *
         * @param extend extend penalty
         * @return this gap penalties builder configured with the specified extend penalty
         */
        public Builder withExtend(final int extend) {
            this.extend = extend;
            return this;
        }

        /**
         * Create and return a new gap penalties configured from this builder.
         *
         * @return a new gap penalties configured from this builder
         */
        public GapPenalties build() {
            return create(match, replace, insert, delete, extend);
        }
    }
}
