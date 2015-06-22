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

import java.util.List;

import java.util.concurrent.Callable;

import com.google.common.base.Splitter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringListArgument;

import org.nmdp.gl.Allele;
import org.nmdp.gl.AlleleList;
import org.nmdp.gl.Genotype;
import org.nmdp.gl.Haplotype;

import org.nmdp.gl.client.GlClient;
import org.nmdp.gl.client.GlClientException;

import org.nmdp.gl.client.local.LocalGlClient;

/**
 * Validate interpretation.
 */
public final class ValidateInterpretation implements Callable<Integer> {
    private final File observedFile;
    private final File outputFile;
    private final File expectedFile;
    private final int resolution;
    private final List<String> loci;
    private final boolean printSummary;
    private final GlClient glclient;
    static final int DEFAULT_RESOLUTION = 2;
    static final List<String> DEFAULT_LOCI = ImmutableList.of("HLA-A", "HLA-B", "HLA-C", "HLA-DRB1", "HLA-DQB1");
    private static final String USAGE = "ngs-validate-interpretation -e expected.txt -b observed.txt -r 2 -l \"HLA-A,HLA-B\"";
    

    /**
     * Validate interpretation.
     *
     * @param expectedFile expected file, at least one of expected or observed file must not be null
     * @param observedFile observed file, at least one of expected or observed file must not be null
     * @param outputFile output file, if any
     * @param resolution resolution, must be in the range [1..4]
     * @param loci list of loci to validate, must not be null
     * @param printSummary print summary report
     * @param glclient genotype list client, must not be null
     */
    public ValidateInterpretation(final File expectedFile, final File observedFile, final File outputFile, final int resolution, final List<String> loci, final boolean printSummary, final GlClient glclient) {
        checkNotNull(loci);
        checkNotNull(glclient);
        checkArgument(expectedFile != null || observedFile != null, "at least one of expected or observed file must not be null");
        checkArgument(resolution > 0 && resolution < 5, "resolution must be in the range [1..4]");
        this.observedFile = observedFile;
        this.expectedFile = expectedFile;
        this.outputFile = outputFile;
        this.resolution = resolution;
        this.loci = loci;
        this.printSummary = printSummary;
        this.glclient = glclient;
    }


