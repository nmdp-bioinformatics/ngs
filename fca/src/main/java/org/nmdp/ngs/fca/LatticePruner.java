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
/**
 * 
 * @param <O> object type for concepts
 * @param <A> attribute type for concepts
 */
public class LatticePruner<O, A> extends Pruner<Concept, Long> {
  private List<O> objects;
  private List<A> attributes;
  private Lattice.Direction go;
  
  protected static abstract class Init<O, A, T extends Init<O, A, T>> extends Pruner.Init<Concept, Long, T> {
    private List<O> objects;
    private List<A> attributes;
    private Lattice.Direction go;
    
    public T go(Lattice.Direction go) {
      this.go = go;
      return self();
    }
    
    public T withObjects(final List<O> objects) {
      this.objects = objects;
      return self();
    }
    
    public T withAttributes(final List<A> attributes) {
      this.attributes = attributes;
      return self();
    }
    
    public LatticePruner build() {
      return new LatticePruner(this);
    }
  }
  
  public static class Builder<O, A> extends Init<O, A, Builder<O, A>> {
    @Override
    protected Builder self() {
      return this;
    }
  }
  
  protected LatticePruner(Init<O, A, ?> init) {
    super(init);
    this.attributes = init.attributes;
    this.objects = init.objects;
    this.go = init.go;
  }
  
  protected LatticePruner(Lattice.Direction direction, List<O> objects, List<A> attributes) {
    super();
    this.go = direction;
    this.objects = objects;
    this.attributes = attributes;
  }
  
  public Lattice.Direction go() {
    return go;
  }
  /**
   * 
   * @param concept to decode
   * @return List of attributes indexed from the decoded concept's intent
   */
  public List<A> decodeIntent(Concept concept) {
    List<A> attributes = new ArrayList<A>();
    
    BitSet bits = concept.intent();
    for(int i = bits.nextSetBit(0); i >= 0; i = bits.nextSetBit(i + 1)) {
      attributes.add(this.attributes.get(i));
    }
    
    return attributes;
  }
  /**
   * 
   * @param edge
   * @return 
   */
  @Override
  public boolean pruneEdge(Vertex.Edge edge) {
    Vertex<Concept, Long> source = (Vertex<Concept, Long>) parent;
    Vertex<Concept, Long> target = (Vertex<Concept, Long>) edge.target();
    
    if(go == Lattice.Direction.DOWN) {
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