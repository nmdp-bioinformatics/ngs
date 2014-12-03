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

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * VCF genotype.
 */
@Immutable
public final class VcfGenotype {
    /** Genotype fields. */
    private final ListMultimap<String, String> fields;


    /**
     * Create a new VCF genotype with the specified genotype fields.
     *
     * @param fields genotype fields, must not be null
     */
    VcfGenotype(final ListMultimap<String, String> fields) {
        this.fields = ImmutableListMultimap.copyOf(fields);
    }


    /**
     * Return the value of the GT genotype field for this VCF genotype or <code>null</code> if no such field exists.
     *
     * @return the value of the GT genotype field for this VCF genotype or <code>null</code> if no such field exists
     */
    public String getGt() {
        return fields.get("GT").isEmpty() ? null : fields.get("GT").get(0);
    }

    /**
     * Return the genotype fields for this VCF genotype.
     *
     * @return the genotype fields for this VCF genotype
     */
    public ListMultimap<String, String> getFields() {
        return fields;
    }

    /**
     * Create and return a new VCF genotype builder.
     *
     * @return a new VCF genotype builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF genotype builder.
     */
    public static final class Builder {
        /** Genotype fields. */
        private ListMultimap<String, String> fields = ArrayListMultimap.create();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF genotype builder configured with the specified GT genotype field.
         *
         * @param gt GT genotype field
         * @return this VCF record builder configured with the specified GT genotype field
         */
        public Builder withGt(final String gt) {
            if (gt == null) {
                fields.removeAll("GT");
            }
            else {
                fields.put("GT", gt);
            }
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype field.
         *
         * @param id genotype field id
         * @param values genotype field values
         * @return this VCF record builder configured with the specified genotype field
         */
        public Builder withField(final String id, final String... values) {
            if (values != null) {
                for (String value : values) {
                    fields.put(id, value);
                }
            }
            return this;
        }

        /**
         * Return this VCF genotype builder configured with the specified genotype fields.
         *
         * @param fields genotype fields, must not be null
         * @return this VCF record builder configured with the specified genotype fields
         */
        public Builder withFields(final ListMultimap<String, String> fields) {
            this.fields.putAll(fields);
            return this;
        }

        /**
         * Reset this VCF genotype builder.
         *
         * @return this VCF genotype builder
         */
        public Builder reset() {
            fields.clear();
            return this;
        }

        /**
         * Create and return a new VCF genotype populated from the configuration of this VCF genotype builder.
         *
         * @return a new VCF genotype populated from the configuration of this VCF genotype builder
         */
        public VcfGenotype build() {
            // check GT cardinality constraint
            if (fields.containsKey("GT")) {
                String gt = fields.get("GT").get(0);
                fields.removeAll("GT");
                if (gt != null) {
                    fields.put("GT", gt);
                }
            }
            return new VcfGenotype(fields);
        }
    }
}
