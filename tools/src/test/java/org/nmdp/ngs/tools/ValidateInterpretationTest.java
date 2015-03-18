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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.nmdp.ngs.tools.ValidateInterpretation.matchByField;
import static org.nmdp.ngs.tools.ValidateInterpretation.readExpected;
import static org.nmdp.ngs.tools.ValidateInterpretation.readObserved;
import static org.nmdp.ngs.tools.ValidateInterpretation.SubjectTyping;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ListMultimap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for ValidateInterpretation.
 */
public final class ValidateInterpretationTest {
    private File expectedFile;
    private File observedFile;
    private File outputFile;
    private int resolution;
    private boolean printSummary;
    private boolean HaploidBoolean;
    private boolean GlstringBoolean;

    private String baseurl;
    private List<String> lociList;

    @Before
    public void setUp() throws Exception {
        expectedFile = File.createTempFile("validateInterpretationTest", "txt");
        observedFile = File.createTempFile("validateInterpretationTest", "txt");
        resolution = ValidateInterpretation.DEFAULT_RESOLUTION;
        printSummary = false;
    }

    @After
    public void tearDown() throws Exception {
        expectedFile.delete();
        observedFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullExpectedFile() {
        new ValidateInterpretation(null, observedFile, lociList, HaploidBoolean,baseurl, GlstringBoolean, outputFile, resolution, printSummary);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullObservedFile() {
        new ValidateInterpretation(expectedFile, null, lociList, HaploidBoolean, baseurl, GlstringBoolean, outputFile, resolution, printSummary);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNullResolutionTooLow() {
        new ValidateInterpretation(expectedFile, observedFile, lociList,  HaploidBoolean, baseurl, GlstringBoolean, outputFile, 0, printSummary);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNullResolutionTooHigh() {
        new ValidateInterpretation(expectedFile, observedFile, lociList, HaploidBoolean, baseurl, GlstringBoolean, outputFile, 5, printSummary);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ValidateInterpretation(expectedFile, observedFile, lociList,  HaploidBoolean, baseurl, GlstringBoolean, outputFile, resolution, printSummary));
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

    @Test
    public void testReadExpectedEmpty() throws Exception {
        assertTrue(readExpected(expectedFile).isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadExpectedInvalid() throws Exception {
        copyResource("expected-invalid.txt", expectedFile);
        readExpected(expectedFile);
    }

    @Test
    public void testReadExpected() throws Exception {
        copyResource("expected.txt", expectedFile);
        Map<String, SubjectTyping> expected = readExpected(expectedFile);
        assertNotNull(expected);
        assertTrue(expected.get("sample0").getTyping("HLA-A").contains("firstAllele0"));
        assertTrue(expected.get("sample0").getTyping("HLA-A").contains("secondAllele0"));
        assertTrue(expected.get("sample1").getTyping("HLA-A").contains("firstAllele1"));
        assertTrue(expected.get("sample1").getTyping("HLA-A").contains("secondAllele1"));
        assertTrue(expected.get("sample2").getTyping("HLA-A").contains("firstAllele2"));
        assertTrue(expected.get("sample2").getTyping("HLA-A").contains("secondAllele2"));
    }

    @Test
    public void testReadObservedEmpty() throws Exception {
        assertTrue(readObserved(observedFile).isEmpty());
    }

    @Test(expected=IOException.class)
    public void testReadObservedInvalid() throws Exception {
        copyResource("observed-invalid.txt", observedFile);
        readObserved(observedFile);
    }

    @Test
    public void testReadObserved() throws Exception {
        copyResource("observed.txt", observedFile);
        Map<String, SubjectTyping> observed = readObserved(observedFile);
        assertNotNull(observed);
//        assertTrue(observed.get("sample0").contains("interpretation0-0"));
//        assertTrue(observed.get("sample0").contains("interpretation0-1"));
//        assertTrue(observed.get("sample1").contains("interpretation1-0"));
//        assertTrue(observed.get("sample1").contains("interpretation1-1"));
    }

    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(FilterConsensusTest.class.getResource(name)), file);
    }
}
