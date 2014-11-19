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

import static org.junit.Assert.assertNotNull;

import java.io.File;

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
        new ValidateInterpretation(null, observedFile, outputFile, resolution, printSummary);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullObservedFile() {
        new ValidateInterpretation(expectedFile, null, outputFile, resolution, printSummary);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNullResolutionTooLow() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, 0, printSummary);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorNullResolutionTooHigh() {
        new ValidateInterpretation(expectedFile, observedFile, outputFile, 5, printSummary);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new ValidateInterpretation(expectedFile, observedFile, outputFile, resolution, printSummary));
    }
}
