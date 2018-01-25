package org.virtualparts.sbol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.sbolstandard.core.SBOLDocument;
import org.sbolstandard.core.SBOLFactory;

public class SBOL1Util {
	public static String serialize(SBOLDocument document) throws IOException
	{
		ByteArrayOutputStream buffer=new ByteArrayOutputStream();  
		SBOLFactory.write(document, buffer);
		String xml=buffer.toString(); 
		return xml;		
	}
}
