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
 * VCF sample parser.
 */
public final class VcfSampleParser {

    /**
     * Private no-arg constructor.
     */
    private VcfSampleParser() {
        // empty
    }


    /**
     * Read zero or more VCF samples from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more VCF samples read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfSample> samples(final Readable readable) throws IOException {
        checkNotNull(readable);
        ParseListener parseListener = new ParseListener();
        VcfParser.parse(readable, parseListener);
        return parseListener.getSamples().values();
    }

    /**
     * Parse listener.
     */
    static final class ParseListener extends VcfParseAdapter {
        /** VCF samples keyed by name. */
        private Map<String, VcfSample> samples = new HashMap<String, VcfSample>();

        /*
          ##SAMPLE=<ID=S_ID,Genomes=G1_ID;G2_ID; ...;GK_ID,Mixture=N1;N2; ...;NK,Description=S1;S2; ...; SK >

          ##SAMPLE=<ID=Blood,Genomes=Germline,Mixture=1.,Description="Patient germline genome">
          ##SAMPLE=<ID=TissueSample,Genomes=Germline;Tumor,Mixture=.3,.7,Description="Patient germline genome;Patient tumor genome">
        */

        @Override
        public void meta(final String meta) throws IOException {
            if (meta.startsWith("##SAMPLE=")) {
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
                samples.put(id, new VcfSample(id, genomes.toArray(new VcfGenome[genomes.size()])));
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
        }

        @Override
        public boolean complete() throws IOException {
            return false;
        }

        /**
         * Return the VCF samples keyed by name.
         *
         * @return the VCF samples keyed by name
         */
        Map<String, VcfSample> getSamples() {
            return samples;
        }
    }
}
