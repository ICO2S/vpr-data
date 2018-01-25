package org.virtualparts.sbol;

import java.net.URI;

import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceOntology;
import org.virtualparts.VPRException;

public class SVPWriteHandler {

	public static SBOLDocument convertToSBOL(SBOLDocument sbolDoc,String svpWrite, String designId) throws SBOLValidationException, VPRException
	{
		if (svpWrite!=null && svpWrite.length()>0)
		{
			if (sbolDoc==null)
			{
				sbolDoc=new SBOLDocument();
				sbolDoc.setDefaultURIprefix("http://www.virtualparts.org/svpwrite/");
			}
			if (designId==null)
			{
				designId="design";
			}
			ComponentDefinition design=sbolDoc.createComponentDefinition(designId, "1",ComponentDefinition.DNA);
			design.addRole(SequenceOntology.ENGINEERED_REGION);
			String[] components=svpWrite.split(";");
			Component previousComponent=null;
			int i=0;
			for (String component:components)
			{
				i++;
				String separator=":";
				boolean urlIdentifier=false;
				if (component.contains("<"))
				{
					separator=">:";
					urlIdentifier=true;
				}
				int index=component.indexOf(separator);
				if (index<component.length())
				{
					try
					{
					String type=component.substring(index+separator.length());
					ComponentDefinition compDef=null;
					String displayId=null;
					String version="1";
					if (urlIdentifier)
					{
						String url=component.substring(1,index);	
						String baseUrl=SBOLHandler.getBaseUri(url);
						sbolDoc.setDefaultURIprefix(baseUrl);
						displayId=SBOLHandler.getDisplayId(url);
						version=SBOLHandler.getVersion(url);
						if (version==null)
						{
							version="1";
							url=url + "/" + version;
						}
						compDef=sbolDoc.getComponentDefinition(URI.create(url));
						if (compDef==null)
						{
							compDef=sbolDoc.createComponentDefinition(displayId,version,ComponentDefinition.DNA);
						}
					}
					else
					{
						displayId=component.substring(0,index);					
						compDef=sbolDoc.getComponentDefinition(displayId,version);
						if (compDef==null)
						{
							compDef=sbolDoc.createComponentDefinition(displayId, version,ComponentDefinition.DNA);
						}
					}
					Component comp=design.createComponent (displayId + "C" + i, AccessType.PUBLIC, compDef.getIdentity());
					if (previousComponent!=null)
					{
						design.createSequenceConstraint(displayId + "_" + previousComponent.getDisplayId(), RestrictionType.PRECEDES, previousComponent.getIdentity(), comp.getIdentity());
					}
					
					if (type.equals("prom"))
					{
						compDef.addRole(SequenceOntology.PROMOTER);
					}
					else if (type.equals("rbs"))
					{
						compDef.addRole(SequenceOntology.RIBOSOME_ENTRY_SITE);
					}
					else if (type.equals("cds"))
					{
						compDef.addRole(SequenceOntology.CDS);
					}
					else if (type.equals("ter"))
					{
						compDef.addRole(SequenceOntology.TERMINATOR);
					}
					else if (type.equals("op"))
					{
						compDef.addRole(SequenceOntology.OPERATOR);
					}
					else if (type.equals("shim"))
					{
						compDef.addRole(SequenceOntology.ENGINEERED_REGION);
					}
					else if (type.equals("eng"))
					{
						compDef.addRole(SequenceOntology.ENGINEERED_REGION);
					}
					
					else
					{
						throw new VPRException("Unsupported part type in the svpwrite String:" +  component);
					}
					previousComponent=comp;
					}
					catch (Exception e)
					{
						throw new VPRException(e.getMessage() + " Could not convert the SVPWrite string to SBOL. Component:" + component,e);
					}
				}
			}
		}
		return sbolDoc;
	}
	
	public static SBOLDocument convertToSBOL(String svpWrite, String designId) throws SBOLValidationException, VPRException
	{
		return convertToSBOL(null, svpWrite, designId);
	}	
}
