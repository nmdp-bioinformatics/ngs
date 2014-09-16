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

import static org.nmdp.ngs.sra.SraWriter.writeAnalysis;
import static org.nmdp.ngs.sra.SraWriter.writeExperiment;
import static org.nmdp.ngs.sra.SraWriter.writeRunSet;
import static org.nmdp.ngs.sra.SraWriter.writeSample;
import static org.nmdp.ngs.sra.SraWriter.writeStudy;
import static org.nmdp.ngs.sra.SraWriter.writeSubmission;

import java.net.URL;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;

import org.nmdp.ngs.sra.jaxb.analysis.Analysis;
import org.nmdp.ngs.sra.jaxb.analysis.AnalysisFileType;

import org.nmdp.ngs.sra.jaxb.experiment.Experiment;
import org.nmdp.ngs.sra.jaxb.experiment.LibraryType;
import org.nmdp.ngs.sra.jaxb.experiment.LibraryDescriptorType;
import org.nmdp.ngs.sra.jaxb.experiment.PlatformType;
import org.nmdp.ngs.sra.jaxb.experiment.SampleDescriptorType;

import org.nmdp.ngs.sra.jaxb.run.Run;
import org.nmdp.ngs.sra.jaxb.run.RunSet;

import org.nmdp.ngs.sra.jaxb.sample.Sample;
import org.nmdp.ngs.sra.jaxb.study.Study;
import org.nmdp.ngs.sra.jaxb.submission.Submission;

/**
 * Unit test for SraWriter.
 */
public final class SraWriterTest {
    private Analysis analysis;
    private Experiment experiment;
    private RunSet runSet;
    private Sample sample;
    private Study study;
    private Submission submission;

    @Before
    public void setUp() {
        analysis = new Analysis();
        analysis.setTitle("Title");
        analysis.setDescription("Description");
        Analysis.AnalysisType analysisType = new Analysis.AnalysisType();
        analysisType.setReferenceSequence("ReferenceSequence");
        analysis.setAnalysisType(analysisType);
        Analysis.Files files = new Analysis.Files();
        AnalysisFileType fileType = new AnalysisFileType();
        fileType.setFilename("Filename");
        fileType.setFiletype("fasta");
        fileType.setChecksumMethod("MD5");
        fileType.setChecksum("00e829c1e3d1a25474ce230f7544c6e0");
        files.getFiles().add(fileType);
        analysis.setFiles(files);

        experiment = new Experiment();
        experiment.setTitle("Title");
        Experiment.StudyRef studyRef = new Experiment.StudyRef();
        experiment.setStudyRef(studyRef);
        LibraryType design = new LibraryType();
        design.setDesignDescription("DesignDescription");
        SampleDescriptorType sampleDescriptor = new SampleDescriptorType();
        design.setSampleDescriptor(sampleDescriptor);
        LibraryDescriptorType libraryDescriptor = new LibraryDescriptorType();
        libraryDescriptor.setLibraryStrategy("WGS");
        libraryDescriptor.setLibrarySource("GENOMIC");
        libraryDescriptor.setLibrarySelection("PCR");
        LibraryDescriptorType.LibraryLayout libraryLayout = new LibraryDescriptorType.LibraryLayout();
        LibraryDescriptorType.LibraryLayout.Single single = new LibraryDescriptorType.LibraryLayout.Single();
        libraryLayout.setSingle(single);
        libraryDescriptor.setLibraryLayout(libraryLayout);
        design.setLibraryDescriptor(libraryDescriptor);
        experiment.setDesign(design);
        PlatformType platform = new PlatformType();
        PlatformType.Illumina illumina = new PlatformType.Illumina();
        illumina.setInstrumentModel("Illumina HiSeq 2500");
        platform.setIllumina(illumina);
        experiment.setPlatform(platform);

        runSet = new RunSet();
        Run run = new Run();
        Run.ExperimentRef experimentRef = new Run.ExperimentRef();
        run.setExperimentRef(experimentRef);
        runSet.getRuns().add(run);

        sample = new Sample();
        Sample.SampleName sampleName = new Sample.SampleName();
        sample.setSampleName(sampleName);

        study = new Study();
        Study.Descriptor descriptor = new Study.Descriptor();
        study.setDescriptor(descriptor);

        submission = new Submission();
    }


