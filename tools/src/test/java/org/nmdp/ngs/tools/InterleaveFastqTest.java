/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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
package org.nmdp.ngs.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
 * Unit test for InterleaveFastq.
 */
public final class InterleaveFastqTest {
    private File firstFastqFile;
    private File secondFastqFile;
    private File pairedFile;
    private File unpairedFile;
    private static final Fastq left = new FastqBuilder().withDescription("foo 1").appendSequence("a").appendQuality("-").build();
    private static final Fastq right = new FastqBuilder().withDescription("foo 2").appendSequence("a").appendQuality("-").build();
    private static final Fastq invalid = new FastqBuilder().withDescription("invalid").appendSequence("a").appendQuality("-").build();
    private static final List<Fastq> LEFT = ImmutableList.of(left);
    private static final List<Fastq> RIGHT = ImmutableList.of(right);
    private static final List<Fastq> INVALID = ImmutableList.of(invalid);

    @Before
    public void setUp() throws IOException {
        firstFastqFile = File.createTempFile("interleaveFastqTest", ".fq");
        secondFastqFile = File.createTempFile("interleaveFastqTest", ".fq");
        pairedFile = File.createTempFile("interleaveFastqTest", ".fq");
        unpairedFile = File.createTempFile("interleaveFastqTest", ".fq");
    }

    @After
    public void tearDown() {
        firstFastqFile.delete();
        secondFastqFile.delete();
        pairedFile.delete();
        unpairedFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFirstFastqFile() {
        new InterleaveFastq(null, secondFastqFile, pairedFile, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondFastqFile() {
        new InterleaveFastq(firstFastqFile, null, pairedFile, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullPairedFile() {
        new InterleaveFastq(firstFastqFile, secondFastqFile, null, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullUnpairedFile() {
        new InterleaveFastq(firstFastqFile, secondFastqFile, pairedFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new InterleaveFastq(firstFastqFile, secondFastqFile, pairedFile, unpairedFile));
    }
}
