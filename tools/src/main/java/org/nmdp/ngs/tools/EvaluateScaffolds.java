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

import static com.google.common.base.Preconditions.checkNotNull;

import static org.dishevelled.compress.Readers.reader;
import static org.dishevelled.compress.Writers.writer;

import static org.nmdp.ngs.align.Blastn.blastn;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import java.util.concurrent.Callable;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import org.biojava.bio.BioException;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;

import org.nmdp.ngs.align.HighScoringPair;

/**
 * Evaluate assembly scaffolds against a reference sequence.
 */
@SuppressWarnings("deprecation")
public final class EvaluateScaffolds implements Callable<Integer> {
    private final File referenceFastaFile;
    private final File scaffoldsFastaFile;
    private final File evalFile;
    private static final String USAGE = "ngs-evaluate-scaffolds -r reference.fa.gz -s scaffolds.fa.gz";


    /**
     * Evaluate assembly scaffolds against a reference sequence.
     *
     * @param referenceFastaFile input reference FASTA file, must not be null
     * @param scaffoldsFastaFile input scaffolds FASTA file, must not be null
     * @param evalFile output eval file, if any
     */
    public EvaluateScaffolds(final File referenceFastaFile, final File scaffoldsFastaFile, final File evalFile) {
        checkNotNull(referenceFastaFile);
        checkNotNull(scaffoldsFastaFile);
        this.referenceFastaFile = referenceFastaFile;
        this.scaffoldsFastaFile = scaffoldsFastaFile;
        this.evalFile = evalFile;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;
        try {
            writer = writer(evalFile);

            Sequence reference = readReference();
            List<Sequence> scaffolds = readScaffolds();

            writer.println("#reference length = " + reference.length());
            writer.println("#scaffold count = " + scaffolds.size());
            writer.println("#scaffold lengths = " + dumpLengths(scaffolds));

            RangeSet<Long> ranges = TreeRangeSet.create();
            for (HighScoringPair hsp : blastn(referenceFastaFile, scaffoldsFastaFile)) {
                if (reference.getName().equals(hsp.target())) {
                    writer.println("#" + hsp.toString());
                    if (hsp.targetStart() <= hsp.targetEnd()) { // strands match
                        ranges.add(Range.closed(hsp.targetStart(), hsp.targetEnd()));
                    }
                    else {
                        ranges.add(Range.closed(hsp.targetEnd(), hsp.targetStart()));
                    }
                }
            }

            writer.println("#coalesced intervals = " + ranges);

            long breadthOfCoverage = 0;
            for (Range<Long> range : ranges.asRanges()) {
                breadthOfCoverage += ContiguousSet.create(range, DiscreteDomain.longs()).size();
            }
            double normalizedBreadthOfCoverage = (double) breadthOfCoverage / (double) reference.length();
            writer.println("#breadth-of-coverage = " + breadthOfCoverage);
            writer.println("#normalized breadth-of-coverage = " + normalizedBreadthOfCoverage);

            StringBuilder sb = new StringBuilder();
            sb.append(referenceFastaFile.getName());
            sb.append("\t");
            sb.append(scaffoldsFastaFile.getName());
            sb.append("\t");
            sb.append(reference.length());
            sb.append("\t");
            sb.append(scaffolds.size());
            sb.append("\t");
            sb.append(ranges.asRanges().size());
            sb.append("\t");
            sb.append(breadthOfCoverage);
            sb.append("\t");
            sb.append(normalizedBreadthOfCoverage);
            sb.append("\t");
            writer.println(sb.toString());

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

    private Sequence readReference() throws IOException, BioException {
        Sequence sequence = null;
        BufferedReader reader = null;
        try {
            reader = reader(referenceFastaFile);
            SequenceIterator sequences = SeqIOTools.readFastaDNA(reader);
            if (!sequences.hasNext()) {
                throw new IOException("reference FASTA file was empty");
            }
            sequence = sequences.nextSequence();
            if (sequences.hasNext()) {
                throw new IOException("reference FASTA file contains more than one sequence");
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
        return sequence;
    }

    private List<Sequence> readScaffolds() throws IOException, BioException {
        BufferedReader reader = null;
        List<Sequence> scaffolds = Lists.newLinkedList();
        try {
            reader = reader(scaffoldsFastaFile);

            for (SequenceIterator sequences = SeqIOTools.readFastaDNA(reader); sequences.hasNext(); ) {
                Sequence sequence = sequences.nextSequence();
                scaffolds.add(sequence);
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
        return scaffolds;
    }

    private static String dumpLengths(final List<Sequence> scaffolds) {
        StringBuilder sb = new StringBuilder();
        Iterator<Sequence> iterator = scaffolds.iterator();
        sb.append(iterator.next().length());
        while (iterator.hasNext()) {
            sb.append(", ");
            sb.append(iterator.next().length());
        }
        return sb.toString();
    }


    /**
     * Main.
     *
     * @param args command line args
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument referenceFastaFile = new FileArgument("r", "reference", "input reference FASTA file", true);
        FileArgument scaffoldsFastaFile = new FileArgument("s", "scaffolds", "input scaffolds FASTA file", true);
        FileArgument evalFile = new FileArgument("e", "eval", "output eval file, default stdout", false);

        ArgumentList arguments = new ArgumentList(about, help, referenceFastaFile, scaffoldsFastaFile, evalFile);
        CommandLine commandLine = new CommandLine(args);

        EvaluateScaffolds evaluateScaffolds = null;
        try {
            CommandLineParser.parse(commandLine, arguments);
            if (about.wasFound()) {
                About.about(System.out);
                System.exit(0);
            }
            if (help.wasFound()) {
                Usage.usage(USAGE, null, commandLine, arguments, System.out);
                System.exit(0);
            }
            evaluateScaffolds = new EvaluateScaffolds(referenceFastaFile.getValue(), scaffoldsFastaFile.getValue(), evalFile.getValue());
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
            System.exit(evaluateScaffolds.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
