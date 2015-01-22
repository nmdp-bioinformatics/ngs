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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.Callable;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

import org.biojava.bio.program.fastq.FastqVariant;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.dishevelled.commandline.ArgumentList;
import org.dishevelled.commandline.CommandLine;
import org.dishevelled.commandline.CommandLineParseException;
import org.dishevelled.commandline.CommandLineParser;
import org.dishevelled.commandline.Switch;
import org.dishevelled.commandline.Usage;

import org.dishevelled.commandline.argument.DoubleArgument;
import org.dishevelled.commandline.argument.FileArgument;
import org.dishevelled.commandline.argument.IntegerArgument;
import org.dishevelled.commandline.argument.StringArgument;

import org.nmdp.ngs.reads.CoverageStrategy;
import org.nmdp.ngs.reads.MutationStrategy;
import org.nmdp.ngs.reads.QualityStrategy;

import org.nmdp.ngs.reads.coverage.MeanCoverageStrategy;
import org.nmdp.ngs.reads.coverage.MinimumCoverageStrategy;

import org.nmdp.ngs.reads.mutation.AmbiguousSubstitutionMutationStrategy;
import org.nmdp.ngs.reads.mutation.CompositeMutationStrategy;
import org.nmdp.ngs.reads.mutation.DeletionMutationStrategy;
import org.nmdp.ngs.reads.mutation.IdentityMutationStrategy;
import org.nmdp.ngs.reads.mutation.IndelMutationStrategy;
import org.nmdp.ngs.reads.mutation.InsertionMutationStrategy;
import org.nmdp.ngs.reads.mutation.SubstitutionMutationStrategy;

import org.nmdp.ngs.reads.quality.RealDistributionQualityStrategy;
import org.nmdp.ngs.reads.quality.ScoreFunctionQualityStrategy;
import org.nmdp.ngs.reads.quality.ScoreFunctions;

/**
 * Generate paired-end next generation sequencing (NGS/HTS) reads.
 */
@SuppressWarnings("deprecation")
public final class GeneratePairedEndReads implements Callable<Integer> {
    private final File referenceFile;
    private final File firstReadFile;
    private final File secondReadFile;
    private final RandomGenerator random;
    private final RealDistribution length;
    private final RealDistribution insertSize;
    private final QualityStrategy quality;
    private final CoverageStrategy coverage;
    private final double mutationRate;
    private final MutationStrategy mutation;

    private static final double DEFAULT_MEAN_LENGTH = 60.0d;
    private static final double DEFAULT_LENGTH_VARIATION = 10.0d;
    private static final double DEFAULT_MEAN_INSERT_SIZE = 500.0d;
    private static final double DEFAULT_INSERT_SIZE_VARIATION = 50.0d;
    private static final int DEFAULT_MINIMUM_COVERAGE = 20;
    private static final double DEFAULT_MEAN_QUALITY = 25.0d;
    private static final double DEFAULT_QUALITY_VARIATION = 5.0d;
    private static final double DEFAULT_MEAN_QUALITY_WEIGHT = 0.9d;
    private static final double DEFAULT_QUALITY_WEIGHT_VARIATION = 0.1d;
    private static final double DEFAULT_EXTEND_INSERTION_RATE = 0.4d;
    private static final int DEFAULT_MAXIMUM_INSERTION_LENGTH = 8;
    private static final double DEFAULT_INSERTION_RATE = 0.5d;
    private static final double DEFAULT_DELETION_RATE = 0.5d;
    private static final double DEFAULT_SUBSTITUTION_RATE = 0.4d;
    private static final double DEFAULT_INDEL_RATE = 0.6d;
    private static final double DEFAULT_AMBIGUOUS_RATE = 0.0d;
    private static final double DEFAULT_MUTATION_RATE = 0.05d;
    private static final double NO_VARIATION = 1.0E-100;
    static final CoverageStrategy DEFAULT_COVERAGE = new MinimumCoverageStrategy(DEFAULT_MINIMUM_COVERAGE);
    static final MutationStrategy DEFAULT_MUTATION = new IdentityMutationStrategy();
    private static final String USAGE = "ngs-generate-paired-end-reads -1 out_R1.fq.gz -2 out_R2.fq.gz [args]";


