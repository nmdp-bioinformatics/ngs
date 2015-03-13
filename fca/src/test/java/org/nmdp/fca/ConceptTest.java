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

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Concept.
 */
public final class ConceptTest {
    private BitSet w, x, y, z;
    private Concept W, X, Y, Z;
    private List a, b, ab, empty;

    @Before
    public void setUp() {
        w = new BitSet(2); // empty
        x = new BitSet(2); x.flip(0); // a
        y = new BitSet(2); y.flip(1); // b
        z = new BitSet(2); z.flip(0); z.flip(1); // ab

        W = new Concept(null, w);
        X = new Concept(null, x);
        Y = new Concept(null, y);
        Z = new Concept(null, z);

        a = new ImmutableList.Builder<String>().add("A").build();
        b = new ImmutableList.Builder<String>().add("B").build();
        ab = new ImmutableList.Builder<String>().add("A").add("B").build();
        empty = new ImmutableList.Builder<String>().build();
    }

    @Test
    public void testDecode() {
        assertEquals(Concept.decode(w, b), empty);
        assertEquals(Concept.decode(x, ab), a);
        assertEquals(Concept.decode(y, ab), b);
        assertEquals(Concept.decode(z, ab), ab);
    }

    @Test
    public void testEncode() {
        assertEquals(Concept.encode(empty, ab), w);
        assertEquals(Concept.encode(a, ab), x);
        assertEquals(Concept.encode(b, ab), y);
        assertEquals(Concept.encode(ImmutableList.copyOf(ab), ab), z);
    }

    @Test
    public void testOrdering() {
        assertEquals(W.ordering(W), Partial.Ordering.EQUAL);
        assertEquals(W.ordering(X), Partial.Ordering.LESS);
        assertEquals(W.ordering(Y), Partial.Ordering.LESS);
        assertEquals(W.ordering(Z), Partial.Ordering.LESS);

        assertEquals(X.ordering(W), Partial.Ordering.GREATER);
        assertEquals(X.ordering(X), Partial.Ordering.EQUAL);
        assertEquals(X.ordering(Y), Partial.Ordering.NONCOMPARABLE);
        assertEquals(X.ordering(Z), Partial.Ordering.LESS);

        assertEquals(Y.ordering(W), Partial.Ordering.GREATER);
        assertEquals(Y.ordering(X), Partial.Ordering.NONCOMPARABLE);
        assertEquals(Y.ordering(Y), Partial.Ordering.EQUAL);
        assertEquals(Y.ordering(Z), Partial.Ordering.LESS);

        assertEquals(Z.ordering(W), Partial.Ordering.GREATER);
        assertEquals(Z.ordering(X), Partial.Ordering.GREATER);
        assertEquals(Z.ordering(Y), Partial.Ordering.GREATER);
        assertEquals(Z.ordering(Z), Partial.Ordering.EQUAL);
    }

    @Test
    public void testGte() {
        assertTrue(W.ordering(W).gte());
        assertFalse(W.ordering(X).gte());
        assertFalse(W.ordering(Y).gte());
        assertFalse(W.ordering(Z).gte());

        assertTrue(X.ordering(W).gte());
        assertTrue(X.ordering(X).gte());
        assertFalse(X.ordering(Y).gte());
        assertFalse(X.ordering(Z).gte());

        assertTrue(Y.ordering(W).gte());
        assertFalse(Y.ordering(X).gte());
        assertTrue(Y.ordering(Y).gte());
        assertFalse(Y.ordering(Z).gte());

        assertTrue(Z.ordering(W).gte());
        assertTrue(Z.ordering(X).gte());
        assertTrue(Z.ordering(Y).gte());
        assertTrue(Z.ordering(Z).gte());
    }

    @Test
    public void testLte() {
        assertTrue(W.ordering(W).lte());
        assertTrue(W.ordering(X).lte());
        assertTrue(W.ordering(Y).lte());
        assertTrue(W.ordering(Z).lte());

        assertFalse(X.ordering(W).lte());
        assertTrue(X.ordering(X).lte());
        assertFalse(X.ordering(Y).lte());
        assertTrue(X.ordering(Z).lte());

        assertFalse(Y.ordering(W).lte());
        assertFalse(Y.ordering(X).lte());
        assertTrue(Y.ordering(Y).lte());
        assertTrue(Y.ordering(Z).lte());

        assertFalse(Z.ordering(W).lte());
        assertFalse(Z.ordering(X).lte());
        assertFalse(Z.ordering(Y).lte());
        assertTrue(Z.ordering(Z).lte());
    }
}
