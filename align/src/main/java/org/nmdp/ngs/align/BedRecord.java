/*

    ngs-align  Sequence alignment.
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
package org.nmdp.ngs.align;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Objects;

import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import com.google.common.collect.Range;

import com.google.common.primitives.Longs;

import javax.annotation.concurrent.Immutable;

/**
 * BED record.
 *
 * <p>Supports the same BED formats as <a href="http://bedtools.readthedocs.org/en/latest/content/general-usage.html">bedtools2</a>.
 * <ul>
 *   <li>BED3: A BED file where each feature is described by chrom, start, and end.<br/>
 *       For example: <code>chr1          11873   14409</code></li>
 *   <li>BED4: A BED file where each feature is described by chrom, start, end, and name.<br/>
 *       For example: <code>chr1  11873  14409  uc001aaa.3</code></li>
 *   <li>BED5: A BED file where each feature is described by chrom, start, end, name, and score.<br/>
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0</code></li>
 *   <li>BED6: A BED file where each feature is described by chrom, start, end, name, score, and strand.<br/>
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0 +</code></li>
 *   <li>BED12: A BED file where each feature is described by all twelve columns listed above.<br/>
 *       For example: <code>chr1 11873 14409 uc001aaa.3 0 + 11873 11873 0 3 354,109,1189, 0,739,1347,</code></li>
 * </ul></p>
 */
@Immutable
public final class BedRecord {
    private final Format format;
    private final String chrom;
    private final long start;
    private final long end;
    private final String name;
    private final String score;
    private final String strand;
    private final long thickStart;
    private final long thickEnd;
    private final String itemRgb;
    private final int blockCount;
    private final long[] blockSizes;
    private final long[] blockStarts;
    private final int hashCode;

    /** BED format. */
    enum Format { BED3, BED4, BED5, BED6, BED12 };

    /** Empty long array. */
    private static final long[] EMPTY = new long[0];

    /** R,G,B pattern. */
    private static final Pattern RGB = Pattern.compile("^[0-9]+,[0-9]+,[0-9]+$");


    /**
     * Create a new BED record.
     *
     * @param format format, must not be null
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name
     * @param score score
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     * @param thickStart thick start, must be at least zero
     * @param thickEnd thick end, must be at least zero and greater than or equal to thick start
     * @param itemRgb item RGB, if present, must be in R,G,B format, e.g. <code>255,0,0</code>
     * @param blockCount block count, must be at least zero
     * @param blockSizes block sizes, must be the same length as block count
     * @param blockStarts block starts, must be the same length as block count
     */
    private BedRecord(final Format format, final String chrom, final long start, final long end, final String name, final String score, final String strand,
                     final long thickStart, final long thickEnd, final String itemRgb, final int blockCount, final long[] blockSizes, final long[] blockStarts) {

        checkNotNull(format);
        checkNotNull(chrom);
        checkArgument(start >= 0L, "start must be at least zero");
        checkArgument(end >= 0L, "end must be at least zero");
        checkArgument(end >= start, "end must be greater than or equal to start");
        checkArgument(thickStart >= 0L, "thickStart must be at least zero");
        checkArgument(thickEnd >= 0L, "thickEnd must be at least zero");
        checkArgument(thickEnd >= thickStart, "thickEnd must be greater than or equal to thickStart");
        checkArgument(blockCount >= 0, "blockCount must be at least zero");
        checkNotNull(blockSizes);
        checkNotNull(blockStarts);
        checkArgument(blockSizes.length == blockCount, "blockSizes must be the same length as blockCount");
        checkArgument(blockStarts.length == blockCount, "blockStarts must be the same length as blockCount");

        if (strand != null) {
            checkArgument("-".equals(strand) || "+".equals(strand), "if present, strand must be either - or +");
        }
        /*

          appears bedtools2 doesn't enforce this format restriction

        if (itemRgb != null && !RGB.matcher(itemRgb).matches()) {
            throw new IllegalArgumentException("if present, itemRgb must be in R,G,B format, e.g. 255,0,0");
        }
        */

        this.format = format;
        this.chrom = chrom;
        this.start = start;
        this.end = end;
        this.name = name;
        this.score = score;
        this.strand = strand;
        this.thickStart = thickStart;
        this.thickEnd = thickEnd;
        this.itemRgb = itemRgb;
        this.blockCount = blockCount;
        if (blockCount > 0) {
            this.blockSizes = (long[]) blockSizes.clone();
            this.blockStarts = (long[]) blockStarts.clone();
        }
        else {
            this.blockSizes = blockSizes;
            this.blockStarts = blockStarts;
        }

        hashCode = Objects.hash(this.format, this.chrom, this.start, this.end, this.name, this.score, this.strand, this.thickStart, this.thickEnd, this.itemRgb, this.blockCount, this.blockSizes, this.blockStarts);
    }

