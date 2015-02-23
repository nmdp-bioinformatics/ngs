/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nmdp.ngs.fca;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.BitSet;
/**
 * 
 * @author int33484
 * @param <G> object type
 * @param <M> attribute type
 */
public abstract class AbstractContext<G, M> implements Context<G, M> {
  protected List<G> objects;
  protected List<M> attributes;
  protected Vertex top;
  protected Vertex bottom;
  protected Graph lattice;
  protected Partial.Order.Direction direction;
  
  private boolean go(Vertex source, Vertex target, Partial.Order.Direction direction) {
    Concept sourceConcept = source.getProperty("label");
    Concept targetConcept = target.getProperty("label");
    return sourceConcept.order(targetConcept).filter(direction);
  }
  
   /**
  * Method to find the supremum or least upper bound.
  * @param intent to find
  * @param generator starting point and tracer
  * @return vertex whose label-concept represents the supremum
  */
 private Vertex supremum(BitSet intent, Vertex generator) {
   System.out.println("supremum(" + intent + ", " + generator.getProperty("label") + ")");
    boolean max = true;
    
		while(max) {   
			max = false;
			for(Edge edge : generator.getEdges(Direction.BOTH)) {
        Vertex target = edge.getVertex(Direction.OUT);
        Concept targetConcept = target.getProperty("label");
        Concept generatorConcept = generator.getProperty("label");
        
        System.out.print(targetConcept.intent() + " gte " + generatorConcept.intent());
        
				if(targetConcept.order(generatorConcept).filter(direction)) {
          System.out.println(" ... yes, continue");
          continue;
        }  
        
				Concept proposed = new Concept(new BitSet(), intent);
        
        System.out.print(" ... no, " + targetConcept.intent() + " gte " + proposed.intent());
        
        if(targetConcept.gte(proposed)) {
          
					generator = target;
          
          System.out.println(" ... yes, generator = " + generator.getProperty("label"));
          
					max = true;
					break;
				}        
			}
		}
    
    System.out.println("GENERATOR = " + generator.getProperty("label"));
    
		return generator;
	}
 
 private void addUndirectedEdge(Vertex source, Vertex target, String weight) {
   lattice.addEdge(null, source, target, weight);
   lattice.addEdge(null, target, source, weight);
 }
 
