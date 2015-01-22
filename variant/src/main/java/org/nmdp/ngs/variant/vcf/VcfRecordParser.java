/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * VCF record parser.
 */
public final class VcfRecordParser {
    /** Arbitrary large capacity for list of VCF records. */
    private static final int CAPACITY = 100000;


    /**
     * Private no-arg constructor.
     */
    private VcfRecordParser() {
        // empty
    }


    /**
     * Read zero or more VCF records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more VCF records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfRecord> records(final Readable readable) throws IOException {
        checkNotNull(readable);
        ParseListener parseListener = new ParseListener();
        VcfParser.parse(readable, parseListener);
        return parseListener.getRecords();
    }

    /**
     * Parse listener.
     */
    static final class ParseListener extends VcfParseAdapter {
        /** VCF record builder. */
        private final VcfRecord.Builder builder = VcfRecord.builder();

        /** List of VCF records. */
        private List<VcfRecord> records = new ArrayList<VcfRecord>(CAPACITY);


        @Override
        public void lineNumber(final long lineNumber) throws IOException {
            builder.withLineNumber(lineNumber);
        }

        @Override
        public void chrom(final String chrom) throws IOException {
            builder.withChrom(chrom);
        }

        @Override
        public void pos(final long pos) throws IOException {
            builder.withPos(pos);
        }

        @Override
        public void id(final String... id) throws IOException {
            builder.withId(id);
        }

        @Override
        public void ref(final String ref) throws IOException {
            builder.withRef(ref);
        }

        @Override
        public void alt(final String... alt) throws IOException {
            builder.withAlt(alt);
        }

        @Override
        public void qual(final double qual) throws IOException {
            builder.withQual(qual);
        }

        @Override
        public void filter(final String... filter) throws IOException {
            builder.withFilter(filter);
        }

        @Override
        public void info(final String infoId, final String... values) throws IOException {
            builder.withInfo(infoId, values);
        }

        @Override
        public void format(final String... format) throws IOException {
            builder.withFormat(format);
        }

        @Override
        public void genotype(final String sampleId, final String formatId, final String... values) throws IOException {
            builder.withGenotype(sampleId, formatId, values);
        }

        @Override
        public boolean complete() throws IOException {
            records.add(builder.build());
            builder.reset();
            return true;
        }

        /**
         * Return the list of VCF records.
         *
         * @return the list of VCF records
         */
        List<VcfRecord> getRecords() {
            return records;
        }
    }
}
