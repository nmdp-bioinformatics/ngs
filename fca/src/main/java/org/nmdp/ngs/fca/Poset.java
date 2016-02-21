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

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public final class Poset<C> extends PartiallyOrdered<Poset<C>> implements Iterable<C> {
    public List<C> collection;
    
    public final static Poset NULL = new Poset<>();
    
    public final static Poset MAGIC = new Poset<>(null);
    
    public Poset(final List<C> list) {
        if(list == null) {
            this.collection = null;
        } else {
           this.collection = ImmutableList.copyOf(list); 
        }
    }
    
    public Poset() {
        collection = new ArrayList<>();
    }
    
    @Override
    public boolean isLessOrEqualTo(final Poset that) {
        return super.apply(that);
    }  
    
    @Override
    public boolean isLessThan(final Poset that) {
        return isLessOrEqualTo(that) && !this.equals(that);
    }
    
    @Override
    public boolean isGreaterThan(final Poset that) {
        return isGreaterOrEqualTo(that) && !this.equals(that);
    }
    
    @Override
    public boolean isGreaterOrEqualTo(final Poset that) {
        return that.equals(this.intersect(that));
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
            List<C> set = new ArrayList<>();
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
    
    
    public static <C extends Comparable<?>> List<Poset<C>> cartesianProduct(Poset<C>...posets) {
        return cartesianProduct(Arrays.asList(posets));
    }

    public static <C extends Comparable<?>> List<Poset<C>> cartesianProduct(List<Poset<C>> posets) {
        List<Poset<C>> resultLists = new ArrayList<>();
        
        if(posets.isEmpty()) {
            resultLists.add(new Poset(new ArrayList<C>()));
            return resultLists;
        } else {
            Poset<C> firstPoset = posets.get(0);
            List<Poset<C>> remainingLists = cartesianProduct(posets.subList(1, posets.size()));
            for(C condition : firstPoset) {
                for(Poset<C> poset : remainingLists) {
                    ArrayList<C> resultList = new ArrayList<C>();
                    resultList.add(condition);
                    resultList.addAll(poset.collection);
                    resultLists.add(new Poset(resultList));
                }
            }
        }
        return resultLists;
    }
    
    /*
    public static <T> List<List<T>> cartesianProduct(List<List<T>> lists) {
        List<List<T>> resultLists = new ArrayList<List<T>>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<T>());
            return resultLists;
        } else {
            List<T> firstList = lists.get(0);
            List<List<T>> remainingLists = cartesianProduct(lists.subList(1, lists.size()));
            for (T condition : firstList) {
                for (List<T> remainingList : remainingLists) {
                    ArrayList<T> resultList = new ArrayList<T>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }
    */
    
    /*
    public Poset<C> multiply(final Poset<C> right) {
        List<Poset<C>> singletons = Poset.singletons(this.collection);
        List<List<C>> result = new ArrayList<List<C>>();
        for(Poset<C> i : singletons) {
            System.out.println("singleton = " + i);
            
            //List<C> list = new ArrayList(i.collection);
            
            
            for(Poset<C> j : Poset.singletons(right.collection)) {
                List<C> product = new ArrayList(i.collection);
                product.addAll(j.collection);
                System.out.println("product = " + product);
                result.add(product);
            }
            
            
            
        }
        
        return new Poset(result);
    }
    */
    

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
        List<C> intersection = new ArrayList<>(collection);
        intersection.retainAll(that.collection);
        return new Poset(intersection);
    }
    
    @Override
    public Poset<C> union(final Poset<C> that) {
        return this;
    }

    public Poset<C> coalesce(final Poset<C> that) {
        List<C> union = new ArrayList<>(collection);
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

    @Override
    public Iterator<C> iterator() {
        return collection.iterator();
    }

}