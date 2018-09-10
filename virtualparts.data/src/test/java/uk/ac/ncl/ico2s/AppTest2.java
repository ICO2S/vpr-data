package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data2.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.TestCase;

public class AppTest2 extends TestCase {

	 public void un_testPopulateWithInteractionsInverterDesign2_v2() throws Exception
	    {
		 try
		 {
			String endpoint="https://synbiohub.org/sparql";
			 
			SBOLDocument doc=new SBOLDocument();
		    String base="https://synbiohub.org/public/bsu/";
		    doc.setDefaultURIprefix(base);
		   
		    //Designs
		    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
		    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
		    
		    
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design1), "design1");
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design2), "design2");
		    
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample2_v2.xml")); 
			    			 
			//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
			SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
			interactionAdder.addInteractions(doc);    	 	   
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample2_output_v2.xml")); 
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	 
	 //This does not find interactions
	 public void un_testYuvalDemo() throws Exception
	    {
		 try
		 {
			 String endpoint="https://synbiohub.org/sparql";//https://synbiohub.utah.edu/sparql";
				 
			//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
			SBOLDocument doc=SBOLReader.read(new File(TestUtils.getOutputDir() + "/" + "YuvalDemo.sbol"));
			SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
			interactionAdder.addInteractions(doc);    	 	   
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "YuvalDemo_output.sbol")); 
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	 

	 public void un_testYeastDemo() throws Exception
	    {
		 try
		 {
			 String endpoint="https://synbiohub.utah.edu/sparql";
			//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
			SBOLDocument doc=SBOLReader.read(new File(TestUtils.getOutputDir() + "/" + "UWYeastDemo.sbol"));
			SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
			interactionAdder.addInteractions(doc);    	 	   
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "UWYeastDemooutput.sbol")); 
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	    
	 
	public void un_testNegativeAutoRegulatory_v2() throws Exception
    {
	 try
	 {
		String endpoint="https://synbiohub.org/sparql";
		  
		SBOLDocument doc=new SBOLDocument();
	    String base="https://synbiohub.org/public/bsu/";
	    doc.setDefaultURIprefix(base);
	   
	    //Designs
	    String design="BO_27632:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
	    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "testNegativeAutoRegulatory_v2");
	    
	    SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);    	 	   
	    
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "testNegativeAutoRegulatory_v2.xml")); 	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
	
	  public void un_testPopulateWithInteractionsInverterDesign_v2() throws Exception
	    {
		 try
		 {
			String endpoint="https://synbiohub.org/sparql";
			 
			SBOLDocument doc=new SBOLDocument();
		    String base="https://synbiohub.org/public/bsu/";
		    doc.setDefaultURIprefix(base);
		   
		    //Designs
		    String design1="BO_27661:prom;BO_27783:rbs;BO_32077:cds;BO_4257:ter";
		    String design2="BO_27632:prom;BO_27783:rbs;BO_31554:cds;BO_4257:ter";
		    
		    
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design1), "design1");
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design2), "design2");
		    
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_v2.xml")); 
			    			 
			//SBOLDocument doc=SBOLReader.read(new File(getOutputDir() + "/" + "my_NegativeAutoRegulatoryDesign.xml"));
			SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
			interactionAdder.addInteractions(doc);    	 	   
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "inverterExample_output_v2.xml")); 
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	  
	  public void test_VPRBug() throws Exception
	    {
		 try
		 {
			 
			 String endpoint="https://synbiohub.programmingbiology.org/sparql";//https://synbiohub.org/sparql";//https://synbiohub.utah.edu/sparql";
			 
				//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
				SBOLDocument doc=SBOLReader.read(new File(TestUtils.getOutputDir() + "/" + "VPR_Bug.xml"));
				
				List<URI> collections=new ArrayList<URI>();
				collections.add(new URI("https://synbiohub.programmingbiology.org/public/Cello_Parts/Cello_Parts_collection/1"));
				        
				QueryParameters params=new QueryParameters();
				params.setCollectionURIs(collections);
				    
				SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"test",params);
				interactionAdder.addInteractions(doc);    	 	   
			    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "VPR_Bug_output.xml")); 
			
			    
			    
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	    
	    
}
