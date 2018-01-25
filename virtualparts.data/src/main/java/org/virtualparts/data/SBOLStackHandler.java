package org.virtualparts.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.Terms;

public class SBOLStackHandler {

	public static List<SBOLInteractionSummary> getInteractions(URI componentDefURI, URI stackURI) throws VPRException
	{
		List<SBOLInteractionSummary> interactions=new ArrayList<SBOLInteractionSummary>();
		TripleStoreHandler ts=new TripleStoreHandler(stackURI.toString());
		String query=ts.getSparqlQuery("getComponentInteractions.sparql");
		
		query=String.format(query, componentDefURI.toString());
		ResultSet rs=ts.executeSparql(query);
		while (rs.hasNext())
		{
			QuerySolution solution = rs.next();
			
			if (solution.get("Interaction")==null)
			{
				break;
			}	
			String interactionUri=solution.get("Interaction").toString();					
			String interactionTypeString=solution.get("InteractionTypes").toString();
			String nameString=solution.get("InteractionNames").toString();
			String descriptionString=solution.get("InteractionDescriptions").toString();
			
			List<URI> interactionTypes=TripleStoreHandler.getUris(interactionTypeString, ",");
			List<String> names=TripleStoreHandler.getItems(nameString, ",");
			List<String> descriptions=TripleStoreHandler.getItems(descriptionString, ",");
						
			List<URI> interactingComponentURIs=TripleStoreHandler.getUris(solution.get("ComponentDefinitionOthers").toString(),",");
			interactingComponentURIs.remove(componentDefURI);
			SBOLInteractionSummary interaction= new SBOLInteractionSummary(URI.create(interactionUri), interactionTypes, interactingComponentURIs);
			interaction.setNames(names);
			interaction.setDescriptions(descriptions);			
			interactions.add(interaction);
		}
		return interactions;								
	}
	
	public static SBOLDocument getSubComponents(URI stackURI, URI componentURI) throws VPRException {
		TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());
		String query = ts.getSparqlQuery("getSubComponents.sparql");
		query = String.format(query, componentURI.toString(),componentURI.toString());
		String sbolData = ts.executeConstructSparql(query);
		SBOLDocument doc =null;
		try
		{
			if (sbolData.contains("ComponentDefinition"))				
			{
				doc = SBOLHandler.read(sbolData);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new VPRException(e.getMessage() +  ". libSBOLj could not create the SBOL document when creating subcomponents. Parent component URI:" +  componentURI,e);
		}
		return doc;
	}
	
