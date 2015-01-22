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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.Reader;

import java.util.Collections;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.collect.Lists;

import org.biojava.bio.program.fastq.Fastq;
import org.biojava.bio.program.fastq.SangerFastqReader;
import org.biojava.bio.program.fastq.StreamListener;

/**
 * Paired end FASTQ reads reader.
 */
public final class PairedEndFastqReader {
    /** Pattern for the left or first read of a paired end read, relies on convention of "<code> 1</code>" in the description line. */
    static final Pattern LEFT = Pattern.compile("^.* 1.*$");

    /** Pattern for the right or second read of a paired end read, relies on convention of "<code> 2</code>" in the description line. */
    static final Pattern RIGHT = Pattern.compile("^.* 2.*$");

    /** Pattern for capturing the prefix of a paired end read name, relies on convention of "<code> 1</code>" or "<code> 2</code>" in the description line. */
    static final Pattern PREFIX = Pattern.compile("^(.+) [12].*$");


    /**
     * Private no-arg constructor.
     */
    private PairedEndFastqReader() {
        // empty
    }


    /**
     * Read the specified paired end reads.  The paired end reads are read fully into RAM before processing.
     *
     * @param firstReader first reader, must not be null
     * @param secondReader second reader, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     * @deprecated by {@link #readPaired(Readable,Readable,PairedEndListener)}, will be removed in version 2.0
     */
    public static void readPaired(final Reader firstReader,
                                  final Reader secondReader,
                                  final PairedEndListener listener) throws IOException {
        readPaired((Readable) firstReader, (Readable) secondReader, listener);
    }

    /**
     * Read the specified paired end reads.  The paired end reads are read fully into RAM before processing.
     *
     * @param firstReadable first readable, must not be null
     * @param secondReadable second readable, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     * @deprecated by {@link #streamPaired(Readable,Readable,PairedEndListener)}, will be removed in version 2.0
     */
    public static void readPaired(final Readable firstReadable,
                                  final Readable secondReadable,
                                  final PairedEndListener listener) throws IOException {

        checkNotNull(firstReadable);
        checkNotNull(secondReadable);
        checkNotNull(listener);

        // read both FASTQ files into RAM (ick)
        final List<Fastq> reads = Lists.newArrayList();
        SangerFastqReader fastqReader = new SangerFastqReader();
        fastqReader.stream(firstReadable, new StreamListener() {
                @Override
                public void fastq(final Fastq fastq) {
                    reads.add(fastq);
                }
            });
        fastqReader.stream(secondReadable, new StreamListener() {
                @Override
                public void fastq(final Fastq fastq) {
                    reads.add(fastq);
                }
            });

        // .. and sort by description
        Collections.sort(reads, new Ordering<Fastq>() {
                @Override
                public int compare(final Fastq left, final Fastq right) {
                    return left.getDescription().compareTo(right.getDescription());
                }
            });

        for (int i = 0, size = reads.size(); i < size; ) {
            Fastq left = reads.get(i);
            if ((i + 1) == size) {
                listener.unpaired(left);
                break;
            }
            Fastq right = reads.get(i + 1);

            if (isLeft(left)) {
                if (isRight(right)) {
                    // todo:  assert prefixes match
                    listener.paired(left, right);
                    i += 2;
                }
                else {
                    listener.unpaired(right);
                    i++;
                }
            }
            else {
                listener.unpaired(left);
                i++;
            }
        }
    }

    /**
     * Stream the specified paired end reads.  RAM usage is minimal if the paired end reads are sorted.
     *
     * @param firstReader first reader, must not be null
     * @param secondReader second reader, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     * @deprecated by {@link #streamPaired(Readable,Readable,PairedEndListener)}, will be removed in version 2.0
     */
    public static void streamPaired(final Reader firstReader,
                                    final Reader secondReader,
                                    final PairedEndListener listener) throws IOException {
        streamPaired((Readable) firstReader, (Readable) secondReader, listener);
    }

