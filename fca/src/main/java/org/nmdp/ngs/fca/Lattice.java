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

import java.util.List;

/**
 * A class for finite (trivially complete) lattices.
 * @param <T> partially ordered type
 */
public interface Lattice<T extends Partial> {
  
  /**
   * The bottom of the lattice.
   * @return the least element in the lattice
   */
    T bottom();
    
    /**
     * The top of the lattice.
     * @return the greatest element in the lattice
     */
    T top();
    
    /**
     * The least upper bound of two lattice elements (also the supremum).
     * @param left element
     * @param right element
     * @return meet of left and right
     */
    T join(T left, T right);
    
    /**
     * The greatest lower bound of two lattice elements (also the infimum).
     * @param left element
     * @param right element
     * @return join of left and right
     */
    T meet(T left, T right);
}
