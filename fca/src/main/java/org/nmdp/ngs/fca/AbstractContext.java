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

import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.transform.BothPipe;
import com.tinkerpop.pipes.util.iterators.SingleIterator;

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
  
  protected Graph lattice;  
  protected Vertex bottom, top;

  protected Partial.Order.Direction direction;
  protected int color, size, order;
  
  private final static String LABEL = "label";
  
  protected boolean filter(Vertex source, Vertex target) {
    Concept sourceConcept = source.getProperty(LABEL);
    Concept targetConcept = target.getProperty(LABEL);
    return filter(sourceConcept, targetConcept);
  }
  
  private boolean filter(Vertex source, Concept right) {
    Concept sourceConcept = source.getProperty(LABEL);
    return filter(sourceConcept, right);
  }
  
  private boolean filter(Concept left, Vertex target) {
    Concept targetConcept = target.getProperty(LABEL);
    return filter(left, targetConcept);
  }
  
  private boolean filter(Concept right, Concept left) {
    return right.order(left).gte();
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
			for(Edge edge : generator.getEdges(Direction.BOTH)) {
        Vertex target = edge.getVertex(Direction.OUT);
        
        if(filter(target, generator)) {
          continue;
        }  
        
				Concept proposed = new Concept(new BitSet(), intent);
        
        if(filter(target, proposed)) {
					generator = target;
					max = true;
					break;
				}        
			}
		}
    
		return generator;
	}
 
  private Vertex addConcept(Concept label) {
    Vertex child = lattice.addVertex(null);
    child.setProperty("label", label);
    child.setProperty("color", color);
    ++size;
    return child;
  }
 
  private void addUndirectedEdge(Vertex source, Vertex target, String weight) {
    Concept sourceConcept = source.getProperty("label");
    Concept targetConcept = target.getProperty("label");
    
    Partial.Order.Direction direction = this.direction;
    
    if(targetConcept.order(sourceConcept).gte()) {
      direction = Partial.Order.Direction.REVERSE;
    }
    
    lattice.addEdge(null, source, target, "");
    lattice.addEdge(null, target, source, "");
    ++order;
  }
  
 private void removeUndirectedEdge(Vertex source, Vertex target) {
   for(Edge edge : source.getEdges(Direction.BOTH)) {
     if(edge.getVertex(Direction.OUT).equals(target)) {
       lattice.removeEdge(edge);
       break;
     }
          
     if(edge.getVertex(Direction.IN).equals(target)) {
       lattice.removeEdge(edge);
       break;
     }
   }
   --order;
 }
 
  /**
  * Method to find the supremum or least upper bound.
  * @param intent to find
  * @param generator starting point and tracer
  * @return vertex whose label-concept represents the supremum
  */
 	private Vertex addIntent(BitSet intent, Vertex generator) {
		generator = supremum(intent, generator);
    Concept proposed = new Concept(new BitSet(), intent);
    
    if(filter(generator, proposed) && filter(proposed, generator)) {
      return generator;
    }
		
		List parents = new ArrayList<>();
    for(Vertex target : lattice.getVertices()) {
      
      if(filter(target, generator)) {
        continue;
			}
			
			Vertex candidate = target;
      if(!filter(target, proposed) && !filter(proposed, target)) {
        Concept targetConcept = target.getProperty(LABEL);
        BitSet meet = (BitSet) targetConcept.intent().clone();
        meet.and(intent);
        candidate = addIntent(meet, candidate);
			}
			
			boolean add = true;
			List doomed = new ArrayList<>();
      for(Iterator it = parents.iterator(); it.hasNext();) {
        Vertex parent = (Vertex) it.next();
        
        if(filter(parent, candidate)) {
					add = false;
					break;
        } else if(filter(candidate, parent)) {
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
    
    Concept generatorConcept = generator.getProperty(LABEL);
    proposed.extent().or(generatorConcept.extent());
    Vertex child = addConcept(proposed);
    addUndirectedEdge(generator, child, "");
    bottom = filter(bottom, proposed) ? child : bottom;

    for(Iterator it = parents.iterator(); it.hasNext();) {
      Vertex parent = (Vertex) it.next();
			if(!parent.equals(generator)) {
        removeUndirectedEdge(parent, generator);
        addUndirectedEdge(parent, child, "");
			}
		}
		
		return child;
	}
  
  @Override
  public Concept insert(G object, List<M> attributes) {
    System.out.println("insert(" + object + ", " + attributes);
    objects.add(object);
    BitSet intent = Concept.encode(attributes, this.attributes);
    Vertex added = addIntent(intent, top);
    
    
    // Add extent
    
    List<G> list = new ArrayList<G>();
    list.add(object);
    BitSet extent = Concept.encode(list, objects);
    //Concept addedConcept = added.getProperty("label");
    //addedConcept.extent().or(extent);
    
    List queue = new ArrayList();
    added.setProperty("color", ++color);
    queue.add(added);
    
    while(!queue.isEmpty()) {
      Vertex visiting = (Vertex) queue.remove(0);
      Concept visitingConcept = visiting.getProperty(LABEL);
      visitingConcept.extent().or(extent);
      
      for(Edge edge : visiting.getEdges(Direction.BOTH)) {
        Vertex target = edge.getVertex(Direction.OUT);
        
        if((int) target.getProperty("color") != color) {
          if(filter(visiting, target)) {
            target.setProperty("color", color);
            queue.add(target);
          }
        }
        
      }
    }
    
    return added.getProperty(LABEL);
  }
  
  public int size() {
    return size;
  }
  
  public int order() {
    return order;
  }
  
  @Override
  public final List getObjects() {
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
  private Vertex queryAttributes(final List...queries) {
    BitSet join = new BitSet();
    
    for(List query : queries) {
      BitSet bits = Concept.encode(query, attributes);
      join.or(bits);
    }
    
    return supremum(join, top);
  }
  
  @Override
  public final Concept leastUpperBound(final List query) {
    return queryAttributes(query).getProperty("label");
  }
  
  public final Concept leastUpperBound(final List left, final List right) {
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
