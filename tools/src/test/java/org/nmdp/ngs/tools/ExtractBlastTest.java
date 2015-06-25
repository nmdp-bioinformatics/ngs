/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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
package org.nmdp.ngs.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.nmdp.ngs.tools.ExtractBlast.readFasta;
import static org.nmdp.ngs.tools.ExtractBlast.readBlast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nmdp.gl.client.local.LocalGlClient;
import org.nmdp.ngs.tools.ExtractBlast.BlastResults;

import com.google.common.base.Splitter;
import com.google.common.collect.ListMultimap;
import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * Unit test for ExtractConsensus.
 */
public final class ExtractBlastTest {
    private File inputBlastFile;
    private File inputFastqFile;
    private File outputFile;
    private ExtractBlast extractBlast;
    
    @Before
    public void setUp() throws Exception {
    	inputBlastFile = File.createTempFile("validateInterpretationTest", "txt");
    	inputFastqFile = File.createTempFile("validateInterpretationTest", "txt");
        outputFile     = File.createTempFile("validateInterpretationTest", "txt");
        extractBlast   = new ExtractBlast(inputBlastFile, inputFastqFile, outputFile,10, "3.20.0");
    }
    
    @After
    public void tearDown() throws Exception {
    	inputBlastFile.delete();
    	inputFastqFile.delete();
        outputFile.delete();
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(extractBlast);
    }

    
    @Test
    public void testReadBlast() throws Exception {
        copyResource("blast.txt", inputBlastFile);
        Map<String, BlastResults> blast = readBlast(10,inputBlastFile);
        assertNotNull(blast);
        assertNotNull(blast.get("0|Ref151|32552989|1||1|0"));
    }

    @Test
    public void testAlleleCutoff() throws Exception {
        copyResource("blast.txt", inputBlastFile);
        Map<String, BlastResults> blast = readBlast(10,inputBlastFile);
        assertNotNull(blast);
        for (String seqId : blast.keySet()) {
            List<String> blastString = Splitter.on("/").splitToList(blast.get(seqId).getTypingGlstring());
            assertEquals(10,blastString.size());
        }
    }    
    
    @Test
    public void testUniqueAllelesInGlstring() throws Exception {
        copyResource("blast.txt", inputBlastFile);
        Map<String, BlastResults> blast = readBlast(10,inputBlastFile);
        assertNotNull(blast);
        for (String seqId : blast.keySet()) {
        	List<String> unique = new ArrayList<String>();
            List<String> blastGlstring = Splitter.on("/").splitToList(blast.get(seqId).getTypingGlstring());
            for(String allele : blastGlstring){
            	boolean inGl = unique.contains(allele);
            	assertFalse(inGl);
            	unique.add(allele); 	
            }
        }
    }        
    
    
    @Test
    public void testReadFasta() throws Exception {
        copyResource("1000-0000-0.fa", inputFastqFile);
        assertNotNull(inputFastqFile);   
        Map<String, String> fasta = readFasta(inputFastqFile);
        assertNotNull(fasta); 
        assertNotNull(fasta.get("0|Ref151|32552989|1||1|0"));
    }
    
    private static void copyResource(final String name, final File file) throws Exception {
        Files.write(Resources.toByteArray(FilterConsensusTest.class.getResource(name)), file);
    }
}
