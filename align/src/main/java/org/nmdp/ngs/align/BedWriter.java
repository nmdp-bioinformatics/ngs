/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintWriter;

/**
 * BED writer.
 */
public final class BedWriter {

    /**
     * Private no-arg constructor.
     */
    private BedWriter() {
        // empty
    }


    /**
     * Write the specified BED record with the specified print writer.
     *
     * @param record BED record to write, must not be null
     * @param writer print writer to write BED record with, must not be null
     */
    public static void write(final BedRecord record, final PrintWriter writer) {
        checkNotNull(record);
        checkNotNull(writer);
        writer.println(record.toString());
    }

    /**
     * Write zero or more BED records with the specified print writer.
     *
     * @param records zero or more BED records to write, must not be null
     * @param writer print writer to write BED records with, must not be null
     */
    public static void write(final Iterable<BedRecord> records, final PrintWriter writer) {
        checkNotNull(records);
        checkNotNull(writer);
        for (BedRecord record : records) {
            writer.println(record.toString());
        }
    }
}
