/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * VCF header parser.
 */
public final class VcfHeaderParser {

    /**
     * Private no-arg constructor.
     */
    private VcfHeaderParser() {
        // empty
    }


    /**
     * Read the VCF header from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return the VCF header read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static VcfHeader header(final Readable readable) throws IOException {
        checkNotNull(readable);
        ParseListener parseListener = new ParseListener();
        VcfParser.parse(readable, parseListener);
        return parseListener.getHeader();
    }

    /**
     * Parse listener.
     */
    static final class ParseListener extends VcfParseAdapter {
        /** File format. */
        private String fileFormat;

        /** List of meta-information header lines. */
        private List<String> meta = new ArrayList<String>();


        @Override
        public void meta(final String meta) throws IOException {
            this.meta.add(meta.trim());
            if (meta.startsWith("##fileformat=")) {
                fileFormat = meta.substring(13).trim();
            }
        }

        @Override
        public boolean complete() throws IOException {
            return false;
        }

        /**
         * Return the VCF header.
         *
         * @return the VCF header
         * @throws IOException if an I/O error occurs
         */
        VcfHeader getHeader() throws IOException {
            if (fileFormat == null) {
                throw new IOException("could not read header, required fileformat meta-information header line not found");
            }
            return new VcfHeader(fileFormat, meta);
        }
    };
}
