package org.virtualparts.data2;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.commons.collections4.map.MultiValueMap;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.Module;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.RefinementType;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceOntology;
import org.sbolstandard.core2.SystemsBiologyOntology;
import org.sbolstandard.examples.Sbol2Terms.component;
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.data.Cacher;
import org.virtualparts.data.QueryParameters;
import org.virtualparts.data.SBOLInteractionSummary;
import org.virtualparts.data.SBOLStackHandler;
import org.virtualparts.sbol.SBOLHandler;

public class SBOLInteractionAdder_GeneCentric{
	private URI endPointUrl=null;
	private String rootModuleId=null;
	private QueryParameters queryParameters=null;
	
	public SBOLInteractionAdder_GeneCentric(URI endPointUrl)
	{
		this.endPointUrl=endPointUrl;
	}
	
	public SBOLInteractionAdder_GeneCentric(URI endPointUrl, String rootModuleId)
	{
		this(endPointUrl);
		this.rootModuleId=rootModuleId;		
	}
	
	public SBOLInteractionAdder_GeneCentric(URI endPointUrl, String rootModuleId, QueryParameters queryParameters)
	{
		this(endPointUrl);
		this.rootModuleId=rootModuleId;		
		this.queryParameters=queryParameters;
	}
	
	private String getRootModuleId(List<ComponentDefinition> designs) throws VPRException
	{
		if (designs.size()>=1)
		{
			if (this.rootModuleId==null)
			{
				return getDisplayId(designs);
			}
			else
			{
				return rootModuleId;
			}
		}
		else
		{
			throw new VPRException("This method should only be called if subdesigns are involved!");
		}
	}
	
	public void addInteractions(SBOLDocument document) throws VPRException, SBOLValidationException, VPRTripleStoreException {
		MultiValueMap<URI, SBOLInteractionSummary> interactions=getInteractions(document);
		if (interactions!=null && interactions.size()>0)
		{
			List<ComponentDefinition> designs=getFlattenedDesigns(document);
			if (designs!=null && designs.size()>0)
			{
				if (designs.size()==1)
				{
					addModuleDefinition(document, designs.get(0), null, interactions);
				}
				else
				{
					document.setDefaultURIprefix(SBOLHandler.getBaseUri(designs.get(0).getIdentity()));
					ModuleDefinition parentModuleDef=document.createModuleDefinition(getRootModuleId(designs));					
					addModuleDefinitions(document, designs, parentModuleDef, interactions);
				}								
			}
		}
	}
	
	
	private void addModuleDefinitions(SBOLDocument document, List<ComponentDefinition> compDefs, ModuleDefinition moduleDef, MultiValueMap<URI, SBOLInteractionSummary>  interactions) throws VPRException, SBOLValidationException, VPRTripleStoreException
	{
		List<ModuleDefinition> subModuleDefinitions=new ArrayList<ModuleDefinition>();				
		for (ComponentDefinition componentDef:compDefs)
		{
			ModuleDefinition subModuleDef=addModuleDefinition(document, componentDef, moduleDef, interactions);
			subModuleDefinitions.add(subModuleDef);
		}
		setInputsOutputs(moduleDef);
		//TODO Implement:
		//addInteractionsBetweenSubModules(document, moduleDef,subModuleDefinitions, interactions);		
	}
		
	
	private ModuleDefinition createModuleDefinition(SBOLDocument document, ModuleDefinition moduleDef) throws SBOLValidationException
	{
		String defaultURIPrefix=document.getDefaultURIprefix();
		String tempDefaultURIPrefix=SBOLHandler.getBaseUri(moduleDef.getIdentity().toString());
		document.setDefaultURIprefix(tempDefaultURIPrefix);
		String displayId=SBOLHandler.getDisplayId(moduleDef.getIdentity());
		String version=SBOLHandler.getVersion(moduleDef.getIdentity().toString());
		ModuleDefinition moduleDefNew=null;
		if (version==null)
		{
			moduleDefNew=document.createModuleDefinition(displayId);
		}
		else
		{
			moduleDefNew=document.createModuleDefinition(displayId, version);
		}
		document.setDefaultURIprefix(defaultURIPrefix);
		return moduleDefNew;
	}
	
