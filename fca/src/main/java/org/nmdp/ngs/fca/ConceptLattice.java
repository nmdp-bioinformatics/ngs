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
 * A concept lattice is a directed acyclic graph (DAG) that results from
 * topologically sorting concepts according to their partial order. Concepts are
 * added to the lattice dynamically with algorithms described by van der Merwe,
 * Obiedkov, and Kourie. AddIntent: a new incremental algorithm for constructing
 * concept lattices. Lecture Notes in Computer Science Volume 2961, 2004, pp
 * 372-385.
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
    super.putVertex(new Concept(null, ones), Long.MIN_VALUE);
    bottom = root;
  }
  
 private Vertex supremum(BitSet intent, Vertex<Concept, Long> generator) {
		System.out.println("supremum: generator = " + generator.getLabel());
		boolean max = true;
		while(max) {
      
			max = false;
      Iterator<Vertex.Edge<Long>> edges = generator.iterator();
      
			while(edges.hasNext())
			{
        Vertex<Concept, Long> target = (Vertex<Concept, Long>) edges.next().target();
				System.out.println("supremum: " + target.getLabel().toString() + ".gte(" + generator.getLabel().toString() + ") is " + (target.getLabel().gte(generator.getLabel()) ? "true" : "false"));
				if(target.getLabel().gte(generator.getLabel())) {
          continue;
        }  
        
				Concept proposed = new Concept(null, intent);
        System.out.println("supremeum: " + target.getLabel().toString() + ".gte(" + proposed + ") is " + (target.getLabel().gte(proposed) ? "true" : "false"));
				if(target.getLabel().gte(proposed)) {
					generator = target;
					System.out.println("supremum: generator = " + generator.getLabel().toString());
					max = true;
					break;
				}
                
			}
		}
		return generator;
	}
 
 	private Vertex addIntent(BitSet intent, Vertex<Concept, Long> generator) {
		System.out.println("addIntent(" + intent.toString() + ", " + generator.getLabel().toString() + ")");
		generator = supremum(intent, generator);
		

    Concept proposed = new Concept(new BitSet(), intent);
    
		if(generator.getLabel().gte(proposed) &&
       proposed.gte(generator.getLabel())) {
      return generator;
    }
		
		List parents = new ArrayList<ConceptVertex>(); 
		System.out.println("generator has " + generator.getOutDegree() + " out nodes");
		Iterator<Vertex.Edge<Long>> edges = generator.iterator();
    while(edges.hasNext()) {
      Vertex<Concept, Long> target = edges.next().target();
			System.out.println(target.getLabel().toString() + ".gte(" + generator.getLabel() + ") is " + (target.getLabel().gte(generator.getLabel()) ? "true" : "false"));
			if(target.getLabel().gte(generator.getLabel())) {
        System.out.println("continue");
				continue;
			}
			
			Vertex<Concept, Long> candidate = target;
			System.out.println("!(" + candidate.getLabel() + ".gte(" + proposed + ") && !(" +
              proposed + ".gte(" + candidate.getLabel());
      if(!(candidate.getLabel().gte(proposed)) &&
         !(proposed.gte(candidate.getLabel()))) {
        
        BitSet meet = (BitSet) candidate.getLabel().intent().clone();
        System.out.println(candidate.getLabel().intent() + "meet(" + intent + ") = " + meet);
        meet.and(intent);
        System.out.println("recurse addIntent(" + meet + ", " + candidate);
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
          System.out.println("doomed.add(" + parent.getLabel() + ")");
					doomed.add(parent);
				}
			}
			
      
      for (Iterator it = doomed.iterator(); it.hasNext();) {
        Vertex vertex = (Vertex) it.next();
        System.out.println("parents.add(" + vertex.getLabel() + ")");
        parents.remove(vertex);
      }
			
			if(add)
			{
        System.out.println("parents.add(" + candidate.getLabel() + ")");
				parents.add(candidate);
			}
		}
		
		//locus.addAll(((Locus) generator.getLabel()).getExtent());
    Long weight = new Long(0);
    System.out.println("putVertex(" + generator.getLabel() + ", " + proposed + ")");
		Vertex<Concept, Long> child = super.putVertex(generator, proposed, weight);
		bottom = bottom.getLabel().gte(child.getLabel()) ? child : bottom;
    
    System.out.println("bottom = " + bottom.getLabel());
		
    for(Iterator it = parents.iterator(); it.hasNext();) {
      Vertex<Concept, Long> parent = (Vertex<Concept, Long>) it.next();
			if(!parent.equals(generator))
			{
        System.out.println("deleteEdge(" + parent.getLabel() + ", " + generator.getLabel() + ")");
				super.deleteEdge(parent, generator);
        System.out.println("putEdge(" + parent.getLabel() + ", " + child.getLabel() + ")");
				super.putEdge(parent, child, new Long(0));
			}
		}
		
		return child;
	}
  
  private GraphIterator top(Pruner pruner)
	{ 
		return super.iterator(pruner);
	}
	
	private GraphIterator bottom(Pruner pruner)
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
    System.out.println("insert(" + attributes + ")");
    objects.add(object);
    
    BitSet intent = new BitSet(attributes.size());
    for(int i = 0; i < this.attributes.size(); i++) {
      if(attributes.contains(this.attributes.get(i))) {
        intent.flip(i);
      }
    }
    
    System.out.println("insert(" + intent + ")");
    GraphIterator inserted = new GraphIterator(++color, new Pruner<Concept, Long>(), addIntent(intent, root)); 
  }
  

  
  public void go(LatticePruner pruner) {
    Iterator it;
    
    if(pruner.go() == LatticePruner.Direction.DOWN) {
      it = top(pruner);
    } else {
      it = bottom(pruner);
    }
    
    while(it.hasNext()) {
      it.next();
    }
  }
  
  @Override
  public String toString() {
    LatticeWriter writer = new LatticeWriter(LatticePruner.Direction.DOWN,
                                             objects,
                                             attributes);
    
    go(writer);
    return writer.toString();
  }
  
  /**
   * Each vertex is labeled with a Concept.
   * @return true
   */
  public boolean isLabeled() {
    return true;
  }
  /**
   * Each edge is weighted with Long.
   * @return true
   */
  public boolean isWeighted() {
    return true;
  }
  /**
   * Multiple edges between vertexes are not present.
   * @return false
   */
  public boolean isMulti() {
    return false;
  }
  /**
   * Loops are not present.
   * @return false
   */
  public boolean isComplex() {
    return false;
  }
  /**
   * Cycles are not present. 
   * @return false
   */
  public boolean isCyclic() {
    return false;
  }
}