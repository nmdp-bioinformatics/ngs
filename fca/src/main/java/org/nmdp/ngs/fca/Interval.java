package com.gsgenetics.java;

public class Interval<T> implements Partial<Interval<T>> {
    private int dimension;
    private Range<T> range;
    
    private Interval() {
        Interval(0);
    }
    
    private Interval(int dimension) {
        this.dimension = dimension;
        range = null;
    }
    
    public Interval(final Range<T> range) {
        checkNotNull(range);
        
        dimension = 1;
        this.range = range;
    }
    
    public Interval(int dimension, final Range<T> range) {
        checkArgument(dimension >= 0);
        checkNotNull(range);
        
        this.dimension = dimension;
        this.range = range;
    }
    
    @Override
    public Partial.Order relation(final Interval<T> that) {
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
    public Interval<T> intersect(final Interval<T> that) {
        if(this.dimension == that.dimension) {
            if(this.range.isConnected(that.range)) {
                return new Interval(this.dimension, this.range.intersection(that.range));
            }
            return new Interval(this.dimension);
        }
        
        return new Interval();
    }
    
    @Override
    public Interval<T> union(final Interval<T> that) {
        if(this.dimension == that.dimension && this.range.isConnected(that.range)) {
            return new Interval(this.dimension, this.range.span(that.range));
        }
        return null;
    }
    
    @Override
    public double measure() {
        try {
            return range.upperEndpoint() - range.lowerEndpoint();  
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean hasNone() {
        return range == null;
    }
    
    public boolean isDimensionless() {
        return getDimension() == 0 && hasNone();
    }
    
    public int getDimension() {
        return dimension;
    }
    
    @Override
    public boolean equals(final Object right) {
        if (!(right instanceof Interval)) {
            return false;
        }

        //if (right == this) {
        //   return true;
       // }

        Interval interval = (Interval) right;
        return interval.dimension == this.dimension && interval.range == this.range;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Integer.toString(dimension));
        sb.append(":");
        
        if(this.hasNone()) {
            sb.append("[]");
        } else {
            sb.append(range.toString());
        }
        
        return sb.toString();
    }
    
    public Range<T> toRange() {
        return range;
    }
}