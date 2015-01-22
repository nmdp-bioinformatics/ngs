/*

    ngs-reads  Next generation sequencing (NGS/HTS) reads.
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
package org.nmdp.ngs.reads.mutation;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.math3.random.RandomGenerator;

import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.reads.MutationStrategy;

/**
 * Indel mutation strategy.
 */
public final class IndelMutationStrategy implements MutationStrategy {
    /** Random. */
    private final RandomGenerator random;

    /** Insertion mutation strategy. */
    private final InsertionMutationStrategy insertion;

    /** Insertion rate. */
    private final double insertionRate;

    /** Deletion mutation strategy. */
    private final DeletionMutationStrategy deletion;


    /**
     * Create a new indel mutation strategy with the specified parameters.
     *
     * @param random random, must not be null
     * @param insertion insertion mutation strategy, must not be null
     * @param insertionRate insertion rate, insertionRate and deletionRate must sum to 1.0d
     * @param deletion deletion mutation strategy, must not be null
     * @param deletionRate deletion rate, insertionRate and deletionRate must sum to 1.0d
     */
    public IndelMutationStrategy(final RandomGenerator random, final InsertionMutationStrategy insertion, final double insertionRate, final DeletionMutationStrategy deletion, final double deletionRate) {
        checkNotNull(random);
        checkNotNull(insertion);
        checkNotNull(deletion);
        checkArgument(1.0d - insertionRate - deletionRate < 0.01d, "insertionRate and deletionRate must sum to 1.0d");
        this.random = random;
        this.insertion = insertion;
        this.insertionRate = insertionRate;
        this.deletion = deletion;
    }


    @Override
    public SymbolList mutate(final Symbol symbol) {
        return random.nextDouble() < insertionRate ? insertion.mutate(symbol) : deletion.mutate(symbol);
    }
}
