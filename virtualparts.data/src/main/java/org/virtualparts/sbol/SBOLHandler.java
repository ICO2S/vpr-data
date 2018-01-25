package org.virtualparts.sbol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;

import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.Range;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceConstraint;
import org.virtualparts.VPRException;

public class SBOLHandler {

	public static SBOLDocument read(String sbolData) throws VPRException
	{
		try
		{
			InputStream is = new ByteArrayInputStream(sbolData.getBytes() );	
			SBOLDocument doc=SBOLReader.read(is);
			return doc;
		}
		catch (Exception e)
		{
			throw new VPRException("Could not read the sbol file",e);
		}
	}
	
	public static String write(SBOLDocument doc) throws VPRException
	{
		try
		{
			String output="";						
			if (doc!=null)
			{
				OutputStream stream=new ByteArrayOutputStream();
				SBOLWriter.write(doc, stream);
				output=stream.toString();			
			}
			return output;
		}
		catch (Exception e)
		{
			throw new VPRException("Could not write the sbol file",e);
		}
	}

	
	public static void write(SBOLDocument doc, File file) throws VPRException
	{
		try
		{
			OutputStream stream=new FileOutputStream(file);
			SBOLWriter.write(doc, stream);
			String output=stream.toString();			
		}
		catch (Exception e)
		{
			throw new VPRException("Could not write the sbol file",e);
		}
	}
	
	public static Set<ComponentDefinition> getRootComponentDefinitions(SBOLDocument doc,URI type)
	{
		Set<URI> childNodes=new HashSet<URI>();
		Set<ComponentDefinition> rootNodes=new HashSet<ComponentDefinition>();
		
		for (ComponentDefinition compDef:doc.getComponentDefinitions())
		{
			for (Component comp:compDef.getComponents())
			{
				if (comp.getDefinitionURI()!=null)
				{
					childNodes.add(comp.getDefinitionURI());
				}
			}
		}
		for (ComponentDefinition compDef:doc.getComponentDefinitions())
		{
			if (!childNodes.contains(compDef.getIdentity()) && compDef.containsType(type))
			{
				rootNodes.add(compDef);
			}
		}
		return rootNodes;
	}
	
	public static List<Component> getOrderedComponents(ComponentDefinition componentDef)
    {
        Set<Component> componentList = componentDef.getComponents();
        Component[] components = componentList.toArray(new Component[componentList.size()]);
        sort(components, 0, components.length - 1, componentDef);
        return new ArrayList<>(Arrays.asList(components));
    }
	
	 //See more at: http://www.java2novice.com/java-sorting-algorithms/quick-sort/#sthash.6zVoEsaf.dpuf
    private static void sort(Component[] components, int lowerIndex, int higherIndex, ComponentDefinition componentDef)
    {
        
        StringBuilder sb = new StringBuilder("sorting... ");
        for(int i=0; i<components.length; i++) {
            sb.append(components[i].getDisplayId()).append(", ");
        }
        System.out.println(sb.toString());

        int i = lowerIndex;
        int j = higherIndex;
        // calculate pivot number, I am taking pivot as middle index number
        Component pivot = components[lowerIndex + (higherIndex - lowerIndex) / 2];
        // Divide into two arrays
        
        while (i <= j)
        {
            /**
             * In each iteration, we will identify a number from left side which
             * is greater then the pivot value, and also we will identify a
             * number from right side which is less then the pivot value. Once
             * the search is done, then we exchange both numbers.
             */
            while (!components[i].getIdentity().toString().equals(pivot.getIdentity().toString()) && precedes(components[i], pivot, componentDef))
            {
                i++;
            }
            while (!components[j].getIdentity().toString().equals(pivot.getIdentity().toString()) && !precedes(components[j], pivot, componentDef))
            {
                j--;
            }
            if (i <= j)
            {
                exchangeComponents(components, i, j);
                //move index to next position on both sides
                i++;
                j--;
            }
        }
        // call sort() method recursively
        if (lowerIndex < j)
        {
            sort(components, lowerIndex, j, componentDef);
        }
        if (i < higherIndex)
        {
            sort(components, i, higherIndex, componentDef);
        }
    }
    
    private static void exchangeComponents(Component[] components, int i, int j)
    {
        Component temp = components[i];
        components[i] = components[j];
        components[j] = temp;
    }
	
    public static boolean precedes(Component component1, Component component2, ComponentDefinition componentDef)
    {
        Set<SequenceAnnotation> annotations = componentDef.getSequenceAnnotations();
        SequenceAnnotation annotation1 = getSequenceAnnotation(component1, new ArrayList<>(annotations));
        SequenceAnnotation annotation2 = getSequenceAnnotation(component2, new ArrayList<>(annotations));
        boolean precedes = true;
        boolean found = false;
        
        // If there are sequence annotations, compare their ranges
      
        if (annotation1 != null && annotation2 != null)
        {
            if (annotation1.getLocations() != null && annotation2.getLocations() != null)
            {
                if ((annotation1.getLocations().iterator().next() instanceof Range) && (annotation2.getLocations().iterator().next() instanceof Range))
                {
                    Range range1 = (Range) annotation1.getLocations().iterator().next();
                    Range range2 = (Range) annotation2.getLocations().iterator().next();
                    precedes = (range1.getStart() < range2.getStart());
                    found = true;
                }
            }
        }
        if (!found)
        {
            precedes = precedesByConstraint(component1, component2, componentDef);
        }
       
        return precedes;
    }


