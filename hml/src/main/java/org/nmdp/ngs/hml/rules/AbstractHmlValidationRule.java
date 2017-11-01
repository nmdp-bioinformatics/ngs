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

import org.nmdp.ngs.hml.HmlValidationRule;

/**
 * Abstract HML validation rule.
 */
public abstract class AbstractHmlValidationRule implements HmlValidationRule {
    private final String description;

    /**
     * Create a new abstract HML validation rule with the specified description.
     *
     * @param description description, must not be null
     */
    protected AbstractHmlValidationRule(final String description) {
        checkNotNull(description);
        this.description = description;
    }


    @Override
    public String toString() {
        return description;
    }
}
