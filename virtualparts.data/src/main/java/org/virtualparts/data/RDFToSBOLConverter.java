package org.virtualparts.data;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import org.sbolstandard.core2.SBOLValidationException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.rdf.RDFHandler;
import org.virtualparts.sbol.SBOLHandler;
import org.virtualparts.sbol.Terms;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.SBOLDocument;
public class RDFToSBOLConverter {

	 private Map<URI,String> namespacePrefixes=new HashMap<URI,String>();
	 public SBOLDocument convertToSBOLDocument (Model model) throws org.virtualparts.VPRTripleStoreException, SBOLValidationException
	 {
		 
		 SBOLDocument document=new SBOLDocument();
		 if (document.getNamespace(URI.create(Terms.rdfs.Ns.getNamespaceURI()))==null)
		 {
			 document.addNamespace(URI.create(Terms.rdfs.Ns.getNamespaceURI()), Terms.rdfs.Ns.getPrefix());
			
		 } 
		 for (QName namespace:document.getNamespaces())
		 {
			 namespacePrefixes.put(URI.create(namespace.getNamespaceURI()), namespace.getPrefix());
		 }
		 ResIterator iterator=model.listSubjects();
		 while (iterator.hasNext())
		 {		
			 Resource sbolResource=iterator.next();
			 addResource(model,sbolResource, document);			 
		 }
		 return document;		 
	 }
	 
