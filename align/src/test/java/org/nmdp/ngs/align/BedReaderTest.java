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

import static org.junit.Assert.assertNotNull;

import static org.nmdp.ngs.align.BedReader.read;
import static org.nmdp.ngs.align.BedReader.stream;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for BedReader.
 */
public final class BedReaderTest {
    private Readable readable;

    @Before
    public void setUp() {
        readable = new StringReader("chr1\t11873\t14409\tuc001aaa.3\t0\t+\t11873\t11873\t0\t3\t354,109,1189,\t0,739,1347,");
    }

    @Test(expected=NullPointerException.class)
    public void testReadNullReadable() throws Exception {
        read(null);
    }

    @Test
    public void testRead() throws Exception {
        for (BedRecord record : read(readable)) {
            assertNotNull(record);
        }
    }

    @Test(expected=IOException.class)
    public void testReadInvalid() throws Exception {
        read(new StringReader("invalid"));
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream(null, new BedListener() {
                @Override
                public boolean record(final BedRecord record) {
                    return true;
                }
            });
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, new BedListener() {
                @Override
                public boolean record(final BedRecord record) {
                    assertNotNull(record);
                    return true;
                }
            });
    }
}
