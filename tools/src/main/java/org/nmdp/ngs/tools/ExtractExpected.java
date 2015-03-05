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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;

import com.google.common.base.Splitter;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.hml.HmlReader;

import org.nmdp.ngs.hml.jaxb.Glstring;
import org.nmdp.ngs.hml.jaxb.Haploid;
import org.nmdp.ngs.hml.jaxb.AlleleAssignment;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Extract expected allele assignments from a file in HML format.
 */
public final class ExtractExpected implements Callable<Integer> {
    private final File inputHmlFile;
    private final File outputFile;
    private final boolean HaploidBoolean;
    private final boolean GlstringBoolean;
    static final int DEFAULT_FLAG = 0;
    private static final String USAGE = "ngs-extract-expected [args]";


    /**
     * Extract expected allele assignments from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     * @param outputFile output interpretation file, if any
     */
    public ExtractExpected(final File inputHmlFile, final File outputFile, final boolean HaploidBoolean, final boolean GlstringBoolean) {
        this.inputHmlFile = inputHmlFile;
        this.outputFile = outputFile;
        this.HaploidBoolean = HaploidBoolean;
        this.GlstringBoolean = GlstringBoolean;        
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter writer = null;
        try {
            reader = reader(inputHmlFile);
            writer = writer(outputFile);

            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String id = sample.getId();
                for (Typing typing : sample.getTyping()) {
                    for (AlleleAssignment alleleAssignment : typing.getAlleleAssignment()) {
                        List<Haploid> HapList = new ArrayList<Haploid>();
                        for (Object glstring : alleleAssignment.getPropertyAndHaploidAndGenotypeList()) {
                            String classType  = glstring.getClass().toString();
                            String objectType = classType.substring(classType.indexOf("jaxb.") + 5,classType.length());
                    
                            if (objectType.equals("Haploid") && HaploidBoolean) {
                                Haploid hap = (Haploid)glstring;
                                HapList.add(hap);
                            }
                            else if(objectType.equals("Glstring") && GlstringBoolean) {
                                Glstring gl = (Glstring)glstring;
                                List<String> Alleles = Splitter.onPattern("[+]+").splitToList(gl.getValue().replaceAll("[\n\r ]", ""));                          

                                String locus = Alleles.get(0).substring(Alleles.get(0).indexOf("-") - 3, Alleles.get(0).indexOf("*"));
                                int zygosity = (Alleles.get(0).equals(Alleles.get(1))) ? 1 : 2;

                                String ars = (locus.equals("HLA-A")) ? "./tutorial/regions/grch38/hla-a/hla-a.ars.txt" :
                                    (locus.equals("HLA-B")) ? "./tutorial/regions/grch38/hla-b/hla-b.ars.txt" : 
                                    (locus.equals("HLA-C")) ? "./tutorial/regions/grch38/hla-c/hla-a.ars.txt" : 	  
                                    (locus.equals("HLA-DRB1")) ? "./tutorial/regions/grch38/hla-drb1/hla-drb1.ars.txt" : 	  
                                    (locus.equals("HLA-DQB1")) ? "./tutorial/regions/grch38/hla-dqb1/hla-dqb1.ars.txt" : 
                                    (locus.equals("HLA-DPB1")) ? "./tutorial/regions/grch38/hla-dpb1/hla-dpb1.ars.txt" : "N/A";

                                writer.println(id + ".fastq.contigs.bwa.sorted.bam\t" + locus + "\t" + ars + "\t" + zygosity + "\t" + Alleles.get(0) + "\t" + Alleles.get(1));
                            }
                        }

                        if (HaploidBoolean) {
                            String hlaTyping1 = HapList.get(0).getLocus() + "*" + HapList.get(0).getType();
                            String hlaTyping2 = HapList.get(1).getLocus() + "*" + HapList.get(1).getType();             		  
                            String locus = HapList.get(0).getLocus();

                            if (!HapList.get(0).getLocus().equals(HapList.get(1).getLocus())) {
                                //error
                            }

                            int zygosity = (HapList.get(0).equals(HapList.get(1))) ? 1 : 2;

                            String ars = (locus.equals("HLA-A")) ? "./tutorial/regions/grch38/hla-a/hla-a.ars.txt" :
                                (locus.equals("HLA-B")) ? "./tutorial/regions/grch38/hla-b/hla-b.ars.txt" : 
                                (locus.equals("HLA-C")) ? "./tutorial/regions/grch38/hla-c/hla-a.ars.txt" : 	  
                                (locus.equals("HLA-DRB1")) ? "./tutorial/regions/grch38/hla-drb1/hla-drb1.ars.txt" : 	  
                                (locus.equals("HLA-DQB1")) ? "./tutorial/regions/grch38/hla-dqb1/hla-dqb1.ars.txt" : 
                                (locus.equals("HLA-DPB1")) ? "./tutorial/regions/grch38/hla-dpb1/hla-dpb1.ars.txt" : "N/A";                    		   

                            writer.println(id + ".fastq.contigs.bwa.sorted.bam\t" + locus + "\t" + ars + "\t" + zygosity + "\t" + hlaTyping1 + "\t" + hlaTyping2);
                        }
                    }
                }
            }
            return 0;
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                writer.close();
            }
            catch (Exception e) {
                // ignore
            }
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
        FileArgument inputHmlFile = new FileArgument("i", "input-hml-file", "input HML file, default stdin", false);
        FileArgument outputFile = new FileArgument("o", "output-file", "output allele assignment file, default stdout", false);
        Switch HaploidBoolean = new Switch("l", "haploid", "Flag for extracting Haploid data");        
        Switch GlstringBoolean = new Switch("g", "glstring", "Flag for extracting Glstring data");  
        
        ArgumentList arguments = new ArgumentList(about, help, inputHmlFile, outputFile, HaploidBoolean, GlstringBoolean);
        CommandLine commandLine = new CommandLine(args);

        ExtractExpected extractExpected = null;
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

            //System.out.println(HaploidBoolean.wasFound());
            //System.out.println(GlstringBoolean.wasFound());
            
            if ((HaploidBoolean.wasFound() && GlstringBoolean.wasFound()) || (HaploidBoolean.wasFound()) && GlstringBoolean.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.err);
                System.exit(-1);
            }
            extractExpected = new ExtractExpected(inputHmlFile.getValue(), outputFile.getValue(), HaploidBoolean.wasFound(), GlstringBoolean.wasFound());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractExpected.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
