/*

    ngs-hml  Mapping for HML XSDs.
    Copyright (c) 2014-2017 National Marrow Donor Program (NMDP)

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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.google.inject.Inject;

import org.nmdp.ngs.hml.HmlValidationException;
import org.nmdp.ngs.hml.HmlValidationRule;
import org.nmdp.ngs.hml.HmlValidator;

import org.nmdp.ngs.hml.jaxb.Hml;

/**
 * HML validator implementation.
 */
final class HmlValidatorImpl implements HmlValidator {
    private final Set<HmlValidationRule> validationRules;


    /**
     * Create a new HML validator with the specified validation rules.
     *
     * @param validationRules variable number of validation rules, must not be null
     *    and must not contain any null validation rules
     */
    HmlValidatorImpl(final HmlValidationRule... validationRules) {
        this(ImmutableSet.copyOf(validationRules));
    }

    /**
     * Create a new HML validator with the specified validation rules.
     *
     * @param validationRules set of validation rules, must not be null
     */
    @Inject
    HmlValidatorImpl(final Set<HmlValidationRule> validationRules) {
        checkNotNull(validationRules);
        this.validationRules = validationRules;
    }


    @Override
    public boolean validate(final Hml hml) throws HmlValidationException {
        checkNotNull(hml);
        for (HmlValidationRule validationRule : validationRules) {
            validationRule.validate(hml);
        }
        return true;
    }
}
