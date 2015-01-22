/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.align.HspReader.read;
import static org.nmdp.ngs.align.HspWriter.write;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for HspWriter.
 */
public final class HspWriterTest {
    private HighScoringPair hsp;
    private Iterable<HighScoringPair> hsps;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        hsps = read(new StringReader("source\ttarget\t99.0\t100\t1\t2\t1\t100\t2\t101\t0.1\t1.0"));
        hsp = hsps.iterator().next();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @After
    public void tearDown() throws Exception {
        writer.close();
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullHsp() throws Exception {
        write((HighScoringPair) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(hsp, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(hsp, writer);
        assertEquals("source\ttarget\t99.0\t100\t1\t2\t1\t100\t2\t101\t0.1\t1.0", stringWriter.toString().trim());
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullHsps() throws Exception {
        write((Iterable<HighScoringPair>) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullWriter() throws Exception {
        write(hsps, null);
    }

    @Test
    public void testWriteIterable() throws Exception {
        write(hsps, writer);
        assertEquals("source\ttarget\t99.0\t100\t1\t2\t1\t100\t2\t101\t0.1\t1.0", stringWriter.toString().trim());
    }
}
