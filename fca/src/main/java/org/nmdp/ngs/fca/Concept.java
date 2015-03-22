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

import java.util.ArrayList;
import java.util.BitSet;
import org.dishevelled.bitset.MutableBitSet;
import java.util.List;

/**
 * Formal concepts and their partial ordering.
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

    /**
     * Define the partial ordering of two concepts.
     *
     * @param that that concept
     * @return partial ordering of this and that concept
     */
    @Override
    public Order ordering(final Concept that) {
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
    public String toString() {
        return intent.toString();
    }

    @Override
    public Concept intersect(Concept that) {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
