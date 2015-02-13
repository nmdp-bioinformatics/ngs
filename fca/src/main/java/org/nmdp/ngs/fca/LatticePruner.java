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
import java.util.ArrayList;
import java.util.BitSet;

public class LatticePruner<O, A> extends Pruner<Concept, Long> {
  List<O> objects;
  List<A> attributes;
  
  public static enum Direction {
    UP,
    DOWN
  }
  
  private Direction go;
  
  public LatticePruner(Direction direction, List<O> objects, List<A> attributes) {
    super();
    this.go = direction;
    this.objects = objects;
    this.attributes = attributes;
  }
  
  public Direction go() {
    return go;
  }
  
  public List<A> decodeIntent(Concept concept) {
    List<A> attributes = new ArrayList<A>();
    
    BitSet bits = concept.intent();
    for(int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
      attributes.add(this.attributes.get(i));
    }
    
    return attributes;
  }
  
  @Override
  public boolean pruneEdge(Vertex.Edge edge) {
    Vertex<Concept, Long> source = (Vertex<Concept, Long>) parent;
    Vertex<Concept, Long> target = (Vertex<Concept, Long>) edge.target();
    
    if(go == Direction.DOWN) {
      if(source.getLabel().order(target.getLabel()) == Partial.Order.LESS) {
        return true;
      }
      return false;
    }
    
    if(source.getLabel().order(target.getLabel()) == Partial.Order.GREATER) {
      return true;
    }
    return false;
	}
  
}