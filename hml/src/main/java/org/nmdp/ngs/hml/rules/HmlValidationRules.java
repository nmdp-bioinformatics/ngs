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

import static org.nmdp.ngs.hml.HmlUtils.getHmlid;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import org.nmdp.ngs.hml.HmlValidationException;
import org.nmdp.ngs.hml.HmlValidationRule;
import org.nmdp.ngs.hml.HmlValidator;

import org.nmdp.ngs.hml.jaxb.Hml;

/**
 * HML validation rules.
 */
public final class HmlValidationRules {

    /** MIRING specification requires <code>lt;hmlid&gt;</code> element. */
    static HmlValidationRule HMLID_ELEMENT_REQUIRED = new AbstractHmlValidationRule("MIRING specification requires <hmlid> element") {
            @Override
            public boolean validate(final Hml hml) throws HmlValidationException {
                if (getHmlid(hml) == null) {
                    throw new HmlValidationException(this);
                }
                return true;
            }
        };

    /**
     * Return the set of HML validation rules that implement the MIRING specification.
     *
     * @return the set of HML validation rules that implement the MIRING specification
     */
    public static Set<HmlValidationRule> miringRules() {
        return ImmutableSet.of(HMLID_ELEMENT_REQUIRED);
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