	private ModuleDefinition addModuleDefinition(SBOLDocument document, ComponentDefinition compDef, ModuleDefinition parentModuleDef,MultiValueMap<URI, SBOLInteractionSummary>  interactions) throws SBOLValidationException, VPRException, VPRTripleStoreException
	{
		ModuleDefinition moduleDef=null;
		if (compDef.getComponents()!=null && compDef.getComponents().size()>0)
		{
			document.setDefaultURIprefix(SBOLHandler.getBaseUri(compDef.getIdentity()));
			moduleDef=document.createModuleDefinition(compDef.getDisplayId() + "_module", "1");	
			moduleDef.createFunctionalComponent(compDef.getDisplayId() + "_fc", AccessType.PRIVATE, compDef.getIdentity(), DirectionType.NONE);
			Set<URI> nonDNAComponentDefs=addInternalInteractions(document, moduleDef, interactions, compDef);
			
			if (parentModuleDef!=null)
			{
				addNonDnaInteractions(document, parentModuleDef, nonDNAComponentDefs, interactions);
				setInputsOutputs(moduleDef);
				//setInputsOutputs(parentModuleDef);
				linkToParent(moduleDef, parentModuleDef);				

			}	
			else{
				addNonDnaInteractions(document, moduleDef, nonDNAComponentDefs, interactions);
				setInputsOutputs(moduleDef);
			}
			
		}		
		return moduleDef;
	}		
	
	
	private void getLeafComponentDefinitions(ComponentDefinition compDef,List<ComponentDefinition> compDefs) {
		for (Component component:compDef.getComponents())
		{
			ComponentDefinition subCompDef=component.getDefinition();
			
				if (subCompDef.getComponents()==null || subCompDef.getComponents().size()==0)
				{//If leaf then add
					compDefs.add(subCompDef);
				}
				else
				{
					getLeafComponentDefinitions(subCompDef, compDefs);
				}
		}	
	}

	private MultiValueMap<URI, SBOLInteractionSummary> getInteractions(SBOLDocument sbolDocument) throws VPRException, SBOLValidationException
	{
		MultiValueMap<URI, SBOLInteractionSummary> interactions=new MultiValueMap<URI, SBOLInteractionSummary>();		
		addInteractions(sbolDocument,  SequenceOntology.CDS, interactions);
		addInteractions(sbolDocument,  SequenceOntology.PROMOTER, interactions);													
		return interactions;
	}
	
	private void addInteractions(SBOLDocument sbolDocument, URI role,MultiValueMap<URI, SBOLInteractionSummary> interactions) throws VPRException, SBOLValidationException
	{
		List<ComponentDefinition> componentDefs=SBOLHandler.getComponentDefinitionsByRole(sbolDocument, role);
		for (ComponentDefinition componentDef:componentDefs)
		{
			List<SBOLInteractionSummary> componentInteractions=SBOLStackHandler.getInteractions(componentDef.getIdentity(),this.endPointUrl,this.queryParameters);
			if (componentInteractions!=null && componentInteractions.size()>0)
			{
				interactions.putAll(componentDef.getIdentity(),componentInteractions);	
				for (SBOLInteractionSummary interactionSummary:componentInteractions)
				{
					for (URI uri:interactionSummary.getComponentDefs())
					{
						if (uri!=componentDef.getIdentity() && interactions.get(uri)==null)
						{
							addProteinInteractions(uri, interactions);
						}
					}
				}
			}
		}
	}
	
	
	private void addProteinInteractions(URI proteinDefUri, MultiValueMap<URI, SBOLInteractionSummary> interactions) throws VPRException, SBOLValidationException
	{
		List<SBOLInteractionSummary> proteinInteractions=SBOLStackHandler.getInteractions(proteinDefUri,this.endPointUrl,this.queryParameters);
		if (proteinInteractions!=null)
		{
			interactions.putAll(proteinDefUri,proteinInteractions);		
			addInteractionsOfDimers(proteinInteractions, proteinDefUri, interactions);
		}
	}		
	
