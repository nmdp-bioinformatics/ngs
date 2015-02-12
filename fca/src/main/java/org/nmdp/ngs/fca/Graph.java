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

import org.nmdp.ngs.fca.Vertex;

/**
 *
 */
public interface Graph<L, W> extends Iterable<Vertex> {
          
  public Vertex putVertex(Vertex source, L label, W weight);
  
  public boolean putEdge(Vertex source, Vertex target, W weight);
  
  public boolean deleteEdge(Vertex source, Vertex target);
	/**
   * 
   * @return the number of vertexes 
   */
	public long size();
	/**
   * 
   * @return the number of edges 
   */
	public long order();
  /**
   * 
   * @return root Vertex
   */
  public Vertex root();
	/**
   * 
   * @return true if the graph has no vertexes 
   */
	public boolean isEmpty();
  /**
   * 
   * @return true if vertexes have non-trivial labels 
   */
  public boolean isLabeled();
  /**
   * 
   * @return true if edges have non-trivial weights
   */
  public boolean isWeighted();
  /**
   * 
   * @return true if edges are directed
   */
  public boolean isDirected();
  /**
   * 
   * @return true if multiple edges between vertexes are allowed
   */
  public boolean isMulti();
  /**
   * 
   * @return true if self-edges (loops) between vertexes are allowed
   */
  public boolean isComplex();
  /**
   * 
   * @return true if cycles are allowed
   */
  public boolean isCyclic();
  
}