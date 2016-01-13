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

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public final class IntervalLatticeTest {
    private IntervalLattice<Integer> intervals;
    
    @Before
    public void setUp() {
        intervals = new IntervalLattice<>(new TinkerGraph());
        
        Interval<Integer> x = new Interval<>(1, Range.closed(9, 12));
        Interval<Integer> y = new Interval<>(1, Range.closed(10, 14));
        Interval<Integer> z = new Interval<>(1, Range.closed(11, 16));
        Interval<Integer> w = new Interval<>(2, Range.closed(1, 2));
        
        // TODO: there's a bug revealed if you swap z and y -- test commutative
        
        intervals.insert(x);
        intervals.insert(y);
        intervals.insert(z);
        
        intervals.insert(w);
        
        System.out.println(intervals);
    }
    
    @Test
    public void testSetup() {
        IntervalLattice<Date> dates = new IntervalLattice<>(new TinkerGraph());
        
        
        Date d1 = new Date(2015, 11, 15, 0, 0);
        Date d3 = new Date(2015, 12, 12, 0, 0);
        Date d4 = new Date(2015, 12, 10, 0, 0);
        Date d5 = new Date(2015, 12, 14, 0, 0);
        Date d6 = new Date(2015, 12, 11, 0, 0);
        Date d7 = new Date(2015, 12, 16, 0, 0);
        Date d8 = new Date(2015, 10, 1, 0, 0);
        Date d9 = new Date(2015, 10, 2, 0, 0);
        
        Interval<Date> a = new Interval<>(1, Range.closed(d1, d3));
        Interval<Date> b = new Interval<>(1, Range.closed(d4, d5));
        Interval<Date> c = new Interval<>(1, Range.closed(d6, d7));
        Interval<Date> d = new Interval<>(1, Range.closed(d8, d9));
        
        System.out.println("a.before(b) ? " + a.before(b));
        System.out.println("d.before(b) ? " + d.before(b));
        
        dates.insert(a);
        
        dates.insert(c);
        dates.insert(b);
        dates.insert(d);
        
        System.out.println(dates);
                
        
    }
}