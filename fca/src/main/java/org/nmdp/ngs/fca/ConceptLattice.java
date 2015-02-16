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

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
/**
 * A class for concept lattices. Implementation is a directed acyclic graph
 * (DAG) that results from topologically sorting concepts according to their
 * partial order. Concepts are added to the lattice dynamically with algorithms
 * described by van der Merwe, Obiedkov, and Kourie. AddIntent: a new
 * incremental algorithm for constructing concept lattices. Lecture Notes in
 * Computer Science Volume 2961, 2004, pp 372-385.
 * @author int33484
 * @param <O> object type
 * @param <A> attribute type
 */
public final class ConceptLattice<O, A> extends ConnectedGraph<Concept, Long> {
  private List<O> objects;
  private List<A> attributes;
  private Vertex<Concept, Long> bottom;
  
  public ConceptLattice(List<A> attributes) {
    super(false);
    objects = new ArrayList<O>();
    this.attributes = attributes;
    BitSet ones = new BitSet(this.attributes.size());
    ones.set(0, this.attributes.size());
    super.putVertex(new Concept(new BitSet(), ones), Long.MIN_VALUE);
    bottom = root;
  }

 private Vertex supremum(BitSet intent, Vertex<Concept, Long> generator) {
		boolean max = true;
		while(max) {
      
			max = false;
      Iterator<Vertex.Edge<Long>> edges = generator.iterator();
      
			while(edges.hasNext())
			{
        Vertex<Concept, Long> target = edges.next().target();
				if(target.getLabel().gte(generator.getLabel())) {
          continue;
        }  
        
				Concept proposed = new Concept(new BitSet(), intent);
        if(target.getLabel().gte(proposed)) {
					generator = target;
					max = true;
					break;
				}
                
			}
		}
		return generator;
	}
 
 	private Vertex addIntent(BitSet intent, Vertex<Concept, Long> generator) {
		generator = supremum(intent, generator);
		

    Concept proposed = new Concept(new BitSet(), intent);
    
		if(generator.getLabel().gte(proposed) &&
       proposed.gte(generator.getLabel())) {
      return generator;
    }
		
		List parents = new ArrayList<ConceptVertex>(); 
		Iterator<Vertex.Edge<Long>> edges = generator.iterator();
    while(edges.hasNext()) {
      Vertex<Concept, Long> target = edges.next().target();
			if(target.getLabel().gte(generator.getLabel())) {
        continue;
			}
			
			Vertex<Concept, Long> candidate = target;
			if(!(candidate.getLabel().gte(proposed)) &&
         !(proposed.gte(candidate.getLabel()))) {
        
        BitSet meet = (BitSet) candidate.getLabel().intent().clone();
        meet.and(intent);
        candidate = addIntent(meet, candidate);
				//Locus intersect = (Locus) candidate.getLabel().intersect(locus);
				
				//if(intersect.isEmpty() || intersect.getLength() > 20)
				//{
					//candidate = push(intersect, candidate);
				//}
				
			}
			
			boolean add = true;
			List doomed = new ArrayList<ConceptVertex>();
      for(Iterator it = parents.iterator(); it.hasNext();) {
        Vertex<Concept, Long> parent =  (Vertex<Concept, Long>) it.next();
        
        if(parent.getLabel().gte(candidate.getLabel())) {
					add = false;
					break;
        } else if(candidate.getLabel().gte(parent.getLabel())) {
          doomed.add(parent);
				}
			}
			
      
      for (Iterator it = doomed.iterator(); it.hasNext();) {
        Vertex vertex = (Vertex) it.next();
        parents.remove(vertex);
      }
			
			if(add)
			{
        parents.add(candidate);
			}
		}
		
		//locus.addAll(((Locus) generator.getLabel()).getExtent());
    Long weight = new Long(0);
    proposed.extent().or(generator.getLabel().extent());
		Vertex<Concept, Long> child = super.putVertex(generator, proposed, weight);
		bottom = bottom.getLabel().gte(child.getLabel()) ? child : bottom;

    for(Iterator it = parents.iterator(); it.hasNext();) {
      Vertex<Concept, Long> parent = (Vertex<Concept, Long>) it.next();
			if(!parent.equals(generator)) {
        super.deleteEdge(parent, generator);
        super.putEdge(parent, child, new Long(0));
			}
		}
		
		return child;
	}
  
