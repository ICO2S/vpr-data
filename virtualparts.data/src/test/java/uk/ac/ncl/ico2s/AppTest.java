package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.VPRException;
import org.virtualparts.data.Cacher;
import org.virtualparts.data.QueryParameters;
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
    
    public void un_testPopulateWithInteractionsInverterDesign_UserProvidedName() throws Exception
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
    
    public void untestPopulateWithInteractionsInverterDesign_CollectionFilter() throws Exception
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
	    
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "InverterDesign_CollectionFilter.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
	    List<URI> collections=new ArrayList<URI>();
	    collections.add(new URI("https://synbiohub.org/public/bsu/bsu_collection/1"));
	   // collections.add(new URI("https://synbiohub.org/public/igem/igem_collection/1"));
	        
	    QueryParameters params=new QueryParameters();
	    params.setCollectionURIs(collections);
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"newDesign", params);
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterDesign_CollectionFilter_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    public void un_testPopulateWithInteractionsInverterSingleDesign_CollectionFilter() throws Exception
    {
	 try
	 {
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
	    String design=design1 + ";" + design2;
	    
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "design");
	    
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterSingleDesign_CollectionFilter.xml")); 
		    
	    	
		 
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
	    List<URI> collections=new ArrayList<URI>();
	    collections.add(new URI("https://synbiohub.org/public/bsu/bsu_collection/1"));
	   // collections.add(new URI("https://synbiohub.org/public/igem/igem_collection/1"));
	        
	    QueryParameters params=new QueryParameters();
	    params.setCollectionURIs(collections);
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"newDesign", params);
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "InverterSingleDesign_CollectionFilter_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    
    public void untestCircuit08() throws Exception
    {
	 try
	 {
		SBOLDocument doc=SBOLReader.read(TestUtils.getOutputDir() + "circuit_0x08_input.xml");
		String endpoint="https://synbiohub.programmingbiology.org/sparql"; 
		List<URI> collections=new ArrayList<URI>();
	    collections.add(new URI("https://synbiohub.programmingbiology.org/public/Cello_VPRGeneration_Paper/Model_consensus_collection/1"));
	    QueryParameters params=new QueryParameters();
	    params.setCollectionURIs(collections);
		//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"newDesign",params);
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "circuit_0x08_output.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    public void testCache() throws Exception
    {
	 try
	 {
		SBOLDocument doc=SBOLReader.read(TestUtils.getOutputDir() + "circuit_0x08_input.xml");
		Cacher cacher=new Cacher();
		cacher.putInCache(doc, 1);
		SBOLDocument doc2=cacher.retrieveFromCache(1);
		if (doc2==null) {
			throw new VPRException("Cache does not work!");
		}
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
