/*

    ngs-variant  Variants.
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
package org.nmdp.ngs.variant.vcf;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

/**
 * VCF reader.
 */
public final class VcfReader {

    /**
     * Private no-arg constructor.
     */
    private VcfReader() {
        // empty
    }


    // todo:  think about Strictness; none vs. research vs. clinical


    // callback methods

    /**
     * Parse the specified readable.
     *
     * @param readable readable to parse, must not be null
     * @param listener low-level event based parser callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void parse(final Readable readable, final VcfParseListener listener) throws IOException {
        VcfParser.parse(readable, listener);
    }

    /**
     * Stream the specified readable.
     *
     * @param readable readable to stream, must not be null
     * @param listener event based reader callback, must not be null
     * @throws IOException if an I/O error occurs
     */
    public static void stream(final Readable readable, final VcfStreamListener listener) throws IOException {
        StreamingVcfParser.stream(readable, listener);
    }


    // collect methods

    /**
     * Read the VCF header from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return the VCF header read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static VcfHeader header(final Readable readable) throws IOException {
        return VcfHeaderParser.header(readable);
    }

    /**
     * Read zero or more VCF samples from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more VCF samples read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfSample> samples(final Readable readable) throws IOException {
        return VcfSampleParser.samples(readable);
    }

    /**
     * Read zero or more VCF records from the specified readable.
     *
     * @param readable readable to read from, must not be null
     * @return zero or more VCF records read from the specified readable
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfRecord> records(final Readable readable) throws IOException {
        return VcfRecordParser.records(readable);
    }


    // convenience methods

    /**
     * Read the VCF header from the specified file.
     *
     * @param file file to read from, must not be null
     * @return the VCF header read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static VcfHeader header(final File file) throws IOException {
        checkNotNull(file);
        // could also use Files.asCharSource(file, Charsets.UTF_8).openBufferedStream()
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return header(reader);
        }
    }

    /**
     * Read the VCF header from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return the VCF header read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static VcfHeader header(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return header(reader);
        }
    }

    /**
     * Read the VCF header from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return the VCF header read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static VcfHeader header(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return header(reader);
        }
    }

    /**
     * Read zero or more VCF samples from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more VCF samples read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfSample> samples(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return samples(reader);
        }
    }

    /**
     * Read zero or more VCF samples from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more VCF samples read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfSample> samples(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return samples(reader);
        }
    }

    /**
     * Read zero or more VCF samples from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more VCF samples read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfSample> samples(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return samples(reader);
        }
    }

    /**
     * Read zero or more VCF records from the specified file.
     *
     * @param file file to read from, must not be null
     * @return zero or more VCF records read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfRecord> records(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return records(reader);
        }
    }

    /**
     * Read zero or more VCF records from the specified URL.
     *
     * @param url URL to read from, must not be null
     * @return zero or more VCF records read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfRecord> records(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return records(reader);
        }
    }

    /**
     * Read zero or more VCF records from the specified input stream.
     *
     * @param inputStream input stream to read from, must not be null
     * @return zero or more VCF records read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Iterable<VcfRecord> records(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return records(reader);
        }
    }
}
