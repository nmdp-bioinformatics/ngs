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

import java.util.Collection;

/**
 * Lattice. An iterable set of partially ordered elements.
 * @param <E> element type
 */
public interface Lattice<E extends PartiallyOrdered> extends Iterable<E> {

    /**
     * Find the least element.
     * @return the least lattice element
     */
    E bottom();

    /**
     * Find the greatest element.
     * @return the greatest lattice element
     */
    E top();
    
    /**
     * Lattice size.
     * @return the number of vertices
     */
    int size();

    /**
     * Find the least upper bound of two elements (supremum).
     * @param left element
     * @param right element
     * @return the join of left and right
     */
    E join(E left, E right);

    /**
     * Find the greatest lower bound of two elements (infimum).
     * @param left element
     * @param right element
     * @return the meet of left and right
     */
    E meet(E left, E right);
    
    /**
     * Test if one element covers another.
     * @param left element
     * @param right element
     * @return true if left (x) is less than right (y) and there is no other
     * element z where x {@literal <} z {@literal <} y.
     */
    boolean covers(E left, E right);
    
    E find(E element);
    
    boolean contains(E element);
    
    boolean containsAll(Collection<? extends E> collection);
    
    boolean isEmpty();
    
    Object[] toArray();
    
    <E> E[] toArray(E[] elements);

    /**
     * Calculate the measure of two elements.
     * @param left element
     * @param right element
     * @return the measure of left and right
     */
    double measure(E left, E right);
}
