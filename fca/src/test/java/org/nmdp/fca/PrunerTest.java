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

import java.util.List;
import java.util.ArrayList;

import org.nmdp.ngs.fca.Vertex;
import static org.nmdp.ngs.fca.Pruner.Builder;

/**
 *
 * @author int33484
 */
public class PrunerTest {
  Pruner pruner;
  List labels;
  List weights;
  
  @Before
  public void setUp() {
    pruner = new Pruner();
    
    labels = new ArrayList<String>();
    labels.add("a");
    
    weights = new ArrayList<String>();
    weights.add("A");
  }
  
  @Test
  public void testDefaultConstructor() {
    assertTrue(pruner.getLabels().isEmpty());
    assertTrue(pruner.getWeights().isEmpty());
  }
  
  @Test
  public void testInitialize() { 
    List<String> weights = new ArrayList<String>();
    labels.add("a");
    labels.add("b");
    
    pruner = new Builder().withLabels(labels).build();
    assertEquals(pruner.getLabels(), labels);
    assertTrue(pruner.getWeights().isEmpty());
    
    pruner = new Builder().withWeights(weights).build();
    assertTrue(pruner.getLabels().isEmpty());
    assertEquals(pruner.getWeights(), weights);
    
    pruner = new Builder().withWeights(weights).withLabels(labels).build();
    assertEquals(pruner.getLabels(), labels);
    assertEquals(pruner.getWeights(), weights);
  }
  
  @Test
  public void testPruneVertex() {
    Vertex a = new Vertex(0, "a");
    assertFalse(pruner.pruneVertex(a));
    assertEquals(pruner.getParent().getLabel(), "a");
    
    pruner = new Builder().withLabels(labels).build();
    assertTrue(pruner.pruneVertex(a));
  }
}
