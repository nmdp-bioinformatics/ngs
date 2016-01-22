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
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.dishevelled.bitset.MutableBitSet;
import org.dishevelled.bitset.ImmutableBitSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

import org.junit.rules.ExpectedException;

/**
 * Unit test for Concept.
 */
public final class ConceptTest {
    //private MutableBitSet w, x, y, z;
    private Concept S, T, U, V, W, X;
    private List s, t, u, v, w, x, a, b, ab, af, bd, acef, abdf, abde, abdefg, abcdefg, STUVWX;
    
    private List objects, attributes;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        abcdefg = list("a", "b", "c", "d", "e", "f", "g");
        STUVWX = list("S", "T", "U", "V", "W", "X");

        a = new ImmutableList.Builder<String>().add("a").build();
        b = new ImmutableList.Builder<String>().add("B").build();
        ab = new ImmutableList.Builder<String>().add("A").add("B").build();
        //empty = new ImmutableList.Builder<String>().build();

        objects = new ImmutableList.Builder<String>()
            .add("S").add("T").add("U").add("V").add("W").add("X").build();
        attributes = new ImmutableList.Builder<String>()
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

        s = new ImmutableList.Builder<String>().add("S").build();
        t = new ImmutableList.Builder<String>().add("T").build();
        u = new ImmutableList.Builder<String>().add("U").build();
        v = new ImmutableList.Builder<String>().add("V").build();
        w = new ImmutableList.Builder<String>().add("W").build();
        x = new ImmutableList.Builder<String>().add("X").build();

        S = Concept.builder().withObjects(s, objects)
                             .withAttributes(abdf, attributes)
                             .build();

        T = Concept.builder().withObjects(t, objects)
                             .withAttributes(abde, attributes)
                             .build();

        U = Concept.builder().withObjects(u, objects)
                             .withAttributes(abdefg, attributes)
                             .build();

        V = Concept.builder().withObjects(v, objects)
                             .withAttributes(acef, attributes)
                             .build();

        W = Concept.builder().withObjects(w, objects)
                             .withAttributes(bd, attributes)
                             .build();

        X = Concept.builder().withObjects(x, objects)
                             .withAttributes(af, attributes)
                             .build();
    }

    @Test
    public void testBuilder() {
        //System.out.println("assertEquals(" + Concept.decode(S.extent(), objects) + ", flip(" + Concept.decode(flip(new MutableBitSet(), 0), objects) + ")");
        Concept empty = Concept.builder().build();
        assertEquals(empty.extent(), bits());
        assertEquals(empty.intent(), bits());

        assertEquals(S.extent(), bits(0));
        assertEquals(S.intent(), bits(0, 1, 3, 5));
    }

    @Test
    public void testDecode() {
        assertEquals(Concept.decode(bits(), abcdefg), list());
        assertEquals(Concept.decode(bits(0), abcdefg), list("a"));
        assertEquals(Concept.decode(bits(1, 2), abcdefg), list("b", "c"));

        exception.expect(java.lang.IndexOutOfBoundsException.class);
        Concept.decode(bits(7), abcdefg);
    }

    @Test
    public void testEncode() {
        assertEquals(Concept.encode(list(), abcdefg), bits());
        assertEquals(Concept.encode(list("a"), abcdefg), bits(0));
        assertEquals(Concept.encode(list("b", "c"), abcdefg), bits(1, 2));
        assertEquals(Concept.encode(list("h"), abcdefg), bits());
    }

    @Test
    public void testLessThan() {
        assertTrue(X.isLessThan(S));
    }

    @Test
    public void testGreaterThan() {
        assertTrue(S.isGreaterThan(X));
    }

    @Test
    public void testNoncomparableTo() {
        assertTrue(W.isNonComparableTo(X));
    }

    @Test
    public void testGreaterOrEqualTo() {
        assertTrue(X.isGreaterOrEqualTo(X));
        assertFalse(X.isGreaterOrEqualTo(S));
        assertTrue(S.isGreaterOrEqualTo(X));
        assertFalse(W.isGreaterOrEqualTo(X));
    }

    @Test
    public void testLessOrEqualTo() {
        assertTrue(X.isLessOrEqualTo(X));
        assertTrue(X.isLessOrEqualTo(S));
        assertFalse(S.isLessOrEqualTo(X));
        assertFalse(W.isLessOrEqualTo(X));
    }

    @Test
    public void testEquals() {
        assertTrue(X.equals(X));
        assertFalse(W.equals(X));
    }

    @Test
    public void testIntersect() {
        Concept intersect = S.intersect(T);
        assertEquals(intersect.extent(), bits());
        assertEquals(intersect.intent(), bits(0, 1, 3));

        intersect = T.intersect(S);
        assertEquals(intersect.intent(), bits(0, 1, 3));

        intersect = W.intersect(W);
        assertEquals(intersect.intent(), W.intent());

        intersect = W.intersect(X);
        assertEquals(intersect.intent(), bits());

        intersect = X.intersect(S);
        assertEquals(intersect.intent(), X.intent());
    }

    @Test
    public void testUnion() {
        Concept union = S.union(T);
        assertEquals(union.extent(), bits(0, 1));
        assertEquals(union.intent(), S.intent());
    }

    @Test
    public void testMeasure() {
        assertEquals(1.0, S.measure(), 0.0);
    }
}
