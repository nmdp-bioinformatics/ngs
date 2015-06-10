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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.tools.ValidateInterpretation.matchByField;
import static org.nmdp.ngs.tools.ValidateInterpretation.read;
import static org.nmdp.ngs.tools.ValidateInterpretation.sameLocus;

import java.io.File;
import java.io.IOException;

import java.util.List;

import com.google.common.base.Charsets;

import com.google.common.collect.ListMultimap;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.nmdp.gl.AlleleList;
import org.nmdp.gl.Genotype;

import org.nmdp.gl.client.GlClient;

import org.nmdp.gl.client.local.LocalGlClient;

import org.nmdp.ngs.tools.ValidateInterpretation.Interpretation;

/**
 * Unit test for ValidateInterpretation.
 */
public final class ValidateInterpretationTest {
    private File expectedFile;
    private File observedFile;
    private File outputFile;
    private int resolution;
    private List<String> loci;
    private boolean printSummary;
    private GlClient glclient;
    private AlleleList alleleList0;
    private AlleleList alleleList1;
    private AlleleList alleleList2;
    private ValidateInterpretation validateInterpretation;

    @Before
    public void setUp() throws Exception {
        expectedFile = File.createTempFile("validateInterpretationTest", "txt");
        observedFile = File.createTempFile("validateInterpretationTest", "txt");
        outputFile = File.createTempFile("validateInterpretationTest", "txt");
        resolution = ValidateInterpretation.DEFAULT_RESOLUTION;
        loci = ValidateInterpretation.DEFAULT_LOCI;
        printSummary = false;
        glclient = LocalGlClient.create();
        alleleList0 = glclient.createAlleleList("HLA-A*03:01:01/HLA-A*03:01:01");
        alleleList1 = glclient.createAlleleList("HLA-A*03:01:02/HLA-A*03:01:03");
        alleleList2 = glclient.createAlleleList("HLA-C*05:24/HLA-C*05:25");

        validateInterpretation = new ValidateInterpretation(expectedFile, observedFile, outputFile, resolution, loci, printSummary, glclient);
    }

