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

import org.nmdp.ngs.reads.CoverageStrategy;
import org.nmdp.ngs.reads.MutationStrategy;
import org.nmdp.ngs.reads.QualityStrategy;
import org.nmdp.ngs.reads.quality.RealDistributionQualityStrategy;

/**
 * Unit test for GenerateReads.
 */
public final class GenerateReadsTest {
    private File referenceFile;
    private File readFile;
    private RandomGenerator random;
    private RealDistribution length;
    private QualityStrategy quality;
    private CoverageStrategy coverage;
    private double mutationRate;
    private MutationStrategy mutation;

    @Before
    public void setUp() throws Exception {
        random = new JDKRandomGenerator();
        length = new NormalDistribution();
        quality = new RealDistributionQualityStrategy(new NormalDistribution());
        coverage = GenerateReads.DEFAULT_COVERAGE;
        mutationRate = 0.0d;
        mutation = GenerateReads.DEFAULT_MUTATION;
    }

    @Test
    public void testConstructor() {
        assertNotNull(new GenerateReads(referenceFile, readFile, random, length, quality, coverage, mutationRate, mutation));
    }
}
