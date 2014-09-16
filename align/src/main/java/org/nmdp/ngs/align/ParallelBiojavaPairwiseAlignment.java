/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.google.common.collect.Lists;

import org.biojava.bio.alignment.AlignmentPair;
import org.biojava.bio.alignment.NeedlemanWunsch;
import org.biojava.bio.alignment.SmithWaterman;
import org.biojava.bio.alignment.SubstitutionMatrix;

import org.biojava.bio.seq.Sequence;

/**
 * Parallel implementation of PairwiseAlignment based on Biojava.
 */
public final class ParallelBiojavaPairwiseAlignment extends AbstractPairwiseAlignment {
    /** Executor service. */
    private final ExecutorService executorService;


    private ParallelBiojavaPairwiseAlignment(final ExecutorService executorService,
                                             final SubstitutionMatrix substitutionMatrix) {
        super(substitutionMatrix);
        this.executorService = executorService;
    }


    @Override
    public Iterable<AlignmentPair> local(final List<Sequence> queries,
                                         final List<Sequence> subjects,
                                         final GapPenalties gapPenalties) {
        checkNotNull(queries);
        checkNotNull(subjects);
        checkNotNull(gapPenalties);

        if (queries.isEmpty() || subjects.isEmpty()) {
            return Collections.<AlignmentPair>emptyList();
        }

        List<SmithWatermanTask> tasks = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        for (Sequence query : queries) {
            for (Sequence subject : subjects) {
                tasks.add(new SmithWatermanTask(query, subject, gapPenalties, getSubstitutionMatrix()));
            }
        }

        List<AlignmentPair> alignmentPairs = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        try {
            List<Future<AlignmentPair>> futures = executorService.invokeAll(tasks);

            for (Future<AlignmentPair> future : futures) {
                alignmentPairs.add(future.get());
            }
        }
        catch (ExecutionException e) {
            // todo
        }
        catch (InterruptedException e) {
            // todo
        }
        return alignmentPairs;
    }

    @Override
    public Iterable<AlignmentPair> global(final List<Sequence> queries,
                                          final List<Sequence> subjects,
                                          final GapPenalties gapPenalties) {
        checkNotNull(queries);
        checkNotNull(subjects);
        checkNotNull(gapPenalties);

        if (queries.isEmpty() || subjects.isEmpty()) {
            return Collections.<AlignmentPair>emptyList();
        }

        List<NeedlemanWunschTask> tasks = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        for (Sequence query : queries) {
            for (Sequence subject : subjects) {
                tasks.add(new NeedlemanWunschTask(query, subject, gapPenalties, getSubstitutionMatrix()));
            }
        }

        List<AlignmentPair> alignmentPairs = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        try {
            List<Future<AlignmentPair>> futures = executorService.invokeAll(tasks);

            for (Future<AlignmentPair> future : futures) {
                alignmentPairs.add(future.get());
            }
        }
        catch (ExecutionException e) {
            // todo
        }
        catch (InterruptedException e) {
            // todo
        }
        return alignmentPairs;
    }

    /**
     * Smith-Waterman task.
     */
    private static final class SmithWatermanTask implements Callable<AlignmentPair> {
        private final Sequence query;
        private final Sequence subject;
        private final GapPenalties gapPenalties;
        private final SubstitutionMatrix substitutionMatrix;

        private SmithWatermanTask(final Sequence query,
                                  final Sequence subject,
                                  final GapPenalties gapPenalties,
                                  final SubstitutionMatrix substitutionMatrix) {
            this.query = query;
            this.subject = subject;
            this.gapPenalties = gapPenalties;
            this.substitutionMatrix = substitutionMatrix;
        }

        @Override
        public AlignmentPair call() {
            SmithWaterman smithWaterman = new SmithWaterman(gapPenalties.match(),
                                                            gapPenalties.replace(),
                                                            gapPenalties.insert(),
                                                            gapPenalties.delete(),
                                                            gapPenalties.extend(),
                                                            substitutionMatrix);
            return smithWaterman.pairwiseAlignment(query, subject);
        }
    }

    /**
     * Needleman-Wunsch task.
     */
    private static final class NeedlemanWunschTask implements Callable<AlignmentPair> {
        private final Sequence query;
        private final Sequence subject;
        private final GapPenalties gapPenalties;
        private final SubstitutionMatrix substitutionMatrix;

        private NeedlemanWunschTask(final Sequence query,
                                    final Sequence subject,
                                    final GapPenalties gapPenalties,
                                    final SubstitutionMatrix substitutionMatrix) {
            this.query = query;
            this.subject = subject;
            this.gapPenalties = gapPenalties;
            this.substitutionMatrix = substitutionMatrix;
        }

        @Override
        public AlignmentPair call() throws Exception {
            NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch(gapPenalties.match(),
                                                                  gapPenalties.replace(),
                                                                  gapPenalties.insert(),
                                                                  gapPenalties.delete(),
                                                                  gapPenalties.extend(),
                                                                  substitutionMatrix);
            return needlemanWunsch.pairwiseAlignment(query, subject);
        }
    }

    /**
     * Create and return a new pairwise alignment implementation configured with a cached thread pool and the default
     * substitution matrix (NUC.4.4.txt).
     *
     * @return a new pairwise alignment implementation configured with a cached thread pool and the default
     *    substitution matrix (NUC.4.4.txt)
     */
    public static PairwiseAlignment create() {
        return create(SubstitutionMatrix.getNuc4_4());
    }

    /**
     * Create and return a new pairwise alignment implementation configured with a fixed thread pool and the default
     * substitution matrix (NUC.4.4.txt).
     *
     * @param threads number of threads, must be <code>&gt;= 0</code>
     * @return a new pairwise alignment implementation configured with a fixed thread pool and the default
     *    substitution matrix (NUC.4.4.txt)
     */
    public static PairwiseAlignment create(final int threads) {
        return create(threads, SubstitutionMatrix.getNuc4_4());
    }

    /**
     * Create and return a new pairwise alignment implementation configured with a cached thread pool and the specified
     * substitution matrix.
     *
     * @param substitutionMatrix substitution matrix, must not be null
     * @return a new pairwise alignment implementation configured with a cached thread pool and the specified
     *    substitution matrix
     */
    public static PairwiseAlignment create(final SubstitutionMatrix substitutionMatrix) {
        return new ParallelBiojavaPairwiseAlignment(Executors.newCachedThreadPool(), substitutionMatrix);
    }

    /**
     * Create and return a new pairwise alignment implementation configured with a fixed thread pool and the specified
     * substitution matrix.
     *
     * @param threads number of threads, must be <code>&gt;= 0</code>
     * @param substitutionMatrix substitution matrix, must not be null
     * @return a new pairwise alignment implementation configured with a fixed thread pool and the specified
     *    substitution matrix
     */
    public static PairwiseAlignment create(final int threads, final SubstitutionMatrix substitutionMatrix) {
        return new ParallelBiojavaPairwiseAlignment(Executors.newFixedThreadPool(threads), substitutionMatrix);
    }
}
