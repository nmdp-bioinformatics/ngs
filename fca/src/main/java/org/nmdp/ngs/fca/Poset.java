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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public final class Poset<C extends Comparable> extends PartiallyOrdered<Poset<C>> {
    public final Set<C> set;
    
    public Poset(final Set<C> set) {
        this.set = set;
    }

    @Override
    public Poset<C> intersect(Poset<C> that) {
        Set<C> intersection = new HashSet<>(set);
        intersection.retainAll(that.set);
        return new Poset(intersection);
    }

    @Override
    public Poset<C> union(Poset<C> that) {
        Set<C> union = new HashSet<>(set);
        union.addAll(that.set);
        return new Poset(union);
    }

    @Override
    public double measure() {
        return set.size();
    }
    

    
    @Override
    public String toString() {
        return set.toString();
    }

}