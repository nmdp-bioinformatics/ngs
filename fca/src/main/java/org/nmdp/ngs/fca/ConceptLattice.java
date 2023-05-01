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

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import org.dishevelled.bitset.MutableBitSet;

/**
 * Concept lattice.
 */
public class ConceptLattice extends CompleteLattice<Concept> {
    
    private static MutableBitSet ones(long numBits) {
        MutableBitSet ones = new MutableBitSet(numBits);
        ones.set(0, numBits);
        return ones;
    }
    
    public ConceptLattice(final Graph graph, long numBits) {
        super(graph, new Concept(new MutableBitSet(), ones(numBits)));
    }
    
    public ConceptLattice(long numBits) {
        super(new Concept(new MutableBitSet(), ones(numBits)));
    }
    
    public Concept insert(final Concept concept) {
        Vertex added = super.addIntent(concept, top);

        List queue = new ArrayList();
        added.setProperty(COLOR, ++color);
        queue.add(added);

        while (!queue.isEmpty()) {
            Vertex visiting = (Vertex) queue.remove(0);
            Concept visitingConcept = visiting.getProperty(LABEL);
            visitingConcept.extent().or(concept.extent());

            for (Edge edge : visiting.getEdges(Direction.BOTH)) {
                Vertex target = edge.getVertex(Direction.OUT);

                if ((int) target.getProperty(COLOR) != color) {
                    if (filter(visiting, target)) {
                        target.setProperty(COLOR, color);
                        queue.add(target);
                    }
                }
            }
        }
        return added.getProperty(LABEL);
    }
    
    @Override
    public final Concept join(final Concept left, final Concept right) {
        MutableBitSet bits = (MutableBitSet) new MutableBitSet().or(left.intent()).or(right.intent());
        Concept query = new Concept(new MutableBitSet(), bits);
        return supremum(query, top).getProperty(LABEL);
    }
}
