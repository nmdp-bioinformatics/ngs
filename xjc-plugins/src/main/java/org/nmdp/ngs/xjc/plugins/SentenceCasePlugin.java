/*

    ngs-xjc-plugins  XJC plugins.
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
package org.nmdp.ngs.xjc.plugins;

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;

import com.sun.tools.xjc.outline.Outline;

import com.sun.xml.bind.api.impl.NameConverter;

import org.xml.sax.ErrorHandler;

/**
 * Sentence case XJC plugin.
 */
public final class SentenceCasePlugin extends Plugin {

    @Override
    public String getOptionName() {
        return "sentence-case";
    }

    @Override
    public String getUsage() {
        return " -sentence-case : use sentence case when converting names";
    }

    @Override
    public void onActivated(final Options opts) throws BadCommandLineException {
        opts.setNameConverter(new SentenceCaseNameConverter(), this);
    }

    @Override
    public boolean run(final Outline model, final Options opt, final ErrorHandler errorHandler) {
        return true;
    }

    static class SentenceCaseNameConverter extends NameConverter.Standard {
        @Override
        public String capitalize(final String s) {
            StringBuilder sb = new StringBuilder(s.length());
            sb.append(Character.toUpperCase(s.charAt(0)));
            sb.append(s.substring(1).toLowerCase());
            return sb.toString();
        }
    }
}