	public static SBOLDocument getInteraction(URI stackURI, URI interactionURI) throws VPRException {
		TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());
		String query = ts.getSparqlQuery("getInteraction.sparql");
		query = String.format(query, interactionURI.toString(),interactionURI.toString());
		String sbolData = ts.executeConstructSparql(query);
		SBOLDocument doc = SBOLHandler.read(sbolData);
		return doc;
	}
	
	public static SBOLDocument getInteractionDetailed(URI stackURI, URI interactionURI) throws VPRException {
		String str2="";
		TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());
		String query = ts.getSparqlQuery("getInteractionDetailed.sparql");
		query = String.format(query, interactionURI.toString(),interactionURI.toString(),interactionURI.toString());
		String sbolData = ts.executeConstructSparql(query);		
		SBOLDocument doc = null;
		try
		{
			doc=SBOLHandler.read(sbolData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new VPRException(e.getMessage() + " Could not populate the interactions, together with components",e);
		}
		return doc;
	}
	
	 public static SBOLDocument getComponent(URI componentURI, URI stackURI) throws VPRTripleStoreException, VPRException
	 {
		 TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());		 
		 String query=ts.getSparqlQuery("getComponent.sparql");
		 query=String.format(query, componentURI);
		 Model model= ts.executeConstructSparqlAsModel(query);
		 SBOLDocument doc =null;
		 try
		 {
			 //doc = SBOLHandler.read(sbolData);
			 RDFToSBOLConverter converter=new RDFToSBOLConverter();
			 doc=converter.convertToSBOLDocument(model);			 
		 }
		 catch (Exception e)
		 {
			 e.printStackTrace();
			 throw new VPRException("Could not convert RDF data into SBOL for the component" + componentURI, e);
		 }
		 return doc;
	}
	 
		public static int getSearchCount(SBOLDocument sbolQuery, URI stackURI) throws VPRTripleStoreException, VPRException
		{
			int count=-1;
			TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());	
			String query=ts.getSparqlQuery("getSearchCount.sparql");
			String filter=getSearchFilter(sbolQuery);
			query=String.format(query, filter);	    			
			ResultSet rs=ts.executeSparql(query);
			if (rs.hasNext())
			{
				QuerySolution solution = rs.next();
				count=Integer.parseInt(solution.get("Count").asLiteral().getLexicalForm());
			}			
			return count;
		}
		
		 public static SBOLDocument searchComponents(SBOLDocument sbolQuery, URI stackURI,int page, int pageSize) throws VPRTripleStoreException, VPRException
		 {
			 TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());		 
			 String query=ts.getSparqlQuery("searchComponents.sparql");			 
			 String filter=getSearchFilter(sbolQuery);
			 int offset=(page-1)*pageSize;
			 query=String.format(query, filter, pageSize, offset);		    					
			 String sbolData = ts.executeConstructSparql(query);
			 SBOLDocument doc = SBOLHandler.read(sbolData);
			 return doc;
		}
		 
		 public static String searchComponentsUsingJson(SBOLDocument sbolQuery, URI stackURI,int page, int pageSize) throws VPRTripleStoreException, VPRException
		 {
			 TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());		 
			 String query=ts.getSparqlQuery("searchComponentsTable.sparql");			 
			 String filter=getSearchFilter(sbolQuery);
			 int offset=(page-1)*pageSize;
			 query=String.format(query, filter, pageSize, offset);		    					
			 String result = ts.executeSparqlWithJson(query);
			 return result;
		}
						
		private static String getSearchFilter(SBOLDocument query)
		{
			String filter="";
			if (query!=null && query.getComponentDefinitions()!=null)
			{
				ComponentDefinition compDef=query.getComponentDefinitions().iterator().next();
				for (URI uri:compDef.getTypes())
				{
					filter=filter +  "?ComponentDefinition sbol2:type <" + uri + ">. ";  
				}
				if (compDef.getRoles()!=null)
				{
					for (URI uri:compDef.getRoles())
					{
						filter=filter +  "?ComponentDefinition sbol2:role <" + uri + ">. ";   
					}
				}
				String sparqlFilter="";
				if (compDef.getAnnotations()!=null)
				{
					for (Annotation annotation:compDef.getAnnotations())
					{
						String annotationProperty=annotation.getQName().getNamespaceURI()  + annotation.getQName().getLocalPart();
						if (Terms.toURI(Terms.edam.searchParameter).toString().equals(annotationProperty))
						{
							 filter = filter + "?ComponentDefinition ?Property ?Value . ";
							 sparqlFilter=updateSparqlFilter(sparqlFilter, "(regex(lcase(str(?Value)) , lcase(\"" + annotation.getStringValue() + "\"))) ");							 
						}
						else if (annotation.getURIValue()!=null)
						{
							filter=filter + String.format("?ComponentDefinition <%s> <%s>. ", annotationProperty, annotation.getURIValue().toString());
						}							
					}
				}
				if (sparqlFilter.length()>0)
				{
					filter= filter + sparqlFilter;
				}
				/*if (compDef.getName()!=null)
				{
					filter=filter + " FILTER (regex(lcase(str(?Name)) , lcase(\"" + compDef.getName() + "\"))) ";
				}*/
			}
			return filter;
		
		}	 
		
		private static String updateSparqlFilter(String sparqlFilter, String subFilter)
		{
			 if (sparqlFilter.equals(""))
			 {
				 sparqlFilter=" FILTER" + subFilter;
			 }
			 else
			 {
				 sparqlFilter=sparqlFilter + "&&" + subFilter;
			 }
			 return  sparqlFilter;
		}
		
}


