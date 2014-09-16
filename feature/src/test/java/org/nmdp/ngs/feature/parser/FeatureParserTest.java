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
package org.nmdp.ngs.feature.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.biojava.bio.seq.StrandedFeature;

import org.biojava.bio.symbol.RangeLocation;

import org.junit.Test;

import org.nmdp.ngs.feature.Allele;
import org.nmdp.ngs.feature.Locus;
import org.nmdp.ngs.feature.Sample;
import org.nmdp.ngs.feature.VcfFile;

import org.nmdp.ngs.variant.vcf.VcfRecord;

/**
 * Unit test for FeatureParser.
 */
public final class FeatureParserTest {

    @Test
    public void parseNumber() throws ParseException {
        /**
         * Decimals
         */
        assertEquals(FeatureParser.parseNumber("0"), new Double(0));
        assertEquals(FeatureParser.parseNumber("0.0"), new Double(0));
        //assertEquals(FeatureParser.parseNumber(".8"), new Double(0.8));
        //assertEquals(FeatureParser.parseNumber("-.8"), new Double(-0.8));
        /**
         * Reals are numbers
         */
        assertEquals(FeatureParser.parseNumber("241910.8"), new Double(241910.8));
        assertEquals(FeatureParser.parseNumber("-241910.8"), new Double(-241910.8));
        /**
         * Integers are numbers
         */
        assertEquals(FeatureParser.parseNumber("2419108"), new Double(2419108));
        assertEquals(FeatureParser.parseNumber("-2419108"), new Double(-2419108));
        /**
         * Parse the first number, not necessarily the entire field-string (non-terminal)
         */
        assertEquals(FeatureParser.parseNumber("24191.0.8"), new Double(24191.0));
        assertEquals(FeatureParser.parseNumber("241910..8"), new Double(241910.0));
    }

    /**
     * Test parsing the entire field-string (terminal)
     */
    @Test
    public void parseNumberField() throws ParseException {
        try {
            FeatureParser.parseNumberField("24191.0.8");
            fail();
        }
        catch (ParseException e) {
            /* pass */
        }

        try {
            FeatureParser.parseNumberField("241910..8");
            fail();
        }
        catch (ParseException e) {
            /* pass */
        }
    }

    @Test
    public void testParseNumbers() throws ParseException {
        assertEquals(FeatureParser.parseNumbers("0,0.0,241910.8,-241910.8,2419108,-2419108").size(), 6);
    }

    @Test
    public void testParseLocation() throws ParseException {
        assertEquals(FeatureParser.parseLocation("29909789").intValue(), 29909789);
        assertEquals(FeatureParser.parseLocation("29,909,789").intValue(), 29909789);
    }
    
    /*
    @Test
    public void testParseEquivalentLocation() throws ParseException {
        assertEquals(FeatureParser.parseEquivalentLocation("14", "ga", reference), new RangeLocation(14, 21));
        assertEquals(FeatureParser.parseEquivalentLocation("21", "ag", reference), new RangeLocation(14, 21));
        assertEquals(FeatureParser.parseEquivalentLocation("14", "ag", reference), new RangeLocation(14, 14));
        assertEquals(FeatureParser.parseEquivalentLocation("18", "gag", reference), new RangeLocation(18, 24));
        assertEquals(FeatureParser.parseEquivalentLocation("24", "gag", reference), new RangeLocation(18, 24));
        assertEquals(FeatureParser.parseEquivalentLocation("1", "CCTCCA", "AGCCCCAA"), new RangeLocation(1, 3));
        assertEquals(FeatureParser.parseEquivalentLocation("3", "TCCACC", "AGCCCCAA"), new RangeLocation(1, 3));
    }
    */

    @Test
    public void testParseRangeLocation() throws ParseException {
        assertEquals(FeatureParser.parseRangeLocation("29909789-29914188"), new RangeLocation(29909789, 29914188));
        assertEquals(FeatureParser.parseRangeLocation("29909789..29914188"), new RangeLocation(29909789, 29914188));
        assertEquals(FeatureParser.parseRangeLocation("29909789\t29914188"), new RangeLocation(29909789, 29914188));
        assertEquals(FeatureParser.parseRangeLocation("29909789+4398"), new RangeLocation(29909789, 29914188));
    }