    /**
     * Create a new BED3 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     */
    public BedRecord(final String chrom, final long start, final long end) {
        this(Format.BED3, chrom, start, end, null, null, null, 0L, 0L, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED4 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name
     */
    public BedRecord(final String chrom, final long start, final long end, final String name) {
        this(Format.BED4, chrom, start, end, name, null, null, 0L, 0L, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED5 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name
     * @param score score
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final String score) {
        this(Format.BED5, chrom, start, end, name, score, null, 0L, 0L, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED6 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name
     * @param score score
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final String score, final String strand) {
        this(Format.BED6, chrom, start, end, name, score, strand, 0L, 0L, null, 0, EMPTY, EMPTY);
    }

    /**
     * Create a new BED12 record.
     *
     * @param chrom chrom, must not be null
     * @param start start, must be at least zero
     * @param end end, must be at least zero, and greater than or equal to start
     * @param name name
     * @param score score
     * @param strand strand, if present must be either <code>-</code> or <code>+</code>
     * @param thickStart thick start, must be at least zero
     * @param thickEnd thick end, must be at least zero and greater than or equal to thick start
     * @param itemRgb item RGB, if present, must be in R,G,B format, e.g. <code>255,0,0</code>
     * @param blockCount block count, must be at least zero
     * @param blockSizes block sizes, must be the same length as block count
     * @param blockStarts block starts, must be the same length as block count
     */
    public BedRecord(final String chrom, final long start, final long end, final String name, final String score, final String strand,
                     final long thickStart, final long thickEnd, final String itemRgb, final int blockCount, final long[] blockSizes, final long[] blockStarts) {
        this(Format.BED12, chrom, start, end, name, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
    }


    /**
     * Return the chrom for this BED record.
     *
     * @return the chrom for this BED record
     */
    public String chrom() {
        return chrom;
    }

    /**
     * Return the start for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the start for this BED record in 0-based coordinate system, closed open range
     */
    public long start() {
        return start;
    }

    /**
     * Return the end for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the end for this BED record in 0-based coordinate system, closed open range
     */
    public long end() {
        return end;
    }

    /**
     * Return the name for this BED record, if any
     *
     * @return the name for this BED record, if any
     */
    public String name() {
        return name;
    }

    /**
     * Return the score for this BED record, if any
     *
     * @return the score for this BED record, if any
     */
    public String score() {
        return score;
    }

    /**
     * Return the strand for this BED record, if any
     *
     * @return the strand for this BED record, if any
     */
    public String strand() {
        return strand;
    }

    /**
     * Return the thick start for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the thick start for this BED record in 0-based coordinate system, closed open range
     */
    public long thickStart() {
        return thickStart;
    }

    /**
     * Return the thick end for this BED record in 0-based coordinate system, closed open range.
     *
     * @return the thick end for this BED record in 0-based coordinate system, closed open range
     */
    public long thickEnd() {
        return thickEnd;
    }

    /**
     * Return the item RGB for this BED record, if any.
     *
     * @return the item RGB for this BED record, if any
     */
    public String itemRgb() {
        return itemRgb;
    }

    /**
     * Return the block count for this BED record.
     *
     * @return the block count for this BED record
     */
    public int blockCount() {
        return blockCount;
    }

    /**
     * Return the block sizes for this BED record.
     *
     * @return the block sizes for this BED record
     */
    public long[] blockSizes() {
        return (long[]) blockSizes.clone();
    }

    /**
     * Return the block starts for this BED record.
     *
     * @return the block starts for this BED record
     */
    public long[] blockStarts() {
        return (long[]) blockStarts.clone();
    }

    /**
     * Return the format of this BED record.
     *
     * @return the format of this BED record
     */
    public Format format() {
        return format;
    }

    /**
     * Return this BED record as a 0-based coordinate system, closed open range.
     *
     * @return this BED record as a 0-based coordinate system, closed open range
     */
    public Range<Long> toRange() {
        return Range.closedOpen(start, end);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BedRecord)) {
            return false;
        }
        BedRecord bedRecord = (BedRecord) o;

        return Objects.equals(format, bedRecord.format)
            && Objects.equals(chrom, bedRecord.chrom)
            && Objects.equals(start, bedRecord.start)
            && Objects.equals(end, bedRecord.end)
            && Objects.equals(name, bedRecord.name)
            && Objects.equals(score, bedRecord.score)
            && Objects.equals(strand, bedRecord.strand)
            && Objects.equals(thickStart, bedRecord.thickStart)
            && Objects.equals(thickEnd, bedRecord.thickEnd)
            && Objects.equals(itemRgb, bedRecord.itemRgb)
            && Objects.equals(blockCount, bedRecord.blockCount)
            && Objects.equals(blockSizes, bedRecord.blockSizes)
            && Objects.equals(blockStarts, bedRecord.blockStarts);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(chrom);
        sb.append("\t");
        sb.append(start);
        sb.append("\t");
        sb.append(end);
        switch (format) {
            case BED3:
                break;
            case BED4:
                sb.append("\t");
                sb.append(name);
                break;
            case BED5:
                sb.append("\t");
                sb.append(name);
                sb.append("\t");
                sb.append(score);
                break;
            case BED6:
                sb.append("\t");
                sb.append(name);
                sb.append("\t");
                sb.append(score);
                sb.append("\t");
                sb.append(strand);
                break;
            case BED12:
                sb.append("\t");
                sb.append(name);
                sb.append("\t");
                sb.append(score);
                sb.append("\t");
                sb.append(strand);
                sb.append("\t");
                sb.append(thickStart);
                sb.append("\t");
                sb.append(thickEnd);
                sb.append("\t");
                sb.append(itemRgb);
                sb.append("\t");
                sb.append(blockCount);
                sb.append("\t");
                sb.append(Joiner.on(",").join(Longs.asList(blockSizes)));
                sb.append("\t");
                sb.append(Joiner.on(",").join(Longs.asList(blockStarts)));
                break;
            default:
                break;
        }
        return sb.toString();
    }

    private static long[] parseLongArray(final String value) {
        List<String> tokens = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(value);
        long[] longs = new long[tokens.size()];
        for (int i = 0, size = tokens.size(); i < size; i++) {
            longs[i] = Long.parseLong(tokens.get(i));
        }
        return longs;
    }

    /**
     * Return a new BED record parsed from the specified value.
     *
     * @param value value to parse
     * @return a new BED record parsed from the specified value
     * @throws IllegalArgumentException if the value is not valid BED[3,4,5,6,12] format
     * @throws NullPointerException if a required field is missing
     * @throws NumberFormatException if a long valued field cannot be parsed as a long 
     */
    public static BedRecord valueOf(final String value) {
        checkNotNull(value);
        List<String> tokens = Splitter.on("\t").trimResults().splitToList(value);
        if (tokens.size() < 3) {
            throw new IllegalArgumentException("value must have at least three fields (chrom, start, end)");
        }
        String chrom = tokens.get(0);
        long start = Long.parseLong(tokens.get(1));
        long end = Long.parseLong(tokens.get(2));
        if (tokens.size() == 3) {
            return new BedRecord(chrom, start, end);
        }
        else {
            String name = tokens.get(3);
            if (tokens.size() == 4) {
                return new BedRecord(chrom, start, end, name);
            }
            else {
                String score = tokens.get(4);
                if (tokens.size() == 5) {
                    return new BedRecord(chrom, start, end, name, score);
                }
                else {
                    String strand = tokens.get(5);
                    if (tokens.size() == 6) {
                        return new BedRecord(chrom, start, end, name, score, strand);
                    }
                    if (tokens.size() != 12) {
                        throw new IllegalArgumentException("value is not in BED3, BED4, BED5, BED6 or BED12 format");
                    }
                    long thickStart = Long.parseLong(tokens.get(6));
                    long thickEnd = Long.parseLong(tokens.get(7));
                    String itemRgb = tokens.get(8);
                    int blockCount = Integer.parseInt(tokens.get(9));
                    long[] blockSizes = parseLongArray(tokens.get(10));
                    long[] blockStarts = parseLongArray(tokens.get(11));

                    return new BedRecord(chrom, start, end, name, score, strand, thickStart, thickEnd, itemRgb, blockCount, blockSizes, blockStarts);
                }
            }
        }
    }
}
