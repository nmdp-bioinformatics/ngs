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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.nmdp.ngs.hml.HmlReader;
import org.nmdp.ngs.hml.jaxb.AlleleAssignment;
import org.nmdp.ngs.hml.jaxb.Glstring;
import org.nmdp.ngs.hml.jaxb.Haploid;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Validate interpretation.
 */
public final class ValidateInterpretation implements Callable<Integer> {
    private final File expectedFile;
    private final File observedFile;
    private final File outputFile;
    private static boolean HaploidBoolean;
    private static boolean GlstringBoolean;    
    private final int resolution;
    private final boolean printSummary;
    static final int DEFAULT_RESOLUTION = 2;
    private static final String USAGE = "ngs-validate-interpretation -e expected.txt -b observed.txt -g -l [args]";


    /**
     * Validate interpretation.
     *
     * @param expectedFile expected file, must not be null
     * @param observedFile observed file, must not be null
     * @param outputFile output file, if any
     * @param resolution minimum fields of resolution, must be in the range [1..4]
     * @param printSummary print summary report
     * @param HaploidBoolean flag to use Haploid object from the HML
     * @param GlstringBoolean flag to use Glstring object from the HML
     */
    public ValidateInterpretation(final File expectedFile, final File observedFile, boolean HaploidBoolean, boolean GlstringBoolean, final File outputFile, final int resolution, final boolean printSummary) {
        checkNotNull(expectedFile);
        checkNotNull(observedFile);
        checkArgument(resolution > 0 && resolution < 5, "resolution must be in the range [1..4]");
        this.expectedFile = expectedFile;
        this.observedFile = observedFile;
        this.outputFile = outputFile;
        this.resolution = resolution;
        this.printSummary = printSummary;
        ValidateInterpretation.HaploidBoolean  = HaploidBoolean;
        ValidateInterpretation.GlstringBoolean = GlstringBoolean;         
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        int passes = 0;
        int failures = 0;
        try {
            writer = writer(outputFile);
            ListMultimap<String, String> expected = readExpected(expectedFile);
            ListMultimap<String, String> observed = readObserved(observedFile);	

            for (String sample : expected.keySet()) {
                List<String> alleles = expected.get(sample);
                List<String> interpretations = observed.get(sample);

                for (String expectedAllele : alleles) {
                    boolean result = false;
                    for (String interpretation : interpretations) {
                        List<String> interpretedAlleles = Splitter.onPattern("[/|]+").splitToList(interpretation);
                        List<String> found = new ArrayList<String>();
                        for (String interpretedAllele : interpretedAlleles) {
                            if (matchByField(expectedAllele, interpretedAllele) >= resolution) {
                                found.add(interpretedAllele);
                            }
                        }

                        if (!found.isEmpty()) {
                            result = true;
                        }
                    }

                    if (result) {
                        passes++;
                    }
                    else {
                        failures++;
                    }

                    if (!printSummary) {
                        writer.println((result ? "PASS" : "FAIL") + "\t" + sample + "\t" + expectedAllele);
                    }
                }
            }

            if (printSummary) {
                writer.println("PASS\t" + passes);
                writer.println("FAIL\t" + failures);
            }

            return 0;
        }
        finally {
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }

    static ListMultimap<String, String> readExpected(final File expectedFile) throws IOException {
        BufferedReader reader = null;
        ListMultimap<String, String> expected = ArrayListMultimap.create();
        
        if(isHML(expectedFile)){
        	reader = reader(expectedFile);
            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String id = sample.getId();
                for (Typing typing : sample.getTyping()) {
                   for (AlleleAssignment alleleAssignment : typing.getAlleleAssignment()) {
                	   List<Haploid> HapList = new ArrayList<Haploid>();
                	   for (Object glstring : alleleAssignment.getPropertyAndHaploidAndGenotypeList()){
                          String classType  = glstring.getClass().toString();
                          String objectType = classType.substring(classType.indexOf("jaxb.") + 5,classType.length());
                		   
                          if(objectType.equals("Haploid") && HaploidBoolean){
                              Haploid hap = (Haploid)glstring;
                              HapList.add(hap);
                              
                          }else if(objectType.equals("Glstring") && GlstringBoolean){
	                       	  Glstring gl = (Glstring)glstring;
	                          List<String> Alleles = Splitter.onPattern("[+]+").splitToList(gl.getValue().replaceAll("[\n\r ]", ""));                         
	                       	  expected.put(id, Alleles.get(0));
	                       	  expected.put(id, Alleles.get(1));
                          }          
                          
                	   }
                	   
                	   if(HaploidBoolean){
                		   String hlaTyping1 = HapList.get(0).getLocus() + "*" +HapList.get(0).getType();
                		   String hlaTyping2 = HapList.get(1).getLocus() + "*" +HapList.get(1).getType();
                		   expected.put(id, hlaTyping1);
                		   expected.put(id, hlaTyping2);
                	   }               	   

                   }
                }
            }
        }else{       
	        try {
	            reader = reader(expectedFile);
	
	            int lineNumber = 1;
	            while (reader.ready()) {
	                String line = reader.readLine();
	                if (line == null) {
	                    break;
	                }
			
	                List<String> tokens = Splitter.onPattern("\\s+").splitToList(line);
	                if (tokens.size() != 6) {
	                    throw new IOException("invalid expected file format at line " + lineNumber);
	                }
	
	                String sample = tokens.get(0);
	                //String locus = tokens.get(1);
	                //String regionsFile = tokens.get(2);
	                //String zygosity = tokens.get(3);
	                String firstAllele = tokens.get(4);
	                String secondAllele = tokens.get(5);
	
	                expected.put(sample, firstAllele);
	                expected.put(sample, secondAllele);
	
	                lineNumber++;
	            }
	        }
	        finally {
	            try {
	                reader.close();
	            }
	            catch (Exception e) {
	                // ignore
	            }
	        }
        }
        return expected;
    }

