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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for SplitFasta.
 */
public final class SplitFastaTest {
    private File fastaFile;
    private String outputFilePrefix;
    private String outputFileExtension;
    private File outputDirectory;

    @Before
    public void setUp() throws Exception {
        fastaFile = File.createTempFile("splitFastaTest", "fa");
        outputFilePrefix = "outputFilePrefix";
        outputFileExtension = "fa";
        outputDirectory = File.createTempFile("splitFastaTest", "dir");
    }

    @After
    public void tearDown() throws Exception {
        fastaFile.delete();
        outputDirectory.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOutputFilePrefix() {
        new SplitFasta(fastaFile, null, outputFileExtension, outputDirectory);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOutputFileExtension() {
        new SplitFasta(fastaFile, outputFilePrefix, null, outputDirectory);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullOutputDirectory() {
        new SplitFasta(fastaFile, outputFilePrefix, outputFileExtension, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new SplitFasta(fastaFile, outputFilePrefix, outputFileExtension, outputDirectory));
    }
}
