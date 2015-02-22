/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nmdp.ngs.fca;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

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
    lattice.addVertex(new Concept(new BitSet(), ones));
    bottom = top;
  }

  @Override
  public Concept greatestLowerBound(List query) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