    @Test
    public void testParseLocus() throws ParseException {
        Locus result = new Locus("chr6", 29909789, 29914188);
        assertEquals(FeatureParser.parseLocus("6:29909789-29914188"), new Locus("6", 29909789, 29914188));
        assertEquals(FeatureParser.parseLocus("gi|568336009|gb|CM000677.2|:29909789-29914188"), new Locus("gi|568336009|gb|CM000677.2|", 29909789, 29914188));
        assertEquals(FeatureParser.parseLocus("chr6:29909789-29914188"), result);
        assertEquals(FeatureParser.parseLocus("chr6:29909789..29914188"), result);
        assertEquals(FeatureParser.parseLocus("chr6:29909789\t29914188"), result);
        assertEquals(FeatureParser.parseLocus("chr6:29909789+4398"), result);
        assertEquals(FeatureParser.parseLocus("chr6:29,909,789-29,914,188"), result);

        try {
          FeatureParser.parseLocus("chr6:299097B9-29914188");
          fail();
        }
        catch (ParseException e) {
            /* pass */
        }

        try {
          FeatureParser.parseLocus("chr6:29,9,09,789-29,914,188");
          fail();
        }
        catch (ParseException e) {
            /* pass */
        }
    }

    /*
    @Test
    public void testParseMpileupAlleles() throws ParseException {
        FeatureParser.parseMpileupAlleles("chr6\t31269192\tA\t4\t.,.,.");
        FeatureParser.parseMpileupAlleles("chr6\t31269192\tA\t4\t.Cc,G");
        FeatureParser.parseMpileupAlleles("chr6\t31271393\tC	\t4\t.,+1a..+1A,+1a");
    }
    

    @Test
    public void testParseVcfAlleles() throws ParseException {
        assertEquals(FeatureParser.parseVcfAlleles("chr20\t14370\trs6054257\tG\tA\t29\tPASS\tNS=2;DP=10;AF=0.333,0.667;AA=T;DB").size(), 1);
        assertEquals(FeatureParser.parseVcfAlleles("chr20\t1234567\tmicrosat1\tGTC\tG,GTCT\t50\tPASS\tNS=3;DP=9;AA=G").size(), 2);
    }
    */

    @Test
    public void testParseVcfSamples() throws ParseException {
        List<String> names = new ArrayList<String>();
        names.add("NA00001");
        assertEquals(FeatureParser.parseVcfSamples("GT:GQ:DP:HQ\t0|0:48:1:51,51", new VcfFile.Header(names)).get(0).getAnnotation().toString(), "{DP=[1.0],GQ=[48.0],GT=[0, 0],HQ=[51.0, 51.0]}");

        names.add("NA00002");
        names.add("NA00003");
        List<Sample> samples = FeatureParser.parseVcfSamples("GT:GQ:DP:HQ\t0|0:48:1:51,51\t1|0:48:8:51,51\t1/1:43:5:.,.", new VcfFile.Header(names));
        assertEquals(samples.get(0).getName(), "NA00001");
        assertEquals(samples.get(0).getAnnotation().toString(), "{DP=[1.0],GQ=[48.0],GT=[0, 0],HQ=[51.0, 51.0]}");

        assertEquals(samples.get(1).getName(), "NA00002");
        assertEquals(samples.get(1).getAnnotation().toString(), "{DP=[8.0],GQ=[48.0],GT=[1, 0],HQ=[51.0, 51.0]}");

        assertEquals(samples.get(2).getName(), "NA00003");
        assertEquals(samples.get(2).getAnnotation().toString(), "{DP=[5.0],GQ=[43.0],GT=[1, 1],HQ=[., .]}");
    }

    @Test
    public void testParseVcfRecord() throws ParseException {
        VcfRecord record = FeatureParser.parseVcfRecord("chr20\t1110696\trs6040355\tA\tG,T\t67\tPASS\tNS=2;DP=10;AF=0.333,0.667;AA=T;DB\tGT:GQ:DP:HQ\t1|2:21:6:23,27\t2|1:2:0:18,2\t2/2:35:4", 0);
        assertEquals(record.getChrom(), "chr20");
        assertEquals(record.getPos(), 1110696);
        assertEquals(record.getAlt().length, 2);
    }

    @Test
    public void testParseVcfMetadata() throws ParseException {
        VcfFile.Metadata version = FeatureParser.parseVcfMetadata("##fileformat=VCFv4.2");
        assertEquals(version.getName(), "fileformat");
        assertEquals(version.getDescription(), "VCFv4.2");
    }

