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

import com.google.common.collect.Range;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import static org.nmdp.ngs.fca.TestUtil.bits;

public class ContextTest {
    List<Poset<String>> ABC;
    
    @Before
    public void setUp() {
        ABC = Poset.singletons(Arrays.asList("a", "b", "c"));
    }
    
    @Test
    public void testSetUp() {
        
        List<Interval<Integer>> a = new ArrayList<>();
        List<Interval<Integer>> b = new ArrayList<>();
        
        Interval<Integer> i1 = new Interval<>(1, Range.closed(2, 4));
        Interval<Integer> i2 = new Interval<>(1, Range.closed(1, 5));
        
        a.add(i1);
        a.add(i2);
        
        b.add(i1);
        b.add(i2);
        
        boolean apply = new LessThan().apply(i2, i1);
        
        System.out.println(apply + " = PartiallyOrdered.LessThan.apply(" + i1 + ", " + i2 + ")");
        
        Context<Interval<Integer>, Interval<Integer>> context;
        context = Context.builder()
                         .withObjects(a)
                         .withAttributes(b)
                         .withRelation(new LessThan())
                         .build();
        
        System.out.println("CONTEXT");
        System.out.println(context);
        
        CrossTable table = context.asCrossTable();
        
        System.out.println("CROSS TABLE");
        System.out.println(table);
        
        ConceptLattice lattice = context.asConceptLattice(new TinkerGraph());
        
        System.out.println("LATTICE");
        System.out.println(lattice);
    }
    
    @Test
    public void testBuilder() {
        
    }
    
        
    @Test
    public void testDown() {
        Context powerset = Context.powerset(ABC);
        ConceptLattice lattice = powerset.asConceptLattice(new TinkerGraph());
        
        ConceptLattice downset = Context.down(lattice).asConceptLattice(new TinkerGraph());
        System.out.println("DOWN = " + downset);
    }
    
    @Test
    public void testDownset() {
        ConceptLattice powerset = Context.powerset(ABC).asConceptLattice(new TinkerGraph());
        Context downset = Context.downset(powerset);
        
        assertEquals(downset.getObjects().size(), 8);
        assertEquals(downset.getAttributes().size(), 8);
        assertTrue(downset.getRelation() instanceof NotGreaterOrEqual);
        
        ConceptLattice lattice = downset.asConceptLattice(new TinkerGraph());
        
        assertEquals(lattice.size(), 20);
        assertEquals(lattice.top(), new Concept(bits(), bits(0, 1, 2, 3, 4, 5, 6, 7)));
        assertEquals(lattice.bottom(), new Concept(bits(0, 1, 2, 3, 4, 5, 6, 7), bits()));
    }
    
    @Test
    public void testPowerset() {
        Context powerset = Context.powerset(ABC);

        assertEquals(powerset.getObjects(), ABC);
        assertEquals(powerset.getAttributes(), ABC);
        assertTrue(powerset.getRelation() instanceof NotEqual);
        
        ConceptLattice lattice = powerset.asConceptLattice(new TinkerGraph());
                
        assertEquals(lattice.size(), 8);
        assertEquals(lattice.top(), new Concept(bits(), bits(0, 1, 2)));
        assertEquals(lattice.bottom(), new Concept(bits(0, 1, 2), bits()));
    }
    
    @Test
    public void testAntichain() {
        Context antichain = Context.antichain(ABC);
        
        assertEquals(antichain.getObjects(), ABC);
        assertEquals(antichain.getAttributes(), ABC);
        assertTrue(antichain.getRelation() instanceof Equal);
        
        ConceptLattice lattice = antichain.asConceptLattice(new TinkerGraph());
        
        assertEquals(lattice.size(), 5);
        assertEquals(lattice.top(), new Concept(bits(), bits(0, 1, 2)));
        assertEquals(lattice.bottom(), new Concept(bits(0, 1, 2), bits()));
    }
}