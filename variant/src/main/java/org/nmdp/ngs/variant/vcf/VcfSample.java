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
}
