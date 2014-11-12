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
 * Study type.
 */
public enum StudyType {
    WHOLE_GENOME_SEQUENCING("Whole Genome Sequencing"),
    METAGENOMICS("Metagenomics"),
    TRANSCRIPTOME_ANALYSIS("Transcriptome Analysis"),
    RESEQUENCING("Resequencing"),
    EPIGENETICS("Epigenetics"),
    SYNTHETIC_GENOMICS("Synthetic Genomics"),
    FORENSIC_OR_PALEO_GENOMICS("Forensic or Paleo-genomics"),
    GENE_REGULATION_STUDY("Gene Regulation Study"),
    CANCER_GENOMICS("Cancer Genomics"),
    POPULATION_GENOMICS("Population Genomics"),
    RNA_SEQ("RNASeq"),
    EXOME_SEQUENCING("Exome Sequencing"),
    POOLED_CLONE_SEQUENCING("Pooled Clone Sequencing"),
    OTHER("Other");

    private final String description;
    private static final Map<String, StudyType> BY_DESCRIPTION;

    static
    {
        Map<String, StudyType> byDescription = Maps.newHashMap();
        for (StudyType studyType : values()) {
            byDescription.put(studyType.getDescription(), studyType);
        }
        BY_DESCRIPTION = ImmutableMap.copyOf(byDescription);
    }

    /**
     * Create a new study type with the specified description.
     *
     * @param description description
     */
    private StudyType(final String description) {
        this.description = description;
    }


    /**
     * Return the description of this study type.
     *
     * @return the description of this study type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return a map of study types keyed by description.
     *
     * @return a map of study types keyed by description
     */
    public static Map<String, StudyType> byDescription() {
        return BY_DESCRIPTION;
    }

    /**
     * Return the study type with the specified description, or null if no such study type exists.
     *
     * @param description description
     * @return the study type with the specified description, or null if no such study type exists
     */
    public static StudyType fromDescription(final String description) {
        return BY_DESCRIPTION.get(description);
    }
}
