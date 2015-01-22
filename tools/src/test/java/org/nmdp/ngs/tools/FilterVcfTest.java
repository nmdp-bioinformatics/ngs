/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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
package org.nmdp.ngs.tools;

import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import org.nmdp.ngs.variant.vcf.VcfRecord;

/**
 * Unit test for FilterVcf.
 */
public final class FilterVcfTest {
    private FilterVcf.Filter filter;
    private File inputVcfFile;
    private File outputVcfFile;

    @Before
    public void setUp() throws Exception {
        filter = new FilterVcf.Filter() {
                @Override
                public boolean accept(final VcfRecord record) {
                    return true;
                }
            };
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFilter() {
        new FilterVcf(null, inputVcfFile, outputVcfFile);
    }

    @Test
    public void testConstructor() {
        assertNotNull(new FilterVcf(filter, inputVcfFile, outputVcfFile));
    }
}
