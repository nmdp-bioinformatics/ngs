/*

    ngs-feature  Features.
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
package org.nmdp.ngs.feature;

import java.util.Collections;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.Edit;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.RangeLocation;
import org.biojava.bio.symbol.SymbolList;

import org.biojava.utils.ChangeVetoException;
/**
 * Allele class for extending assembly-specific genomic coordinates (Locus
 * objects) with sequence data.
 */
public final class Allele extends Locus {
    /**
     * Name of the allele.
     */
    String name;

    public enum Lesion {
        UNKNOWN,
        MATCH,
        SUBSTITUTION,
        INSERTION,
        DELETION
    }

    public final SymbolList sequence;
    public final Lesion lesion;

    //System.out.println("new Allele(" + contig + ", " + range + ", " + quality + ", " + filters + ", " + identifiers + ", " + alternate + ", " + annotation + ")");    
    /*
      Allele.Lesion lesion = Allele.Lesion.UNKNOWN;

      if (alternate.seqString().length() > ref.length()) {
        lesion = Allele.Lesion.INSERTION;
      }
      else if(alternate.seqString().length() < ref.length()) {
        lesion = Allele.Lesion.DELETION;
      }
      else if(alternate.seqString().equals(reference)) {
        lesion = Allele.Lesion.MATCH;
      }
      else {
        lesion = Allele.Lesion.SUBSTITUTION;
      }
      
      try {
        Allele allele = new Allele(contig, range, quality, filters, identifiers, alternate);
        alleles.add(allele.equivalent(reference));
      }
      catch(AlleleException exception) {
        throw new ParseException(exception.getMessage());
      }
    }

    return alleles;
    */