    /**
     * Generate paired-end next generation sequencing (NGS/HTS) reads.
     *
     * @param referenceFile reference file, if any
     * @param firstReadFile first read file, must not be null
     * @param secondReadFile second read file, must not be null
     * @param random random generator, must not be null
     * @param length length distribution, must not be null
     * @param insertSize insert size distribution, mut not be null
     * @param quality quality strategy, must not be null
     * @param coverage coverage strategy, must not be null
     * @param mutationRate mutation rate
     * @param mutation mutation strategy, must not be null
     */
    public GeneratePairedEndReads(final File referenceFile,
                                  final File firstReadFile,
                                  final File secondReadFile,
                                  final RandomGenerator random,
                                  final RealDistribution length,
                                  final RealDistribution insertSize,
                                  final QualityStrategy quality,
                                  final CoverageStrategy coverage,
                                  final double mutationRate,
                                  final MutationStrategy mutation) {

        checkNotNull(firstReadFile);
        checkNotNull(secondReadFile);
        checkNotNull(random);
        checkNotNull(length);
        checkNotNull(insertSize);
        checkNotNull(quality);
        checkNotNull(coverage);
        checkNotNull(mutation);
        this.referenceFile = referenceFile;
        this.firstReadFile = firstReadFile;
        this.secondReadFile = secondReadFile;
        this.random = random;
        this.length = length;
        this.insertSize = insertSize;
        this.quality = quality;
        this.coverage = coverage;
        this.mutationRate = mutationRate;
        this.mutation = mutation;
    }


