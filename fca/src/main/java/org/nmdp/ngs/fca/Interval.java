/*

    ngs-fca  Formal concept analysis for genomics.
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
package org.nmdp.ngs.fca;

import com.google.common.annotations.Beta;

import com.google.common.base.Objects;

import com.google.common.collect.Range;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BoundType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.RangeSet;

/**
 * For spatial or temporal intervals defined by lower and upper comparable
 * endpoints. This class wraps com.google.common.collect.Range and is endowed
 * with a dimension to support programmatic frameworks for intervals in
 * multidimensional space or time.
 * @param <C> comparable endpoint type
 */
public class Interval<C extends Comparable> implements Partial<Interval<C>>,
                                                       Comparable<Interval<C>> {
    private int dimension;
    private Range<C> range;
    
    /**
     * The one and only dimensionless interval with no values
     */
    public final static Interval NULL = new Interval<>();
    
    /**
     * The one and only dimensionless interval with every value
     */
    public final static Interval MAGIC = new Interval<>(Range.all());
    
    /**
     * Experimental class to contain the difference (complement) of closed sets.
     * @param <C> comparable type
     * @see Interval#minus(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Complement_(set_theory)">
     * complement </a>
     */
    @Beta
    public final class Difference<C extends Comparable> {
        private int dimension;
        private ImmutableRangeSet<C> ranges;
        
        /**
         * Construct a new Difference, which may have zero, one, or two non-null
         * , nonempty, non-overlapping interval ranges in the same dimension.
         * @param that interval
         * @param other interval
         * @see ImmutableRangeSet#add(com.google.common.collect.Range) 
         */
        private Difference(final Interval<C> that, final Interval<C> other) {
            ImmutableRangeSet.Builder builder = ImmutableRangeSet.builder();
            
            if(that.dimension == other.dimension) {
                dimension = that.dimension;
            } else {
                dimension = 0;
            }
            
            if(that.overlaps(other)) {
                dimension = that.getDimension();
                builder.add(that.coalesce(other).toRange());
            } else {
                if(!that.hasNone() && !that.range.isEmpty()) {
                    dimension = that.dimension;
                    builder.add(that.range);
                }
                if(!other.hasNone() && !other.range.isEmpty()) {
                    if(dimension == 0) {
                        dimension = other.dimension;
                    }
                    if(dimension == other.dimension) {
                        builder.add(other.range);
                    }
                }
            }
            ranges = builder.build();
        }
        
        public int getDimension() {
            return dimension;
        }
        
        public ImmutableRangeSet<C> getRanges() {
            return ranges;
        }
        
        @Override
        public String toString() {
            return dimension + ":" + ranges.toString();
        }
    }
    
    /**
     * Construct the null interval.
     * @see #NULL
     */
    private Interval() {
        dimension = 0;
        range = null;
    }
    
    /**
     * Construct an interval with dimension but no values.
     * @param dimension as specified
     */
    private Interval(int dimension) {
        this();
        this.dimension = dimension;
    }
    
    /**
     * Construct an interval with no dimension but specified values.
     * @param range as specified
     * @see #MAGIC
     */
    private Interval(final Range<C> range) {
        this();
        this.range = range;
    }
    
    /**
     * Construct an interval with specified dimension and values.
     * @param dimension as specified (at least zero)
     * @param range as specified (cannot be null)
     */
    public Interval(int dimension, final Range<C> range) {
        checkArgument(Range.atLeast(0).contains(dimension));
        checkNotNull(range);
        
        this.dimension = dimension;
        this.range = range;
    }
    
    private BoundType reverse(final BoundType type) {
        return this.range.upperBoundType() == BoundType.OPEN ? BoundType.CLOSED : BoundType.OPEN;
    }
    
    /**
     * Get the partial order between two intervals.
     * @param that interval
     * @return partial order enumerated type
     */
    @Override
    public Partial.Order relation(final Interval<C> that) {
        if(this.equals(that)) {
            return Partial.Order.EQUAL;
        }
        if(this.equals(this.intersect(that))) {
            return Partial.Order.LESS;
        }
        if(that.equals(this.intersect(that))) {
            return Partial.Order.GREATER;
        } 
        return Partial.Order.NONCOMPARABLE;
    }
    
    /**
     * Find the intersection of two intervals.
     * @param that interval
     * @return intersection of this and that, which is {@link #NULL} if the two
     * intervals do not overlap
     * @see Range#intersection(com.google.common.collect.Range)
     * @see Range#isConnected(com.google.common.collect.Range) 
     * @see Interval#overlaps(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Commutative_property">
     * commutative property</a>
     * @see <a href="https://en.wikipedia.org/wiki/Idempotence"> idempotence</a> 
     */
    @Override
    public Interval<C> intersect(final Interval<C> that) {
        if(this == MAGIC || that == NULL) {
            return that;
        }
        if(this == NULL) {
            return NULL;
        }
        if(that == MAGIC) {
            return new Interval(this.dimension, this.range);
        }
        
        if(this.dimension == that.dimension) {
            if(this.isConnected(that)) {
                return new Interval(this.dimension,
                                    this.range.intersection(that.range));
            }
            return new Interval(this.dimension);
        }
        return NULL;
    }
    
    /**
     * Find the lattice-compatible union of two intervals. If the intent is to
     * span connected intervals use Interval#coalesce(org.nmdp.ngs.fca.Interval)
     * instead.
     * @param that interval
     * @return  that
     * @see Interval#coalesce(org.nmdp.ngs.fca.Interval) 
     */
    @Override
    public Interval<C> union(final Interval<C> that) {
        return this;
    }
    
    /**
     * Find the coalesced result of two overlapping intervals.
     * @param that interval
     * @return coalesced interval or #NULL if none exists
     * @see Range#span(com.google.common.collect.Range) 
     */
    public Interval<C> coalesce(final Interval<C> that) {
        if(this.overlaps(that)) {
            return new Interval(this.dimension, this.range.span(that.range));
        }
        return NULL;
    }
    
    @Override
    public double measure() {
        //return (Class<C>) range.lowerEndpoint() - range.upperEndpoint();
        
        return 0; //range.lowerEndpoint() - range.upperEndpoint();
    }
    
    public boolean isConnected(final Interval<C> that) {
        if(this.hasNone() || that.hasNone()) {
            return false; 
        }
        return this.range.isConnected(that.range);
    }
    
    /**
     * Test if this interval range has infinite endpoints.
     * @return true if range equals Range#ALL
     * @see #MAGIC
     */
    public boolean hasAll() {
        return !hasNone() && range.equals(Range.all());
    }
    
    /**
     * Test if this interval range has no endpoints.
     * @return true if range is null
     * @see #NULL
     */
    public boolean hasNone() {
        return range == null;
    }
    
    /**
     * Test if this interval has no dimension
     * @return true if dimension equals zero
     * @see #getDimension() 
     */
    public boolean isDimensionless() {
        return getDimension() == 0;
    }
    
    /**
     * Get the interval dimension.
     * @return dimension
     * @see #isDimensionless() 
     */
    public int getDimension() {
        return dimension;
    }
    
    /**
     * Experimental method to find the difference between two intervals.
     * @param that interval
     * @return the difference of this and that
     * @see #ahead() 
     * @see #behind() 
     * @see #intersect(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html#removeAll-java.util.Collection-">
     * java.util.Collection.removeAll</a>
     * @see <a href="https://en.wikipedia.org/wiki/Complement_(set_theory)">
     * complement</a>
     */
    @Beta
    public Difference<C> minus(final Interval<C> that) {
        return new Difference(this.intersect(that.ahead()),
                              this.intersect(that.behind()));
    }
    
    /**
     * Experimental method to find the complement of this interval.
     * @return the complement
     * @see #minus(org.nmdp.ngs.fca.Interval)
     * @see <a href="https://en.wikipedia.org/wiki/Complement_(set_theory)">
     * complement</a>
     */
    @Beta
    public Difference<C> complement() {
        return new Interval(this.dimension, Range.all()).minus(this);
    }
    
    /**
     * Find the interval that extends ahead of this one.
     * @return interval with lower endpoint equal to this upper endpoint and
     * upper endpoint equal to infinity
     * @see #behind() 
     */
    public Interval<C> ahead() {
        if(this.hasNone()) {
            return new Interval(this.dimension, Range.all());
        }
        if(this.range.equals(Range.all())) {
            return new Interval(this.dimension);
        }
        return new Interval(this.dimension,
                            Range.downTo(this.range.upperEndpoint(),
                            reverse(this.range.upperBoundType())));
    }
    
    /**
     * Find the interval that extends behind this one.
     * @return interval with lower endpoint equal to minus infinity and upper
     * endpoint equal to this lower endpoint
     * @see #ahead
     */
    public Interval<C> behind() {
        if(this.hasNone()) {
            return new Interval(this.dimension, Range.all());
        }
        if(this.range.equals(Range.all())) {
            return new Interval(this.dimension);
        }
        return new Interval(this.dimension,
                            Range.upTo(this.range.lowerEndpoint(),
                            reverse(this.range.lowerBoundType())));
    }
    
    /**
     * Find the gap between two intervals. 
     * @param that interval
     * @return the interval between this and that or {@link Interval#NULL} if
     * none exists
     * @see #ahead()  
     * @see #behind() 
     * @see #intersect(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Commutative_property">
     * commutative property</a>
     */
    public Interval<C> gap(final Interval<C> that) {
        if(this.before(that)) {
            return this.ahead().intersect(that.behind());
        }
        if(this.after(that)) {
            return this.behind().intersect(that.ahead());
        }
        return NULL;
    }
    
    /**
     * Test if an interval is before another.
     * @param that interval
     * @return true if this interval (upper endpoint) is before that (lower
     * endpoint)
     * @see #after(org.nmdp.ngs.fca.Interval) 
     */
    public boolean before(final Interval<C> that) {
        if(this.hasNone() || that.hasNone()) {
            return false;
        }
        return this.dimension == that.dimension &&
               this.range.upperEndpoint().compareTo(that.range.lowerEndpoint()) < 0;
    }
    
    /**
     * Test if an interval is after another.
     * @param that interval
     * @return true if that interval (upper endpoint) is before this (lower
     * endpoint)
     * @see #before(org.nmdp.ngs.fca.Interval) 
     */
    public boolean after(final Interval<C> that) {
        if(this.hasNone() || that.hasNone()) {
            return false;
        }
        return that.before(this);
    }
    
    /**
     * Test if an interval is between two others.
     * @param that interval
     * @param other interval
     * @return true if this interval is after that and before the other
     * @see #before(org.nmdp.ngs.fca.Interval) 
     * @see #after(org.nmdp.ngs.fca.Interval) 
     */
    public boolean between(final Interval<C> that, final Interval<C> other) {
        checkNotNull(this.range, that.range);
        checkNotNull(other.range);
        return this.after(that) && this.before(other);
    }
    
    /**
     * Test if an interval overlaps another.
     * @param that interval
     * @return true if the intersection range of this and that is not null
     * @see #intersect(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Allen%27s_interval_algebra">
     * Allen's interval algebra</a>
     */
    public boolean overlaps(final Interval<C> that) {
        return !this.intersect(that).hasNone();
    }
    
    /**
     * Test if an interval implies another.
     * @param that interval
     * @return true if all elements of this are also in that
     * @see #intersect(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Allen%27s_interval_algebra">
     * Allen's interval algebra</a>
     */
    public boolean then(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return this.intersect(that).equals(this);
    }
    
    /**
     * Test if an interval starts another.
     * @param that interval
     * @return true if the intervals share a common lower endpoint (start) and
     * this upper endpoint is less than that
     * @see #ends(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Allen%27s_interval_algebra">
     * Allen's interval algebra</a>
     */
    public boolean starts(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return this.dimension == that.dimension && this.range.lowerEndpoint().compareTo(that.range.lowerEndpoint()) == 0 && this.range.upperEndpoint().compareTo(that.range.upperEndpoint()) < 0;
    }
    
    /**
     * Test if an interval ends another.
     * @param that interval
     * @return true if the intervals share a common upper endpoint (end) and
     * this lower endpoint is greater than that
     * @see #starts(org.nmdp.ngs.fca.Interval) 
     * @see <a href="https://en.wikipedia.org/wiki/Allen%27s_interval_algebra">
     * Allen's interval algebra</a>
     */
    public boolean ends(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return this.dimension == that.dimension && this.range.upperEndpoint().compareTo(that.range.upperEndpoint()) == 0 && this.range.lowerEndpoint().compareTo(that.range.lowerEndpoint()) > 0;
    }
    
    /** Test if two intervals are equal.
     * @param that interval
     * @return true if this equals that
     * @see <a href="https://en.wikipedia.org/wiki/Allen%27s_interval_algebra">
     * Allen's interval algebra</a>
     */
    @Override
    public boolean equals(final Object that) {
        if (!(that instanceof Interval)) {
            return false;
        }

        if (that == this) {
            return true;
        }

        Interval interval = (Interval) that;
        
        // TODO: clean this up
        if(interval.dimension == this.dimension) {
            if(this.hasNone()) {
                if(interval.hasNone()) {
                    return true;
                }
                return false;
            }
            if(interval.hasNone()) {
                if(this.hasNone()) {
                    return true;
                }
                return false;
            }
            return interval.range.equals(this.range);
        }
        return false;
    }
    
    /**
     * Get the string representation of this interval.
     * @return interval string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                
        if(this != MAGIC && this != NULL) {
            sb.append(Integer.toString(dimension)).append(":");
        }
        
        if(this.hasNone()) {
            sb.append("()");
        } else {
            sb.append(range.toString());
        }
        
        return sb.toString();
    }
    
    /**
     * Get the hash representation of this interval.
     * @return interval hash code
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(dimension, range);
    }
    
    /**
     * Get the range representation of this interval.
     * @return the dimensionless range
     */
    public Range<C> toRange() {
        return range;
    }

    /**
     * Compare two intervals.
     * @param that interval
     * @return a negative integer as this before that, zero as this.equals(that)
     * , or a positive integer as this greater than that.
     */
    @Override
    public int compareTo(Interval<C> that) {
        if(this.before(that)) {
            return -1;
        }
        if(this.after(that)) {
            return 1;
        }
        return 0;
    }
}