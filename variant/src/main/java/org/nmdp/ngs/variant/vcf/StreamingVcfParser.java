/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Streaming VCF parser.
 */
public final class StreamingVcfParser {

    /**
     * Private no-arg constructor.
     */
    private StreamingVcfParser() {
        // empty
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final VcfStreamListener listener) throws IOException {
        checkNotNull(readable);
        checkNotNull(listener);

        VcfParser.parse(readable, new VcfParseAdapter() {
                /** VCF record builder. */
                private final VcfRecord.Builder builder = VcfRecord.builder();

                /** File format, e.g. <code>VCFv4.2</code>, the only required header field. */
                private String fileFormat;

                /** List of meta-information header lines. */
                private List<String> meta = new ArrayList<String>();

                /** VCF samples keyed by name. */
                private Map<String, VcfSample> samples = new HashMap<String, VcfSample>();


                @Override
                public void lineNumber(final long lineNumber) throws IOException {
                    builder.withLineNumber(lineNumber);
                }

                @Override
                public void meta(final String meta) throws IOException {
                    this.meta.add(meta.trim());
                    if (meta.startsWith("##fileformat=")) {
                        fileFormat = meta.substring(13).trim();
                    }
                    else if (meta.startsWith("##SAMPLE=")) {
                        ListMultimap<String, String> values = ArrayListMultimap.create();
                        String[] tokens = meta.substring(10).split(",");
                        for (String token : tokens) {
                            String[] metaTokens = token.split("=");
                            String key = metaTokens[0];
                            String[] valueTokens = metaTokens[1].split(";");
                            for (String valueToken : valueTokens) {
                                values.put(key, valueToken.replace("\"", "").replace(">", ""));
                            }
                        }

                        String id = values.get("ID").get(0);
                        List<String> genomeIds = values.get("Genomes");
                        List<String> mixtures = values.get("Mixture");
                        List<String> descriptions = values.get("Description");

                        List<VcfGenome> genomes = new ArrayList<VcfGenome>(genomeIds.size());
                        for (int i = 0, size = genomeIds.size(); i < size; i++) {
                            genomes.add(new VcfGenome(genomeIds.get(i), Double.parseDouble(mixtures.get(i)), descriptions.get(i)));
                        }
                        samples.put(id, new VcfSample(id, genomes.toArray(new VcfGenome[0])));
                    }
                }

                @Override
                public void samples(final String... samples) throws IOException {
                    for (String sample : samples) {
                        // add if missing in meta lines
                        if (!this.samples.containsKey(sample)) {
                            this.samples.put(sample, new VcfSample(sample, new VcfGenome[0]));
                        }
                    }

                    // at end of header lines, notify listener of header
                    listener.header(new VcfHeader(fileFormat, meta));
                    // ...and samples
                    for (VcfSample sample : this.samples.values()) {
                        listener.sample(sample);
                    }
                }

                @Override
                public void chrom(final String chrom) throws IOException {
                    builder.withChrom(chrom);
                }

                @Override
                public void pos(final long pos) throws IOException {
                    builder.withPos(pos);
                }

                @Override
                public void id(final String... id) throws IOException {
                    builder.withId(id);
                }

                @Override
                public void ref(final String ref) throws IOException {
                    builder.withRef(ref);
                }

                @Override
                public void alt(final String... alt) throws IOException {
                    builder.withAlt(alt);
                }

                @Override
                public void qual(final double qual) throws IOException {
                    builder.withQual(qual);
                }

                @Override
                public void filter(final String... filter) throws IOException {
                    builder.withFilter(filter);
                }

                @Override
                public void info(final String infoId, final String... values) throws IOException {
                    builder.withInfo(infoId, values);
                }

                @Override
                public void format(final String... format) throws IOException {
                    builder.withFormat(format);
                }

                @Override
                public void genotype(final String sampleId, final String formatId, final String... values) throws IOException {
                    builder.withGenotype(sampleId, formatId, values);
                }

                @Override
                public boolean complete() throws IOException {
                    listener.record(builder.build());

                    builder.reset();
                    fileFormat = null;
                    meta = null;
                    samples = null;

                    return true;
                }
            });
    }
}
