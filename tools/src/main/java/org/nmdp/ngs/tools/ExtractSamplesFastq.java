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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.nmdp.ngs.hml.HmlReader;

import org.nmdp.ngs.hml.jaxb.ConsensusSequence;
import org.nmdp.ngs.hml.jaxb.ConsensusSequenceBlock;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.ReferenceDatabase;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.Typing;

/**
 * Extract consensus sequences from a file in HML format.
 */
public final class ExtractSamplesFastq implements Callable<Integer> {
    private final File inputHmlFile;
    private final String subjectId;
    private static final String USAGE = "ngs-extract-samples [args]";


    /**
     * Extract consensus sequences from a file in HML format.
     *
     * @param inputHmlFile input HML file, if any
     */
    public ExtractSamplesFastq(final File inputHmlFile, final String subjectId) {
        this.inputHmlFile = inputHmlFile;
        this.subjectId    = subjectId;
    }


    @Override
    public Integer call() throws Exception {
    	
        BufferedReader reader = null;
        try {
            reader = reader(inputHmlFile);
            Hml hml = HmlReader.read(reader);
            for (Sample sample : hml.getSample()) {
                String id = sample.getId();

                if(id.equals(subjectId)){
                    
    	                for (Typing typing : sample.getTyping()) {
    	                    for (ConsensusSequence consensusSequence : typing.getConsensusSequence()) {

    	                            int blocks = 0;
    	                            for (ConsensusSequenceBlock consensusSequenceBlock : consensusSequence.getConsensusSequenceBlock()) {
    	                                StringBuilder sb = new StringBuilder(1200);
    	                                sb.append(">");
    	                                sb.append(blocks);
    	                                sb.append("|");
    	                                sb.append(((ReferenceDatabase.ReferenceSequence) consensusSequenceBlock.getReferenceSequenceId()).getId());
    	                                sb.append("|");
    	                                sb.append(id);
    	                                sb.append("|");
    	                                sb.append(consensusSequenceBlock.getStart() == null ? "" : consensusSequenceBlock.getStart());
    	                                sb.append("|");
    	                                sb.append(consensusSequenceBlock.getPhasingGroup() == null ? "" : consensusSequenceBlock.getPhasingGroup());
    	                                sb.append("|");
    	                                sb.append(consensusSequenceBlock.getExpectedCopyNumber() == null ? "" : consensusSequenceBlock.getExpectedCopyNumber());
    	                                sb.append("|");
    	                                sb.append(consensusSequenceBlock.getVariant().isEmpty() ? "1" : "0");
    	                                sb.append("|");
    	                                sb.append((consensusSequenceBlock.isContinuity() != null && consensusSequenceBlock.isContinuity().booleanValue()) ? "1" : "0");
    	                                sb.append("\n");
    	                                sb.append(toDnaSymbolList(consensusSequenceBlock.getSequence()).seqString().toUpperCase());
                             
    	                                System.out.println(sb.toString());
    	                                
    	                                blocks++;
    	                            }
    	                        
    	
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
        }
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help  = new Switch("h", "help", "display help message");

        StringArgument subjectid  = new StringArgument("s", "subjectid", "subject id to be extracted", false);
        FileArgument inputHmlFile = new FileArgument("i"  , "input-hml-file", "input HML file, default stdin", false);


        ArgumentList arguments  = new ArgumentList(about, help, inputHmlFile, subjectid);
        CommandLine commandLine = new CommandLine(args);

        ExtractSamplesFastq extractSamples = null;
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
            extractSamples = new ExtractSamplesFastq(inputHmlFile.getValue(),subjectid.getValue());
        }
        catch (CommandLineParseException | IllegalArgumentException e) {
            Usage.usage(USAGE, e, commandLine, arguments, System.err);
            System.exit(-1);
        }
        try {
            System.exit(extractSamples.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
