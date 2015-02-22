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

import java.util.Iterator;
import java.lang.StringBuilder;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Class for abstract graphs.
 * @param <L> type for vertexes
 * @param <W> type for edges
 */
public abstract class AbstractGraph<L, W extends Comparable> implements Graph {
  protected Vertex root;
  protected boolean directed;
  protected int color, size, order;
  /**
   * Method to add a new vertex connected to root.
   * @param label assigned to vertex
   * @param weight assigned to edge
   * @return the new vertex
   */
  public Vertex putVertex(L label, W weight) {
    return putVertex(root, label, weight);
  }
  /**
   * Method to add a new vertex connected to source.
   * @param source vertex
   * @param label assigned to vertex
   * @param weight assigned to edge
   * @return the new vertex
   */
  public Vertex putVertex(Vertex source, L label, W weight) {
    checkNotNull(label);
    checkNotNull(weight);        
    
    Vertex target = addVertex(null); // new Vertex(color, label);
    target.setProperty("label", label);
		putEdge(source, target, weight);
    ++size;

		return target;
  }
  /**
   * Method to add a new edge between source and target vertexes.
   * @param source vertex
   * @param target vertex
   * @param weight assigned to edge
   * @return true if edge was added
   */
  public boolean putEdge(Vertex source, Vertex target, W weight) {
    checkNotNull(target);
    checkNotNull(weight);
    
    if(source == root && root == null) {
      root = target;
      return true;
    }
    
    addEdge(null, source, target, weight.toString());
    
    if(!directed) {
      addEdge(null, target, source, weight.toString());
    }
    
    ++order;

		return true;
  }
  /**
   * Method to delete edge between source and target vertexes.
   * @param source vertex
   * @param target vertex
   * @return true if edge was deleted
   */
  public boolean deleteEdge(Vertex source, Vertex target) {
    checkNotNull(source);
    checkNotNull(target);
    
    Edge found = null;
    for(Edge edge : source.getEdges(Direction.BOTH)) {
      if(edge.getVertex(Direction.OUT).equals(target)) {
        found = edge;
        break;
      }
    }
    
    if(found != null) {
      removeEdge(found);
      return true;
    }
    
    return false;
  }
  
  public long size() {
    return size;
  }

  public long order() {
    return order;
  }
  
  public Vertex root() {
    return root;
  }
  
  public boolean isEmpty() {
    return size() == 0 && order() == 0;
  }
  /**
   * 
   * @return false
   */
  public boolean isLabeled() {
    return false;
  }
  /**
   * 
   * @return false
   */
  public boolean isWeighted() {
    return false;
  }
  /**
   * 
   * @return true if edges are directed
   */
  public boolean isDirected() {
    return directed;
  }
  /**
   * 
   * @return true
   */
  public boolean isMulti() {
    return true;
  }
  /**
   * 
   * @return true
   */
  public boolean isComplex() {
    return true;
  }
  /**
   * 
   * @return true
   */
  public boolean isCyclic() {
    return true;
  }
  
}