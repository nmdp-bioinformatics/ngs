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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.variant.vcf.VcfReader.parse;
import static org.nmdp.ngs.variant.vcf.VcfReader.header;
import static org.nmdp.ngs.variant.vcf.VcfReader.records;
import static org.nmdp.ngs.variant.vcf.VcfReader.samples;
import static org.nmdp.ngs.variant.vcf.VcfReader.stream;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.CharBuffer;

import com.google.common.collect.ImmutableList;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for VcfReader.
 */
public final class VcfReaderTest {
    private Readable readable;
    private Readable emptyReadable;
    private VcfParseListener parseListener;
    private VcfStreamListener streamListener;
    private static final String VCF = "ALL.chr22.phase1_release_v3.20101123.snps_indels_svs.genotypes-2-indv-thin-20000bp-trim.vcf";

    @Before
    public void setUp() {
        readable = CharBuffer.wrap("##fileformat=VCFv4.2\n");
        emptyReadable = CharBuffer.wrap("");
        parseListener = new VcfParseAdapter();
        streamListener = new VcfStreamAdapter();
    }

    @Test(expected=NullPointerException.class)
    public void testParseNullReadable() throws Exception {
        parse(null, parseListener);
    }

    @Test(expected=NullPointerException.class)
    public void testParseNullListener() throws Exception {
        parse(readable, null);
    }

