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

import java.util.List;
import java.util.Map;

public final class ArbitraryBinaryRelation<G extends Relatable, M extends Relatable> {
    private final List<Entry<G, M>> list;
    
    public static class Entry<G, M> implements Map.Entry<G, M> {
        private G key;
        private M value;

        public Entry(final G key, final M value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public G getKey() {
            return key;
        }

        @Override
        public M getValue() {
            return value;
        }

        @Override
        public M setValue(final M value) {
            this.value = value;
            return this.value;
        }
        
        @Override
        public boolean equals(final Object right) {
            if (!(right instanceof Entry)) {
                return false;
            }

            if (right == this) {
               return true;
            }

            Entry entry = (Entry) right;
            return entry.key.equals(this.key) &&
                   entry.value.equals(this.value);

        }
        
    }
    
    public boolean apply(final G left, final M right) {
        return list.contains(new Entry<G, M>(left, right));
    }
    
    public ArbitraryBinaryRelation(final List<Entry<G, M>> list) {
        this.list = list;
    }

    
}
 