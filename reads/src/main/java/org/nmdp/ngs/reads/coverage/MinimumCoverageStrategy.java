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

import static com.google.common.base.Preconditions.checkArgument;

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.symbol.IntegerAlphabet;
import org.biojava.bio.symbol.SymbolList;

/**
 * Minimum coverage strategy.
 */
public final class MinimumCoverageStrategy extends AbstractCoverageStrategy {
    /** Minimum coverage. */
    private final int minimumCoverage;


    /**
     * Create a new minimum coverage strategy with the specified minimum coverage.
     *
     * @param minimumCoverage minimum coverage, must be at least 0
     */
    public MinimumCoverageStrategy(final int minimumCoverage) {
        checkArgument(minimumCoverage > 0, "minimumCoverage must be at least 0");
        this.minimumCoverage = minimumCoverage;
    }


    @Override
    protected boolean evaluate(final Sequence reference, final SymbolList coverage) {
        int min = Integer.MAX_VALUE;
        for (int i = 1, size = coverage.length() + 1; i < size; i++) {
            int c = ((IntegerAlphabet.IntegerSymbol) coverage.symbolAt(i)).intValue();
            if (c < min) {
                min = c;
            }
        }
        return min >= minimumCoverage;
    }
}
