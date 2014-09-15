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
package org.nmdp.ngs.reads.quality;

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.math3.distribution.RealDistribution;

import org.nmdp.ngs.reads.QualityStrategy;

/**
 * Quality strategy which samples from a single real distribution and multiplies
 * by a positional score function.
 */
public final class ScoreFunctionQualityStrategy implements QualityStrategy {
    /** Quality distribution. */
    private final RealDistribution distribution;

    /** Score function. */
    private final ScoreFunction scoreFunction;


    /**
     * Create a new score function quality strategy.
     *
     * @param distribution distribution, must not be null
     * @param scoreFunction score function, must not be null
     */
    public ScoreFunctionQualityStrategy(final RealDistribution distribution, final ScoreFunction scoreFunction) {
        checkNotNull(distribution);
        checkNotNull(scoreFunction);
        this.distribution = distribution;
        this.scoreFunction = scoreFunction;
    }


    @Override
    public double qualityScore(final int position, final int length) {
        return distribution.sample() * scoreFunction.evaluate((double) position / (double) length);
    }
}