    /**
     * Stream the specified paired end reads.  RAM usage is minimal if the paired end reads are sorted.
     *
     * @param firstReadable first readable, must not be null
     * @param secondReadable second readable, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamPaired(final Readable firstReadable,
                                    final Readable secondReadable,
                                    final PairedEndListener listener) throws IOException {

        checkNotNull(firstReadable);
        checkNotNull(secondReadable);
        checkNotNull(listener);

        final ConcurrentMap<String, Fastq> keyedByPrefix = new ConcurrentHashMap<>();

        final StreamListener streamListener = new StreamListener() {
                @Override
                public void fastq(final Fastq fastq) {
                    String prefix = prefix(fastq);
                    Fastq other = keyedByPrefix.putIfAbsent(prefix, fastq);
                    if ((other != null) && !fastq.equals(other)) {
                        if (isLeft(other) && isRight(fastq)) {
                            listener.paired(other, fastq);
                        }
                        else if (isRight(other) && isLeft(fastq)) {
                            listener.paired(fastq, other);
                        }
                        else {
                            throw new PairedEndFastqReaderException("fastq " + fastq + " other " + other);
                        }
                        keyedByPrefix.remove(prefix);
                    }
                }
            };

        try {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            Callable<Void> task1 = new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    new SangerFastqReader().stream(firstReadable, streamListener);
                    return null;
                }
            };
            Callable<Void> task2 = new Callable<Void>() {
                @Override
                public Void call() throws IOException {
                    new SangerFastqReader().stream(secondReadable, streamListener);
                    return null;
                }
            };

            for (Future<Void> future : executor.invokeAll(ImmutableList.of(task1, task2))) {
                future.get();
            }
            executor.shutdown();
        }
        catch (ExecutionException e) {
            throw new IOException(e.getCause());
        }
        catch (InterruptedException e) {
            // ignore
        }
        catch (PairedEndFastqReaderException e) {
            throw new IOException("could not read paired end FASTQ reads", e);
        }

        for (Fastq unpaired : keyedByPrefix.values()) {
            listener.unpaired(unpaired);
        }
    }

    /**
     * Stream the specified interleaved paired end reads.  Per the interleaved format, all reads must be sorted and paired.
     *
     * @param reader reader, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     * @deprecated by {@link #streamInterleaved(Readable,PairedEndListener)}, will be removed in version 2.0
     */
    public static void streamInterleaved(final Reader reader, final PairedEndListener listener) throws IOException {
        streamInterleaved((Readable) reader, listener);
    }

    /**
     * Stream the specified interleaved paired end reads.  Per the interleaved format, all reads must be sorted and paired.
     *
     * @param readable readable, must not be null
     * @param listener paired end listener, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void streamInterleaved(final Readable readable, final PairedEndListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        StreamListener streamListener = new StreamListener() {
                private Fastq left;

                @Override
                public void fastq(final Fastq fastq) {
                    if (isLeft(fastq) && (left == null)) {
                        left = fastq;
                    }
                    else if (isRight(fastq) && (left != null) && (prefix(left).equals(prefix(fastq)))) {
                        Fastq right = fastq;
                        listener.paired(left, right);
                        left = null;
                    }
                    else {
                        throw new PairedEndFastqReaderException("invalid interleaved FASTQ format, left=" + (left == null ? "null" : left.getDescription()) + " right=" + (fastq == null ? "null" : fastq.getDescription()));
                    }
                }
            };

        try {
            new SangerFastqReader().stream(readable, streamListener);
        }
        catch (PairedEndFastqReaderException e) {
            throw new IOException("could not stream interleaved paired end FASTQ reads", e);
        }
    }

    /**
     * Return true if the specified fastq is the left or first read of a paired end read.
     *
     * @param fastq fastq, must not be null
     * @return true if the specified fastq is the left or first read of a paired end read
     */
    static boolean isLeft(final Fastq fastq) {
        checkNotNull(fastq);
        return LEFT.matcher(fastq.getDescription()).matches();
    }

    /**
     * Return true if the specified fastq is the right or second read of a paired end read.
     *
     * @param fastq fastq, must not be null
     * @return true if the specified fastq is the right or second read of a paired end read
     */
    static boolean isRight(final Fastq fastq) {
        checkNotNull(fastq);
        return RIGHT.matcher(fastq.getDescription()).matches();
    }

    /**
     * Return the prefix of the paired end read name of the specified fastq.
     *
     * @param fastq fastq, must not be null
     * @return the prefix of the paired end read name of the specified fastq
     */
    static String prefix(final Fastq fastq) {
        checkNotNull(fastq);
        Matcher m = PREFIX.matcher(fastq.getDescription());
        if (!m.matches()) {
            throw new PairedEndFastqReaderException("could not parse prefix from description " + fastq.getDescription());
        }
        return m.group(1);
    }
}
