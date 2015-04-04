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

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import java.util.ArrayList;
import java.util.BitSet;

import org.dishevelled.bitset.MutableBitSet;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract lattice.
 *
 * @param <T> type
 */
public abstract class AbstractLattice<T extends Partial> implements Lattice<T> {
    protected Graph lattice;
    protected Vertex bottom;
    protected Vertex top;

    protected Partial.Order.Direction direction;
    protected int color;
    protected int size;
    protected int order;

    protected static final String LABEL = "label";

    protected boolean filter(final Vertex source, final Vertex target) {
        T sourceConcept = source.getProperty(LABEL);
        T targetConcept = target.getProperty(LABEL);
        return filter(sourceConcept, targetConcept);
    }

    private boolean filter(final Vertex source, final T right) {
        T sourceConcept = source.getProperty(LABEL);
        return filter(sourceConcept, right);
    }

    private boolean filter(final T left, final Vertex target) {
        T targetConcept = target.getProperty(LABEL);
        return filter(left, targetConcept);
    }

    private boolean filter(final T right, final T left) {
        return right.relation(left).greaterOrEqual();
    }

    /**
     * Find the supremum or least upper bound.
     *
     * @param intent to find
     * @param generator starting point and tracer
     * @return vertex whose label-concept represents the supremum
     */
    // todo:  modifies generator parameter
    private Vertex supremum(final T proposed, Vertex generator) {
        boolean max = true;
        while (max) {
            max = false;
            for (Edge edge : generator.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);

                if (filter(target, generator)) {
                    continue;
                }
                //Concept proposed = new Concept(new MutableBitSet(), proposed.intent);

                if (filter(target, proposed)) {
                    generator = target;
                    max = true;
                    break;
                }
            }
        }
        return generator;
    }

    private Vertex add(final T label) {
        Vertex child = lattice.addVertex(null);
        child.setProperty("label", label);
        child.setProperty("color", color);
        ++size;
        return child;
    }

    private Edge addUndirectedEdge(final Vertex source, final Vertex target, final String weight) {
        T sourceConcept = source.getProperty("label");
        T targetConcept = target.getProperty("label");
        Partial.Order.Direction direction = this.direction;

        if (targetConcept.relation(sourceConcept).gte()) {
            direction = Partial.Order.Direction.REVERSE;
        }

        lattice.addEdge(null, source, target, weight);
        Edge edge = lattice.addEdge(null, target, source, weight);
        ++order;
        return edge;
    }

    private void removeUndirectedEdge(final Vertex source, final Vertex target) {
        for (Edge edge : source.getEdges(Direction.BOTH)) {
            if (edge.getVertex(Direction.OUT).equals(target)) {
                lattice.removeEdge(edge);
                break;
            }

            if (edge.getVertex(Direction.IN).equals(target)) {
                lattice.removeEdge(edge);
                break;
            }
        }
        --order;
    }

    /**
     * Find the supremum or least upper bound.
     *
     * @param intent to find
     * @param generator starting point and tracer
     * @return vertex whose label-concept represents the supremum
     */
    // todo:  modifies generator parameter
    protected Vertex addIntent(T proposed, Vertex generator) {
        generator = supremum(proposed, generator);
        

        if (filter(generator, proposed) && filter(proposed, generator)) {
            return generator;
        }
        List parents = new ArrayList<>();
        for (Vertex target : lattice.getVertices()) {
            if (filter(target, generator)) {
                continue;
            }

            Vertex candidate = target;
            if (!filter(target, proposed) && !filter(proposed, target)) {
                T targetConcept = target.getProperty(LABEL);
                // MutableBitSet meet = (MutableBitSet) new MutableBitSet().or(targetConcept.intent()).and(proposed.intent());
                T intersect = (T) targetConcept.intersect(proposed);
                candidate = addIntent(intersect, candidate);
            }

            boolean add = true;
            List doomed = new ArrayList<>();
            for (Iterator it = parents.iterator(); it.hasNext();) {
                Vertex parent = (Vertex) it.next();

                if (filter(parent, candidate)) {
                    add = false;
                    break;
                }
                else if (filter(candidate, parent)) {
                    doomed.add(parent);
                }
            }

            for (Iterator it = doomed.iterator(); it.hasNext();) {
                Vertex vertex = (Vertex) it.next();
                parents.remove(vertex);
            }

            if (add) {
                parents.add(candidate);
            }
        }

        T generatorConcept = generator.getProperty(LABEL);
        
        Vertex child = add((T) proposed.union(generatorConcept));
        addUndirectedEdge(generator, child, "");
        // proposed.extent().or(generatorConcept.extent());
        bottom = filter(bottom, proposed) ? child : bottom;

        for (Iterator it = parents.iterator(); it.hasNext();) {
            Vertex parent = (Vertex) it.next();
            if (!parent.equals(generator)) {
                removeUndirectedEdge(parent, generator);
                addUndirectedEdge(parent, child, "");
            }
        }
        return child;
    }

    public int size() {
        return size;
    }

    public int order() {
        return order;
    }



    @Override
    public T bottom() {
        return bottom.getProperty(LABEL);
    }

    @Override
    public T top() {
        return top.getProperty(LABEL);
    }
    
    /*
    @Override
    public final T join(final T left, final T right) {
        MutableBitSet bits = (MutableBitSet) new MutableBitSet().or(leftConcept.intent()).or(rightConcept.intent());
        Concept query = new Concept(new MutableBitSet(), bits);
        return supremum(query, top).getProperty(LABEL);
    }
    */

    @Override
    public final T meet(final T left, final T right) {
        // MutableBitSet bits = (MutableBitSet) new MutableBitSet().or(left.intent()).and(right.intent());
        return supremum((T) left.intersect(right), top).getProperty(LABEL);
    }
    
    /*
    public final Concept leastUpperBound(final List left, final List right) {
        return queryAttributes(left, right).getProperty("label");
    }
    */
    /**
     * Retrieve the number of objects that have the query attributes.
     *
     * @param query attributes
     * @return the number of objects with the given attributes
     */
    /*
    public long support(final List query) {
        return leastUpperBound(query).extent().cardinality();
    }
    */

    /**
     * Determine the support given two sets of query attributes.
     *
     * @param left set of attributes
     * @param right set of attributes
     * @return the extent cardinality for the join of the attribute
     */
    /*
    public long support(final List left, final List right) {
        return leastUpperBound(left, right).extent().cardinality();
    }
    */

    /**
     * Calculate the marginal frequency of the given attributes.
     *
     * @param query attributes
     * @return marginal frequency of observing the given attributes. Calculation
     *    is the number of times the given attributes are observed divided by the
     *    total number of observations (objects)
     */
    /*
    @Override
    public double marginal(final List query) {
        return (double) support(query) / objects.size();
    }
    */

    /**
     * Calculate the marginal frequency given two sets of attributes.
     *
     * @param left set of query attributes
     * @param right set of query attributes
     * @return the joint frequency of observing both sets of attributes.
     *    Calculation is the number of times the given attributes are observed
     *    together divided by the total number of observations (objects)
     */
    /*
    public double joint(final List left, final List right) {
        return (double) support(left, right) / objects.size();
    }
    */

    /**
     * Calculate the conditional frequency of one attribute set given another.
     *
     * @param left set of query attributes
     * @param right set of query attributes
     * @return the conditional frequency. Calculation is the joint divided by the prior
     */
    /*
    @Override
    public double conditional(final List left, final List right) {
        Concept concept = leastUpperBound(right);
        Concept meet = meet(leastUpperBound(left), concept);
        return (double) meet.extent().cardinality() / concept.extent().cardinality();
    }
    */
}
