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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

/**
 * VCF record.
 */
@Immutable
public final class VcfRecord {
    /** Line number. */
    private long lineNumber;

    /** Chromosome. */
    private String chrom;

    /** Position. */
    private long pos;

    /** Array of ids. */
    private String[] id;

    /** Reference allele. */
    private String ref;

    /** Array of alternate alleles. */
    private String[] alt;

    /** QUAL score. */
    private double qual;

    /** Filter. */
    private String[] filter;

    /** INFO key-value(s) pairs. */
    private ListMultimap<String, String> info;

    /** Format. */
    private String[] format;

    /** Genotypes keyed by sample id. */
    private Map<String, VcfGenotype> genotypes;


    /**
     * Create a new VCF record.
     *
     * @param lineNumber line number
     * @param chrom chromosome
     * @param pos position
     * @param id array of ids, must not be null
     * @param ref reference allele
     * @param alt array of alternate alleles, must not be null
     * @param qual QUAL score
     * @param filter filter
     * @param info INFO key-value(s) pairs, must not be null
     * @param format format
     * @param genotypes genotypes keyed by sample id, must not be null
     */
    VcfRecord(final long lineNumber,
              final String chrom,
              final long pos,
              final String[] id,
              final String ref,
              final String[] alt,
              final double qual,
              final String[] filter,
              final ListMultimap<String, String> info,
              final String[] format,
              final Map<String, VcfGenotype> genotypes) {

        checkNotNull(id);
        checkNotNull(alt);
        checkNotNull(info);
        checkNotNull(genotypes);
        this.lineNumber = lineNumber;
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.info = ImmutableListMultimap.copyOf(info);
        this.format = format;
        this.genotypes = ImmutableMap.copyOf(genotypes);
    }


    /**
     * Return the line number for this VCF record.
     *
     * @return the line number for this VCF record
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Return the chromosome for this VCF record.
     *
     * @return the chromosome for this VCF record
     */
    public String getChrom() {
        return chrom;
    }

    /**
     * Return the position for this VCF record.
     *
     * @return the position for this VCF record
     */
    public long getPos() {
        return pos;
    }

    /**
     * Return the array of ids for this VCF record.
     *
     * @return the array of ids for this VCF record
     */
    public String[] getId() {
        return id;
    }

    /**
     * Return the reference allele for this VCF record.
     *
     * @return the reference allele for this VCF record
     */
    public String getRef() {
        return ref;
    }

    /**
     * Return the alternate alleles for this VCF record.
     *
     * @return the alternate alleles for this VCF record
     */
    public String[] getAlt() {
        return alt;
    }

    /**
     * Return the QUAL score for this VCF record.
     *
     * @return the QUAL score for this VCF record
     */
    public double getQual() {
        return qual;
    }

    /**
     * Return the filter for this VCF record.
     *
     * @return the filter for this VCF record
     */
    public String[] getFilter() {
        return filter;
    }

    /**
     * Return the INFO key-value(s) pairs for this VCF record.
     *
     * @return the INFO key-value(s) pairs for this VCF record
     */
    public ListMultimap<String, String> getInfo() {
        return info;
    }

    /**
     * Return the format for this VCF record.
     *
     * @return the format for this VCF record
     */
    public String[] getFormat() {
        return format;
    }

    /**
     * Return the genotypes keyed by sample id for this VCF record.
     *
     * @return the genotypes keyed by sample id for this VCF record
     */
    public Map<String, VcfGenotype> getGenotypes() {
        return genotypes;
    }

    /**
     * Create and return a new VCF record builder.
     *
     * @return a new VCF record builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF record builder.
     */
    public static final class Builder {
        /** Line number. */
        private long lineNumber;

        /** Chromosome. */
        private String chrom;

        /** Position. */
        private long pos;

        /** Array of ids. */
        private String[] id;

        /** Reference allele. */
        private String ref;

        /** Array of alternate alleles. */
        private String[] alt;

        /** QUAL score. */
        private double qual;

        /** Filter. */
        private String[] filter;

        /** Map of INFO key-value(s) pairs. */
        private ListMultimap<String, String> info = ArrayListMultimap.create();

        /** Format. */
        private String[] format;

        /** Map builder for genotypes keyed by sample id. */
        private ImmutableMap.Builder<String, VcfGenotype> genotypes = ImmutableMap.builder();

