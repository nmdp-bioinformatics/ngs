
package org.nmdp.ngs.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.nmdp.ngs.hml.jaxb.Allele;
import org.nmdp.ngs.hml.jaxb.AlleleList;
import org.nmdp.ngs.hml.jaxb.Amplification;
import org.nmdp.ngs.hml.jaxb.ConsensusSequence;
import org.nmdp.ngs.hml.jaxb.DiploidCombination;
import org.nmdp.ngs.hml.jaxb.GenotypeList;
import org.nmdp.ngs.hml.jaxb.Glstring;
import org.nmdp.ngs.hml.jaxb.Gssp;
import org.nmdp.ngs.hml.jaxb.Haploid;
import org.nmdp.ngs.hml.jaxb.Hml;
import org.nmdp.ngs.hml.jaxb.Interpretation;
import org.nmdp.ngs.hml.jaxb.LocusBlock;
import org.nmdp.ngs.hml.jaxb.ObjectFactory;
import org.nmdp.ngs.hml.jaxb.Present;
import org.nmdp.ngs.hml.jaxb.Sample;
import org.nmdp.ngs.hml.jaxb.SbtNgs;
import org.nmdp.ngs.hml.jaxb.SbtSanger;
import org.nmdp.ngs.hml.jaxb.Sequence;
import org.nmdp.ngs.hml.jaxb.Sso;
import org.nmdp.ngs.hml.jaxb.Ssp;
import org.nmdp.ngs.hml.jaxb.TargetedRegion;
import org.nmdp.ngs.hml.jaxb.Typing;
import org.nmdp.ngs.hml.jaxb.TypingTestName;
import org.nmdp.ngs.hml.jaxb.TypingTestNames;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * @author dvaliga
 *
 */
public class HMLGenerator {

    /**
     * The HML message to generate.
     */
    private Hml newMsg = null;
    
    /**
     * HML JAXB ObjectFactory
     */
    static ObjectFactory hmlfact = new ObjectFactory();

    /**
     * Namespace hash of namespace to prefix.    
     */
    static final Map<String, String> hmlNSMap = new HashMap<String, String>();
    static {
        hmlNSMap.put("http://schemas.nmdp.org/spec/hml/0.9.4", "hml");
        hmlNSMap.put("http://schemas.nmdp.org/spec/hml/0.9.5", "hml");
        hmlNSMap.put("http://schemas.nmdp.org/spec/hml/0.9.6", "hml");
        hmlNSMap.put("http://schemas.nmdp.org/spec/hml/0.9.7", "hml");
        hmlNSMap.put("http://www.w3.org/2001/XMLSchema", "xs");
        hmlNSMap.put("https://gl.immunogenomics.org/gl-resource", "gl");
        hmlNSMap.put("https://gl.immunogenomics.org/gl-resource-xlink", "glx");
    }


    /**
     * HMLGenerator 
     */
    public HMLGenerator() {
        newMsg = hmlfact.createHml();
    }

    
    private Hml getHML() {
        return this.newMsg;
    }
    

    private void createHeaderInfo() throws IOException {
        String temp = this.displaySimpleOption("Enter a project name like 'LAB'", "LAB");
        getHML().setProjectName(temp);

        temp = this.displaySimpleOption("Enter HML version", "0.9.5");
        getHML().setVersion(temp);

        temp = this.displaySimpleOption("Enter NMDP reporting center code like '567'", null);
        getHML().setReportingCenter(temp);
    }
    

    private void createTypingMethods(Typing typing) throws IOException {
        
        String[] typingMethodMenu = new String[] {"SSO", "SSP", "SBT-Sanger", "SBT-NGS"};
        int selectedOption = displayOptionMenu("Select Typing Method", typingMethodMenu);
        switch(selectedOption) {
            case 1:
                //SSO
                createSSOElement(typing);
                break;
            case 2:
                //SSP
                createSSPElement(typing);
                break;
            case 3:
                //SBT-Sanger
                createSBTSanger(typing);
                break;
            case 4:
                //SBT-NGS
                createSBTNGS(typing);
                break;
        }
        
        String temp = this.displaySimpleOption("Do you want to model additional typing methods (SSO/SSP/SBT-Sanger/SBT-NGS)?", "N");
        if(temp != null && temp.equalsIgnoreCase("Y")) {
            createTypingMethods(typing);
        }
    }