	private void addInteractionsOfDimers(List<SBOLInteractionSummary> proteinInteractions, URI proteinDefUri,MultiValueMap<URI, SBOLInteractionSummary> interactions) throws VPRException, SBOLValidationException
	{
		for(SBOLInteractionSummary proteinInteraction:proteinInteractions)
		{
			if (proteinInteraction.getTypes().contains(SystemsBiologyOntology.NON_COVALENT_BINDING))
			{				
				for (URI componentDefUri: proteinInteraction.getComponentDefs())
				{
					if (componentDefUri!=proteinDefUri)
					{
						if (interactions.getCollection(componentDefUri)==null)
						{
							List<SBOLInteractionSummary> dimerInteractions=SBOLStackHandler.getInteractions(componentDefUri,this.endPointUrl,this.queryParameters);
							if (dimerInteractions!=null)
							{
								interactions.putAll(componentDefUri,dimerInteractions);			
								addInteractionsOfDimers(dimerInteractions, componentDefUri, interactions);
							}
						}
					}
				}
			}
		}	
	}
	
	
	private void linkToParent(ModuleDefinition moduleDef, ModuleDefinition parentModuleDef) throws SBOLValidationException {
		if (moduleDef.getFunctionalComponents()!=null)
		{
			for (FunctionalComponent fComp:moduleDef.getFunctionalComponents())
			{
				if (isPublicInputOrOutput(fComp))
				{
					FunctionalComponent parentFComp=getFunctionalComponent(parentModuleDef,fComp.getDefinitionURI());
					//If the associated functional component does not exist in the parent moduledef, create the functional component
					if (parentFComp==null)
					{
						parentFComp= parentModuleDef.createFunctionalComponent(fComp.getDisplayId(), fComp.getAccess(), fComp.getDefinitionURI(), fComp.getDirection());						
					}
					else
					{
						if (parentFComp.getDirection()!=fComp.getDirection())
						{
							parentFComp.setDirection(DirectionType.INOUT);
						}
					}
					Module subModule=getSubModule(parentModuleDef, moduleDef);
					//If the sub module does not exist, create the sub module
					if (subModule==null)
					{
						subModule=parentModuleDef.createModule(parentModuleDef.getDisplayId() + "_" +  moduleDef.getDisplayId() + "_sub", moduleDef.getDisplayId(), "1");
					}						
					MapsTo mapsTo=getMapsTo(subModule, parentFComp, fComp);
					//If the mapping does not exist already, create the mapsTo entity
					if (mapsTo==null)
					{
						subModule.createMapsTo(subModule.getDisplayId()  + "_" + parentFComp.getDisplayId() + "_" + fComp.getDisplayId()  , RefinementType.VERIFYIDENTICAL,parentFComp.getIdentity(), fComp.getIdentity());
					}																	
				}
			}
		}		
	}
	
	
	private Set<URI> addInternalInteractions(SBOLDocument document, ModuleDefinition moduleDef,
			MultiValueMap<URI, SBOLInteractionSummary> interactions, ComponentDefinition design) throws SBOLValidationException, VPRException, VPRTripleStoreException 
	{		
		List<Interaction> dnaInteractions=new ArrayList<Interaction>();
		
		List<ComponentDefinition> compDefs=new ArrayList<ComponentDefinition>();
		
		getLeafComponentDefinitions(design, compDefs);
		
		for (ComponentDefinition compDef:compDefs)
		{
			//ComponentDefinition compDef=comp.getDefinition();
			Collection<SBOLInteractionSummary> interactionSummaries=interactions.getCollection(compDef.getIdentity());
			if (interactionSummaries!=null)
			{
				for (SBOLInteractionSummary interactionSummary:interactionSummaries)
				{
					List<ModuleDefinition> moduleDefs = addInteraction(document, moduleDef, interactionSummary, compDef, design);	
					for (ModuleDefinition moduleDefTemp:moduleDefs)
					{
						for (Interaction interactionTemp:moduleDefTemp.getInteractions())
						{
							dnaInteractions.add(interactionTemp);	
						}
					}
				}
			}
		}
		
		
		Set<URI> nonDNAComponentDefs=new HashSet<URI>();
			for (Interaction dnaInteraction:dnaInteractions)
			{
				for (Participation participation:dnaInteraction.getParticipations())
				{
					URI participatingCompDefURI=participation.getParticipantDefinition().getIdentity();
					if (participatingCompDefURI!=design.getIdentity())
					{
						nonDNAComponentDefs.add(participatingCompDefURI);
					}
				}
			}								
			
			return nonDNAComponentDefs;
	}		
	