    public static SequenceAnnotation getSequenceAnnotation(Component component, List<SequenceAnnotation> annotations)
    {
        for (SequenceAnnotation sequenceAnnotation : annotations)
        {
            if (sequenceAnnotation.getComponent().getIdentity().equals(component.getIdentity()))
            {
                return sequenceAnnotation;
            }
        }
        return null;
    }
    
    private static boolean precedesByConstraint(Component component1, Component component2, ComponentDefinition componentDef)
    {
        Set<SequenceConstraint> constraints = componentDef.getSequenceConstraints();
        Map<String, String> constraintMap = new HashMap<>();

        for (SequenceConstraint constraint : constraints) {
            if (precedes(component1, component2, constraint)) {
                return true;
            }
            
            constraintMap.put(constraint.getSubjectURI().toString(), constraint.getObjectURI().toString());
        }
        
        
        
        // If component1 has no constraint where it is the object, it is first
        
        if(!constraintMap.containsValue(component1.getIdentity().toString())
                && constraintMap.containsKey(component1.getIdentity().toString())) {
            
            return true;
        } else {
            
            // Starting from component1, follow chain of subject -> object derived from all constraints
            
            String next = component1.getIdentity().toString();
            
            while(next != null) {
                if(constraintMap.containsKey(next)) {
                    next = constraintMap.get(next);

                    if(next.equals(component2.getIdentity().toString())) {
                        return true;
                    }
                } else {
                    next = null;
                }
            }
        }

        return false;
    }
    
    private static boolean precedes(Component component1, Component component2, SequenceConstraint constraint)
    {
        
        if (constraint.getSubject().equals(component1.getIdentity()) && constraint.getObject().equals(component2.getIdentity())
                && constraint.getRestriction().equals(RestrictionType.PRECEDES))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static Annotation getAnnotation(Identified identified, QName qname)
    {
        if (identified != null && identified.getAnnotations() != null)
        {
            for (Annotation annotation : identified.getAnnotations())
            {
                                 
                if (annotation.getQName().equals(qname)) {
                    return annotation;
                }
            }
        }
        return null;
    }
   
    
    public static Annotation getAnnotation(Identified identified, URI uri)
    {
        if (identified != null && identified.getAnnotations() != null)
        {
            for (Annotation annotation : identified.getAnnotations())
            {
                                 
                if (toURI(annotation.getQName()).equals(uri)) {
                    return annotation;
                }
            }
        }
        return null;
    }
    
	public static List<ComponentDefinition> getComponentDefinitionWithRole(SBOLDocument document, ModuleDefinition module, Interaction interaction, URI participationRole)
	{
		List<ComponentDefinition> components=null;
		if (interaction.getParticipations()!=null)
		{
			for (Participation participation:interaction.getParticipations())
			{
				if (participation.containsRole(participationRole))
				{
					if (components==null)
					{
						components=new ArrayList<ComponentDefinition>();
					}
					ComponentDefinition compDef=getParticipantDefinition(document, module, participation);
					components.add(compDef);
					//TODO participation.getParticipantDefinition() method does not work
					//components.add(participation.getParticipantDefinition());
					//participation.getParticipant()
				}
			}
		}
		
		return components;
	}
	
	public static List<ComponentDefinition> getComponentDefinitionWithType(SBOLDocument document, ModuleDefinition module, Interaction interaction, URI componentDefinitionType)
	{
		List<ComponentDefinition> componentDefinitions=null;
		if (interaction.getParticipations()!=null)
		{
			for (Participation participation:interaction.getParticipations())
			{									
					ComponentDefinition compDef=getParticipantDefinition(document, module, participation);
					if (compDef.containsType(componentDefinitionType))
					{
						if (componentDefinitions==null)
						{
							componentDefinitions=new ArrayList<ComponentDefinition>();
						}
						componentDefinitions.add(compDef);
					}
					//TODO participation.getParticipantDefinition() method does not work
					//components.add(participation.getParticipantDefinition());
					//participation.getParticipant()				
			}
		}		
		return componentDefinitions;
	}
	
	public static List<ComponentDefinition> getParticipantDefinitions(SBOLDocument document, ModuleDefinition module, Interaction interaction)
	{
		List<ComponentDefinition> componentDefinitions=new ArrayList<ComponentDefinition>();
		if (interaction.getParticipations()!=null)
		{
			for (Participation participation:interaction.getParticipations())
			{		
					ComponentDefinition compDef=getParticipantDefinition(document, module, participation);
					componentDefinitions.add(compDef);										
			}
		}		
		return componentDefinitions;
	}
	
	public static ComponentDefinition getParticipantDefinition(SBOLDocument document, ModuleDefinition module, Participation participation)	
	{		
		URI participantURI=participation.getParticipantURI();
		FunctionalComponent fc=module.getFunctionalComponent(participantURI);
		ComponentDefinition compDef=document.getComponentDefinition(fc.getDefinitionURI());
		return compDef;
	}
	
	public static Participation getParticipation(Interaction interaction, URI participationRole)	
	{		
		if (interaction!=null && interaction.getParticipations()!=null)
		{
			for (Participation participation: interaction.getParticipations())
			{
				Set<URI> roles= participation.getRoles();
				if (roles.contains(participationRole))
				{
					return participation;
				}
			}
		}
		return null;		
	}
	
	public static List<ComponentDefinition> getComponentDefinitionsByType(SBOLDocument sbolDocument, URI type)
	{
		List<ComponentDefinition> compDefsFound=new ArrayList<ComponentDefinition>();
		Set<ComponentDefinition> compDefs=sbolDocument.getComponentDefinitions();
		for (ComponentDefinition compDef:compDefs)
		{
			if (compDef.containsType(type))
			{
				compDefsFound.add(compDef);
			}
		}
		return compDefsFound;		
	}

	
	public static List<ComponentDefinition> getComponentDefinitionsByRole(SBOLDocument sbolDocument, URI role)
	{
		List<ComponentDefinition> compDefsFound=new ArrayList<ComponentDefinition>();
		Set<ComponentDefinition> compDefs=sbolDocument.getComponentDefinitions();
		for (ComponentDefinition compDef:compDefs)
		{
			if (compDef.containsRole(role))
			{
				compDefsFound.add(compDef);
			}
		}
		return compDefsFound;		
	}
	 private static URI toURI(QName qname)
		{
			return URI.create(qname.getNamespaceURI() + qname.getLocalPart());
		}
	 
	public static String getDisplayId(Identified identified) 
	{
		if (identified.getDisplayId() != null && identified.getDisplayId().length() > 0)
		{
			return identified.getDisplayId();
		} 
		else 
		{
			return getDisplayId(identified.getIdentity());
		}
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
				String displayId= uriString.substring(index+1);
				char firstChar=displayId.charAt(0);
				if (Character.isDigit(firstChar))
				{
					uriString=uriString.substring(0,index);
					return getDisplayId(uriString);					
				}
				else
				{
					return displayId;
				}
			}
			else
			{
				return null;
			}
		}
		
