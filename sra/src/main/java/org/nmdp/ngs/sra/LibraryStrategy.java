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
 * Library strategy.
 */
public enum LibraryStrategy {
    WGS("WGS", "Random sequencing of the whole genome"),
    WGA("WGA", "Whole genome amplification to replace some instances of random"),
    WXS("WXS", "Random sequencing of exonic regions selected from the genome"),
    RNA_SEQ("RNA-SEQ", "Random sequencing of whole transcriptome"),
    MIRNA_SEQ("miRNA-Seq", "Micro RNA and other small non-coding RNA sequencing"),
    NCRNA_SEQ("ncRNA-Seq", "Non-coding RNA"),
    WCS("WCS", "Random sequencing of a whole chromosome or other replicon isolated from a genome"),
    CLONE("CLONE", "Genomic clone based (hierarchical) sequencing"),
    POOLCLONE("POOLCLONE", "Shotgun of pooled clones (usually BACs and Fosmids)"),
    AMPLICON("AMPLICON", "Sequencing of overlapping or distinct PCR or RT-PCR products"),
    CLONEEND("CLONEEND", "Clone end (5', 3', or both) sequencing"),
    FINISHING("FINISHING", "Sequencing intended to finish (close) gaps in existing coverage"),
    CHIP_SEQ("ChIP-Seq", "Direct sequencing of chromatin immunoprecipitates"),
    MNASE_SEQ("MNase-Seq", "Direct sequencing following MNase digestion"),
    DNASE_HYPERSENSITIVITY("DNase-Hypersensitivity", "Sequencing of hypersensitive sites, or segments of open chromatin that are more readily cleaved by DNaseI"),
    BISULFITE_SEQ("Bisulfite-Seq", "Sequencing following treatment of DNA with bisulfite to convert cytosine residues to uracil depending on methylation status"),
    EST("EST", "Single pass sequencing of cDNA templates"),
    FL_CDNA("FL-cDNA", "Full-length sequencing of cDNA templates"),
    CTS("CTS", "Concatenated tag sequencing"),
    MRE_SEQ("MRE-Seq", "Methylation-Sensitive Restriction Enzyme Sequencing strategy"),
    MEDIP_SEQ("MeDIP-Seq", "Methylated DNA Immunoprecipitation Sequencing strategy"),
    MDB_SEQ("MDB-Seq", "Direct sequencing of methylated fractions sequencing strategy"),
    TN_SEQ("Tn-Seq", "Gene fitness determination through transposon seeding"),
    VALIDATION("VALIDATION", "Validation"),
    //FAIRE_SEQ("FAIRE-Seq", "Formaldehyde-Assisted Isolation of Regulatory Elements"),
    FAIRE_SEQ("FAIRE-seq", "Formaldehyde-Assisted Isolation of Regulatory Elements"),
    SELEX("SELEX", "Systematic Evolution of Ligands by EXponential enrichment (SELEX) is an in vitro strategy to analyze RNA sequences that perform an activity of interest, most commonly high affinity binding to a ligand"),
    RIP_SEQ("RIP-Seq", "Direct sequencing of RNA immunoprecipitates (includes CLIP-Seq, HITS-CLIP and PAR-CLI)"),
    CHIA_PET("ChiA-PET", "Direct sequencing of proximity-ligated chromatin immunoprecipitates"),
    OTHER("OTHER", "Library strategy not listed");

    private final String description;
    private final String definition;
    private static final Map<String, LibraryStrategy> BY_DESCRIPTION;

    static
    {
        Map<String, LibraryStrategy> byDescription = Maps.newHashMap();
        for (LibraryStrategy studyType : values()) {
            byDescription.put(studyType.getDescription(), studyType);
        }
        BY_DESCRIPTION = ImmutableMap.copyOf(byDescription);
    }


    /**
     * Create a new library strategy with the specified description and definition.
     *
     * @param description description
     * @param definition definition
     */
    private LibraryStrategy(final String description, final String definition) {
        this.description = description;
        this.definition = definition;
    }


    /**
     * Return the description of this library strategy.
     *
     * @return the description of this library strategy
     */
    public String getDescription() {
        return description;
    }

    /**
     * Return the definition of this library strategy.
     *
     * @return the definition of this library strategy
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Return a map of library strategies keyed by description.
     *
     * @return a map of library strategies keyed by description
     */
    public static Map<String, LibraryStrategy> byDescription() {
        return BY_DESCRIPTION;
    }

    /**
     * Return the library strategy with the specified description, or null if no such library strategy exists.
     *
     * @param description description
     * @return the library strategy with the specified description, or null if no such library strategy exists
     */
    public static LibraryStrategy fromDescription(final String description) {
        return BY_DESCRIPTION.get(description);
    }
}
