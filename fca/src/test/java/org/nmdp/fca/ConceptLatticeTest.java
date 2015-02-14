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
import java.util.Arrays;

public final class ConceptLatticeTest {
  
  
  @Test
  public void testInsert() {
    String[] ab = {"a", "b"};
    String[] bc = {"b", "c"};
    String[] ac = {"a", "c"};
    
    String[] u = {"a", "b", "c"};
    
    ConceptLattice lattice = new ConceptLattice(Arrays.asList(u));

    lattice.insert(0, Arrays.asList(ab));

    lattice.insert(0, Arrays.asList(bc));

    lattice.insert(0, Arrays.asList(ac));
    
    System.out.println("CONCEPT LATTICE");
    System.out.println(lattice);
    
    System.out.println("WRITE");

    System.out.println(lattice);
    
  }
  /**
   * Example 3.15 taken from Davey and Priestly's "Introduction to Lattices
   * and Order" second edition (p 77).
   */
  @Test
  public void testNothing() {
    String [] attributes = {"a", "b", "c", "d", "e", "f", "g"};
    
    String [] S = {"a", "b", "d", "f"};
    String [] T = {"a", "b", "d", "e"};
    String [] U = {"a", "b", "d", "e", "f", "g"};
    String [] V = {"a", "c", "e", "f"};
    String [] W = {"b", "d"};
    String [] X = {"a", "f"};
    
    ConceptLattice lattice = new ConceptLattice(Arrays.asList(attributes));
    
    lattice.insert("S", Arrays.asList(S));
    lattice.insert("T", Arrays.asList(T));
    lattice.insert("U", Arrays.asList(U));
    lattice.insert("V", Arrays.asList(V));
    lattice.insert("W", Arrays.asList(W));
    lattice.insert("X", Arrays.asList(X));
    
    assertEquals(lattice.size(), 12);
    assertEquals(lattice.order(), 18);
    
    System.out.println(lattice);
    
  }
  
}