    @Test
    public void testParseVcfInfo() throws ParseException {
        //"INFO=<ID=NS,Number=1,Type=Integer,Description=Number>"
        VcfFile.Info info = (VcfFile.Info) FeatureParser.parseVcfMetadata("##INFO=<ID=NS,Number=1,Type=Integer,Description=Number of Samples With Data>");
        assertEquals(info.getName(), "NS");
        assertEquals(info.getNumber(), 1.0);
        assertEquals(info.getType(), VcfFile.Metadata.Type.INTEGER);
        assertEquals(info.getDescription(), "Number of Samples With Data");

        info = (VcfFile.Info) FeatureParser.parseVcfMetadata("##INFO=<ID=AF,Number=A,Type=Float,Description=Allele Frequency>");
        assertEquals(info.getName(), "AF");
        assertEquals(info.getNumber(), "A");
        assertEquals(info.getType(), VcfFile.Metadata.Type.FLOAT);
        assertEquals(info.getDescription(), "Allele Frequency");
    }
    
    @Test
    public void testParseVcfFilter() throws ParseException {
        VcfFile.Filter filter = (VcfFile.Filter) FeatureParser.parseVcfMetadata("##FILTER=<ID=q10,Description=Quality below 10>");
        assertEquals(filter.getName(), "q10");
        assertEquals(filter.getDescription(), "Quality below 10");

        filter = (VcfFile.Filter) FeatureParser.parseVcfMetadata("##FILTER=<ID=s50,Description=Less than 50% of samples have data>");
        assertEquals(filter.getName(), "s50");
        assertEquals(filter.getDescription(), "Less than 50% of samples have data");
    }

    @Test
    public void testParseVcfFormat() throws ParseException {
        VcfFile.Format format = (VcfFile.Format) FeatureParser.parseVcfMetadata("##FORMAT=<ID=GT,Number=1,Type=String,Description=Genotype>");
        assertEquals(format.getName(), "GT");
        assertEquals(format.getNumber(), 1.0);
        assertEquals(format.getType(), VcfFile.Metadata.Type.STRING);
        assertEquals(format.getDescription(), "Genotype");

        format = (VcfFile.Format) FeatureParser.parseVcfMetadata("##FORMAT=<ID=GQ,Number=A,Type=Integer,Description=Genotype Quality>");
        assertEquals(format.getName(), "GQ");
        assertEquals(format.getNumber(), "A");
        assertEquals(format.getType(), VcfFile.Metadata.Type.INTEGER);
        assertEquals(format.getDescription(), "Genotype Quality");
    }

    @Test
    public void testParseVcfHeader() throws ParseException {
        assertEquals(FeatureParser.parseVcfHeader("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tNA00001\tNA00002\tNA00003").names.toString(), "[NA00001, NA00002, NA00003]");
    }

    @Test
    public void testParseDnaSequence() throws ParseException {
        assertEquals(FeatureParser.parseDnaSequence("A").seqString(), "a");
        assertEquals(FeatureParser.parseDnaSequence("ACGT").seqString(), "acgt");
        /**
         * Assert cannot parse RNA
         */
        try {
            String result = FeatureParser.parseDnaSequence("ACGU").seqString();
            fail();
        }
        catch (ParseException exception) {
            /* pass */
        }
    }

    @Test
    public void testParseSequence() throws ParseException {
        // stub
    }

    @Test
    public void testParseAnnotations() throws ParseException {
        //System.out.println("PARSE ANNOTATION: " + FeatureParser.parseAnnotations("NS=2;DP=10;AF=0.333,0.667;AA=T;DB"));
        assertEquals(FeatureParser.parseAnnotations("NS=3;DP=14;AF=0.5;DB;H2").toString(), "{AF=[0.5],DB=[],DP=[14.0],H2=[],NS=[3.0]}");
        assertEquals(FeatureParser.parseAnnotations("NS=2;DP=10;AF=0.333,0.667;AA=T;DB").toString(), "{AA=[T],AF=[0.333, 0.667],DB=[],DP=[10.0],NS=[2.0]}");
    }

    @Test
    public void testParseGffStrand() throws ParseException {
        assertEquals(FeatureParser.parseGffStrand("."), StrandedFeature.UNKNOWN);
        assertEquals(FeatureParser.parseGffStrand("-"), StrandedFeature.NEGATIVE);
        assertEquals(FeatureParser.parseGffStrand("+"), StrandedFeature.POSITIVE);
    }
}
