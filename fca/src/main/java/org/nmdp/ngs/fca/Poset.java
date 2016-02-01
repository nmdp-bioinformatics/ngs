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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class Poset<C extends Comparable> extends PartiallyOrdered<Poset<C>> {
    public final Collection<C> collection;
    
    public final static Poset NULL = new Poset<>();
    
    public final static Poset MAGIC = new Poset<>(null);
    
    public Poset(final Collection<C> collection) {
        this.collection = collection;
    }
    
    public Poset() {
        collection = new HashSet<>();
    }
    
    /**
     * Calculate partially ordered singletons from a totally-ordered collection.
     * @param <C> comparable element type
     * @param collection of comparable elements
     * @return list of partially ordered singleton sets
     * @throws IllegalArgumentException if collection elements are not disjoint
     */
    public static <C extends Comparable<?>> List<Poset<C>> singletons(final Collection<? extends C> collection) {
        List<Poset<C>> singletons = new ArrayList();
        
        Poset previous = NULL;
        for(C element : collection) {
            Set<C> set = new HashSet<>();
            set.add(element);
            
            Poset poset = new Poset(set);
            if(previous != NULL && poset.isComparableTo(previous)) {
                throw new IllegalArgumentException("singletons must be disjoint");
            }
            singletons.add(poset);
            previous = poset;
        }
        
        return singletons;
    }
    

    @Override
    public Poset<C> intersect(final Poset<C> that) {
        if(this == MAGIC || that == NULL) {
            return that;
        }
        if(this == NULL) {
            return NULL;
        }
        if(that == MAGIC) {
            return new Poset(collection);
        }
        Set<C> intersection = new HashSet<>(collection);
        intersection.retainAll(that.collection);
        return new Poset(intersection);
    }
    
    @Override
    public Poset<C> union(final Poset<C> that) {
        return this;
    }

    public Poset<C> coalesce(final Poset<C> that) {
        Set<C> union = new HashSet<>(collection);
        union.addAll(that.collection);
        return new Poset(union);
    }

    @Override
    public double measure() {
        return collection.size();
    }
    
    @Override
    public String toString() {
        if(this == MAGIC) {
            return "MAGIC";
        }
        
        return collection.toString();
    }
    
    @Override
    public boolean equals(final Object right) {
        if(this == MAGIC) {
            return right == MAGIC;
        }
        
        if (!(right instanceof Poset)) {
            return false;
        }

        if (right == this) {
           return true;
        }

        Poset poset = (Poset) right;
        return this.collection.equals(poset.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.collection);
    }

}