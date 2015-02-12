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

import java.util.Set;
import java.util.TreeSet;

public class Pruner<L, W> {
	protected Vertex parent;
	protected Set<L> labels;
	protected Set<W> weights;
	
	public Pruner()
	{
		labels = new TreeSet<L>();
		weights = new TreeSet<W>();
	}
	
	public void setLabels(final Set<L> labels)
	{
		this.labels = labels;
	}
	
	public void setWeights(final Set<W> weights)
	{
		this.weights = weights;
	}
	
	public boolean pruneVertex(Vertex vertex)
	{
		parent = vertex;
		
		if(labels.contains(vertex.getLabel()))
		{
			return true;
		}
		
		return false;
	}
	
	public boolean pruneEdge(Vertex.Edge edge)
	{
		if(weights.contains(edge.weight()))
		{
			return true;
		}
		return false;
	}
}