    static ListMultimap<String, String> readObserved(final File observedFile) throws IOException {
       
        ListMultimap<String, String> observed = ArrayListMultimap.create();
        BufferedReader reader = null;
        if(isHML(observedFile)){
        	reader = reader(observedFile);
            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String id = sample.getId();
                for (Typing typing : sample.getTyping()) {
                   for (AlleleAssignment alleleAssignment : typing.getAlleleAssignment()) {
                	   List<Haploid> HapList = new ArrayList<Haploid>();
                	   for (Object glstring : alleleAssignment.getPropertyAndHaploidAndGenotypeList()){
                           String classType  = glstring.getClass().toString();
                           String objectType = classType.substring(classType.indexOf("jaxb.") + 5,classType.length());
                           
                           if(objectType.equals("Haploid") && HaploidBoolean){
                               Haploid hap = (Haploid)glstring;
                               HapList.add(hap);
                               
                           }else if(objectType.equals("Glstring") && GlstringBoolean){
                        	   Glstring gl = (Glstring)glstring;
                        	   observed.put(id, gl.getValue().replaceAll("[\n\r ]", ""));
                           }
                		   
                	   }
                	   if(HaploidBoolean){
                		   String hlaTyping1 = HapList.get(0).getLocus() + "*" +HapList.get(0).getType();
                		   String hlaTyping2 = HapList.get(1).getLocus() + "*" +HapList.get(1).getType();
                		   observed.put(id, hlaTyping1);
                		   observed.put(id, hlaTyping2);
                	   }
                   }
                }
            }
        }else{
        	
	        try {
	        	reader = reader(observedFile);
	        	
	            int lineNumber = 1;
	            while (reader.ready()) {
	                String line = reader.readLine();
	                if (line == null) {
	                    break;
	                }
	
	                List<String> tokens = Splitter.onPattern("\\s+").splitToList(line);
	                if (tokens.size() != 2) {
	                    throw new IOException("invalid observed file format at line " + lineNumber);
	                }
	
	                String sample = tokens.get(0);
	                String interpretation = tokens.get(1);
	
	                observed.put(sample, interpretation);
	
	                lineNumber++;
	            }
	        }
	        finally {
	            try {
	                reader.close();
	            }
	            catch (Exception e) {
	                // ignore
	            }
	        }
        }
        return observed;
    }

    static int matchByField(final String firstAllele, final String secondAllele) {
        checkNotNull(firstAllele);
        checkNotNull(secondAllele);
        List<String> firstAlleleParts = Splitter.on(":").splitToList(firstAllele);
        List<String> secondAlleleParts = Splitter.on(":").splitToList(secondAllele);
        int smallest = firstAlleleParts.size() < secondAlleleParts.size() ? firstAlleleParts.size() : secondAlleleParts.size();

        for (int i = 0; i < smallest; i++) {
            if (!firstAlleleParts.get(i).equals(secondAlleleParts.get(i))) {
                return i;
            }
        }
        return smallest;
    }

    static boolean isHML(final File hmlFile) {
    	String file = hmlFile.toString();
        if(file.matches("(.+)\\.hml") || file.matches("(.+)\\.xml")){
        	return true;
        }else{
        	return false;
        }   
    }
    
    
    
    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument expectedFile = new FileArgument("e", "expected-file", "expected interpretation file", true);
        FileArgument observedFile = new FileArgument("b", "observed-file", "observed interpretation file", true);
        FileArgument outputFile = new FileArgument("o", "output-file", "output file, default stdout", false);
        IntegerArgument resolution = new IntegerArgument("r", "resolution", "resolution, must be in the range [1..4], default " + DEFAULT_RESOLUTION, false);
        Switch printSummary = new Switch("s", "summary", "print summary");
        Switch HaploidBoolean = new Switch("l", "haploid", "Flag for extracting Haploid data");        
        Switch GlstringBoolean = new Switch("g", "glstring", "Flag for extracting Glstring data");  
      
        
        ArgumentList arguments = new ArgumentList(about, help, expectedFile, observedFile, HaploidBoolean,  GlstringBoolean, outputFile, resolution, printSummary);
        CommandLine commandLine = new CommandLine(args);

        ValidateInterpretation validateInterpretation = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            validateInterpretation = new ValidateInterpretation(expectedFile.getValue(), observedFile.getValue(), HaploidBoolean.wasFound(), GlstringBoolean.wasFound(), outputFile.getValue(), resolution.getValue(DEFAULT_RESOLUTION), printSummary.wasFound());
        }
        catch (CommandLineParseException e) {
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(validateInterpretation.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    
}
