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

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import org.biojava.bio.BioException;

import org.biojava.bio.alignment.AlignmentPair;
import org.biojava.bio.alignment.NeedlemanWunsch;
import org.biojava.bio.alignment.SmithWaterman;
import org.biojava.bio.alignment.SubstitutionMatrix;

import org.biojava.bio.seq.Sequence;

/**
 * Serial implementation of PairwiseAlignment based on Biojava.
 */
public final class BiojavaPairwiseAlignment extends AbstractPairwiseAlignment implements Serializable {

    private BiojavaPairwiseAlignment(final SubstitutionMatrix substitutionMatrix) {
        super(substitutionMatrix);
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

        SmithWaterman smithWaterman = new SmithWaterman(gapPenalties.match(),
                                                        gapPenalties.replace(),
                                                        gapPenalties.insert(),
                                                        gapPenalties.delete(),
                                                        gapPenalties.extend(),
                                                        getSubstitutionMatrix());

        List<AlignmentPair> alignmentPairs = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        for (Sequence query : queries) {
            for (Sequence subject : subjects) {
                AlignmentPair alignmentPair = smithWaterman.pairwiseAlignment(query, subject);
                alignmentPairs.add(alignmentPair);
            }
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

        NeedlemanWunsch needlemanWunsch = new NeedlemanWunsch(gapPenalties.match(),
                                                              gapPenalties.replace(),
                                                              gapPenalties.insert(),
                                                              gapPenalties.delete(),
                                                              gapPenalties.extend(),
                                                              getSubstitutionMatrix());

        List<AlignmentPair> alignmentPairs = Lists.newArrayListWithExpectedSize(queries.size() * subjects.size());
        for (Sequence query : queries) {
            for (Sequence subject : subjects) {
                try {
                    AlignmentPair alignmentPair = needlemanWunsch.pairwiseAlignment(query, subject);
                    alignmentPairs.add(alignmentPair);
                }
                catch (BioException e) {
                    // todo
                }
            }
        }
        return alignmentPairs;
    }

    /**
     * Create and return a new pairwise alignment implementation with the default substitution matrix (NUC.4.4.txt).
     *
     * @return a new pairwise alignment implementation with the default substitution matrix (NUC.4.4.txt)
     */
    public static PairwiseAlignment create() {
        return create(SubstitutionMatrix.getNuc4_4());
    }

    /**
     * Create and return a new pairwise alignment implementation with the specified substitution matrix.
     *
     * @param substitutionMatrix substitution matrix, must not be null
     * @return a new pairwise alignment implementation with the specified substitution matrix
     */
    public static PairwiseAlignment create(final SubstitutionMatrix substitutionMatrix) {
        return new BiojavaPairwiseAlignment(substitutionMatrix);
    }

    // serialization support

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(final ObjectInputStream in) throws InvalidObjectException {
        throw new InvalidObjectException("proxy required for serialization");
    }

    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("proxy required for deserialization");
    }

    /**
     * Serialization proxy.
     */
    private static final class SerializationProxy implements Serializable {
        private final SubstitutionMatrix substitutionMatrix;

        private SerializationProxy(final BiojavaPairwiseAlignment pairwiseAlignment) {
            this.substitutionMatrix = pairwiseAlignment.getSubstitutionMatrix();
        }

        private Object readResolve() {
            return BiojavaPairwiseAlignment.create(substitutionMatrix);
        }

        private static final long serialVersionUID = -1L;
    }
}
