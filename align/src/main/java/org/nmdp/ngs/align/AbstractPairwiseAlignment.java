/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.alignment.AlignmentPair;
import org.biojava.bio.alignment.SubstitutionMatrix;

/**
 * Abstract pairwise DNA sequence alignment implementation.
 */
abstract class AbstractPairwiseAlignment implements PairwiseAlignment {
    /** Substitution matrix. */
    private final SubstitutionMatrix substitutionMatrix;


    /**
     * Create a new abstract pairwise alignment with the specified substitution matrix.
     *
     * @param substitutionMatrix substitution matrix
     */
    protected AbstractPairwiseAlignment(final SubstitutionMatrix substitutionMatrix) {
        this.substitutionMatrix = substitutionMatrix;
    }


    /**
     * Return the substitution matrix for this pairwise alignment.
     *
     * @return the substitution matrix for this pairwise alignment
     */
    public final SubstitutionMatrix getSubstitutionMatrix() {
        return substitutionMatrix;
    }

    @Override
    public Iterable<AlignmentPair> local(final Sequence query, final Sequence subject) {
        return local(ImmutableList.of(query), ImmutableList.of(subject), DEFAULT_LOCAL_GAP_PENALTIES);
    }

    @Override
    public Iterable<AlignmentPair> local(final Sequence query,
                                         final Sequence subject,
                                         final GapPenalties gapPenalties) {
        return local(ImmutableList.of(query), ImmutableList.of(subject), gapPenalties);
    }

    @Override
    public Iterable<AlignmentPair> local(final Sequence query, final List<Sequence> subjects) {
        return local(ImmutableList.of(query), subjects, DEFAULT_LOCAL_GAP_PENALTIES);
    }

    @Override
    public Iterable<AlignmentPair> local(final Sequence query,
                                         final List<Sequence> subjects,
                                         final GapPenalties gapPenalties) {
        return local(ImmutableList.of(query), subjects, gapPenalties);
    }

    @Override
    public Iterable<AlignmentPair> local(final List<Sequence> queries, final List<Sequence> subjects) {
        return local(queries, subjects, DEFAULT_LOCAL_GAP_PENALTIES);
    }

    @Override
    public Iterable<AlignmentPair> global(final Sequence query, final Sequence subject) {
        return global(ImmutableList.of(query), ImmutableList.of(subject), DEFAULT_GLOBAL_GAP_PENALTIES);
    }

    @Override
    public Iterable<AlignmentPair> global(final Sequence query,
                                          final Sequence subject,
                                          final GapPenalties gapPenalties) {
        return global(ImmutableList.of(query), ImmutableList.of(subject), gapPenalties);
    }

    @Override
    public Iterable<AlignmentPair> global(final Sequence query, final List<Sequence> subjects) {
        return global(ImmutableList.of(query), subjects, DEFAULT_GLOBAL_GAP_PENALTIES);
    }

    @Override
    public Iterable<AlignmentPair> global(final Sequence query,
                                          final List<Sequence> subjects,
                                          final GapPenalties gapPenalties) {
        return global(ImmutableList.of(query), subjects, gapPenalties);
    }

    @Override
    public Iterable<AlignmentPair> global(final List<Sequence> queries, final List<Sequence> subjects) {
        return global(queries, subjects, DEFAULT_GLOBAL_GAP_PENALTIES);
    }
}
