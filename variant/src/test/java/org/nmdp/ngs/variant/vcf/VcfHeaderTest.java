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

import static org.junit.Assert.assertEquals;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfHeader.
 */
public final class VcfHeaderTest {
    private String fileFormat;
    private List<String> meta;

    @Before
    public void setUp() {
        fileFormat = "VCFv4.2";
        meta = ImmutableList.of("##fileformat=VCFv4.2");
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFileFormat() {
        new VcfHeader(null, meta);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullMeta() {
        new VcfHeader(fileFormat, null);
    }

    @Test
    public void testConstructor() {
        VcfHeader header = new VcfHeader(fileFormat, meta);
        assertEquals("VCFv4.2", header.getFileFormat());
        assertEquals(meta, header.getMeta());
    }

    @Test
    public void testBuilder() {
        VcfHeader header = VcfHeader.builder().withFileFormat(fileFormat).withMeta(meta).build();
        assertEquals("VCFv4.2", header.getFileFormat());
        assertEquals(meta, header.getMeta());
    }
}
