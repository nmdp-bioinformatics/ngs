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
import java.util.List;
import java.util.ArrayList;
/**
 * A class to iterate over graphs.
 */
public class GraphIterator implements Iterator<Vertex> {
	private final int color;
	private final Pruner pruner;
	private final List<Vertex> path;
	/**
   * Constructor for graph iterators.
   * @param color of the graph
   * @param pruner used to prune vertexes
   * @param vertex to start from
   */
	public GraphIterator(int color, final Pruner pruner, final Vertex vertex)
	{
		this.color = color;
		this.pruner = pruner;
		path = new ArrayList<Vertex>();
		path.add(vertex);
	}
  /**
   * Method to retrieve the next vertex in the iteration path.
   * @return true if the iteration path is not empty
   */
  @Override
	public boolean hasNext() {
		return !path.isEmpty();
	}
  /**
   * Method to retrieve the next vertex in the iteration.
   * @return true if another vertex exists in the iteration
   */
  @Override
	public Vertex next() {
		Vertex vertex = path.get(path.size() - 1);
		path.remove(path.size() - 1);
		vertex.setColor(color);

		if(!pruner.pruneVertex(vertex)) {
      
			for(Iterator<Vertex.Edge> it = vertex.iterator(); it.hasNext();) {   
        Vertex.Edge edge = it.next();	
        if(!pruner.pruneEdge(edge)) {	
          if(edge.target().getColor() != color) {
						edge.target().setColor(color);
						path.add(edge.target());
					}
				}
			}
      
		}
    
		return vertex;
	}

  @Override
	public void remove() {
		
	}

}
