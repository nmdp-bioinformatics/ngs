/*

    ngs-feature  Features.
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
package org.nmdp.ngs.feature;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import java.util.zip.GZIPInputStream;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.SimpleAnnotation;

import org.nmdp.ngs.variant.vcf.VcfRecord;

import org.nmdp.ngs.feature.parser.FeatureParser;
import org.nmdp.ngs.feature.parser.ParseException;

public final class VcfFile {
    public final static int BUFFER_SIZE = 2048;
  
    public final static String INFO = "INFO";
    public final static String FILTER = "FILTER";
    public final static String FORMAT = "FORMAT";
    public final static String CONTIG = "contig";
  
    public final class Iterator implements java.util.Iterator<VcfRecord> {
        private long lineNumber;
        private final InputStream stream;
        private final byte [] buffer = new byte[BUFFER_SIZE];
        public int bytes;

        public Iterator(final InputStream stream) {
            lineNumber = 0;
            bytes = 0;
            this.stream = stream;
        }

        @Override
        public boolean hasNext() {
            try {
                return stream.available() == 1;
            }
            catch (IOException exception) {
                return false;
            }
        }

        @Override
        public VcfRecord next() {
            try {
                bytes += stream.read(buffer);
                return FeatureParser.parseVcfRecord(Arrays.toString(buffer), lineNumber);
            }
            catch (IOException | ParseException exception) {
                return null;
            }
        }

        @Override
        public void remove() {
            // empty
        }
    }

    public Iterator iterator() throws IOException {
        stream.reset();
        return new Iterator(stream);
    }

    public static enum Strictness {
        /**
         * Do not log syntax errors
         */
        NONE,
        /**
         * Log syntax errors but do not fail
         */
        RESEARCH,
        /**
         * Fail on syntax errors
         */
        CLINICAL
    }
  
    public final String filename;
    public final String format;
    public final String reference;
    public final Header header;
    public final List<Metadata> metadata;
    public final Strictness strictness;
    private InputStream stream;
    private final byte [] buffer = new byte[BUFFER_SIZE];
    private int bytes;
  
    public final static class Header {
        public final List<String> names;

        public Header(final List<String> names) {
            this.names = ImmutableList.copyOf(names);
        }
    }
  
    public static class Metadata {
        public final static String ID = "ID";
        public final static String DESCRIPTION = "Description";
        public final static String INTEGER ="Integer";
        public final static String FLOAT = "Float";
        public final static String CHARACTER = "Character";
        public final static String STRING = "String";
        public final static String FLAG = "Flag";

        public enum Type {
            UNKNOWN,
            INTEGER,
            FLOAT,
            CHARACTER,
            STRING,
            FLAG
        }

        private String name;
        private String description;
        protected SimpleAnnotation annotation;

        public Metadata(final String name, final String description) {
            this.name = name;
            this.description = description;
        }
    
        public Metadata(final SimpleAnnotation annotation) throws VcfFileException {
            this.annotation = annotation;
            try {
                name = ((ArrayList<String>) this.annotation.getProperty(ID)).get(0);
            }
            catch(NoSuchElementException exception) {
                throw new VcfFileException("missing metadata field \"" + ID + "\"");
            }

            try {
                description = ((ArrayList<String>) this.annotation.getProperty(DESCRIPTION)).get(0);
            }
            catch(NoSuchElementException exception) {
                throw new VcfFileException("missing metadata field \"" + DESCRIPTION + "\"");
            }
        }

        public final String getName() {
            return name;
        }

        public final String getDescription() {
            return description.replace(">", "");
        }

        public final SimpleAnnotation getFullAnnotation() {
            return annotation;
        }
    }

    public static class Info extends VcfFile.Metadata {
        public final static String NUMBER = "Number";
        public final static String TYPE = "Type";

        private Type type;
        private Object number;

        public Info(final SimpleAnnotation annotation) throws VcfFileException {
            super(annotation);

            try {
                number = ((ArrayList<String>) this.annotation.getProperty(NUMBER)).get(0);
            }
            catch(NoSuchElementException exception) {
                throw new VcfFileException("missing " + INFO + " field \"" + NUMBER + "\"");
            }

            try {
                String argument = ((ArrayList<String>) this.annotation.getProperty(TYPE)).get(0);

                switch (argument) {
                case INTEGER:
                    type = VcfFile.Metadata.Type.INTEGER;
                    break;
                case FLOAT:
                    type = VcfFile.Metadata.Type.FLOAT;
                    break;
                case STRING:
                    type = VcfFile.Metadata.Type.STRING;
                    break;
                case FLAG:
                    type = VcfFile.Metadata.Type.FLAG;
                    break;
                case CHARACTER:
                    type = VcfFile.Metadata.Type.CHARACTER;
                    break;
                default:
                    type = VcfFile.Metadata.Type.UNKNOWN;
                    break;
                }
            }
            catch(NoSuchElementException exception) {
                throw new VcfFileException("missing " + INFO + " field \"" + TYPE + "\"");
            }
        }

        public Object getNumber() {
            return number;
        }

        public Type getType() {
            return type;
        }
    }

    public final static class Filter extends VcfFile.Metadata {
        public Filter(final SimpleAnnotation annotation) throws VcfFileException {
            super(annotation);
        }
    }

    public final static class Format extends VcfFile.Info {
        public Format(final SimpleAnnotation annotation) throws VcfFileException {
            super(annotation);
        }
    }
  
    public VcfFile(final String filename) throws IOException, ParseException {
        this.filename = filename;
        this.format = "UNKNOWN";
        this.reference = "UNKNOWN";
        this.header = null;
        this.metadata = new ArrayList<Metadata>();
        strictness = Strictness.NONE;
        bytes = 0;

        this.stream = new FileInputStream(filename);

        if(filename.endsWith(".gz")) {
            try {
                this.stream = new GZIPInputStream(stream);
            }
            finally {
                // stream.close();
            }
        }

        bytes += stream.read(buffer);
        String line = new String(buffer, "UTF8");
        //System.out.println("bytes = " + bytes + " line = " + line);

        while(line.startsWith("#")) {
            metadata.add(FeatureParser.parseVcfMetadata(line));
            bytes += stream.read(buffer);
            line = Arrays.toString(buffer);
            //System.out.println("bytes = " + bytes + " line = " + line);
        }

        stream.mark(120);
    }
}
