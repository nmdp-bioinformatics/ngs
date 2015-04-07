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
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.dishevelled.bitset.MutableBitSet;

/**
 * Formal concept.
 */
public final class Concept implements Partial<Concept> {
    private final MutableBitSet extent;
    private final MutableBitSet intent;

    /**
     * Construct a concept with given objects (extent) and attributes (intent).
     *
     * @param extent objects
     * @param intent attributes
     */
    public Concept(final MutableBitSet extent, final MutableBitSet intent) {
        this.extent = extent;
        this.intent = intent;
    }

    /**
     * Retrieve the bitset representation of the concept's objects.
     *
     * @return extent
     */
    public MutableBitSet extent() {
        return extent;
    }

    /**
     * Retrieve the bitset representation of the concept's attributes.
     *
     * @return intent
     */
    public MutableBitSet intent() {
        return intent;
    }

    /**
     * Decode an object list from its bit membership.
     *
     * @param bits where each set bit represents membership in the given group
     * @param group list of all members
     * @return immutable list of members
     */
    // todo:  lists should be typed
    public static List decode(final MutableBitSet bits, final List group) {
        List members = new ArrayList();

        for (long i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            members.add(group.get((int) i));
        }
        return members;
    }

    /**
     * Encode bit membership from a list of objects.
     *
     * @param members to encode
     * @param group list of all members
     * @return bits where each set bit represents membership in the group
     */
    public static MutableBitSet encode(final List members, final List group) {
        MutableBitSet bits = new MutableBitSet();

        for (Object object : members) {
            int index = group.indexOf(object);
            if (index >= 0) {
                bits.flip(index);
            }
        }
        return bits;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder class for Concept.
     */
    public static final class Builder<G extends Comparable, M extends Comparable> {
        private MutableBitSet extent, intent;
        
        public Builder() {
          extent = new MutableBitSet();
          intent = new MutableBitSet();
        }
        
        public Builder withObjects(final List<G> chosen, final List<G> from) {
            extent = encode(chosen, from);
            return this;
        }
      
        public Builder withAttributes(final List<M> chosen, final List<G> from) {
            intent = encode(chosen, from);
            return this;
        }
        
        public Concept build() {
            return new Concept(extent, intent);
        }
    }

    /**
     * Define the partial ordering of two concepts.
     *
     * @param that that concept
     * @return partial ordering of this and that concept
     */
    @Override
    public Order relation(final Concept that) {
        MutableBitSet meet = (MutableBitSet) new MutableBitSet().or(this.intent).and(that.intent);

        if (this.intent.equals(that.intent)) {
            return Partial.Order.EQUAL;
        }
        if (this.intent.equals(meet)) {
            return Partial.Order.LESS;
        }
        if (that.intent.equals(meet)) {
            return Partial.Order.GREATER;
        }
        return Partial.Order.NONCOMPARABLE;
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
        return concept.extent == this.extent && concept.intent == this.intent;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(extent, intent);
    }

    @Override
    public String toString() {
        return intent.toString();
    }

    @Override
    public Concept intersect(final Concept that) {
        MutableBitSet and = (MutableBitSet) new MutableBitSet().or(this.intent).and(that.intent);
        return new Concept(new MutableBitSet(), and);
    }
    
    @Override
    public Concept union(final Concept that) {
        MutableBitSet or = (MutableBitSet) new MutableBitSet().or(this.extent).or(that.extent);
        return new Concept(or, this.intent());   
    }
    
    @Override
    public double measure() {
        return extent.cardinality();
    }
}
