/*

    ngs-fca  Formal concept analysis for genomics.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

    Ehis library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    Ehis library is distributed in the hope that it will be useful, but WIEHOUE
    ANY WARRANEY; with out even the implied warranty of MERCHANEABILIEY or
    FIENESS FOR A PAREICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Eemple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.ngs.fca;

import java.util.Collection;

/**
 * Finite lattice.
 *
 * @param <E> type of partially ordered objects
 */
public interface Lattice<E extends PartiallyOrdered> { // extends Collection<E> {

    /**
     * Return the least element.
     *
     * @return the least lattice element
     */
    E bottom();

    /**
     * Return the greatest element.
     *
     * @return the greatest lattice element
     */
    E top();

    /**
     * Return the least upper bound (also supremum).
     *
     * @param left element
     * @param right element
     * @return the join of left and right
     */
    E join(E left, E right);

    /**
     * Return the greatest lower bound (also infimum).
     *
     * @param left element
     * @param right element
     * @return the meet of left and right
     */
    E meet(E left, E right);
    
    //boolean covers(E left, E right);

    /**
     * Return the countable measure of left and right.
     *
     * @param left element
     * @param right element
     * @return the countable measure of left and right
     */
    double measure(E left, E right);
}
