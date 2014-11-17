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

import java.util.List;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Splitter;

/**
 * High-scoring segment pair (HSP).
 */
@Immutable
public final class HighScoringPair {
    private final String source;
    private final String target;
    private final double percentIdentity;
    private final long alignmentLength;
    private final int mismatches;
    private final int gapOpens;
    private final long sourceStart;
    private final long sourceEnd;
    private final long targetStart;
    private final long targetEnd;
    private final double evalue;
    private final double bitScore;


    /**
     * Create a new high-scoring segment pair (HSP).
     *
     * @param source source
     * @param target target
     * @param percentIdentity percent identity
     * @param alignmentLength alignment length
     * @param mismatches mismatches
     * @param gapOpens gap opens
     * @param sourceStart source start
     * @param sourceEnd source end
     * @param targetStart target start
     * @param targetEnd target end
     * @param evalue evalue
     * @param bitScore bit score
     */
    HighScoringPair(final String source,
                    final String target,
                    final double percentIdentity,
                    final long alignmentLength,
                    final int mismatches,
                    final int gapOpens,
                    final long sourceStart,
                    final long sourceEnd,
                    final long targetStart,
                    final long targetEnd,
                    final double evalue,
                    final double bitScore) {

        this.source = source;
        this.target = target;
        this.percentIdentity = percentIdentity;
        this.alignmentLength = alignmentLength;
        this.mismatches = mismatches;
        this.gapOpens = gapOpens;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.targetStart = targetStart;
        this.targetEnd = targetEnd;
        this.evalue = evalue;
        this.bitScore = bitScore;
    }


    /**
     * Return the source for this high-scoring segment pair (HSP).
     *
     * @return the source for this high-scoring segment pair (HSP)
     */
    public String source() {
        return source;
    }

    /**
     * Return the target for this high-scoring segment pair (HSP).
     *
     * @return the target for this high-scoring segment pair (HSP)
     */
    public String target() {
        return target;
    }

    /**
     * Return the percent identity for this high-scoring segment pair (HSP).
     *
     * @return the percent identity for this high-scoring segment pair (HSP)
     */
    public double percentIdentity() {
        return percentIdentity;
    }

    /**
     * Return the alignment length for this high-scoring segment pair (HSP).
     *
     * @return the alignment length for this high-scoring segment pair (HSP)
     */
    public long alignmentLength() {
        return alignmentLength;
    }

    /**
     * Return the mismatches for this high-scoring segment pair (HSP).
     *
     * @return the mismatches for this high-scoring segment pair (HSP)
     */
    public int mismatches() {
        return mismatches;
    }

    /**
     * Return the gap opens for this high-scoring segment pair (HSP).
     *
     * @return the gap opens for this high-scoring segment pair (HSP)
     */
    public int gapOpens() {
        return gapOpens;
    }

    /**
     * Return the source start for this high-scoring segment pair (HSP).
     *
     * @return the source start for this high-scoring segment pair (HSP)
     */
    public long sourceStart() {
        return sourceStart;
    }

    /**
     * Return the source end for this high-scoring segment pair (HSP).
     *
     * @return the source end for this high-scoring segment pair (HSP)
     */
    public long sourceEnd() {
        return sourceEnd;
    }

    /**
     * Return the target start for this high-scoring segment pair (HSP).
     *
     * @return the target start for this high-scoring segment pair (HSP)
     */
    public long targetStart() {
        return targetStart;
    }

    /**
     * Return the target end for this high-scoring segment pair (HSP).
     *
     * @return the target end for this high-scoring segment pair (HSP)
     */
    public long targetEnd() {
        return targetEnd;
    }

    /**
     * Return the evalue for this high-scoring segment pair (HSP).
     *
     * @return the evalue for this high-scoring segment pair (HSP)
     */
    public double evalue() {
        return evalue;
    }

    /**
     * Return the bit score for this high-scoring segment pair (HSP).
     *
     * @return the bit score for this high-scoring segment pair (HSP)
     */
    public double bitScore() {
        return bitScore;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(source);
        sb.append("\t");
        sb.append(target);
        sb.append("\t");
        sb.append(percentIdentity);
        sb.append("\t");
        sb.append(alignmentLength);
        sb.append("\t");
        sb.append(mismatches);
        sb.append("\t");
        sb.append(gapOpens);
        sb.append("\t");
        sb.append(sourceStart);
        sb.append("\t");
        sb.append(sourceEnd);
        sb.append("\t");
        sb.append(targetStart);
        sb.append("\t");
        sb.append(targetEnd);
        sb.append("\t");
        sb.append(evalue);
        sb.append("\t");
        sb.append(bitScore);
        return sb.toString();
    }

    /**
     * Return a new high scoring pair parsed from the specified value.
     *
     * @param value value to parse, must not be null
     * @return a new high scoring pair parsed from the specified value
     * @throws IllegalArgumentException if the value is not valid high scoring pair format
     * @throws NumberFormatException if a number valued field cannot be parsed as a number
     */
    public static HighScoringPair valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").trimResults().splitToList(value);
        if (tokens.size() != 12) {
            throw new IllegalArgumentException("value must have twelve fields");
        }
        String source = tokens.get(0).trim();
        String target = tokens.get(1).trim();
        double percentIdentity = Double.parseDouble(tokens.get(2).trim());
        long alignmentLength = Long.parseLong(tokens.get(3).trim());
        int mismatches = Integer.parseInt(tokens.get(4).trim());
        int gapOpens = Integer.parseInt(tokens.get(5).trim());
        long sourceStart = Long.parseLong(tokens.get(6).trim());
        long sourceEnd = Long.parseLong(tokens.get(7).trim());
        long targetStart = Long.parseLong(tokens.get(8).trim());
        long targetEnd = Long.parseLong(tokens.get(9).trim());
        double evalue = Double.parseDouble(tokens.get(10).trim());
        double bitScore = Double.parseDouble(tokens.get(11).trim());

        return new HighScoringPair(source, target, percentIdentity, alignmentLength, mismatches, gapOpens, sourceStart, sourceEnd, targetStart, targetEnd, evalue, bitScore);
    }
}

