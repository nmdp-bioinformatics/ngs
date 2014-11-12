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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for IntersectBed.
 */
public final class IntersectBedTest {
    private File aInputFile;
    private File bInputFile;
    private File outputFile;
    private IntersectBed.Strategy strategy;

    @Before
    public void setUp() throws Exception {
        bInputFile = File.createTempFile("intersectBedTest", ".bed");
        strategy = new IntersectBed.Strategy() {
                @Override
                public void intersectBed(final BufferedReader a, final BufferedReader b, final PrintWriter w) throws IOException {
                    // empty
                }
            };
    }

    @After
    public void tearDown() throws Exception {
        bInputFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullBInputFile() {
        new IntersectBed(aInputFile, null, outputFile, strategy);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullStrategy() {
        new IntersectBed(aInputFile, bInputFile, outputFile, null);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new IntersectBed(aInputFile, bInputFile, outputFile, strategy));
    }
}