	 private void populateComponentDef(Model model,Resource resource, ComponentDefinition compDef, SBOLDocument document) throws VPRTripleStoreException, SBOLValidationException
	 {
		 StmtIterator iterator=resource.listProperties();
		 while (iterator.hasNext())
		 {
			 Statement stmt=iterator.next();
			 String predicate=stmt.getPredicate().getURI();
			 if (predicate.equals(Terms.toURI(Terms.sbol2.vocabulary.type).toString()))
			 {				 				 
				 continue;
			 }
			 else if (predicate.equals(Terms.toURI(Terms.sbol2.vocabulary.role).toString()))
			 {
				 compDef.addRole(URI.create(stmt.getObject().toString()));
			 }
			 else if (predicate.equals(Terms.toURI(Terms.sbol2.vocabulary.sequence).toString()))
			 {
				 compDef.addSequence(URI.create(stmt.getObject().toString()));	
			 }
			 else if (!stmt.getPredicate().getNameSpace().equals(Terms.sbol2.Ns.getNamespaceURI()))
			 {
				 addIdentifiedProperties(document,compDef,stmt);
			 }
		 }		
	 }
	 
	 
/*	 private void populateFunctionalComponent(SBOLDocument document, Model model,ModuleDefinition moduleDef, Resource resource) throws VPRTripleStoreException, SBOLValidationException
	 {
		if (moduleDef.getFunctionalComponent(URI.create(resource.stringValue()))==null)
		{
			Set<URI> definitionUris=getURIValues(model, resource, toURI(Terms.sbol2.vocabulary.definition));
			Set<URI> accessUris=getURIValues(model, resource, toURI(Terms.sbol2.vocabulary.access));
			Set<URI> directionUris=getURIValues(model, resource, toURI(Terms.sbol2.vocabulary.direction));
			AccessType accessType=AccessType.PUBLIC;			
			if (accessUris.size()>0)
			{
				String accessUri=accessUris.iterator().next().toString();
				if (getDisplayId(accessUri).equals("private"))
				{
					accessType=AccessType.PRIVATE;
				}
			}
			DirectionType direction=DirectionType.INOUT;			
			if (directionUris.size()>0)
			{
				String directionLabel=getDisplayId(directionUris.iterator().next().toString());
				if (directionLabel.equals("in"))
				{
					direction=DirectionType.IN;
				}
				else if (directionLabel.equals("out"))
				{
					direction=DirectionType.OUT;
				}
				else if (directionLabel.equals("none"))
				{
					direction=DirectionType.NONE;
				}				
			}				
			if (definitionUris.size()>0)
			{
				FunctionalComponent functionalComp= moduleDef.createFunctionalComponent(getDisplayId(resource.stringValue()), 
					accessType, 
					definitionUris.iterator().next(),
					direction);
				for (Statement stmt:model.filter(resource, null, null))
				 {
					 String predicate=stmt.getPredicate().toString();			 
					 if (predicate.equals(toURI(Terms.sbol2.vocabulary.definition).toString()))
					 {				 				 
						 continue;
					 }
					 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.access).toString()))
					 {
						 continue;
					 }	
					 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.direction).toString()))
					 {
						 continue;
					 }	
					 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.mapsTo).toString()))
					 {
						 throw new VPRTripleStoreException("Populating FunctionalComponents with the mapsTo property has not been implemented yet!");
					 }	
					 else
					 {
						 addIdentifiedProperties(document, functionalComp, stmt);
					 }
				 }
			}			
			else{
				throw new VPRTripleStoreException("No definition property for the functional component" + resource.stringValue());
			}
		}		 
	 }
	 private void populateParticipation(Model model,Resource resource,ModuleDefinition moduleDef, Interaction interaction, SBOLDocument document) throws VPRTripleStoreException, SBOLValidationException
	 {
		 String uri=model.filter(resource, null, null).subjectURI().stringValue();
		 Set<URI> participantURIs=getURIValues(model, resource, toURI(Terms.sbol2.vocabulary.participant));
		 for (URI participantURI:participantURIs)
		 {
			 if (moduleDef.getFunctionalComponent(participantURI)==null)
			 {			 
				 populateFunctionalComponent(document,model,moduleDef,new URIImpl(participantURI.toString()));
			 }
			 Participation participation=interaction.createParticipation(getDisplayId(uri), participantURI, SystemsBiologyOntology.MOLECULAR_INTERACTION);
			 for (Statement stmt:model.filter(resource, null, null))
			 {
				 String predicate=stmt.getPredicate().toString();			 
				 if (predicate.equals(toURI(Terms.sbol2.vocabulary.participant).toString()))
				 {				 				 
					 continue;
				 }
				 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.role).toString()))
				 {
					 participation.addRole(URI.create(stmt.getObject().toString()));
				 }			
				 else
				 {
					 addIdentifiedProperties(document,participation,stmt);
				 }
			 }
		 }
	 }
	 
	 private void populateInteraction(Model model,Resource resource, ModuleDefinition moduleDef, SBOLDocument document) throws VPRTripleStoreException, SBOLValidationException
	 {
		 Set<URI> types=getURIValues(model, resource, toURI(Terms.sbol2.vocabulary.type));
		 String uri=model.filter(resource, null, null).subjectURI().stringValue();
		 String displayId=getDisplayId(uri);
		 Interaction interaction=moduleDef.createInteraction(displayId, types);
		 
		 for (Statement stmt:model.filter(resource, null, null))
		 {
			 String predicate=stmt.getPredicate().toString();			 
			 if (predicate.equals(toURI(Terms.sbol2.vocabulary.type).toString()))
			 {				 				 
				 continue;
			 }
			 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.participation).toString()))
			 {
				 Resource participationResource=((Resource) stmt.getObject());
				 populateParticipation(model, participationResource,moduleDef, interaction, document);					 				 
			 }			
			 else
			 {
				 addIdentifiedProperties(document,interaction,stmt);
			 }
		 }		 
	 }
	 
	 private void populateModuleDef(Model model,Resource resource, ModuleDefinition moduleDef, SBOLDocument document) throws VPRTripleStoreException, SBOLValidationException
	 {
		 for (Statement stmt:model.filter(resource, null, null))
		 {
			 String predicate=stmt.getPredicate().toString();			
			 if (predicate.equals(toURI(Terms.sbol2.vocabulary.interaction).toString()))
			 {
				 Resource interactionResource=((Resource) stmt.getObject());
				 populateInteraction(model, interactionResource, moduleDef, document);				 
			 }
			 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.role).toString()))
			 {
				 moduleDef.addRole(URI.create(stmt.getObject().toString()));			 
			 }
			 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.module).toString()))
			 {
				 throw new VPRTripleStoreException("Populating ModuleDefinition with the module property has not been implemented yet!");		 
			 }
			 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.functionalComponent).toString()))
			 {
				 throw new VPRTripleStoreException("Populating ModuleDefinition with the functionalComponent property has not been implemented yet!");		 
			 }
			 else if (predicate.equals(toURI(Terms.sbol2.vocabulary.model).toString()))
			 {
				 throw new VPRTripleStoreException("Populating ModuleDefinition with the model property has not been implemented yet!");		 
			 }			 
			 else
			 {
				 addIdentifiedProperties(document,moduleDef,stmt);
			 }
		 }
	 }
	 */
	 	