        /** Map of genotype fields keyed by sample id. */
        private Map<String, ListMultimap<String, String>> genotypeFields = new HashMap<String, ListMultimap<String, String>>();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF record builder configured with the specified line number.
         *
         * @param lineNumber line number
         * @return this VCF record builder configured with the specified line number
         */
        public Builder withLineNumber(final long lineNumber) {
            this.lineNumber = lineNumber;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified chromosome.
         *
         * @param chrom chromosome
         * @return this VCF record builder configured with the specified chromosome
         */
        public Builder withChrom(final String chrom) {
            this.chrom = chrom;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified position.
         *
         * @param pos position
         * @return this VCF record builder configured with the specified position
         */
        public Builder withPos(final long pos) {
            this.pos = pos;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified ids.
         *
         * @param id ids
         * @return this VCF record builder configured with the specified ids
         */
        public Builder withId(final String... id) {
            this.id = id;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified reference allele.
         *
         * @param ref reference allele
         * @return this VCF record builder configured with the specified reference allele
         */
        public Builder withRef(final String ref) {
            this.ref = ref;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified alternate alleles.
         *
         * @param alt alternate alleles
         * @return this VCF record builder configured with the specified alternate alleles
         */
        public Builder withAlt(final String... alt) {
            this.alt = alt;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified QUAL score.
         *
         * @param qual QUAL score
         * @return this VCF record builder configured with the specified QUAL score
         */
        public Builder withQual(final double qual) {
            this.qual = qual;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified filter.
         *
         * @param filter filter
         * @return this VCF record builder configured with the specified filter
         */
        public Builder withFilter(final String... filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value(s) pair.
         *
         * @param infoId INFO ID key, must not be null
         * @param values INFO values
         * @return this VCF record builder configured with the specified INFO key-value(s) pair
         */
        public Builder withInfo(final String infoId, final String... values) {
            checkNotNull(infoId);

            if (values == null) {
                info.removeAll(infoId);
            }
            else {
                for (String value : values) {
                    info.put(infoId, value);
                }
            }
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified INFO key-value pairs.
         *
         * @param info INFO key-value pairs, must not be null
         * @return this VCF record builder configured with the specified INFO key-value pairs
         */
        public Builder withInfo(final ListMultimap<String, String> info) {
            this.info.putAll(info);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified format.
         *
         * @param format format
         * @return this VCF record builder configured with the specified format
         */
        public Builder withFormat(final String... format) {
            this.format = format;
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotype field keyed by sample id.
         *
         * @param sampleId sample id, must not be null
         * @param formatId genotype field format id, must not be null
         * @param values values
         * @return this VCF record builder configured with the specified genotype field keyed by sample id
         */
        public Builder withGenotype(final String sampleId, final String formatId, final String... values) {
            if (!genotypeFields.containsKey(sampleId)) {
                genotypeFields.put(sampleId, ArrayListMultimap.<String, String>create());
            }
            if (values == null) { // || ".".equals(value)
                genotypeFields.get(sampleId).removeAll(formatId);
            }
            else {
                for (String value : values) {
                    // what about missing values here?
                    genotypeFields.get(sampleId).put(formatId, value);
                }
            }
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotype keyed by sample id.
         *
         * @param sampleId sample id, must not be null
         * @param genotype genotype, must not be null
         * @return this VCF record builder configured with the specified genotype keyed by sample id
         */
        public Builder withGenotype(final String sampleId, final VcfGenotype genotype) {
            genotypes.put(sampleId, genotype);
            return this;
        }

        /**
         * Return this VCF record builder configured with the specified genotypes keyed by sample id.
         *
         * @param genotypes genotypes keyed by sample id, must not be null
         * @return this VCF record builder configured with the specified genotypes keyed by sample id
         */
        public Builder withGenotypes(final Map<String, VcfGenotype> genotypes) {
            this.genotypes.putAll(genotypes);
            return this;
        }

        /**
         * Reset this VCF record builder.
         *
         * @return this VCF record builder
         */
        public Builder reset() {
            lineNumber = -1L;
            chrom = null;
            pos = -1L;
            id = null;
            ref = null;
            alt = null;
            qual = -1.0d;
            filter = null;
            info.clear();
            genotypes = ImmutableMap.builder();
            genotypeFields.clear();
            return this;
        }

        /**
         * Create and return a new VCF record populated from the configuration of this VCF record builder.
         *
         * @return a new VCF record populated from the configuration of this VCF record builder
         */
        public VcfRecord build() {
            // build genotypes from genotype fields if necessary
            for (Map.Entry<String, ListMultimap<String, String>> entry : genotypeFields.entrySet()) {
                String sampleId = entry.getKey();
                genotypes.put(sampleId, VcfGenotype.builder().withFields(entry.getValue()).build());
            }
            return new VcfRecord(lineNumber, chrom, pos, id, ref, alt, qual, filter, info, format, genotypes.build());
        }
    }
}
