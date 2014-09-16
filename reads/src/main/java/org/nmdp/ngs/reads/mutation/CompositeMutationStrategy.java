/*

    ngs-reads  Next generation sequencing (NGS/HTS) reads.
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
package org.nmdp.ngs.reads.mutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.math3.random.RandomGenerator;

import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.reads.MutationStrategy;

/**
 * Composite mutation strategy.
 */
public final class CompositeMutationStrategy implements MutationStrategy {
    /** Random. */
    private final RandomGenerator random;

    /** Substitution mutation strategy. */
    private final SubstitutionMutationStrategy substitution;

    /** Substitution rate. */
    private final double substitutionRate;

    /** Indel mutation strategy. */
    private final IndelMutationStrategy indel;

    /** Indel rate. */
    private final double indelRate;

    /** Ambiguous substitution mutation strategy. */
    private final AmbiguousSubstitutionMutationStrategy ambiguous;


    /**
     * Create a new composite mutation strategy with the specified parameters.
     *
     * @param random random, must not be null
     * @param substitution substitution mutation strategy, must not be null
     * @param substitutionRate substitution rate, substitutionRate, indelRate, and ambiguousRate must sum to 1.0d
     * @param indel indel mutation strategy, must not be null
     * @param indelRate indel rate, substitutionRate, indelRate, and ambiguousRate must sum to 1.0d
     * @param ambiguous ambiguous mutation strategy, must not be null
     * @param ambiguousRate ambiguous rate, substitutionRate, indelRate, and ambiguousRate must sum to 1.0d
     */
    public CompositeMutationStrategy(final RandomGenerator random,
                                     final SubstitutionMutationStrategy substitution,
                                     final double substitutionRate,
                                     final IndelMutationStrategy indel,
                                     final double indelRate,
                                     final AmbiguousSubstitutionMutationStrategy ambiguous,
                                     final double ambiguousRate) {
        checkNotNull(random);
        checkNotNull(substitution);
        checkNotNull(indel);
        checkNotNull(ambiguous);
        checkArgument(1.0d - substitutionRate - indelRate - ambiguousRate < 0.01d, "substitutionRate, indelRate, and ambiguousRate must sum to 1.0d");
        this.random = random;
        this.substitution = substitution;
        this.substitutionRate = substitutionRate;
        this.indel = indel;
        this.indelRate = indelRate;
        this.ambiguous = ambiguous;
    }


    @Override
    public SymbolList mutate(final Symbol symbol) {
        double d = random.nextDouble();
        if (d < substitutionRate) {
            return substitution.mutate(symbol);
        }
        if (d < (substitutionRate + indelRate)) {
            return indel.mutate(symbol);
        }
        return ambiguous.mutate(symbol);
    }
}
