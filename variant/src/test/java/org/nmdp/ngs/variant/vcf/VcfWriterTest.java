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

import static org.junit.Assert.assertEquals;

import static org.nmdp.ngs.variant.vcf.VcfWriter.write;
import static org.nmdp.ngs.variant.vcf.VcfWriter.writeColumnHeader;
import static org.nmdp.ngs.variant.vcf.VcfWriter.writeHeader;
import static org.nmdp.ngs.variant.vcf.VcfWriter.writeRecord;
import static org.nmdp.ngs.variant.vcf.VcfWriter.writeRecords;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfWriter.
 */
public final class VcfWriterTest {
    private VcfHeader header;
    private ListMultimap<String, String> info;
    private Map<String, VcfGenotype> genotypes;
    private List<VcfSample> samples;
    private VcfRecord record;
    private List<VcfRecord> records;
    private ByteArrayOutputStream outputStream;
    private PrintWriter writer;

    @Before
    public void setUp() throws Exception {
        header = new VcfHeader("VCFv4.1", ImmutableList.of("##fileformat=VCFv4.1"));

        VcfSample sample = new VcfSample("NA19131", new VcfGenome[0]);
        samples = ImmutableList.of(sample);

        info = ImmutableListMultimap.<String, String>builder().build();
        VcfGenotype.Builder genotypeBuilder = VcfGenotype.builder().withGt("1|1");
        genotypes = ImmutableMap.<String, VcfGenotype>builder().put("NA19131", genotypeBuilder.build()).put("NA19223", genotypeBuilder.build()).build();

        record = new VcfRecord(3L, "22", 16140370L, new String[] { "rs2096606" }, "A", new String[] { "G" }, 100.0d, new String[] { "PASS" }, info, new String[] { "GT" }, genotypes);
        records = ImmutableList.of(record);

        outputStream = new ByteArrayOutputStream();
        writer = new PrintWriter(outputStream);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullHeader() throws Exception {
        write(null, samples, records, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullSamples() throws Exception {
        write(header, null, records, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullRecords() throws Exception {
        write(header, samples, null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteNullWriter() throws Exception {
        write(header, samples, records, null);
    }

    @Test
    public void testWrite() throws Exception {
        write(header, samples, records, writer);
        writer.close();
        assertEquals("##fileformat=VCFv4.1" + System.lineSeparator()
                     + "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA19131" + System.lineSeparator()
                     + "22\t16140370\trs2096606\tA\tG\t100\tPASS\t.\tGT\t1|1" + System.lineSeparator(), outputStream.toString());
    }


    @Test(expected=NullPointerException.class)
    public void testWriteHeaderNullHeader() throws Exception {
        writeHeader(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteHeaderNullWriter() throws Exception {
        writeHeader(header, null);
    }

    @Test
    public void testWriteHeader() throws Exception {
        writeHeader(header, writer);
        writer.close();
        assertEquals("##fileformat=VCFv4.1" + System.lineSeparator(), outputStream.toString());
    }


    @Test(expected=NullPointerException.class)
    public void testWriteColumnHeaderNullSamples() throws Exception {
        writeColumnHeader(null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteColumnHeaderNullWriter() throws Exception {
        writeColumnHeader(samples, null);
    }

    @Test
    public void testWriteColumnHeader() throws Exception {
        writeColumnHeader(samples, writer);
        writer.close();
        assertEquals("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA19131" + System.lineSeparator(), outputStream.toString());
    }


    @Test(expected=NullPointerException.class)
    public void testWriteRecordsNullSamples() throws Exception {
        writeRecords(null, records, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordsNullRecords() throws Exception {
        writeRecords(samples, null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordsNullWriter() throws Exception {
        writeRecords(samples, records, null);
    }

    @Test
    public void testWriteRecords() throws Exception {
        writeRecords(samples, records, writer);
        writer.close();
        assertEquals("22\t16140370\trs2096606\tA\tG\t100\tPASS\t.\tGT\t1|1" + System.lineSeparator(), outputStream.toString());
    }


    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullSamples() throws Exception {
        writeRecord(null, record, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullRecord() throws Exception {
        writeRecord(samples, null, writer);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRecordNullWriter() throws Exception {
        writeRecord(samples, record, null);
    }

    @Test
    public void testWriteRecord() throws Exception {
        writeRecord(samples, record, writer);
        writer.close();
        assertEquals("22\t16140370\trs2096606\tA\tG\t100\tPASS\t.\tGT\t1|1" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void testWriteRecordMissingQual() throws Exception {
        VcfRecord missingQual = new VcfRecord(3L, "22", 16140370L, new String[] { "rs2096606" }, "A", new String[] { "G" }, Double.NaN, new String[] { "PASS" }, info, new String[] { "GT" }, genotypes);

        writeRecord(samples, missingQual, writer);
        writer.close();
        assertEquals("22\t16140370\trs2096606\tA\tG\t.\tPASS\t.\tGT\t1|1" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void testWriteRecordMissingId() throws Exception {
        VcfRecord missingId = new VcfRecord(3L, "22", 16140370L, new String[0], "A", new String[] { "G" }, Double.NaN, new String[] { "PASS" }, info, new String[] { "GT" }, genotypes);

        writeRecord(samples, missingId, writer);
        writer.close();
        assertEquals("22\t16140370\t.\tA\tG\t.\tPASS\t.\tGT\t1|1" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void testWriteRecordGenotypeFields() throws Exception {
        Map<String, VcfGenotype> genotypes = new HashMap<String, VcfGenotype>();
        VcfGenotype genotype = VcfGenotype.builder().withField("GT", "1|1").withField("DS", "0.000").withField("GL", "-0.02", "-1.38", "-5.00").build();
        genotypes.put("NA19131", genotype);
        VcfRecord genotypeFields = new VcfRecord(3L, "22", 16140370L, new String[] { "rs2096606" }, "A", new String[] { "G" }, Double.NaN, new String[] { "PASS" }, info, new String[] { "GT", "DS", "GL" }, genotypes);

        writeRecord(samples, genotypeFields, writer);
        writer.close();
        assertEquals("22\t16140370\trs2096606\tA\tG\t.\tPASS\t.\tGT:DS:GL\t1|1:0.000:-0.02,-1.38,-5.00" + System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void testWriteRecordMissingGenotypeField() throws Exception {
        Map<String, VcfGenotype> genotypes = new HashMap<String, VcfGenotype>();
        VcfGenotype genotype = VcfGenotype.builder().withField("GT", "1|1").withField("GL", "-0.02", "-1.38", "-5.00").build();
        genotypes.put("NA19131", genotype);
        VcfRecord genotypeFields = new VcfRecord(3L, "22", 16140370L, new String[] { "rs2096606" }, "A", new String[] { "G" }, Double.NaN, new String[] { "PASS" }, info, new String[] { "GT", "DS", "GL" }, genotypes);

        writeRecord(samples, genotypeFields, writer);
        writer.close();
        assertEquals("22\t16140370\trs2096606\tA\tG\t.\tPASS\t.\tGT:DS:GL\t1|1:.:-0.02,-1.38,-5.00" + System.lineSeparator(), outputStream.toString());
    }
}
