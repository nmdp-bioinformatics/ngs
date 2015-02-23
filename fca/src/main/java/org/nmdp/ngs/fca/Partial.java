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
 * Interface for partially ordering objects.
 * @param <T> type of object
 */
public interface Partial<T> {
  /**
   * Enumerated partial orders that extends natural (complete) orders for
   * comparable objects. For example, real numbers are completely ordered in the
   * normal way using greater-than, less-than, and equal to operators. Sets of
   * real numbers, by contrast, are partially ordered by inclusion using subset,
   * superset, and equals, respectively. A third possibility exists where sets
   * are disjoint (intersection results in the null set). We call these sets
   * noncomparable and define another ordering category that extends to all
   * partially ordered objects.
   */
  public static enum Order {
    /**
     * Equivalent to a return value of -1 for comparable objects. For example, 0
     * is less than 1 and {0} is a subset {0, 1}.
     */
    LESS,
    /**
     * Equivalent to a return value of 1 for comparable objects. For example, 1
     * is greater than 0 and {0, 1} is a superset {0}.
     */
    GREATER,
    /**
     * Equivalent to a return value of 0 for comparable objects. For example, 1
     * is equal to 1 and {1} is equal to {1}.
     */
    EQUAL,
    /**
     * No equivalent for comparable objects. For example, {0} and {1} are
     * disjoint and therefore noncomparable.
     */
    NONCOMPARABLE;

    public static enum Direction {
      FORWARD,
      REVERSE
    }
    
    public boolean gte() {
      return this.equals(GREATER) || this.equals(EQUAL);
    }
    
    public boolean lte() {
      return this.equals(LESS) || this.equals(EQUAL);
    }
    
    public boolean filter(Direction direction) {
      if(direction.equals(Direction.FORWARD)) {
        return gte();
      }
      return lte();
    }
  }
  
  /**
   * Method to define the partial order.
   * @param type of object
   * @return partial order
   */
  public Order order(T type);
}