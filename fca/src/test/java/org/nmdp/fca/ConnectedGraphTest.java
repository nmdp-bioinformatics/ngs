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

import static org.nmdp.ngs.fca.ConnectedGraph.builder;

import com.tinkerpop.blueprints.Graph;

import java.util.Iterator;

public final class ConnectedGraphTest {
  private AbstractGraph graph;
  
  @Before
  public void setUp() {
    graph = new ConnectedGraph(false);
  }
  
  @Test
  public void testPath() {
    graph = builder().path(4).build();
    assertEquals(graph.size(), 4);
    assertEquals(graph.order(), 3);
    //assertEquals(graph.root().getInDegree(), 1);
    //assertEquals(graph.root().getOutDegree(), 1);
  }
  
  @Test
  public void testStar() {
    graph = builder().star(4).build();
    assertEquals(graph.size(), 4);
    assertEquals(graph.order(), 3);
    //assertEquals(graph.root().getInDegree(), 3);
    //assertEquals(graph.root().getOutDegree(), 3);
  }
  
  @Test
  public void testCycle() {
    graph = builder().cycle(4).build();
    assertEquals(graph.size(), 4);
    assertEquals(graph.order(), 4);
    //assertEquals(graph.root().getInDegree(), 2);
    //assertEquals(graph.root().getOutDegree(), 2);
  }
  
  @Test
  public void testComplete() {
    graph = builder().complete(4).build();
    assertEquals(graph.size(), 4);
    assertEquals(graph.order(), 6);
    //assertEquals(graph.root().getInDegree(), 3);
    //assertEquals(graph.root().getOutDegree(), 3);
  }
  
  @Test
  public void isEmpty() {
    assertTrue(graph.isEmpty());
  }
  
  @Test
  public void isLabeled() {
    assertFalse(graph.isLabeled());
  }
  
  @Test
  public void  isWeighted() {
    assertFalse(graph.isWeighted());
  }

  @Test
  public void isDirected() {
    assertFalse(graph.isDirected());
  }
  
  @Test
  public void isMulti() {
    assertTrue(graph.isMulti());
  }
 
  @Test
  public void isComplex() {
    assertTrue(graph.isComplex());
  }

  @Test
  public void isCyclic() {
    assertTrue(graph.isCyclic());
  }
}