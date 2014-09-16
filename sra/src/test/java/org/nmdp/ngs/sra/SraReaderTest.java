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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import static org.nmdp.ngs.sra.SraReader.readAnalysis;
import static org.nmdp.ngs.sra.SraReader.readExperiment;
import static org.nmdp.ngs.sra.SraReader.readRunSet;
import static org.nmdp.ngs.sra.SraReader.readSample;
import static org.nmdp.ngs.sra.SraReader.readStudy;
import static org.nmdp.ngs.sra.SraReader.readSubmission;

import java.net.URL;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;

import com.google.common.io.Files;
import com.google.common.io.Resources;

import org.junit.Test;

import org.nmdp.ngs.sra.jaxb.analysis.Analysis;
import org.nmdp.ngs.sra.jaxb.experiment.Experiment;
import org.nmdp.ngs.sra.jaxb.run.RunSet;
import org.nmdp.ngs.sra.jaxb.sample.Sample;
import org.nmdp.ngs.sra.jaxb.study.Study;
import org.nmdp.ngs.sra.jaxb.submission.Submission;

/**
 * Unit test for SraReader.
 */
public final class SraReaderTest {

    @Test(expected=NullPointerException.class)
    public void testReadAnalysisNullReader() throws Exception {
        readAnalysis((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadAnalysisNullFile() throws Exception {
        readAnalysis((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadAnalysisNullURL() throws Exception {
        readAnalysis((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadAnalysisNullInputStream() throws Exception {
        readAnalysis((InputStream) null);
    }

    @Test
    public void testReadAnalysisFile() throws Exception {
        validate(readAnalysis(createFile("sra-analysis-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadAnalysisEmptyFile() throws Exception {
        readAnalysis(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadAnalysisInvalidSyntaxFile() throws Exception {
        readAnalysis(createFile("invalid-analysis-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadAnalysisInvalidSchemaFile() throws Exception {
        readAnalysis(createFile("invalid-analysis-schema.xml"));
    }

    @Test
    public void testReadAnalysisURL() throws Exception {
        validate(readAnalysis(createURL("sra-analysis-example.xml")));
    }

    @Test
    public void testReadAnalysisInputStream() throws Exception {
        validate(readAnalysis(createInputStream("sra-analysis-example.xml")));
    }

    private static void validate(final Analysis analysis) {
        assertNotNull(analysis);
    }


    @Test(expected=NullPointerException.class)
    public void testReadExperimentNullReader() throws Exception {
        readExperiment((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadExperimentNullFile() throws Exception {
        readExperiment((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadExperimentNullURL() throws Exception {
        readExperiment((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadExperimentNullInputStream() throws Exception {
        readExperiment((InputStream) null);
    }

    @Test
    public void testReadExperimentFile() throws Exception {
        validate(readExperiment(createFile("sra-experiment-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadExperimentEmptyFile() throws Exception {
        readExperiment(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadExperimentInvalidSyntaxFile() throws Exception {
        readExperiment(createFile("invalid-experiment-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadExperimentInvalidSchemaFile() throws Exception {
        readExperiment(createFile("invalid-experiment-schema.xml"));
    }

    @Test
    public void testReadExperimentURL() throws Exception {
        validate(readExperiment(createURL("sra-experiment-example.xml")));
    }

    @Test
    public void testReadExperimentInputStream() throws Exception {
        validate(readExperiment(createInputStream("sra-experiment-example.xml")));
    }

    private static void validate(final Experiment experiment) {
        assertNotNull(experiment);
    }


    @Test(expected=NullPointerException.class)
    public void testReadRunSetNullReader() throws Exception {
        readRunSet((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadRunSetNullFile() throws Exception {
        readRunSet((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadRunSetNullURL() throws Exception {
        readRunSet((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadRunSetNullInputStream() throws Exception {
        readRunSet((InputStream) null);
    }

    @Test
    public void testReadRunSetFile() throws Exception {
        validate(readRunSet(createFile("sra-run-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadRunSetEmptyFile() throws Exception {
        readRunSet(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadRunSetInvalidSyntaxFile() throws Exception {
        readRunSet(createFile("invalid-run-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadRunSetInvalidSchemaFile() throws Exception {
        readRunSet(createFile("invalid-run-schema.xml"));
    }

    @Test
    public void testReadRunSetURL() throws Exception {
        validate(readRunSet(createURL("sra-run-example.xml")));
    }

    @Test
    public void testReadRunSetInputStream() throws Exception {
        validate(readRunSet(createInputStream("sra-run-example.xml")));
    }

    private static void validate(final RunSet runSet) {
        assertNotNull(runSet);
    }


    @Test(expected=NullPointerException.class)
    public void testReadSampleNullReader() throws Exception {
        readSample((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSampleNullFile() throws Exception {
        readSample((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSampleNullURL() throws Exception {
        readSample((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSampleNullInputStream() throws Exception {
        readSample((InputStream) null);
    }

    @Test
    public void testReadSampleFile() throws Exception {
        validate(readSample(createFile("sra-sample-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadSampleEmptyFile() throws Exception {
        readSample(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadSampleInvalidSyntaxFile() throws Exception {
        readSample(createFile("invalid-sample-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadSampleInvalidSchemaFile() throws Exception {
        readSample(createFile("invalid-sample-schema.xml"));
    }

    @Test
    public void testReadSampleURL() throws Exception {
        validate(readSample(createURL("sra-sample-example.xml")));
    }

    @Test
    public void testReadSampleInputStream() throws Exception {
        validate(readSample(createInputStream("sra-sample-example.xml")));
    }

    private static void validate(final Sample sample) {
        assertNotNull(sample);
    }


    @Test(expected=NullPointerException.class)
    public void testReadStudyNullReader() throws Exception {
        readStudy((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadStudyNullFile() throws Exception {
        readStudy((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadStudyNullURL() throws Exception {
        readStudy((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadStudyNullInputStream() throws Exception {
        readStudy((InputStream) null);
    }

    @Test
    public void testReadStudyFile() throws Exception {
        validate(readStudy(createFile("sra-study-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadStudyEmptyFile() throws Exception {
        readStudy(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadStudyInvalidSyntaxFile() throws Exception {
        readStudy(createFile("invalid-study-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadStudyInvalidSchemaFile() throws Exception {
        readStudy(createFile("invalid-study-schema.xml"));
    }

    @Test
    public void testReadStudyURL() throws Exception {
        validate(readStudy(createURL("sra-study-example.xml")));
    }

    @Test
    public void testReadStudyInputStream() throws Exception {
        validate(readStudy(createInputStream("sra-study-example.xml")));
    }

    private static void validate(final Study study) {
        assertNotNull(study);
    }


    @Test(expected=NullPointerException.class)
    public void testReadSubmissionNullReader() throws Exception {
        readSubmission((Reader) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSubmissionNullFile() throws Exception {
        readSubmission((File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSubmissionNullURL() throws Exception {
        readSubmission((URL) null);
    }

    @Test(expected=NullPointerException.class)
    public void testReadSubmissionNullInputStream() throws Exception {
        readSubmission((InputStream) null);
    }

    @Test
    public void testReadSubmissionFile() throws Exception {
        validate(readSubmission(createFile("sra-submission-example.xml")));
    }

    @Test(expected=IOException.class)
    public void testReadSubmissionEmptyFile() throws Exception {
        readSubmission(createFile("empty.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadSubmissionInvalidSyntaxFile() throws Exception {
        readSubmission(createFile("invalid-submission-syntax.xml"));
    }

    @Test(expected=IOException.class)
    public void testReadSubmissionInvalidSchemaFile() throws Exception {
        readSubmission(createFile("invalid-submission-schema.xml"));
    }

    @Test
    public void testReadSubmissionURL() throws Exception {
        validate(readSubmission(createURL("sra-submission-example.xml")));
    }

    @Test
    public void testReadSubmissionInputStream() throws Exception {
        validate(readSubmission(createInputStream("sra-submission-example.xml")));
    }

    private static void validate(final Submission submission) {
        assertNotNull(submission);
    }


    private static URL createURL(final String name) throws Exception {
        return SraReaderTest.class.getResource(name);
    }

    private static InputStream createInputStream(final String name) throws IOException {
        return SraReaderTest.class.getResourceAsStream(name);
    }

    private static File createFile(final String name) throws IOException {
        File file = File.createTempFile("sraReaderTest", ".xml");
        Files.write(Resources.toByteArray(SraReaderTest.class.getResource(name)), file);
        file.deleteOnExit();
        return file;
    }
}
