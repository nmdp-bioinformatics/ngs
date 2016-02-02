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
import org.dishevelled.bitset.MutableBitSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.nmdp.ngs.fca.TestUtil.list;
import static org.nmdp.ngs.fca.TestUtil.bits;

public class ContextTest {
    List<Poset<String>> ABC;
    List<String> attributes;
    Context<Poset<String>, Poset<String>> context;

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Before
    public void setUp() {
        ABC = Poset.singletons(Arrays.asList("a", "b", "c"));
        attributes = list("a", "b", "c", "d", "e", "f", "g");
        
        context = Context.builder()
                         .withObjects(attributes)
                         .withAttributes(attributes)
                         .withRelation(new LessThan())
                         .build();
    }
    
    @Test
    public void testBuilder() {
        assertEquals(context.getObjects(), attributes);
        assertEquals(context.getAttributes(), attributes);
        assertTrue(context.getRelation() instanceof LessThan);
    }
    
    @Test
    public void testDecodeIntent() {
        MutableBitSet all = bits(0, 1, 2, 3, 4, 5, 6);
        assertEquals(context.decodeIntent(new Concept(all, bits())), list());
        assertEquals(context.decodeIntent(new Concept(all, bits(0))), list("a"));
        assertEquals(context.decodeIntent(new Concept(all, bits(1, 2))), list("b", "c"));

        exception.expect(java.lang.IndexOutOfBoundsException.class);
        context.decodeIntent(new Concept(all, bits(7)));
    }
    
    @Test
    public void testDecodeExtent() {
        MutableBitSet all = bits(0, 1, 2, 3, 4, 5, 6);
        assertEquals(context.decodeExtent(new Concept(bits(), all)), list());
        assertEquals(context.decodeExtent(new Concept(bits(0), all)), list("a"));
        assertEquals(context.decodeExtent(new Concept(bits(1, 2), all)), list("b", "c"));

        exception.expect(java.lang.IndexOutOfBoundsException.class);
        context.decodeExtent(new Concept(bits(7), all));
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