    Allele(final String name, final String contig, final int min, final int max, final SymbolList sequence, final Lesion lesion) {
        super(contig, min, max);
        this.name = name;
        this.sequence = sequence; 
        this.lesion = lesion;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static final class Builder {
        private String name, contig, reference;
        private int min, max;
        private SymbolList sequence;
        private Lesion lesion;

        private Builder() {
            // empty
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withContig(final String contig) {
            this.contig = contig;
            return this;
        }

        public Builder withMin(final int min) {
            this.min = min;
            return this;
        }
      
        public Builder withMax(final int max) {
            this.max = max;
            return this;
        }
      
        public Builder withSequence(final SymbolList sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder withLesion(final Lesion lesion) {
            this.lesion = lesion;
            return this;
        }

        public Builder withReference(final String reference) {
            this.reference = reference;
            return this;
        }

        public String getReference() {
            return reference;
        }

        public Builder reset() {
            this.name = "";
            this.contig = "";
            this.reference = "";
            this.min = 0;
            this.max = 0;
            this.sequence = SymbolList.EMPTY_LIST;
            this.lesion = Lesion.UNKNOWN;
            return this;
        }

        public Allele build() throws AlleleException {

            if (sequence == null || sequence == SymbolList.EMPTY_LIST) {
                String gaps = Joiner.on("").join(Collections.nCopies(max - min, "-"));

                try {
                    this.sequence = DNATools.createDNA(gaps);
                }
                catch (IllegalSymbolException ex) {
                    throw new AlleleException("cannot build allele: " + ex.getMessage());
                }
            }

            if (reference != null && !reference.isEmpty()) {
                if (lesion == Lesion.INSERTION || lesion == Lesion.DELETION) {
                    if (max == min) {
                        min = Locus.Util.pushLeft(min, sequence.seqString(), reference);
                        max = Locus.Util.pushRight(max, sequence.seqString(), reference);
                    }
                }
            }

            return new Allele(name, contig, min, max, sequence, lesion);
        }
    }
    
    @Override
    public boolean equals(final Object right) {
        if (!super.equals(right)) {
            return false;
        }

        Allele allele = (Allele) right;
        return allele.lesion == this.lesion && allele.sequence.equals(this.sequence);
    }
    
    @Override
    public int hashCode() {
      return super.hashCode() + Objects.hashCode(name, lesion) + sequence.hashCode();
    }

    public Allele doubleCrossover(final Allele right) throws IllegalSymbolException, IndexOutOfBoundsException, IllegalAlphabetException {
        if (this.overlaps(right)) {
            //System.out.println("this range" + this.toString() + " sequence = " + this.sequence.seqString() + "sequence length = " + sequence.length());

            RangeLocation homologue = this.intersection(right);

            //System.out.println("homologue = " + homologue);

            SymbolList copy = DNATools.createDNA(this.sequence.seqString());
            int length = homologue.getMax() - homologue.getMin();
            int target = homologue.getMin() - right.getMin() + 1;
            int from = homologue.getMin() - this.getMin() + 1;

            //System.out.println("length = " + length);
            //System.out.println("target = " + target);
            //System.out.println("from = " + from);
            //System.out.println("copy = " + copy.seqString());

            try {
                SymbolList replace = right.sequence.subList(target, target + length - 1);
                //System.out.println("replace = " + replace.seqString());
                copy.edit(new Edit(from, length, replace));
            }
            catch(ChangeVetoException e) {
                //System.out.println("CHANGE VETO EXCEPTON" + e.getMessage());
            }

            //System.out.println("CROSSEDOVER SEQUENCE = " + copy.seqString());


            //copy.edit(new Edit());

            //Sequence left = this.sequence.subList(0, homologue.getMin());
            //Sequence middle = right.sequence.subList(homologue.getMin() - right.getMin(), i1);
            return new Allele(this.name, this.contig, this.getMin(), this.getMax(), copy, Lesion.UNKNOWN);
        }
        return new Allele(this.name, this.contig, this.getMin(), this.getMax(), this.sequence, Lesion.UNKNOWN);
    }
    
    public Allele merge(final Allele right, final long minimumOverlap) throws IllegalSymbolException, IndexOutOfBoundsException, IllegalAlphabetException, AlleleException {
      
        Allele.Builder builder = Allele.builder();
        Locus overlap = ((Locus) this).intersection((Locus) right);

        // System.out.println("overlap = " + overlap);
        // System.out.println("overlap.length() " + overlap.length() + " < " + minimumOverlap + "??");

        if (overlap.length() < minimumOverlap) {
            return builder.reset().build();
        }

        Allele bit = builder  
            .withContig(overlap.getContig())
            .withMin(overlap.getMin())
            .withMax(overlap.getMax())
            .withSequence(SymbolList.EMPTY_LIST)
            .withLesion(Lesion.UNKNOWN)
            .build();

        // System.out.println("bit = " + bit + " " + bit.sequence.seqString());

        Allele a = bit.doubleCrossover(right);

        // System.out.println("a = " + a + " " + a.sequence.seqString());

        Allele b = bit.doubleCrossover(this);

        // System.out.println("b = " + b + " " + b.sequence.seqString());   

        if (a.sequence.seqString().equals(b.sequence.seqString())) {
            Locus union = ((Locus) this).union((Locus) right);
            return builder
                .withName(right.getName())
                .withContig(union.getContig())
                .withMin(union.getMin())
                .withMax(union.getMax())
                .withSequence(SymbolList.EMPTY_LIST)
                .withLesion(Lesion.UNKNOWN)
                .build()

                .doubleCrossover(right)
                .doubleCrossover(this);
        }
        return builder.reset().build(); 
    }
    
    public Allele leftHardClip(final String pattern) throws IllegalAlphabetException, AlleleException, IllegalSymbolException {
        int min = this.getMin();

        SymbolList copy = DNATools.createDNA(sequence.seqString());
        while (copy.seqString().startsWith(pattern)) {
            copy.edit(new Edit(1, pattern.length(), SymbolList.EMPTY_LIST));
            min += pattern.length();
        }

        return builder()
            .withContig(this.getContig())
            .withMin(min)
            .withMax(this.getMax())
            .withSequence(copy)
            .withLesion(this.lesion)
            .build();
    }

    public Allele rightHardClip(final String pattern) throws IllegalSymbolException, IndexOutOfBoundsException, IllegalAlphabetException, AlleleException {
        int max = this.getMax();

        // System.out.println("max before = " + max);

        SymbolList copy = DNATools.createDNA(sequence.seqString());
        while (copy.seqString().endsWith(pattern)) {
            copy.edit(new Edit(copy.length() - pattern.length() + 1, pattern.length(), SymbolList.EMPTY_LIST));

            max -= pattern.length();

            // System.out.println("max during " + max);
            // System.out.println("editing copy " + copy.seqString());
        }

        // System.out.println("max after = " + max);
        // System.out.println("edited copy = " + copy.seqString());

        return builder()
            .withContig(this.getContig())
            .withMin(this.getMin())
            .withMax(max)
            .withSequence(copy)
            .withLesion(this.lesion)
            .build();
    }
}
