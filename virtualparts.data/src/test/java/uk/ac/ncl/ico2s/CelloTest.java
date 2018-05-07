package uk.ac.ncl.ico2s;

import java.io.File;
import java.net.URI;

import org.sbolstandard.core2.SBOLDocument;
import org.virtualparts.data.SBOLInteractionAdder_GeneCentric;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.SVPWriteHandler;

import junit.framework.TestCase;

public class CelloTest extends TestCase {

	
    /*public void testpopulateWithInteractionsFromDB_AraSensor() throws Exception
    {
	 try
	 {
		//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
		SBOLDocument doc=new SBOLDocument();
    	//String base="http://synbiohub.org/public/goksel/fourinputandsensor/";
    	String base="http://synbiohub.ico2s.org:7777/public/Fourinputsensor/";
    	
    	doc.setDefaultURIprefix(base);
    	
    	//Designs
    	String design="Promoter:prom;RBS:rbs;araC:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "araC_Generator");
    	
    	design="pBAD:prom;RBS:rbs;ipgC:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "IpgC_Generator");
    	
    	SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create("http://synbiohub.ico2s.org:7777/sparql"));
		interactionAdder.addInteractions(doc);   
		
    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "Arac_IpgC_populateWithInteractionsFromDB_AraSensor.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }*/
    
    /*public void test_Ara_atC_SwitchFromDB_AraSensor() throws Exception
    {
	 try
	 {
		//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
		SBOLDocument doc=new SBOLDocument();
    	//String base="http://synbiohub.org/public/goksel/fourinputandsensor/";
    	String base="http://synbiohub.ico2s.org:7777/public/Fourinputsensor/";
    	String endpoint="http://synbiohub.ico2s.org:7777/sparql";
    	
		//String base="https://synbiohub.utah.edu/public/ACS_SynBio_2017_Cello_Datatset_v4/";
    	//String endpoint="http://synbiohub.ico2s.org:7777/sparql";
    	
    	
    	doc.setDefaultURIprefix(base);
    	
    	//Designs
    	String design="pBAD:prom;RBS:rbs;tetR:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "TetR_Generator");
    	
    	design="pTet:prom;RBS:rbs;araC:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "AraC_Generator");
    	
    	SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);   
		
    	 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "Ara_atC_switch_populateWithInteractionsFromDB_AraSensor.xml")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    */
	//https://synbiohub.utah.edu/public/ACS_SynBio_2017_Cello_Datatset_v4/http___cellocad_org_pAmtR/1
    public void No_test_AmtR_BetI_SwitchFromDB_AraSensor() throws Exception
    {
	 try
	 {
		//String sbol=TestUtil.getResourceFile("sbol/AraSensor.xml");
		SBOLDocument doc=new SBOLDocument();
    	//String base="http://synbiohub.org/public/goksel/fourinputandsensor/";
    	
		//String base="http://synbiohub.ico2s.org:7777/public/Fourinputsensor/";
    	//String endpoint="http://synbiohub.ico2s.org:7777/sparql";
    	
		String base="https://synbiohub.utah.edu/public/ACS_SynBio_2017_Cello_Datatset_v5/";
    	String endpoint="https://synbiohub.utah.edu/sparql";
    	
    	
    	doc.setDefaultURIprefix(base);
    	
    	//Designs
    	String design="pTac:prom;RBS:rbs;BetI:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc,TestUtils.getSVPDesign(base, design), "pTac_BetI_Generator");
    	
    	design="pAmtR:prom;RBS:rbs;BetI:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "pAmtR_BetI_Generator");
    	
    	design="pTet:prom;RBS:rbs;AmtR:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "pTet_AmtR_Generator");
    	
    	design="pBetI:prom;RBS:rbs;AmtR:cds;Terminator:ter";
    	SVPWriteHandler.convertToSBOL(doc, TestUtils.getSVPDesign(base, design), "pBetI_AmtR_Generator");
    	
    	
    	SBOLInteractionAdder_GeneCentric interactionAdder=new SBOLInteractionAdder_GeneCentric(URI.create(endpoint));
		interactionAdder.addInteractions(doc);   
		
		SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "input_AmtR_BetI_switch_populateWithInteractionsFromDB_AraSensor.sbol")); 
		 	   
	    SBOLHandler.write(doc, new File(TestUtils.getOutputDir() + "output_AmtR_BetI_switch_populateWithInteractionsFromDB_AraSensor.sbol")); 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
    public void testWriteSBOL() throws Exception
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
		    
	    	
		 
	
	 }
	 catch (Exception e)
	 {
		 e.printStackTrace();
		 throw e;
	 }
    }
}