    private void createSSOElement(Typing typing) throws IOException {
        Sso ssoNode = hmlfact.createSso();
        JAXBElement<Sso> ssoJaxb = hmlfact.createSso(ssoNode);
        String temp = this.displaySimpleOption("Enter a locus for the SSO node like 'HLA-A'", null);
        ssoNode.setLocus(temp);

        ssoNode.setScores("18811881188118811881188118811881");
        
        if(getHML().getTypingTestNames() != null && getHML().getTypingTestNames().size() > 0) {
//TODO - Bug in JAXB so ID/IDREF will fail until it is fixed
            //ssoNode.setRefId("sampleTest001");
        } else {
            String[] options = new String[] {"gtr", "nmdp-refid", "probe-name"};
            int option = this.displayOptionMenu("Test ID source for this SSO typing", options);
            ssoNode.setTestIdSource(options[option - 1]);
            ssoNode.setTestId("1234567");
        }
        typing.getTypingMethodOrInterpretation().add(ssoJaxb);     
    }
    
    
    private void createSSPElement(Typing typing) throws IOException {
        Ssp sspNode = hmlfact.createSsp();
        JAXBElement<Ssp> sspJaxb = hmlfact.createSsp(sspNode);
        String temp = this.displaySimpleOption("Enter a locus for the SSP node like 'HLA-A'", null);
        sspNode.setLocus(temp);

        sspNode.setScores("81188118811881188118811881188118");
        
        if(getHML().getTypingTestNames() != null && getHML().getTypingTestNames().size() > 0) {
//TODO - Bug in JAXB so ID/IDREF will fail until it is fixed
            //sspNode.setRefId("sampleTest001");
        } else {
            String[] options = new String[] {"gtr", "nmdp-refid", "probe-name"};
            int option = this.displayOptionMenu("Test ID source for this SSP typing", options);
            sspNode.setTestIdSource(options[option - 1]);
            sspNode.setTestId("1234567");
        }
        typing.getTypingMethodOrInterpretation().add(sspJaxb);
    }


    /**
     * SBT-Sanger typing-method
     * @param typing
     * @throws IOException
     */
    private void createSBTSanger(Typing typing) throws IOException {
        SbtSanger sbt = hmlfact.createSbtSanger();
        JAXBElement<SbtSanger> sbtJaxb = hmlfact.createSbtSanger(sbt);
        String temp = this.displaySimpleOption("Enter a locus for the SBT Sanger node like 'HLA-A'", null);
        sbt.setLocus(temp);

        if(getHML().getTypingTestNames() != null && getHML().getTypingTestNames().size() > 0) {
//TODO - Bug in JAXB so ID/IDREF will fail until it is fixed
            //sbt.setRefId("sampleTest001");
        } else {
            String[] options = new String[] {"gtr", "nmdp-refid", "probe-name"};
            int option = this.displayOptionMenu("Test ID source for this SSO typing", options);
            sbt.setTestIdSource(options[option - 1]);
            sbt.setTestId("1234567");
        }
        
        //AMPLIFICATION
        Amplification amp = hmlfact.createAmplification();
        String[] alpha = new String[] {"DNA", "RNA"};
        int option = this.displayOptionMenu("Select alphabet for SBT amplification data", alpha);
        amp.setAlphabet(alpha[option - 1]);
        amp.setRegisteredName("L999.A1.B2.C123456");
        amp.setValue("ATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGCATGC");
        sbt.setAmplification(amp);

        //GSSP
        temp = this.displaySimpleOption("Include GSSP for this SBT Sanger?", "N");
        if(temp != null && temp.equalsIgnoreCase("Y")) {
            Gssp gssp = hmlfact.createGssp();
            String[] gsspAttrs = new String[] {"registered-name", "primer-sequence", "primer-target"};
            option = this.displayOptionMenu("Select a GSSP primer identification method", gsspAttrs);
            switch(option) {
                case 1:
                    gssp.setRegisteredName("L999.A1.B2.C123456");
                    break;
                case 2:
                    gssp.setPrimerSequence("(PCR sequence value)");
                    break;
                case 3:
                    gssp.setPrimerTarget("(Primer target)");
                    break;
            }
            gssp.setValue("CATGCATGCATG");
            sbt.getGssp().add(gssp);
        }
        
        typing.getTypingMethodOrInterpretation().add(sbtJaxb);
    }