  public GraphIterator top(Pruner pruner)
	{ 
		return super.iterator(pruner);
	}
	
	public GraphIterator bottom(Pruner pruner)
	{ 
		return new GraphIterator(++color, pruner, bottom);
	}
  
  public List<O> getObjects() {
    return objects;
  }
  
  public List<A> getAttributes() {
    return attributes;
  }
  
  public void insert(O object, List<A> attributes) {
    if(!objects.contains(object)) {
      objects.add(object);
    }

    BitSet intent = Concept.encode(attributes, this.attributes);
    Pruner adder = new ExtentAdder.Builder().withObject(object).withObjects(objects).build();
    GraphIterator inserted = new GraphIterator(++color, adder, addIntent(intent, root));
    
    while(inserted.hasNext()) {
      inserted.next();
    }
  }
  
  public GraphIterator queryAttributes(final List<A> query) {
    BitSet bits = Concept.encode(query, attributes);
    return new GraphIterator(++color, new Pruner(), supremum(bits, root));
  }
  
  public final Concept leastUpperBound(final List<A> query) {
    return (( Vertex<Concept, Long> ) queryAttributes(query).next()).getLabel();
  }
  
  public GraphIterator queryAttributes(final List<A> p, final List<A> q) {
    BitSet join = Concept.encode(p, attributes);
    BitSet bits = Concept.encode(q, attributes);
    join.or(bits);
    return new GraphIterator(++color, new Pruner(), supremum(join, root));
  }
  
  public final Concept leastUpperBound(final List<A> p, final List<A> q) {
    return (( Vertex<Concept, Long> ) queryAttributes(p, q).next()).getLabel();
  }
  
  public double marginal(final List<A> query) {
    return (double) leastUpperBound(query).extent().cardinality() / objects.size();
  }
  
  public double joint(final List<A> p, final List<A> q) {
    return (double) leastUpperBound(p, q).extent().cardinality() / objects.size();
  }
  
  public double conditional(final List<A> p, final List<A> q) {
    return joint(p, q) / marginal(q);
  }
  /**
   * Iterate over the lattice and do some work defined by pruner. If
   * Lattice.Direction is DOWN traversal happens from top to bottom.
   * If Lattice.Direction is UP traversal happens in reverse order across the
   * lattice's dual from bottom to top.
   * @param pruner 
   */
  public void go(Pruner pruner) {
    LatticePruner filter = (LatticePruner) pruner;
    Iterator it;
    
    if(filter.go() == Lattice.Direction.DOWN) {
      it = top(filter);
    } else {
      it = bottom(filter);
    }
    
    while(it.hasNext()) {
      it.next();
    }
  }
  
  @Override
  public String toString() {
    Pruner writer = new LatticeWriter.Builder()
                                     .go(Lattice.Direction.DOWN)
                                     .withObjects(objects)
                                     .withAttributes(attributes)
                                     .build();
                                        
    go(writer);
    return writer.toString();
  }
  /**
   * Each vertex is labeled with a Concept.
   * @return true
   */
  @Override
  public boolean isLabeled() {
    return true;
  }
  /**
   * Each edge is weighted with Long.
   * @return true
   */
  @Override
  public boolean isWeighted() {
    return true;
  }
  /**
   * Edges are directed.
   * @return true
   */
  @Override
  public boolean isDirected() {
    return true;
  }
  /**
   * Multiple edges between vertexes are not present.
   * @return false
   */
  @Override
  public boolean isMulti() {
    return false;
  }
  /**
   * Loops are not present.
   * @return false
   */
  @Override
  public boolean isComplex() {
    return false;
  }
  /**
   * Cycles are not present. 
   * @return false
   */
  @Override
  public boolean isCyclic() {
    return false;
  }
}