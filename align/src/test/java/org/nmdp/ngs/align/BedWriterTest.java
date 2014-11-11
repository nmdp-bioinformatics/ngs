/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.align.BedReader.read;
import static org.nmdp.ngs.align.BedWriter.write;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for BedWriter.
 */
public final class BedWriterTest {
    private BedRecord record;
    private Iterable<BedRecord> records;
    private StringWriter stringWriter;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        records = read(new StringReader("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,"));
        record = records.iterator().next();
        stringWriter = new StringWriter();
        writer = new PrintWriter(stringWriter);
    }

    @After
    public void tearDown() throws Exception {
        writer.close();
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullRecord() throws Exception {
        write((BedRecord) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(record, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(record, writer);
        // note writer does not add trailing commas to blockSizes and blockStarts as examples contain
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189\t0,739,1347", stringWriter.toString().trim());
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullRecords() throws Exception {
        write((Iterable<BedRecord>) null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteIterableNullWriter() throws Exception {
        write(records, null);
    }

    @Test
    public void testWriteIterable() throws Exception {
        write(records, writer);
        // note writer does not add trailing commas to blockSizes and blockStarts as examples contain
        assertEquals("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189\t0,739,1347", stringWriter.toString().trim());
    }
}
