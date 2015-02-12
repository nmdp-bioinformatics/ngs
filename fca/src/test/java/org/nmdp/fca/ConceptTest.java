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
  
  @Test
  public void testOrder() {
    BitSet w = new BitSet(2);
    BitSet x = new BitSet(2);
    BitSet y = new BitSet(2);
    BitSet z = new BitSet(2);
    
    x.flip(0);
    y.flip(1);
    z.flip(0);
    z.flip(1);
    
    Concept W = new Concept(null, w);
    Concept X = new Concept(null, x);
    Concept Y = new Concept(null, y);
    Concept Z = new Concept(null, z);
    
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
  
}