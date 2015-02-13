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

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Iterator;
/**
 * A connected graph where every vertex is connected to any other vertex through
 * at least one path.
 * @param <L> Label type of vertexes
 * @param <W> Weight type of edges
 */
public class ConnectedGraph<L, W extends Comparable> extends AbstractGraph<L, W> {
  /**
   * Construct a connected graph.
   * @param directed argument
   */
  public ConnectedGraph(boolean directed)
	{
		this.directed = directed;
		root = null;
		size = 0;
    order = 0;
	}
  /**
   * Default iterator performs a breadth first search without pruning.
   * @return GraphIterator with default constructed Pruner
   */
  @Override
  public GraphIterator iterator() {
    return new GraphIterator(++color, new Pruner<L, W>(), root);
  }
  /**
   * Iterator with customizable pruning.
   * @param pruner argument
   * @return GraphIterator with argument-defined Pruner
   */
  public GraphIterator iterator(Pruner pruner)
	{
		return new GraphIterator(++color, pruner, root);
	}
  /**
   * 
   * @return an empty builder ready to parameterize
   */
  public static Builder builder() {
    return new Builder();
  }
  /**
   * A class for building common kinds of graphs (mostly for testing). 
   * @param <L> Label type for vertexes
   * @param <W> weight type for edges
   */
  public static class Builder<L, W> {
    public enum Kind {
      /**
       * A graph with no vertexes and no edges
       */
      NULL,
      /**
       * A linear graph with no branches (effectively a linked list of vertexes)
       */
      PATH,
      /**
       * A graph with one internal vertex (root) that connects the others
       */
      STAR,
      /**
       * A path graph where the head and tail vertexes are connected
       */
      CYCLE,
      /**
       * A graph where each and vertex is connected to all other vertexes
       */
      COMPLETE
    }

    private int size;
    private Kind kind;
    private boolean directed;
    
    public Builder directed() {
      size = 0;
      kind = Kind.NULL;
      directed = true;
      return this;
    }
    
    public Builder path(int size) {
      this.size = size;
      kind = Kind.PATH;
      directed = false;
      return this;
    }
    
    public Builder star(int size) {
      this.size = size;
      kind = Kind.STAR;
      directed = false;
      return this;
    }
    
    public Builder cycle(int size) {
      this.size = size;
      kind = Kind.CYCLE;
      directed = false;
      return this;
    }
    

      
    public Graph build() {
      Graph graph = new ConnectedGraph(directed);
      
      if(kind == Kind.PATH) {
        Vertex source = graph.root();
        while(graph.size() < size) {
          source = graph.putVertex(source, graph.size(), 0);
        }
      } else if(kind == Kind.STAR) {
        while(graph.size() < size) {
          graph.putVertex(graph.size(), 0);
        }
      } else if(kind == Kind.CYCLE) {
        Vertex source = graph.root();
        while(graph.size() < size) {
          source = graph.putVertex(source, graph.size(), 0);
        }
        graph.putEdge(graph.root(), source, 0);
      }
      
      
      
      
      return graph;
    }
  }

}