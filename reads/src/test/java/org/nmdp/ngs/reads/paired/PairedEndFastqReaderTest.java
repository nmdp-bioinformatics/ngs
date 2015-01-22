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
package org.nmdp.ngs.reads.paired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.isLeft;
import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.isRight;
import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.prefix;
import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.readPaired;
import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.streamInterleaved;
import static org.nmdp.ngs.reads.paired.PairedEndFastqReader.streamPaired;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import com.google.common.collect.ImmutableList;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.SangerFastqWriter;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for PairedEndFastqReader.
 */
public final class PairedEndFastqReaderTest {
    private Fastq left;
    private Fastq right;
    private Fastq invalidPrefix;
    private Fastq mismatchPrefix;
    private Reader firstReader;
    private Reader secondReader;
    private Reader reader;
    private PairedEndListener listener;

    @Before
    public void setUp() throws Exception {
        left = Fastq.builder().withDescription("prefix 1").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();
        right = Fastq.builder().withDescription("prefix 2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();

        invalidPrefix = Fastq.builder().withDescription("no-space").withSequence("aaaaa").withQuality("44444").build();
        mismatchPrefix = Fastq.builder().withDescription("mismatch 2").withSequence("aaaaatttttcccccggggg").withQuality("44444222224444422222").build();

        ByteArrayOutputStream first = new ByteArrayOutputStream();
        new SangerFastqWriter().write(first, left);
        firstReader = new StringReader(first.toString());

        ByteArrayOutputStream second = new ByteArrayOutputStream();
        new SangerFastqWriter().write(second, right);
        secondReader = new StringReader(second.toString());

        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, right));
        reader = new StringReader(interleaved.toString());

        listener = new PairedEndAdapter();
    }

    @Test(expected=NullPointerException.class)
    public void testIsLeftNull() {
        isLeft(null);
    }

    @Test
    public void testIsLeft() {
        assertTrue(isLeft(left));
        assertFalse(isLeft(right));
    }

    @Test(expected=NullPointerException.class)
    public void testIsRightNull() {
        isRight(null);
    }

    @Test
    public void testIsRight() {
        assertFalse(isRight(left));
        assertTrue(isRight(right));
    }

    @Test(expected=NullPointerException.class)
    public void testPrefixNull() {
        prefix(null);
    }

    @Test
    public void testPrefix() {
        assertEquals("prefix", prefix(left));
        assertEquals("prefix", prefix(right));
    }

    @Test(expected=PairedEndFastqReaderException.class)
    public void testPrefixInvalidPrefix() {
        prefix(invalidPrefix);
    }

    @Test(expected=NullPointerException.class)
    public void testReadPairedNullFirstReader() throws Exception {
        readPaired(null, secondReader, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testReadPairedNullSecondReader() throws Exception {
        readPaired(firstReader, null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testReadPairedNullListener() throws Exception {
        readPaired(firstReader, secondReader, null);
    }

    @Test
    public void testReadPaired() throws Exception {
        readPaired(firstReader, secondReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                    assertEquals(PairedEndFastqReaderTest.this.right.getDescription(), right.getDescription());
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    fail("unpaired " + unpaired);
                }
            });
    }

    @Test
    public void testReadPairedUnpaired() throws Exception {
        readPaired(firstReader, firstReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    fail("paired " + left + " " + right);
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                }
            });
    }


    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullFirstReader() throws Exception {
        streamPaired(null, secondReader, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullSecondReader() throws Exception {
        streamPaired(firstReader, null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamPairedNullListener() throws Exception {
        streamPaired(firstReader, secondReader, null);
    }

    @Test
    public void testStreamPaired() throws Exception {
        streamPaired(firstReader, secondReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                    assertEquals(PairedEndFastqReaderTest.this.right.getDescription(), right.getDescription());
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    fail("unpaired " + unpaired);
                }
            });
    }

    @Test
    public void testStreamPairedUnpaired() throws Exception {
        streamPaired(firstReader, firstReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    fail("paired " + left + " " + right);
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                }
            });
    }

    @Test(expected=IOException.class)
    public void testStreamPairedInvalidPrefixLeft() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, invalidPrefix);
        Reader invalidPrefixReader = new StringReader(outputStream.toString());

        streamPaired(invalidPrefixReader, secondReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamPairedInvalidPrefixRight() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, invalidPrefix);
        Reader invalidPrefixReader = new StringReader(outputStream.toString());

        streamPaired(firstReader, invalidPrefixReader, listener);
    }

    @Test
    public void testStreamPairedMismatchPrefix() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, mismatchPrefix);
        Reader mismatchPrefixReader = new StringReader(outputStream.toString());

        streamPaired(firstReader, mismatchPrefixReader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    fail("paired " + left + " " + right);
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    assertTrue(left.getDescription().equals(unpaired.getDescription()) ||
                               mismatchPrefix.getDescription().equals(unpaired.getDescription()));
                }
            });
    }

    @Test(expected=NullPointerException.class)
    public void testStreamInterleavedNullReader() throws Exception {
        streamInterleaved(null, listener);
    }

    @Test(expected=NullPointerException.class)
    public void testStreamInterleavedNullListener() throws Exception {
        streamInterleaved(reader, null);
    }

    @Test
    public void testStreamInterleaved() throws Exception {
        streamInterleaved(reader, new PairedEndAdapter() {
                @Override
                public void paired(final Fastq left, final Fastq right) {
                    assertEquals(PairedEndFastqReaderTest.this.left.getDescription(), left.getDescription());
                    assertEquals(PairedEndFastqReaderTest.this.right.getDescription(), right.getDescription());
                }

                @Override
                public void unpaired(final Fastq unpaired) {
                    fail("unpaired " + unpaired);
                }
            });
    }

    @Test
    public void testStreamInterleavedUnpairedLeft() throws Exception {
        streamInterleaved(firstReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedUnpairedRight() throws Exception {
        streamInterleaved(secondReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedInvalidPrefix() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, invalidPrefix);
        Reader invalidPrefixReader = new StringReader(outputStream.toString());

        streamInterleaved(invalidPrefixReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedInvalidPrefixLeft() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, ImmutableList.of(left, right, invalidPrefix));
        Reader invalidPrefixLeftReader = new StringReader(outputStream.toString());

        streamInterleaved(invalidPrefixLeftReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedInvalidPrefixRight() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new SangerFastqWriter().write(outputStream, ImmutableList.of(left, invalidPrefix));
        Reader invalidPrefixRightReader = new StringReader(outputStream.toString());

        streamInterleaved(invalidPrefixRightReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedOnlyLeft() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, left));
        Reader onlyLeftReader = new StringReader(interleaved.toString());

        streamInterleaved(onlyLeftReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedOnlyRight() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(right, right));
        Reader onlyRightReader = new StringReader(interleaved.toString());

        streamInterleaved(onlyRightReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedDuplicateLeft() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, left, right));
        Reader duplicateLeftReader = new StringReader(interleaved.toString());

        streamInterleaved(duplicateLeftReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedDuplicateRight() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, right, right));
        Reader duplicateRightReader = new StringReader(interleaved.toString());

        streamInterleaved(duplicateRightReader, listener);
    }

    @Test(expected=IOException.class)
    public void testStreamInterleavedMismatchPrefix() throws Exception {
        ByteArrayOutputStream interleaved = new ByteArrayOutputStream();
        new SangerFastqWriter().write(interleaved, ImmutableList.of(left, mismatchPrefix));
        Reader mismatchPrefixReader = new StringReader(interleaved.toString());

        streamInterleaved(mismatchPrefixReader, listener);
    }
}
