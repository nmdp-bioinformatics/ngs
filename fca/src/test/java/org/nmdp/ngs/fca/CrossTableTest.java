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

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import static org.nmdp.ngs.fca.TestUtil.bits;
import org.dishevelled.bitset.MutableBitSet;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CrossTableTest {
    private CrossTable table, other;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    @Before
    public void setUp() {
        table = new CrossTable();
        table.addRow(bits(1, 2));
        table.addRow(bits(2));
        table.addRow(bits(1));
        
        other = new CrossTable();
        other.addRow(bits(1, 2));
        other.addRow(bits(2));
        other.addRow(bits());
    }
    
    @Test
    public void testRow() {
        CrossTable.Row row = new CrossTable.Row(0, bits(0));
        
        assertEquals(row.index, 0);
        assertEquals(row.intent, bits(0));
        
        row = new CrossTable.Row(1, bits(0));
        
        assertEquals(row.asConcept().extent(), bits(1));
        assertEquals(row.asConcept().intent(), bits(0));
    }
    
    @Test
    public void testCrossTable() {
        assertEquals(table.getNumberOfRows(), 3);
        assertEquals(table.getNumberOfColumns(), 3);
    }
    
    @Test
    public void testAddRow() {
        table.addRow(bits(4, 5));
        assertEquals(table.getNumberOfRows(), 4);
        assertEquals(table.getNumberOfColumns(), 6);
        assertEquals(table.getRow(3).intent, bits(4, 5));
    }
    
    @Test
    public void testGetRow() {
        exception.expect(java.lang.IllegalArgumentException.class);
        table.getRow(-10);
        
        exception.expect(java.lang.IllegalArgumentException.class);
        table.getRow(10);
        
        assertEquals(table.getRow(1).index, 1);
        assertEquals(table.getRow(1).intent, bits(1, 2));
    }
    
    @Test
    public void testComplement() {
        CrossTable complement = CrossTable.complement(table);
        assertEquals(complement.getNumberOfRows(), 3);
        assertEquals(complement.getNumberOfColumns(), 3);
        assertEquals(complement.getRow(0).intent, bits(0));
    }
    
    @Test
    public void testHorizontalSum() {
        CrossTable sum = CrossTable.horizontalSum(table, other);
        
        assertEquals(sum.getNumberOfRows(), 6);
        assertEquals(sum.getNumberOfColumns(), 6);
        assertEquals(sum.getRow(0).intent, bits(1, 2));
        assertEquals(sum.getRow(3).intent, bits(4, 5));
        assertEquals(sum.getRow(5).intent, bits());
    }
    
    @Test
    public void testVerticalSum() {
        CrossTable sum = CrossTable.verticalSum(table, other);
        
        assertEquals(sum.getNumberOfRows(), 6);
        assertEquals(sum.getNumberOfColumns(), 6);
        assertEquals(sum.getRow(0).intent, bits(1, 2, 3, 4, 5));
        assertEquals(sum.getRow(3).intent, bits(4, 5));
        assertEquals(sum.getRow(5).intent, bits());
        
        assertEquals(table.getNumberOfRows(), 3);
        assertEquals(table.getRow(0).intent, bits(1, 2));
    }
    
    @Test
    public void testDirectProduct() {
        CrossTable sum = CrossTable.directProduct(table, other);
        
        assertEquals(sum.getNumberOfRows(), 6);
        assertEquals(sum.getNumberOfColumns(), 6);
        assertEquals(sum.getRow(0).intent, bits(1, 2, 3, 4, 5));
        assertEquals(sum.getRow(3).intent, bits(0, 1, 2, 4, 5));
        assertEquals(sum.getRow(5).intent, bits(0, 1, 2));
        
        assertEquals(table.getNumberOfRows(), 3);
        assertEquals(table.getRow(0).intent, bits(1, 2));
    }
}