/*

    ngs-sra  Mapping for SRA submission XSDs.
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
package org.nmdp.ngs.sra;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Library source.
 */
public enum LibrarySource {
    GENOMIC("GENOMIC", "Genomic DNA (includes PCR products from genomic DNA)"),
    TRANSCRIPTOMIC("TRANSCRIPTOMIC", "Transcription products or non genomic DNA (EST, cDNA, RT-PCR, screened libraries)"),
    METAGENOMIC("METAGENOMIC", "Mixed material from metagenome"),
    METATRANSCRIPTOMIC("METATRANSCRIPTOMIC", "Transcription products from community targets"),
    SYNTHETIC("SYNTHETIC", "Synthetic DNA"),
    VIRAL_RNA("VIRAL RNA", "Viral RNA"),
    OTHER("OTHER", "Other, unspecified, or unknown library source material");

    private final String description;
    private final String definition;
    private static final Map<String, LibrarySource> BY_DESCRIPTION;

    static
    {
        Map<String, LibrarySource> byDescription = Maps.newHashMap();
        for (LibrarySource studyType : values()) {
            byDescription.put(studyType.getDescription(), studyType);
        }
        BY_DESCRIPTION = ImmutableMap.copyOf(byDescription);
    }


    /**
     * Create a new library source with the specified description and definition.
     *
     * @param description description
     * @param definition definition
     */
    private LibrarySource(final String description, final String definition) {
        this.description = description;
        this.definition = definition;
    }


    /**
     * Return the description of this library source.
     *
     * @return the description of this library source
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the definition of this library source.
     *
     * @return the definition of this library source
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Return a map of library sources keyed by description.
     *
     * @return a map of library sources keyed by description
     */
    public static Map<String, LibrarySource> byDescription() {
        return BY_DESCRIPTION;
    }

    /**
     * Return the library source with the specified description, or null if no such library source exists.
     *
     * @param description description
     * @return the library source with the specified description, or null if no such library source exists
     */
    public static LibrarySource fromDescription(final String description) {
        return BY_DESCRIPTION.get(description);
    }
}
