/*

    ngs-sra  Mapping for SRA submission XSDs.
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
package org.nmdp.ngs.sra;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Reader;

import javax.xml.XMLConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javax.xml.transform.stream.StreamSource;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import com.google.common.base.Charsets;

import com.google.common.io.Resources;

import org.nmdp.ngs.sra.jaxb.analysis.Analysis;
import org.nmdp.ngs.sra.jaxb.experiment.Experiment;
import org.nmdp.ngs.sra.jaxb.run.RunSet;
import org.nmdp.ngs.sra.jaxb.sample.Sample;
import org.nmdp.ngs.sra.jaxb.study.Study;
import org.nmdp.ngs.sra.jaxb.submission.Submission;

import org.xml.sax.SAXException;

/**
 * Reader for SRA xml.
 */
public final class SraReader {

    /**
     * Read an analysis from the specified reader.
     *
     * @param reader reader, must not be null
     * @return an analysis read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static Analysis readAnalysis(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(Analysis.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL analysisSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.analysis.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(analysisSchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (Analysis) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal Analysis", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal Analysis", e);
        }
    }

    /**
     * Read an analysis from the specified file.
     *
     * @param file file, must not be null
     * @return an analysis read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Analysis readAnalysis(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readAnalysis(reader);
        }
    }

    /**
     * Read an analysis from the specified URL.
     *
     * @param url URL, must not be null
     * @return an analysis read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Analysis readAnalysis(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readAnalysis(reader);
        }
    }

    /**
     * Read an analysis from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return an analysis read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Analysis readAnalysis(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readAnalysis(reader);
        }
    }


    /**
     * Read an experiment from the specified reader.
     *
     * @param reader reader, must not be null
     * @return an experiment read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static Experiment readExperiment(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(Experiment.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL experimentSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.experiment.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(experimentSchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (Experiment) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal Experiment", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal Experiment", e);
        }
    }

    /**
     * Read an experiment from the specified file.
     *
     * @param file file, must not be null
     * @return an experiment read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Experiment readExperiment(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readExperiment(reader);
        }
    }

    /**
     * Read an experiment from the specified URL.
     *
     * @param url URL, must not be null
     * @return an experiment read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Experiment readExperiment(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readExperiment(reader);
        }
    }

    /**
     * Read an experiment from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return an experiment read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Experiment readExperiment(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readExperiment(reader);
        }
    }


    /**
     * Read a run set from the specified reader.
     *
     * @param reader reader, must not be null
     * @return a run set read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static RunSet readRunSet(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(RunSet.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL runSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.run.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(runSchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (RunSet) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal RunSet", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal RunSet", e);
        }
    }

    /**
     * Read a run set from the specified file.
     *
     * @param file file, must not be null
     * @return a run set read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static RunSet readRunSet(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readRunSet(reader);
        }
    }

    /**
     * Read a run set from the specified URL.
     *
     * @param url URL, must not be null
     * @return a run set read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static RunSet readRunSet(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readRunSet(reader);
        }
    }

    /**
     * Read a run set from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return a run set read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static RunSet readRunSet(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readRunSet(reader);
        }
    }


    /**
     * Read a sample from the specified reader.
     *
     * @param reader reader, must not be null
     * @return a sample read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static Sample readSample(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(Sample.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL sampleSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.sample.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(sampleSchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (Sample) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal Sample", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal Sample", e);
        }
    }

    /**
     * Read a sample from the specified file.
     *
     * @param file file, must not be null
     * @return a sample read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Sample readSample(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readSample(reader);
        }
    }

    /**
     * Read a sample from the specified URL.
     *
     * @param url URL, must not be null
     * @return a sample read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Sample readSample(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readSample(reader);
        }
    }

    /**
     * Read a sample from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return a sample read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Sample readSample(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readSample(reader);
        }
    }


    /**
     * Read a study from the specified reader.
     *
     * @param reader reader, must not be null
     * @return a study read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static Study readStudy(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(Study.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL studySchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.study.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(studySchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (Study) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal Study", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal Study", e);
        }
    }

    /**
     * Read a study from the specified file.
     *
     * @param file file, must not be null
     * @return a study read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Study readStudy(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readStudy(reader);
        }
    }

    /**
     * Read a study from the specified URL.
     *
     * @param url URL, must not be null
     * @return a study read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Study readStudy(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readStudy(reader);
        }
    }

    /**
     * Read a study from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return a study read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Study readStudy(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readStudy(reader);
        }
    }


    /**
     * Read a submission from the specified reader.
     *
     * @param reader reader, must not be null
     * @return a submission read from the specified reader
     * @throws IOException if an I/O error occurs
     */
    public static Submission readSubmission(final Reader reader) throws IOException {
        checkNotNull(reader);
        try {
            JAXBContext context = JAXBContext.newInstance(Submission.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL submissionSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.submission.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(submissionSchemaURL.toString()) });
            unmarshaller.setSchema(schema);
            return (Submission) unmarshaller.unmarshal(reader);
        }
        catch (JAXBException e) {
            throw new IOException("could not unmarshal Submission", e);
        }
        catch (SAXException e) {
            throw new IOException("could not unmarshal Submission", e);
        }
    }

    /**
     * Read a submission from the specified file.
     *
     * @param file file, must not be null
     * @return a submission read from the specified file
     * @throws IOException if an I/O error occurs
     */
    public static Submission readSubmission(final File file) throws IOException {
        checkNotNull(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return readSubmission(reader);
        }
    }

    /**
     * Read a submission from the specified URL.
     *
     * @param url URL, must not be null
     * @return a submission read from the specified URL
     * @throws IOException if an I/O error occurs
     */
    public static Submission readSubmission(final URL url) throws IOException {
        checkNotNull(url);
        try (BufferedReader reader = Resources.asCharSource(url, Charsets.UTF_8).openBufferedStream()) {
            return readSubmission(reader);
        }
    }

    /**
     * Read a submission from the specified input stream.
     *
     * @param inputStream input stream, must not be null
     * @return a submission read from the specified input stream
     * @throws IOException if an I/O error occurs
     */
    public static Submission readSubmission(final InputStream inputStream) throws IOException {
        checkNotNull(inputStream);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readSubmission(reader);
        }
    }
}
