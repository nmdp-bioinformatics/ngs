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
import java.util.List;
import org.dishevelled.bitset.MutableBitSet;

public class Context<G extends Comparable, M extends Comparable> {
    protected List<G> objects;
    protected List<M> attributes;
    
    private List decode(final MutableBitSet bits, final List group) {
        List members = new ArrayList();

        for (long i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            members.add(group.get((int) i));
        }
        return members;
    }
    
    private MutableBitSet encode(final List members, final List group) {
        MutableBitSet bits = new MutableBitSet();

        for (Object object : members) {
            int index = group.indexOf(object);
            if (index >= 0) {
                bits.flip(index);
            }
        }
        return bits;
    }
    
    public List decodeExtent(final Concept concept) {
        return decode(concept.extent(), objects);
    }
    
    public List decodeIntent(final Concept concept) {
        return decode(concept.intent(), attributes);
    }

}