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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static org.nmdp.ngs.variant.vcf.VcfGenotype.builder;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for VcfGenotype.
 */
public final class VcfGenotypeTest {
    private ListMultimap<String, String> empty;
    private ListMultimap<String, String> fields;

    @Before
    public void setUp() {
        empty = ImmutableListMultimap.<String, String>builder().build();
        fields = ImmutableListMultimap.<String, String>builder().put("GT", "1|1").build();
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorNullFields() {
        new VcfGenotype(null);
    }

    @Test
    public void testConstructor() {
        VcfGenotype genotype = new VcfGenotype(fields);
        assertEquals("1|1", genotype.getGt());
        assertEquals(ImmutableList.of("1|1"), genotype.getFields().get("GT"));
    }

    @Test
    public void testBuilder() {
        assertNotNull(builder());
    }

    @Test
    public void testBuilderDefaultGt() {
        assertNull(builder().build().getGt());
    }

    @Test
    public void testBuilderBuildNullGt() {
        assertNull(builder().withGt(null).build().getGt());
    }

    @Test
    public void testBuilderBuildDefaultFields() {
        assertEquals(empty, builder().build().getFields());
    }

    @Test
    public void testBuilderBuildFields() {
        assertEquals(fields, builder().withFields(fields).build().getFields());
    }

    @Test
    public void testBuilderBuildEmptyFields() {
        assertEquals(empty, builder().withFields(empty).build().getFields());
    }

    @Test
    public void testBuilderReset() {
        assertEquals("0|1", builder().withGt("1|1").reset().withGt("0|1").build().getGt());
    }

    @Test
    public void testBuilderBuildWithGt() {
        VcfGenotype genotype = builder().withGt("1|1").build();
        assertEquals("1|1", genotype.getGt());
        assertEquals(fields, genotype.getFields());
    }

    @Test
    public void testBuilderBuildWithFields() {
        VcfGenotype genotype = builder().withFields(fields).build();
        assertEquals("1|1", genotype.getGt());
        assertEquals(fields, genotype.getFields());
    }

    @Test
    public void testBuilderBuildWithGtWithFields() {
        VcfGenotype genotype = builder().withGt("1|1").withFields(fields).build();
        assertEquals("1|1", genotype.getGt());
        assertEquals(fields, genotype.getFields());
    }
}
