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
}
