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
 * Substitution mutation strategy.
 */
public final class SubstitutionMutationStrategy implements MutationStrategy {
    /** Random. */
    private final RandomGenerator random;

    /** List of DNA symbols. */
    private static final List<AtomicSymbol> SYMBOLS = ImmutableList.of(DNATools.a(), DNATools.c(), DNATools.g(), DNATools.t());


    /**
     * Create a new substitution mutation strategy.
     *
     * @param random random, must not be null
     */
    public SubstitutionMutationStrategy(final RandomGenerator random) {
        checkNotNull(random);
        this.random = random;
    }


    @Override
    public SymbolList mutate(final Symbol symbol) {
        SimpleSymbolList symbolList = new SimpleSymbolList(DNATools.getDNA());
        int index = random.nextInt(SYMBOLS.size());
        try {
            symbolList.addSymbol(SYMBOLS.get(index));
        }
        catch (IllegalSymbolException e) {
            // ignore
        }
        return symbolList;
    }
}
