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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.variant.vcf.VcfRecord.builder;

import java.util.Map;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfRecord.
 */
public final class VcfRecordTest {
    private long lineNumber;
    private String chrom;
    private long pos;
    private String[] id;
    private String ref;
    private String[] alt;
    private double qual;
    private String[] filter;
    private ListMultimap<String, String> info;
    private String[] format;
    private Map<String, VcfGenotype> genotypes;

    @Before
    public void setUp() {
        lineNumber = 42L;
        chrom = "22";
        pos = 16140370L;
        id = new String[] { "rs2096606" };
        ref = "A";
        alt = new String[] { "G" };
        qual = 100.0d;
        filter = new String[] { "PASS" };
        info = ImmutableListMultimap.<String, String>builder().build();
        format = new String[] { "GT" };
        VcfGenotype.Builder genotypeBuilder = VcfGenotype.builder().withGt("1|1");
        genotypes = ImmutableMap.<String, VcfGenotype>builder().put("NA19131", genotypeBuilder.build()).put("NA19223", genotypeBuilder.build()).build();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullId() {
        new VcfRecord(lineNumber, chrom, pos, null, ref, alt, qual, filter, info, format, genotypes);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullAlt() {
        new VcfRecord(lineNumber, chrom, pos, id, ref, null, qual, filter, info, format, genotypes);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullInfo() {
        new VcfRecord(lineNumber, chrom, pos, id, ref, alt, qual, filter, null, format, genotypes);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullGenotypes() {
        new VcfRecord(lineNumber, chrom, pos, id, ref, alt, qual, filter, info, format, null);
    }

    @Test
    public void testConstructor() {
        VcfRecord record = new VcfRecord(lineNumber, chrom, pos, id, ref, alt, qual, filter, info, format, genotypes);
        assertNotNull(record);
        assertEquals(lineNumber, record.getLineNumber());
        assertEquals(chrom, record.getChrom());
        assertEquals(id, record.getId());
        assertEquals(ref, record.getRef());
        assertEquals(alt, record.getAlt());
        assertEquals(qual, record.getQual(), 0.1d);
        assertEquals(filter, record.getFilter());
        assertEquals(info, record.getInfo());
        assertEquals(format, record.getFormat());
        assertEquals(genotypes, record.getGenotypes());
    }

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithInfoNullKey() {
        builder().withInfo(null, "value");
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithInfoNull() {
        builder().withInfo(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypeNullSampleId() {
        builder().withGenotype(null, VcfGenotype.builder().withGt("1|1").build());
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypeNullValue() {
        builder().withGenotype("NA19131", null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderWithGenotypesNullGenotypes() {
        builder().withGenotypes(null);
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullId() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test(expected=NullPointerException.class)
    public void testBuilderBuildNullAlt() {
        builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();
    }

    @Test
    public void testBuilderBuildNullInfoValues() {
        VcfRecord record = builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo("NS", (String[]) null)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        assertTrue(record.getInfo().get("NS").isEmpty());
    }

    @Test
    public void testBuilderBuild() {
        VcfRecord record = builder()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        assertNotNull(record);
        assertEquals(lineNumber, record.getLineNumber());
        assertEquals(chrom, record.getChrom());
        assertEquals(pos, record.getPos());
        assertEquals(id, record.getId());
        assertEquals(ref, record.getRef());
        assertEquals(alt, record.getAlt());
        assertEquals(qual, record.getQual(), 0.1d);
        assertEquals(filter, record.getFilter());
        assertEquals(info, record.getInfo());
        assertEquals(format, record.getFormat());
        assertEquals(genotypes, record.getGenotypes());
    }

    @Test
    public void testBuilderReset() {
        VcfRecord record = builder()
            .withLineNumber(42L)
            .reset()
            .withLineNumber(lineNumber)
            .withChrom(chrom)
            .withPos(pos)
            .withId(id)
            .withRef(ref)
            .withAlt(alt)
            .withQual(qual)
            .withFilter(filter)
            .withInfo(info)
            .withFormat(format)
            .withGenotypes(genotypes)
            .build();

        assertNotNull(record);
        assertEquals(lineNumber, record.getLineNumber());
    }
}