    /**
     * SBT-NGS typing-method
     * @param typing
     * @throws IOException
     */
    private void createSBTNGS(Typing typing) throws IOException {
        SbtNgs sbt = hmlfact.createSbtNgs();
        JAXBElement<SbtNgs> sbtJaxb = hmlfact.createSbtNgs(sbt);
        String temp = this.displaySimpleOption("Enter a locus for the SBT Sanger node like 'HLA-A'", null);
        sbt.setLocus(temp);

        if(getHML().getTypingTestNames() != null && getHML().getTypingTestNames().size() > 0) {
//TODO - Bug in JAXB so ID/IDREF will fail until it is fixed
            //sbt.setRefId("sampleTest001");
        } else {
            String[] options = new String[] {"gtr", "nmdp-refid", "probe-name"};
            int option = this.displayOptionMenu("Test ID source for this SSO typing", options);
            sbt.setTestIdSource(options[option - 1]);
            sbt.setTestId("1234567");
        }

        //Consensus Sequence
        ConsensusSequence conseq = hmlfact.createConsensusSequence();
            //URI read out-of-scope
        sbt.getConsensusSequence().add(conseq);
        
        TargetedRegion tregion = hmlfact.createTargetedRegion();
        tregion.setAssembly("GRCh38");
        tregion.setContig("6");
        tregion.setDescription("HLA-A exon 2");
        tregion.setStart(29942756L);
        tregion.setEnd(29943026L);
        tregion.setStrand("1");
        tregion.setId("CCDS34373.12");
        conseq.setTargetedRegion(tregion);
        
        String[] alpha = new String[] {"DNA", "RNA"};
        int option = this.displayOptionMenu("Select alphabet for SBT consensus sequence", alpha);
        
        Sequence seq1 = hmlfact.createSequence();
        seq1.setAlphabet(alpha[option - 1]);
        seq1.setValue("GGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTA");
        conseq.getSequence().add(seq1);

        Sequence seq2 = hmlfact.createSequence();
        seq2.setAlphabet(alpha[option - 1]);
        seq2.setValue("CTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGGCTAGG");
        conseq.getSequence().add(seq2);
        
        //Raw reads out-of-scope

        typing.getTypingMethodOrInterpretation().add(sbtJaxb);
    }

    
    /**
     * Creates the interpretation portion of the HML message.
     * @param typing
     * @throws IOException
     */
    private void createInterpretation(Typing typing) throws IOException {

        Interpretation interp = hmlfact.createInterpretation();
        
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xcal;
        try {
            xcal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            interp.setDate(xcal);
        } catch (DatatypeConfigurationException e) {
            System.err.println("Error creating interpretation date. " + e.getMessage());
        }

        String temp = this.displaySimpleOption("Enter the interpretation database", "IMGT/HLA");
        interp.setAlleleDb(temp);
        temp = this.displaySimpleOption("Enter the interpretation database version", "3.15.0");
        interp.setAlleleVersion(temp);

        typing.getTypingMethodOrInterpretation().add(interp);

        String[] interpMethodMenu = new String[] {"haploid", "gl-resource", "glstring", "genotype-list"};
        int selectedOption = displayOptionMenu("Select interpretation format", interpMethodMenu);
        switch(selectedOption) {
            case 1:
                //haploid
                createHaploidData(interp);
                break;
            case 2:
                //gl-resource
                createGLResource(interp);
                break;
            case 3:
                //glstring
                createGLString(interp);
                break;
            case 4:
                //genotype-list
                createGenotypeList(interp);
                break;
        }
    }


    private void createHaploidData(Interpretation interp) throws IOException {
        Haploid hap1 = hmlfact.createHaploid();

        String temp = this.displaySimpleOption("Enter a locus for the haploid like 'HLA-DRB1'", null);
        hap1.setLocus(temp);
        String[] hapMethod = new String[] {"DNA", "SER"};
        int option = this.displayOptionMenu("Select haploid method", hapMethod);
        hap1.setMethod(hapMethod[option - 1]);
        temp = this.displaySimpleOption("Enter haploid type like '01:02' or '03:YGKM'", null);
        hap1.setType(temp);

        interp.getHaploidOrGlResourceOrGenotypeList().add(hap1);

        //Enter additional haploid?
        temp = this.displaySimpleOption("Do you want to include an additional haploid?", "N");
        if(temp != null && temp.equalsIgnoreCase("Y")) {
            createHaploidData(interp);
        }
    }


