/*

    ngs-feature  Features.
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
package org.nmdp.ngs.feature;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;

public final class VcfRecord {
    private final List<Allele> alleles;
    private final List<Sample> samples;
  
    public VcfRecord(final List<Allele> alleles, final List<Sample> samples) {
        checkNotNull(alleles);
        checkNotNull(samples);
        this.alleles = ImmutableList.copyOf(alleles);
        this.samples = ImmutableList.copyOf(samples);
    }


    public List<Allele> getAlleles() {
        return alleles;
    }

    public List<Sample> getSamples() {
        return samples;
    }
}
