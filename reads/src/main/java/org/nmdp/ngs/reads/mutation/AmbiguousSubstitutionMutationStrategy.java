/*

    ngs-reads  Next generation sequencing (NGS/HTS) reads.
    Copyright (c) 2014 National Marrow Donor Program (NMDP)

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
package org.nmdp.ngs.reads.mutation;

import org.biojava.bio.seq.DNATools;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;

import org.nmdp.ngs.reads.MutationStrategy;

/**
 * Ambiguous substitution mutation strategy, replaces unambiguous base with <code>n</code>.
 */
public final class AmbiguousSubstitutionMutationStrategy implements MutationStrategy
{
    /** Ambiguous symbol list. */
    private static final SimpleSymbolList N = new SimpleSymbolList(DNATools.getDNA());

    static {
        try {
            N.addSymbol(DNATools.n());
        }
        catch (IllegalSymbolException e) {
            // ignore
        }
    }

    @Override
    public SymbolList mutate(final Symbol symbol) {
        return N;
    }
}
