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

/**
 * Score functions.
 */
public final class ScoreFunctions {

    /**
     * Private no-arg constructor.
     */
    private ScoreFunctions() {
        // empty
    }


    /**
     * Illumina positional score function.
     *
     * @return the Illumina positional score function
     */
    public static ScoreFunction illumina() {
        return new ScoreFunction() {
            @Override
            public double evaluate(final double relativePosition) {
                // TODO: this could use improvement; perhaps re-use quality profiles from ART
                if (relativePosition < 0.05d) {
                    return 14400.0d * (relativePosition * relativePosition);
                }
                else if (relativePosition < 0.8d) {
                    return 36.0d;
                }
                else {
                    return 22600.0d * Math.pow(relativePosition - 1.0d, 4.0d);
                }
            }
        };
    }
}
