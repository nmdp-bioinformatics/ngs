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

import java.util.List;

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.alignment.AlignmentPair;

/**
 * Pairwise DNA sequence alignment.
 */
public interface PairwiseAlignment {
    /** Default local alignment gap penalties, <code>match -1 replace 3 insert 2 delete 2 extend 1</code>. */
    final GapPenalties DEFAULT_LOCAL_GAP_PENALTIES = GapPenalties.create(-1, 3, 2, 2, 1);

    /** Default global alignment gap penalties, <code>match 0 replace 2 insert 4 delete 4 extend 6</code>. */
    final GapPenalties DEFAULT_GLOBAL_GAP_PENALTIES = GapPenalties.create(0, 2, 4, 4, 6);


    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the default local alignment gap penalties.
     *
     * @see #DEFAULT_LOCAL_GAP_PENALTIES
     * @param query query DNA sequence, must not be null
     * @param subject subject DNA sequence, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(Sequence query, Sequence subject);

    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param query query DNA sequence, must not be null
     * @param subject subject DNA sequence, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(Sequence query, Sequence subject, GapPenalties gapPenalties);

    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the default local alignment gap penalties.
     *
     * @see #DEFAULT_LOCAL_GAP_PENALTIES
     * @param query query DNA sequence, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(Sequence query, List<Sequence> subjects);

    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param query query DNA sequence, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(Sequence query, List<Sequence> subjects, GapPenalties gapPenalties);

    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the default local alignment gap penalties.
     *
     * @see #DEFAULT_LOCAL_GAP_PENALTIES
     * @param queries list of query DNA sequences, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(List<Sequence> queries, List<Sequence> subjects);

    /**
     * Return the alignment pairs from local alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param queries list of query DNA sequences, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from local alignment of the query and subject DNA sequences
     *    with the default local alignment gap penalties
     */
    Iterable<AlignmentPair> local(List<Sequence> queries, List<Sequence> subjects, GapPenalties gapPenalties);

    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the default global alignment gap penalties.
     *
     * @see #DEFAULT_LOCAL_GAP_PENALTIES
     * @param query query DNA sequence, must not be null
     * @param subject subject DNA sequence, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(Sequence query, Sequence subject);

    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param query query DNA sequence, must not be null
     * @param subject subject DNA sequence, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(Sequence query, Sequence subject, GapPenalties gapPenalties);


    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the default global alignment gap penalties.
     *
     * @see #DEFAULT_GLOBAL_GAP_PENALTIES
     * @param query query DNA sequence, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(Sequence query, List<Sequence> subjects);

    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param query query DNA sequence, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(Sequence query, List<Sequence> subjects, GapPenalties gapPenalties);

    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the default global alignment gap penalties.
     *
     * @see #DEFAULT_GLOBAL_GAP_PENALTIES
     * @param queries list of query DNA sequences, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(List<Sequence> queries, List<Sequence> subjects);

    /**
     * Return the alignment pairs from global alignment of the query and subject DNA sequences
     * with the specified gap penalties.
     *
     * @param queries list of query DNA sequences, must not be null
     * @param subjects list of subject DNA sequences, must not be null
     * @param gapPenalties gap penalties, must not be null
     * @return zero or more alignment pairs from global alignment of the query and subject DNA sequences
     *    with the default global alignment gap penalties
     */
    Iterable<AlignmentPair> global(List<Sequence> queries, List<Sequence> subjects, GapPenalties gapPenalties);
}
