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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.BitSet;

public final class ConceptTest {
  BitSet w, x, y, z;
  Concept W, X, Y, Z;
  
  @Before
  public void setUp() {
    w = new BitSet(2);
    x = new BitSet(2);
    y = new BitSet(2);
    z = new BitSet(2);
    
    x.flip(0);
    y.flip(1);
    z.flip(0);
    z.flip(1);
    
    W = new Concept(null, w);
    X = new Concept(null, x);
    Y = new Concept(null, y);
    Z = new Concept(null, z);
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
    assertTrue(W.gte(W));
    assertFalse(W.gte(X));
    assertFalse(W.gte(Y));
    assertFalse(W.gte(Z));
    
    assertTrue(X.gte(W));
    assertTrue(X.gte(X));
    assertFalse(X.gte(Y));
    assertFalse(X.gte(Z));
    
    assertTrue(Y.gte(W));
    assertFalse(Y.gte(X));
    assertTrue(Y.gte(Y));
    assertFalse(Y.gte(Z));
    
    assertTrue(Z.gte(W));
    assertTrue(Z.gte(X));
    assertTrue(Z.gte(Y));
    assertTrue(Z.gte(Z));
  }
  
  @Test
  public void testLte() {
    assertTrue(W.lte(W));
    assertTrue(W.lte(X));
    assertTrue(W.lte(Y));
    assertTrue(W.lte(Z));
    
    assertFalse(X.lte(W));
    assertTrue(X.lte(X));
    assertFalse(X.lte(Y));
    assertTrue(X.lte(Z));
    
    assertFalse(Y.lte(W));
    assertFalse(Y.lte(X));
    assertTrue(Y.lte(Y));
    assertTrue(Y.lte(Z));
    
    assertFalse(Z.lte(W));
    assertFalse(Z.lte(X));
    assertFalse(Z.lte(Y));
    assertTrue(Z.lte(Z));
  }
  
}