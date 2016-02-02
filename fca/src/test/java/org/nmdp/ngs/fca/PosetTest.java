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
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.dishevelled.bitset.MutableBitSet;
import org.dishevelled.bitset.ImmutableBitSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class PosetTest {
    private Poset<String> A, B, C, AB, AD, ABC;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Before
    public void SetUp() {
        A = new Poset<>(new HashSet<>(Arrays.asList("a")));
        B = new Poset<>(new HashSet<>(Arrays.asList("b")));
        C = new Poset<>(new HashSet<>(Arrays.asList("c")));
        AB = new Poset<>(new HashSet<>(Arrays.asList("a", "b")));
        AD = new Poset<>(new HashSet<>(Arrays.asList("a", "d")));
        ABC = new Poset<>(new HashSet<>(Arrays.asList("a", "b", "c")));
    }
    
    @Test
    public void testIntersect() {
        assertEquals(A.intersect(B), Poset.NULL);
        assertEquals(A.intersect(A), A);
        assertEquals(ABC.intersect(AD), A);
        assertEquals(Poset.MAGIC.intersect(Poset.NULL), Poset.NULL);
    }
    
    @Test
    public void testLessThan() {
        assertFalse(new LessThan().apply(A, B));
        assertTrue(new LessThan().apply(A, AB));
        assertFalse(new LessThan().apply(AB, A));
        assertTrue(new LessThan().apply(Poset.NULL, A));
        assertFalse(new LessThan().apply(A, Poset.NULL));
        assertTrue(new LessThan().apply(ABC, Poset.MAGIC));
        assertFalse(new LessThan().apply(Poset.MAGIC, ABC));
    }
    
    @Test
    public void testComparable() {
        org.nmdp.ngs.fca.partial.Comparable comparable = new org.nmdp.ngs.fca.partial.Comparable();
        assertFalse(comparable.apply(A, B));
        assertFalse(comparable.apply(B, A));
        
        assertTrue(comparable.apply(A, AD));
        assertTrue(comparable.apply(AD, A));
        
        assertFalse(comparable.apply(C, AB));
        assertFalse(comparable.apply(AB, C));
        
        assertTrue(comparable.apply(ABC, Poset.MAGIC));
        assertTrue(comparable.apply(Poset.MAGIC, ABC));
        
        assertTrue(comparable.apply(A, Poset.NULL));
        assertTrue(comparable.apply(Poset.NULL, A));
        
        assertTrue(comparable.apply(Poset.NULL, Poset.MAGIC));
        assertTrue(comparable.apply(Poset.MAGIC, Poset.NULL));
    }
    
    @Test
    public void testSingletons() {
        List<Poset<String>> strings = Poset.singletons(Arrays.asList("a", "b", "c"));
        assertEquals(strings.size(), 3);
        assertEquals(strings.get(0), A);
        
        exception.expect(java.lang.IllegalArgumentException.class);
        strings = Poset.singletons(Arrays.asList("a", "a"));
    }
}