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

public class SingleTest extends TestCase {
    public void PopulateWithInteractionsNegativeAutoRegulatoryeCelloDesign() throws Exception
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
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "testSBOLWrite.xml")); 
	    		
	    SBOLDocument newDoc=SBOLReader.read(new File(TestUtils.getOutputDir() + "testSBOLWrite.xml"));
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    
    public void testInteractionsCopiedToTopLevel() throws Exception
    {
	 try
	 {
		SBOLDocument doc=SBOLReader.read(TestUtils.getOutputDir() + "InteractionsCopiedToTopLevelInput.sbol");
		String endpoint="https://synbiohub.programmingbiology.org/sparql"; 
		List<URI> collections=new ArrayList<URI>();
	    collections.add(new URI("https://synbiohub.programmingbiology.org/public/Eco1C1G1T1/Eco1C1G1T1_collection/1"));
	    QueryParameters params=new QueryParameters();
	    params.setCollectionURIs(collections);
		SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint),"newDesign",params);
		interactionAdder.addInteractions(doc);    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "InteractionsCopiedToTopLevelOutput.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }

}