    @Override
    public Integer call() throws Exception {
        BufferedReader reader = null;
        PrintWriter firstWriter = null;
        PrintWriter secondWriter = null;
        try {
            reader = reader(referenceFile);

            // since writes append to each output file, they must be deleted first
            firstReadFile.delete();
            secondReadFile.delete();

            SequenceIterator sequences = SeqIOTools.readFastaDNA(reader);
            while (sequences.hasNext()) {
                Sequence sequence = sequences.nextSequence();
                try {
                    firstWriter = writer(firstReadFile, true);
                    secondWriter = writer(secondReadFile, true);

                    new org.nmdp.ngs.reads.GeneratePairedEndReads(sequence, FastqVariant.FASTQ_SANGER, random, length, insertSize, quality, coverage, mutationRate, mutation, firstWriter, secondWriter, new SangerFastqWriter()).run();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
                finally {
                    try {
                        firstWriter.flush();
                    }
                    catch (Exception e) {
                        // ignore
                    }
                    try {
                        secondWriter.flush();
                    }
                    catch (Exception e) {
                        // ignore
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
                firstWriter.close();
            }
            catch (Exception e) {
                // ignore
            }
            try {
                secondWriter.close();
            }
            catch (Exception e) {
                // ignore
            }
        }
    }


    /**
     * Main.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        Switch about = new Switch("a", "about", "display about message");
        Switch help = new Switch("h", "help", "display help message");
        FileArgument referenceFile = new FileArgument("r", "reference", "reference input file, in fasta format, default stdin", false);
        FileArgument firstReadFile = new FileArgument("1", "first-read", "first read output file, in fastq format", true);
        FileArgument secondReadFile = new FileArgument("2", "second-read", "second read output file, in fastq format", true);
        DoubleArgument meanLength = new DoubleArgument("l", "mean-length", "mean length, default " + DEFAULT_MEAN_LENGTH, false);
        DoubleArgument lengthVariation = new DoubleArgument("v", "length-variation", "length variation, default " + DEFAULT_LENGTH_VARIATION, false);
        DoubleArgument meanInsertSize = new DoubleArgument("j", "mean-insert-size", "mean insert size, default " + DEFAULT_MEAN_INSERT_SIZE, false);
        DoubleArgument insertSizeVariation = new DoubleArgument("k", "insert-size-variation", "insert size variation, default " + DEFAULT_INSERT_SIZE_VARIATION, false);
        IntegerArgument minimumCoverage = new IntegerArgument("c", "minimum-coverage", "minimum coverage, default " + DEFAULT_MINIMUM_COVERAGE, false);
        IntegerArgument meanCoverage = new IntegerArgument("g", "mean-coverage", "mean coverage", false);
        StringArgument qualityType = new StringArgument("u", "quality", "quality strategy type { illumina, normal }, default normal", false);
        DoubleArgument meanQualityWeight = new DoubleArgument("w", "mean-quality-weight", "mean quality weight, default " + DEFAULT_MEAN_QUALITY_WEIGHT, false);
        DoubleArgument qualityWeightVariation = new DoubleArgument("t", "quality-weight-variation", "quality weight variation, default " + DEFAULT_QUALITY_WEIGHT_VARIATION, false);
        DoubleArgument meanQuality = new DoubleArgument("q", "mean-quality", "mean quality, default " + DEFAULT_MEAN_QUALITY, false);
        DoubleArgument qualityVariation = new DoubleArgument("f", "quality-variation", "quality variation, default " + DEFAULT_QUALITY_VARIATION, false);
        StringArgument mutationType = new StringArgument("m", "mutation", "mutation strategy type { substitution, insertion, deletion, ambiguous, indel, composite }, default identity", false);
        DoubleArgument extendInsertionRate = new DoubleArgument("x", "extend-insertion-rate", "extend insertion rate, default " + DEFAULT_EXTEND_INSERTION_RATE, false);
        IntegerArgument maximumInsertionLength = new IntegerArgument("e", "maximum-insertion-length", "maximum insertion length, default " + DEFAULT_MAXIMUM_INSERTION_LENGTH, false);
        DoubleArgument insertionRate = new DoubleArgument("i", "insertion-rate", "insertion rate, default " + DEFAULT_INSERTION_RATE, false);
        DoubleArgument deletionRate = new DoubleArgument("d", "deletion-rate", "deletion rate, default " + DEFAULT_DELETION_RATE, false);
        DoubleArgument substitutionRate = new DoubleArgument("s", "substitution-rate", "substitution rate, default " + DEFAULT_SUBSTITUTION_RATE, false);
        DoubleArgument indelRate = new DoubleArgument("y", "indel-rate", "indel rate, default " + DEFAULT_INDEL_RATE, false);
        DoubleArgument ambiguousRate = new DoubleArgument("b", "ambiguous-rate", "ambiguous substitution rate, default " + DEFAULT_AMBIGUOUS_RATE, false);
        DoubleArgument mutationRate = new DoubleArgument("n", "mutation-rate", "mutation rate, default " + DEFAULT_MUTATION_RATE, false);
        IntegerArgument seed = new IntegerArgument("z", "seed", "random number seed, default relates to current time", false);

        ArgumentList arguments = new ArgumentList(about, help, referenceFile, firstReadFile, secondReadFile, meanLength, lengthVariation,
                                                  meanInsertSize, insertSizeVariation, minimumCoverage, meanCoverage,
                                                  qualityType, meanQualityWeight, qualityWeightVariation, meanQuality, qualityVariation,
                                                  mutationType, extendInsertionRate, maximumInsertionLength, insertionRate, deletionRate,
                                                  substitutionRate, indelRate, ambiguousRate, mutationRate, seed);

        CommandLine commandLine = new CommandLine(args);

        GeneratePairedEndReads generatePairedEndReads = null;
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

            RandomGenerator random = seed.wasFound() ? new MersenneTwister(seed.getValue()) : new MersenneTwister();

            double lv = Math.max(NO_VARIATION, lengthVariation.getValue(DEFAULT_LENGTH_VARIATION));
            RealDistribution length = new NormalDistribution(random, meanLength.getValue(DEFAULT_MEAN_LENGTH), lv, NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);

            double isv = Math.max(NO_VARIATION, insertSizeVariation.getValue(DEFAULT_INSERT_SIZE_VARIATION));
            RealDistribution insertSize = new NormalDistribution(random, meanInsertSize.getValue(DEFAULT_MEAN_INSERT_SIZE), isv, NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);

            CoverageStrategy coverage = DEFAULT_COVERAGE;
            if (minimumCoverage.wasFound()) {
                coverage = new MinimumCoverageStrategy(minimumCoverage.getValue());
            }
            else if (meanCoverage.wasFound()) {
                coverage = new MeanCoverageStrategy(meanCoverage.getValue());
            }

            QualityStrategy quality = null;
            if ("illumina".equals(qualityType.getValue())) {
                RealDistribution realDistribution = new NormalDistribution(random, meanQualityWeight.getValue(DEFAULT_MEAN_QUALITY_WEIGHT), qualityWeightVariation.getValue(DEFAULT_QUALITY_WEIGHT_VARIATION), NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
                quality = new ScoreFunctionQualityStrategy(realDistribution, ScoreFunctions.illumina());
            }
            else {
                RealDistribution realDistribution = new NormalDistribution(random, meanQuality.getValue(DEFAULT_MEAN_QUALITY), qualityVariation.getValue(DEFAULT_QUALITY_VARIATION), NormalDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY);
                quality = new RealDistributionQualityStrategy(realDistribution);
            }

            MutationStrategy mutation = DEFAULT_MUTATION;
            if (mutationType.wasFound()) {
                if ("substitution".equals(mutationType.getValue())) {
                    mutation = new SubstitutionMutationStrategy(random);
                }
                else if ("ambiguous".equals(mutationType.getValue())) {
                    mutation = new AmbiguousSubstitutionMutationStrategy();
                }
                else if ("insertion".equals(mutationType.getValue())) {
                    mutation = new InsertionMutationStrategy(random, extendInsertionRate.getValue(DEFAULT_EXTEND_INSERTION_RATE), maximumInsertionLength.getValue(DEFAULT_MAXIMUM_INSERTION_LENGTH));
                }
                else if ("deletion".equals(mutationType.getValue())) {
                    mutation = new DeletionMutationStrategy();
                }
                else if ("indel".equals(mutationType.getValue())) {
                    InsertionMutationStrategy insertion = new InsertionMutationStrategy(random, insertionRate.getValue(DEFAULT_INSERTION_RATE), maximumInsertionLength.getValue(DEFAULT_MAXIMUM_INSERTION_LENGTH));
                    DeletionMutationStrategy deletion = new DeletionMutationStrategy();
                    mutation = new IndelMutationStrategy(random, insertion, insertionRate.getValue(DEFAULT_INSERTION_RATE), deletion, deletionRate.getValue(DEFAULT_DELETION_RATE));
                }
                else if ("composite".equals(mutationType.getValue())) {
                    SubstitutionMutationStrategy substitution = new SubstitutionMutationStrategy(random);
                    InsertionMutationStrategy insertion = new InsertionMutationStrategy(random, insertionRate.getValue(DEFAULT_INSERTION_RATE), maximumInsertionLength.getValue(DEFAULT_MAXIMUM_INSERTION_LENGTH));
                    DeletionMutationStrategy deletion = new DeletionMutationStrategy();
                    IndelMutationStrategy indel = new IndelMutationStrategy(random, insertion, insertionRate.getValue(DEFAULT_INSERTION_RATE), deletion, deletionRate.getValue(DEFAULT_DELETION_RATE));
                    AmbiguousSubstitutionMutationStrategy ambiguous = new AmbiguousSubstitutionMutationStrategy();
                    mutation = new CompositeMutationStrategy(random, substitution, substitutionRate.getValue(DEFAULT_SUBSTITUTION_RATE), indel, indelRate.getValue(DEFAULT_INDEL_RATE), ambiguous, ambiguousRate.getValue(DEFAULT_AMBIGUOUS_RATE));
                }
            }

            generatePairedEndReads = new GeneratePairedEndReads(referenceFile.getValue(), firstReadFile.getValue(), secondReadFile.getValue(), random, length, insertSize, quality, coverage, mutationRate.getValue(DEFAULT_MUTATION_RATE), mutation);
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
            System.exit(generatePairedEndReads.call());
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
