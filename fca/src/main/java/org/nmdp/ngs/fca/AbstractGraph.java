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

public abstract class AbstractGraph<Label, Weight extends Comparable> implements Graph<Label, Weight>, Iterable<Vertex> {
  protected Vertex root;
  protected boolean directed;
  protected int color, size, order;
  
  @Override
  public Vertex putVertex(Label label, Weight weight) {
    return putVertex(root, label, weight);
  }
  
  @Override
  public Vertex putVertex(Vertex source, Label label, Weight weight) {
    checkNotNull(label);
    checkNotNull(weight);
            
    
    Vertex target = new Vertex(color, label);
		
		//if(isEmpty())
		//{
			//root = target;
		//}
		//else
		//{
		putEdge(source, target, weight);
    ++size;
      
    
		//}
		
		
		return target;
  }
  
  @Override
  public boolean putEdge(Vertex source, Vertex target, Weight weight) {
    checkNotNull(target);
    checkNotNull(weight);
    
    if(source == root && root == null)
    {
      root = target;
      return true;
    }
    
    if(source.adopt(target, weight)) {
      if(!directed)
			{
				if(!target.adopt(source, weight)) {
          return false;
        }
			}
      ++order;
      return true;
    }
			

	
		// System.out.println("order = " + order);
		

		
		// System.out.println(source.toString() + " targeting " + target.toString() + " order = " + order);
		
		return false;
  }
  
  @Override
  public boolean deleteEdge(Vertex source, Vertex target) {
    checkNotNull(source);
    checkNotNull(target);
    
    boolean found = source.orphan(target);
		
		if(!directed)
		{
			target.orphan(source);
		}
		
		if(!source.equals(target))
		{	
			if(root.equals(source))
			{
				root = target;
			}
		}
		
		if(found)
		{
			--order;
		}
		
		
		// System.out.println(source.toString() + " releasing " + target.toString() + " order = " + order);
		
		return found;
  }
  
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(directed ? "digraph" : "graph").append(" {\n");
    
    Iterator<Vertex> vertexes = iterator();
    while(vertexes.hasNext()) {
      
      Vertex vertex = vertexes.next();
      Iterator<Vertex.Edge> edges = vertex.iterator();
      while(edges.hasNext()) {
        Vertex.Edge edge = edges.next();
        builder.append("  \"")
               .append(vertex)
               .append("\" -> \"")
               .append(edge.target())
               .append("\"[label=\"")
               .append(edge.weight())
               .append("\"];\n");
      }
    }

    return builder.append("}").toString();
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