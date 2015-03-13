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
package org.nmdp.ngs.hml;

import org.nmdp.ngs.hml.jaxb.Hml;

/**
 * HML validation rule.
 */
public interface HmlValidationRule {

    /**
     * Validate the specified HML document.
     *
     * @param hml HML document to validate, must not be null
     * @return true if the specified HML document passes this validation rule
     * @throws HmlValidationException if this validation rule fails to apply
     */
    boolean validate(Hml hml) throws HmlValidationException;

    /**
     * Return a description of this HML validation rule suitable for use in an exception message.  Must not return null.
     *
     * @return a description of this HML validation rule suitable for use in an exception message
     */
    @Override
    String toString();
}
