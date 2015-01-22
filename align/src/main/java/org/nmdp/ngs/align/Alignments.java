/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BoundType;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import org.biojava.bio.alignment.AlignmentPair;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.AlphabetManager;
import org.biojava.bio.symbol.BasisSymbol;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.GappedSymbolList;

/**
 * Static utility methods on alignments.
 */
public final class Alignments {

    /**
     * Private no-arg constructor.
     */
    private Alignments() {
        // empty
    }


    // all ranges here are 0-based [closed, open)

    /**
     * Confirm that the specified range is [closed, open).
     *
     * @param range range to check, must not be null
     */
    static void checkClosedOpen(final Range<Long> range) {
        checkNotNull(range);
        checkArgument(BoundType.CLOSED == range.lowerBoundType(), "range must be [closed, open), lower bound type was open");
        checkArgument(BoundType.OPEN == range.upperBoundType(), "range must be [closed, open), upper bound type was closed");
    }

    /**
     * Return the length of the specified range.
     *
     * @param range range, must not be null and must be [closed, open)
     * @return the length of the specified range
     */
    public static long length(final Range<Long> range) {
        checkClosedOpen(range);
        return Math.max(0L, range.upperEndpoint() - range.lowerEndpoint());
    }

    /**
     * Return the lengths of the specified ranges.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the lengths of the specified ranges
     */
    public static List<Long> lengths(final Iterable<Range<Long>> ranges) {
        checkNotNull(ranges);
        List<Long> lengths = new ArrayList<Long>();
        for (Range<Long> range : ranges) {
            lengths.add(length(range));
        }
        return lengths;
    }

    /**
     * Return the sum of lengths of the specified ranges, after merging overlapping ranges.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the sum of lengths of the specified ranges, after merging overlapping ranges
     */
    public static long length(final Iterable<Range<Long>> ranges) {
        checkNotNull(ranges);
        RangeSet<Long> rangeSet = TreeRangeSet.create();
        for (Range<Long> range : ranges) {
            rangeSet.add(range);
        }
        long length = 0L;
        for (Range<Long> range : rangeSet.asRanges()) {
            length += length(range);
        }
        return length;
    }

    /**
     * Return the maximum length in the specified ranges, or <code>-1</code> if ranges is empty.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the maximum length in the specified ranges, or <code>-1</code> if ranges is empty
     */
    public static long maximumLength(final Iterable<Range<Long>> ranges) {
        checkNotNull(ranges);
        if (Iterables.isEmpty(ranges)) {
            return -1L;
        }
        return Ordering.natural().max(lengths(ranges)).longValue();
    }

    /**
     * Return the count of the specified ranges.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the count of the specified ranges
     */
    public static int count(final Iterable<Range<Long>> ranges) {
        int count = 0;
        for (Range<Long> range : ranges) {
            checkClosedOpen(range);
            count++;
        }
        return count;
    }

    /**
     * Return the start/lower endpoints in the specified ranges.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the start/lower endpoints in the specified ranges
     */
    public static List<Long> starts(final Iterable<Range<Long>> ranges) {
        List<Long> starts = new ArrayList<Long>();
        for (Range<Long> range : ranges) {
            checkClosedOpen(range);
            starts.add(range.lowerEndpoint());
        }
        return starts;
    }

    /**
     * Return the end/upper endpoints in the specified ranges.
     *
     * @param ranges ranges, must not be null, must not contain any null ranges, and all ranges must be [closed, open)
     * @return the end/upper endpoints in the specified ranges
     */
    public static List<Long> ends(final Iterable<Range<Long>> ranges) {
        List<Long> ends = new ArrayList<Long>();
        for (Range<Long> range : ranges) {
            checkClosedOpen(range);
            ends.add(range.upperEndpoint());
        }
        return ends;
    }

    /**
     * Return true if the specified symbol is a gap symbol.
     *
     * @param symbol symbol
     * @return true if the specified symbol is a gap symbol
     */
    static boolean isGapSymbol(final Symbol symbol) {
        return AlphabetManager.getGapSymbol().equals(symbol)
            || DNATools.getDNA().getGapSymbol().equals(symbol);
    }

    /**
     * Return true if the specified symbol represents an alignment match.
     *
     * @param symbol symbol
     * @return true if the specified symbol represents an alignment match
     */
    static boolean isMatchSymbol(final Symbol symbol) {
        if (!(symbol instanceof BasisSymbol)) {
            return false;
        }
        BasisSymbol basisSymbol = (BasisSymbol) symbol;
        Set<Symbol> uniqueSymbols = new HashSet<Symbol>();
        for (Object o : basisSymbol.getSymbols()) {
            Symbol s = (Symbol) o;
            if (isGapSymbol(s)) {
                return false;
            }
            uniqueSymbols.add((Symbol) o);
        }
        return (uniqueSymbols.size() == 1);
    }