	 private String getDisplayId(Model model, Resource sbolResource)
	 {
		 String displayId=RDFHandler.getPropertySingleValue(model, sbolResource, Terms.toURI(Terms.sbol2.vocabulary.displayId));
		 if (displayId==null || displayId.length()==0)
		 {
			 displayId=SBOLHandler.getDisplayId(sbolResource.getURI());
		 }
		 return displayId;
	 }
	 
	 private String getVersion(Model model, Resource sbolResource)
	 {
		 String version=RDFHandler.getPropertySingleValue(model, sbolResource, Terms.toURI(Terms.sbol2.vocabulary.version));
		 if (version==null || version.length()==0)
		 {
			 version=SBOLHandler.getVersion(sbolResource.getURI());
		 }
		 return version;
	 }
	 	 	 
	 private void addResource(Model model, Resource sbolResource, SBOLDocument document) throws org.virtualparts.VPRTripleStoreException, SBOLValidationException
	 {		
		 if (RDFHandler.hasType(model, sbolResource,Terms.toURI(Terms.sbol2.vocabulary.ComponentDefinition)))
		 {
			 
			 String displayId=getDisplayId(model, sbolResource);//SBOLHandler.getDisplayId(sbolResource.getURI());
			 document.setDefaultURIprefix(SBOLHandler.getBaseUri(sbolResource.getURI()));			 
			 Set<URI> types=new HashSet<URI>();	
			 List<String> typeList=RDFHandler.getPropertyValues(model, sbolResource, Terms.toURI(Terms.sbol2.vocabulary.type));
			 for (String type:typeList)
			 {
				 types.add(URI.create(type));
			 }			 
			 String version=getVersion(model, sbolResource);
			 ComponentDefinition compDef=null;
			 if (version==null)
			 {
				 compDef=document.createComponentDefinition(displayId, types);
			 }
			 else
			 {
				 compDef=document.createComponentDefinition(displayId,version, types);
			 }
			 populateComponentDef(model, sbolResource,compDef,document);			 
		 }
			
		 else if (RDFHandler.hasType(model, sbolResource,Terms.toURI(Terms.sbol2.vocabulary.Sequence)))
		 {
			 String displayId=getDisplayId(model, sbolResource);//SBOLHandler.getDisplayId(sbolResource.getURI());
			 document.setDefaultURIprefix(SBOLHandler.getBaseUri(sbolResource.getURI()));
			 String elements=RDFHandler.getPropertyValueAsString(model, sbolResource, Terms.toURI(Terms.sbol2.vocabulary.elements));
			 String encoding=RDFHandler.getPropertyValueAsString(model, sbolResource, Terms.toURI(Terms.sbol2.vocabulary.encoding));
			 document.createSequence(displayId, elements, URI.create(encoding));
		 }
		 		 
		 /*filtered=model.filter(sbolResource, RDF.TYPE, new URIImpl(toURI(Terms.sbol2.vocabulary.ModuleDefinition).toString()));		 
		 if (filtered!=null && !filtered.isEmpty())
		 {
			 String uri=model.filter(sbolResource, null, null).subjectURI().stringValue();
			 String displayId=getDisplayId(uri);
			 String baseUri=getBaseUri(uri, displayId);
			 document.setDefaultURIprefix(baseUri);			 								 
			 ModuleDefinition moduleDef=document.createModuleDefinition(baseUri, displayId, "1.0");
			 populateModuleDef(model, sbolResource,moduleDef,document);			 
		 }
		 */
		 
	 }
	 
