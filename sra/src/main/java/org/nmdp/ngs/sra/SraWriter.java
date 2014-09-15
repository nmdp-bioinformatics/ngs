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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.XMLConstants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import javax.xml.transform.stream.StreamSource;

import org.nmdp.ngs.sra.jaxb.analysis.Analysis;
import org.nmdp.ngs.sra.jaxb.experiment.Experiment;
import org.nmdp.ngs.sra.jaxb.run.RunSet;
import org.nmdp.ngs.sra.jaxb.sample.Sample;
import org.nmdp.ngs.sra.jaxb.study.Study;
import org.nmdp.ngs.sra.jaxb.submission.Submission;

import org.xml.sax.SAXException;

/**
 * Writer for SRA xml.
 */
public final class SraWriter {

    /**
     * Private no-arg constructor.
     */
    private SraWriter() {
        // empty
    }


    public static void writeAnalysis(final Analysis analysis, final Writer writer) throws IOException {
        checkNotNull(analysis);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Analysis.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL analysisSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.analysis.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(analysisSchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(analysis, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal Analysis", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal Analysis", e);
        }
    }

    public static void writeAnalysis(final Analysis analysis, final File file) throws IOException {
        checkNotNull(analysis);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeAnalysis(analysis, writer);
        }
    }

    public static void writeAnalysis(final Analysis analysis, final OutputStream outputStream) throws IOException {
        checkNotNull(analysis);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeAnalysis(analysis, writer);
        }
    }


    public static void writeExperiment(final Experiment experiment, final Writer writer) throws IOException {
        checkNotNull(experiment);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Experiment.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL experimentSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.experiment.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(experimentSchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(experiment, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal Experiment", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal Experiment", e);
        }
    }

    public static void writeExperiment(final Experiment experiment, final File file) throws IOException {
        checkNotNull(experiment);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeExperiment(experiment, writer);
        }
    }

    public static void writeExperiment(final Experiment experiment, final OutputStream outputStream) throws IOException {
        checkNotNull(experiment);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeExperiment(experiment, writer);
        }
    }


    public static void writeRunSet(final RunSet runSet, final Writer writer) throws IOException {
        checkNotNull(runSet);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(RunSet.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL runSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.run.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(runSchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(runSet, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal RunSet", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal RunSet", e);
        }
    }

    public static void writeRunSet(final RunSet runSet, final File file) throws IOException {
        checkNotNull(runSet);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeRunSet(runSet, writer);
        }
    }

    public static void writeRunSet(final RunSet runSet, final OutputStream outputStream) throws IOException {
        checkNotNull(runSet);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeRunSet(runSet, writer);
        }
    }


    public static void writeSample(final Sample sample, final Writer writer) throws IOException {
        checkNotNull(sample);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Sample.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL sampleSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.sample.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(sampleSchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(sample, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal Sample", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal Sample", e);
        }
    }

    public static void writeSample(final Sample sample, final File file) throws IOException {
        checkNotNull(sample);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeSample(sample, writer);
        }
    }

    public static void writeSample(final Sample sample, final OutputStream outputStream) throws IOException {
        checkNotNull(sample);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeSample(sample, writer);
        }
    }


    public static void writeStudy(final Study study, final Writer writer) throws IOException {
        checkNotNull(study);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Study.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL studySchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.study.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(studySchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(study, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal Study", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal Study", e);
        }
    }

    public static void writeStudy(final Study study, final File file) throws IOException {
        checkNotNull(study);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeStudy(study, writer);
        }
    }

    public static void writeStudy(final Study study, final OutputStream outputStream) throws IOException {
        checkNotNull(study);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeStudy(study, writer);
        }
    }


    public static void writeSubmission(final Submission submission, final Writer writer) throws IOException {
        checkNotNull(submission);
        checkNotNull(writer);

        try {
            JAXBContext context = JAXBContext.newInstance(Submission.class);
            Marshaller marshaller = context.createMarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL commonSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.common.xsd");
            URL submissionSchemaURL = SraReader.class.getResource("/org/nmdp/ngs/sra/xsd/SRA.submission.xsd");
            Schema schema = schemaFactory.newSchema(new StreamSource[] { new StreamSource(commonSchemaURL.toString()), new StreamSource(submissionSchemaURL.toString()) });
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(submission, writer);
        }
        catch (JAXBException e) {
            throw new IOException("could not marshal Submission", e);
        }
        catch (SAXException e) {
            throw new IOException("could not marshal Submission", e);
        }
    }

    public static void writeSubmission(final Submission submission, final File file) throws IOException {
        checkNotNull(submission);
        checkNotNull(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeSubmission(submission, writer);
        }
    }

    public static void writeSubmission(final Submission submission, final OutputStream outputStream) throws IOException {
        checkNotNull(submission);
        checkNotNull(outputStream);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
            writeSubmission(submission, writer);
        }
    }
}