  /**
  * Method to find the supremum or least upper bound.
  * @param intent to find
  * @param generator starting point and tracer
  * @return vertex whose label-concept represents the supremum
  */
 	private Vertex addIntent(BitSet intent, Vertex generator) {
    System.out.println("addIntent(" + intent + ", " + generator.getProperty("label") + ")");
		generator = supremum(intent, generator);
    Concept proposed = new Concept(new BitSet(), intent);
    Concept generatorConcept = generator.getProperty("label");
		if(generatorConcept.gte(proposed) &&
       proposed.gte(generatorConcept)) {
      return generator;
    }
		
		List parents = new ArrayList<>();
    for(Vertex target : lattice.getVertices()) {
      Concept targetConcept = target.getProperty("label");
      generatorConcept = generator.getProperty("label");
      
      System.out.print(targetConcept.intent() + " gte " + generatorConcept.intent());
      
			if(targetConcept.gte(generatorConcept)) {
        System.out.println(" ... yes, continue");
        continue;
			}
			
      System.out.print(" ... no, ");
			Vertex candidate = target;
      Concept candidateConcept = candidate.getProperty("label");
      
      System.out.print("!" + candidateConcept.intent() + " gte " + proposed.intent() + " and !" + proposed.intent() + " gte " + candidateConcept.intent());
      
			if(!(targetConcept.gte(proposed)) &&
         !(proposed.gte(targetConcept))) {
        
        
        BitSet meet = (BitSet) targetConcept.intent().clone();
        meet.and(intent);
        
        System.out.println(" ... yes, addIntent(meet =" + meet);
        candidate = addIntent(meet, candidate);
			}
			
			boolean add = true;
			List doomed = new ArrayList<>();
      for(Iterator it = parents.iterator(); it.hasNext();) {
        Vertex parent = (Vertex) it.next();
        Concept parentConcept = parent.getProperty("label");
        candidateConcept = candidate.getProperty("label");
        
        System.out.print(parentConcept.intent() + " gte " + candidateConcept.intent());
        
        if(parentConcept.gte(candidateConcept)) {
          System.out.println(" ... yes, add = false");
					add = false;
					break;
        } else if(candidateConcept.gte(parentConcept)) {
          System.out.println(" ... no, doomed.add(" + parentConcept.intent());
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
		Vertex child = lattice.addVertex(null);
    child.setProperty("label", proposed);
    
    System.out.println("lattice.addEdge(" + generatorConcept.intent() + ", " + proposed.intent() + ")");
            
    addUndirectedEdge(generator, child, "");
		bottom = bottomConcept.gte(proposed) ? child : bottom;

    for(Iterator it = parents.iterator(); it.hasNext();) {
      Vertex parent = (Vertex) it.next();
      Concept parentConcept = parent.getProperty("label");
      
      System.out.println("parentConcept = " + parentConcept.intent());
      
			if(!parent.equals(generator)) {
        Edge found = null;
        for(Edge edge : parent.getEdges(Direction.BOTH)) {
          if(edge.getVertex(Direction.OUT).getProperty("label").equals(generator.getProperty("label"))) {
            lattice.removeEdge(edge);
            //break;
          }
          
          if(edge.getVertex(Direction.IN).getProperty("label").equals(generator.getProperty("label"))) {
            lattice.removeEdge(edge);
            //break;
          }
        }
        
        System.out.println("addEdge" + parentConcept.intent() + ", " + generatorConcept.intent() + ")");
        
        addUndirectedEdge(parent, child, "");
			}
		}
		
		return child;
	}
  
  public Concept insert(G object, List<M> attributes) {
    objects.add(object);
    BitSet intent = Concept.encode(attributes, this.attributes);
    Vertex added = addIntent(intent, top);
    
    // Add extent
    
    return added.getProperty("label");
  }
  
  @Override
  public List<G> getObjects() {
    return objects;
  }
  
  @Override
  public List<M> getAttributes() {
    return attributes;
  }

  @Override
  public Concept bottom() {
    return bottom.getProperty("label");
  }

  @Override
  public Concept top() {
    return top.getProperty("label");
  }
  /**
   * Method to find the concept with given attributes. This is a low-level
   * retrieval method that enables iteration over the entire lattice starting at
   * the queried vertex; however, access to the vertex label (concept) and its
   * methods (intent and extent) require some tedious dereferencing. If your
   * application doesn't require further query use the leastUpperBound method
   * instead.
   * @param query attributes
   * @return the found vertex
   */
  private Vertex queryAttributes(final List query) {
    BitSet bits = Concept.encode(query, attributes);
    return supremum(bits, top);
  }
  
  private Vertex queryAttributes(final List left, final List right) {
    BitSet join = Concept.encode(left, attributes);
    BitSet bits = Concept.encode(right, attributes);
    join.or(bits);
    return supremum(join, top);
  }
  
  @Override
  public final Concept leastUpperBound(final List query) {
    return queryAttributes(query).getProperty("label");
  }
  
  public final Concept leastUpperBound(final List<M> left, final List<M> right) {
    return queryAttributes(left, right).getProperty("label");
  }
  /**
   * Method to retrieve the number of objects that have the query attributes.
   * @param query attributes
   * @return the number of objects with the given attributes
   */
  @Override
  public int support(final List query) {
    return leastUpperBound(query).extent().cardinality();
  }
  /**
   * Method to retrieve the number of objects that have the combined query
   * attributes.
   * @param p first set of query attributes
   * @param q second set of query attributes
   * @return the number of objects with the query attributes
   */
  @Override
  public int support(final List left, final List right) {
    return leastUpperBound(left, right).extent().cardinality();
  }
  /**
   * Method to calculate the marginal frequency of the given attributes. 
   * @param query attributes
   * @return marginal frequency of observing the given attributes. Calculation
   * is the number of times the given attributes are observed divided by the
   * total number of observations (objects)
   */
  @Override
  public double marginal(final List query) {
    return (double) support(query) / objects.size();
  }
  /**
   * Method to calculate the marginal frequency given two sets of attributes.
   * @param left set of query attributes
   * @param right set of query attributes
   * @return the joint frequency of observing both sets of attributes.
   * Calculation is the number of times the given attributes are observed
   * together divided by the total number of observations (objects)
   */
  @Override
  public double joint(final List left, final List right) {
    return (double) support(left, right) / objects.size();
  }
  /**
   * Method to calculate the conditional frequency of one attribute set given
   * another.
   * @param left set of query attributes
   * @param right set of query attributes
   * @return the conditional frequency. Calculation is the joint divided by the
   * prior
   */
  @Override
  public double conditional(final List left, final List right) {
    return joint(left, right) / marginal(right);
  }
}
