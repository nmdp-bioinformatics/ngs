/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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
package org.nmdp.ngs.tools;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for GenerateBed.
 */
public final class GenerateBedTest {
    private File bedFile;
    private String chrom;
    private int n;
    private int size;
    private RandomGenerator random;
    private RealDistribution length;

    @Before
    public void setUp() {
        chrom = "1";
        n = 10;
        size = 100;
        random = new JDKRandomGenerator();
        length = new NormalDistribution();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidN() {
        new GenerateBed(bedFile, -1, size, chrom, random, length);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidSize() {
        new GenerateBed(bedFile, n, -1, chrom, random, length);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullChrom() {
        new GenerateBed(bedFile, n, size, null, random, length);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullRandom() {
        new GenerateBed(bedFile, n, size, chrom, null, length);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNullLength() {
        new GenerateBed(bedFile, n, size, chrom, random, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new GenerateBed(bedFile, n, size, chrom, random, length));
    }
}
