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
package org.nmdp.ngs.reads.coverage;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.IntegerAlphabet;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.reads.CoverageStrategy;

/**
 * Abstract coverage strategy.
 */
abstract class AbstractCoverageStrategy implements CoverageStrategy {
    /** Coverages. */
    private final LoadingCache<Sequence, SimpleSymbolList> coverages = CacheBuilder.newBuilder()
        .build(new CacheLoader<Sequence, SimpleSymbolList>()
               {
                   @Override
                   public SimpleSymbolList load(final Sequence reference) {
                       SimpleSymbolList coverage = new SimpleSymbolList(IntegerAlphabet.INSTANCE);
                       for (int i = 1, size = reference.length() + 1; i < size; i++) {
                           try {
                               coverage.addSymbol(IntegerAlphabet.INSTANCE.getSymbol(0));
                           }
                           catch (IllegalSymbolException e) {
                               // ignore
                           }
                       }
                       return coverage;
                   }
               });


    /**
     * Create a new abstract coverage strategy.
     */
    protected AbstractCoverageStrategy() {
        // empty
    }


    /**
     * Return true if the specified reference sequence has enough coverage given
     * the specified coverage scores.
     *
     * @param reference reference sequence to evaluate
     * @param coverage coverage scores
     * @return true if the specified reference sequence has enough coverage given
     *    the specified coverage scores
     */
    protected abstract boolean evaluate(final Sequence reference, final SymbolList coverage);

    @Override
    public final boolean evaluate(final Sequence reference) {
        return evaluate(reference, coverages.getUnchecked(reference));
    }

    @Override
    public final void add(final Sequence reference, final int start, final int end) {
        SymbolList coverage = coverages.getUnchecked(reference);

        for (int i = start; i < end; i++) {
            int c = ((IntegerAlphabet.IntegerSymbol) coverage.symbolAt(i)).intValue();
            try {
                coverage.edit(new Edit(i, IntegerAlphabet.INSTANCE, IntegerAlphabet.INSTANCE.getSymbol(c + 1)));
            }
            catch (IllegalAlphabetException e) {
                // ignore
            }
            catch (IllegalSymbolException e) {
                // ignore
            }
        }
    }
}
