/*

    ngs-hml  Mapping for HML XSDs.
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
package org.nmdp.ngs.hml;

import static org.nmdp.ngs.hml.HmlWriter.write;

import java.net.URL;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;

import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Unit test for HmlWriter.
 */
public final class HmlWriterTest {
    private Hml data;

    @Before
    public void setUp() {
        data = new Hml();
        data.setVersion("0.9.4");
        data.setProjectName("LAB");
        data.setReportingCenter("789");

        Sample sample = new Sample();
        sample.setId("123456789");
        sample.setCenterCode("321");

        Typing typing = new Typing();
        typing.setGeneFamily("HLA");
        sample.getTyping().add(typing);
        data.getSample().add(sample);        
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(data, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullFile() throws Exception {
        write(data, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullOutputStream() throws Exception {
        write(data, (OutputStream) null);
    }

    @Test
    public void testWriteWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            write(data, writer);
        }
    }

    @Test
    public void testWriteFile() throws Exception {
        write(data, createFile());
    }

    @Test
    public void testWriteOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            write(data, outputStream);
        }
    }

    private static File createFile() throws Exception {
        File file = File.createTempFile("hmlWriterTest", ".xml");
        file.deleteOnExit();
        return file;
    }

    private static OutputStream createOutputStream() throws Exception {
        return new ByteArrayOutputStream();
    }
}
