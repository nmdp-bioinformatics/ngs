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

import java.util.BitSet;
import java.util.List;
import java.util.ArrayList;

/**
 * Class for representing formal concepts and their partial ordering.
 */
public final class Concept implements Partial<Concept> {
    private final BitSet extent, intent;

    /**
     * Construct a concept with given objects (extent) and attributes (intent).
     *
     * @param extent objects
     * @param intent attributes
     */
    public Concept(final BitSet extent, final BitSet intent) {
        this.extent = extent;
        this.intent = intent;
    }

    /**
     * Retrieve a concept's shared objects.
     *
     * @return concept's extent
     */
    public BitSet extent() {
        return extent;
    }

    /**
     * Retrieve a concept's shared attributes.
     *
     * @return concept's intent
     */
    public BitSet intent() {
        return intent;
    }

    /**
     * Decode an object list from bits.
     *
     * @param bits where each set bit represents membership in the given group
     * @param group list of all members
     * @return immutable list of members
     */
    public final static List decode(final BitSet bits, final List group) {
        List members = new ArrayList();
        for (int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            members.add(group.get(i));
        }
        return members;
    }

    /**
     * Encode a bit membership from a list of objects.
     *
     * @param members to encode
     * @param group list of all members
     * @return bits where each set bit represents membership in the 
     */
    public static BitSet encode(final List members, final List group) {
        BitSet bits = new BitSet();

        for (Object object : members) {
            int index = group.indexOf(object);
            if (index >= 0) {
                bits.flip(index);
            }
        }
        return bits;
    }

    /**
     * Determine the partial order of two concepts.
     *
     * @param that concept
     * @return enumerated partial order for this and that concept
     */
    @Override
    public Order order(final Concept that) {
        BitSet meet = (BitSet) this.intent.clone();
        meet.and(that.intent);

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

    /**
     * Determine greater-than-or-equal-to (gte) relationship of
     * two concepts.
     *
     * @param that concept
     * @return true if this concept is greater-than-or-equal-to that
     */
    public boolean gte(final Concept that) {
        return this.order(that) == Partial.Order.GREATER ||
            this.order(that) == Partial.Order.EQUAL;
    }

    /**
     * Determine less-than-or-equal-to (lte) relationship of two
     * concepts.
     *
     * @param that concept
     * @return true if this concept is less-than-or-equal-to that
     */
    public boolean lte(final Concept that) {
        return this.order(that) == Partial.Order.LESS ||
            this.order(that) == Partial.Order.EQUAL;
    }

    @Override
    public String toString() {
        return intent.toString();
    }
}
