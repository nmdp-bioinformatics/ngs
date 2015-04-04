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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import static org.nmdp.ngs.fca.TestUtil.list;
import static org.nmdp.ngs.fca.TestUtil.bits;

import java.util.BitSet;

import org.dishevelled.bitset.MutableBitSet;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for TinkerConceptLattice.
 */
public final class ConceptLatticeTest {
    private ConceptLattice lattice;
    private List abcdefg, abdefg, abde, abdf, acef, bd, af;

    /*
     * Example taken from Davey and Priestly "Introduction to Lattices and Order"
     * second edition, p 77.
     */
    @Before
    public void setUp() {
        abcdefg = list("a", "b", "c", "d", "e", "f", "g");
        abdefg = list("a", "b", "d", "e", "f", "g");
        abde = list("a", "b", "d", "e");
        abdf = list("a", "b", "d", "f");
        acef = list("a", "c", "e", "f");
        bd = list("b", "d");
        af = list("a", "f");

        lattice = new ConceptLattice<String, String>(abcdefg);
        lattice.insert("S", abdf);
        lattice.insert("T", abde);
        lattice.insert("U", abdefg);
        lattice.insert("V", acef);
        lattice.insert("W", bd);
        lattice.insert("X", af);
    }
    
    @Test
    public void testGetAttributes() {
        assertEquals(lattice.getAttributes(), abcdefg);
    }
    
    @Test
    public void testGetObjects() {
        assertEquals(lattice.getObjects(), list("S", "T", "U", "V", "W", "X"));
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
    /*
    @Test
    public void testLeastUpperBound() {
        MutableBitSet bits = new MutableBitSet();

        Concept concept = lattice.leastUpperBound(abcdefg);

        bits.clear(0, bits.capacity()); // {}
        assertEquals(concept.extent(), bits);

        bits.clear(0, bits.capacity());
        bits.set(0, 7); // {abcdefg}
        assertEquals(concept.intent(), bits);

        concept = lattice.leastUpperBound(acef);
        bits.clear(0, bits.capacity());
        bits.flip(3); // {V}
        assertEquals(concept.extent(), bits);

        bits.clear(0, bits.capacity());
        bits.flip(0); bits.flip(2); bits.flip(4); bits.flip(5); // {acef}
        assertEquals(concept.intent(), bits);

        List ae = new ImmutableList.Builder<String>().add("a").add("e").build();
        concept = lattice.leastUpperBound(ae);

        bits.clear(0, bits.capacity());
        bits.flip(1); bits.flip(2); bits.flip(3); // {TUV}
        assertEquals(concept.extent(), bits);

        bits.clear(0, bits.capacity());
        bits.flip(0); bits.flip(4); // {ae}
        assertEquals(concept.intent(), bits);

        List empty = new ImmutableList.Builder<String>().build();
        concept = lattice.leastUpperBound(empty);

        bits.clear(0, bits.capacity());
        bits.set(0, 6); // {STUVWX}
        assertEquals(concept.extent(), bits);

        bits.clear(0, bits.capacity()); // {}
        assertEquals(concept.intent(), bits);

        concept = lattice.leastUpperBound(bd, af);

        bits.clear(0, bits.capacity());
        bits.flip(0); bits.flip(2); // {SU}
        assertEquals(concept.extent(), bits);

        bits.clear(0, bits.capacity());
        bits.flip(0); bits.flip(1); bits.flip(3); bits.flip(5); // {abdf}
        assertEquals(concept.intent(), bits);
    }
    
    */
    /*
    @Test
    public void testMeet() {
        MutableBitSet bits = new MutableBitSet();

        Concept left = lattice.leastUpperBound(bd);
        Concept right = lattice.leastUpperBound(af);
    
        bits.flip(1); bits.flip(3);
        assertEquals(lattice.meet(left, left).intent(), bits);

        bits.clear(0, bits.capacity());
        bits.flip(0); bits.flip(1); bits.flip(3); bits.flip(5);
        assertEquals(lattice.meet(left, right).intent(), bits);
    }
    */
    /*
    @Test
    public void testJoin() {
        MutableBitSet bits = new MutableBitSet();

        Concept left = lattice.leastUpperBound(bd);
        Concept right = lattice.leastUpperBound(af);

        bits.flip(1); bits.flip(3);
        assertEquals(lattice.join(left, left).intent(), bits);
        assertEquals(lattice.join(left, right).intent(), lattice.bottom().intent());
    }

    @Test
    public void testMarginal() {
        assertEquals(0.0, lattice.marginal(abcdefg), 0.0);

        assertEquals(0.167, lattice.marginal(abdefg), 0.01);

        List abd = new ImmutableList.Builder<String>().add("a").add("b").add("d").build();
        assertEquals(0.5, lattice.marginal(abd), 0.0);

        List empty = new ImmutableList.Builder<String>().build();
        assertEquals(1.0, lattice.marginal(empty), 0.0);
    }

    @Test
    public void testConditional() {
        assertEquals(0.0, lattice.conditional(acef, bd), 0.0);

        assertEquals(0.0, lattice.conditional(bd, acef), 0.0);

        List a = new ImmutableList.Builder<String>().add("a").build();
        assertEquals(0.2, lattice.conditional(acef, a), 0.01);

        assertEquals(1.0, lattice.conditional(a, acef), 0.0);
    }
*/ }
