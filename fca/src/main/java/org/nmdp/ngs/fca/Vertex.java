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

public class Vertex<L, W extends Comparable> implements Iterable<Vertex.Edge<W>> {
  protected final L label;
  protected final int id, hashcode;
  protected int color;

	protected long indegree;
	protected long outdegree;
	
	private List<Edge<W>> edges;
	
	public static class Edge<W extends Comparable> implements Comparable<Vertex.Edge<W>>
	{
    private Vertex target;
		private W weight;

		Edge(Vertex target, W weight)
		{ 
			this.target = target;
			this.weight = weight;
		}
		
		Vertex target() {
      return target;
    }
		
		public void setWeight(W weight) {
      this.weight = weight;
    }
		
		public W weight() {
      return weight;
    }
    
    @Override
    public String toString() {
      return "{ target : \"" + target + "\", weight : \"" + weight + "\" }";
    }
		
		@Override
		public boolean equals(Object right)
		{
			//System.out.println(target + ".equals(" + ((Edge) right).target + ")");
			if(target == ((Edge<W>) right).target) {
        return true;
      }
      
			if(!(((Edge<W>) right).target instanceof Vertex)) {
        return false;
      }
      
			return this.compareTo((Edge<W>) right) == 0;
		}
    
    @Override
		public int compareTo(Edge<W> right) {
			return weight.compareTo(right.weight());
		}
	}

	public Vertex(int id, L label)
	{
		this.id = id;
    this.color = 0;
		this.label = label;
		this.edges = new ArrayList<Edge<W>>();
    hashcode = Objects.hash(id, indegree, outdegree);
	}
  
	/**
	 * Set the node label
	 * @param label
	 */
	
	
	public int getId() {
    return id;
  }
  
  public int getColor() {
    return color;
  }
  
  public void setColor(int color) {
    this.color = color;
  }
	
	public L getLabel() { return label; }
	
	public long getInDegree() { return indegree; }
	
	public long getOutDegree() { return outdegree; }
	
	public boolean isEmpty() { return outdegree == 0; }
	
	public boolean isSingleton() { return indegree == 0 && isEmpty(); }
	
	public String toString() {
    return label.toString();
  }
	
	public boolean adopt(Vertex target, W weight)
	{
		// System.out.println(this + ".adopt(" + target + ", " + weight + ")");
		boolean added = edges.add(new Edge<W>(target, weight));
    if(added) {
      outdegree++;
		  target.indegree++;
    }
    
    // System.out.println("edges = " + edges);
    
    return added;
		
	}
	
	public boolean orphan(Edge<W> edge)
	{
		//System.out.println(this + ".orphan(" + edge.getTargetNode() + "");
		boolean removed = edges.remove(edge);
    
    if(removed) {
      edge.target().indegree--;
		  outdegree--;
    }

		//System.out.println("done");
		return removed;
	}
	
	public boolean orphan(Vertex query)
	{
		
		
		/*
		if(edges.contains(proxy))
		{
			System.out.println(target + " proxy found: " + proxy.getTargetNode());
		}
		else
		{
			// System.out.println("proxy not found");
		}
		*/
		
		
		//Edge proxy = new Edge<?>(target, weight);
		//return orphan(proxy);
		
		
		
		Iterator<Edge<W>> iterator = edges.iterator();
		while(iterator.hasNext())
		{
			Edge edge = iterator.next();
			//System.out.println(label + " orphan " + edge.getTargetNode().getL());
			if(edge.target().equals(query))
			{
				// System.out.println("Yes");
				orphan(edge);
				return true; // found
			}
			else
			{
				// System.out.println("No");
			}
		}
		
		return false;
		
		
	}

  @Override
	public Iterator<Vertex.Edge<W>> iterator()
	{
    Iterator<Edge<W>> edge = edges.iterator();
		return edge;
	}
  
  @Override
  public int hashCode() {
    return hashcode;
  }
  
  @Override
  public boolean equals(final Object right) {
    if(right == this) {
      return true;
    }
    
    if(!(right instanceof Vertex)) {
      return false;
    }
    
    Vertex vertex = (Vertex) right;
    return Objects.equals(id, vertex.getId())
        && Objects.equals(label, vertex.getLabel());
                
  }
  
}