	private void setInputsOutputs(ModuleDefinition moduleDef) throws SBOLValidationException
	{
		for (FunctionalComponent fComp:moduleDef.getFunctionalComponents())
		{				
			DirectionType direction=DirectionType.NONE;
			AccessType access=AccessType.PRIVATE;	
			if (!fComp.getDefinition().containsType(ComponentDefinition.DNA))
			{					
					access=AccessType.PUBLIC;
					if (hasRole(moduleDef, fComp, SystemsBiologyOntology.PRODUCT))
					{
						direction=DirectionType.INOUT;			
					}
					else
					{
						direction=DirectionType.IN;										
					}		
				
			
				if (direction==DirectionType.IN )
				{
					if (existAsOutInSubModules(moduleDef, fComp))
					{
						direction=DirectionType.INOUT;
					}
				}
			}
			else
			{
				direction=DirectionType.INOUT;
				access=AccessType.PUBLIC;
			}
			
			fComp.setDirection(direction);
			fComp.setAccess(access);		
		}
	}
	
	private boolean existAsOutInSubModules(ModuleDefinition moduleDef, FunctionalComponent fComp)	
	{
		boolean result=false;
		if (moduleDef.getModules()!=null )
		{
			for (Module module:moduleDef.getModules())
			{
				if (module.getMapsTos()!=null)
				{
					for (MapsTo mapsTo: module.getMapsTos())				
					{
						if (mapsTo.getLocal()==fComp)
						{
							DirectionType direction=((FunctionalComponent)mapsTo.getRemote()).getDirection();
							if (direction==DirectionType.OUT || direction==DirectionType.INOUT)
							{
								return true;
							}
						}
					}
				}
				
			}
		}
		return result;
	}
	
