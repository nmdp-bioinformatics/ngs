/*
ngs-fca Formal concept analysis for genomics.
Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)
This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 3 of the License, or (at
your option) any later version.
This library is distributed in the hope that it will be useful, but WITHOUT
ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
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

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Direction;

public final class TinkerContextTest {
  TinkerContext lattice;
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
  lattice = new TinkerContext(abcdefg);
  }

  @Test
  public void testInsert() {
    
    List ab = new ImmutableList.Builder<String>().add("a").add("b").build();
    List bc = new ImmutableList.Builder<String>().add("b").add("c").build();
    List ac = new ImmutableList.Builder<String>().add("a").add("c").build();
    
    //lattice.insert("A", ab);
    //lattice.insert("B", bc);
    //lattice.insert("C", ac);
    
    
    System.out.println("insert(S, " + abdf);
    lattice.insert("S", abdf);
    System.out.println("insert(T, " + abde);
    lattice.insert("T", abde);
    System.out.println("insert(U, " + abdefg);
    lattice.insert("U", abdefg);
    System.out.println("insert(V, " + acef);
    lattice.insert("V", acef);
    System.out.println("insert(W, " + bd);
    lattice.insert("W", bd);
    System.out.println("insert(X, " + af);
    lattice.insert("X", af);
    System.out.println("done inserting");
            
    
    System.out.println(lattice);
  }
}