    @Test
    public void testParse() throws Exception {
        parse(readable, parseListener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullReadable() throws Exception {
        stream(null, streamListener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamNullListener() throws Exception {
        stream(readable, null);
    }

    @Test
    public void testStream() throws Exception {
        stream(readable, streamListener);
    }

    @Test
    public void testStreamFile() throws Exception {
        try (BufferedReader reader = new BufferedReader(new FileReader(createFile(VCF)))) {
            stream(reader, new VcfStreamAdapter() {
                @Override
                public void header(final VcfHeader header) {
                    validateHeader(header);
                }

                @Override
                public void sample(final VcfSample sample) {
                    validateSample(sample);
                }

                @Override
                public void record(final VcfRecord record) {
                    validateRecord(record);
                }
            });
        }
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullReadable() throws Exception {
        header((Readable) null);
    }

    @Test(expected=IOException.class)
    public void testHeaderEmptyReadable() throws Exception {
        header(emptyReadable);
    }

    @Test
    public void testHeader() throws Exception {
        VcfHeader header = header(readable);
        assertEquals("VCFv4.2", header.getFileFormat());
    }

    @Test(expected=NullPointerException.class)
    public void testSamplesNullReadable() throws Exception {
        samples((Readable) null);
    }

    @Test
    public void testSamplesEmptyReadable() throws Exception {
        samples(emptyReadable);
    }

    @Test
    public void testSamples() throws Exception {
        Iterable<VcfSample> samples = samples(readable);
        assertNotNull(samples);
        assertTrue(ImmutableList.copyOf(samples).isEmpty());
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullReadable() throws Exception {
        records((Readable) null);
    }

    @Test
    public void testRecordsEmptyReadable() throws Exception {
        records(emptyReadable);
    }

    @Test
    public void testRecords() throws Exception {
        Iterable<VcfRecord> records = records(readable);
        assertNotNull(records);
        assertTrue(ImmutableList.copyOf(records).isEmpty());
    }


    @Test(expected=NullPointerException.class)
    public void testHeaderNullFile() throws Exception {
        header((File) null);
    }

    @Test
    public void testHeaderFile() throws Exception {
        validateHeader(header(createFile(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullURL() throws Exception {
        header((URL) null);
    }

    @Test
    public void testHeaderURL() throws Exception {
        validateHeader(header(createURL(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testHeaderNullInputStream() throws Exception {
        header((InputStream) null);
    }

    @Test
    public void testHeaderInputStream() throws Exception {
        validateHeader(header(createInputStream(VCF)));
    }


    @Test(expected=NullPointerException.class)
    public void testSamplesNullFile() throws Exception {
        samples((File) null);
    }

    @Test
    public void testSamplesFile() throws Exception {
        validateSamples(samples(createFile(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testSamplesNullURL() throws Exception {
        samples((URL) null);
    }

    @Test
    public void testSamplesURL() throws Exception {
        validateSamples(samples(createURL(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testSamplesNullInputStream() throws Exception {
        samples((InputStream) null);
    }

    @Test
    public void testSamplesInputStream() throws Exception {
        validateSamples(samples(createInputStream(VCF)));
    }


    @Test(expected=NullPointerException.class)
    public void testRecordsNullFile() throws Exception {
        records((File) null);
    }

    @Test
    public void testRecordsFile() throws Exception {
        validateRecords(records(createFile(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullURL() throws Exception {
        records((URL) null);
    }

    @Test
    public void testRecordsURL() throws Exception {
        validateRecords(records(createURL(VCF)));
    }

    @Test(expected=NullPointerException.class)
    public void testRecordsNullInputStream() throws Exception {
        records((InputStream) null);
    }

    @Test
    public void testRecordsInputStream() throws Exception {
        validateRecords(records(createInputStream(VCF)));
    }

    @Test
    public void testSamplesFullHeader() throws Exception {
        Iterable<VcfSample> samples = samples(createInputStream("chr22-header.vcf"));
        assertEquals(1092, ImmutableList.copyOf(samples).size());
    }

    @Test
    public void testSamplesWithGenomes() throws Exception {
        int count = 0;
        for (VcfSample sample : samples(createInputStream("samples.vcf"))) {
            assertNotNull(sample);

            if ("Blood".equals(sample.getId())) {
                assertNotNull(sample.getGenomes());
                assertEquals(1, sample.getGenomes().length);
                VcfGenome genome = sample.getGenomes()[0];
                assertEquals("Germline", genome.getId());
                assertEquals(1.0d, genome.getMixture(), 0.1d);
                assertEquals("Patient germline genome", genome.getDescription());
            }
            else if ("TissueSample".equals(sample.getId())) {
                assertNotNull(sample.getGenomes());
                assertEquals(2, sample.getGenomes().length);
                VcfGenome germline = sample.getGenomes()[0];
                assertEquals("Germline", germline.getId());
                assertEquals(0.3d, germline.getMixture(), 0.01d);
                assertEquals("Patient germline genome", germline.getDescription());
                VcfGenome tumor = sample.getGenomes()[1];
                assertEquals("Tumor", tumor.getId());
                assertEquals(0.7d, tumor.getMixture(), 0.01d);
                assertEquals("Patient tumor genome", tumor.getDescription());
            }

            count++;
        }
        assertEquals(2, count);
    }

    @Test(expected=IOException.class)
    public void testInvalidDataLine() throws Exception {
        records(createInputStream("invalid-data-line.vcf"));
    }

    @Test(expected=IOException.class)
    public void testInvalidPos() throws Exception {
        records(createInputStream("invalid-pos.vcf"));
    }

    @Test(expected=IOException.class)
    public void testInvalidQual() throws Exception {
        records(createInputStream("invalid-qual.vcf"));
    }

    @Test
    public void testRecordsFullInfo() throws Exception {
        VcfRecord record = records(createInputStream("chr22-info.vcf")).iterator().next();
        assertNotNull(record);

        /*
          LDAF=0.0649;RSQ=0.8652;AN=2184;ERATE=0.0046;VT=SNP;AA=.;AVGPOST=0.9799;THETA=0.0149;SNPSOURCE=LOWCOV;AC=134;AF=0.06;ASN_AF=0.04;AMR_AF=0.05;AFR_AF=0.10;EUR_AF=0.06
         */
        assertEquals("0.0649", record.getInfo().get("LDAF").get(0));
        assertEquals("0.8652", record.getInfo().get("RSQ").get(0));
        assertEquals("2184", record.getInfo().get("AN").get(0));
        assertEquals("0.0046", record.getInfo().get("ERATE").get(0));
        assertEquals("SNP", record.getInfo().get("VT").get(0));

        assertNotNull(record.getInfo().get("AA"));
        assertTrue(record.getInfo().get("AA").isEmpty());

        /*
          what about flags?

          record.getInfo().get("H2") ??

          what about some but not all missing values?

          AA=.,0.123,.

        */

        assertEquals("0.9799", record.getInfo().get("AVGPOST").get(0));
        assertEquals("0.0149", record.getInfo().get("THETA").get(0));
        assertEquals("LOWCOV", record.getInfo().get("SNPSOURCE").get(0));
        assertEquals("134", record.getInfo().get("AC").get(0));
        assertEquals("0.06", record.getInfo().get("AF").get(0));
        assertEquals("0.04", record.getInfo().get("ASN_AF").get(0));
        assertEquals("0.05", record.getInfo().get("AMR_AF").get(0));
        assertEquals("0.10", record.getInfo().get("AFR_AF").get(0));
        assertEquals("0.06", record.getInfo().get("EUR_AF").get(0));

        /*
          GT:DS:GL
          0|0:0.000:-0.02,-1.38,-5.00
         */
        assertEquals("0|0", record.getGenotypes().get("NA20828").getGt());
        assertEquals(ImmutableList.of("0|0"), record.getGenotypes().get("NA20828").getFields().get("GT"));
        assertEquals(ImmutableList.of("0.000"), record.getGenotypes().get("NA20828").getFields().get("DS"));
        assertEquals(ImmutableList.of("-0.02", "-1.38", "-5.00"), record.getGenotypes().get("NA20828").getFields().get("GL"));
    }

    @Test
    public void testMissingQual() throws Exception {
        VcfRecord record = records(createInputStream("missing-qual.vcf")).iterator().next();
        assertNotNull(record);
        assertTrue(Double.isNaN(record.getQual()));
    }

    @Ignore
    public void testHapmapInfo() throws Exception {
        VcfRecord record = records(createInputStream("hapmap-info.vcf")).iterator().next();
        assertNotNull(record);

        // todo:  there isn't a way to put an empty mapping in a ListMultimap
        assertTrue(record.getInfo().containsKey("H2"));
        assertTrue(record.getInfo().get("H2").isEmpty());
    }

    @Test
    public void testMissingAlt() throws Exception {
        VcfRecord record = records(createInputStream("missing-alt.vcf")).iterator().next();
        assertNotNull(record);
        assertNotNull(record.getAlt());
        assertEquals(0, record.getAlt().length);
    }

    @Test
    public void testMissingFilter() throws Exception {
        VcfRecord record = records(createInputStream("missing-filter.vcf")).iterator().next();
        assertNotNull(record);
        assertNotNull(record.getFilter());
        assertEquals(0, record.getFilter().length);
    }

    @Test
    public void testMissingGenotypeField() throws Exception {
        VcfRecord record = records(createInputStream("missing-genotype-field.vcf")).iterator().next();
        assertNotNull(record);

        assertEquals("0|0", record.getGenotypes().get("NA20828").getGt());
        assertEquals(ImmutableList.of("0|0"), record.getGenotypes().get("NA20828").getFields().get("GT"));

        // DS is missing, i.e. 0|0:.:-0.02,-1.38,-5.00
        assertTrue(record.getGenotypes().get("NA20828").getFields().get("DS").isEmpty());
        assertEquals(ImmutableList.of("-0.02", "-1.38", "-5.00"), record.getGenotypes().get("NA20828").getFields().get("GL"));
    }

    @Test
    public void testMissingId() throws Exception {
        VcfRecord record = records(createInputStream("missing-id.vcf")).iterator().next();
        assertNotNull(record);
        assertNotNull(record.getId());
        assertEquals(0, record.getId().length);
    }

    @Test
    public void testGatkGvcf() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(createInputStream("gatk-example.gvcf")))) {
            stream(reader, new VcfStreamListener() {
                @Override
                public void header(final VcfHeader header) {
                    validateHeader(header);
                    int count = 0;
                    for (String meta : header.getMeta()) {
                        if (meta.startsWith("##GVCFBlock")) {
                            count++;
                        }
                    }
                    assertEquals(4, count);
                }

                @Override
                public void sample(final VcfSample sample) {
                    assertTrue("NA12878".equals(sample.getId()));
                }

                @Override
                public void record(final VcfRecord record) {
                    if (record.getPos() == 10000212) {
                        assertEquals("A", record.getRef());
                        assertEquals(1, record.getAlt().length);
                        assertEquals("<NON_REF>", record.getAlt()[0]);
                    }
                    else if (record.getPos() == 10001298) {
                        assertEquals("T", record.getRef());
                        assertEquals(2, record.getAlt().length);
                        assertEquals("A", record.getAlt()[0]);
                        assertEquals("<NON_REF>", record.getAlt()[1]);
                    }
                }
            });
        }
    }

    @Test
    public void testCephGatkHaplotypeJoint() throws Exception {
        VcfRecord record = records(createInputStream("ceph-bwa-j-gatk-haplotype-joint.excerpt.vcf")).iterator().next();
        assertNull(record.getGenotypes().get("NA12878-3").getGt());
        assertEquals("0/1", record.getGenotypes().get("NA12891-3").getGt());
        assertEquals("0/0", record.getGenotypes().get("NA12892-3").getGt());
    }

    private static void validateHeader(final VcfHeader header) {
        assertNotNull(header);
        assertEquals("VCFv4.1", header.getFileFormat());
        assertNotNull(header.getMeta());
        assertFalse(header.getMeta().isEmpty());
    }

    private static void validateSample(final VcfSample sample) {
        assertNotNull(sample);
        assertNotNull(sample.getId());
        assertNotNull(sample.getGenomes());
        assertTrue("NA19131".equals(sample.getId()) || "NA19223".equals(sample.getId()));
    }

    private static void validateSamples(final Iterable<VcfSample> samples) {
        assertNotNull(samples);

        int count = 0;
        for (VcfSample sample : samples) {
            validateSample(sample);
            count++;
        }
        assertEquals(2, count);
    }

    private static void validateRecord(final VcfRecord record) {
        assertNotNull(record);

        if (16140370 == record.getPos()) {
            assertEquals("22", record.getChrom());
            assertEquals(1, record.getId().length);
            assertEquals("rs2096606", record.getId()[0]);
            assertEquals("A", record.getRef());
            assertEquals(1, record.getAlt().length);
            assertEquals("G", record.getAlt()[0]);
            assertEquals(100.0d, record.getQual(), 0.1d);
            assertEquals("PASS", record.getFilter()[0]);
            assertNotNull(record.getInfo());
            assertTrue(record.getInfo().isEmpty());
            assertNotNull(record.getGenotypes());
            assertFalse(record.getGenotypes().isEmpty());
            assertEquals("1|1", record.getGenotypes().get("NA19131").getGt());
            assertEquals("1|1", record.getGenotypes().get("NA19223").getGt());
        }
        else if (17512091 == record.getPos()) {
            assertEquals("22", record.getChrom());
            assertEquals(1, record.getId().length);
            assertEquals("rs5992615", record.getId()[0]);
            assertEquals("G", record.getRef());
            assertEquals(1, record.getAlt().length);
            assertEquals("A", record.getAlt()[0]);
            assertEquals(100.0d, record.getQual(), 0.1d);
            assertEquals("PASS", record.getFilter()[0]);
            assertNotNull(record.getInfo());
            assertTrue(record.getInfo().isEmpty());
            assertNotNull(record.getGenotypes());
            assertFalse(record.getGenotypes().isEmpty());
            assertEquals("1|0", record.getGenotypes().get("NA19131").getGt());
            assertEquals("0|0", record.getGenotypes().get("NA19223").getGt());
        }
    }

    private static void validateRecords(final Iterable<VcfRecord> records) {
        assertNotNull(records);

        int count = 0;
        for (VcfRecord record : records) {
            validateRecord(record);
            count++;
        }
        assertEquals(70, count);
    }

    private static URL createURL(final String name) throws Exception {
        return VcfReaderTest.class.getResource(name);
    }

    private static InputStream createInputStream(final String name) throws IOException {
        return VcfReaderTest.class.getResourceAsStream(name);
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("vcfReaderTest", ".vcf");
        Files.write(Resources.toByteArray(VcfReaderTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
