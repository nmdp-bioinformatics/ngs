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
package org.nmdp.ngs.hml;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import org.nmdp.ngs.hml.jaxb.Hml;

/**
 * Unit test for HmlValidationException.
 */
public final class HmlValidationExceptionTest {
    private HmlValidationRule validationRule = new HmlValidationRule() {
            @Override
            public boolean validate(final Hml hml) throws HmlValidationException {
                return true;
            }
        };

    @Test
    public void testConstructorString() {
        assertNotNull(new HmlValidationException("message"));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorStringNullMessage() {
        new HmlValidationException((String) null);
    }

    @Test
    public void testConstructorStringThrowable() {
        assertNotNull(new HmlValidationException("message", new Throwable()));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorStringThrowableNullMessage() {
        new HmlValidationException((String) null, new Throwable());
    }

    @Test
    public void testConstructorValidationRule() {
        assertNotNull(new HmlValidationException(validationRule));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorValidationRuleNullValidationRule() {
        new HmlValidationException((HmlValidationRule) null);
    }

    @Test
    public void testConstructorValidationRuleThrowable() {
        assertNotNull(new HmlValidationException(validationRule, new Throwable()));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorValidationRuleThrowableNullValidationRule() {
        new HmlValidationException((HmlValidationRule) null, new Throwable());
    }
}
