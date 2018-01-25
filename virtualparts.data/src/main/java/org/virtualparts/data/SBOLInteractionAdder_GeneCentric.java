package org.virtualparts.data;

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
import org.virtualparts.VPRException;
import org.virtualparts.VPRTripleStoreException;
import org.virtualparts.sbol.SBOLHandler;

public class SBOLInteractionAdder_GeneCentric{
	private URI endPointUrl=null;
	public SBOLInteractionAdder_GeneCentric(URI endPointUrl)
	{
		this.endPointUrl=endPointUrl;
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
					ModuleDefinition parentModuleDef=document.createModuleDefinition(getDisplayId(designs));					
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
		
	private ModuleDefinition addModuleDefinition(SBOLDocument document, ComponentDefinition compDef, ModuleDefinition parentModuleDef,MultiValueMap<URI, SBOLInteractionSummary>  interactions) throws SBOLValidationException, VPRException, VPRTripleStoreException
	{
		ModuleDefinition moduleDef=null;
		if (compDef.getComponents()!=null && compDef.getComponents().size()>0)
		{
			document.setDefaultURIprefix(SBOLHandler.getBaseUri(compDef.getIdentity()));
			moduleDef=document.createModuleDefinition(compDef.getDisplayId() + "_module", "1");	
			Set<URI> nonDNAComponentDefs=addInternalInteractions(document, moduleDef, interactions, compDef);
			
//			if (isComposedOfTUs(compDef))
//			{
//				throw new VPRException("Complex designs with multiple transcription units within the same ComponentDefinitions are currently not supported. Please split them into individual TUs, each reprenseted by a different ComponentDefinition and try again! Design:" + compDef.getIdentity().toString());
//
//				/*				
//				//for (Component component:compDef.getComponents())
//				//{
//				//	compDefs.add(component.getDefinition());
//				//}
//				addModuleDefinitions(document, compDefs, moduleDef, interactions);	
//				*/
//			}
//			else
//			{
//				addInternalInteractions(document, moduleDef, interactions, compDef);
//			}
			
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
	
	
	private void getLeafComponentDefinitions(ComponentDefinition compDef,List<ComponentDefinition> compDefs, URI componentDefToExclude) {
		for (Component component:compDef.getComponents())
		{
			ComponentDefinition subCompDef=component.getDefinition();
			if (subCompDef.containsRole(componentDefToExclude))
			//E.g. In some modelling abstractions, it is not important how the promoter is composed. Interactions should be treated at the promoter level for iBioSim promoter models.
			{
				compDefs.add(subCompDef);				
			}
			else
			{
				if (subCompDef.getComponents()==null || subCompDef.getComponents().size()==0)
				{//If leaf then add
					compDefs.add(subCompDef);
				}
				else
				{
					getLeafComponentDefinitions(subCompDef, compDefs, componentDefToExclude);
				}
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
			List<SBOLInteractionSummary> componentInteractions=SBOLStackHandler.getInteractions(componentDef.getIdentity(),this.endPointUrl);
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
		List<SBOLInteractionSummary> proteinInteractions=SBOLStackHandler.getInteractions(proteinDefUri,this.endPointUrl);
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
							List<SBOLInteractionSummary> dimerInteractions=SBOLStackHandler.getInteractions(componentDefUri,this.endPointUrl);
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
	
	
	private void addComponentDefinition(ComponentDefinition flattenedDesign, ComponentDefinition parentCompDef, Component subComponent,List<Component> leafComponents, OrientationType parentOrientation) throws SBOLValidationException 
	{
		ComponentDefinition subCompDef=subComponent.getDefinition();
		if (subCompDef.getComponents()==null || subCompDef.getComponents().size()==0)
		{
			Component newSubComponent=flattenedDesign.createComponent(subComponent.getDisplayId(), AccessType.PUBLIC, subCompDef.getIdentity());
			OrientationType newOrientation=getOrientation(parentOrientation, parentCompDef.getSequenceAnnotation(subComponent));			
			flattenedDesign.createSequenceAnnotation(subCompDef.getDisplayId() + "_" + leafComponents.size(), String.valueOf(leafComponents.size()), newOrientation);			
			leafComponents.add(newSubComponent);
		}
		else
		{
			for (Component component:subCompDef.getComponents())
			{
				addComponentDefinition(flattenedDesign, subCompDef,component, leafComponents,parentOrientation);
			}
		}		
	}

	

	
	/*private boolean particantsExist(ModuleDefinition moduleDef, List<URI> componentDefURIs)
	{
		
	}*/
	private void addInteractionsBetweenSubModules(SBOLDocument document, ModuleDefinition moduleDef,
			List<ModuleDefinition> subModuleDefinitions, MultiValueMap<URI, SBOLInteractionSummary> interactions) throws VPRException, SBOLValidationException, VPRTripleStoreException 
	{
		
		if (moduleDef.getFunctionalComponents()!=null)
		{
			List<SBOLInteractionSummary> interactionsToAdd=new ArrayList<SBOLInteractionSummary>();
			
			for (FunctionalComponent fComp:moduleDef.getFunctionalComponents())
			{				
				if (fComp.getDirection().equals(DirectionType.NONE))
				{
					continue;
				}
				Collection<SBOLInteractionSummary> componentInteractions=interactions.getCollection(fComp.getDefinition().getIdentity());
				if (componentInteractions==null)
				{
					continue;
				}
				for (SBOLInteractionSummary interactionSummary:componentInteractions)
				{
					
					if (!interactionSummary.getComponentDefs().contains(fComp.getDefinitionURI()))
					{
						interactionSummary.getComponentDefs().add(fComp.getDefinitionURI());
					}
					
					boolean addInteraction=true;
					for (URI interactingComponent:interactionSummary.getComponentDefs())
					{
						ComponentDefinition interactingComponentDef=document.getComponentDefinition(interactingComponent);
						//If one of the participants is DNA, then increase the number of matches to avoid adding this interaction. Such interactions are already included in submodules
						
						if (interactingComponentDef!=null && interactingComponentDef.containsType(ComponentDefinition.DNA))
						{
							addInteraction=false;
							break;
						}
					}
					if (addInteraction && !interactionsToAdd.contains(interactionSummary))
					{
						for (Module subModule:moduleDef.getModules())
						{
							int numberOfParticipatingComponents=0;		
									for (URI interactingComponent:interactionSummary.getComponentDefs())
									{																														
												for (MapsTo mapsTo:subModule.getMapsTos())
												{
													URI targetURI=moduleDef.getFunctionalComponent(mapsTo.getLocalURI()).getDefinitionURI();
																							
													if (interactingComponent.equals(targetURI))
													{
														numberOfParticipatingComponents++;
														break;
													}	
												}
												
											}
																																										
									if (numberOfParticipatingComponents==interactionSummary.getComponentDefs().size())
									{
										addInteraction=false;
										break;
									}
						}
					}
				
							
					if (addInteraction)
					{
						interactionsToAdd.add(interactionSummary);
					}
				}								
			}
			
			if (interactionsToAdd.size()>0)
			{
				for (SBOLInteractionSummary interationSummary:interactionsToAdd)
				{
					addInteraction(document, moduleDef, interationSummary, null, null);
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
	

	
	private List<SBOLInteractionSummary> getInteraction(MultiValueMap<URI, SBOLInteractionSummary> interactions,URI componentURI, URI interactionType)
	{
		List<SBOLInteractionSummary> foundInteractions=new ArrayList<SBOLInteractionSummary>();
		Collection<SBOLInteractionSummary> componentInteractions=interactions.getCollection(componentURI);
		for (SBOLInteractionSummary interaction:componentInteractions)
		{
			if ((interaction.getTypes().contains(interactionType)))
			{
				foundInteractions.add(interaction);
			}
		}
		return foundInteractions;		
	}
	
	
		
	
	
	
	
	
	private Set<URI> addInternalInteractions(SBOLDocument document, ModuleDefinition moduleDef,
			MultiValueMap<URI, SBOLInteractionSummary> interactions, ComponentDefinition design) throws SBOLValidationException, VPRException, VPRTripleStoreException 
	{		
		List<Interaction> dnaInteractions=new ArrayList<Interaction>();
		
		List<ComponentDefinition> compDefs=new ArrayList<ComponentDefinition>();
		
		getLeafComponentDefinitions(design, compDefs,SequenceOntology.PROMOTER);
		
		for (ComponentDefinition compDef:compDefs)
		{
			//ComponentDefinition compDef=comp.getDefinition();
			Collection<SBOLInteractionSummary> interactionSummaries=interactions.getCollection(compDef.getIdentity());
			if (interactionSummaries!=null)
			{
				for (SBOLInteractionSummary interactionSummary:interactionSummaries)
				{
					Interaction interaction = addInteraction(document, moduleDef, interactionSummary, compDef, design);					
					dnaInteractions.add(interaction);										
				}
			}
		}
		
		/*FunctionalComponent fCompDesign= getFunctionalComponent(moduleDef, design.getIdentity());
		if (fCompDesign!=null)
		{
			fCompDesign.setDirection(DirectionType.NONE);
			fCompDesign.setAccess(AccessType.PRIVATE);			
		}*/
		
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
			//GMGM addNonDnaInteractions(document, moduleDef, nonDNAComponentDefs, interactions);
			
			
			
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
				}
			
			if (direction==DirectionType.IN )
			{
				if (existAsOutInSubModules(moduleDef, fComp))
				{
					direction=DirectionType.INOUT;
				}
			}
			
			fComp.setDirection(direction);
			fComp.setAccess(access);		
		}
	}
	
	private boolean existAsOutInSubModules(ModuleDefinition moduleDef, FunctionalComponent fComp)	
	{
		boolean result=false;
		if (moduleDef.getModules()!=null)
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
						SBOLDocument sbolInteractionDocument=SBOLStackHandler.getInteractionDetailed(this.endPointUrl,participantInteractionSummary.getUri());
						Interaction interaction=sbolInteractionDocument.getModuleDefinitions().iterator().next().getInteractions().iterator().next();
						if (moduleDef.getInteraction(interaction.getDisplayId())==null)
						{
							boolean hasDnaParticipant=hasDnaParticipant(sbolInteractionDocument, moduleDef, interaction);
							if (!hasDnaParticipant)
							{
								//DNA interactions are already added.
								addInteraction(document, moduleDef, interaction,null,null);	
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
	
	private Interaction addInteraction(SBOLDocument document, ModuleDefinition moduleDef, Interaction sourceInteraction,ComponentDefinition compDef, ComponentDefinition replacementCompDef) throws SBOLValidationException, VPRException, VPRTripleStoreException {
		Interaction interaction=copyInteraction(document, sourceInteraction, moduleDef,compDef,replacementCompDef);	
		
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
		return interaction;		
	}
			
	private Interaction addInteraction(SBOLDocument document, ModuleDefinition moduleDef, SBOLInteractionSummary summaryInteraction, ComponentDefinition compDef, ComponentDefinition replacementCompDef) throws VPRException, SBOLValidationException, VPRTripleStoreException
	{
		Interaction interaction=null;
		SBOLDocument sbolInteractionDocument=SBOLStackHandler.getInteraction(this.endPointUrl,summaryInteraction.getUri());
		for (ModuleDefinition moduleDefTemp:sbolInteractionDocument.getModuleDefinitions())
		{
			for (Interaction sourceInteraction:moduleDefTemp.getInteractions())
			{							
				interaction=addInteraction(document, moduleDef, sourceInteraction, compDef, replacementCompDef);				
			}
		}	
		return interaction;
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
	
	private ComponentDefinition getFlattened(SBOLDocument document, ComponentDefinition design) throws SBOLValidationException
	{
		if (document.getDefaultURIprefix()==null)
		{
			document.setDefaultURIprefix(SBOLHandler.getBaseUri(design.getIdentity().toString()));
		}
		ComponentDefinition flattenedDesign=document.createComponentDefinition(design.getDisplayId() + "_flattened", design.getTypes());
		List<Component> orderedComponents=SBOLHandler.getOrderedComponents(design);
		List<Component> leafComponents=new ArrayList<Component>();					
		for (Component component:orderedComponents)
		{
			addComponentDefinition(flattenedDesign, design, component,leafComponents,OrientationType.INLINE);
		}
		addComponentOrder(flattenedDesign, leafComponents);
		return flattenedDesign;
	}
	
	

	private List<ComponentDefinition> getComponents(ComponentDefinition compDef, URI subComponentRole)
	{
		List<ComponentDefinition> subComponentDefs=new ArrayList<ComponentDefinition>();
		if (compDef.getComponents()!=null)
		{
			for (Component component:compDef.getComponents())
			{
				if (component.getDefinition().containsRole(subComponentRole))
				{
					subComponentDefs.add(component.getDefinition());
				}
			}
		}
		return subComponentDefs;
	}

//	private boolean isComposedOfTUs(ComponentDefinition compDef) {
//		if (compDef.getComponents()!=null)
//		{
//			/*for (Component component:compDef.getComponents())
//			{
//				if (!component.getDefinition().containsRole(SequenceOntology.ENGINEERED_REGION) ||
//						!component.getDefinition().containsRole(SequenceOntology.ENGINEERED_GENE))
//				{
//					return false;
//				}
//			}*/
//		}
//		return false;
//	}
	

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
			getLeafComponentDefinitions(compDef, compDefs, SequenceOntology.PROMOTER);
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
		
	

	private ModuleDefinition getInteractionsModule(SBOLDocument sbolDocument) throws SBOLValidationException
	{
		sbolDocument.setDefaultURIprefix("http://www.virtualparts.org");//TODO:Replace the harcoded URL with a variable
		URI uri=URI.create(sbolDocument.getDefaultURIprefix() +  "interactions");
		ModuleDefinition moduleDef=sbolDocument.getModuleDefinition(uri);
		if (moduleDef==null)
		{			
			moduleDef=sbolDocument.createModuleDefinition(sbolDocument.getDefaultURIprefix(), "interactions", "1.0");
		}
		return moduleDef;
	}
		
												
	
	private List<ModuleDefinition> getUsedModuled(SBOLDocument document, ComponentDefinition compDef)
	{
		List<ModuleDefinition> moduleDefs=new ArrayList<ModuleDefinition>();
		if (document!=null && document.getModuleDefinitions()!=null && document.getModuleDefinitions().size()>0)
		{
			for (ModuleDefinition moduleDef:document.getModuleDefinitions())
			{
				if (moduleDef.getFunctionalComponents()!=null)
				{
					for (FunctionalComponent funcComp: moduleDef.getFunctionalComponents())
					{
						if (funcComp.getDefinition().equals(compDef.getIdentity()))
						{
							moduleDefs.add(moduleDef);
						}
					}
				}
			}
		}
		return moduleDefs;
	}	
		
	private boolean isForReplacement(FunctionalComponent fcSource, ComponentDefinition componentWithTheInteraction, ComponentDefinition replacement)
	{
		if (componentWithTheInteraction!=null && replacement!=null && fcSource.getDefinitionURI().equals(componentWithTheInteraction.getIdentity()))
		{
			return true;
		}
		else
		{
			return false;
		}			
	}
	private Interaction copyInteraction (SBOLDocument doc, Interaction source,ModuleDefinition moduleDef, ComponentDefinition componentWithTheInteraction, ComponentDefinition replacement) throws SBOLValidationException, VPRException
	{
		Interaction interaction=moduleDef.getInteraction(source.getDisplayId());
		if (interaction!=null)
		{
			return interaction;
		}
		interaction=moduleDef.createInteraction(source.getDisplayId(), source.getTypes());			
		
		copyIdentifiedProperties(doc,interaction, source);	
		for (Participation sourceParticipation:source.getParticipations())
		{
			FunctionalComponent fcSource=sourceParticipation.getParticipant();
			FunctionalComponent fc=null;
			if (isForReplacement(fcSource, componentWithTheInteraction, replacement))
			{
				fc=getFunctionalComponent(moduleDef, replacement.getIdentity());
			}			
			else
			{
				fc=getFunctionalComponent(moduleDef, fcSource.getDefinitionURI());
			}
			if (fc==null)
			{
				URI definitionURI=fcSource.getDefinitionURI();
				AccessType access=fcSource.getAccess();				
				String displayId=SBOLHandler.getDisplayId(definitionURI);
				DirectionType direction=fcSource.getDirection();
				access=AccessType.PUBLIC;								
				
				if (isForReplacement(fcSource, componentWithTheInteraction, replacement))
				{
					definitionURI=replacement.getIdentity();
					displayId=replacement.getDisplayId();					
				}
				fc=moduleDef.createFunctionalComponent(displayId,access, definitionURI, direction);
			}
			Participation participation=interaction.createParticipation(sourceParticipation.getDisplayId(), fc.getIdentity(), sourceParticipation.getRoles());
			copyIdentifiedProperties(doc,participation, sourceParticipation);
			participation.setParticipant(fc.getIdentity());
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
	
	private List<ModuleDefinition> getModuleDefinitions (SBOLDocument document, ComponentDefinition compDef)
	{
		List<ModuleDefinition> moduleDefinitions=new ArrayList<ModuleDefinition>();
		if (document!=null && document.getModuleDefinitions()!=null)
		{
			for (ModuleDefinition moduleDef:document.getModuleDefinitions())
			{
				if (moduleDef.getFunctionalComponents()!=null)
				{
					for (FunctionalComponent funcComp: moduleDef.getFunctionalComponents())
					{
						if (funcComp.getDefinitionURI().equals(compDef.getIdentity()))
						{
							moduleDefinitions.add(moduleDef);
						}
					}
				}
			}
		}
		return moduleDefinitions;
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
	
	private List<Component> getCDSComponents(List<Component> components,int promoterIndex, ComponentDefinition parentComponentDef) throws VPRException
	{
		Component promoterComp=components.get(promoterIndex);
		
		boolean foundTerminator=false;
		String moduleId="";
		OrientationType orientation=getOrientation(OrientationType.INLINE, parentComponentDef.getSequenceAnnotation(promoterComp));		
		int index=promoterIndex;
				
		int increment=1;
		if (orientation==OrientationType.REVERSECOMPLEMENT)
		{
			increment=-1;
		}		
		List<Component> CDSs=new ArrayList<Component>();	
		while (!foundTerminator)
		{
			Component currentComp=components.get(index);
			OrientationType currentComponnetOrientation=getOrientation(OrientationType.INLINE, parentComponentDef.getSequenceAnnotation(currentComp));
			if (moduleId.length()>0)
			{
				moduleId=moduleId + "_";
			}
			moduleId=moduleId + currentComp.getDefinition().getDisplayId();
			
			if (currentComp.containsRole(SequenceOntology.CDS))
			{
				if (currentComponnetOrientation==orientation)
				{
					CDSs.add(currentComp);
				}
			}
			else if (currentComp.containsRole(SequenceOntology.TERMINATOR))
			{
				if (currentComponnetOrientation==orientation)
				{
					foundTerminator=true;
					break;
				}
			}
			index=index + increment;
			if (index==-1 || index==components.size()+1)
			{
				break;
			}
		}
		if (foundTerminator)
		{
			return CDSs;
		}
		else
		{
			throw new VPRException(String.format("Could not create the model for %s. %s promoter does not end with a terminator!", parentComponentDef.getDisplayId(), promoterComp.getDisplayId()));			
		}
	}
	
	private String getId (List<Component> entities)
	{
		String id="";
		if (entities!=null)
		{
			for (Identified identified:entities)
			{
				if (id.length()>0)
				{
					id=id + "_";
				}
				id=id + identified.getDisplayId();
			}
		}
		return id;
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
