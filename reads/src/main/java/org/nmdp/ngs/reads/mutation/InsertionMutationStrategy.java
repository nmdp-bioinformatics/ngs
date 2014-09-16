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

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.apache.commons.math3.random.RandomGenerator;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.AtomicSymbol;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.reads.MutationStrategy;

/**
 * Insertion mutation strategy.
 */
public final class InsertionMutationStrategy implements MutationStrategy {
    /** Random. */
    private final RandomGenerator random;

    /** Insertion rate. */
    // TODO:  this needs a new name
    private final double insertionRate;

    /** Maximum insertion length. */
    private final int maximumInsertionLength;

    /** List of DNA symbols. */
    private static final List<AtomicSymbol> SYMBOLS = ImmutableList.of(DNATools.a(), DNATools.c(), DNATools.g(), DNATools.t());


    /**
     * Create a new insertion mutation strategy with the specified parameters.
     *
     * @param random random, must not be null
     * @param insertionRate insertion rate
     * @param maximumInsertionLength maximum insertion length, must be at least zero
     */
    public InsertionMutationStrategy(final RandomGenerator random, final double insertionRate, final int maximumInsertionLength) {
        checkNotNull(random);
        checkArgument(maximumInsertionLength >= 0, "maximumInsertionLength must be at least zero");
        this.random = random;
        this.insertionRate = insertionRate;
        this.maximumInsertionLength = maximumInsertionLength;
    }


    @Override
    public SymbolList mutate(final Symbol symbol) {
        SimpleSymbolList symbolList = new SimpleSymbolList(DNATools.getDNA());
        try {
            symbolList.addSymbol(symbol);
        }
        catch (IllegalSymbolException e) {
            // ignore
        }
        while (symbolList.length() < maximumInsertionLength) {
            if (random.nextDouble() < insertionRate) {
                break;
            }
            int index = random.nextInt(SYMBOLS.size());
            try {
                symbolList.addSymbol(SYMBOLS.get(index));
            }
            catch (IllegalSymbolException e) {
                // ignore
            }
        }
        return symbolList;
    }
}