	 public static String getVersion(String uriString)
		{
		 	int index=uriString.lastIndexOf("#");
			int index2=uriString.lastIndexOf("/");
			if (index2>index)
			{
				index=index2;
			}
			if (uriString.length()>index+1)
			{
				String displayId= uriString.substring(index+1);
				char firstChar=displayId.charAt(0);
				if (Character.isDigit(firstChar))
				{
					String version=uriString.substring(index+1);
					return version;
				}
				else
				{
					return null;
				}
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
				String displayId= uriString.substring(index+1);
				char firstChar=displayId.charAt(0);
				if (Character.isDigit(firstChar))
				{
					uriString=uriString.substring(0,index);
					return getBaseUri(uriString);					
				}
				else
				{
					return uriString.substring(0,index);
				}
				
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

		public static ComponentDefinition appendComponent(SBOLDocument doc, ComponentDefinition design, String displayId,URI role,
				String sequence) throws SBOLValidationException {
			ComponentDefinition cd=doc.createComponentDefinition(displayId, ComponentDefinition.DNA);
			cd.setName(displayId);
			cd.addRole(role);
			
			Component comp=design.createComponent("comp_" + displayId, AccessType.PUBLIC, cd.getIdentity());

			
			Sequence seq=doc.createSequence("seq_" + displayId, sequence, Sequence.IUPAC_DNA);
			cd.addSequence(seq);
			int start=1;
			if (design.getSequences()==null  || design.getSequences().size()==0)
			{
				Sequence designSequence=doc.createSequence("seq_" + design.getDisplayId(), sequence, Sequence.IUPAC_DNA);
				design.addSequence(designSequence);					
			}
			else
			{
				
				String elements=design.getSequences().iterator().next().getElements();
				start=elements.length()+1;
				elements+=sequence;
				design.getSequences().iterator().next().setElements(elements);
			}
			int end=start + sequence.length();
			String annotationId=design.getDisplayId() + "_" + displayId + "_" + start + "_" + end;
			SequenceAnnotation sa= design.createSequenceAnnotation(annotationId, annotationId + "_loc", start, end);
			 
			sa.setComponent(comp.getIdentity());
			return cd;
		}
		
		
    
}
