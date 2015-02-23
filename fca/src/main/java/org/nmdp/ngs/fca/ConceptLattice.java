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
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
/**
 * A class for concept lattices. Implementation is a directed acyclic graph
 * (DAG) that results from topologically sorting concepts according to their
 * partial order.
 *
 * @param <O> object type
 * @param <A> attribute type
 */
public final class ConceptLattice<O, A> extends ConnectedGraph<Concept, Long> {
    private final List<O> objects;
    private final List<A> attributes;
    private Vertex<Concept, Long> bottom;

    /**
     * Construct a concept lattice from a list of attributes.
     *
     * @param attributes
     */
    public ConceptLattice(final List<A> attributes) {
        super(false);
        objects = new ArrayList<>();
        this.attributes = attributes;
        BitSet ones = new BitSet(this.attributes.size());
        ones.set(0, this.attributes.size());
        super.putVertex(new Concept(new BitSet(), ones), Long.MIN_VALUE);
        bottom = root;
    }

    /**
     * Find the supremum or least upper bound.
     *
     * @param intent to find
     * @param generator starting point and tracer
     * @return vertex whose label-concept represents the supremum
     */
    private Vertex supremum(final BitSet intent, Vertex<Concept, Long> generator) {
        boolean max = true;
        while (max) {
            max = false;
            Iterator<Vertex.Edge<Long>> edges = generator.iterator();

            while (edges.hasNext()) {
                Vertex<Concept, Long> target = edges.next().target();
                if (target.getLabel().gte(generator.getLabel())) {
                    continue;
                }

                Concept proposed = new Concept(new BitSet(), intent);
                if (target.getLabel().gte(proposed)) {
                    generator = target; // prevents parameter from being final
                    max = true;
                    break;
                }
            }
        }
        return generator;
    }

