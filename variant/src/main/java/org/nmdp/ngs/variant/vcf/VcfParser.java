/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;

import java.util.Collection;
import java.util.Map;

import com.google.common.io.CharStreams;
import com.google.common.io.LineProcessor;

/**
 * Low-level VCF parser.
 */
public final class VcfParser {

    /**
     * Private no-arg constructor.
     */
    private VcfParser() {
        // empty
    }


    /**
     * Parse the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener low-level event based parser callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void parse(final Readable readable, final VcfParseListener listener) throws IOException {
        checkNotNull(readable);
        VcfLineProcessor lineProcessor = new VcfLineProcessor(listener);
        CharStreams.readLines(readable, lineProcessor);
    }

    /**
     * VCF line processor.
     */
    private static final class VcfLineProcessor implements LineProcessor<Object> {
        /** Line number. */
        private long lineNumber = 0;

        /** VCF parse listener. */
        private final VcfParseListener listener;

        /** Map of sample names keyed by column. */
        private final Map<Integer, String> samples = newHashMap();


        /**
         * Create a new VCF line processor.
         *
         * @param listener VCF parse listener
         */
        private VcfLineProcessor(final VcfParseListener listener) {
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
            lineNumber++;
            // consider using guava Splitter
            String[] tokens = line.split("\t");

            if (tokens[0].startsWith("##")) {
                // meta-information lines
                listener.meta(line);
            }
            else if (tokens[0].startsWith("#CHROM")) {
                // header line
                if (tokens.length > 8) {
                    for (int column = 9, columns = tokens.length; column < columns; column++) {
                        samples.put(column, tokens[column]);
                    }
                }
                Collection<String> values = samples.values();
                listener.samples(values.toArray(new String[values.size()]));
            }
            else {
                // data lines
                listener.lineNumber(lineNumber);
                if (tokens.length < 8) {
                    throw new IOException("invalid data line at line number " + lineNumber + ", expected 8 tokens, found " + tokens.length);
                }

                listener.chrom(tokens[0]);

                try {
                    listener.pos(Long.parseLong(tokens[1]));
                }
                catch (NumberFormatException e) {
                    throw new IOException("invalid pos at line number " + lineNumber, e);
                }

                String[] idTokens = tokens[2].split(";");
                listener.id(isMissingValue(idTokens) ? new String[0] : idTokens);

                listener.ref(tokens[3]);

                // todo: check for symbolic alleles
                String[] altTokens = tokens[4].split(",");
                listener.alt(isMissingValue(altTokens) ? new String[0] : altTokens);

                try {
                    listener.qual(isMissingValue(tokens[5]) ? Double.NaN : Double.parseDouble(tokens[5]));
                }
                catch (NumberFormatException e) {
                    throw new IOException("invalid qual at line number " + lineNumber, e);
                }

                String[] filterTokens = tokens[6].split(";");
                listener.filter(isMissingValue(filterTokens) ? new String[0] : filterTokens);

                String[] infoTokens = tokens[7].split(";");
                if (!isMissingValue(infoTokens)) {
                    for (String infoToken : infoTokens) {
                        String[] entryTokens = infoToken.split("=");
                        if (entryTokens.length == 1) {
                            listener.info(entryTokens[0]);
                        }
                        else if (entryTokens.length == 2) {
                            String infoId = entryTokens[0];
                            String value = entryTokens[1];
                            if (isMissingValue(value)) {
                                listener.info(infoId);
                            }
                            else {
                                listener.info(infoId, value.split(","));
                            }
                        }
                    }
                }

                if (tokens.length > 8) {
                    String[] formatTokens = tokens[8].split(":");
                    listener.format(formatTokens);

                    for (int column = 9, columns = tokens.length; column < columns; column++) {
                        String[] genotypeTokens = tokens[column].split(":");

                        if (genotypeTokens.length != formatTokens.length) {
                            throw new IOException("invalid genotype fields at line number " + lineNumber);
                        }
                        for (int i = 0, size = formatTokens.length; i < size; i++) {
                            if (!isMissingValue(genotypeTokens[i])) {
                                listener.genotype(samples.get(column), formatTokens[i], genotypeTokens[i].split(","));
                            }
                        }
                    }
                }

                if (!listener.complete()) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Return true if the specified value is the missing value (<code>"."</code>).
     *
     * @param value value
     * @return true if the specified value is the missing value (<code>"."</code>)
     */
    static boolean isMissingValue(final String value) {
        return ".".equals(value);
    }

    /**
     * Return true if the specified array of values is the missing value (<code>"."</code>).
     *
     * @param values array of values
     * @return true if the specified array of values is the missing value (<code>"."</code>)
     */
    static boolean isMissingValue(final String[] values) {
        return (values.length == 1 && ".".equals(values[0]));
    }
}
