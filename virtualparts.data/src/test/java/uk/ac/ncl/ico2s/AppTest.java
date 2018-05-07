package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
 
   

    
    public void un_testPopulateWithInteractionsNegativeAutoRegulatoryDesign() throws Exception
    {
	 try
	 {
		//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
		SBOLDocument doc=SBOLReader.read(new File(TestUtils.getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "my_NegativeAutoRegulatoryDesign_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    
    public void un_testPopulateWithInteractionsNegativeAutoRegulatoryeCelloDesign() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String rbsCdsTerDesign="BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, rbsCdsTerDesign), "rbsCdsTerDesign");
	    String design="BO_27632:prom;rbsCdsTerDesign:eng";
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "NegativeAutoRegulatoryDesign_Cello");
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "my_NegativeAutoRegulatoryCelloDesign.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "my_NegativeAutoRegulatoryCelloDesign_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    public void un_testPopulateWithInteractionsInverterDesign() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
	    
	    
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design1), "design1");
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design2), "design2");
	    
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    public void testPopulateWithInteractionsInverterDesign_UserProvidedName() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
	    
	    
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design1), "design1");
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design2), "design2");
	    
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_UserProvidedName.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"newDesign");
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_UserProvidedName_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    
    /*public void testPopulateWithInteractionsInverterSingleDesign() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
	    String toggleswicth="design1:eng;design2:eng";
	    
	    SVPWriteHandler.convertToSBOL(doc,getSVPDesign(base, design1), "design1");
	    SVPWriteHandler.convertToSBOL(doc,getSVPDesign(base, design2), "design2");
	    SVPWriteHandler.convertToSBOL(doc,getSVPDesign(base, toggleswicth), "toggleswicth");
		    
	    SBOLHandler.write(doc, new File(getOutputDir() + "inverterSingleDesignExample.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(getOutputDir() + "inverterSingleDesignExample_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    */
    
    
	
    
    
    public static String endpoint="https://synbiohub.org/sparql";
    
    
}
