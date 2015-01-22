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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * High-scoring segment pair (HSP) reader.
 */
public final class HspReader {

    /**
     * Private no-arg constructor.
     */
    private HspReader() {
        // empty
    }


    /**
     * Read zero or more high-scoring segment pairs from the specified readable.
     *
     * @param readable to read from, must not be null
     * @return zero or more high-scoring segment pairs read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<HighScoringPair> read(final Readable readable) throws IOException {
        checkNotNull(readable);
        Collect collect = new Collect();
        stream(readable, collect);
        return collect.hsps();
    }

    /**
     * Stream zero or more high-scoring segment pairs from the specified readable.
     *
     * @param readable readable to stream from, must not be null
     * @param listener event based listener callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final HspListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        HspLineProcessor lineProcessor = new HspLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * High-scoring segment pair (HSP) line processor.
     */
    private static final class HspLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** High-scoring segment pair listener. */
        private final HspListener listener;


        /**
         * Create a new high-scoring segment pair line processor with the specified high-scoring segment pair listener.
         *
         * @param listener high-scoring segment pair listener, must not be null
         */
        private HspLineProcessor(final HspListener listener) {
            checkNotNull(listener);
            this.listener = listener;
        }


        @Override
        public Object getResult() {
            return null;
        }

        @Override
        public boolean processLine(final String line) throws IOException
        {
            try {
                lineNumber++;
                return line.startsWith("#") ? true : listener.hsp(HighScoringPair.valueOf(line));
            }
            catch (IllegalArgumentException e) {
                throw new IOException("could not read high-scoring segment pair at line " + lineNumber + ", caught " + e.getMessage(), e);
            }
        }
    }


    /**
     * Collect.
     */
    private static class Collect implements HspListener {
        /** List of collected high-scoring segment pairs. */
        private final List<HighScoringPair> hsps = new LinkedList<HighScoringPair>();


        @Override
        public boolean hsp(final HighScoringPair hsp) {
            hsps.add(hsp);
            return true;
        }

        /**
         * Return zero or more collected high-scoring segment pairs.
         *
         * @return zero or more collected high-scoring segment pairs
         */
        Iterable<HighScoringPair> hsps() {
            return hsps;
        }
    }
}
