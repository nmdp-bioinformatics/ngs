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
import java.util.List;

import com.google.common.collect.ImmutableList;

public final class ConceptLatticeTest {
  ConceptLattice lattice;
  private List abcdefg, abdefg, abde, abdf, acef, bd, af;
  /**
   * Example taken from Davey and Priestly's "Introduction to Lattices and
   * Order" second edition (p 77).
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
    
    lattice = new ConceptLattice(abcdefg);
  }

  @Test
  public void testInsert() {
    lattice.insert("S", abdf);
    lattice.insert("T", abde);
    lattice.insert("U", abdefg);
    lattice.insert("V", acef);
    lattice.insert("W", bd);
    lattice.insert("X", af);
    
    assertEquals(lattice.size(), 12);
    assertEquals(lattice.order(), 18);
  }
  
  @Test
  public void testLeastUpperBound() {
    testInsert(); // build the example lattice
    
    BitSet bits = new BitSet();
    
    Concept concept = lattice.leastUpperBound(abcdefg);
    
    bits.clear(); // {}
    assertEquals(concept.extent(), bits);
    
    bits.clear();
    bits.set(0, 7); // {abcdefg}
    assertEquals(concept.intent(), bits);
    
    concept = lattice.leastUpperBound(acef);
    
    bits.clear();
    bits.flip(3); // {V}
    assertEquals(concept.extent(), bits);
    
    bits.clear();
    bits.flip(0); bits.flip(2); bits.flip(4); bits.flip(5); // {acef}
    assertEquals(concept.intent(), bits);
    
    List ae = new ImmutableList.Builder<String>().add("a").add("e").build();
    concept = lattice.leastUpperBound(ae);
    
    bits.clear();
    bits.flip(1); bits.flip(2); bits.flip(3); // {TUV}
    assertEquals(concept.extent(), bits);
    
    bits.clear();
    bits.flip(0); bits.flip(4); // {ae}
    assertEquals(concept.intent(), bits);
    
    List empty = new ImmutableList.Builder<String>().build();
    concept = lattice.leastUpperBound(empty);
    
    bits.clear();
    bits.set(0, 6); // {STUVWX}
    assertEquals(concept.extent(), bits);
    
    bits.clear(); // {}
    assertEquals(concept.intent(), bits);
    
    concept = lattice.leastUpperBound(bd, af);
    
    bits.clear();
    bits.flip(0); bits.flip(2); // {SU}
    assertEquals(concept.extent(), bits);
    
    bits.clear();
    bits.flip(0); bits.flip(1); bits.flip(3); bits.flip(5); // {abdf}
    assertEquals(concept.intent(), bits);
  }
  
  @Test
  public void testMarginal() {
    testInsert(); // build the example lattice
    
    assertEquals(0.0, lattice.marginal(abcdefg), 0.0);

    assertEquals(0.167, lattice.marginal(abdefg), 0.01);
    
    List abd = new ImmutableList.Builder<String>().add("a").add("b").add("d").build();
    assertEquals(0.5, lattice.marginal(abd), 0.0);
    
    List empty = new ImmutableList.Builder<String>().build();
    assertEquals(1.0, lattice.marginal(empty), 0.0);
  }
  
  @Test
  public void testConditional() {
    testInsert(); // build the example lattice
    
    assertEquals(0.0, lattice.conditional(acef, bd), 0.0);
    assertEquals(0.0, lattice.conditional(bd, acef), 0.0);
    
    List a = new ImmutableList.Builder<String>().add("a").build();
    assertEquals(0.2, lattice.conditional(acef, a), 0.01);
    assertEquals(1.0, lattice.conditional(a, acef), 0.0);
  }
  
  @Test
  public void testIsDirected() {
    assertTrue(lattice.isDirected());
  }
  
  @Test
  public void testIsLabeled() {
    assertTrue(lattice.isLabeled());
  }

  @Test
  public void testIsWeighted() {
    assertTrue(lattice.isWeighted());
  }
  
  @Test
  public void testIsMulti() {
    assertFalse(lattice.isMulti());
  }

  @Test
  public void isComplex() {
    assertFalse(lattice.isComplex());
  }
  
  @Test
  public void isCyclic() {
    assertFalse(lattice.isCyclic());
  } 
}