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

/**
 * Interface for graphs.
 *
 * @param <L> label type for vertexes
 * @param <W> weight type for edges
 */
public interface Graph<L, W> extends Iterable<Vertex> {

    /**
     * Add a new vertex connected to root.
     *
     * @param label assigned to vertex
     * @param weight assigned to edge
     * @return the new vertex
     */
    Vertex putVertex(L label, W weight);

    /**
     * Add a new vertex connected to source.
     *
     * @param source vertex
     * @param label assigned to vertex
     * @param weight assigned to edge
     * @return the new vertex
     */
     Vertex putVertex(Vertex source, L label, W weight);

     /**
      * Add a new vertex connected to source.
      *
      * @param source vertex
      * @param label assigned to vertex
      * @param weight assigned to edge
      * @return the new vertex
      */
     boolean putEdge(Vertex source, Vertex target, W weight);

     /**
      * Add a new edge between source and target vertexes.
      *
      * @param source vertex
      * @param target vertex
      * @return true if edge was added
      */
     boolean deleteEdge(Vertex source, Vertex target);

     /**
      * Retrieve number of vertexes.
      *
      * @return graph size
      */
     long size();

     /**
      * Retrieve the number of edges.
      *
      * @return graph order 
      */
     long order();

     /**
      * Retrieve the root vertex.
      *
      * @return graph root
      */
     Vertex root();

     /**
      * Determine if the graph is empty.
      *
      * @return true if the graph has no vertexes 
      */
     boolean isEmpty();

     /**
      * Determine if the graph is labeled.
      *
      * @return true if vertexes have non-trivial labels 
      */
     boolean isLabeled();

     /**
      * Determine if the graph is weighted.
      *
      * @return true if edges have non-trivial weights
      */
     boolean isWeighted();

     /**
      * Determine if the graph is directed.
      *
      * @return true if edges are directed
      */
     boolean isDirected();

     /**
      * Determine if this is a multigraph.
      *
      * @return true if multiple edges between vertexes are allowed
      */
     boolean isMulti();

     /**
      * Determine if the graph is complex.
      *
      * @return true if self-edges (loops) between vertexes are allowed
      */
     boolean isComplex();

     /**
      * Determine if the graph supports cycles.
      *
      * @return true if cycles are allowed
      */
     boolean isCyclic();
}
