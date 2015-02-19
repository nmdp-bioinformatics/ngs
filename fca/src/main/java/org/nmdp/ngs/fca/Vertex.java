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

import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
/**
 * A class for vertexes.
 * @param <L> label type
 * @param <W> weight type
 */
public class Vertex<L, W extends Comparable> implements Iterable<Vertex.Edge<W>> {
  protected final L label;
  protected final int id, hashcode;
  protected int color;

	protected long indegree;
	protected long outdegree;
	
	private List<Edge<W>> edges;
	/**
   * A class for edges.
   * @param <W> weight type
   */
	public static class Edge<W extends Comparable> implements Comparable<Vertex.Edge<W>> {
    private Vertex target;
		private W weight;
    /**
     * Construct a new edge.
     * @param target vertex
     * @param weight of the edge
     */
		Edge(Vertex target, W weight) { 
			this.target = target;
			this.weight = weight;
		}
		/**
     * Method to retrieve the target vertex.
     * @return target vertex
     */
		Vertex target() {
      return target;
    }
		/**
     * Method to set the edge weight.
     * @param weight of the edge
     */
		public void setWeight(W weight) {
      this.weight = weight;
    }
		/**
     * Method to retrieve the edge weight.
     * @return edge weight
     */
		public W weight() {
      return weight;
    }
    
    @Override
    public String toString() {
      return "{ target : \"" + target + "\", weight : \"" + weight + "\" }";
    }
		/**
     * Method to determine edge equivalence.
     * @param that
     * @return true if this edge equals that
     */
		@Override
		public boolean equals(Object that) {
			if(target == ((Edge<W>) that).target) {
        return true;
      }
      
      if(!(that instanceof Edge)) {
        return false;
      }
 
      // TODO: include target vertex equivalence
       
      Edge edge = (Edge) that;
			return Objects.equals(weight, edge.weight());
		}
    /**
     * Method to compare edges by weight.
     * @param that edge
     * @return integer code representing the result of comparison
     */
    @Override
		public int compareTo(Edge<W> that) {
			return weight.compareTo(that.weight());
		}
	}
  /**
   * Construct a new singleton vertex.
   * @param id
   * @param label 
   */
	public Vertex(int id, L label) {
		this.id = id;
    this.color = 0;
		this.label = label;
		this.edges = new ArrayList<Edge<W>>();
    hashcode = Objects.hash(id, label);
	}
  /**
   * Method to retrieve the vertex id.
   * @return vertex id
   */
	public int getId() {
    return id;
  }
  /**
   * Method to retrieve the vertex color.
   * @return vertex color
   */
  public int getColor() {
    return color;
  }
  /**
   * Method to set the vertex color
   * @param color of vertex
   */
  public void setColor(int color) {
    this.color = color;
  }
	/**
   * Method to retrieve the vertex label.
   * @return vertex label
   */
	public L getLabel() { return label; }
	/**
   * Method to retrieve the in-degree.
   * @return number of in-edges
   */
	public long getInDegree() { return indegree; }
	/**
   * Method to retrieve the out-degree.
   * @return number of out-edges
   */
	public long getOutDegree() { return outdegree; }
	/**
   * Method to determinig if vertex is empty.
   * @return true if the vertex has no out-edges.
   */
	public boolean isEmpty() { return outdegree == 0; }
	/**
   * Method to determine if vertex is a singleton.
   * @return  true if the vertex has no in- or out-edges
   */
	public boolean isSingleton() { return indegree == 0 && isEmpty(); }
	/**
   * Method to convert this vertex to its string representation
   * @return vertex label
   */
  @Override
	public String toString() {
    return label.toString();
  }
	/**
   * Method to add a target to this vertex connected by given weighted edge.
   * @param target vertex
   * @param weight of edge
   * @return true if this vertex adopted target
   */
	public boolean adopt(Vertex target, W weight) {
		boolean added = edges.add(new Edge<W>(target, weight));
    if(added) {
      outdegree++;
		  target.indegree++;
    }
    
    return added;
	}
	/**
   * Method to remove an edge from this vertex.
   * @param edge to remove
   * @return true if edge was removed
   */
	public boolean orphan(Edge<W> edge) {
		boolean removed = edges.remove(edge);
    
    if(removed) {
      edge.target().indegree--;
		  outdegree--;
    }

		return removed;
	}
	/**
   * Method to remove the target from this vertex if an edge exists connecting
   * them.
   * @param target vertex to remove
   * @return true if target vertex was removed
   */
	public boolean orphan(Vertex target) {
		Iterator<Edge<W>> iterator = edges.iterator();
		while(iterator.hasNext()) {
			Edge edge = iterator.next();
			if(edge.target().equals(target)) {
				orphan(edge);
				return true; // found
			}
		}
		
		return false;
	}
  /**
   * Method to iterate over vertex edges.
   * @return vertex iterator
   */
  @Override
	public Iterator<Vertex.Edge<W>> iterator()
	{
    Iterator<Edge<W>> edge = edges.iterator();
		return edge;
	}
  /**
   * Method to retrieve the vertex hash code.
   * @return hash code
   */
  @Override
  public int hashCode() {
    return hashcode;
  }
  /**
   * Method to determine vertex equivalence.
   * @param that vertex
   * @return true of this vertex equals that (id, label, and edges)
   */
  @Override
  public boolean equals(final Object that) {
    if(that == this) {
      return true;
    }
    
    if(!(that instanceof Vertex)) {
      return false;
    }
    
    Vertex vertex = (Vertex) that;
    return Objects.equals(id, vertex.getId())
        && Objects.equals(label, vertex.getLabel())
        && Objects.equals(edges, vertex.edges);         
  }
  /**
   * Method to make a shallow copy of this vertex with id and label.
   * @return shallow copy of this vertex with none of the original edges
   */
  Vertex shallowCopy() {
    return new Vertex(id, label);
  }
}