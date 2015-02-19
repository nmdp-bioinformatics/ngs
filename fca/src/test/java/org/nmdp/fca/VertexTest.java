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

import java.util.Iterator;

public final class VertexTest {
  private Vertex x, y, z;
  private Iterator<Vertex.Edge<String>> edge;
  
  @Before
  public void setUp() {
    x = new Vertex(0, "X");
    y = new Vertex(1, "Y");
    z = new Vertex(2, "Z");
  }
  
  @Test
  public void testInit() {
    assertEquals(x.getId(), 0);
    assertEquals(x.getLabel(), "X");
    assertEquals(x.getInDegree(), 0);
    assertEquals(x.getOutDegree(), 0);
    assertTrue(x.isEmpty());
    assertTrue(x.isSingleton());
  }
  
  @Test
  public void testAdoptOrphanLoop() {
    assertTrue(x.adopt(x, 0));
    
    assertEquals(x.getInDegree(), 1);
    assertEquals(x.getOutDegree(), 1);
    
    edge = x.iterator();
    assertEquals(x, edge.next().target());
    
    assertTrue(x.orphan(x));
    assertEquals(x.getInDegree(), 0);
    assertEquals(x.getOutDegree(), 0);
    
    assertFalse(x.orphan(x));
  }
  
  @Test
  public void testAdoptOrphanDirectedEdge() {
    assertTrue(x.adopt(y, 0));
    
    assertEquals(x.getInDegree(), 0);
    assertEquals(x.getOutDegree(), 1);
    assertEquals(y.getInDegree(), 1);
    assertEquals(y.getOutDegree(), 0);
    
    edge = x.iterator();
    assertEquals(y, edge.next().target());
    
    edge = y.iterator();
    assertFalse(edge.hasNext());
    
    assertTrue(x.orphan(y));
    assertTrue(x.isSingleton());
    assertTrue(y.isSingleton());
    
    assertFalse(x.orphan(y));
  }
  
  @Test
  public void testAdoptOrphanUndirectedEdge() {
    assertTrue(x.adopt(y, 0));
    assertTrue(y.adopt(x, 0));
    
    assertEquals(x.getInDegree(), 1);
    assertEquals(x.getOutDegree(), 1);
    assertEquals(y.getInDegree(), 1);
    assertEquals(y.getOutDegree(), 1);
    
    edge = x.iterator();
    assertEquals(y, edge.next().target());
    
    edge = y.iterator();
    assertEquals(x, edge.next().target());
    
    assertTrue(x.orphan(y));
    assertTrue(x.isEmpty());
    assertFalse(y.isEmpty());
    
    assertTrue(y.orphan(x));
    assertTrue(x.isSingleton());
    assertTrue(y.isSingleton());
    
    assertFalse(x.orphan(y));
    assertFalse(y.orphan(x));
  }
  
  @Test
  public void testAdoptOrphanParallelLoop() {
    x.adopt(x, 0);
    x.adopt(x, 0);

    assertEquals(x.getInDegree(), 2);
    assertEquals(x.getOutDegree(), 2);
    
    edge = x.iterator();
    assertEquals(x, edge.next().target());
    assertEquals(x, edge.next().target());
    assertFalse(edge.hasNext());
    
    assertTrue(x.orphan(x));
    assertEquals(x.getInDegree(), 1);
    assertEquals(x.getOutDegree(), 1);
    
    assertTrue(x.orphan(x));
    assertTrue(x.isSingleton());
    
    assertFalse(x.orphan(x));
  }
  
  @Test
  public void testAdoptOrphanParallelDirectedEdge() {
    
  }
  
  @Test
  public void testAdoptOrphanParallelUndirectedEdge() {
    
  }
  
  @Test
  public void testAdoptOrphanCycle() {
    assertTrue(x.adopt(y, 0));
    assertTrue(y.adopt(z, 0));
    assertTrue(z.adopt(x, 0));
    
    assertEquals(x.getInDegree(), 1);
    assertEquals(x.getOutDegree(), 1);
    assertEquals(y.getInDegree(), 1);
    assertEquals(y.getOutDegree(), 1);
    assertEquals(z.getInDegree(), 1);
    assertEquals(z.getOutDegree(), 1);
   
    edge = x.iterator();
    assertEquals(y, edge.next().target());
    edge = y.iterator();
    assertEquals(z, edge.next().target());
    edge = z.iterator();
    assertEquals(x, edge.next().target());
    assertFalse(edge.hasNext());
    
    assertTrue(x.orphan(y));
    assertTrue(y.orphan(z));
    assertTrue(z.orphan(x));
    
    assertTrue(x.isSingleton());
    assertTrue(y.isSingleton());
    assertTrue(z.isSingleton());
    
    assertFalse(x.orphan(y));
    assertFalse(y.orphan(z));
    assertFalse(z.orphan(x));
  }
  
  @Test
  public void testIterator() {
    
  }
  
  @Test
  public void testEquals() {
    Vertex vertex = new Vertex(0, "X");
    assertEquals(vertex, x);
  }
  
  @Test
  public void testShallowCopy() {
    y.adopt(x, 0);
    Vertex copy = y.shallowCopy();
    assertEquals(copy.getId(), 1);
    assertEquals(copy.getLabel(), "Y");
    assertEquals(copy.getInDegree(), 0);
    assertEquals(copy.getOutDegree(), 0);
  }
}