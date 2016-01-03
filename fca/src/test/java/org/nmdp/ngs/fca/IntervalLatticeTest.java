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

import java.time.LocalDateTime;
import java.time.Month;

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
        IntervalLattice<LocalDateTime> dates = new IntervalLattice<>(new TinkerGraph());
        
        
        LocalDateTime d1 = LocalDateTime.of(2015, Month.NOVEMBER, 15, 0, 0);
        LocalDateTime d3 = LocalDateTime.of(2015, Month.DECEMBER, 12, 0, 0);
        LocalDateTime d4 = LocalDateTime.of(2015, Month.DECEMBER, 10, 0, 0);
        LocalDateTime d5 = LocalDateTime.of(2015, Month.DECEMBER, 14, 0, 0);
        LocalDateTime d6 = LocalDateTime.of(2015, Month.DECEMBER, 11, 0, 0);
        LocalDateTime d7 = LocalDateTime.of(2015, Month.DECEMBER, 16, 0, 0);
        LocalDateTime d8 = LocalDateTime.of(2015, Month.OCTOBER, 1, 0, 0);
        LocalDateTime d9 = LocalDateTime.of(2015, Month.OCTOBER, 2, 0, 0);
        
        Interval<LocalDateTime> a = new Interval<>(1, Range.closed(d1, d3));
        Interval<LocalDateTime> b = new Interval<>(1, Range.closed(d4, d5));
        Interval<LocalDateTime> c = new Interval<>(1, Range.closed(d6, d7));
        Interval<LocalDateTime> d = new Interval<>(1, Range.closed(d8, d9));
        
        System.out.println("a.before(b) ? " + a.before(b));
        System.out.println("d.before(b) ? " + d.before(b));
        
        dates.insert(a);
        
        dates.insert(c);
        dates.insert(b);
        dates.insert(d);
        
        System.out.println(dates);
                
        
    }
}