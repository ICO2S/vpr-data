package org.virtualparts.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.Terms;

public class SBOLStackHandler {

	private static String getCollectionFilter(QueryParameters params)
	{
		String collectionFilter="";
		for (URI uri: params.getCollectionURIs())
		{
			if (collectionFilter!="")
			{
				collectionFilter+=" || ";
			}
			collectionFilter+="?Collection=<" + uri.toString() + ">";
		}
		return collectionFilter;
		
	}
	public static List<SBOLInteractionSummary> getInteractions(URI componentDefURI, URI stackURI,QueryParameters params) throws VPRException, VPRTripleStoreException
	{
		List<SBOLInteractionSummary> interactions=new ArrayList<SBOLInteractionSummary>();
		TripleStoreHandler ts=new TripleStoreHandler(stackURI.toString());
		String query=null;
		if (params!=null && params.getCollectionURIs()!=null && params.getCollectionURIs().size()>0)
		{
			String collectionFilter=getCollectionFilter(params);
			String componentDefFilter=String.format("?ComponentDefinition=<%s>", componentDefURI.toString());
			String filter=null;
			if (params.getCollectionURIs().size()>1)
			{			
				filter= String.format("%s && (%s)", componentDefFilter, collectionFilter);	
			}
			else
			{
				filter= String.format("%s && %s", componentDefFilter, collectionFilter);	
			}
			query=ts.getSparqlQuery("getComponentInteractionsByCollections.sparql");
			query=String.format(query, filter);
		}
		else
		{
			query=ts.getSparqlQuery("getComponentInteractions.sparql");
			query=String.format(query, componentDefURI.toString());
		}
		ResultSet rs=null;
		try
		{
		   rs=ts.executeSparql(query);
		}
		catch (Exception e)
		{
			throw new VPRTripleStoreException("Could mnot execute the query:" + query);
			
		}
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
		
		private static String getCommaSeparatedURIs (List<Identified> identifieds)
		{
			String result="";
			if (identifieds!=null)
			{
				for (Identified identified:identifieds)
				{
					result= result + String.format("<%s>,", identified.getIdentity());
				}
				if  (!result.isEmpty())
				{
					result=result.substring(0, result.length()-1);
				}
			}
			return result;
		}
		
		private static boolean isAnnotation(String propertyURI)
		{
			boolean result=true;
			if (propertyURI.startsWith(Terms.sbol2.Ns.getPrefix()))
			{
				result=false;
			}
			else if (propertyURI.startsWith(Terms.rdf.Ns.getPrefix()))
			{
				result=false;
			}
			else if (propertyURI.startsWith(Terms.rdfs.Ns.getPrefix()))
			{
				result=false;
			}
			else if (propertyURI.startsWith(Terms.prov.Ns.getPrefix()))
			{
				result=false;
			}
			return result;			
		}
		
		public static void annotateIdentifieds(List<Identified> identifieds, String stackURI) throws VPRException, SBOLValidationException
		{
			annotateIdentifieds(identifieds, stackURI,null);
		}
		
		private static String getPropertyFilter(String namespacePrefix)
		{
			//e.g.:   FILTER (strstarts(str(?p),"http://www.virtualparts.org/terms#"))
			if (namespacePrefix.isEmpty())
			{
				return "";
			}
			else
			{
				return String.format("FILTER (strstarts(str(?p),\"%s\"))",namespacePrefix);
			}
		}
		
		public static void annotateIdentifieds(List<Identified> identifieds, String stackURI, String annotationNameSpacePrefix) throws VPRException, SBOLValidationException
		{
			 TripleStoreHandler ts = new TripleStoreHandler(stackURI.toString());		 
			 String query=ts.getSparqlQuery("getIdentifiedAnnotations.sparql");
			 String uriList=getCommaSeparatedURIs(identifieds);
			 String propertyFilter=getPropertyFilter(annotationNameSpacePrefix);
			 query=String.format(query, propertyFilter,uriList);
			 Model model= ts.executeConstructSparqlAsModel(query);
			 for (Identified identified:identifieds)
			 {
				 Resource resource=model.getResource(identified.getIdentity().toString());
				 if (resource!=null)
				 {
					 StmtIterator stmtIterator=resource.listProperties();
					 if (stmtIterator!=null)
					 {
						 while (stmtIterator.hasNext())
						 {
							 Statement stmt=stmtIterator.next();
							 Property property=stmt.getPredicate();
							 if (isAnnotation(property.getURI()))
							 {
								  RDFNode object=stmt.getObject();
								  String value="";
								  if (object.isResource())
								  {
									 value =object.asResource().getURI();
								  }
								  else
								  {
									  value=object.asLiteral().getValue().toString();
								  }
								  QName qName=new QName(property.getNameSpace(), property.getLocalName());
								  identified.createAnnotation(qName, value);
							 }
						 }
					 }
				 
				 }
			 }
			
		}
		
}


