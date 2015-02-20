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

import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

/**
 * A class for vertexes.
 *
 * @param <L> label type
 * @param <W> weight type
 */
public class Vertex<L, W extends Comparable> implements Iterable<Vertex.Edge<W>> {
    protected final L label;
    protected final int id, hashcode;
    protected int color;
    protected long indegree;
    protected long outdegree;
    private List<Edge<W>> edges;

    /**
     * A class for edges.
     *
     * @param <W> weight type
     */
    public static class Edge<W extends Comparable> implements Comparable<Vertex.Edge<W>> {
        private Vertex target;
        private W weight;

        /**
         * Construct a new edge.
         *
         * @param target vertex
         * @param weight of the edge
         */
        Edge(final Vertex target, final W weight) { 
            this.target = target;
            this.weight = weight;
        }

        /**
         * Retrieve the target vertex.
         *
         * @return target vertex
         */
        Vertex target() {
            return target;
        }

        /**
         * Set the edge weight.
         *
         * @param weight of the edge
         */
        public void setWeight(final W weight) {
            this.weight = weight;
        }

        /**
         * Retrieve the edge weight.
         *
         * @return edge weight
         */
        public W weight() {
            return weight;
        }

        @Override
        public String toString() {
            return "{ target : \"" + target + "\", weight : \"" + weight + "\" }";
        }

        /**
         * Determine edge equivalence.
         *
         * @param that
         * @return true if this edge equals that
         */
        @Override
        public boolean equals(final Object that) {
            if (target == ((Edge<W>) that).target) {
                return true;
            }

            if (!(that instanceof Edge)) {
                return false;
            }

            // TODO: include target vertex equivalence
            Edge edge = (Edge) that;
            return Objects.equals(weight, edge.weight());
        }

        /**
         * Compare edges by weight.
         *
         * @param that edge
         * @return integer code representing the result of comparison
         */
        @Override
        public int compareTo(final Edge<W> that) {
            return weight.compareTo(that.weight());
        }
    }

    /**
     * Construct a new singleton vertex.
     *
     * @param id
     * @param label 
     */
    public Vertex(final int id, final L label) {
        this.id = id;
        this.color = 0;
        this.label = label;
        this.edges = new ArrayList<Edge<W>>();
        hashcode = Objects.hash(id, label);
    }

    /**
     * Retrieve the vertex id.
     *
     * @return vertex id
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieve the vertex color.
     *
     * @return vertex color
     */
    public int getColor() {
        return color;
    }

    /**
     * Set the vertex color
     *
     * @param color of vertex
     */
    public void setColor(final int color) {
        this.color = color;
    }

    /**
     * Retrieve the vertex label.
     *
     * @return vertex label
     */
    public L getLabel() {
        return label;
    }

    /**
     * Retrieve the in-degree.
     *
     * @return number of in-edges
     */
    public long getInDegree() {
        return indegree;
    }

    /**
     * Retrieve the out-degree.
     *
     * @return number of out-edges
     */
    public long getOutDegree() {
        return outdegree;
    }

    /**
     * Determine if vertex is empty.
     *
     * @return true if the vertex has no out-edges.
     */
    public boolean isEmpty() {
        return outdegree == 0;
    }

    /**
     * Determine if vertex is a singleton.
     *
     * @return  true if the vertex has no in- or out-edges
     */
    public boolean isSingleton() {
        return indegree == 0 && isEmpty();
    }

    /**
     * Convert this vertex to its string representation
     *
     * @return vertex label
     */
    @Override
    public String toString() {
        return label.toString();
    }

    /**
     * Add a target to this vertex connected by given weighted edge.
     *
     * @param target vertex
     * @param weight of edge
     * @return true if this vertex adopted target
     */
    public boolean adopt(final Vertex target, final W weight) {
        boolean added = edges.add(new Edge<W>(target, weight));
        if (added) {
            outdegree++;
            target.indegree++;
        }
        return added;
    }

    /**
     * Remove an edge from this vertex.
     *
     * @param edge to remove
     * @return true if edge was removed
     */
    public boolean orphan(final Edge<W> edge) {
        boolean removed = edges.remove(edge);
        if (removed) {
            edge.target().indegree--;
            outdegree--;
        }
        return removed;
    }

    /**
     * Remove the target from this vertex if an edge exists connecting them.
     *
     * @param target vertex to remove
     * @return true if target vertex was removed
     */
    public boolean orphan(final Vertex target) {
        Iterator<Edge<W>> iterator = edges.iterator();
        while (iterator.hasNext()) {
            Edge edge = iterator.next();
            if (edge.target().equals(target)) {
                orphan(edge);
                return true; // found
            }
        }
        return false;
    }

    /**
     * Iterate over vertex edges.
     *
     * @return vertex iterator
     */
    @Override
    public Iterator<Vertex.Edge<W>> iterator() {
        Iterator<Edge<W>> edge = edges.iterator();
        return edge;
    }

    /**
     * Retrieve the vertex hash code.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return hashcode;
    }

    /**
     * Determine vertex equivalence.
     *
     * @param that vertex
     * @return true of this vertex equals that (id, label, and edges)
     */
    @Override
    public boolean equals(final Object that) {
        if (that == this) {
            return true;
        }
        if (!(that instanceof Vertex)) {
            return false;
        }

        Vertex vertex = (Vertex) that;
        return Objects.equals(id, vertex.getId())
            && Objects.equals(label, vertex.getLabel())
            && Objects.equals(edges, vertex.edges);         
    }

    /**
     * Make a shallow copy of this vertex with id and label.
     *
     * @return shallow copy of this vertex with none of the original edges
     */
    Vertex shallowCopy() {
        return new Vertex(id, label);
    }
}
