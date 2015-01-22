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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.tools.FastqToSsake.isLeft;
import static org.nmdp.ngs.tools.FastqToSsake.isRight;

import java.io.File;
import java.io.IOException;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for FastqToSsake.
 */
public final class FastqToSsakeTest {
    private File firstFastqFile;
    private File secondFastqFile;
    private File ssakeFile;
    private File unpairedFile;
    private static final Fastq left = new FastqBuilder().withDescription("foo 1").appendSequence("a").appendQuality("-").build();
    private static final Fastq right = new FastqBuilder().withDescription("foo 2").appendSequence("a").appendQuality("-").build();
    private static final Fastq invalid = new FastqBuilder().withDescription("invalid").appendSequence("a").appendQuality("-").build();
    private static final List<Fastq> LEFT = ImmutableList.of(left);
    private static final List<Fastq> RIGHT = ImmutableList.of(right);
    private static final List<Fastq> INVALID = ImmutableList.of(invalid);

    @Before
    public void setUp() throws IOException {
        firstFastqFile = File.createTempFile("fastqToSsakeTest", ".fq");
        secondFastqFile = File.createTempFile("fastqToSsakeTest", ".fq");
    }

    @After
    public void tearDown() {
        firstFastqFile.delete();
        secondFastqFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFirstFastqFile() {
        new FastqToSsake(null, secondFastqFile, ssakeFile, FastqToSsake.DEFAULT_INSERT_SIZE, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondFastqFile() {
        new FastqToSsake(firstFastqFile, null, ssakeFile, FastqToSsake.DEFAULT_INSERT_SIZE, unpairedFile);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInsertSizeTooSmall() {
        new FastqToSsake(firstFastqFile, secondFastqFile, ssakeFile, -1, unpairedFile);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new FastqToSsake(firstFastqFile, secondFastqFile, ssakeFile, FastqToSsake.DEFAULT_INSERT_SIZE, unpairedFile));
    }

    @Test
    public void testIsLeft() {
        for (Fastq fastq : LEFT) {
            assertTrue(isLeft(fastq));
        }
        for (Fastq fastq : RIGHT) {
            assertFalse(isLeft(fastq));
        }
        for (Fastq fastq : INVALID) {
            assertFalse(isLeft(fastq));
        }
    }

    @Test
    public void testIsRight() {
        for (Fastq fastq : LEFT) {
            assertFalse(isRight(fastq));
        }
        for (Fastq fastq : RIGHT) {
            assertTrue(isRight(fastq));
        }
        for (Fastq fastq : INVALID) {
            assertFalse(isRight(fastq));
        }
    }
}
