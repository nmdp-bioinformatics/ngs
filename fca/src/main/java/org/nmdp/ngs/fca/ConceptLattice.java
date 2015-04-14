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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import org.dishevelled.bitset.MutableBitSet;

/**
 * Concept lattice.
 *
 * @param <G> object type
 * @param <M> attribute type
 */
public class ConceptLattice<G, M> extends AbstractLattice<Concept> {
    protected List<G> objects;
    protected List<M> attributes;

    public ConceptLattice(final Graph lattice, final List<M> attributes) {
        super(lattice);
        objects = new ArrayList<>();
        this.attributes = attributes;
        MutableBitSet ones = new MutableBitSet(this.attributes.size());
        ones.set(0, this.attributes.size());
        top = lattice.addVertex(null);
        top.setProperty("label", new Concept(new MutableBitSet(), ones));
        top.setProperty("color", color);
        direction = Partial.Order.Direction.FORWARD;
        size = 1;
        order = 0;
        bottom = top;
    }

    public Concept insert(final G object, final List<M> attributes) {
        objects.add(object);
        MutableBitSet intent = Concept.encode(attributes, this.attributes);
        Concept proposed = new Concept(new MutableBitSet(), intent);
        Vertex added = super.addIntent(proposed, top);

        List<G> list = new ArrayList<G>();
        list.add(object);
        MutableBitSet extent = Concept.encode(list, objects);

        List queue = new ArrayList();
        added.setProperty("color", ++color);
        queue.add(added);

        while (!queue.isEmpty()) {
            Vertex visiting = (Vertex) queue.remove(0);
            Concept visitingConcept = visiting.getProperty(LABEL);
            visitingConcept.extent().or(extent);

            for (Edge edge : visiting.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);

                if ((int) target.getProperty("color") != color) {
                    if (filter(visiting, target)) {
                        target.setProperty("color", color);
                        queue.add(target);
                    }
                }
            }
        }
        return added.getProperty(LABEL);
    }

    // todo:  this should be typed
    public final List getObjects() {
        return objects;
    }

    public List<M> getAttributes() {
        return attributes;
    }

    @Override
    public final Concept join(final Concept left, final Concept right) {
        MutableBitSet bits = (MutableBitSet) new MutableBitSet().or(left.intent()).or(right.intent());
        Concept query = new Concept(new MutableBitSet(), bits);
        return supremum(query, top).getProperty(LABEL);
    }

    public Concept greatestLowerBound(final List query) {
        // todo:  support this method
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("digraph {\n");
        for (Vertex vertex : lattice.getVertices()) {
            for (Edge edge : vertex.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);
                if (!vertex.getProperty("label").equals(target.getProperty("label"))) {
                    Concept vertexConcept = vertex.getProperty("label");
                    Concept targetConcept = target.getProperty("label");
                    if (!filter(vertex, target)) {
                        sb.append("  \"" + Concept.decode(vertexConcept.extent(), objects) + Concept.decode(vertexConcept.intent(), attributes) + "\" -> \"" + Concept.decode(targetConcept.extent(), objects) + Concept.decode(targetConcept.intent(), attributes) + "\"[label=\"" + edge.getLabel() + "\"]\n");
                    }
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
