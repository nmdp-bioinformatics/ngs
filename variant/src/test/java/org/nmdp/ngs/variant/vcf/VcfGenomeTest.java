/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for VcfGenome.
 */
public final class VcfGenomeTest {

    @Test(expected=NullPointerException.class)
    public void testConstructorNullId() {
        new VcfGenome(null, 1.0d, "Description");
    }

    @Test
    public void testConstructor() {
        VcfGenome genome = new VcfGenome("genomeId", 1.0d, "Description");
        assertEquals("genomeId", genome.getId());
        assertEquals(1.0d, genome.getMixture(), 0.1d);
        assertEquals("Description", genome.getDescription());
    }
}
