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

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import java.util.List;
import java.util.ArrayList;
import java.util.BitSet;

/**
 *
 * @author int33484
 */
public class TinkerContext<G, M> extends AbstractContext<G, M> {
  
  public TinkerContext(List<M> attributes) {
    objects = new ArrayList<>();
    this.attributes = attributes;
    BitSet ones = new BitSet(this.attributes.size());
    ones.set(0, this.attributes.size());
    lattice = new TinkerGraph();
    top = lattice.addVertex(null);
    top.setProperty("label", new Concept(new BitSet(), ones));
    top.setProperty("color", color);
    direction = Partial.Order.Direction.FORWARD;
    size = 1;
    order = 0;
    bottom = top;
  }

  @Override
  public Concept greatestLowerBound(List query) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
  
  @Override
  public String toString() {
    String string = "digraph {\n";
    for(Vertex vertex : lattice.getVertices()) {
      for(Edge edge : vertex.getEdges(Direction.BOTH)) {
        Vertex target = edge.getVertex(Direction.OUT);
        if(!vertex.getProperty("label").equals(target.getProperty("label"))) {
          Concept vertexConcept = vertex.getProperty("label");
          Concept targetConcept = target.getProperty("label");
          if(!filter(vertex, target)) {
           string += "  \"" + Concept.decode(vertexConcept.extent(), objects) + Concept.decode(vertexConcept.intent(), attributes) + "\" -> \"" + Concept.decode(targetConcept.extent(), objects) + Concept.decode(targetConcept.intent(), attributes) + "\"[label=\"" + edge.getLabel() + "\"]\n";

          }
        }
        
      }
    }
    
    string += "}";
    return string;
  }
}
