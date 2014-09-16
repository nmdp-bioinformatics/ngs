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

import java.io.IOException;

/**
 * VCF parse listener.
 */
public interface VcfParseListener {

    /**
     * Notify this parse listener of the line number.
     *
     * @param lineNumber line number
     * @throws IOException if an I/O error occurs
     */
    void lineNumber(long lineNumber) throws IOException;

    /**
     * Notify this parse listener of a meta string.
     *
     * @param meta meta string
     * @throws IOException if an I/O error occurs
     */
    void meta(String meta) throws IOException;

    /**
     * Notify this parse listener of sample strings.
     *
     * @param samples sample strings
     * @throws IOException if an I/O error occurs
     */
    void samples(String... samples) throws IOException;

    /**
     * Notify this parse listener of a chrom string.
     *
     * @param chrom chrom string
     * @throws IOException if an I/O error occurs
     */
    void chrom(String chrom) throws IOException;

    /**
     * Notify this parse listener of a position.
     *
     * @param pos position
     * @throws IOException if an I/O error occurs
     */
    void pos(long pos) throws IOException;

    /**
     * Notify this parse listener of id strings.
     *
     * @param id id strings
     * @throws IOException if an I/O error occurs
     */
    void id(String... id) throws IOException;

    /**
     * Notify this parse listener of a ref string.
     *
     * @param ref ref string
     * @throws IOException if an I/O error occurs
     */
    void ref(String ref) throws IOException;

    /**
     * Notify this parse listener of alt strings.
     *
     * @param alt alt strings
     * @throws IOException if an I/O error occurs
     */
    void alt(String... alt) throws IOException;

    /**
     * Notify this parse listener of a qual score.
     *
     * @param qual qual score
     * @throws IOException if an I/O error occurs
     */
    void qual(double qual) throws IOException;

    /**
     * Notify this parse listener of filter strings.
     *
     * @param filter filter strings
     * @throws IOException if an I/O error occurs
     */
    void filter(String... filter) throws IOException;

    /**
     * Notify this parse listener of info fields.
     *
     * @param infoId info id
     * @param values values
     * @throws IOException if an I/O error occurs
     */
    void info(String infoId, String... values) throws IOException;

    /**
     * Notify this parse listener of format strings.
     *
     * @param format format strings
     * @throws IOException if an I/O error occurs
     */
    void format(String... format) throws IOException;

    /**
     * Notify this parse listener of genotype fields.
     *
     * @param sampleId sample id
     * @param formatId format id
     * @param values values
     * @throws IOException if an I/O error occurs
     */
    void genotype(String sampleId, String formatId, String... values) throws IOException;

    /**
     * Notify this parse listener a record is complete.
     *
     * @return true to continue parsing
     * @throws IOException if an I/O error occurs
     */
    boolean complete() throws IOException;
}
