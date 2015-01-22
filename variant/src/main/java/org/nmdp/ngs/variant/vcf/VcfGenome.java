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

import javax.annotation.concurrent.Immutable;

/**
 * VCF genome.
 */
@Immutable
public final class VcfGenome {
    /** Id. */
    private final String id;

    /** Mixture. */
    private final double mixture;

    /** Description. */
    private final String description;


    /**
     * Create a new VCF genome.
     *
     * @param id id, must not be null
     * @param mixture mixture
     * @param description description
     */
    VcfGenome(final String id, final double mixture, final String description) {
        checkNotNull(id);
        this.id = id;
        this.mixture = mixture;
        this.description = description;
    }


    /**
     * Return the id for this VCF genome.
     *
     * @return the id for this VCF genome
     */
    public String getId() {
        return id;
    }

    /**
     * Return the mixture for this VCF genome.
     *
     * @return the mixture for this VCF genome
     */
    public double getMixture() {
        return mixture;
    }

    /**
     * Return the description for this VCF genome.
     *
     * @return the description for this VCF genome
     */
    public String getDescription() {
        return description;
    }
}
