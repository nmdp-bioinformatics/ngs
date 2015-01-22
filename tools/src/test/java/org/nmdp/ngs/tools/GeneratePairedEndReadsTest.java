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

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.JDKRandomGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.nmdp.ngs.reads.CoverageStrategy;
import org.nmdp.ngs.reads.MutationStrategy;
import org.nmdp.ngs.reads.QualityStrategy;
import org.nmdp.ngs.reads.quality.RealDistributionQualityStrategy;

/**
 * Unit test for GeneratePairedEndReads.
 */
public final class GeneratePairedEndReadsTest {
    private File referenceFile;
    private File firstReadFile;
    private File secondReadFile;
    private RandomGenerator random;
    private RealDistribution length;
    private RealDistribution insertSize;
    private QualityStrategy quality;
    private CoverageStrategy coverage;
    private double mutationRate;
    private MutationStrategy mutation;

    @Before
    public void setUp() throws Exception {
        firstReadFile = File.createTempFile("generatePairedEndReadsTest", "fq.gz");
        secondReadFile = File.createTempFile("generatePairedEndReadsTest", "fq.gz");
        random = new JDKRandomGenerator();
        length = new NormalDistribution();
        insertSize = new NormalDistribution();
        quality = new RealDistributionQualityStrategy(new NormalDistribution());
        coverage = GeneratePairedEndReads.DEFAULT_COVERAGE;
        mutationRate = 0.0d;
        mutation = GeneratePairedEndReads.DEFAULT_MUTATION;
    }

    @After
    public void tearDown() throws Exception {
        firstReadFile.delete();
        secondReadFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFirstReadFile() {
        new GeneratePairedEndReads(referenceFile, null, secondReadFile, random, length, insertSize, quality, coverage, mutationRate, mutation);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondReadFile() {
        new GeneratePairedEndReads(referenceFile, firstReadFile, null, random, length, insertSize, quality, coverage, mutationRate, mutation);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new GeneratePairedEndReads(referenceFile, firstReadFile, secondReadFile, random, length, insertSize, quality, coverage, mutationRate, mutation));
    }
}
