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
import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.tools.FilterConsensus.cigarToEditList;
import static org.nmdp.ngs.tools.FilterConsensus.readGenomicFile;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.biojava.bio.symbol.Edit;

import org.nmdp.ngs.feature.Allele;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMRecord;

/**
 * Unit test for FilterConsensus.
 */
public final class FilterConsensusTest {
    private File inputBamFile;
    private File inputGenomicFile;
    private File outputFile;
    private String gene;
    private boolean cdna;
    private boolean removeGaps;
    private double minimumBreadth;
    private int expectedPloidy;

    @Before
    public void setUp() throws Exception {
        inputBamFile = File.createTempFile("filterConsensusTest", ".bam");
        inputGenomicFile = File.createTempFile("filterConsensusTest", ".txt");
        outputFile = File.createTempFile("filterConsensusTest", ".fa");
        gene = "HLA-A";
        cdna = true;
        removeGaps = true;
        minimumBreadth = 0.5d;
        expectedPloidy = 2;
    }

    @After
    public void tearDown() throws Exception {
        inputBamFile.delete();
        inputGenomicFile.delete();
    }

    @Test
    public void testConstructor() {
        assertNotNull(new FilterConsensus(inputBamFile, inputGenomicFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy));
    }

    @Test
    public void testEmptyReadGenomicFile() throws Exception {
        Map<Integer, Allele> exons = readGenomicFile(inputGenomicFile);
        assertTrue(exons.isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadGenomicFileInvalidTokens() throws Exception {
        copyResource("invalid-tokens.txt", inputGenomicFile);
        readGenomicFile(inputGenomicFile);
    }

    @Test(expected=IOException.class)
    public void testReadGenomicFileInvalidExon() throws Exception {
        copyResource("invalid-exon.txt", inputGenomicFile);
        readGenomicFile(inputGenomicFile);
    }

    @Test(expected=IOException.class)
    public void testReadGenomicFileInvalidLocus() throws Exception {
        copyResource("invalid-locus.txt", inputGenomicFile);
        readGenomicFile(inputGenomicFile);
    }

    @Test
    public void testReadGenomicFile() throws Exception {
        copyResource("hla-a.txt", inputGenomicFile);
        Map<Integer, Allele> exons = readGenomicFile(inputGenomicFile);
        assertFalse(exons.isEmpty());
        assertTrue(exons.containsKey(2));
        assertTrue(exons.containsKey(3));
    }

    @Test
    public void testEmptyCigarToEditList() throws Exception {
        List<Edit> edits = cigarToEditList(new SAMRecord(new SAMFileHeader()));
        assertTrue(edits.isEmpty());
    }

    @Test
    public void testRun() throws Exception {
        copyResource("hla-a.bam", inputBamFile);
        copyResource("hla-a.txt", inputGenomicFile);
        new FilterConsensus(inputBamFile, inputGenomicFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy).run();
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(FilterConsensusTest.class.getResource(name)), file);        
    }
    
    @Test
    public void testRunPloidy() throws Exception {
              // copy hla-a.bam resource to inputBamFile
        Files.write(Resources.toByteArray(getClass().getResource("testfile2.bam")), inputBamFile);

        // copy hla-a.txt to inputGenomicFile
        Files.write(Resources.toByteArray(getClass().getResource("hla-a.txt")), inputGenomicFile);
        
        new FilterConsensus(inputBamFile, inputGenomicFile, outputFile, gene, cdna, removeGaps, minimumBreadth, expectedPloidy).run();
    
        int lines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(outputFile));
        
        while(reader.readLine() != null) {
          lines++;
        }
        reader.close();
        
        assertEquals(lines, 4);
        
        new FilterConsensus(inputBamFile, inputGenomicFile, outputFile, gene, cdna, removeGaps, minimumBreadth, 3).run();
    
        lines = 0;
        reader = new BufferedReader(new FileReader(outputFile));
        
        while(reader.readLine() != null) {
          lines++;
        }
        reader.close();
        
        assertEquals(lines, 6);
    }
}
