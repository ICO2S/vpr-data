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
	  
	  public void no_test_VPRBug() throws Exception
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
	  
	  public void no_test_Recursive_subParents() throws Exception
	    {
		 try
		 {
			 String endpoint="https://synbiohub.org/sparql";//https://synbiohub.utah.edu/sparql";
				
			SBOLDocument doc=new SBOLDocument();
		    String base="https://synbiohub.org/public/bsu/";
		    doc.setDefaultURIprefix(base);
		   
		    //Designs
		    String rbsCdsTerDesign="BO_27783:rbs;BO_32077:cds;BO_4257:ter";
		    
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, rbsCdsTerDesign), "rbsCdsTerDesign");
		    String design="BO_27632:prom;rbsCdsTerDesign:eng";
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "NegativeAutoRegulatoryDesign_Cello_data2");
		    
		    SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		    interactionAdder.addInteractions(doc); 
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "NegativeAutoRegulatoryDesign_Cello_data2.xml")); 
		
		    /*
		    MD_top
		    	FuncComp TU
		    		MapsTo BO_27682 (promoter) to BO_27682_top (promoter_top)
		    		MapsTo rbsCdsTerDesign (gene) to rbsCdsTerDesign_top (gene_top)
		    		
		    	FuncComp rbsCdsTerDesign_top (gene_top)
		    		MapsTo BO_32077(cds) to BO_32077_top(cds_top)
		    	
		    	FuncComp BO_32077
		    	
		    	FuncComp BO_27682 
		    	
		    	FuncComp BO_10845 
		    */
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	  
	  public void test_Recursive_subParents2() throws Exception
	    {
		 try
		 {
			 String endpoint="https://synbiohub.org/sparql";//https://synbiohub.utah.edu/sparql";
				
			SBOLDocument doc=new SBOLDocument();
		    String base="https://synbiohub.org/public/bsu/";
		    doc.setDefaultURIprefix(base);
		   
		    //Designs
		    String rbsCdsDesign="BO_27783:rbs;BO_32077:cds";
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, rbsCdsDesign), "rbsCdsDesign");
		    
		    String rbsCdsTerDesign="rbsCdsDesign:eng;BO_4257:ter";
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, rbsCdsTerDesign), "rbsCdsTerDesign");
		    
		    String design="BO_27632:prom;rbsCdsTerDesign:eng";
		    SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "NegativeAutoRegulatoryDesign_Cello_2_data2");
		    
		    SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		    interactionAdder.addInteractions(doc); 
		    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "NegativeAutoRegulatoryDesign_Cello_2_data2.xml")); 
		
		    /*
		    MD_top
		    	FuncComp TU
		    		MapsTo BO_27682 (promoter) to BO_27682_top (promoter_top)
		    		MapsTo rbsCdsTer to rbsCdsTer_top
		    	
		    	FuncComp rbsCdsTer_top
		    		MapsTo rbsCds to rbs_Cds_top
		    		
		    	FuncComp rbsCds_top
		    		MapsTo BO_32077(cds) to BO_32077_top(cds_top)
		    	
		    	FuncComp BO_32077
		    	
		    	FuncComp BO_27682 
		    	
		    	FuncComp BO_10845 
		    */
		
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw e;
		 }
	    }
	    
	    
}
