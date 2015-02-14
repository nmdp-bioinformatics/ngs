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

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Class for pruning labeled vertexes and weighted edges.
 * @param <L> label type for vertexes
 * @param <W> weight type for edges
 */
public class Pruner<L, W> {
	protected Vertex parent;
	protected List<L> labels;
	protected List<W> weights;
  /**
   * Abstract class for constructing polymorphic pruners using the builder
   * pattern.
   * @param <L> label type for vertexes
   * @param <W> weight type for edges
   * @param <T> builder type for heritable pruning methods
   */
  protected static abstract class Init<L, W, T extends Init<L, W, T>> {
    private List<L> labels;
    private List<W> weights;
    /**
     * 
     * @return initialized object 
     */
    protected abstract T self();
    /**
     * 
     * @param labels for pruning vertexes
     * @return object with set labels
     */
    public T withLabels(final List<L> labels) {
      this.labels= labels;
      return self();
    }
    /**
     * 
     * @param weights for pruning edges
     * @return object with set weights
     */
    public T withWeights(final List<W> weights) {
      this.weights = weights;
      return self();
    }
    /**
     * 
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
   * Construct a new pruner from a initializer.
   * @param init 
   */
  protected Pruner(Init<L, W, ?> init) {
    this.labels = init.labels == null ? new ArrayList<L>() : init.labels;
    this.weights = init.weights == null ? new ArrayList<W>() : init.weights;
  }
	/**
   * Construct a default pruner with empty labels and weights.
   */
	public Pruner()
	{
		labels = new ArrayList<L>();
		weights = new ArrayList<W>();
	}
  /**
   * Get the vertex currently visited by this pruner.
   * @return parent vertex
   */
  public Vertex getParent() {
    return parent;
  }
  /**
   * Get the pruning labels.
   * @return list of labels
   */
  public List<L> getLabels() {
    return labels;
  }
  /**
   * Get the pruning weights.
   * @return list of weights
   */
  public List<W> getWeights() {
    return weights;
  }
	/**
   * 
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
   * 
   * @param edge considered for pruning
   * @return true if edge or target should be pruned
   */
	public boolean pruneEdge(Vertex.Edge edge)
	{
		if(weights.contains(edge.weight()))
		{
			return true;
		}
		return false;
	}
}