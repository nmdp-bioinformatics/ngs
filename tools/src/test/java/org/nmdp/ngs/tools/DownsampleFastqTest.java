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

import org.apache.commons.math3.random.JDKRandomGenerator;

import org.apache.commons.math3.distribution.BinomialDistribution;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for DownsampleFastq.
 */
public final class DownsampleFastqTest {
    private File inputFastqFile;
    private File outputFastqFile;
    private BinomialDistribution distribution;

    @Before
    public void setUp() throws Exception {
        distribution = new BinomialDistribution(new JDKRandomGenerator(), 1, 1.0d);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullDistribution() {
        new DownsampleFastq(inputFastqFile, outputFastqFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new DownsampleFastq(inputFastqFile, outputFastqFile, distribution));
    }
}
