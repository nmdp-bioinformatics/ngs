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

import java.util.BitSet;

public final class Concept implements Partial<Concept> {
  private BitSet extent, intent;
	
	public Concept(final BitSet extent, final BitSet intent) {
    this.extent = extent;
    this.intent = intent;
  }
  
  public BitSet extent() {
    return extent;
  }
  
  public BitSet intent() {
    return intent;
  }

  @Override
  public Order order(Concept right) {
    BitSet meet = (BitSet) this.intent.clone();
    meet.and(right.intent);
    
    if(this.intent.equals(right.intent)) {
      return Partial.Order.EQUAL;
    }
    
    if(this.intent.equals(meet)) {
      return Partial.Order.LESS;
    }
    
    if(right.intent.equals(meet)) {
      return Partial.Order.GREATER;
    }
    
    return Partial.Order.NONCOMPARABLE;
  }
  
  public boolean gte(Concept right) {
    return this.order(right) == Partial.Order.GREATER ||
           this.order(right) == Partial.Order.EQUAL;
  }
  
  public boolean lte(Concept right) {
    return this.order(right) == Partial.Order.LESS ||
           this.order(right) == Partial.Order.EQUAL;
  }
}
