/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static org.nmdp.ngs.align.Genewise.genewiseExons;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for Genewise.
 */
public final class GenewiseTest {
    private File aminoAcidHmm2File;
    private File genomicDnaFastaFile;

    @Before
    public void setUp() throws Exception {
        aminoAcidHmm2File = File.createTempFile("genewiseTest", ".hmm2");
        genomicDnaFastaFile = File.createTempFile("genewiseTest", ".fasta");
    }

    @After
    public void tearDown() throws Exception {
        aminoAcidHmm2File.delete();
        genomicDnaFastaFile.delete();
    }

    @Test(expected=NullPointerException.class)
    public void testGenewiseExonsNullAminoAcidHmm2File() throws Exception {
        genewiseExons(null, genomicDnaFastaFile);
    }

    @Test(expected=NullPointerException.class)
    public void testGenewiseExonsNullGenomicDnaFastaFile() throws Exception {
        genewiseExons(aminoAcidHmm2File, null);
    }
}
