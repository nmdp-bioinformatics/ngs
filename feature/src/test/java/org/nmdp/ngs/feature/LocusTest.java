/*

    ngs-feature  Features.
    Copyright (c) 2014 National Marrow Donor Program (NMDP)

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
package org.nmdp.ngs.feature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for Locus.
 */
public class LocusTest {
    static String reference = "cgacgccgcgagtccgagagaggagccgcgggcgccgtggatagagc";

    @Test
    public void testGetContig() {
        Locus locus1 = new Locus("chr6", 29942488, 29943994);
        Locus locus2 = new Locus("chr6", 29942554, 29942626);
        Locus locus3 = new Locus("chr7", 29942554, 29942626);
        //System.out.println("locus1 intersection locus2 = " + locus1.intersection(locus2));
    }

    @Test
    public void testIntersection() {
        Locus locus1 = new Locus("chr6", 1, 1000);
        Locus locus2 = new Locus("chr7", 1, 1000);
        Locus locus3 = new Locus("chr6", 1001, 2000);

        assertTrue(locus1.intersection(locus2).isEmpty());
        assertTrue(locus1.intersection(locus3).isEmpty());
    }

    @Test
    public void testPushRight() {
      assertEquals(Locus.Util.pushRight(19, "ag", reference), 21);
      assertEquals(Locus.Util.pushRight(14, "ga", reference), 21);
      assertEquals(Locus.Util.pushRight(20, "gg", reference), 22);
      assertEquals(Locus.Util.pushRight(21, "gag", reference), 24);
      assertEquals(Locus.Util.pushRight(18, "gag", reference), 24);
      assertEquals(Locus.Util.pushRight(1, "CCTCCA", "AGCCCCAA"), 3);
    }

    @Test
    public void testPushLeft() {
      assertEquals(Locus.Util.pushLeft(19, "ag", reference), 14);
      assertEquals(Locus.Util.pushLeft(14, "ga", reference), 14);
      assertEquals(Locus.Util.pushLeft(21, "ga", reference), 21);
      assertEquals(Locus.Util.pushLeft(21, "gag", reference), 18);
      assertEquals(Locus.Util.pushLeft(18, "gag", reference), 18);
      assertEquals(Locus.Util.pushLeft(3, "TCCACC", "AGCCCCAA"), 1);
    }
    
    @Test
    public void testCompareTo() {
      Locus locus1 = new Locus("chr6", 1, 10);
      Locus locus2 = new Locus("chr6", 2, 10);
      Locus locus3 = new Locus("chr6", 1, 5);
      Locus locus4 = new Locus("chr7", 1, 5);
      
      assertTrue(locus1.compareTo(locus2) < 0);
      assertTrue(locus2.compareTo(locus3) > 0);
      assertTrue(locus2.compareTo(locus2) == 0);
      assertTrue(locus3.compareTo(locus4) < 0);
    }
}
