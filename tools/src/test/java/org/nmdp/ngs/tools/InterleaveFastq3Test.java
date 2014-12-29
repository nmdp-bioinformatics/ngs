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

import static org.dishevelled.compress.Readers.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.FastqReader;
import org.biojava.bio.program.fastq.SangerFastqReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for InterleaveFastq3.
 */
public final class InterleaveFastq3Test {
    private File firstFastqFile;
    private File secondFastqFile;
    private File pairedFile;
    private File unpairedFile;

    @Before
    public void setUp() throws IOException {
        firstFastqFile = File.createTempFile("interleaveFastqTest", ".fq.gz");
        secondFastqFile = File.createTempFile("interleaveFastqTest", ".fq.gz");
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
        new InterleaveFastq3(null, secondFastqFile, pairedFile, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullSecondFastqFile() {
        new InterleaveFastq3(firstFastqFile, null, pairedFile, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullPairedFile() {
        new InterleaveFastq3(firstFastqFile, secondFastqFile, null, unpairedFile);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullUnpairedFile() {
        new InterleaveFastq3(firstFastqFile, secondFastqFile, pairedFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new InterleaveFastq3(firstFastqFile, secondFastqFile, pairedFile, unpairedFile));
    }

    @Test
    public void testInterleaveFastq() throws Exception {
        copyResource("paired_R1.fq.gz", firstFastqFile);
        copyResource("paired_R2.fq.gz", secondFastqFile);
        new InterleaveFastq3(firstFastqFile, secondFastqFile, pairedFile, unpairedFile).call();

        assertEquals(8, countFastq(pairedFile));
        assertEquals(0, countFastq(unpairedFile));
    }

    @Test
    public void testInterleaveFastqUnpaired() throws Exception {
        copyResource("unpaired_R1.fq.gz", firstFastqFile);
        copyResource("unpaired_R2.fq.gz", secondFastqFile);
        new InterleaveFastq3(firstFastqFile, secondFastqFile, pairedFile, unpairedFile).call();

        assertEquals(4, countFastq(pairedFile));
        assertEquals(2, countFastq(unpairedFile));
    }

    private static int countFastq(final File file) throws Exception {
        FastqReader fastqReader = new SangerFastqReader();
        int count = 0;
        for (Fastq fastq : fastqReader.read(file)) {
            count++;
        }
        return count;
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(InterleaveFastq3Test.class.getResource(name)), file);
    }
}
