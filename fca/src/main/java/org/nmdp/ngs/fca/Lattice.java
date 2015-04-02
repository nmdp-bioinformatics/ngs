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

/**
 * Finite lattice.
 * @param <T> type of partially ordered objects
 */
public interface Lattice<T extends Partial> {
  
  /**
   * The bottom of the lattice.
   * @return the least lattice element
   */
    T bottom();
    
    /**
     * The top of the lattice.
     * @return the greatest lattice element
     */
    T top();
    
    /**
     * The join of one lattice element (also supremum).
     * @param query element
     * @return the greatest lower bound of lattice element query
     */
    T greatestLowerBound(T query);
    
    /**
     * The meet of one lattice element (also infimum).
     * @param query element
     * @return the least upper bound of lattice element query
     */
    T leastUpperBound(T query);
    
    /**
     * The join of two lattice elements (also supremum).
     * @param left element
     * @param right element
     * @return the least upper bound of lattice elements left and right
     */
    T join(T left, T right);
    
    /**
     * The meet of two lattice elements (also infimum).
     * @param left element
     * @param right element
     * @return the greatest lower bound of lattice elements left and right
     */
    T meet(T left, T right);
    
    double measure(T query);
    
    double measure(T left, T right);
}
