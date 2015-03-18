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

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * HML validation exception.
 */
public final class HmlValidationException extends Exception {

    /**
     * Create a new HML validation exception with the specified message.
     *
     * @param message message, must not be null
     */
    public HmlValidationException(final String message) {
        this(message, null);
    }

    /**
     * Create a new HML validation exception with the specified message and cause.
     *
     * @param message message, must not be null
     * @param cause cause
     */
    public HmlValidationException(final String message, final Throwable cause) {
        super(message, cause);
        checkNotNull(message, "message must not be null");
    }

    /**
     * Create a new HML validation exception with the specified HML validation rule.
     *
     * @param validationRule, HML validation rule, must not be null
     */
    public HmlValidationException(final HmlValidationRule validationRule) {
        this(validationRule, null);
    }

    /**
     * Create a new HML validation exception with the specified HML validation rule and cause.
     *
     * @param validationRule, HML validation rule, must not be null
     * @param cause cause
     */
    public HmlValidationException(final HmlValidationRule validationRule, final Throwable cause) {
        this(validationRule == null ? (String) null : validationRule.toString(), cause);
    }
}
