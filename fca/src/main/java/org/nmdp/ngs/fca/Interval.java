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

import com.google.common.collect.Range;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class Interval<C extends Comparable> implements Partial<Interval<C>> {
    private int dimension;
    private Range<C> range;
    
    public final static Interval NULL = new Interval<>();
    public final static Interval MAGIC = new Interval<>(Range.all());
    
    private Interval() {
        dimension = 0;
        range = null;
    }
    
    private Interval(int dimension) {
        this();
        this.dimension = dimension;
    }
    
    private Interval(final Range<C> range) {
        this();
        this.range = range;
    }
    
    public Interval(int dimension, final Range<C> range) {
        checkArgument(Range.atLeast(0).contains(dimension));
        //checkArgument(dimension >= 0);
        checkNotNull(range);
        
        this.dimension = dimension;
        this.range = range;
    }
    
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
                return new Interval(this.dimension, this.range.intersection(that.range));
            }
            return new Interval(this.dimension);
        }
        return NULL;
    }
    
    @Override
    public Interval<C> union(final Interval<C> that) {
        return this;
        
        /*
        if(this.dimension == that.dimension) {
            if(this.hasNone()) {
                return new Interval(this.dimension);
            }
            if(this.range.isConnected(that.range)) {
                return new Interval(this.dimension, this.range.span(that.range));
            }
        }
        return new Interval();
                */
                
    }
    
    @Override
    public double measure() {
        return 0; //range.lowerEndpoint() - range.upperEndpoint();
    }
    
    public boolean isConnected(final Interval<C> that) {
        if(this.hasNone() || that.hasNone()) {
            return false; 
        }
        return this.range.isConnected(that.range);
    }
    
    public boolean hasAll() {
        return !hasNone() && range.equals(Range.all());
    }
    
    public boolean hasNone() {
        return range == null;
    }
    
    public boolean isDimensionless() {
        return getDimension() == 0;
    }
    
    public int getDimension() {
        return dimension;
    }
    
    public boolean before(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return this.dimension == that.dimension && this.range.upperEndpoint().compareTo(that.range.lowerEndpoint()) < 0;
    }
    
    public boolean after(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return that.before(this);
    }
    
    public boolean overlaps(final Interval<C> that) {
        checkNotNull(this.range, that.range);
        return this.intersect(that) != NULL;
    }
    
    @Override
    public boolean equals(final Object right) {
        if (!(right instanceof Interval)) {
            return false;
        }

        if (right == this) {
            return true;
        }

        Interval interval = (Interval) right;
        return interval.dimension == this.dimension && interval.range == this.range;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
                
        if(this != MAGIC) {
            sb.append(Integer.toString(dimension)).append(":");
        }
        
        if(this.hasNone()) {
            sb.append("[]");
        } else {
            sb.append(range.toString());
        }
        
        return sb.toString();
    }
    
    public Range<C> toRange() {
        return range;
    }
}