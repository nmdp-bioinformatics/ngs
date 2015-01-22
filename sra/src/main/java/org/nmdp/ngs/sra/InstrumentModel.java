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
 * Instrument model.
 */
public enum InstrumentModel {
    ILLUMINA_GENOME_ANALYZER("Illumina Genome Analyzer"),
    ILLUMINA_GENOME_ANALYZER_II("Illumina Genome Analyzer II"),
    ILLUMINA_GENOME_ANALYZER_IIX("Illumina Genome Analyzer IIx"),
    ILLUMINA_HISEQ_2500("Illumina HiSeq 2500"),
    ILLUMINA_HISEQ_2000("Illumina HiSeq 2000"),
    ILLUMINA_HISEQ_1000("Illumina HiSeq 1000"),
    ILLUMINA_MISEQ("Illumina MiSeq"),
    ILLUMINA_HISCANSQ("Illumina HiScanSQ"),
    ROCHE_454_GS("454 GS"),
    ROCHE_454_GS_20("454 GS 20"),
    ROCHE_454_GS_FLX("454 GS FLX"),
    ROCHE_454_GS_FLX_PLUS("454 GS FLX+"),
    ROCHE_454_GS_FLX_TITANIUM("454 GS FLX Titanium"),
    ROCHE_454_GS_JUNIOR("454 GS Junior"),
    AB_SOLID_SYSTEM("AB SOLiD System"),
    AB_SOLID_SYSTEM_2_0("AB SOLiD System 2.0"),
    AB_SOLID_SYSTEM_3_0("AB SOLiD System 3.0"),
    AB_SOLID_3_PLUS_SYSTEM("AB SOLiD 3 Plus System"),
    AB_SOLID_4_SYSTEM("AB SOLiD 4 System"),
    AB_SOLID_4HQ_SYSTEM("AB SOLiD 4hq System"),
    AB_SOLID_PI_SYSTEM("AB SOLiD PI System"),
    AB_5500_GENETIC_ANALYZER("AB 5500 Genetic Analyzer"),
    AB_5500XL_GENETIC_ANALYZER("AB 5500xl Genetic Analyzer"),
    UNSPECIFIED("unspecified");

    private final String description;
    private static final Map<String, InstrumentModel> BY_DESCRIPTION;

    static
    {
        Map<String, InstrumentModel> byDescription = Maps.newHashMap();
        for (InstrumentModel studyType : values()) {
            byDescription.put(studyType.getDescription(), studyType);
        }
        BY_DESCRIPTION = ImmutableMap.copyOf(byDescription);
    }


    /**
     * Create a new instrument model with the specified description.
     *
     * @param description description
     */
    private InstrumentModel(final String description) {
        this.description = description;
    }


    /**
     * Return the description of this instrument model.
     *
     * @return the description of this instrument model
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return a map of instrument models keyed by description.
     *
     * @return a map of instrument models keyed by description
     */
    public static Map<String, InstrumentModel> byDescription() {
        return BY_DESCRIPTION;
    }

    /**
     * Return the instrument model with the specified description, or null if no such instrument model exists.
     *
     * @param description description
     * @return the instrument model with the specified description, or null if no such instrument model exists
     */
    public static InstrumentModel fromDescription(final String description) {
        return BY_DESCRIPTION.get(description);
    }
}
