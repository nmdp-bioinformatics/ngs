/*

    ngs-hml  Mapping for HML XSDs.
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
package org.nmdp.ngs.hml.rules;

import java.util.Collections;
import java.util.Set;

import org.nmdp.ngs.hml.HmlValidationRule;
import org.nmdp.ngs.hml.HmlValidator;

/**
 * HML validation rules.
 */
public final class HmlValidationRules {

    /**
     * Return the set of HML validation rules that implement the MIRING specification.
     *
     * @return the set of HML validation rules that implement the MIRING specification
     */
    public static Set<HmlValidationRule> miringRules() {
        return Collections.<HmlValidationRule>emptySet();
    }

    /**
     * Return an HML validator based on the set of HML validation rules that implement the MIRING specification.
     *
     * @return an HML validator based on the set of HML validation rules that implement the MIRING specification
     */
    public static HmlValidator miring() {
        return new HmlValidatorImpl(miringRules());
    }
}
