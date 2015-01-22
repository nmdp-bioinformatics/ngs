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

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.alignment.AlignmentPair;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.SequenceIterator;

import org.biojava.bio.seq.io.SeqIOTools;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract unit test for implementations of PairwiseAlignment.
 */
@SuppressWarnings("deprecation")
public abstract class AbstractPairwiseAlignmentTest {
    private Sequence query;
    private Sequence subject;
    private List<Sequence> queries;
    private List<Sequence> subjects;
    private GapPenalties gapPenalties;
    protected PairwiseAlignment align;

    /**
     * Create and return a new instance of an implementation of PairwiseAlignment to test.
     *
     * @return a new instance of an implementation of PairwiseAlignment to test
     */
    protected abstract PairwiseAlignment createPairwiseAlignment();

    @Before
    public void setUp() throws Exception {
        query = read("query.fa");
        subject = read("subject.fa");
        queries = ImmutableList.of(query, query);
        subjects = ImmutableList.of(query, subject);
        gapPenalties = GapPenalties.create(0, 1, 3, 2, 4);

        align = createPairwiseAlignment();
    }

    @Test
    public void testCreatePairwiseAlignment() {
        assertNotNull(align);
    }


    @Test(expected=NullPointerException.class)
    public void testLocalNullQuery() {
        align.local(null, subject);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalNullSubject() {
        align.local(query, (Sequence) null);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalGapPenaltiesNullQuery() {
        align.local(null, subject, gapPenalties);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalGapPenaltiesNullSubject() {
        align.local(query, (Sequence) null, gapPenalties);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalNullGapPenalties() {
        align.local(query, subject, null);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalNullSubjects() {
        align.local(query, (List<Sequence>) null);
    }

    @Test(expected=NullPointerException.class)
    public void testLocalNullQueries() {
        align.local((List<Sequence>) null, subjects);
    }

    @Test
    public void testLocal() {
        for (AlignmentPair alignmentPair : align.local(query, subject)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testLocalGapPenalties() {
        for (AlignmentPair alignmentPair : align.local(query, subject, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testLocalSubjects() {
        for (AlignmentPair alignmentPair : align.local(query, subjects)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testLocalSubjectsGapPenalties() {
        for (AlignmentPair alignmentPair : align.local(query, subjects, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testLocalQueries() {
        for (AlignmentPair alignmentPair : align.local(queries, subjects)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testLocalQueriesGapPenalties() {
        for (AlignmentPair alignmentPair : align.local(queries, subjects, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalNullQuery() {
        align.global(null, subject);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalNullSubject() {
        align.global(query, (Sequence) null);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalGapPenaltiesNullQuery() {
        align.global(null, subject, gapPenalties);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalGapPenaltiesNullSubject() {
        align.global(query, (Sequence) null, gapPenalties);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalNullGapPenalties() {
        align.global(query, subject, null);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalNullSubjects() {
        align.global(query, (List<Sequence>) null);
    }

    @Test(expected=NullPointerException.class)
    public void testGlobalNullQueries() {
        align.global((List<Sequence>) null, subjects);
    }

    @Test
    public void testGlobal() {
        for (AlignmentPair alignmentPair : align.global(query, subject)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testGlobalGapPenalties() {
        for (AlignmentPair alignmentPair : align.global(query, subject, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testGlobalSubjects() {
        for (AlignmentPair alignmentPair : align.global(query, subjects)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testGlobalSubjectsGapPenalties() {
        for (AlignmentPair alignmentPair : align.global(query, subjects, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testGlobalQueries() {
        for (AlignmentPair alignmentPair : align.global(queries, subjects)) {
            assertNotNull(alignmentPair);
        }
    }

    @Test
    public void testGlobalQueriesGapPenalties() {
        for (AlignmentPair alignmentPair : align.global(queries, subjects, gapPenalties)) {
            assertNotNull(alignmentPair);
        }
    }

    private static Sequence read(final String name) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(AbstractPairwiseAlignmentTest.class.getResourceAsStream(name)))) {
            SequenceIterator sequences = SeqIOTools.readFastaDNA(reader);
            return sequences.hasNext() ? sequences.nextSequence() : null;
        }
    }
}
