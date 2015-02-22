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

import com.tinkerpop.blueprints.Vertex;
/**
 * A class for concept lattices. Implementation is a directed acyclic graph
 * (DAG) that results from topologically sorting concepts according to their
 * partial order.
 * @param <O> object type
 * @param <A> attribute type
 */
public final class ConceptLattice<O, A> extends ConnectedGraph<Concept, Long> {
  private final List<O> objects;
  private final List<A> attributes;
  private Vertex bottom;
  /**
   * Construct a concept lattice from a list of attributes.
   * @param attributes
   */
  public ConceptLattice(List<A> attributes) {
    super(false);
    objects = new ArrayList<>();
    this.attributes = attributes;
    BitSet ones = new BitSet(this.attributes.size());
    ones.set(0, this.attributes.size());
    super.putVertex(new Concept(new BitSet(), ones), Long.MIN_VALUE);
    bottom = root;
  }
 /**
  * Method to find the supremum or least upper bound.
  * @param intent to find
  * @param generator starting point and tracer
  * @return vertex whose label-concept represents the supremum
  */
 private Vertex supremum(BitSet intent, Vertex generator) {
    boolean max = true;
		while(max) {
      
			max = false;
      
			for(Vertex target : getVertices()) {
        Concept left = target.getProperty("label");
        Concept right = generator.getProperty("label");
				if(left.gte(right)) {
          continue;
        }  
        
				Concept proposed = new Concept(new BitSet(), intent);
        if(left.gte(proposed)) {
					generator = target;
					max = true;
					break;
				}
                
			}
		}
		return generator;
	}
  /**
   * Method to add concepts dynamically. Algorithm originally described by van
   * der Merwe, Obiedkov, and Kourie. AddIntent: a new incremental algorithm for
   * constructing concept lattices. Lecture Notes in Computer Science Volume
   * 2961, 2004, pp 372-385.
   **/
 	private Vertex addIntent(BitSet intent, Vertex generator) {
		generator = supremum(intent, generator);
    Concept proposed = new Concept(new BitSet(), intent);
    Concept generatorConcept = generator.getProperty("label");
		if(generatorConcept.gte(proposed) &&
       proposed.gte(generatorConcept)) {
      return generator;
    }
		
		List parents = new ArrayList<>();
    for(Vertex target : getVertices()) {
      Concept targetConcept = target.getProperty("label");
      generatorConcept = generator.getProperty("label");
			if(targetConcept.gte(generatorConcept)) {
        continue;
			}
			
			Vertex candidate = target;
      targetConcept = candidate.getProperty("label");
			if(!(targetConcept.gte(proposed)) &&
         !(proposed.gte(targetConcept))) {
        
        BitSet meet = (BitSet) targetConcept.intent().clone();
        meet.and(intent);
        candidate = addIntent(meet, candidate);
			}
			
			boolean add = true;
			List doomed = new ArrayList<>();
      for(Iterator it = parents.iterator(); it.hasNext();) {
        Vertex parent = (Vertex) it.next();
        Concept parentConcept = parent.getProperty("label");
        Concept candidateConcept = candidate.getProperty("label");
        
        if(parentConcept.gte(candidateConcept)) {
					add = false;
					break;
        } else if(candidateConcept.gte(parentConcept)) {
          doomed.add(parent);
				}
			}
      
      for (Iterator it = doomed.iterator(); it.hasNext();) {
        Vertex vertex = (Vertex) it.next();
        parents.remove(vertex);
      }
			
			if(add) {
        parents.add(candidate);
			}
		}
    
    Concept bottomConcept = bottom.getProperty("label");
    proposed.extent().or(generatorConcept.extent());
		Vertex child = super.putVertex(generator, proposed, (long) 0);
    Concept childConcept = child.getProperty("label");
		bottom = bottomConcept.gte(childConcept) ? child : bottom;

    for(Iterator it = parents.iterator(); it.hasNext();) {
      Vertex parent = (Vertex) it.next();
			if(!parent.equals(generator)) {
        super.deleteEdge(parent, generator);
        super.putEdge(parent, child, (long) 0);
			}
		}
		
		return child;
	}
  /**
   * Method to retrieve the top vertex as a graph iterator.
   * @param pruner
   * @return an iterator to the top (root) vertex
   */
  public GraphIterator top(Pruner pruner) { 
		return super.iterator(pruner);
	}
	/**
   * Method to retrieve the bottom vertex as a graph iterator.
   * @param pruner
   * @return an iterator to the bottom vertex
   */
	public GraphIterator bottom(Pruner pruner) { 
		return new GraphIterator(++color, pruner, bottom);
	}
  /**
   * Method to retrieve all objects of this context (concept lattice).
   * @return list of objects 
   */
  public List<O> getObjects() {
    return objects;
  }
  /**
   * Method to retrieve all attributes of this context (concept lattice).
   * @return list of attributes
   */
  public List<A> getAttributes() {
    return attributes;
  }
  /**
   * Method to dynamically insert an object and its attributes into the lattice.
   * Duplicate objects with identical attributes are okay and will be treated as
   * distinct observations.
   * @param object
   * @param attributes list
   */
  public void insert(O object, List<A> attributes) {
    objects.add(object);
    BitSet intent = Concept.encode(attributes, this.attributes);
    Pruner adder = new ExtentAdder.Builder().withObject(object).withObjects(objects).build();
    GraphIterator inserted = new GraphIterator(++color, adder, addIntent(intent, root));
    
    while(inserted.hasNext()) {
      inserted.next();
    }
  }
  /**
   * Method to find the concept with given attributes. This is a low-level
   * retrieval method that enables iteration over the entire lattice starting at
   * the queried vertex; however, access to the vertex label (concept) and its
   * methods (intent and extent) require some tedious dereferencing. If your
   * application doesn't require further query use the leastUpperBound method
   * instead.
   * @param query attributes
   * @return an iterator to the found vertex
   */
  public GraphIterator queryAttributes(final List<A> query) {
    BitSet bits = Concept.encode(query, attributes);
    return new GraphIterator(++color, new Pruner(), supremum(bits, root));
  }
  /**
   * A utility method to find the concept with given attributes using built-in
   * dereferencing.
   * @param query attributes
   * @return the found concept
   */
  public final Concept leastUpperBound(final List<A> query) {
    return (( Vertex ) queryAttributes(query).next()).getLabel();
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
  /**
   * Method to retrieve the number of objects that have the query attributes.
   * @param query attributes
   * @return the number of objects with the given attributes
   */
  public int observed(final List<A> query) {
    return leastUpperBound(query).extent().cardinality();
  }
  /**
   * Method to retrieve the number of objects that have the combined query
   * attributes.
   * @param p first set of query attributes
   * @param q second set of query attributes
   * @return the number of objects with the query attributes
   */
  public int observed(final List<A> p, final List<A> q) {
    return leastUpperBound(p, q).extent().cardinality();
  }
  /**
   * Method to calculate the marginal frequency of the given attributes. 
   * @param query attributes
   * @return marginal frequency of observing the given attributes. Calculation
   * is the number of times the given attributes are observed divided by the
   * total number of observations (objects)
   */
  public double marginal(final List<A> query) {
    return (double) observed(query) / objects.size();
  }
  /**
   * Method to calculate the marginal frequency given two sets of attributes.
   * @param p first set of query attributes
   * @param q second set of query attributes
   * @return the joint frequency of observing both sets of attributes.
   * Calculation is the number of times the given attributes are observed
   * together divided by the total number of observations (objects)
   */
  public double joint(final List<A> p, final List<A> q) {
    return (double) observed(p, q) / objects.size();
  }
  /**
   * Method to calculate the conditional frequency of one attribute set given
   * another.
   * @param p first set of query attributes
   * @param q second set of query attributes
   * @return the conditional frequency. Calculation is the joint divided by the
   * prior
   */
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