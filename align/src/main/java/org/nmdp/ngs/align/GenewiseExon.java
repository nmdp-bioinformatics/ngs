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

import com.google.common.collect.Range;

import javax.annotation.concurrent.Immutable;

/**
 * Genewise exon.
 */
@Immutable
public final class GenewiseExon {
    private final long start;
    private final long end;
    private final int phase;
    private final Range<Long> range;


    /**
     * Create a new genewise exon.
     *
     * @param start start, in 1-based coordinate system, fully closed range
     * @param end end, in 1-based coordinate system, fully closed range
     * @param phase phase
     */
    GenewiseExon(final long start, final long end, final int phase) {
        this.start = start;
        this.end = end;
        this.phase = phase;
        range = Range.closed(start, end);
    }


    /**
     * Return the start of this genewise exon in 1-based coordinate system, fully closed range.
     *
     * @return the start of this genewise exon in 1-based coordinate system, fully closed range
     */
    public long start() {
        return start;
    }

    /**
     * Return the start of this genewise exon in 1-based coordinate system, fully closed range.
     *
     * @return the start of this genewise exon in 1-based coordinate system, fully closed range
     */
    public long end() {
        return end;
    }

    /**
     * Return the phase of this genewise exon.
     *
     * @return the phase of this genewise exon
     */
    public int phase() {
        return phase;
    }

    /**
     * Return this genewise exon as a 1-based coordinate system, fully closed range.
     *
     * @return this genewise exon as a 1-based coordinate system, fully closed range
     */
    public Range<Long> asRange() {
        return range;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Exon ");
        sb.append(start);
        sb.append(" ");
        sb.append(end);
        sb.append(" phase ");
        sb.append(phase);
        sb.append(" range ");
        sb.append(range);
        return sb.toString();
    }
}
