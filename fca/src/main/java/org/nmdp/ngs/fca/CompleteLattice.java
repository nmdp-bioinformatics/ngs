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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

/**
 * Complete lattice.
 *
 * @param <T> type
 * @see <a href="https://en.wikipedia.org/wiki/Complete_lattice"> complete
 * lattice </a>
 */
public abstract class CompleteLattice<T extends PartiallyOrdered> implements Lattice<T> {
    protected Graph lattice;
    protected Vertex bottom;
    protected Vertex top;

    //protected Partial.Order.Direction direction;
    protected int color;
    protected int size;
    protected int order;

    protected static final String LABEL = "label";
    protected static final String COLOR = "color";

    protected CompleteLattice(final Graph lattice) {
        checkNotNull(lattice);
        this.lattice = lattice;
        top = lattice.addVertex(null); // TODO: pass in a first property -- easy for IntervalLattice, requires some work for ConceptLattice
        bottom = top;
        color = 0;
        order = 0;
        size = 1;
    }

    protected boolean filter(final Vertex source, final Vertex target) {
        T sourceConcept = source.getProperty(LABEL);
        T targetConcept = target.getProperty(LABEL);
        return filter(sourceConcept, targetConcept);
    }

    private boolean filter(final Vertex source, final T right) {
        if(source.getProperty(LABEL) == null) {
            return false;
        }
        
        T sourceConcept = source.getProperty(LABEL);
        return filter(sourceConcept, right);
    }

    private boolean filter(final T left, final Vertex target) {
        T targetConcept = target.getProperty(LABEL);
        return filter(left, targetConcept);
    }

    private boolean filter(final T right, final T left) {
        return right.isGreaterOrEqualTo(left);
    }

    /**
     * Find the supremum or least upper bound.
     *
     * @param proposed proposed
     * @param generator starting point and tracer
     * @return vertex whose label-concept represents the supremum
     */
    // todo:  modifies generator parameter
    protected Vertex supremum(final T proposed, Vertex generator) {
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
     * @param proposed proposed
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
        for (Edge edge : generator.getEdges(Direction.BOTH)) {
            Vertex target = edge.getVertex(Direction.OUT);
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
    public final T bottom() {
        return bottom.getProperty(LABEL);
    }

    @Override
    public final T top() {
        return top.getProperty(LABEL);
    }

    @Override
    public T join(final T left, final T right) {
        return supremum((T) left.union(right), top).getProperty(LABEL);
    }

    @Override
    public T meet(final T left, final T right) {
        return supremum((T) left.intersect(right), top).getProperty(LABEL);
    }

    @Override
    public double measure(final T left, final T right) {
        //Concept concept = leastUpperBound(right);
        //Concept meet = left.meet(leastUpperBound(left), concept);
        // T meet = this.meet(left, right);
        return (double) join(left, right).measure() / meet(right, top()).measure();
    }
}
