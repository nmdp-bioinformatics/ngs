/*

    ngs-tools  Next generation sequencing (NGS/HTS) command line tools.
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
package org.nmdp.ngs.tools;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.PrintStream;

/**
 * About.
 */
final class About {
    private static final String ARTIFACT_ID = "${project.artifactId}";
    private static final String BUILD_TIMESTAMP = "${maven.build.timestamp}";
    private static final String COMMIT = "${git.commit.id}";
    private static final String COPYRIGHT = "Copyright (c) 2014 National Marrow Donor Program (NMDP)";
    private static final String LICENSE = "Licensed GNU Lesser General Public License (LGPL), version 3 or later";
    private static final String VERSION = "${project.version}";


    /**
     * Return the artifact id.
     *
     * @return the artifact id
     */
    public String artifactId() {
        return ARTIFACT_ID;
    }

    /**
     * Return the build timestamp.
     *
     * @return the build timestamp
     */
    public String buildTimestamp() {
        return BUILD_TIMESTAMP;
    }

    /**
     * Return the last commit.
     *
     * @return the last commit
     */
    public String commit() {
        return COMMIT;
    }

    /**
     * Return the copyright.
     *
     * @return the copyright
     */
    public String copyright() {
        return COPYRIGHT;
    }

    /**
     * Return the license.
     *
     * @return the license
     */
    public String license() {
        return LICENSE;
    }

    /**
     * Return the version.
     *
     * @return the version
     */
    public String version() {
        return VERSION;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(artifactId());
        sb.append(" ");
        sb.append(version());
        sb.append("\n");
        sb.append("Commit: ");
        sb.append(commit());
        sb.append("  Build: ");
        sb.append(buildTimestamp());
        sb.append("\n");
        sb.append(copyright());
        sb.append("\n");
        sb.append(license());
        sb.append("\n");
        return sb.toString();
    }


    /**
     * Write about text to the specified print stream.
     *
     * @param out print stream to write about text to
     */
    public static void about(final PrintStream out) {
        checkNotNull(out);
        out.print(new About().toString());
    }
}
