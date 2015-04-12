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
   * The least lattice element.
   * @return bottom
   */
    T bottom();
    
    /**
     * The greatest lattice element.
     * @return top
     */
    T top();
    
    /**
     * The least upper bound (also supremum).
     * @param left element
     * @param right element
     * @return The join of left and right
     */
    T join(T left, T right);
    
    /**
     * The greatest lower bound (also infimum).
     * @param left element
     * @param right element
     * @return the meet of left and right
     */
    T meet(T left, T right);
    
    /**
     * Measure value.
     * @param left element
     * @param right element
     * @return the countable measure of left and right
     */
    double measure(T left, T right);
}