    /**
     * Return true if the specified symbol represents an alignment mismatch.
     *
     * @param symbol symbol
     * @return true if the specified symbol represents an alignment mismatch
     */
    static boolean isMismatchSymbol(final Symbol symbol) {
        if (!(symbol instanceof BasisSymbol)) {
            return false;
        }
        BasisSymbol basisSymbol = (BasisSymbol) symbol;
        Set<Symbol> uniqueSymbols = new HashSet<Symbol>();
        for (Object o : basisSymbol.getSymbols()) {
            Symbol s = (Symbol) o;
            if (isGapSymbol(s)) {
                return false;
            }
            uniqueSymbols.add((Symbol) o);
        }
        return (uniqueSymbols.size() > 1);
    }

    /**
     * Return the gaps in the specified gapped symbol list as 0-based [closed, open) ranges.
     *
     * @param gappedSymbols gapped symbol list, must not be null
     * @return the gaps in the specified gapped symbol list as 0-based [closed, open) ranges
     */
    public static List<Range<Long>> gaps(final GappedSymbolList gappedSymbols) {
        checkNotNull(gappedSymbols);
        List<Range<Long>> gaps = new ArrayList<Range<Long>>();
        int gapStart = -1;
        for (int i = 1, length = gappedSymbols.length() + 1; i < length; i++) {
            if (isGapSymbol(gappedSymbols.symbolAt(i))) {
                if (gapStart < 0) {
                    gapStart = i;
                }
            }
            else {
                if (gapStart > 0) {
                    // biojava coordinates are 1-based
                    gaps.add(Range.closedOpen(Long.valueOf(gapStart - 1L), Long.valueOf(i - 1L)));
                    gapStart = -1;
                }
            }
        }
        if (gapStart > 0) {
            gaps.add(Range.closedOpen(Long.valueOf(gapStart - 1L), Long.valueOf(gappedSymbols.length())));
        }
        return gaps;
    }

    /**
     * Return the alignment matches in the specified alignment pair as 0-based [closed, open) ranges.
     *
     * @param alignmentPair alignment pair, must not be null
     * @return the alignment matches in the alignment pair as 0-based [closed, open) ranges
     */
    public static List<Range<Long>> matches(final AlignmentPair alignmentPair) {
        checkNotNull(alignmentPair);
        List<Range<Long>> matches = new ArrayList<Range<Long>>();
        int matchStart = -1;
        for (int i = 1, length = alignmentPair.length() + 1; i < length; i++) {
            if (isMatchSymbol(alignmentPair.symbolAt(i))) {
                if (matchStart < 0) {
                    matchStart = i;
                }
            }
            else {
                if (matchStart > 0) {
                    // biojava coordinates are 1-based
                    matches.add(Range.closedOpen(Long.valueOf(matchStart - 1L), Long.valueOf(i - 1L)));
                    matchStart = -1;
                }
            }
        }
        if (matchStart > 0) {
            matches.add(Range.closedOpen(Long.valueOf(matchStart - 1L), Long.valueOf(alignmentPair.length())));
        }
        return matches;
    }

    /**
     * Return the alignment mismatches in the specified alignment pair as 0-based [closed, open) ranges.
     *
     * @param alignmentPair alignment pair, must not be null
     * @return the alignment mismatches in the alignment pair as 0-based [closed, open) ranges
     */
    public static List<Range<Long>> mismatches(final AlignmentPair alignmentPair) {
        checkNotNull(alignmentPair);
        List<Range<Long>> mismatches = new ArrayList<Range<Long>>();
        int mismatchStart = -1;
        for (int i = 1, length = alignmentPair.length() + 1; i < length; i++) {
            if (isMismatchSymbol(alignmentPair.symbolAt(i))) {
                if (mismatchStart < 0) {
                    mismatchStart = i;
                }
            }
            else {
                if (mismatchStart > 0) {
                    // biojava coordinates are 1-based
                    mismatches.add(Range.closedOpen(Long.valueOf(mismatchStart - 1L), Long.valueOf(i - 1L)));
                    mismatchStart = -1;
                }
            }
        }
        if (mismatchStart > 0) {
            mismatches.add(Range.closedOpen(Long.valueOf(mismatchStart - 1L), Long.valueOf(alignmentPair.length())));
        }
        return mismatches;
    }
}
