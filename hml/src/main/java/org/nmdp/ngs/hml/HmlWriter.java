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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.XMLConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.nmdp.ngs.hml.jaxb.Hml;

import org.xml.sax.SAXException;

/**
 * Writer for HML xml.
 */
public final class HmlWriter {

    /**
     * Private no-arg constructor.
     */
    private HmlWriter() {
        // empty
    }


    /**
     * Write the specified HML to the specified writer.
     *
     * @param data HML to write, must not be null
     * @param writer writer to write to, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void write(final Hml data, final Writer writer) throws IOException {
        checkNotNull(data);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Hml.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaURL = HmlReader.class.getResource("/org/nmdp/ngs/hml/xsd/hml-1.0.xsd");
            Schema schema = schemaFactory.newSchema(schemaURL);
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(data, writer);
        }
        catch (JAXBException | SAXException e) {
            e.printStackTrace();
            throw new IOException("could not marshal HML", e);
        }
    }

    /**
     * Write the specified HML to the specified file.
     *
     * @param data HML to write, must not be null
     * @param file file to write to, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void write(final Hml data, final File file) throws IOException {
        checkNotNull(data);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            write(data, writer);
        }
    }

    /**
     * Write the specified HML to the specified output stream.
     *
     * @param data HML to write, must not be null
     * @param outputStream output stream to write to, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void write(final Hml data, final OutputStream outputStream) throws IOException {
        checkNotNull(data);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            write(data, writer);
        }
    }
}
