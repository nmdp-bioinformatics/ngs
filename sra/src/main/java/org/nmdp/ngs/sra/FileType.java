/*

    ngs-sra  Mapping for SRA submission XSDs.
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
package org.nmdp.ngs.sra;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * File type.
 */
public enum FileType {
    SRF("srf"),
    SFF("sff"),
    FASTQ("fastq"),
    CRAM("cram"),
    BAM("bam"),
    ILLUMINA_NATIVE_QSEQ("Illumina_native_qseq"),
    ILLUMINA_NATIVE_SCARF("Illumina_native_scarf"),
    ILLUMINA_NATIVE_FASTQ("Illumina_native_fastq"),
    SOLID_NATIVE_CSFASTA("SOLiD_native_csfasta"),
    SOLID_NATIVE_QUAL("SOLiD_native_qual"),
    PACBIO_HDF5("PacBio_HDF5"),
    COMPLETE_GENOMICS_NATIVE("CompleteGenomics_native");

    private final String description;
    private static final Map<String, FileType> BY_DESCRIPTION;

    static
    {
        Map<String, FileType> byDescription = Maps.newHashMap();
        for (FileType fileType : values()) {
            byDescription.put(fileType.getDescription(), fileType);
        }
        BY_DESCRIPTION = ImmutableMap.copyOf(byDescription);
    }


    /**
     * Create a new file type with the specified description.
     *
     * @param description description
     */
    private FileType(final String description) {
        this.description = description;
    }


    /**
     * Return the description of this file type.
     *
     * @return the description of this file type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return a map of file types keyed by description.
     *
     * @return a map of file types keyed by description
     */
    public static Map<String, FileType> byDescription() {
        return BY_DESCRIPTION;
    }

    /**
     * Return the file type with the specified description, or null if no such file type exists.
     *
     * @param description description
     * @return the file type with the specified description, or null if no such file type exists
     */
    public static FileType fromDescription(final String description) {
        return BY_DESCRIPTION.get(description);
    }
}
