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
import com.tinkerpop.blueprints.Graph;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.dishevelled.bitset.AbstractBitSet;
import static org.nmdp.ngs.fca.Poset.MAGIC;

/**
 * CrossTable.
 * @param <G> object type
 * @param <M> attribute type
 */
public final class CrossTable implements Iterable<CrossTable.Row> {
    private long nrow, ncol;
    private List<Row> table;


    
    public final static class Row {
        public final long index;
        public final MutableBitSet intent;
        
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
        
        @Override
        public boolean equals(final Object right) {
            if (!(right instanceof Row)) {
                return false;
            }

            if (right == this) {
               return true;
            }

            Row row = (Row) right;
            return this.index == row.index &&
                   this.intent.equals(row.intent);
        }
    }
        
    public final static class Decomposition implements Map.Entry<CrossTable, CrossTable> {
        CrossTable key, value;

        @Override
        public CrossTable getKey() {
            return key;
        }

        @Override
        public CrossTable getValue() {
            return value;
        }

        @Override
        public CrossTable setValue(CrossTable value) {
            this.value = value;
            return this.value;
        }
    }
    
    public CrossTable() {
        nrow = ncol = 0;
        table = new ArrayList<>();
    }
    
    public CrossTable(final CrossTable that) {
        this();
        
        for(long i = 0; i < that.nrow; i++) {
            this.addRow(new MutableBitSet().or(that.getRow((int) i).intent));
        }
    }
    
    public CrossTable(final CrossTable that, long plus) {
        this(that);
        ncol += plus;
    }
    
    public static <G extends Relatable, M extends Relatable> CrossTable fromContext(final Context<G, M> context) {
        checkNotNull(context);
        
        CrossTable table = new CrossTable();
        
        List<G> objects = context.getObjects();
        List<M> attributes = context.getAttributes();
        BinaryRelation relation = context.getRelation();

        for(int i = 0; i < objects.size(); i++) {
            MutableBitSet bits = new MutableBitSet(attributes.size());
            for(int j = 0; j < attributes.size(); j++) {
                if(relation.apply(objects.get((int) i), attributes.get((int) j))) {
                    bits.flip(j);
                }
            }
            table.addRow(bits);
        }
        
        return table;
    }
    
    public long getNumberOfRows() {
        return nrow;
    }
    
    public long getNumberOfColumns() {
        return ncol;
    }
    
    public void addRow(final MutableBitSet bits) {
        long n = bits.prevSetBit(bits.capacity());
        
        if(ncol <= n) {
            ncol = n + 1;
        }

        table.add(new Row((int) nrow++, bits));
    }
  
    public Row getRow(int index) {
        checkArgument(Range.closedOpen(0, (int) nrow).contains(index));
        return table.get(index);
    }
    
    public static CrossTable complement(final CrossTable that) {
        checkNotNull(that);
        CrossTable complement = new CrossTable();
        
        for(int i = 0; i < that.getNumberOfRows(); i++) {
            MutableBitSet ones = new MutableBitSet(that.ncol);
            ones.set(0, that.ncol);
            complement.addRow(new MutableBitSet().andNot(ones, that.getRow(i).intent));
        }
        
        return complement;
    }
    
    public static CrossTable horizontalSum(final CrossTable that,
                                           final CrossTable other) {
        checkNotNull(that, other);
        CrossTable sum = new CrossTable(that, other.getNumberOfColumns());
        
        for(int i = 0; i < other.nrow; i++) {
            MutableBitSet bits = new MutableBitSet(that.ncol + other.ncol);
            MutableBitSet intent = other.getRow(i).intent;
            for(long j = intent.nextSetBit(0); j >= 0;
                     j = intent.nextSetBit(j + 1)) {
                bits.set(that.ncol + j);
            }
            sum.addRow(bits);
        }
        
        return sum;
    }
    
    public static CrossTable verticalSum(final CrossTable that,
                                         final CrossTable other) {
        CrossTable sum = CrossTable.horizontalSum(that, other);
        
        for(int i = 0; i < that.nrow; i++) {
            sum.getRow(i).intent.set(that.ncol, that.ncol + other.ncol);
        }
        
        return sum;
    }
    
    public static CrossTable directProduct(final CrossTable that,
                                           final CrossTable other) {
        CrossTable product = CrossTable.verticalSum(that, other);

        for(long i = that.nrow; i < product.nrow; i++) {
            product.getRow((int) i).intent.set(0, that.ncol);
        }
        
        return product;
    }
    
    public Decomposition parallelDecomposition() {
        return new Decomposition();
    }
    
    public Decomposition seriesDecomposition() {
        return new Decomposition();
    }
    
    public ConceptLattice asConceptLattice(final Graph graph) {
        ConceptLattice lattice = new ConceptLattice(graph, ncol);
        
        Iterator it = this.iterator();
        while(it.hasNext()) {
            CrossTable.Row row = (CrossTable.Row) it.next();
            lattice.insert(row.asConcept(ncol));
        }
        
        return lattice;
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
    
    @Override
    public boolean equals(final Object right) {
        if (!(right instanceof CrossTable)) {
            return false;
        }

        if (right == this) {
           return true;
        }

        CrossTable table = (CrossTable) right;
        return this.nrow == table.nrow &&
               this.ncol == table.ncol &&
               this.table.equals(table.table);
    }
}