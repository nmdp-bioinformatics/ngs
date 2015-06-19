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

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;
import static org.nmdp.ngs.hml.HmlUtils.toDnaSymbolList;
import groovyjarjarantlr.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.biojava.bio.BioException;
import org.biojava.bio.program.fastq.StreamListener;
import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;
import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.symbol.Symbol;
import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.nmdp.ngs.align.HighScoringPair;
import org.nmdp.ngs.align.HspReader;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Extract consensus sequences from a file in HML format.
 */
public final class ExtractBlast implements Callable<Integer> {
    private final File inputBlastFile;
    private final File inputFastaFile;
    private final File outputFile;
    private final int alleleCutoff;
    private static final String USAGE = "ngs-extract-blast [args]";


    /**
     * Extract consensus sequences from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     */
    public ExtractBlast(final File inputBlastFile, final File inputFastqFile, final File outputFile,final int alleleCutoff) {
        this.inputBlastFile = inputBlastFile;
        this.inputFastaFile = inputFastqFile;
        this.alleleCutoff   = alleleCutoff;
        this.outputFile     = outputFile;
    }


    @Override
    public Integer call() throws Exception {
    	PrintWriter writer = null;
    	
        try {
        	writer = writer(outputFile);
        	String subjectId = getSubjectId(inputFastaFile);
        	Map<String, String> fastaSequence = readFasta(inputFastaFile);
        	Map<String, BlastResults> blastResults  = readBlast(alleleCutoff, inputBlastFile);
        	
        	 for (String seqId : fastaSequence.keySet()) {

        		 BlastResults blast = blastResults.get(seqId);
        		 String glstring =  blast.getTypingGlstring();
        		 String locus    =  blast.getLocus();
        		 String sequence = fastaSequence.get(seqId);
        		 
        		 writer.println(subjectId + "\t" + locus + "\t" + "HLA" +
        		 "\t" + "IMGT/HLA" + "\t" + "na" + "\t" + glstring + "\t" + sequence);
        		 
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


    static Map<String, String> readFasta(final File inputFastqFile) throws IOException, NoSuchElementException, BioException{
    	
    	BufferedReader reader = null;
    	HashMap<String, String> fasta = new HashMap<String, String>();
        
    	try{
    		 reader = reader(inputFastqFile);
    		 for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) 
    		{
                 Sequence sequence = sequences.nextSequence();
                 String sequenceId = sequence.getName();
                 String fastaSeq   = sequence.seqString();
                 fasta.put(sequenceId, fastaSeq);
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
    
        
        return fasta;
    	
    } 
    
    static HashMap<String, BlastResults> readBlast(final int alleleCutoff, final File inputBlastFile) throws IOException{
    	
        BufferedReader reader = null;
        HashMap<String, BlastResults> blast = new HashMap<String, BlastResults>();
        
        try {
            reader = new BufferedReader(new FileReader(inputBlastFile));

            for (HighScoringPair hsp : HspReader.read(reader)) {

            	//id	allele 
            	List<String> BlastnParts = Splitter.on("\t").splitToList(hsp.toString());
            	String sequenceId        = BlastnParts.get(0);
            	String allele            = BlastnParts.get(1);
            
               BlastResults blastResults    = blast.get(sequenceId);
               if(blastResults == null){
            	   blastResults  = new BlastResults(alleleCutoff);
               	   blast.put(sequenceId, blastResults);
               }
               
               blastResults.addTyping(allele);
            }
            
        }finally {
            try {
            	//fastqReader.close();
            }
            catch (Exception e) {
            	 e.printStackTrace();
            	 System.exit(1);
            }
        }
    
        
        return blast;
    	
    }  
    
    
    static String getSubjectId(final File fastaFile){
    	String fileAsString = fastaFile.toString();
    	List<String> alleleParts = Splitter.on("_").splitToList(fileAsString);
    	List<String> pathParts  = Splitter.on("/").splitToList(alleleParts.get(0));
    	return pathParts.get(pathParts.size()-1);
    }
    
    public static class BlastResults{
    	
    	private int alleleRank = 1;
    	final int alleleCutoff;
        final List<String> typingList     = new ArrayList<String>(); 
        final ListMultimap<String, String> seqList        = ArrayListMultimap.create();    	
        final ListMultimap<String, String> typingListTrim = ArrayListMultimap.create();
    	
    	public BlastResults(int alleleCutoff) {
    		super();
    		this.alleleCutoff = alleleCutoff;
    	}
    	
    	public void addTyping(String typing){
    		//locus exists in list
    		if(alleleRank <= alleleCutoff){
    			typingList.add("HLA-" + typing);
    		}
    		alleleRank++;
    	}

    	public List<String> getTypingList() {	
    		return typingList;
    	}
    	
    	public String getTypingGlstring() {
    		String glstring = Joiner.on("/").join(typingList);
    		return glstring;
    	}
    	
    	public String getLocus(){
    		String allele = typingList.get(0);
    		List<String> alleleParts = Splitter.on("*").splitToList(allele);
        	String locus = "HLA-" + alleleParts.get(0);
    		return locus;
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
        
        IntegerArgument alleleCutoff = new IntegerArgument("c", "allele-cutoff", "allele cutoff value", false);
        FileArgument inputBlastFile  = new FileArgument("i", "input-blast-file", "input blast file, default stdin", false);
        FileArgument inputFastaFile  = new FileArgument("f", "input-fasta-file", "input fastq file, default stdin", false);
        FileArgument outputFile      = new FileArgument("o", "output-file", "output file, default stdout", false);
        
        ArgumentList arguments  = new ArgumentList(about, help, inputBlastFile, inputFastaFile, outputFile);
        CommandLine commandLine = new CommandLine(args);

        ExtractBlast extractBlast = null;
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

            int cutoff  = alleleCutoff.getValue() == null ? 10 : alleleCutoff.getValue();
            extractBlast = new ExtractBlast(inputBlastFile.getValue(),inputFastaFile.getValue(),outputFile.getValue(),cutoff);
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractBlast.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
