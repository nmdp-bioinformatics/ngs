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

import com.google.common.collect.ImmutableList;
import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.fca.TestUtil.list;
import static org.nmdp.ngs.fca.TestUtil.bits;

import java.util.List;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import org.dishevelled.bitset.MutableBitSet;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for ConceptLattice.
 */
public final class ConceptLatticeTest {
    private ConceptLattice lattice;
    
    /*
     * From Davey and Priestly "Introduction to Lattices and Order" second
     * edition, page 77.
     */
    @Before
    public void setUp() {
        lattice = new ConceptLattice(new TinkerGraph(), 7);

        lattice.insert(new Concept(bits(0), bits(0, 1, 3, 5)));
        lattice.insert(new Concept(bits(1), bits(0, 1, 3, 4)));
        lattice.insert(new Concept(bits(2), bits(0, 1, 3, 4, 5, 6)));
        lattice.insert(new Concept(bits(3), bits(0, 2, 4, 5)));
        lattice.insert(new Concept(bits(4), bits(1, 3)));
        lattice.insert(new Concept(bits(5), bits(0, 5)));
    }

    @Test
    public void testInsert() {
        Concept top = (Concept) lattice.top();
        Concept bottom = (Concept) lattice.bottom();

        assertEquals(top.intent(), bits(0, 1, 2, 3, 4, 5, 6));
        assertEquals(top.extent(), bits());

        assertEquals(bottom.intent(), bits());
        assertEquals(bottom.extent(), bits(0, 1, 2, 3, 4, 5));
        assertEquals(lattice.size(), 12);
    }
    
    @Test
    public void testIterator() {
        int count = 0;
        for(Concept concept : lattice) { count++; }
        assertEquals(count, 12);
    }

    @Test
    public void testMeet() {
        Concept left = (Concept) lattice.meet(lattice.top(), new Concept(bits(), bits(1, 3)));
        Concept right = (Concept) lattice.meet(lattice.top(), new Concept(bits(), bits(0, 5)));

        assertEquals(left.intent(), bits(1, 3));
        assertEquals(right.intent(), bits(0, 5));
        assertEquals(((Concept) lattice.meet(left, left)).intent(), bits(1, 3));
        assertEquals(((Concept) lattice.meet(left, right)).intent(), bits());
    }

    @Test
    public void testJoin() {
        Concept left = (Concept) lattice.join(lattice.bottom(), new Concept(bits(), bits(1, 3)));
        Concept right = (Concept) lattice.join(lattice.bottom(), new Concept(bits(), bits(0, 5)));

        assertEquals(left.intent(), bits(1, 3));
        assertEquals(right.intent(), bits(0, 5));
        assertEquals(((Concept) lattice.join(left, left)).intent(), bits(1, 3));
        assertEquals(((Concept) lattice.join(left, right)).intent(), bits(0, 1, 3, 5));
    }

    @Test
    public void testMeasure() {
        Concept V = new Concept(bits(3), bits(0, 2, 4, 5));
        Concept W = new Concept(bits(4), bits(1, 3));
        Concept Z = new Concept(bits(6), bits(0));

        assertEquals(0.0, lattice.measure(V, W), 0.0);
        assertEquals(0.0, lattice.measure(W, V), 0.0);
        assertEquals(0.2, lattice.measure(V, Z), 0.01);
        assertEquals(1.0, lattice.measure(Z, V), 0.0);
    }
    
    @Test
    public void testSize() {
        assertEquals(new ConceptLattice(new TinkerGraph(), 7).size(), 1);
        assertEquals(lattice.size(), 12);
    }
    
    @Test
    public void testIsEmpty() {
        assertTrue(new ConceptLattice(new TinkerGraph(), 7).isEmpty());
        assertFalse(lattice.isEmpty());
    }
    
    @Test
    public void testFind() {
        Concept element = new Concept(bits(), bits(0, 2, 4, 5));
        Concept found = lattice.find(element);
        
        assertEquals(found.intent(), element.intent());
        assertEquals(found.extent(), bits(3));
        assertEquals(lattice.find(new Concept(bits(), bits())), lattice.bottom());
        assertEquals(lattice.find(new Concept(bits(), bits(0, 1, 2, 3, 4, 5, 6))), lattice.top());
        assertEquals(lattice.find(new Concept(bits(), bits(0, 2, 4))).intent(), element.intent());
    }
    
    @Test
    public void testContains() {
        Concept element = new Concept(bits(), bits(0, 2, 4, 5));
        
        assertFalse(lattice.contains(element));
        assertFalse(lattice.contains(new Concept(bits(3), bits(0, 2, 4))));
        assertTrue(lattice.contains(new Concept(bits(3), element.intent())));
        assertTrue(lattice.contains(new Concept(bits(0, 1, 2, 3, 4, 5), bits())));
        assertTrue(lattice.contains(new Concept(bits(), bits(0, 1, 2, 3, 4, 5, 6))));
    }
    
    @Test
    public void testCovers() {
        Concept STUW = new Concept(bits(0, 1, 2, 4), bits(1, 3));
        Concept STU = new Concept(bits(0, 1, 2), bits(0, 1, 3));
        Concept SU = new Concept(bits(0, 2), bits(0, 1, 3, 5));
        
        assertFalse(lattice.covers(STUW, STU));
        assertTrue(lattice.covers(STU, STUW));
        assertTrue(lattice.covers(SU, STU));
        assertFalse(lattice.covers(SU, STUW));
        assertFalse(lattice.covers(STUW, STUW));
    }
    
    @Test
    public void testToArray() {
        Object[] concepts = lattice.toArray();
        assertEquals(concepts.length, 12);
        assertTrue(concepts[0] instanceof Concept);
    }
}