	 private void addIdentifiedProperties(SBOLDocument document, Identified identified, Statement stmt) throws org.virtualparts.VPRTripleStoreException, SBOLValidationException
	 {
		 String predicate=stmt.getPredicate().getURI();
		 if (predicate.equals(RDF.type))
		 {			
			 String value=stmt.getObject().asResource().getURI();
			 if (value.contains(Terms.sbol2.Ns.getNamespaceURI().toString()))
			 {			 
				 return;				 
			 }
			 else
			 {
				 identified.createAnnotation(
						 new QName(RDF.type.getNameSpace(),RDF.type.getLocalName(),Terms.rdf.Ns.getPrefix()),
						 URI.create(value));
			 }
		 }
		 else if (predicate.equals(Terms.toURI(Terms.sbol2.vocabulary.displayId).toString()))
		 {
			 return;					 
		 }
		 else if (predicate.equals(Terms.toURI(Terms.sbol2.vocabulary.persistentIdentity).toString()))
		 {
			 return;					 
		 }
		 else if (predicate.equals(Terms.toURI(Terms.sbol2.documented.name).toString()))
		 {
			 if (identified.getName()==null || identified.getName().length()==0)
			 {
				 identified.setName(stmt.getObject().asLiteral().getLexicalForm());
			 }
			 else
			 {
				 identified.createAnnotation(Terms.sbol2.documented.name, stmt.getObject().asLiteral().getLexicalForm());
			 }
		 }
		 else if (predicate.equals(Terms.toURI(Terms.sbol2.documented.description).toString()))
		 {
			 if (identified.getDescription()==null || identified.getDescription().length()==0)
			 {
				 identified.setDescription(stmt.getObject().toString());
			 }
			 else
			 {
				 identified.createAnnotation(Terms.sbol2.documented.description, stmt.getObject().asLiteral().getLexicalForm());
			 }
		 }
		 else
		 {		
			 String namespace=stmt.getPredicate().getNameSpace(); 
			 String prefix=namespacePrefixes.get(URI.create(namespace));
			 if (prefix==null)
			 {
				 prefix="ns" + (namespacePrefixes.size()+1);
				 namespacePrefixes.put(URI.create(namespace), prefix);
				 document.addNamespace(URI.create(namespace), prefix);
			 }
			 
			 if (stmt.getObject().isResource()) {				
				 identified.createAnnotation(new QName(namespace,stmt.getPredicate().getLocalName(),prefix), URI.create(stmt.getObject().asResource().getURI()));
			 }
			else if (stmt.getObject().isLiteral())
			{
				 identified.createAnnotation(new QName(namespace,stmt.getPredicate().getLocalName(),prefix), stmt.getObject().asLiteral().getLexicalForm());				 
			}
			else
			{
				 identified.createAnnotation(new QName(namespace,stmt.getPredicate().getLocalName(),prefix), URI.create(stmt.getObject().asResource().getURI()));				 					
			}			 
		 }
		 
	 }
	 /*
	 private static URI toURI(QName qname)
		{
			return URI.create(qname.getNamespaceURI() + qname.getLocalPart());
		}
	 
	 public static String getDisplayId(URI uri)
		{
		 	String uriString=uri.toString();	
		 	return getDisplayId(uriString);
		}
	 
	 
	 public static String getDisplayId(String uriString)
		{
		 	int index=uriString.lastIndexOf("#");
			int index2=uriString.lastIndexOf("/");
			if (index2>index)
			{
				index=index2;
			}
			if (uriString.length()>index+1)
			{
				return uriString.substring(index+1);
			}
			else
			{
				return null;
			}
		}
		
		private static String getBaseUri(String uri,String displayId)
		{
			int index=uri.lastIndexOf(displayId);
			if (index>0)
			{
				return uri.substring(0,index);
			}
			else
			{
				return null;
			}
		}
		
		public static String getBaseUri(URI uri)
		{
			String uriString=uri.toString();
			return getBaseUri(uriString);
		}
		
		public static String getBaseUri(String uriString)
		{
			int index=uriString.lastIndexOf("#");
			int index2=uriString.lastIndexOf("/");
			if (index2>index)
			{
				index=index2;
			}
			if (uriString.length()>index+1)
			{
				return uriString.substring(0,index);
			}
			else
			{
				return null;
			}
		}
		
		public static List<URI> getUris(String data,String separator)
		{
			List<String> items=getItems(data, separator);
			List<URI> uriList=new ArrayList<URI>();
			for (String item:items)
			{
				uriList.add(URI.create(item));
			}
			return uriList;
		}
		
		public static List<String> getItems(String data,String separator)
		{
			List<String> itemList=new ArrayList<String>();
			if (data!=null && data.length()>0)
			{
				String[] items=data.split(separator);
				itemList=Arrays.asList(items);
			}
			return itemList;
		}
		*/
}
