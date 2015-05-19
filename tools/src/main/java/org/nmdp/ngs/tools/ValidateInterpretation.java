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
import java.util.HashMap;
import java.util.Map;
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
import org.dishevelled.commandline.argument.StringListArgument;

/**
 * Validate interpretation.
 */
public final class ValidateInterpretation implements Callable<Integer> {
    private final File observedFile;
    private final File outputFile;
    private final File expectedFile; 
    private final int resolution;
    private final List<String> lociList;
    private final boolean printSummary;
    static final int DEFAULT_RESOLUTION = 2;
    
    private static final String USAGE = "ngs-extract-expected-haploids -i expected.xml | ngs-validate-interpretation -b observed.txt -g -l  [args]";
    
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
    public ValidateInterpretation(final File expectedFile, final File observedFile, final List<String> lociList, final File outputFile, final int resolution, final boolean printSummary) {
    	checkNotNull(observedFile);
        checkArgument(resolution > 0 && resolution < 5, "resolution must be in the range [1..4]");
        this.observedFile = observedFile;
        this.expectedFile = expectedFile;
        this.outputFile = outputFile;
        this.resolution = resolution;
        this.printSummary = printSummary;
        this.lociList = lociList;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        
        int passes = 0;
        int failures = 0;
        try {
            writer = writer(outputFile);
            Map<String, SubjectTyping> expected = readExpected(expectedFile,lociList);
            Map<String, SubjectTyping> observed = readObserved(observedFile,lociList);	
   
            for (String sample : expected.keySet()) {
            	for(String loc : lociList){

            		if(observed.get(sample) != null){
            		
		                List<String> alleles = expected.get(sample).getTyping(loc);
		                List<String> interpretations = observed.get(sample).getTyping(loc);
	
		                if(interpretations.size() != 0){
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
			                        writer.println((result ? "PASS" : "FAIL") + "\t" + sample + "\t" + expectedAllele );
			                    }
			                }
	            		}
		                
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
           	 e.printStackTrace();
           	 System.exit(1);
            }
        }
    }
    