	private boolean hasRole(ModuleDefinition moduleDef, FunctionalComponent fComp, URI role)
	{
		for (Interaction interaction:moduleDef.getInteractions())
		{
			for (Participation participation:interaction.getParticipations())
			{
				if (participation.getParticipantURI().equals(fComp.getIdentity()))
				{
					if (participation.containsRole(role))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private SBOLDocument getInteractionDetail(SBOLInteractionSummary participantInteractionSummary, URI uri) throws VPRException
	{
		Cacher cacher=new Cacher();
		
		String hashCode=cacher.getHashCode(participantInteractionSummary, uri);
		SBOLDocument sbolInteractionDocument=null;
		if (hashCode!=null) 
		{
			sbolInteractionDocument=cacher.retrieveFromCache(hashCode);
		}
		else
		{
			throw new VPRException ("The interaction does not contain any participant: " + participantInteractionSummary.getUri());
		}
		if (sbolInteractionDocument==null)
		{
			sbolInteractionDocument=SBOLStackHandler.getInteractionDetailed(this.endPointUrl,participantInteractionSummary.getUri());
			cacher.putInCache(sbolInteractionDocument, hashCode);
		}
		return sbolInteractionDocument;
	}
	
	private void addNonDnaInteractions(SBOLDocument document, ModuleDefinition moduleDef,Set<URI> compDefURIs,MultiValueMap<URI, SBOLInteractionSummary> interactions) throws VPRException, SBOLValidationException, VPRTripleStoreException
	{
		for (URI uri:compDefURIs)
		{			
			Collection<SBOLInteractionSummary> participantInteractions=interactions.getCollection(uri);
			if (participantInteractions!=null)
			{
				for (SBOLInteractionSummary participantInteractionSummary:participantInteractions)
				{								
					String displayId=SBOLHandler.getDisplayId(uri.toString());
					if (moduleDef.getInteraction(displayId)==null)
					{
						SBOLDocument sbolInteractionDocument=getInteractionDetail(participantInteractionSummary, uri);
						
						Interaction interaction=sbolInteractionDocument.getModuleDefinitions().iterator().next().getInteractions().iterator().next();
						if (moduleDef.getInteraction(interaction.getDisplayId())==null)
						{
							boolean hasDnaParticipant=hasDnaParticipant(sbolInteractionDocument, moduleDef, interaction);
							if (!hasDnaParticipant)
							{
								//DNA interactions are already added.
								addInteraction(sbolInteractionDocument, moduleDef, sbolInteractionDocument, null, null);
								if (interaction.containsType(SystemsBiologyOntology.NON_COVALENT_BINDING))
								{													
									addNonDnaInteractions(document, moduleDef, new HashSet<URI>(participantInteractionSummary.getComponentDefs()), interactions);							
								}								
							}	
						}
					}
				}
			}
		}
	}

	private ComponentDefinition createComponentDefinition(SBOLDocument document, ComponentDefinition tempCompDef) throws SBOLValidationException
	{
		String defaultURIPrefix=document.getDefaultURIprefix();
		String tempDefaultURIPrefix=SBOLHandler.getBaseUri(tempCompDef.getIdentity().toString());
		document.setDefaultURIprefix(tempDefaultURIPrefix);
		ComponentDefinition compDef=document.createComponentDefinition(tempCompDef.getDisplayId(),tempCompDef.getVersion(),tempCompDef.getTypes());
		document.setDefaultURIprefix(defaultURIPrefix);
		return compDef;
	}
	
			
	private ModuleDefinition copySourceModuleDefinition(SBOLDocument document, ModuleDefinition sourceModuleDef) throws SBOLValidationException, VPRException, VPRTripleStoreException
	{
		ModuleDefinition newModuleDef=createModuleDefinition(document, sourceModuleDef);
		copyIdentifiedProperties(document,newModuleDef, sourceModuleDef);
		for (Interaction sourceInteraction: sourceModuleDef.getInteractions())
		{
			Interaction interaction=copyInteraction(document, sourceInteraction, newModuleDef);	
			
			for (Participation participation:interaction.getParticipations())
			{
				URI componentDefinitionURI=participation.getParticipant().getDefinitionURI();					
				if (document.getComponentDefinition(componentDefinitionURI)==null)
				{
					SBOLDocument tempDocument= SBOLStackHandler.getComponent(componentDefinitionURI, this.endPointUrl);
					for (ComponentDefinition tempCompDef:tempDocument.getComponentDefinitions())
					{
						ComponentDefinition compDefParticipation=createComponentDefinition(document, tempCompDef);
						compDefParticipation.setRoles(tempCompDef.getRoles());
						copyIdentifiedProperties(document, compDefParticipation, tempCompDef);
					}						
				}
			}
		}		
		return newModuleDef;
	}
	
	private List<ModuleDefinition> addInteraction(SBOLDocument document, ModuleDefinition moduleDef, SBOLInteractionSummary summaryInteraction, ComponentDefinition compDef, ComponentDefinition design) throws VPRException, SBOLValidationException, VPRTripleStoreException
	{
		SBOLDocument sbolInteractionDocument=SBOLStackHandler.getInteraction(this.endPointUrl,summaryInteraction.getUri());
		List<ModuleDefinition> moduleDefs= addInteraction(document, moduleDef, sbolInteractionDocument, compDef, design);
		
		return moduleDefs;
	}
	
	private List<ModuleDefinition> addInteraction(SBOLDocument document, ModuleDefinition moduleDef, SBOLDocument sourceDocument, ComponentDefinition compDef, ComponentDefinition design) throws VPRException, SBOLValidationException, VPRTripleStoreException
	{
		List<ModuleDefinition> moduleDefs=new ArrayList<ModuleDefinition>();
		if (sourceDocument.getModuleDefinitions()!=null)
		{
			for (ModuleDefinition moduleDefTemp:sourceDocument.getModuleDefinitions())
			{
				ModuleDefinition newModuleDefinition=document.getModuleDefinition(moduleDefTemp.getIdentity());
				if (newModuleDefinition==null)
				{
					newModuleDefinition=copySourceModuleDefinition(document, moduleDefTemp);
					setInputsOutputs(newModuleDefinition);
					linkToParent(newModuleDefinition, moduleDef);
				}
				if ( compDef!=null && compDef.getTypes().contains(ComponentDefinition.DNA))
				{
					linkComponent(moduleDef, design, compDef);
				}		
				moduleDefs.add(newModuleDefinition);
			}	
		}
		return moduleDefs;
	}
	
	
	private void findUses(List<Component> result, ComponentDefinition design, ComponentDefinition compDef)
	{
		if (design.getComponents()!=null)
		{
			for (Component componentTemp: design.getComponents())
			{
				if (componentTemp.getDefinitionURI().equals(compDef.getIdentity()))
				{
					result.add(componentTemp);
				}
				findUses(result, componentTemp.getDefinition(), compDef);
			}
			
		}
	}
	
	private void linkComponent(ModuleDefinition moduleDef, ComponentDefinition design, ComponentDefinition compDef) throws SBOLValidationException
	{
		FunctionalComponent fcDesign=getFunctionalComponent(moduleDef, design.getIdentity());
		FunctionalComponent fcComponent=getFunctionalComponent(moduleDef, compDef.getIdentity());		
		List<Component> uses=new ArrayList<Component>();
		findUses(uses, design, compDef);
		int i=1;
		
		for (Component component: uses)
		{
			MapsTo mapsTo=fcDesign.getMapsTo(compDef.getDisplayId() + "_mapsTo_" + i++);
			if (mapsTo==null) {
				
				fcDesign.createMapsTo(compDef.getDisplayId() + "_mapsTo", RefinementType.USELOCAL, fcComponent.getIdentity(), component.getIdentity());
			}
		}
	}
	
	private List<ComponentDefinition> getFlattenedDesigns(SBOLDocument document) throws SBOLValidationException, VPRException
	{
		List<ComponentDefinition> flattened=null;
		Set<ComponentDefinition> designs = SBOLHandler.getRootComponentDefinitions(document, ComponentDefinition.DNA);
		if (designs!=null)
		{
			flattened=new ArrayList<ComponentDefinition>();
			for (ComponentDefinition compDef:designs)
			{
				if(isComposedOfBasicComponents(compDef))
				{
					flattened.add(compDef);
				}
				else if (isComposedOfSingleTU(compDef))
				{
					flattened.add(compDef);					
				}
				else //if (!isComposedOfTUs(compDef))
				{
					throw new VPRException("Complex designs are currently not supported. Designs MUST be composed of basic components or each design MUST contain a single transcriptional units. Design:" + compDef.getIdentity().toString());
					/*TODO Open the brackets and test this part of the code.
					 * flattened.add(getFlattened(document, compDef));
					extractTUs(compDef);*/
				}
			}
		}
		return flattened;
	}		
	
	private void extractTUs(ComponentDefinition compDef) throws VPRException {
		throw new VPRException("extractTUs method has not been implemented yet!");		
	}

	private void addComponentOrder(ComponentDefinition compDefNew, List<Component> leafNodes) throws SBOLValidationException
	{
		if (leafNodes.size()>1)
		{
			for (int i=0;i<leafNodes.size()-2;i++)
			{
				Component preceding=leafNodes.get(i);
				Component preceded=leafNodes.get(i+1);				
				compDefNew.createSequenceConstraint(preceding.getDisplayId() + "_precedes_" + preceded.getDisplayId() , RestrictionType.PRECEDES, preceding.getIdentity(), preceded.getIdentity());
			}
		}
	}
	
	


	private int getCountByRole(List<ComponentDefinition> compDefs, URI role)
	{
		int result=0;
		for (ComponentDefinition compDef:compDefs)
		{
			if (compDef.containsRole(role))
			{
				result++;
			}
		}
		return result;
	}
	
	//Here it is assumed that if there is only one terminator, it is a single TU.
	//if the number of terminators is more than 1, there must be only one promoter.
	private boolean isComposedOfSingleTU(ComponentDefinition compDef) {
		if (compDef.getComponents()!=null)
		{
			List<ComponentDefinition> compDefs=new ArrayList<ComponentDefinition>();
			getLeafComponentDefinitions(compDef, compDefs);
			int terminatorCount=getCountByRole(compDefs, SequenceOntology.TERMINATOR);
			if (terminatorCount==1)
			{
				return true;
			}
			else if (terminatorCount>1)
			{
				int promoterCount=getCountByRole(compDefs, SequenceOntology.PROMOTER);
				if (promoterCount>1)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean isBasicComponent(ComponentDefinition compDef)
	{
		if (compDef.containsRole(SequenceOntology.ENGINEERED_REGION) ||
				compDef.containsRole(SequenceOntology.ENGINEERED_GENE))
		{
			return false;
		}
		return true;
	}
	
	private boolean isComposedOfBasicComponents(ComponentDefinition compDef) {
		if (compDef.getComponents()!=null)
		{
			for (Component component:compDef.getComponents())
			{
				if (!isBasicComponent(component.getDefinition()))
				{
					return false;
				}
				/*if (component.getDefinition().getComponents()!=null && component.getDefinition().getComponents().size()>0)					
				{
					return false;
				}*/
			}
		}
		return true;
	}

	private String getDisplayId(List<ComponentDefinition> flattenedDesigns)
	{
		String displayId="";
		for (ComponentDefinition compDef: flattenedDesigns)
		{
			if (displayId.length()==0)
			{
				displayId=compDef.getDisplayId();
			}
			else
			{
				displayId=displayId + "_" + compDef.getDisplayId();
			}
		}
		displayId = displayId  + "_module"; 					 
		return displayId;
	}
		
		
	
	private Interaction copyInteraction (SBOLDocument doc, Interaction source,ModuleDefinition newModuleDef) throws SBOLValidationException, VPRException
	{
		Interaction interaction=newModuleDef.getInteraction(source.getDisplayId());
		if (interaction!=null)
		{
			return interaction;
		}
		interaction=newModuleDef.createInteraction(source.getDisplayId(), source.getTypes());			
		
		copyIdentifiedProperties(doc,interaction, source);	
		for (Participation sourceParticipation:source.getParticipations())
		{
			FunctionalComponent fcSource=sourceParticipation.getParticipant();
			FunctionalComponent fcNew=getFunctionalComponent(newModuleDef, fcSource.getDefinitionURI());
			if (fcNew==null)
			{
				URI definitionURI=fcSource.getDefinitionURI();
				AccessType access=fcSource.getAccess();				
				String displayId=SBOLHandler.getDisplayId(definitionURI);
				DirectionType direction=fcSource.getDirection();
				access=AccessType.PUBLIC;								
				fcNew=newModuleDef.createFunctionalComponent(displayId,access, definitionURI, direction);
			}
			Participation participation=interaction.createParticipation(sourceParticipation.getDisplayId(), fcNew.getIdentity(), sourceParticipation.getRoles());
			copyIdentifiedProperties(doc,participation, sourceParticipation);
			participation.setParticipant(fcNew.getIdentity());
		}
		return interaction;		
	}
		
	private boolean isPublicInputOrOutput(FunctionalComponent fComp)
	{
		boolean result=false;
		if (fComp.getAccess().equals(AccessType.PUBLIC))
			{
			if (fComp.getDirection().equals(DirectionType.IN) || fComp.getDirection().equals(DirectionType.INOUT) || fComp.getDirection().equals(DirectionType.OUT))
			{
				result=true;			
			}
		}
		return result;
	}
	
	

	/* Parent Orientation: + + - -
	 * Child Annotation  : + - + -
	 * Result            : + - - +
	 * */
	private OrientationType getOrientation(OrientationType parentOrientation, SequenceAnnotation seqAnnotation)
	{
		OrientationType newOrientation=OrientationType.INLINE;		
		if (seqAnnotation!=null)
		{			
			if (seqAnnotation.getLocations()!=null)
			{
				OrientationType childOrientation=seqAnnotation.getLocations().iterator().next().getOrientation();
				if (parentOrientation.equals(childOrientation))
				{
					newOrientation=OrientationType.INLINE;
				}
				else
				{
					newOrientation=OrientationType.REVERSECOMPLEMENT;
				}					
			}			
		}
		return newOrientation;
	}
	
	
	private boolean contains(List<Interaction> interactions, URI type)
	{
		for (Interaction interaction:interactions)
		{
			if (interaction.containsType(type))
			{
				return true;				
			}
		}
		return false;
	}
	
	private List<Interaction> getInteractions(ModuleDefinition moduleDef, FunctionalComponent fComp)
	{
		List<Interaction> interactions=new ArrayList<Interaction>();
		for (Interaction interaction:moduleDef.getInteractions())
		{
			for (Participation participation:interaction.getParticipations())
			{
				if (participation.getParticipantURI().equals(fComp.getIdentity()))
				{
					interactions.add(interaction);
					break;
				}
			}
		}
		return interactions;
	}

	private boolean hasDnaParticipant(SBOLDocument document,ModuleDefinition moduleDef, Interaction interaction)
	{
		for (Participation participant:interaction.getParticipations())
		{
			ComponentDefinition compDef=participant.getParticipantDefinition();
			if (compDef!=null && compDef.containsType(ComponentDefinition.DNA))
			{
				return true;
			}
		}
		return false;
	}
	
	
	private QName getNameSpace(SBOLDocument doc, String nameSpaceURI)
	{
		if (doc.getNamespaces()!=null)
		{
			for (QName qName:doc.getNamespaces())
			{
				if (qName.getNamespaceURI().toLowerCase().equals(nameSpaceURI.toLowerCase()))
				{
					return qName;
				}
			}
		}
		return  null;
	}
	private void copyIdentifiedProperties(SBOLDocument doc,Identified identified, Identified source) throws SBOLValidationException
	{
		if (source.hasAnnotations())
		{
			for (Annotation annotation:source.getAnnotations())
			{				
				if (annotation.isStringValue())
				{
					QName qname=annotation.getQName();	
					QName qNameToUse=getNameSpace(doc, qname.getNamespaceURI());
					if (qNameToUse==null)
					{
						doc.addNamespace(qname);
					}
					else
					{
						qname=new QName(qNameToUse.getNamespaceURI(), qname.getLocalPart(), qNameToUse.getPrefix());
					}
					identified.createAnnotation(qname, annotation.getStringValue());
				}
				else
				{
					//TODO					
				}
			}
		}
		if (source.isSetName()) {
			identified.setName(source.getName());
		}
		if (source.isSetDescription()) {
			identified.setDescription(source.getDescription());
		}
	}
	
	
	private int getPosition(ComponentDefinition[] compDefs, URI role, int startFrom, OrientationType orientation)
	{
		int pos=-1;
		if (orientation==OrientationType.INLINE)
		{
			for (int i=startFrom;i<compDefs.length;i++)
			{
				if (compDefs[i].containsRole(role))
				{
					return i;
				}
			}
		}
		else
		{
			for (int i=startFrom;i>0;i--)
			{
				if (compDefs[i].containsRole(role))
				{
					return i;
				}
			}
		}
		return pos;
	}
	
	
	private MapsTo getMapsTo(Module module,
			FunctionalComponent functionalComp, FunctionalComponent childFunctionalComp) {
		if (module.getMapsTos()!=null)
		{
			for (MapsTo mapsTo:module.getMapsTos())
			{
				if (mapsTo.getLocalURI().equals(functionalComp.getIdentity()) && mapsTo.getRemoteURI().equals(childFunctionalComp.getIdentity()))
				{
					return mapsTo;
				}
			}
		}
		return null;
	}

	private Module getSubModule(ModuleDefinition parentModuleDef, ModuleDefinition moduleDef) {		
		if (parentModuleDef.getModules()!=null)
		{
			for (Module module:parentModuleDef.getModules())
			{
				if (module.getDefinitionURI().equals(moduleDef.getIdentity()))
				{
					return module;
				}
			}
		}
		return null;
	}

	private FunctionalComponent getFunctionalComponent(ModuleDefinition moduleDef,
			URI componentDefinitionURI) {
		if (moduleDef.getFunctionalComponents()!=null)
		{
			
			for (FunctionalComponent fComp:moduleDef.getFunctionalComponents())
			{
				if (fComp.getDefinitionURI().equals(componentDefinitionURI))
				{
					return fComp;
				}
			}
		}
		return null;
	}	


	
}




//
//- For each CDS that is not linked to a genetic production in a module, create a genetic production interaction
//  - If there is no module definition in the document including the CDS, then create the module definition
//  - If there are modules, but this interaction is not included, then find the top most module and create the interaction
//  	- If there is no top most module definition, then create a new module and add the interaction
//- If there is an interaction between entities of different modules, find the closest parent module and add the interaction  	



//Get the list of root designs
//If there is a single root, create a parent moduledef for that root
//If there are multiple roots, create a moduledef and add submodules and moduledefs for roots

//For each bottom most componetdef of a root, with at least one TU, create a moduledef
//For each parent leaf, create a moduledef and add submodules for modules created from the previous step
//Repeat until reaching to the root(s)
//If there are multiple roots, create a moduledef and add submodules and moduledefs for leaves of roots 

/*
- Flatten the design
- Verify that that flattened design can be populated with interactions
- Create a module definition for the flattened design
- For each TU, add a module definition and add internal interactions
  Internal interactions:
    - Small molecule-Protein interactions
    - Protein dimerisation
    - Protein tetramerisation
    - Genetic production
    - Promoter activation and repression
    - Protein and complex degradation
  TU: Starts with at least one promoter and ends with at least one terminator
*/
