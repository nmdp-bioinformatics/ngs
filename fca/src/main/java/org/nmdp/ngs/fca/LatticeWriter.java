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

import java.lang.StringBuilder;

public class LatticeWriter extends LatticePruner {
  StringBuilder builder = new StringBuilder();

  public LatticeWriter(Direction direction, List objects, List attributes) {
    super(direction, objects, attributes);
    builder.append("digraph").append(" {\n");
  }
  
  @Override
  public boolean pruneEdge(Vertex.Edge edge) {
    if(super.pruneEdge(edge)) {
      return true;
    }
    
    Vertex<Concept, Long> source = parent;
    Vertex<Concept, Long> target = edge.target();
    builder.append("  \"")
           .append(decodeIntent(source.getLabel()))
           .append("\" -> \"")
           .append(decodeIntent(target.getLabel()))
           .append("\"[label=\"")
           .append(edge.weight())
           .append("\"];\n");
    
		return false;
	}
  
  @Override
  public String toString() {
    return builder.append("}").toString();
  }
  
}