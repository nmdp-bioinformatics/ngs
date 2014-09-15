/*

    ngs-gtr  Mapping for GTR XSDs.
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
package org.nmdp.ngs.gtr;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

import org.nmdp.ngs.gtr.jaxb.GTRPublicData;

import org.xml.sax.SAXException;

/**
 * Reader for GTR public data xml.
 */
public final class GtrReader {

    /**
     * Private no-arg constructor.
     */
    private GtrReader() {
        // empty
    }


    /**
     * Read GTR public data from the specified reader.
     *
     * @param reader reader to read from, must not be null
     * @return the GTR public data read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static GTRPublicData read(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            // todo:  may want to cache some of this for performance reasons
            JAXBContext context = JAXBContext.newInstance(GTRPublicData.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL schemaURL = GtrReader.class.getResource("/org/nmdp/ngs/gtr/xsd/GTRPublicData.xsd");
            Schema schema = schemaFactory.newSchema(schemaURL);
            unmarshaller.setSchema(schema);
            return (GTRPublicData) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal GTRPublicData", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal GTRPublicData", e);
        }
    }

    /**
     * Read GTR public data from the specified file.
     *
     * @param file file to read from, must not be null
     * @return the GTR public data read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static GTRPublicData read(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return read(reader);
        }
    }

    /**
     * Read GTR public data from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return the GTR public data read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static GTRPublicData read(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return read(reader);
        }
    }

    /**
     * Read GTR public data from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return the GTR public data read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static GTRPublicData read(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return read(reader);
        }
    }
}
