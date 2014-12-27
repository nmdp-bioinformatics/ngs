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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

/**
 * VCF sample.
 */
@Immutable
public final class VcfSample {
    /** Id. */
    private final String id;

    /** Array of VCF genomes. */
    private final VcfGenome[] genomes;


    /**
     * Create a new VCF sample.
     *
     * @param id id, must not be null
     * @param genomes VCF genomes, must not be null
     */
    VcfSample(final String id, final VcfGenome... genomes) {
        checkNotNull(id);
        checkNotNull(genomes);
        this.id = id;
        this.genomes = genomes;
    }


    /**
     * Return the id for this VCF sample.
     *
     * @return the id for this VCF sample.
     */
    public String getId() {
        return id;
    }

    /**
     * Return the VCF genomes for this VCF sample.
     *
     * @return the VCF genomes for this VCF sample
     */
    public VcfGenome[] getGenomes() {
        return genomes;
    }


    /**
     * Create and return a new VCF sample builder.
     *
     * @return a new VCF sample builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF sample builder.
     */
    public static final class Builder {
        /** Id. */
        private String id;

        /** List of VCF genomes. */
        private final List<VcfGenome> genomes = new ArrayList<VcfGenome>();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF sample builder configured with the specified id.
         *
         * @param id id
         * @return this VCF sample builder configured with the specified id
         */
        public Builder withId(final String id) {
            this.id = id;
            return this;
        }

        /**
         * Return this VCF sample builder configured with the specified VCF genome.
         *
         * @param genome VCF genome, must not be null
         * @return this VCF sample builder configured with the specified VCF genome
         */
        public Builder withGenome(final VcfGenome genome) {
            checkNotNull(genome);
            genomes.add(genome);
            return this;
        }

        /**
         * Return this VCF sample builder configured with the specified VCF genomes.
         *
         * @param genome one or more VCF genomes, must not contain null
         * @return this VCF sample builder configured with the specified VCF genomes
         */
        public Builder withGenomes(final VcfGenome... genomes) {
            checkNotNull(genomes);
            for (VcfGenome genome : genomes) {
                withGenome(genome);
            }
            return this;
        }

        /**
         * Return this VCF sample builder configured with the specified VCF genomes.
         *
         * @param genome one or more VCF genomes, must not be null and must not contain null values
         * @return this VCF sample builder configured with the specified VCF genomes
         */
        public Builder withGenomes(final List<VcfGenome> genomes) {
            checkNotNull(genomes);
            for (VcfGenome genome : genomes) {
                withGenome(genome);
            }
            return this;
        }

        /**
         * Reset this VCF sample builder.
         *
         * @return this VCF sample builder
         */
        public Builder reset() {
            id = null;
            genomes.clear();
            return this;
        }

        /**
         * Create and return a new VCF sample populated from the configuration of this VCF sample builder.
         *
         * @return a new VCF sample populated from the configuration of this VCF sample builder
         */
        public VcfSample build() {
            return new VcfSample(id, genomes.toArray(new VcfGenome[genomes.size()]));
        }
    }
}
