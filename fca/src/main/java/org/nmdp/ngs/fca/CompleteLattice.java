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
import java.util.ArrayList;
import java.util.Collection;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Complete Lattice.
 * @param <E> element type
 * @see <a href="https://en.wikipedia.org/wiki/Complete_lattice">complete
 * lattice </a>
 */
public abstract class CompleteLattice<E extends PartiallyOrdered> implements Lattice<E> {
    protected Graph graph;
    protected Vertex top, bottom;
    protected int color, size, order;

    protected static final String LABEL = "label";
    protected static final String COLOR = "color";

    /**
     * Construct a complete lattice assigned to a designated graph (backend).
     * @param graph assignment
     * @param top element
     */
    protected CompleteLattice(final Graph graph, PartiallyOrdered top) {
        checkNotNull(graph, top);
        this.graph = graph;
        order = 0;
        color = 0;
        
        this.top = graph.addVertex(null);
        this.top.setProperty(LABEL, top);
        this.top.setProperty(COLOR, color);
        bottom = this.top;
        size = 1;
    }
    
    public class Iterator<E extends PartiallyOrdered> implements java.util.Iterator<E> {
        private final java.util.Iterator<Vertex> vertices;

        public Iterator(final Graph graph) {
            vertices = graph.getVertices().iterator();
        }

        @Override
        public boolean hasNext() {
            return vertices.hasNext();
        }

        @Override
        public E next() {
            return vertices.next().getProperty(LABEL);
        }
        
        @Override
        public void remove() {
            
        }
    }

    protected boolean filter(final Vertex source, final Vertex target) {
        E sourceConcept = source.getProperty(LABEL);
        E targetConcept = target.getProperty(LABEL);
        return filter(sourceConcept, targetConcept);
    }

    private boolean filter(final Vertex source, final E right) {
        if(source.getProperty(LABEL) == null) {
            return false;
        }
        
        E sourceConcept = source.getProperty(LABEL);
        return filter(sourceConcept, right);
    }

    private boolean filter(final E left, final Vertex target) {
        E targetConcept = target.getProperty(LABEL);
        return filter(left, targetConcept);
    }

    private boolean filter(final E right, final E left) {
        return right.isGreaterOrEqualTo(left);
    }

    /**
     * Find the supremum or least upper bound.
     * @param proposed element
     * @param generator element
     * @return supremum vertex
     */
    protected final Vertex supremum(final E proposed, Vertex generator) {
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
    
    @Override
    public final E find(final E element) {
        return this.meet(element, this.top());
    }

    @Override
    public final boolean contains(final E element) {
        return this.find(element).equals(element);
    }

    /**
     * Test if the lattice contains all elements of the given collection.
     * @param collection of elements
     * @return true if lattice contains all elements of the given collection
     */
    @Override
    public final boolean containsAll(final Collection<? extends E> collection) {
        for(E element : collection) {
            if(!this.contains(element)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public final boolean covers(final E left, final E right) {
        Vertex found = this.supremum(left, top);

        if(found != bottom) {
            for(Edge edge : found.getEdges(Direction.IN)) {
                Vertex target = edge.getVertex(Direction.OUT);

                if(target.getProperty(LABEL).equals(right)) {
                    return right.isLessThan(left);
                }
            }
        }
        
        return false;
    }
    
    @Override
    public final Iterator iterator() {
        return new Iterator(graph);
    }

    private Vertex insert(final E label) {
        Vertex child = graph.addVertex(null);
        child.setProperty("label", label);
        child.setProperty("color", color);
        ++size;
        return child;
    }

    private Edge addUndirectedEdge(final Vertex source, final Vertex target, final String weight) {
        graph.addEdge(null, source, target, weight);
        Edge edge = graph.addEdge(null, target, source, weight);
        ++order;
        return edge;
    }

    private void removeUndirectedEdge(final Vertex source, final Vertex target) {
        for (Edge edge : source.getEdges(Direction.BOTH)) {
            if (edge.getVertex(Direction.OUT).equals(target)) {
                graph.removeEdge(edge);
                break;
            }

            if (edge.getVertex(Direction.IN).equals(target)) {
                graph.removeEdge(edge);
                break;
            }
        }
        --order;
    }

    /**
     * Add a new element dynamically to the lattice.
     * @param proposed element
     * @param generator vertex
     * @return new added vertex or generator if the proposed element already
     * exists
     */
    protected final Vertex addIntent(final E proposed, Vertex generator) {
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
                E targetElement = target.getProperty(LABEL);
                E intersect = (E) targetElement.intersect(proposed);
                candidate = addIntent(intersect, candidate);
            }

            boolean add = true;
            List doomed = new ArrayList<>();
            for (java.util.Iterator it = parents.iterator(); it.hasNext();) {
                Vertex parent = (Vertex) it.next();

                if (filter(parent, candidate)) {
                    add = false;
                    break;
                }
                else if (filter(candidate, parent)) {
                    doomed.add(parent);
                }
            }

            for (java.util.Iterator it = doomed.iterator(); it.hasNext();) {
                Vertex vertex = (Vertex) it.next();
                parents.remove(vertex);
            }

            if (add) {
                parents.add(candidate);
            }
        }

        E generatorLabel = generator.getProperty(LABEL);

        Vertex child = insert((E) proposed.union(generatorLabel));
        addUndirectedEdge(generator, child, ""); 
        
        bottom = filter(bottom, proposed) ? child : bottom;

        for (java.util.Iterator it = parents.iterator(); it.hasNext();) {
            Vertex parent = (Vertex) it.next();
            if (!parent.equals(generator)) {
                removeUndirectedEdge(parent, generator);
                addUndirectedEdge(parent, child, "");
            }
        }
        return child;
    }

    /**
     * Lattice size.
     * @return the number of vertices
     */
    @Override
    public final int size() {
        return size;
    }
    
    /**
     * The lattice order.
     * @return the number of edges
     */
    public final int order() {
        return order;
    }

    /**
     * Find the least element.
     * @return the least lattice element.
     */
    @Override
    public final E bottom() {
        return bottom.getProperty(LABEL);
    }

    /**
     * Find the greatest element.
     * @return the greatest lattice element
     */
    @Override
    public final E top() {
        return top.getProperty(LABEL);
    }

    @Override
    public E join(final E left, final E right) {
        return supremum((E) left.union(right), top).getProperty(LABEL);
    }

    @Override
    public E meet(final E left, final E right) {
        return supremum((E) left.intersect(right), top).getProperty(LABEL);
    }

    @Override
    public double measure(final E left, final E right) {
        return (double) join(left, right).measure() /
                        meet(right, top()).measure();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("digraph {\n");
       
        for (Vertex vertex : graph.getVertices()) {
            for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);
                
                E sourceElement = vertex.getProperty(LABEL);
                E targetElement = target.getProperty(LABEL);
                
                if (!sourceElement.equals(targetElement)) {
                    if (!filter(sourceElement, targetElement)) {
                        sb.append(" \"")
                          .append(sourceElement)
                          .append("\" -> \"")
                          .append(targetElement)
                          .append("\"[label=\"")
                          .append(edge.getLabel())
                          .append("\"]\n");
                    }
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    @Override
    public final boolean isEmpty() {
        return bottom == top;
    }
    
    @Override
    public final Object[] toArray() {
        return this.toArray(new Object[size]);
    }
    
    @Override
    public final <E> E[] toArray(E[] elements) {
        int i = 0;
        for(Object element : this) {
            elements[i++] = (E) element;
        }
        return elements;
    }
}
