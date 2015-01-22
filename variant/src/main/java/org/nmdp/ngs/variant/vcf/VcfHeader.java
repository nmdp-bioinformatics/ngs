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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

/**
 * VCF header.
 */
@Immutable
public final class VcfHeader {
    /** File format, e.g. <code>VCFv4.2</code>, the only required header field. */
    private final String fileFormat;

    /** List of meta-information header lines. */
    private final List<String> meta;


    /**
     * Create a new VCF header.
     *
     * @param fileFormat file format, must not be null
     * @param meta list of meta-information heder lines, must not be null
     */
    VcfHeader(final String fileFormat, final List<String> meta) {
        checkNotNull(fileFormat);
        checkNotNull(meta);
        this.fileFormat = fileFormat;
        this.meta = ImmutableList.copyOf(meta);
    }


    /**
     * Return the file format for this VCF header.
     *
     * @return the file format for this VCF header
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * Return the meta-information header lines for this VCF header.
     *
     * @return the meta-information header lines for this VCF header
     */
    public List<String> getMeta() {
        return meta;
    }


    /**
     * Create and return a new VCF header builder.
     *
     * @return a new VCF header builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * VCF header builder.
     */
    public static final class Builder {
        /** File format. */
        private String fileFormat;

        /** List of meta-information header lines. */
        private final List<String> meta = new ArrayList<String>();


        /**
         * Private no-arg constructor.
         */
        private Builder() {
            // empty
        }


        /**
         * Return this VCF header builder configured with the specified file format.
         *
         * @param fileFormat file format
         * @return this VCF header builder configured with the specified file format
         */
        public Builder withFileFormat(final String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        /**
         * Return this VCF header builder configured with the specified meta-information header line.
         *
         * @param meta meta-information header line, must not be null
         * @return this VCF header builder configured with the specified meta-information header line
         */
        public Builder withMeta(final String meta) {
            checkNotNull(meta);
            this.meta.add(meta);
            return this;
        }

        /**
         * Return this VCF header builder configured with the specified meta-information header lines.
         *
         * @param meta one or more meta-information header lines, must not contain null
         * @return this VCF header builder configured with the specified meta-information header line
         */
        public Builder withMeta(final String... meta) {
            checkNotNull(meta);
            for (String m : meta) {
                withMeta(m);
            }
            return this;
        }

        /**
         * Return this VCF header builder configured with the specified meta-information header lines.
         *
         * @param meta list of meta-information header lines, must not be null and must not contain null
         * @return this VCF header builder configured with the specified meta-information header lines
         */
        public Builder withMeta(final List<String> meta) {
            checkNotNull(meta);
            for (String m : meta) {
                withMeta(m);
            }
            return this;
        }

        /**
         * Reset this VCF header builder.
         *
         * @return this VCF header builder
         */
        public Builder reset() {
            fileFormat = null;
            meta.clear();
            return this;
        }

        /**
         * Create and return a new VCF header populated from the configuration of this VCF header builder.
         *
         * @return a new VCF header populated from the configuration of this VCF header builder
         */
        public VcfHeader build() {
            // todo: copy fileFormat to first line of meta?
            return new VcfHeader(fileFormat, meta);
        }
    }
}
