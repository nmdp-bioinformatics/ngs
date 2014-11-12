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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.nmdp.ngs.align.Alignments.count;
import static org.nmdp.ngs.align.Alignments.ends;
import static org.nmdp.ngs.align.Alignments.gaps;
import static org.nmdp.ngs.align.Alignments.isGapSymbol;
import static org.nmdp.ngs.align.Alignments.isMatchSymbol;
import static org.nmdp.ngs.align.Alignments.isMismatchSymbol;
import static org.nmdp.ngs.align.Alignments.length;
import static org.nmdp.ngs.align.Alignments.lengths;
import static org.nmdp.ngs.align.Alignments.matches;
import static org.nmdp.ngs.align.Alignments.maximumLength;
import static org.nmdp.ngs.align.Alignments.mismatches;
import static org.nmdp.ngs.align.Alignments.starts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import org.biojava.bio.alignment.AlignmentPair;
import org.biojava.bio.alignment.SubstitutionMatrix;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.GappedSequence;

import org.biojava.bio.symbol.AlphabetManager;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Alignments.
 */
public final class AlignmentsTest {
    private List<Range<Long>> ranges;
    private RangeSet<Long> rangeSet;
    private GappedSequence subject;
    private GappedSequence query;
    private SubstitutionMatrix substitutionMatrix;
    private AlignmentPair alignmentPair;

    @Before
    public void setUp() throws Exception {
        ranges = new ArrayList<Range<Long>>();
        ranges.add(Range.closedOpen(0L, 0L));
        ranges.add(Range.closedOpen(0L, 1L));
        ranges.add(Range.closedOpen(0L, 43L));
        ranges.add(Range.closedOpen(1L, 43L));

        rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closedOpen(0L, 0L));
        rangeSet.add(Range.closedOpen(0L, 1L));
        rangeSet.add(Range.closedOpen(0L, 43L));
        rangeSet.add(Range.closedOpen(1L, 43L));

