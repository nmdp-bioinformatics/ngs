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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import static org.nmdp.ngs.fca.TestUtil.list;
import static org.nmdp.ngs.fca.TestUtil.bits;

import java.util.BitSet;
import java.util.List;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.Set;

import org.dishevelled.bitset.MutableBitSet;
import org.dishevelled.bitset.ImmutableBitSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;

public class PosetTest {
    private Poset<String> a, b, c;
    
    @Before
    public void SetUp() {
        a = new Poset<>(new HashSet<String>());
        a.set.add("x");
        a.set.add("y");
        a.set.add("z");
        
        b = new Poset<>(new HashSet<String>());
        b.set.add("x");
        b.set.add("z");
        
    }
    
    @Test
    public void testIntersect() {
        System.out.println("INTERSECT");
        System.out.println(a.intersect(b));
        System.out.println(a.intersect(a));
    }
    
}