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
/**
 * Class for pruning labeled vertexes and weighted edges. Pruners are the main
 * mechanism for performing read-only operations on graphs.
 * @param <L> label type for vertexes
 * @param <W> weight type for edges
 */
public class Pruner<L, W> {
  protected Path path;
	protected Vertex parent, clone;
	protected List<L> labels;
	protected List<W> weights;
  protected boolean remember;
  /**
   * Abstract class for constructing polymorphic pruners with a builder-like
   * pattern.
   * @param <L> label type for vertexes
   * @param <W> weight type for edges
   * @param <T> builder type for heritable pruning methods
   */
  protected static abstract class Init<L, W, T extends Init<L, W, T>> {
    private List<L> labels;
    private List<W> weights;
    private boolean remember;
    /**
     * Abstract method that returns the current object. Used instead of this in
     * the normal builder pattern.
     * @return instantiated, partially initialized builder
     */
    protected abstract T self();
    /**
     * Method to initialize labels assigned for pruning.
     * @param labels for pruning vertexes
     * @return builder with set labels
     */
    public T withLabels(final List<L> labels) {
      this.labels= labels;
      return self();
    }
    /**
     * Method to initialize weights assigned for pruning.
     * @param weights for pruning edges
     * @return builder with set weights
     */
    public T withWeights(final List<W> weights) {
      this.weights = weights;
      return self();
    }
    /**
     * Method to initialize traversed path memory.
     * @return builder with memory set to true
     */
    public T rememberPath() {
      this.remember = true;
      return self();
    }
    /**
     * Method to build a fully initialized pruner.
     * @return new initialized pruner
     */
    public Pruner build() {
      return new Pruner(this);
    }
  }
  /**
   * Instantiatable class for building pruners.
   * @param <L> label type for vertexes
   * @param <W> weight type for edges
   */
  public static class Builder<L, W> extends Init<L, W, Builder<L, W>> {
    @Override
    protected Builder self() {
      return this;
    }
  }
  /**
   * Constructs a new pruner from an initializer.
   * @param init 
   */
  protected Pruner(Init<L, W, ?> init) {
    this();
    path = new Path();
    this.remember = init.remember;
    
    if(init.labels != null) {
      this.labels = init.labels;
    }
    
    if(init.weights != null) {
      this.weights = init.weights;
    }
  }
	/**
   * Constructs a default pruner that visits all vertexes and has no memory of
   * its traversed path.
   */
	public Pruner()
	{
    path = new Path();
    remember = false;
		labels = new ArrayList<L>();
		weights = new ArrayList<W>();
	}
  /**
   * Get the vertex currently visited.
   * @return parent vertex
   */
  public Vertex getParent() {
    return parent;
  }
  /**
   * Get the assigned pruned labels.
   * @return list of labels
   */
  public List<L> getLabels() {
    return labels;
  }
  /**
   * Get the assigned pruned weights.
   * @return list of weights
   */
  public List<W> getWeights() {
    return weights;
  }
	/**
   * Method for pruning visited vertexes.
   * @param vertex considered for pruning
   * @return true if vertex should be pruned
   */
	public boolean pruneVertex(Vertex vertex)
	{
		parent = vertex;
		
		if(labels.contains(vertex.getLabel()))
		{
			return true;
		}

		return false;
	}
	/**
   * Method for pruning edges and target vertexes. If the pruner is told it will
   * remember its traversed path by creating a new connected graph from shallow
   * copies of visited vertexes. Each new vertex is connected by edges to
   * (unpruned) target vertexes with corresponding weights from the original
   * graph.
   * @param edge considered for pruning
   * @return true if edge or target should be pruned
   */
	public boolean pruneEdge(Vertex.Edge edge)
	{
		if(weights.contains(edge.weight()))
		{
			return true;
		}
    
    if(remember) {
      clone = parent.shallowCopy();
      Vertex child = edge.target().shallowCopy();
      path.putEdge(clone, child, edge.weight());
    }
    
		return false;
	}
  /**
   * Method for retrieving the traversed path.  If the pruner has no memory the
   * result is a null graph with no vertexes and no edges.
   * @return a connected graph representing the traversed path
   */
  public Graph path() {
    return path;
  }
}