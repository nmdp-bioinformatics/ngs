/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    System.out.println("TinkerContext top = " + top.getProperty("label"));
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
