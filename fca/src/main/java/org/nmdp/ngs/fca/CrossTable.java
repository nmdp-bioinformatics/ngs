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

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.dishevelled.bitset.MutableBitSet;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Range;

/**
 * CrossTable.
 * @param <G> object type
 * @param <M> attribute type
 */
public final class CrossTable<G extends Relatable,
                              M extends Relatable> implements Iterable<CrossTable.Row> {
    private final List<Row> table;
    
    public final static class Row {
        public int index;
        public MutableBitSet intent;
        
        public Row(int index, final MutableBitSet intent) {
            checkNotNull(index, intent);
            
            this.index = index;
            this.intent = intent;
        }
        
        public Concept asConcept() {
            return this.asConcept(MutableBitSet.DEFAULT_NUM_BITS);
        }

        public Concept asConcept(long size) {
            MutableBitSet extent = new MutableBitSet(size);
            extent.flip(index);
            return new Concept(extent, intent);
        }
        
        @Override
        public String toString() {
            return this.asConcept().toString();
        }
    }
    
    public CrossTable(final List<G> objects,
                      final List<M> attributes,
                      final BinaryRelation relation) {
        checkNotNull(objects, attributes);
        checkNotNull(relation);
        
        table = new ArrayList<>();

        for(int i = 0; i < objects.size(); i++) {
            MutableBitSet bits = new MutableBitSet(attributes.size());
            for(int j = 0; j < attributes.size(); j++) {
                if(relation.apply(objects.get(i), attributes.get(j))) {
                    bits.flip(j);
                }
            }
            table.add(new Row(i, bits));
        }
    }
  
    public Row getRow(int index) {
        checkArgument(Range.greaterThan(0).contains(index));
        return table.get(index);
    }
    
    @Override
    public Iterator<Row> iterator() {
        return table.iterator();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        for(Row row : table) {
            sb.append(row.toString()).append("\n");
        }
        
        return sb.toString();
    }
}