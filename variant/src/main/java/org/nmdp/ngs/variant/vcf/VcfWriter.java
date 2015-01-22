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

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

/**
 * VCF writer.
 */
public final class VcfWriter {

    /**
     * Private no-arg constructor.
     */
    private VcfWriter() {
        // empty
    }


    /**
     * Write VCF with the specified print writer.
     *
     * @param header VCF header, must not be null
     * @param samples zero or more VCF samples, must not be null
     * @param records zero or more VCF records, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void write(final VcfHeader header,
                             final List<VcfSample> samples,
                             final List<VcfRecord> records,
                             final PrintWriter writer) {

        writeHeader(header, writer);
        writeColumnHeader(samples, writer);
        writeRecords(samples, records, writer);
    }

    /**
     * Write VCF header with the specified print writer.
     *
     * @param header VCF header, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeHeader(final VcfHeader header, final PrintWriter writer) {
        checkNotNull(header);
        checkNotNull(writer);

        for (String meta : header.getMeta()) {
            writer.println(meta);
        }
    }

    /**
     * Write VCF column header with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeColumnHeader(final List<VcfSample> samples, final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(writer);

        StringBuilder sb = new StringBuilder("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO");
        if (!samples.isEmpty()) {
            sb.append("\tFORMAT");
        }
        for (VcfSample sample : samples) {
            sb.append("\t");
            sb.append(sample.getId());
        }
        writer.println(sb.toString());
    }

    /**
     * Write VCF records with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param records zero or more VCF records, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeRecords(final List<VcfSample> samples,
                                    final List<VcfRecord> records,
                                    final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(records);
        checkNotNull(writer);

        for (VcfRecord record : records) {
            writeRecord(samples, record, writer);
        }
    }

    /**
     * Write VCF record with the specified print writer.
     *
     * @param samples zero or more VCF samples, must not be null
     * @param record VCF record, must not be null
     * @param writer print writer to write VCF with, must not be null
     */
    public static void writeRecord(final List<VcfSample> samples, final VcfRecord record, final PrintWriter writer) {
        checkNotNull(samples);
        checkNotNull(record);
        checkNotNull(writer);

        StringBuilder sb = new StringBuilder();
        sb.append(record.getChrom());
        sb.append("\t");
        sb.append(record.getPos());

        sb.append("\t");
        if (record.getId().length == 0) {
            sb.append(".");
        }
        else {
            sb.append(Joiner.on(";").join(record.getId()));
        }

        sb.append("\t");
        sb.append(record.getRef());
        sb.append("\t");
        sb.append(Joiner.on(",").join(record.getAlt()));

        sb.append("\t");
        if (Double.isNaN(record.getQual())) {
            sb.append(".");
        }
        else {
            sb.append((int) record.getQual());
        }

        sb.append("\t");
        sb.append(Joiner.on(";").join(record.getFilter()));

        sb.append("\t");
        if (record.getInfo().isEmpty()) {
            sb.append(".");
        }
        else {
            sb.append(Joiner.on(";").withKeyValueSeparator("=").join(record.getInfo().asMap()));
        }

        if (!samples.isEmpty()) {
            sb.append("\t");
            sb.append(Joiner.on(":").join(record.getFormat()));
            for (VcfSample sample : samples) {
                sb.append("\t");

                List<String> values = new ArrayList<String>();
                for (String formatId : record.getFormat()) {
                    List<String> fieldValues = record.getGenotypes().get(sample.getId()).getFields().get(formatId);
                    values.add(fieldValues.isEmpty() ? "." : Joiner.on(",").join(fieldValues));
                }
                sb.append(Joiner.on(":").join(values));
            }
        }
        writer.println(sb.toString());
    }
}
