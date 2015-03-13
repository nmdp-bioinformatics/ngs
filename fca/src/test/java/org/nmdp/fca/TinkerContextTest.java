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

import java.util.BitSet;
import java.util.List;

import com.google.common.collect.ImmutableList;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for TinkerContext.
 */
public final class TinkerContextTest {
    private TinkerContext context;
    private List abcdefg, abdefg, abde, abdf, acef, bd, af;

    /**
     * Example taken from Davey and Priestly "Introduction to Lattices and Order"
     * second edition, p 77.
     */
    @Before
    public void setUp() {
        abcdefg = new ImmutableList.Builder<String>()
            .add("a").add("b").add("c").add("d").add("e").add("f").add("g").build();
        abdefg = new ImmutableList.Builder<String>()
            .add("a").add("b").add("d").add("e").add("f").add("g").build();
        abde = new ImmutableList.Builder<String>()
            .add("a").add("b").add("d").add("e").build();
        abdf = new ImmutableList.Builder<String>()
            .add("a").add("b").add("d").add("f").build();
        acef = new ImmutableList.Builder<String>()
            .add("a").add("c").add("e").add("f").build();
        bd = new ImmutableList.Builder<String>()
            .add("b").add("d").build();
        af = new ImmutableList.Builder<String>()
            .add("a").add("f").build();

        context = new TinkerContext(abcdefg);
        context.insert("S", abdf);
        context.insert("T", abde);
        context.insert("U", abdefg);
        context.insert("V", acef);
        context.insert("W", bd);
        context.insert("X", af);
    }

    @Test
    public void testInsert() {
        BitSet ones = new BitSet();
        BitSet zeros = new BitSet();

        ones.set(0, 6);
        assertEquals(context.bottom().intent(), zeros);
        assertEquals(context.bottom().extent(), ones);

        ones.set(0, 7);
        assertEquals(context.top().intent(), ones);
        assertEquals(context.top().extent(), zeros);
        assertEquals(context.size(), 12);
    }

    @Test
    public void testGetObjects() {
        List STUVWX = new ImmutableList.Builder<String>()
            .add("S").add("T").add("U").add("V").add("W").add("X").build();

        assertEquals(context.getObjects(), STUVWX);
    }

    @Test
    public void testGetAttributes() {
        assertEquals(context.getAttributes(), abcdefg);
    }

    @Test
    public void testLeastUpperBound() {
        BitSet bits = new BitSet();

        Concept concept = context.leastUpperBound(abcdefg);

        bits.clear(); // {}
        assertEquals(concept.extent(), bits);

        bits.clear();
        bits.set(0, 7); // {abcdefg}
        assertEquals(concept.intent(), bits);

        concept = context.leastUpperBound(acef);
        bits.clear();
        bits.flip(3); // {V}
        assertEquals(concept.extent(), bits);

        bits.clear();
        bits.flip(0); bits.flip(2); bits.flip(4); bits.flip(5); // {acef}
        assertEquals(concept.intent(), bits);

        List ae = new ImmutableList.Builder<String>().add("a").add("e").build();
        concept = context.leastUpperBound(ae);

        bits.clear();
        bits.flip(1); bits.flip(2); bits.flip(3); // {TUV}
        assertEquals(concept.extent(), bits);

        bits.clear();
        bits.flip(0); bits.flip(4); // {ae}
        assertEquals(concept.intent(), bits);

        List empty = new ImmutableList.Builder<String>().build();
        concept = context.leastUpperBound(empty);

        bits.clear();
        bits.set(0, 6); // {STUVWX}
        assertEquals(concept.extent(), bits);

        bits.clear(); // {}
        assertEquals(concept.intent(), bits);

        concept = context.leastUpperBound(bd, af);

        bits.clear();
        bits.flip(0); bits.flip(2); // {SU}
        assertEquals(concept.extent(), bits);

        bits.clear();
        bits.flip(0); bits.flip(1); bits.flip(3); bits.flip(5); // {abdf}
        assertEquals(concept.intent(), bits);
    }

    @Test
    public void testMeet() {
        BitSet bits = new BitSet();

        Concept left = context.leastUpperBound(bd);
        Concept right = context.leastUpperBound(af);
    
        bits.flip(1); bits.flip(3);
        assertEquals(context.meet(left, left).intent(), bits);

        bits.clear();
        bits.flip(0); bits.flip(1); bits.flip(3); bits.flip(5);
        assertEquals(context.meet(left, right).intent(), bits);
    }

    @Test
    public void testJoin() {
        BitSet bits = new BitSet();

        Concept left = context.leastUpperBound(bd);
        Concept right = context.leastUpperBound(af);

        bits.flip(1); bits.flip(3);
        assertEquals(context.join(left, left).intent(), bits);
        assertEquals(context.join(left, right).intent(), context.bottom().intent());
    }

    @Test
    public void testMarginal() {
        assertEquals(0.0, context.marginal(abcdefg), 0.0);

        assertEquals(0.167, context.marginal(abdefg), 0.01);

        List abd = new ImmutableList.Builder<String>().add("a").add("b").add("d").build();
        assertEquals(0.5, context.marginal(abd), 0.0);

        List empty = new ImmutableList.Builder<String>().build();
        assertEquals(1.0, context.marginal(empty), 0.0);
    }

    @Test
    public void testConditional() {
        assertEquals(0.0, context.conditional(acef, bd), 0.0);

        assertEquals(0.0, context.conditional(bd, acef), 0.0);

        List a = new ImmutableList.Builder<String>().add("a").build();
        assertEquals(0.2, context.conditional(acef, a), 0.01);

        assertEquals(1.0, context.conditional(a, acef), 0.0);
    }
}
