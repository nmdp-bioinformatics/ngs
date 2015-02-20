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
import java.util.BitSet;

/**
 * Extent adder.
 */
public class ExtentAdder<O, A> extends LatticePruner<O, A> {
    private O object;
    private BitSet extent;

    protected static abstract class Init<O, A, T extends Init<O, A, T>> extends LatticePruner.Init<O, A, T> {
        private O object;

        public T withObject(final O object) {
            this.object = object;
            return self();
        }

        public ExtentAdder build() {
            return new ExtentAdder(this);
        }
    }

    public static class Builder<O, A> extends Init<O, A, Builder<O, A>> {
        @Override
        protected Builder self() {
            return this;
        }
    }

    protected ExtentAdder(final Init<O, A, ?> init) {
        super(init);
        this. object = init.object;
        List<O> list = new ArrayList<O>();
        list.add(object);
        extent = Concept.encode(list, objects);
        go = Lattice.Direction.DOWN;
    }

    public boolean pruneVertex(final Vertex vertex) {
        if (super.pruneVertex(vertex)) {
            return true;
        }

        Vertex<Concept, Long> source = vertex;
        source.getLabel().extent().or(extent);
        return false;
    }
}