        subject = DNATools.createGappedDNASequence("aaaattttaaaattttaaaa", "subject");
        query = DNATools.createGappedDNASequence("aaaaccccaaaa----aaaa", "query");
        substitutionMatrix = SubstitutionMatrix.getNuc4_4();
        alignmentPair = new AlignmentPair(query, subject, substitutionMatrix);
    }

    @Test(expected=NullPointerException.class)
    public void testLengthNullRange() {
        length((Range<Long>) null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthOpenRange() {
        length(Range.open(2L, 4L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthClosedRange() {
        length(Range.closed(2L, 4L));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthSingletonRange() {
        length(Range.singleton(2L));
    }

    @Test
    public void testLengthZero() {
        assertEquals(0L, length(Range.closedOpen(0L, 0L)));
    }

    @Test
    public void testLengthOne() {
        assertEquals(1L, length(Range.closedOpen(0L, 1L)));
    }

    @Test
    public void testLengthFromZero() {
        assertEquals(43L, length(Range.closedOpen(0L, 43L)));
    }

    @Test
    public void testLengthFromOne() {
        assertEquals(42L, length(Range.closedOpen(1L, 43L)));
    }

    @Test(expected=NullPointerException.class)
    public void testMaximumLengthNull() {
        maximumLength(null);
    }

    @Test(expected=NullPointerException.class)
    public void testMaximumLengthNullRange() {
        maximumLength(Collections.singleton((Range<Long>) null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaximumLengthOpenRange() {
        maximumLength(Collections.singleton(Range.open(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaximumLengthClosedRange() {
        maximumLength(Collections.singleton(Range.closed(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testMaximumLengthSingletonRange() {
        maximumLength(Collections.singleton(Range.singleton(2L)));
    }

    @Test
    public void testMaximumLengthEmpty() {
        assertEquals(-1L, maximumLength(Collections.<Range<Long>>emptySet()));
    }

    @Test
    public void testMaximumLengthSingletonZero() {
        assertEquals(0L, maximumLength(Collections.singleton(Range.closedOpen(0L, 0L))));
    }

    @Test
    public void testMaximumLengthSingletonOne() {
        assertEquals(1L, maximumLength(Collections.singleton(Range.closedOpen(0L, 1L))));
    }

    @Test
    public void testMaximumLengthSingletonFromZero() {
        assertEquals(43L, maximumLength(Collections.singleton(Range.closedOpen(0L, 43L))));
    }

    @Test
    public void testMaximumLengthSingletonFromOne() {
        assertEquals(42L, maximumLength(Collections.singleton(Range.closedOpen(1L, 43L))));
    }

    @Test
    public void testMaximumLength() {
        assertEquals(43L, maximumLength(ranges));
    }

    @Test
    public void testMaximumLengthsRangeSet() {
        assertEquals(43L, maximumLength(rangeSet.asRanges()));
    }

    @Test(expected=NullPointerException.class)
    public void testLengthNullRanges() {
        length((Iterable<Range<Long>>) null);
    }

    @Test(expected=NullPointerException.class)
    public void testLengthRangesNullRange() {
        length(Collections.singleton((Range<Long>) null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthRangesOpenRange() {
        length(Collections.singleton(Range.open(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthRangesClosedRange() {
        length(Collections.singleton(Range.closed(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLengthRangesSingletonRange() {
        length(Collections.singleton(Range.singleton(2L)));
    }

    @Test
    public void testLengthEmptyRanges() {
        assertEquals(0L, length(Collections.<Range<Long>>emptySet()));
    }

    @Test
    public void testLengthSingletonZeroRanges() {
        assertEquals(0L, length(Collections.singleton(Range.closedOpen(0L, 0L))));
    }

    @Test
    public void testLengthSingletonOneRanges() {
        assertEquals(1L, length(Collections.singleton(Range.closedOpen(0L, 1L))));
    }

    @Test
    public void testLengthSingletonFromZeroRanges() {
        assertEquals(43L, length(Collections.singleton(Range.closedOpen(0L, 43L))));
    }

    @Test
    public void testLengthSingletonFromOneRanges() {
        assertEquals(42L, length(Collections.singleton(Range.closedOpen(1L, 43L))));
    }

    @Test
    public void testLengthRanges() {
        assertEquals(43L, length(ranges));
    }

    @Test
    public void testLengthRangeSet() {
        assertEquals(43L, length(rangeSet.asRanges()));
    }


    @Test(expected=NullPointerException.class)
    public void testLengthsNull() {
        lengths(null);
    }

    @Test
    public void testLengthsEmpty() {
        lengths(Collections.<Range<Long>>emptySet());
    }

    @Test
    public void testLengths() {
        List<Long> lengths = lengths(ranges);
        assertEquals(4, lengths.size());
        assertEquals(Long.valueOf(0L), lengths.get(0));
        assertEquals(Long.valueOf(1L), lengths.get(1));
        assertEquals(Long.valueOf(43L), lengths.get(2));
        assertEquals(Long.valueOf(42L), lengths.get(3));
    }

    @Test
    public void testLengthsRangeSet() {
        List<Long> lengths = lengths(rangeSet.asRanges());
        // connected ranges are coalesced in a RangeSet
        assertEquals(1, lengths.size());
        assertEquals(Long.valueOf(43L), lengths.get(0));
    }

    @Test(expected=NullPointerException.class)
    public void testCountNull() {
        count(null);
    }

    @Test
    public void testCountEmpty() {
        assertEquals(0, count(Collections.<Range<Long>>emptySet()));
    }

    @Test(expected=NullPointerException.class)
    public void testCountNullRange() {
        count(Collections.singleton((Range<Long>) null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCountOpen() {
        count(Collections.singleton(Range.open(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCountClosed() {
        count(Collections.singleton(Range.closed(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testCountSingleton() {
        count(Collections.singleton(Range.singleton(2L)));
    }

    @Test
    public void testCountOne() {
        assertEquals(1, count(Collections.singleton(Range.closedOpen(0L, 1L))));
    }

    @Test
    public void testCount() {
        assertEquals(4, count(ranges));
    }

    @Test
    public void testCountRangeSet() {
        assertEquals(1, count(rangeSet.asRanges()));
    }

    @Test(expected=NullPointerException.class)
    public void testStartsNull() {
        starts(null);
    }

    @Test
    public void testStartsEmpty() {
        assertEquals(Collections.emptyList(), starts(Collections.<Range<Long>>emptySet()));
    }

    @Test(expected=NullPointerException.class)
    public void testStartsNullRange() {
        starts(Collections.singleton((Range<Long>) null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testStartsOpen() {
        starts(Collections.singleton(Range.open(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testStartsClosed() {
        starts(Collections.singleton(Range.closed(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testStartsSingleton() {
        starts(Collections.singleton(Range.singleton(2L)));
    }

    @Test
    public void testStarts() {
        List<Long> starts = starts(ranges);
        assertEquals(4, starts.size());
        assertEquals(Long.valueOf(0L), starts.get(0));
        assertEquals(Long.valueOf(0L), starts.get(1));
        assertEquals(Long.valueOf(0L), starts.get(2));
        assertEquals(Long.valueOf(1L), starts.get(3));
    }

    @Test
    public void testStartsRangeSet() {
        List<Long> starts = starts(rangeSet.asRanges());
        assertEquals(1, starts.size());
        assertEquals(Long.valueOf(0L), starts.get(0));
    }

    @Test(expected=NullPointerException.class)
    public void testEndsNull() {
        ends(null);
    }

    @Test
    public void testEndsEmpty() {
        assertEquals(Collections.emptyList(), ends(Collections.<Range<Long>>emptySet()));
    }

    @Test(expected=NullPointerException.class)
    public void testEndsNullRange() {
        ends(Collections.singleton((Range<Long>) null));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndsOpen() {
        ends(Collections.singleton(Range.open(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndsClosed() {
        ends(Collections.singleton(Range.closed(2L, 4L)));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEndsSingleton() {
        ends(Collections.singleton(Range.singleton(2L)));
    }

    @Test
    public void testEnds() {
        List<Long> ends = ends(ranges);
        assertEquals(4, ends.size());
        assertEquals(Long.valueOf(0L), ends.get(0));
        assertEquals(Long.valueOf(1L), ends.get(1));
        assertEquals(Long.valueOf(43L), ends.get(2));
        assertEquals(Long.valueOf(43L), ends.get(3));
    }

    @Test
    public void testEndsRangeSet() {
        List<Long> ends = ends(rangeSet.asRanges());
        assertEquals(1, ends.size());
        assertEquals(Long.valueOf(43L), ends.get(0));
    }

    @Test
    public void testIsGapSymbolNull() {
        assertFalse(isGapSymbol(null));
    }

    @Test
    public void testIsGapSymbol() {
        assertFalse(isGapSymbol(DNATools.a()));
        assertFalse(isGapSymbol(DNATools.c()));
        assertFalse(isGapSymbol(DNATools.t()));
        assertFalse(isGapSymbol(DNATools.g()));
        assertFalse(isGapSymbol(DNATools.n()));
        assertTrue(isGapSymbol(AlphabetManager.getGapSymbol()));
        assertTrue(isGapSymbol(DNATools.getDNA().getGapSymbol()));
    }

    @Test
    public void testIsMatchSymbolNull() {
        assertFalse(isMatchSymbol(null));
    }

    @Test
    public void testIsMatchSymbolGapSymbol() {
        assertFalse(isMatchSymbol(DNATools.getDNA().getGapSymbol()));
    }

    @Test
    public void testIsMatchSymbol() {
        // match
        assertTrue(isMatchSymbol(alignmentPair.symbolAt(1)));
        // mismatch
        assertFalse(isMatchSymbol(alignmentPair.symbolAt(5)));
        // gap
        assertFalse(isMatchSymbol(alignmentPair.symbolAt(13)));
    }

    @Test
    public void testIsMismatchSymbolNull() {
        assertFalse(isMismatchSymbol(null));
    }

    @Test
    public void testIsMismatchSymbolGapSymbol() {
        assertFalse(isMismatchSymbol(DNATools.getDNA().getGapSymbol()));
    }

    @Test
    public void testIsMismatchSymbol() {
        // match
        assertFalse(isMismatchSymbol(alignmentPair.symbolAt(1)));
        // mismatch
        assertTrue(isMismatchSymbol(alignmentPair.symbolAt(5)));
        // gap
        assertFalse(isMismatchSymbol(alignmentPair.symbolAt(13)));
    }

    @Test(expected=NullPointerException.class)
    public void testGapsNull() {
        gaps(null);
    }

    @Test
    public void testGapsNoGaps() {
        assertEquals(Collections.<Range<Long>>emptyList(), gaps(subject));
    }

    @Test
    public void testGapsAllGaps() throws Exception {
        GappedSequence allGaps = DNATools.createGappedDNASequence("--------------------", "all gaps");
        List<Range<Long>> gaps = gaps(allGaps);
        assertEquals(1, gaps.size());
        assertEquals(Range.closedOpen(0L, 20L), gaps.get(0));
    }

    @Test
    public void testGaps() {
        List<Range<Long>> gaps = gaps(query);
        assertEquals(1, gaps.size());
        assertEquals(Range.closedOpen(12L, 16L), gaps.get(0));
    }

    @Test(expected=NullPointerException.class)
    public void testMatchesNull() {
        matches(null);
    }

    @Test
    public void testMatchesNoMatches() throws Exception {
        GappedSequence mismatch = DNATools.createGappedDNASequence("gggggggggggggggggggg", "mismatch");
        AlignmentPair allMismatches = new AlignmentPair(mismatch, subject, substitutionMatrix);
        assertEquals(Collections.<Range<Long>>emptyList(), matches(allMismatches));
    }

    @Test
    public void testMatchesAllMatches() throws Exception {
        AlignmentPair allMatches = new AlignmentPair(subject, subject, substitutionMatrix);
        List<Range<Long>> matches = matches(allMatches);
        assertEquals(1, matches.size());
        assertEquals(Range.closedOpen(0L, 20L), matches.get(0));
    }

    @Test
    public void testMatches() throws Exception {
        List<Range<Long>> matches = matches(alignmentPair);
        assertEquals(3, matches.size());
        assertEquals(Range.closedOpen(0L, 4L), matches.get(0));
        assertEquals(Range.closedOpen(8L, 12L), matches.get(1));
        assertEquals(Range.closedOpen(16L, 20L), matches.get(2));
    }

    @Test(expected=NullPointerException.class)
    public void testMismatchesNull() {
        mismatches(null);
    }

    @Test
    public void testMismatchesNoMismatches() throws Exception {
        AlignmentPair allMatches = new AlignmentPair(subject, subject, substitutionMatrix);
        assertEquals(Collections.<Range<Long>>emptyList(), mismatches(allMatches));
    }

    @Test
    public void testMismatchesAllMismatches() throws Exception {
        GappedSequence mismatch = DNATools.createGappedDNASequence("gggggggggggggggggggg", "mismatch");
        AlignmentPair allMismatches = new AlignmentPair(mismatch, subject, substitutionMatrix);
        List<Range<Long>> mismatches = mismatches(allMismatches);
        assertEquals(1, mismatches.size());
        assertEquals(Range.closedOpen(0L, 20L), mismatches.get(0));
    }

    @Test
    public void testMismatches() {
        List<Range<Long>> mismatches = mismatches(alignmentPair);
        assertEquals(1, mismatches.size());
        assertEquals(Range.closedOpen(4L, 8L), mismatches.get(0));
    }
}
