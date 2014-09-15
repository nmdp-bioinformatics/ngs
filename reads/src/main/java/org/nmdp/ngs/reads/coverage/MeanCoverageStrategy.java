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
package org.nmdp.ngs.reads.coverage;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.symbol.IntegerAlphabet;
import org.biojava.bio.symbol.SymbolList;

/**
 * Mean coverage strategy.
 */
public final class MeanCoverageStrategy extends AbstractCoverageStrategy {
    /** Mean coverage. */
    private final double meanCoverage;


    /**
     * Create a new mean coverage strategy with the specified mean coverage.
     *
     * @param meanCoverage mean coverage
     */
    public MeanCoverageStrategy(final double meanCoverage) {
        super();
        this.meanCoverage = meanCoverage;
    }


    @Override
    protected boolean evaluate(final Sequence reference, final SymbolList coverage) {
        SummaryStatistics summaryStatistics = new SummaryStatistics();
        for (int i = 1, size = coverage.length() + 1; i < size; i++) {
            int c = ((IntegerAlphabet.IntegerSymbol) coverage.symbolAt(i)).intValue();
            summaryStatistics.addValue((double) c);
        }
        return summaryStatistics.getMean() >= meanCoverage;
    }
}
