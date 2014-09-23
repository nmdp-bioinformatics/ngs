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

import org.biojava.bio.seq.Sequence;

import org.biojava.bio.symbol.RangeLocation;

import com.google.common.base.Objects;

/**
 * Locus.
 */
public class Locus extends RangeLocation {
    protected String contig;

    public Locus(final String contig, final int start, final int end) {
        super(start, end);
        this.contig = contig;
    }
  
    public Locus(final String contig, final RangeLocation range) {
        super(range.getMin(), range.getMax());
        this.contig = contig;
    }

    public final int getStart() {
        return getMin();
    }

    public final int getEnd() {
        return getMax();
    }

    public boolean isPointLocation() {
        return this.getStart() == this.getEnd();
    }
  
    public final static class Util {
        // TODO: use SymbolList instead of String to handle IUPAC ambiguity
        /**
         * @param pattern sequence, either inserted or deleted
         */
        private static String rotateLeft(final String pattern) {
            return pattern.substring(pattern.length() - 1) + pattern.substring(0, pattern.length() - 1);
        }

        /**
         * @param pattern sequence, either inserted or deleted
         */
        private static String rotateRight(final String pattern) {
            return pattern.substring(1, pattern.length()) + pattern.substring(0, 1);
        }

        /**
         * @param reference contig
         * @param position of the reference contig immediately preceeding the inserted
         * or deleted pattern. This is to maintain consistency with VCF positioning
         * but is inconsistent with other variant nomenclatures (eg HGVS)
         * @param pattern sequence, either inserted or deleted
         */
        public static int pushLeft(final int position, final String pattern, final String reference) {
            int lower = position;

            String base = reference.substring(lower, lower + 1);
            String rotated = rotateLeft(pattern);

            if ((base + pattern).equals(rotated + base)) {
                lower = pushLeft(position - 1, rotated, reference);
            }

            return lower;
        }

        public static int pushRight(final int position, final String pattern, final String reference) {
            int upper = position;

            String base = reference.substring(upper + 1, upper + 2);
            String rotated = rotateRight(pattern);

            if ((pattern + base).equals(base + rotated)) {
                upper = pushRight(position + 1, rotated, reference);
            }

            return upper;
        }
    }
  
    public String getContig() {
        return contig;
    }

    public Locus intersection(final Locus right) {
        if(!this.contig.equals(right.contig) ) {
            return new Locus("", 0, 0);
        }
    
        if (this.getEnd() == right.getStart() || this.getStart() == right.getEnd()) {
            return new Locus(contig, 0, 0);
        }
    
        if (this.overlaps(right)) {
            return new Locus(contig, (RangeLocation) super.intersection(right));
        }

        return new Locus(contig, 0, 0);
    }

    public Locus union(final Locus right) {
        if (!this.contig.equals(right.contig)) {
            return new Locus(contig, 0, 0);
        }
        else {
            if (this.overlaps(right)) {
                return new Locus(contig, (RangeLocation) super.union(right));
            }
            return new Locus(contig, 0, 0);
        }
    }

    public final int length() {
        return this.getEnd() - this.getStart();
    }

    public boolean isEmpty() {
        return this.getStart() == 0 && this.getEnd() == 0;
    }

    public boolean overlaps(final Locus right) {
        if (!this.contig.equals(right.contig)) {
            return false;
        }
    
        if (this.getEnd() == right.getStart() || this.getStart() == right.getEnd()) {
            return false;
        }

        return super.overlaps(right);
    }

    @Override
    public boolean equals(final Object right) {
        if (!super.equals(right)) {
            return false;
        }

        Locus locus = (Locus) right;
        return this.contig.equals(locus.contig);
    }
    
    @Override
    public int hashCode() {
      return Objects.hashCode(this.getStart(), this.getEnd(), contig);
    }
  
    @Override
    public String toString() {
        return contig + super.toString();
    }
}
