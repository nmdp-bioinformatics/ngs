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

import java.lang.StringBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LatticeWriter extends LatticePruner {
  StringBuilder builder = new StringBuilder();
  private String filepath;
  
  protected static abstract class Init<O, A, T extends Init<O, A, T>> extends LatticePruner.Init<O, A, T> {
    private String filepath;
    
    public T toFile(final String filepath) {
      this.filepath = filepath;
      return self();
    }
    
    public LatticePruner build() {
      return new LatticeWriter(this);
    }
  }
  
  public static class Builder<O, A> extends Init<O, A, Builder<O, A>> {
    @Override
    protected Builder self() {
      return this;
    }
  }
  
  protected LatticeWriter(Init<?, ?, ?> init) {
    super(init);
    this.filepath = init.filepath;
    builder.append("digraph").append(" {\n");
  }
  
  @Override
  public boolean pruneEdge(Vertex.Edge edge) {
    if(super.pruneEdge(edge)) {
      return true;
    }
    
    Vertex<Concept, Long> source = parent;
    Vertex<Concept, Long> target = edge.target();
    builder.append("  \"")
           .append(decodeIntent(source.getLabel()))
           .append("\" -> \"")
           .append(decodeIntent(target.getLabel()))
           .append("\"[label=\"")
           .append(edge.weight())
           .append("\"];\n");
    
		return false;
	}
  
  public void write() throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath));
    bufferedWriter.write(builder.append("}").toString());
    bufferedWriter.close();
  }
  
  @Override
  public String toString() {
    return builder.append("}").toString();
  }
}