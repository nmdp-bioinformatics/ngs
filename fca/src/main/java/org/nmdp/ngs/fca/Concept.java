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

import com.google.common.base.Objects;

import org.dishevelled.bitset.MutableBitSet;

/**
 * Formal concept.
 */
public final class Concept extends PartiallyOrdered<Concept> {
    private final MutableBitSet intent, extent;

    /**
     * Construct a concept with given objects (extent) and attributes (intent).
     * @param extent objects
     * @param intent attributes
     */
    public Concept(final MutableBitSet extent, final MutableBitSet intent) {
        this.extent = extent;
        this.intent = intent;
    }
    
    private MutableBitSet and(final Concept that) {
        return new MutableBitSet().or(this.intent).and(that.intent);
    }
    
    private MutableBitSet or(final Concept that) {
        return new MutableBitSet().or(this.extent).or(that.extent);
    }

    /**
     * Get the bitset representation of the concept's objects.
     * @return extent
     */
    public MutableBitSet extent() {
        return extent;
    }

    /**
     * Get the bitset representation of the concept's attributes.
     * @return intent
     */
    public MutableBitSet intent() {
        return intent;
    }
    
    @Override
    public boolean isLessThan(final Concept that) {
        return super.isLessThan(that);
    }
    
    @Override
    public boolean isLessOrEqualTo(final Concept that) {
        return this.intent.equals(this.and(that));
    }
    
    
    @Override
    public boolean isGreaterThan(final Concept that) {
        return this.isGreaterOrEqualTo(that) && !this.equals(that);
    }
    
    
    @Override
    public boolean isGreaterOrEqualTo(final Concept that) {
        return that.intent.equals(this.and(that));
    }
    
    @Override
    public boolean equals(final Object right) {
        if (!(right instanceof Concept)) {
            return false;
        }

        if (right == this) {
           return true;
        }

        Concept concept = (Concept) right;
        return concept.extent.equals(this.extent) &&
               concept.intent.equals(this.intent);
    }

    /**
     * Get the hash code.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(extent, intent);
    }
    
    /**
     * Get the string representation. This is the extent and intent as sets of
     * set bits or, equivalently, indexes into corresponding context objects and
     * attributes, respectively.
     * @return concept as a string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Context.indexes(extent)).append(Context.indexes(intent));
        return sb.toString();
    }

    @Override
    public Concept intersect(final Concept that) {
        return new Concept(new MutableBitSet(), this.and(that));
    }

    @Override
    public Concept union(final Concept that) {
        return new Concept(this.or(that), this.intent());
    }

    @Override
    public double measure() {
        return extent.cardinality();
    }
}