    @Test(expected=NullPointerException.class)
    public void testWriteAnalysisNullWriter() throws Exception {
        writeAnalysis(analysis, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteAnalysisNullFile() throws Exception {
        writeAnalysis(analysis, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteAnalysisNullOutputStream() throws Exception {
        writeAnalysis(analysis, (OutputStream) null);
    }

    @Test
    public void testWriteAnalysisWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeAnalysis(analysis, writer);
        }
    }

    @Test
    public void testWriteAnalysisFile() throws Exception {
        writeAnalysis(analysis, createFile());
    }

    @Test
    public void testWriteAnalysisOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeAnalysis(analysis, outputStream);
        }
    }


    @Test(expected=NullPointerException.class)
    public void testWriteExperimentNullWriter() throws Exception {
        writeExperiment(experiment, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteExperimentNullFile() throws Exception {
        writeExperiment(experiment, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteExperimentNullOutputStream() throws Exception {
        writeExperiment(experiment, (OutputStream) null);
    }

    @Test
    public void testWriteExperimentWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeExperiment(experiment, writer);
        }
    }

    @Test
    public void testWriteExperimentFile() throws Exception {
        writeExperiment(experiment, createFile());
    }

    @Test
    public void testWriteExperimentOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeExperiment(experiment, outputStream);
        }
    }


    @Test(expected=NullPointerException.class)
    public void testWriteRunSetNullWriter() throws Exception {
        writeRunSet(runSet, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRunSetNullFile() throws Exception {
        writeRunSet(runSet, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteRunSetNullOutputStream() throws Exception {
        writeRunSet(runSet, (OutputStream) null);
    }

    @Test
    public void testWriteRunSetWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeRunSet(runSet, writer);
        }
    }

    @Test
    public void testWriteRunSetFile() throws Exception {
        writeRunSet(runSet, createFile());
    }

    @Test
    public void testWriteRunSetOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeRunSet(runSet, outputStream);
        }
    }


    @Test(expected=NullPointerException.class)
    public void testWriteSampleNullWriter() throws Exception {
        writeSample(sample, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteSampleNullFile() throws Exception {
        writeSample(sample, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteSampleNullOutputStream() throws Exception {
        writeSample(sample, (OutputStream) null);
    }

    @Test
    public void testWriteSampleWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeSample(sample, writer);
        }
    }

    @Test
    public void testWriteSampleFile() throws Exception {
        writeSample(sample, createFile());
    }

    @Test
    public void testWriteSampleOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeSample(sample, outputStream);
        }
    }


    @Test(expected=NullPointerException.class)
    public void testWriteStudyNullWriter() throws Exception {
        writeStudy(study, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteStudyNullFile() throws Exception {
        writeStudy(study, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteStudyNullOutputStream() throws Exception {
        writeStudy(study, (OutputStream) null);
    }

    @Test
    public void testWriteStudyWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeStudy(study, writer);
        }
    }

    @Test
    public void testWriteStudyFile() throws Exception {
        writeStudy(study, createFile());
    }

    @Test
    public void testWriteStudyOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeStudy(study, outputStream);
        }
    }


    @Test(expected=NullPointerException.class)
    public void testWriteSubmissionNullWriter() throws Exception {
        writeSubmission(submission, (Writer) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteSubmissionNullFile() throws Exception {
        writeSubmission(submission, (File) null);
    }

    @Test(expected=NullPointerException.class)
    public void testWriteSubmissionNullOutputStream() throws Exception {
        writeSubmission(submission, (OutputStream) null);
    }

    @Test
    public void testWriteSubmissionWriter() throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(createFile()))) {
            writeSubmission(submission, writer);
        }
    }

    @Test
    public void testWriteSubmissionFile() throws Exception {
        writeSubmission(submission, createFile());
    }

    @Test
    public void testWriteSubmissionOutputStream() throws Exception {
        try (OutputStream outputStream = createOutputStream()) {
            writeSubmission(submission, outputStream);
        }
    }


    private static File createFile() throws Exception {
        File file = File.createTempFile("sraWriterTest", ".xml");
        file.deleteOnExit();
        return file;
    }

    private static OutputStream createOutputStream() throws Exception {
        return new ByteArrayOutputStream();
    }
}
