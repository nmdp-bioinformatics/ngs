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

public class GraphIterator implements Iterator<Vertex> {
	int color;
	Pruner pruner;
	private List<Vertex> path;
	
	public GraphIterator(int color, final Pruner pruner, final Vertex node)
	{
		this.color = color;
		this.pruner = pruner;
		path = new ArrayList<Vertex>();
		path.add(node);
	}

	public boolean hasNext() {
		// TODO Auto-generated method stub
		return !path.isEmpty();
	}

	public Vertex next() {
		Vertex vertex = path.get(path.size() - 1);
		path.remove(path.size() - 1);
		
		
		
		vertex.setColor(color);
		
		// System.out.println("node = " + node.toString() + " set color to " + color);
		
		if(!pruner.pruneVertex(vertex))
		{
			// System.out.println("don't prune node");
      Iterator<Vertex.Edge> edges = vertex.iterator();
			while(edges.hasNext())
			{
        Vertex.Edge edge = edges.next();
				// System.out.println("edge target = " + edge.getTargetNode().toString());
				if(!pruner.pruneEdge(edge))
				{
					// System.out.println("don't prune edge");
					if(edge.target().getColor() != color)
					{
						
						edge.target().setColor(color);
						path.add(edge.target());
					}
				}
			}
		}
		
		return vertex;
	}

	public void remove() {
		
	}

}