    /**
     * Add concepts dynamically. Algorithm originally described by van
     * der Merwe, Obiedkov, and Kourie. AddIntent: a new incremental algorithm for
     * constructing concept lattices. Lecture Notes in Computer Science Volume
     * 2961, 2004, pp 372-385.
     */
    private Vertex addIntent(final BitSet intent, Vertex<Concept, Long> generator) {
        generator = supremum(intent, generator); // prevents parameter from being final
        Concept proposed = new Concept(new BitSet(), intent);

        if (generator.getLabel().gte(proposed) && proposed.gte(generator.getLabel())) {
            return generator;
        }

        List parents = new ArrayList<>(); 
        Iterator<Vertex.Edge<Long>> edges = generator.iterator();
        while (edges.hasNext()) {
            Vertex<Concept, Long> target = edges.next().target();
            if (target.getLabel().gte(generator.getLabel())) {
                continue;
            }
		
            Vertex<Concept, Long> candidate = target;
            if (!(candidate.getLabel().gte(proposed)) && !(proposed.gte(candidate.getLabel()))) {        
                BitSet meet = (BitSet) candidate.getLabel().intent().clone();
                meet.and(intent);
                candidate = addIntent(meet, candidate);
            }

            boolean add = true;
            List doomed = new ArrayList<>();
            for (Iterator it = parents.iterator(); it.hasNext();) {
                Vertex<Concept, Long> parent =  (Vertex<Concept, Long>) it.next();

                if (parent.getLabel().gte(candidate.getLabel())) {
                    add = false;
                    break;
                }
                else if(candidate.getLabel().gte(parent.getLabel())) {
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

        proposed.extent().or(generator.getLabel().extent());
        Vertex<Concept, Long> child = super.putVertex(generator, proposed, (long) 0);
        bottom = bottom.getLabel().gte(child.getLabel()) ? child : bottom;

        for (Iterator it = parents.iterator(); it.hasNext();) {
            Vertex<Concept, Long> parent = (Vertex<Concept, Long>) it.next();
            if (!parent.equals(generator)) {
                super.deleteEdge(parent, generator);
                super.putEdge(parent, child, (long) 0);
            }
        }

        return child;
    }

    /**
     * Retrieve the top vertex as a graph iterator.
     *
     * @param pruner
     * @return an iterator to the top (root) vertex
     */
    public GraphIterator top(final Pruner pruner) { 
        return super.iterator(pruner);
    }

    /**
     * Retrieve the bottom vertex as a graph iterator.
     *
     * @param pruner
     * @return an iterator to the bottom vertex
     */
    public GraphIterator bottom(final Pruner pruner) { 
        return new GraphIterator(++color, pruner, bottom);
    }

    /**
     * Retrieve all objects of this context (concept lattice).
     *
     * @return list of objects 
     */
    public List<O> getObjects() {
        return objects;
    }

    /**
     * Retrieve all attributes of this context (concept lattice).
     *
     * @return list of attributes
     */
    public List<A> getAttributes() {
        return attributes;
    }

    /**
     * Insert an object and its attributes dynamically into the lattice.
     * Duplicate objects with identical attributes are okay and will be treated as
     * distinct observations.
     *
     * @param object
     * @param attributes list
     */
    public void insert(final O object, final List<A> attributes) {
        objects.add(object);
        BitSet intent = Concept.encode(attributes, this.attributes);
        Pruner adder = new ExtentAdder.Builder().withObject(object).withObjects(objects).build();
        GraphIterator inserted = new GraphIterator(++color, adder, addIntent(intent, root));

        while (inserted.hasNext()) {
            inserted.next();
        }
    }

    /**
     * Find the concept with given attributes. This is a low-level
     * retrieval method that enables iteration over the entire lattice starting at
     * the queried vertex; however, access to the vertex label (concept) and its
     * methods (intent and extent) require some tedious dereferencing. If your
     * application doesn't require further query use the leastUpperBound method
     * instead.
     *
     * @param query attributes
     * @return an iterator to the found vertex
     */
    public GraphIterator queryAttributes(final List<A> query) {
        BitSet bits = Concept.encode(query, attributes);
        return new GraphIterator(++color, new Pruner(), supremum(bits, root));
    }

    /**
     * Find the concept with given attributes using built-in dereferencing.
     *
     * @param query attributes
     * @return the found concept
     */
    public final Concept leastUpperBound(final List<A> query) {
        return ((Vertex<Concept, Long>) queryAttributes(query).next()).getLabel();
    }

    public GraphIterator queryAttributes(final List<A> p, final List<A> q) {
        BitSet join = Concept.encode(p, attributes);
        BitSet bits = Concept.encode(q, attributes);
        join.or(bits);
        return new GraphIterator(++color, new Pruner(), supremum(join, root));
    }
  
    public final Concept leastUpperBound(final List<A> p, final List<A> q) {
        return ((Vertex<Concept, Long>) queryAttributes(p, q).next()).getLabel();
    }

    /**
     * Retrieve the number of objects that have the query attributes.
     *
     * @param query attributes
     * @return the number of objects with the given attributes
     */
    public int observed(final List<A> query) {
        return leastUpperBound(query).extent().cardinality();
    }

    /**
     * Retrieve the number of objects that have the combined query attributes.
     *
     * @param p first set of query attributes
     * @param q second set of query attributes
     * @return the number of objects with the query attributes
     */
    public int observed(final List<A> p, final List<A> q) {
        return leastUpperBound(p, q).extent().cardinality();
    }

    /**
     * Calculate the marginal frequency of the given attributes.
     *
     * @param query attributes
     * @return marginal frequency of observing the given attributes. Calculation
     *    is the number of times the given attributes are observed divided by the
     *    total number of observations (objects)
     */
    public double marginal(final List<A> query) {
        return (double) observed(query) / objects.size();
    }

    /**
     * Calculate the marginal frequency given two sets of attributes.
     *
     * @param p first set of query attributes
     * @param q second set of query attributes
     * @return the joint frequency of observing both sets of attributes.
     *    Calculation is the number of times the given attributes are observed
     *    together divided by the total number of observations (objects)
     */
    public double joint(final List<A> p, final List<A> q) {
        return (double) observed(p, q) / objects.size();
    }

    /**
     * Calculate the conditional frequency of one attribute set given another.
     *
     * @param p first set of query attributes
     * @param q second set of query attributes
     * @return the conditional frequency. Calculation is the joint divided by the prior
     */
    public double conditional(final List<A> p, final List<A> q) {
        return joint(p, q) / marginal(q);
    }

    /**
     * Iterate over the lattice and do some work defined by pruner. If
     * Lattice.Direction is DOWN traversal happens from top to bottom.
     * If Lattice.Direction is UP traversal happens in reverse order across the
     * lattice's dual from bottom to top.
     * @param pruner 
     */
    public void go(final Pruner pruner) {
        LatticePruner filter = (LatticePruner) pruner;
        Iterator it;

        if (filter.go() == Lattice.Direction.DOWN) {
            it = top(filter);
        }
        else {
            it = bottom(filter);
        }

        while (it.hasNext()) {
            it.next();
        }
    }
  
    @Override
    public String toString() {
        Pruner writer = new LatticeWriter.Builder()
            .go(Lattice.Direction.DOWN)
            .withObjects(objects)
            .withAttributes(attributes)
            .build();

        go(writer);
        return writer.toString();
    }

    /**
     * Each vertex is labeled with a Concept.
     *
     * @return true
     */
    @Override
    public boolean isLabeled() {
        return true;
    }

    /**
     * Each edge is weighted with Long.
     *
     * @return true
     */
    @Override
    public boolean isWeighted() {
        return true;
    }

    /**
     * Edges are directed.
     *
     * @return true
     */
    @Override
    public boolean isDirected() {
        return true;
    }

    /**
     * Multiple edges between vertexes are not present.
     *
     * @return false
     */
    @Override
    public boolean isMulti() {
        return false;
    }

    /**
     * Loops are not present.
     *
     * @return false
     */
    @Override
    public boolean isComplex() {
        return false;
    }

    /**
     * Cycles are not present.
     *
     * @return false
   */
    @Override
    public boolean isCyclic() {
        return false;
    }
}
