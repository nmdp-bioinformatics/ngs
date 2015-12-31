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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Edge;

public class IntervalLattice<C extends Comparable<?>> extends AbstractLattice<Interval<C>> {
    
    public IntervalLattice(final Graph graph) {
        super(graph);
        
        top.setProperty(LABEL, Interval.MAGIC);
        top.setProperty(COLOR, color);
    }
    
    public Interval<C> insert(final Interval<C> interval) {
        return super.addIntent(interval, top).getProperty(LABEL);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("digraph {\n");
        for (Vertex vertex : lattice.getVertices()) {
            for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);
                if (!vertex.getProperty("label").equals(target.getProperty("label"))) {
                    Interval<C> vertexConcept = vertex.getProperty("label");
                    Interval<C> targetConcept = target.getProperty("label");
                    if (!filter(vertex, target)) {
                        sb.append("  \"" + vertexConcept + "\" -> \"" + targetConcept + "\"[label=\"" + edge.getLabel() + "\"]\n");
                    }
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
}