    private void createGLResource(Interpretation interp) throws IOException {
        //GlResource glresource = hmlfact.createGlResource();

        //org.nmdp.ngs.hml.jaxb.gl.ObjectFactory glfact = new org.nmdp.ngs.hml.jaxb.gl.ObjectFactory();
        //glfact.createG

        //Not implemented
        System.out.println("\ngl-resource not implemented.\n");
        
        //interp.getHaploidOrGlResourceOrGenotypeList().add(glresource);
    }


    private void createGLString(Interpretation interp) throws IOException {
        Glstring glstr = hmlfact.createGlstring();
        glstr.setValue("HLA-DRB1*04:11:01+HLA-DRB1*04:07:01/HLA-DRB1*04:92");
        interp.getHaploidOrGlResourceOrGenotypeList().add(glstr);
    }

    
    private void createGenotypeList(Interpretation interp) throws IOException {
        GenotypeList glList = hmlfact.createGenotypeList();

        DiploidCombination dipCom = hmlfact.createDiploidCombination();
        glList.getDiploidCombination().add(dipCom);

        LocusBlock loc1 = hmlfact.createLocusBlock();
        dipCom.getLocusBlock().add(loc1);

        AlleleList all1 = hmlfact.createAlleleList();
        loc1.getAlleleList().add(all1);

        Allele allele1 = hmlfact.createAllele();
        allele1.setPresent(Present.Y);
        allele1.setValue("HLA-DRB1*03:01:01:01");
        all1.getAllele().add(allele1);

        Allele allele2 = hmlfact.createAllele();
        allele2.setPresent(Present.Y);
        allele2.setValue("HLA-DRB1*03:03");
        all1.getAllele().add(allele2);

        LocusBlock loc2 = hmlfact.createLocusBlock();
        dipCom.getLocusBlock().add(loc2);

        AlleleList all2 = hmlfact.createAlleleList();
        loc2.getAlleleList().add(all2);

        Allele allele3 = hmlfact.createAllele();
        allele3.setPresent(Present.Y);
        allele3.setValue("HLA-DRB3*02:03");
        all2.getAllele().add(allele3);

        interp.getHaploidOrGlResourceOrGenotypeList().add(glList);
    }

    
    /**
     * Creates typing-test-names and typing tests as references for other 
     * data in this message (SSO, etc.)
     */
    private void createTypingTests() {
        TypingTestNames namesObj = hmlfact.createTypingTestNames();
        namesObj.setRefId("sampleTest001");

        TypingTestName aTest = hmlfact.createTypingTestName();
        aTest.setName("Test1Name");
        namesObj.getTypingTestName().add(aTest);

        TypingTestName aTest2 = hmlfact.createTypingTestName();
        aTest2.setName("Test2Name");
        namesObj.getTypingTestName().add(aTest2);

        TypingTestName aTest3 = hmlfact.createTypingTestName();
        aTest3.setName("TestForEachProbeRead");
        namesObj.getTypingTestName().add(aTest3);

        getHML().getTypingTestNames().add(namesObj);
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("HML Generator Version 0.9.5");
        System.out.println("This utility was created to generate a sample HML format for labs and organizations ");
        System.out.println("intending to transmit histoimmunogenic data to the NMDP. This utility does NOT generate ");
        System.out.println("a complete HML message, but rather creates a skeleton structure based on answers to ");
        System.out.println("questions about the data you wish to transmit. The final result is expected to be ");
        System.out.println("edited with actual data before transmission.");
        System.out.println("Please review your message before submitting and make sure it is valid with respect ");
        System.out.println("to the public HML schema specification.");

        HMLGenerator generator = new HMLGenerator();
        try {
            //Header info
            generator.createHeaderInfo();

            String temp = generator.displaySimpleOption("Do you have typing data that refers to a typing test list?", "Y");
            if(temp != null && temp.equalsIgnoreCase("Y")) {
                generator.createTypingTests();
            }

            //Add single (required) sample
            Sample testSample = new Sample();
            generator.getHML().getSample().add(testSample);
            temp = generator.displaySimpleOption("Enter the 3-digit NMDP center-code to use for this sample.", null);
            if(temp == null || temp.trim().length() > 3) {
                testSample.setCenterCode("999");
            } else {
                testSample.setCenterCode(temp);
            }
            testSample.setId("1234-5678-9");

            //Typing for sample
            Typing typing = new Typing();
            String[] options = new String[] {"HLA", "KIR"};
            int option = generator.displayOptionMenu("Select GENE-FAMILY", options);
            typing.setGeneFamily(options[option - 1]);

            testSample.getTyping().add(typing);

            //Typing method data (must have at least 1)
            generator.createTypingMethods(typing);

            //Interpretation
            temp = generator.displaySimpleOption("Do you have interpretation data?", "Y");
            if(temp != null && temp.equalsIgnoreCase("Y")) {
                generator.createInterpretation(typing);
            }


            //Write HML
            System.out.println("\n\n--- COPY HML MESSAGE BELOW ---\n");
            
            StringWriter writer = new StringWriter();
            JAXBContext context;
            try {
                context = JAXBContext.newInstance(Hml.class.getPackage().getName());
                Marshaller m = context.createMarshaller();
                
                /* NamespacePrefixMapper hmlMapper = new NamespacePrefixMapper() {
                    @Override
                    public String getPreferredPrefix(String namespaceURI, String suggestionPrefix, boolean required) {
                        if(HMLGenerator.hmlNSMap.get(namespaceURI) != null) {
                            return suggestionPrefix;
                        }
                        return "";
                    }
                };

                m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", hmlMapper);
                */
                m.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://schemas.nmdp.org/spec/hml/0.9.5/hml-0.9.5.xsd");
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.marshal(generator.getHML(), writer);

                // output string to console
                String theXML = writer.toString();
                System.out.println(theXML);
            } catch (JAXBException e) {
                System.err.println("Failed to write HML output - " + e.getMessage());
                e.printStackTrace();
            } finally {
                System.out.println("\nDONE\n");
            }

            
        } catch(IOException ex) {
            System.err.println("There was an error while generating HML - " + ex.getMessage());
        }
    }
    
    
    /**
     * Displays a formatted menu of options for more complex questions that may 
     * have multiple answers.  Returns the user selection which should be a 
     * number that corresponds to one of the options given.
     * @param title
     * @param options
     * @return int
     * @throws IOException
     */
    public int displayOptionMenu(String title, String[] options) throws IOException {
        System.out.println("\n=== " + title + " ===");
        for(int optNum = 0; optNum < options.length; optNum++) {
            System.out.println(" (" + (optNum + 1) + ") " + options[optNum]);
        }
        System.out.println("---------- ");
        InputStreamReader inReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inReader);
        String input = reader.readLine();
        int selectedOption = 0;
        try {
            selectedOption = Integer.parseInt(input);
            if(selectedOption < 1 || selectedOption > options.length) {
                throw new Exception("Option out of range");
            }
        } catch(NumberFormatException nfe) {
            System.err.println("\nINVALID RESPONSE - SELECT THE NUMBER OF THE OPTION YOU WISH TO CHOOSE\n");
            return displayOptionMenu(title, options);
        } catch(Exception e) {
            System.err.println("\nINVALID OPTION SELECTED\n");
            return displayOptionMenu(title, options);
        }
        return selectedOption;
    }


    /**
     * Displays a formatted prompt for simple user input.  This would be a simple 
     * question or Y/N answers.  
     * @param prompt
     * @param defaultVal
     * @return String
     * @throws IOException
     */
    public String displaySimpleOption(String prompt, String defaultVal) throws IOException {
        System.out.print("\n" + prompt + ((defaultVal != null && defaultVal.trim().length() > 0) ? " [" + defaultVal + "] > " : " > "));
        InputStreamReader inReader = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(inReader);
        String input = reader.readLine();
        try {
            if(defaultVal != null && (input == null || input.trim().length() < 1)) {
                return defaultVal;
            } else if(input == null || input.trim().length() < 1) {
                //No default and no valid response
                throw new Exception("Empty response");
            } else {
                return input;
            }
        } catch(Exception e) {
            System.err.println("\nINVALID INPUT\n");
            return displaySimpleOption(prompt, defaultVal);
        }
    }
}
