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

import java.io.PrintWriter;

/**
 * High-scoring segment pair (HSP) writer.
 */
public final class HspWriter {

    /**
     * Private no-arg constructor.
     */
    private HspWriter() {
        // empty
    }


    /**
     * Write the specified high-scoring segment pair with the specified print writer.
     *
     * @param hsp high-scoring segment pair to write, must not be null
     * @param writer print writer to write high-scoring segment pair with, must not be null
     */
    public static void write(final HighScoringPair hsp, final PrintWriter writer) {
        checkNotNull(hsp);
        checkNotNull(writer);
        writer.println(hsp.toString());
    }

    /**
     * Write zero or more high-scoring segment pairs with the specified print writer.
     *
     * @param hsps zero or more high-scoring segment pairs to write, must not be null
     * @param writer print writer to write high-scoring segment pairs with, must not be null
     */
    public static void write(final Iterable<HighScoringPair> hsps, final PrintWriter writer) {
        checkNotNull(hsps);
        checkNotNull(writer);
        for (HighScoringPair hsp : hsps) {
            writer.println(hsp.toString());
        }
    }
}