    @After
    public void tearDown() throws Exception {
        expectedFile.delete();
        observedFile.delete();
        outputFile.delete();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNullExpectedFileAndObservedFile() {
        new ValidateInterpretation(null, null, outputFile, resolution, loci, printSummary, glclient);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorResolutionTooLow() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, 0, loci, printSummary, glclient);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorResolutionTooHigh() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, 5, loci, printSummary, glclient);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullLoci() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, resolution, null, printSummary, glclient);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullGlclient() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, resolution, loci, printSummary, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(validateInterpretation);
    }

    @Test(expected=NullPointerException.class)
    public void testMatchByFieldNullFirstAllele() {
        matchByField(null, "HLA-A*01:01:01:01");
    }

    @Test(expected=NullPointerException.class)
    public void testMatchByFieldNullSecondAllele() {
        matchByField("HLA-A*01:01:01:01", null);
    }

    @Test
    public void testMatchByField() {
        assertEquals(1, matchByField("HLA-A*01", "HLA-A*01"));
        assertEquals(1, matchByField("HLA-A*01:01", "HLA-A*01"));
        assertEquals(1, matchByField("HLA-A*01:02:01", "HLA-A*01:01:01"));
        assertEquals(2, matchByField("HLA-DQB1*05:02:01", "HLA-DQB1*05:02:04"));
        assertEquals(3, matchByField("HLA-A*01:01:01", "HLA-A*01:01:01"));
        assertEquals(4, matchByField("HLA-A*01:01:01:01", "HLA-A*01:01:01:01"));
        assertEquals(0, matchByField("HLA-DRB1*13:01:01", "HLA-DRB3*01:01:02:01"));
        assertEquals(0, matchByField("HLA-DRB1*13:01:01", "HLA-DRB3*01:01:02:02"));
    }

    @Test(expected=NullPointerException.class)
    public void testSameLocusNullAlleleList0() {
        sameLocus(null, alleleList1);
    }

    @Test(expected=NullPointerException.class)
    public void testSameLocusNullAlleleList1() {
        sameLocus(alleleList0, null);
    }

    @Test
    public void testSameLocus() {
        assertTrue(sameLocus(alleleList0, alleleList1));
        assertFalse(sameLocus(alleleList0, alleleList2));
    }

    @Test
    public void testReadExpectedEmpty() throws Exception {
        assertTrue(read(expectedFile).isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadExpectedInvalid() throws Exception {
        copyResource("expected-invalid.txt", expectedFile);
        read(expectedFile);
    }

    @Test
    public void testReadExpected() throws Exception {
        copyResource("expected.txt", expectedFile);
        ListMultimap<String, Interpretation> expected = read(expectedFile);
        assertNotNull(expected);
        assertEquals(1, expected.get("sample0").size());
        assertEquals("HLA-A*03:01:01+HLA-A*03:01:01", expected.get("sample0").get(0).glstring());
        assertEquals(1, expected.get("sample2").size());
        assertEquals("HLA-A*03:01:01+HLA-A*03:01:02", expected.get("sample2").get(0).glstring());
    }

    @Test
    public void testReadObservedEmpty() throws Exception {
        assertTrue(read(observedFile).isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadObservedInvalid() throws Exception {
        copyResource("observed-invalid.txt", observedFile);
        read(observedFile);
    }

    @Test
    public void testReadObserved() throws Exception {
        copyResource("observed.txt", observedFile);
        ListMultimap<String, Interpretation> observed = read(observedFile);
        assertNotNull(observed);
        assertEquals(2, observed.get("sample0").size());
        assertEquals("HLA-A*03:01:01/HLA-A*03:01:01", observed.get("sample0").get(0).glstring());
        assertEquals(1, observed.get("sample2").size());
        assertEquals("HLA-A*03:01:02/HLA-A*03:01:03", observed.get("sample2").get(0).glstring());
    }

    @Test(expected=NullPointerException.class)
    public void testShouldValidateNullInterpretation() {
        validateInterpretation.shouldValidate(null, alleleList0);
    }

    @Test(expected=NullPointerException.class)
    public void testShouldValidateNullAlleleList() {
        Interpretation interpretation = Interpretation.builder().withLocus("HLA-A").build();
        validateInterpretation.shouldValidate(interpretation, null);
    }

    @Test
    public void testShouldValidate() {
        Interpretation interpretation = Interpretation.builder().withLocus("HLA-A").build();
        assertTrue(validateInterpretation.shouldValidate(interpretation, alleleList0));
    }

    @Test
    public void testShouldValidateLocusNotInLoci() {
        Interpretation interpretation = Interpretation.builder().withLocus("HLA-G").build();
        assertFalse(validateInterpretation.shouldValidate(interpretation, alleleList0));
    }

    @Test
    public void testShouldValidateNonMatchingLoci() {
        Interpretation interpretation = Interpretation.builder().withLocus("HLA-A").build();
        assertFalse(validateInterpretation.shouldValidate(interpretation, alleleList2));
    }

    @Test(expected=NullPointerException.class)
    public void testAsAlleleListNullInterpretation() throws Exception {
        validateInterpretation.asAlleleList(null);
    }

    @Test
    public void testAsAlleleList() throws Exception {
        Interpretation interpretation = Interpretation.builder().withGlstring("HLA-A*01:01:01:01/HLA-A*01:01:01:02N").build();
        AlleleList alleleList = validateInterpretation.asAlleleList(interpretation);
        assertNotNull(alleleList);
        assertEquals("HLA-A*01:01:01:01/HLA-A*01:01:01:02N", alleleList.getGlstring());
    }

    @Test(expected=IOException.class)
    public void testAsAlleleListInvalidSyntax() throws Exception {
        Interpretation interpretation = Interpretation.builder().withGlstring("invalid syntax").build();
        validateInterpretation.asAlleleList(interpretation);
    }

    @Test(expected=NullPointerException.class)
    public void testAsGenotypeNullInterpretation() throws Exception {
        validateInterpretation.asGenotype(null);
    }

    @Test
    public void testAsGenotype() throws Exception {
        Interpretation interpretation = Interpretation.builder().withGlstring("HLA-A*01:01:01:01+HLA-A*01:01:01:02N").build();
        Genotype genotype = validateInterpretation.asGenotype(interpretation);
        assertNotNull(genotype);
        assertEquals("HLA-A*01:01:01:01+HLA-A*01:01:01:02N", genotype.getGlstring());
    }

    @Test(expected=IOException.class)
    public void testAsGenotypeInvalidSyntax() throws Exception {
        Interpretation interpretation = Interpretation.builder().withGlstring("invalid syntax").build();
        validateInterpretation.asGenotype(interpretation);
    }

    @Test
    public void testValidateInterpretationResolution1() throws Exception {
        copyResource("expected.txt", expectedFile);
        copyResource("observed.txt", observedFile);
        ValidateInterpretation resolution1 = new ValidateInterpretation(expectedFile, observedFile, outputFile, 1, loci, printSummary, glclient);
        Integer result = resolution1.call();
        for (String output : Files.readLines(outputFile, Charsets.UTF_8)) {
            assertTrue(output.startsWith("PASS"));
        }
    }

    @Test
    public void testValidateInterpretation() throws Exception {
        copyResource("expected.txt", expectedFile);
        copyResource("observed.txt", observedFile);
        Integer result = validateInterpretation.call();
        for (String output : Files.readLines(outputFile, Charsets.UTF_8)) {
        	System.out.println(output);
            assertTrue(output.startsWith("PASS"));
        }
    }

    @Test
    public void testValidateInterpretationResolution3() throws Exception {
        copyResource("expected.txt", expectedFile);
        copyResource("observed.txt", observedFile);
        ValidateInterpretation resolution3 = new ValidateInterpretation(expectedFile, observedFile, outputFile, 3, loci, printSummary, glclient);
        Integer result = resolution3.call();

        int passes = 0;
        int failures = 0;
        for (String output : Files.readLines(outputFile, Charsets.UTF_8)) {
            if (output.startsWith("PASS")) {
                passes++;
            }
            else {
                failures++;
                assertTrue(output.contains("HLA-A*03:01:01"));
            }
        }
        assertEquals(5, passes);
        assertEquals(1, failures);
    }

    @Test
    public void testValidateInterpretationResolution4() throws Exception {
        copyResource("expected.txt", expectedFile);
        copyResource("observed.txt", observedFile);
        ValidateInterpretation resolution4 = new ValidateInterpretation(expectedFile, observedFile, outputFile, 4, loci, printSummary, glclient);
        Integer result = resolution4.call();
        for (String output : Files.readLines(outputFile, Charsets.UTF_8)) {
            assertTrue(output.startsWith("FAIL"));
        }
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(FilterConsensusTest.class.getResource(name)), file);
    }
}
