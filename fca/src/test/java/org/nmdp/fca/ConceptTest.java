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

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import com.google.common.collect.ImmutableList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.BitSet;

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
  public void testOrder() {
    assertEquals(W.order(W), Partial.Order.EQUAL);
    assertEquals(W.order(X), Partial.Order.LESS);
    assertEquals(W.order(Y), Partial.Order.LESS);
    assertEquals(W.order(Z), Partial.Order.LESS);
    
    assertEquals(X.order(W), Partial.Order.GREATER);
    assertEquals(X.order(X), Partial.Order.EQUAL);
    assertEquals(X.order(Y), Partial.Order.NONCOMPARABLE);
    assertEquals(X.order(Z), Partial.Order.LESS);
    
    assertEquals(Y.order(W), Partial.Order.GREATER);
    assertEquals(Y.order(X), Partial.Order.NONCOMPARABLE);
    assertEquals(Y.order(Y), Partial.Order.EQUAL);
    assertEquals(Y.order(Z), Partial.Order.LESS);
    
    assertEquals(Z.order(W), Partial.Order.GREATER);
    assertEquals(Z.order(X), Partial.Order.GREATER);
    assertEquals(Z.order(Y), Partial.Order.GREATER);
    assertEquals(Z.order(Z), Partial.Order.EQUAL);
  }
  
  @Test
  public void testGte() {
    assertTrue(W.order(W).gte());
    assertFalse(W.order(X).gte());
    assertFalse(W.order(Y).gte());
    assertFalse(W.order(Z).gte());
    
    assertTrue(X.order(W).gte());
    assertTrue(X.order(X).gte());
    assertFalse(X.order(Y).gte());
    assertFalse(X.order(Z).gte());
    
    assertTrue(Y.order(W).gte());
    assertFalse(Y.order(X).gte());
    assertTrue(Y.order(Y).gte());
    assertFalse(Y.order(Z).gte());
    
    assertTrue(Z.order(W).gte());
    assertTrue(Z.order(X).gte());
    assertTrue(Z.order(Y).gte());
    assertTrue(Z.order(Z).gte());
  }
  
  @Test
  public void testLte() {
    assertTrue(W.order(W).lte());
    assertTrue(W.order(X).lte());
    assertTrue(W.order(Y).lte());
    assertTrue(W.order(Z).lte());
    
    assertFalse(X.order(W).lte());
    assertTrue(X.order(X).lte());
    assertFalse(X.order(Y).lte());
    assertTrue(X.order(Z).lte());
    
    assertFalse(Y.order(W).lte());
    assertFalse(Y.order(X).lte());
    assertTrue(Y.order(Y).lte());
    assertTrue(Y.order(Z).lte());
    
    assertFalse(Z.order(W).lte());
    assertFalse(Z.order(X).lte());
    assertFalse(Z.order(Y).lte());
    assertTrue(Z.order(Z).lte());
  }
  
}