    @Override
    public Integer call() throws Exception {
        PrintWriter writer = null;

        int passes = 0;
        int failures = 0;
        try {
            writer = writer(outputFile);

            // interpretations keyed by sample
            ListMultimap<String, Interpretation> expected = read(expectedFile);
            ListMultimap<String, Interpretation> observed = read(observedFile);

            
            
            // for each sample in observed
            for (String sample : observed.keySet()) {
            	
            // for each matching sample in expected
            	for (Interpretation e : expected.get(sample)) {
            		Genotype expectedGenotype = asGenotype(e);
            		for (Haplotype expectedHaplotype : expectedGenotype.getHaplotypes()) {
            			for (AlleleList expectedAlleleList : expectedHaplotype.getAlleleLists()) {
            				if (shouldValidate(e, expectedAlleleList)) {
            					// for each pair of expected and observed alleles
            					for (Allele expectedAllele : expectedAlleleList.getAlleles()) {

            						boolean match = false;

            						for (Interpretation o : observed.get(sample)) {
            							AlleleList observedAlleleList = asAlleleList(o);

            							if (sameLocus(observedAlleleList, expectedAlleleList)) {
        									for (Allele observedAllele : observedAlleleList.getAlleles()) {
        										if (matchByField(expectedAllele.getGlstring(), observedAllele.getGlstring()) >= resolution) {
        											match = true;
        											break;
        										}
        									}
            							}
            							if (match) {
            								passes++;
            							}
            							else {
            								failures++;
            							}


            						}
            						if (!printSummary) {
            							writer.println((match ? "PASS" : "FAIL") + "\t" + sample + "\t" + expectedAllele);
            						}
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

    boolean shouldValidate(final Interpretation interpretation, final AlleleList alleleList) {
        checkNotNull(interpretation);
        checkNotNull(alleleList);
        return loci.contains(interpretation.locus()) && interpretation.locus().equals(alleleList.getAlleles().get(0).getLocus().getGlstring());
    }

    /*

      todo: allow for various strategies

      - GL String syntax checking only, use LocalGlClient with strict mode off (necessary if interpretations contain allele codes)
      - re-parse GL Strings under reported version of nomenclature, use LocalGlClient in strict mode with reported IMGT/HLA nomenclature
      - register GL Strings under reported version of nomenclature, use GlClient in strict mode against reported IMGT/HLA nomenclature namespace
      - re-parse GL Strings under latest version of nomenclature, regardless of the reported version, use LocalGlClient in strict mode with latest IMGT/HLA nomenclature
      - register GL Strings under latest version of nomenclature, regardless of the reported version, use GlClient in strict mode against latest IMGT/HLA nomenclature namespace
      - re-parse GL Strings under latest version of nomenclature, using liftover service to generate new GL Strings, then use LocalGlClient in strict mode with latest IMGT/HLA nomenclature
      - register GL Strings under latest version of nomenclature, using liftover service to generate new GL Strings, then use GlClient in strict mode against latest IMGT/HLA nomenclature namespace

     */
    AlleleList asAlleleList(final Interpretation interpretation) throws IOException {
        checkNotNull(interpretation);
        try {
            return glclient.createAlleleList(interpretation.glstring());
        }
        catch (GlClientException e) {
            throw new IOException("could not convert interpretation to allele list, caught " + e.getMessage(), e);
        }
    }

    Genotype asGenotype(final Interpretation interpretation) throws IOException {
        checkNotNull(interpretation);
        try {
            return glclient.createGenotype(interpretation.glstring());
        }
        catch (GlClientException e) {
            throw new IOException("could not convert interpretation to genotype, caught " + e.getMessage(), e);
        }
    }


    static int matchByField(final String allele0, final String allele1) {
        checkNotNull(allele0);
        checkNotNull(allele1);
        List<String> allele0Parts = Splitter.on(":").splitToList(allele0);
        List<String> allele1Parts = Splitter.on(":").splitToList(allele1);
        int smallest = Math.min(allele0Parts.size(), allele1Parts.size());

        for (int i = 0; i < smallest; i++) {
            if (!allele0Parts.get(i).equals(allele1Parts.get(i))) {
                return i;
            }
        }
        return smallest;
    }

    static boolean sameLocus(final AlleleList alleleList0, final AlleleList alleleList1) {
        checkNotNull(alleleList0);
        checkNotNull(alleleList1);
        return (alleleList0.getAlleles().get(0).getLocus().equals(alleleList1.getAlleles().get(0).getLocus()));
    }

    static ListMultimap<String, Interpretation> read(final File file) throws IOException {
        BufferedReader reader = null;
        final ListMultimap<String, Interpretation> interpretations = ArrayListMultimap.create();
        final Interpretation.Builder builder = Interpretation.builder();
        try {
            reader = reader(file);
            CharStreams.readLines(reader, new LineProcessor<Void>() {
                    private int count = 0;

                    @Override
                    public boolean processLine(final String line) throws IOException {
                        String[] tokens = line.split("\t");
                        if (tokens.length < 6) {
                            throw new IOException("illegal interpretation format, expected at least 6 columns, found " + tokens.length + "\nline=" + line);
                        }
                        Interpretation interpretation = builder.reset()
                            .withSample(tokens[0])
                            .withLocus(tokens[1])
                            .withGeneFamily(tokens[2])
                            .withAlleleDb(tokens[3])
                            .withAlleleVersion(tokens[4])
                            .withGlstring(tokens[5])
                            .withConsensus(tokens.length > 6 ? tokens[6] : null)
                            .build();

                        interpretations.put(interpretation.sample(), interpretation);
                        count++;
                        return true;
                    }

                    @Override
                    public Void getResult() {
                        return null;
                    }
                });

            return interpretations;
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

    static final class Interpretation {
        private final String sample;
        private final String locus;
        private final String geneFamily;
        private final String alleleDb;
        private final String alleleVersion;
        private final String glstring;
        private final String consensus;

        private Interpretation(final String sample,
                               final String locus,
                               final String geneFamily,
                               final String alleleDb,
                               final String alleleVersion,
                               final String glstring,
                               final String consensus) {
            this.sample = sample;
            this.locus = locus;
            this.geneFamily = geneFamily;
            this.alleleDb = alleleDb;
            this.alleleVersion = alleleVersion;
            this.glstring = glstring;
            this.consensus = consensus;
        }

        String sample() {
            return sample;
        }

        String locus() {
            return locus;
        }

        String geneFamily() {
            return geneFamily;
        }

        String alleleDb() {
            return alleleDb;
        }

        String alleleVersion() {
            return alleleVersion;
        }

        String glstring() {
            return glstring;
        }

        String consensus() {
            return consensus;
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {
            private String sample;
            private String locus;
            private String geneFamily;
            private String alleleDb;
            private String alleleVersion;
            private String glstring;
            private String consensus;

            private Builder() {
                // empty
            }

            Builder withSample(final String sample) {
                this.sample = sample;
                return this;
            }

            Builder withLocus(final String locus) {
                this.locus = locus;
                return this;
            }

            Builder withGeneFamily(final String geneFamily) {
                this.geneFamily = geneFamily;
                return this;
            }

            Builder withAlleleDb(final String alleleDb) {
                this.alleleDb = alleleDb;
                return this;
            }

            Builder withAlleleVersion(final String alleleVersion) {
                this.alleleVersion = alleleVersion;
                return this;
            }

            Builder withGlstring(final String glstring) {
                this.glstring = glstring;
                return this;
            }

            Builder withConsensus(final String consensus) {
                this.consensus = consensus;
                return this;
            }

            Builder reset() {
                sample = null;
                locus = null;
                geneFamily = null;
                alleleDb = null;
                alleleVersion = null;
                glstring = null;
                consensus = null;
                return this;
            }

            Interpretation build() {
                return new Interpretation(sample, locus, geneFamily, alleleDb, alleleVersion, glstring, consensus);
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

        FileArgument expectedFile = new FileArgument("e", "expected-file", "expected interpretation file, default stdin; at least one of expected or observed file must be provided", false);
        FileArgument observedFile = new FileArgument("b", "observed-file", "observed interpretation file, default stdin; at least one of expected or observed file must be provided", false);
        FileArgument outputFile = new FileArgument("o", "output-file", "output file, default stdout", false);
        IntegerArgument resolution = new IntegerArgument("r", "resolution", "resolution, must be in the range [1..4], default " + DEFAULT_RESOLUTION, false);
        StringListArgument loci = new StringListArgument("l", "loci", "list of loci to validate, default " + DEFAULT_LOCI, false);
        Switch printSummary = new Switch("s", "summary", "print summary");

        ArgumentList arguments = new ArgumentList(about, help, expectedFile, observedFile, outputFile, resolution, loci, printSummary);
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

            // todo: allow for configuration of glclient
            validateInterpretation = new ValidateInterpretation(expectedFile.getValue(), observedFile.getValue(), outputFile.getValue(), resolution.getValue(DEFAULT_RESOLUTION), loci.getValue(DEFAULT_LOCI), printSummary.wasFound(), LocalGlClient.create());
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
