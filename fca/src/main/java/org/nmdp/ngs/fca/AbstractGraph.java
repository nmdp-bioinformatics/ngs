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

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Class for abstract graphs.
 * @param <L> type for vertexes
 * @param <W> type for edges
 */
public abstract class AbstractGraph<L, W extends Comparable> implements Graph<L, W>, Iterable<Vertex> {
  protected Vertex root;
  protected boolean directed;
  protected int color, size, order;
  /**
   * Method to add a new vertex connected to root.
   * @param label assigned to vertex
   * @param weight assigned to edge
   * @return the new vertex
   */
  @Override
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
  @Override
  public Vertex putVertex(Vertex source, L label, W weight) {
    checkNotNull(label);
    checkNotNull(weight);        
    
    Vertex target = new Vertex(color, label);
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
  @Override
  public boolean putEdge(Vertex source, Vertex target, W weight) {
    checkNotNull(target);
    checkNotNull(weight);
    
    if(source == root && root == null) {
      root = target;
      return true;
    }
    
    if(source.adopt(target, weight)) {
      if(!directed) {
				if(!target.adopt(source, weight)) {
          return false;
        }
			}
      ++order;
      return true;
    }

		return false;
  }
  /**
   * Method to delete edge between source and target vertexes.
   * @param source vertex
   * @param target vertex
   * @return true if edge was deleted
   */
  @Override
  public boolean deleteEdge(Vertex source, Vertex target) {
    checkNotNull(source);
    checkNotNull(target);
    
    boolean found = source.orphan(target);
		
		if(!directed) {
			target.orphan(source);
		}
		
		if(!source.equals(target)) {	
			if(root.equals(source)) {
				root = target;
			}
		}
		
		if(found) {
			--order;
		}

		return found;
  }
  
  @Override
  public long size() {
    return size;
  }

  @Override
  public long order() {
    return order;
  }
  
  @Override
  public Vertex root() {
    return root;
  }
  
  @Override
  public boolean isEmpty() {
    return size() == 0 && order() == 0;
  }
  /**
   * 
   * @return false
   */
  @Override
  public boolean isLabeled() {
    return false;
  }
  /**
   * 
   * @return false
   */
  @Override
  public boolean isWeighted() {
    return false;
  }
  /**
   * 
   * @return true if edges are directed
   */
  @Override
  public boolean isDirected() {
    return directed;
  }
  /**
   * 
   * @return true
   */
  @Override
  public boolean isMulti() {
    return true;
  }
  /**
   * 
   * @return true
   */
  @Override
  public boolean isComplex() {
    return true;
  }
  /**
   * 
   * @return true
   */
  @Override
  public boolean isCyclic() {
    return true;
  }
  
}