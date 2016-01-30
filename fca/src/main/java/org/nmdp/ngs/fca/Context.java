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

import static com.google.common.base.Preconditions.checkNotNull;
import com.tinkerpop.blueprints.Graph;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dishevelled.bitset.MutableBitSet;

/**
 * Formal context. A context is a triple (G, M, I) of objects, attributes, and
 * their corresponding relation, respectively. In the usual way, gIm means that
 * object g has attribute m. Slightly more descriptive (but with a heavier
 * footprint), a context can be represented as a cross table of rows
 * (attributes) and columns (objects). Table elements are true if the ith object
 * has attribute j. More descriptive still (but with the heaviest footprint) is
 * the lattice representation, a collection of all partially ordered concepts of
 * a given context.
 * @param <G> the type of objects
 * @param <M> the type of attributes
 */
public final class Context<G extends Relatable, M extends Relatable> {
    private final List<G> objects;
    private final List<M> attributes;
    private final BinaryRelation relation;
    
    private Context(final List<G> objects,
                    final List<M> attributes,
                    final BinaryRelation relation) {
        
        this.objects = objects;
        this.attributes = attributes;
        this.relation = relation;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder<G extends Relatable,
                                      M extends Relatable> {
        private List<G> objects;
        private List<M> attributes;
        private BinaryRelation relation;
        
        public Builder withObjects(final List<G> objects) {
            checkNotNull(objects);
            this.objects = objects;
            return this;
        }
        
        public Builder withAttributes(final List<M> attributes) {
            checkNotNull(attributes);
            this.attributes = attributes;
            return this;
        }
        
        public Builder withRelation(final BinaryRelation relation) {
            checkNotNull(relation);
            this.relation = relation;
            return this;
        }
        
        public Context build() {
            return new Context(objects, attributes, relation);
        }
    }
    
    public CrossTable asCrossTable() {
        return new CrossTable(objects, attributes, relation);
    }
    
    public ConceptLattice asConceptLattice(final Graph graph) {
        CrossTable table = this.asCrossTable();
        ConceptLattice lattice = new ConceptLattice(graph, attributes.size());
        
        Iterator it = table.iterator();
        while(it.hasNext()) {
            CrossTable.Row row = (CrossTable.Row) it.next();
            lattice.insert(row.asConcept(objects.size()));
        }
        
        return lattice;
    }

    //public Context down() {
        
    //}
    
    // public Context downset();
    
    // public Context powerset();
    
    private static List decode(final MutableBitSet bits, final List group) {
        List members = new ArrayList();

        for (long i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            members.add(group.get((int) i));
        }
        return members;
    }
    
    private static MutableBitSet encode(final List members, final List group) {
        MutableBitSet bits = new MutableBitSet();

        for (Object object : members) {
            int index = group.indexOf(object);
            if (index >= 0) {
                bits.flip(index);
            }
        }
        return bits;
    }
    
    public List<G> getObjects() {
        return objects;
    }
    
    public List<M> getAttributes() {
        return attributes;
    }
    
    public List decodeExtent(final Concept concept) {
        return decode(concept.extent(), objects);
    }
    
    public List decodeIntent(final Concept concept) {
        return decode(concept.intent(), attributes);
    }
    
    @Override
    public String toString() {

        
        StringBuilder sb = new StringBuilder();
        sb.append("{\"objects\": ").append(objects.toString())
          .append(", \"attributes\": ").append(attributes.toString())
          .append(", \"relation\": ").append(relation.toString()).append("}");
        
        return sb.toString();
    }

}