     static Map<String, SubjectTyping> readExpected(final File expectedFile, final List<String> lociList) throws IOException {

    	BufferedReader reader = null;
        Map<String, SubjectTyping> expected = new HashMap<String, SubjectTyping>();
        
    	try{
    		
    		reader = reader(expectedFile);

    		String input;
            int lineNumber = 1;
            while((input=reader.readLine())!=null){
            	

    			List<String> idGenotype  = Splitter.onPattern("\t").splitToList(input.replaceAll("[\n\r ]", "")); 			
    			
                if (idGenotype.size() != 2) {
                    throw new IOException("invalid expected file format at line " + lineNumber);
                }

    			String subjectID         = idGenotype.get(0);
    			String genotype          = idGenotype.get(1);
                List<String> alleles     = Splitter.onPattern("[+]+").splitToList(genotype);                   
              	List<String> loci        = Splitter.onPattern("[*]").splitToList(alleles.get(0));
              	
                SubjectTyping subject    = expected.get(subjectID);
                if(subject == null){	
                	subject  = new SubjectTyping(lociList);
                	expected.put(subjectID, subject);
                }
                
                subject.addTyping(loci.get(0), alleles.get(0));
                subject.addTyping(loci.get(1), alleles.get(1));
                
                lineNumber++;
            }
    		
    		
    	}finally {
	            try {
	                reader.close();
	            }
	            catch (Exception e) {
	            	 e.printStackTrace();
	            	 System.exit(1);
	            }
	        }
        
        
        return expected;
    }

    
    static Map<String, SubjectTyping> readObserved(final File observedFile, final List<String> lociList) throws IOException {
       
        Map<String, SubjectTyping> observed = new HashMap<String, SubjectTyping>();
        
        BufferedReader reader = null;
        try {
        	reader = reader(observedFile);
        	
            int lineNumber = 1;
            String previousLoc = null;
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

                String subjectID = sample;
                if (sample.matches("(.+)\\.bam")) {
                	List<String> filePath  = Splitter.onPattern("(/)|(\\.)").splitToList(sample);  
                	String sampleId        = filePath.get(filePath.size()-6);
                	List<String> subId     = Splitter.onPattern("(_)").splitToList(sampleId); 
                	subjectID = subId.get(0);
                }
                
                String interpretation = tokens.get(1);

                
                SubjectTyping subject = observed.get(subjectID);
                if(subject == null){
                	subject  = new SubjectTyping(lociList);
                	observed.put(subjectID, subject);
                }
                
                if(interpretation.matches("HLA(.+)")){
                	String locus = getLocusFromInterp(interpretation);
                	previousLoc = locus;
                	subject.addTyping(locus, interpretation);
                }else{
                	if(previousLoc != null){
                		subject.addSeq(previousLoc, interpretation);
                	}
                }
              
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
        
        return observed;
    }

    
    static String getLocusFromInterp(String interp){
    	List<String> interpretedAlleles = Splitter.onPattern("[/|]+").splitToList(interp);
    	List<String> lociAllele = Splitter.onPattern("[*]").splitToList(interpretedAlleles.get(0));
    	return lociAllele.get(0);
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
        if (file.matches("(.+)\\.hml") || file.matches("(.+)\\.xml")) {
            return true;
        }
        else {
            return false;
        }
    }
    
    static String removeLocus(String locusAllele){
    	List<String> alleleParts = Splitter.on("*").splitToList(locusAllele);
    	String allele = alleleParts.get(1);
    	return allele;
    }
  
  
    public static class SubjectTyping{
    	
    	private List<String> lociList;
        final ListMultimap<String, String> typingList     = ArrayListMultimap.create(); 
        final ListMultimap<String, String> seqList        = ArrayListMultimap.create();    	
        final ListMultimap<String, String> typingListTrim = ArrayListMultimap.create();
    	
    	public SubjectTyping(List<String> lociList) {
    		super();
    		this.lociList = lociList;
    	}
    	
    	public void addTyping(String locus, String typing){
    		//locus exists in list
			lociList = lociList == null ? Splitter.on(",").splitToList("HLA-A,HLA-B,HLA-C,HLA-DRB1,HLA-DQB1") 
					: lociList;
   	
    		if(lociList.contains(locus)){
    			typingList.put(locus, typing);
    			String noLocus = typing.replaceAll(locus +"[*]", "");
    			typingListTrim.put(locus,noLocus);
    		}
    	}
    	
    	public void addSeq(String locus, String sequence){
    		if(locus !=null && lociList.contains(locus)){
    			seqList.put(locus, sequence);
    		}
    	}
    	

    	public List<String> getTyping(String locus) {
    		return typingList.get(locus);
    	}
    	
    	public List<String> getTrimedTyping(String locus) {
    		return typingListTrim.get(locus);
    	}
    	
    	public List<String> getSeq(String locus) {
    		return seqList.get(locus);
    	}    	
    	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((lociList == null) ? 0 : lociList.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubjectTyping other = (SubjectTyping) obj;
			if (lociList == null) {
				if (other.lociList != null)
					return false;
			} else if (!lociList.equals(other.lociList))
				return false;
			return true;
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
        
        FileArgument expectedFile  = new FileArgument("e", "expected-file", "expected interpretation file", false);
        FileArgument observedFile  = new FileArgument("b", "observed-file", "observed interpretation file", true);
        FileArgument outputFile    = new FileArgument("o", "output-file", "output file, default stdout", false);
        IntegerArgument resolution = new IntegerArgument("r", "resolution", "resolution, must be in the range [1..4], default " + DEFAULT_RESOLUTION, false);
        
        StringListArgument loci = new StringListArgument("l", "loci", "list of loci that will be validated", false);
        Switch printSummary     = new Switch("s", "summary", "print summary");
        ArgumentList arguments  = new ArgumentList(about, help, expectedFile, observedFile, outputFile, resolution, loci, printSummary);
        CommandLine commandLine = new CommandLine(args);

        ValidateInterpretation validateInterpretation = null;
        try
        {
            CommandLineParser.parse(commandLine, arguments);
            
            List<String> locList  = loci.getValue() == null ? Splitter.on(",").splitToList("HLA-A,HLA-B,HLA-C,HLA-DRB1,HLA-DQB1") : loci.getValue();
            
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }

            validateInterpretation = new ValidateInterpretation( expectedFile.getValue(), observedFile.getValue(), locList, outputFile.getValue(), resolution.getValue(DEFAULT_RESOLUTION), printSummary.wasFound());
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
