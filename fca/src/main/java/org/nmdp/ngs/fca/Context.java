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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.dishevelled.bitset.MutableBitSet;
import org.nmdp.ngs.fca.partial.Equal;
import org.nmdp.ngs.fca.partial.LessOrEqual;
import org.nmdp.ngs.fca.partial.NotEqual;
import org.nmdp.ngs.fca.partial.NotGreaterOrEqual;

/**
 * Formal context. A context is a triple (G, M, I) of objects, attributes, and
 * their corresponding relation, respectively. In the usual way, gIm means that
 * object g has attribute m. Slightly more descriptive (but with a heavier
 * footprint), a context can be represented as a cross table of rows
 * (attributes) and columns (objects). Table elements are true if the ith object
 * has attribute j. More descriptive still (but with the heaviest footprint) is
 * the lattice representation, a set of all partially ordered concepts of a
 * given context.
 * @param <G> object type
 * @param <M> attribute type
 */
public final class Context<G extends Relatable, M extends Relatable> {
    private final List<G> objects;
    private final List<M> attributes;
    private final BinaryRelation relation;
    
    /**
     * Constructor. Use the builder pattern.
     * @param objects parameter
     * @param attributes parameter
     * @param relation parameter
     */
    private Context(final List<G> objects,
                    final List<M> attributes,
                    final BinaryRelation relation) {
        
        this.objects = objects;
        this.attributes = attributes;
        this.relation = relation;
    }
    
    /**
     * Start building.
     * @return unparameterized Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder.
     * @param <G> object type
     * @param <M> attribute type
     */
    public static final class Builder<G extends Relatable,
                                      M extends Relatable> {
        private List<G> objects;
        private List<M> attributes;
        private BinaryRelation relation;
        
        /**
         * Specify objects.
         * @param objects parameter
         * @return Builder with specified objects
         */
        public Builder withObjects(final List<G> objects) {
            checkNotNull(objects);
            this.objects = objects;
            return this;
        }
        
        /**
         * Specify attributes.
         * @param attributes parameter
         * @return Builder with specified attributes
         */
        public Builder withAttributes(final List<M> attributes) {
            checkNotNull(attributes);
            this.attributes = attributes;
            return this;
        }
        
        /**
         * Specify relation.
         * @param relation parameter
         * @return Builder with specified binary relation
         */
        public Builder withRelation(final BinaryRelation relation) {
            checkNotNull(relation);
            this.relation = relation;
            return this;
        }
        
        /**
         * Build it.
         * @return parameterized context
         */
        public Context build() {
            return new Context(objects, attributes, relation);
        }
    }
    
    /*
    public static <G, M> Context horizontalSum(final Context left,
                                               final Context right) {
        List<G> objects = new ArrayList<>(left.objects);
        objects.addAll(right.objects);
        
        List<M> attributes = new ArrayList<>(left.attributes);
        attributes.addAll(right.attributes);
        
        
        
        //Context<G, M> sum = Context.builder().withAttributes()
    }
    */
    
    /**
     * Get the cross table for this context.
     * @return cross table
     */
    public CrossTable asCrossTable() {
        return CrossTable.fromContext(this);
    }
    
    /**
     * Get the concept lattice for this context.
     * @param graph backend
     * @return concept lattice
     */
    public ConceptLattice asConceptLattice(final Graph graph) {
        ConceptLattice lattice = new ConceptLattice(graph, attributes.size());
        
        Iterator it = this.asCrossTable().iterator();
        while(it.hasNext()) {
            CrossTable.Row row = (CrossTable.Row) it.next();
            lattice.insert(row.asConcept(attributes.size()));
        }
        
        return lattice;
    }
    
    /**
     * Calculate the isomorphic down lattice.
     * @param <G> element type
     * @param lattice of elements
     * @return the down lattice of the given complete lattice
     * @see <a href="https://en.wikipedia.org/wiki/Upper_set">upper set</a>
     */
    public static <G extends Relatable<?>> Context down(final CompleteLattice lattice) {
        List<Object> elements = Arrays.asList(lattice.toArray());
        return new Context(elements, elements, new LessOrEqual());
    }
    
    /**
     * Calculate the isomorphic downset lattice.
     * @param <G> element type
     * @param lattice of elements
     * @return the downset lattice of a given complete lattice
     */
    public static <G extends Relatable<?>> Context downset(final CompleteLattice lattice) {
        List<Object> elements = Arrays.asList(lattice.toArray());
        return new Context(elements, elements, new NotGreaterOrEqual());
    }
    
    /**
     * Calculate the powerset lattice.
     * @param <G> element type
     * @param set of partially-ordered elements
     * @return the powerset lattice
     */
    public static <G extends PartiallyOrdered<?>> Context powerset(final List<G> set) {
        return new Context(set, set, new NotEqual());
    }
    
    /**
     * Calculate the antichain lattice.
     * @param <G> element type
     * @param set of partially-ordered elements
     * @return the antichain lattice
     */
    public static <G extends PartiallyOrdered<?>> Context antichain(final List<G> set) {
        return new Context(set, set, new Equal());
    }
    
    /**
     * Get the indexes of a bitset.
     * @param bits parameter
     * @return list of set bits
     */
    public static List<Long> indexes(final MutableBitSet bits) {
        List indexes = new ArrayList();
        
        for (long i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
            indexes.add(i);
        }
        
        return indexes;
    }
    
    private static List decode(final MutableBitSet bits, final List group) {
        List members = new ArrayList();

        for(Long index : Context.indexes(bits)) {
            members.add(group.get(index.intValue()));
        }
        
        return members;
    }
    
    /**
     * Get objects.
     * @return objects
     */
    public List<G> getObjects() {
        return objects;
    }
    
    /**
     * Get attributes.
     * @return attributes
     */
    public List<M> getAttributes() {
        return attributes;
    }
    
    /**
     * Get relation.
     * @return binary relation
     */
    public BinaryRelation getRelation() {
        return relation;
    }
    
    /**
     * Get the list of objects indexed by the concepts extent.
     * @param concept parameter
     * @return extent-objects
     */
    public List decodeExtent(final Concept concept) {
        return decode(concept.extent(), objects);
    }
    
    /**
     * Get the list of attributes indexed by the concept's intent.
     * @param concept parameter
     * @return intent-attributes
     */
    public List decodeIntent(final Concept concept) {
        return decode(concept.intent(), attributes);
    }
    
    /**
     * Get the string representation.
     * @return context as string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("{\"objects\": ").append(objects.toString())
          .append(", \"attributes\": ").append(attributes.toString())
          .append(", \"relation\": ").append(relation.toString()).append("}");
        
        return sb.toString();
    }
}