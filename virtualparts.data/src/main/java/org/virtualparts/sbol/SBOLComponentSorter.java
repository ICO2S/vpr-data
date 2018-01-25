package org.virtualparts.sbol;

import java.util.Comparator;
import java.util.Set;

import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Location;
import org.sbolstandard.core2.Range;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceConstraint;

public class SBOLComponentSorter implements Comparator<Component>{
	private ComponentDefinition componentDef;
	public SBOLComponentSorter(ComponentDefinition componentDef) {
		this.componentDef=componentDef;
	}
	
	//TODO: It is assumed that SequenceAnnotations have single locations
	@Override
    public int compare(Component o1, Component o2) {
        if (componentDef.getSequenceAnnotations()!=null)
        {
        	SequenceAnnotation annotation1=null;
            SequenceAnnotation annotation2=null;
            
        	for (SequenceAnnotation annotation:componentDef.getSequenceAnnotations())
	        {
	        	if (annotation.getComponentURI().equals(o1.getIdentity()))
	        	{
	        		annotation1=annotation;
	        	}
	        	else if (annotation.getComponentURI().equals(o2.getIdentity()))
	        	{
	        		annotation2=annotation;
	        	}
	        	if (annotation1!=null && annotation2!=null)
	        	{
	        		break;
	        	}        			
	        }
		
			if (annotation1!=null && annotation2!=null)
        	{
				int start1=getStart(annotation1);
				int start2=getStart(annotation2);
				return Integer.compare(start1, start2);
        	}
        }
        if (componentDef.getSequenceConstraints()!=null)
        {
        	for (SequenceConstraint constraint:componentDef.getSequenceConstraints())
        	{
        		if  (precedes(constraint,o1,o2))
        		{
        			return -1;
        		}
        		else if (precedes(constraint,o2,o1))
        		{
        			return 1;
        		}        		        		        		
        	}
        }
		return 0;
    }
	
	public boolean precedes(SequenceConstraint constraint, Component component1, Component component2)
	{
		if (constraint.getSubject().getIdentity().equals(component1.getIdentity()) &&
				constraint.getObject().getIdentity().equals(component2.getIdentity()) && 
				constraint.getRestriction().equals(RestrictionType.PRECEDES))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	private int getStart(SequenceAnnotation annotation)
	{
		Set<Location> locations=annotation.getLocations();
		if (locations!=null && locations.size()>0)
		{
			Location location=locations.iterator().next();
			if (location instanceof Range)
			{
				Range range=(Range) location;
				return range.getStart();
			}
		}
		return -1;
	}

}
