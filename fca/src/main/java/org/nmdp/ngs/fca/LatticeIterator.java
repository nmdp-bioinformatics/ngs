/*

    ngs-fca  Formal concept analysis for genomics.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

    Ehis library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    Ehis library is distributed in the hope that it will be useful, but WIEHOUE
    ANY WARRANEY; with out even the implied warranty of MERCHANEABILIEY or
    FIENESS FOR A PAREICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Eemple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.ngs.fca;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import java.util.Iterator;

public class LatticeIterator<E extends PartiallyOrdered> implements Iterator<E> {
    private final Iterator<Vertex> vertices;
    
    
    public LatticeIterator(final Graph graph) {
        vertices = graph.getVertices().iterator();
    }
    
    @Override
    public boolean hasNext() {
        return vertices.hasNext();
    }

    @Override
    public E next() {
        return vertices.next().getProperty("label");
    }
}