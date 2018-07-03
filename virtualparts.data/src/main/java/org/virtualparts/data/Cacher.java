package org.virtualparts.data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.apache.commons.jcs.JCS;
import org.apache.commons.jcs.access.CacheAccess;
import org.apache.commons.jcs.access.exception.CacheException;
import org.apache.commons.jcs.engine.control.CompositeCacheManager;
import org.sbolstandard.core2.Interaction;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.virtualparts.VPRException;

public class Cacher {
	private CacheAccess<Object, SBOLDocument> cache = null;
	
public Cacher() throws VPRException
{
	 try 
     {
		 CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance(); 
		 Properties props = new Properties(); 
		 //props.load(new "cache.ccf"); 
		 InputStream stream= new TripleStoreHandler("").getClass().getClassLoader().getResourceAsStream("cache.ccf");
		 props.load(stream);
		 //parseProperties(props);
		 ccm.configure(props);
			
         cache = JCS.getInstance( "default" );
     }
     catch ( Exception e ) 
     {
    	 throw new VPRException (String.format( "Problem initializing cache: %s", e.getMessage() ),e );
         
     }	
}


public int getHashCode(Interaction interaction)
{
	String result=null;
	if (interaction!=null && interaction.getParticipations()!=null)
	{
		for (Participation participation:interaction.getParticipations())
		{
			result+=participation.getParticipantDefinition().getIdentity();
		}
		return result.hashCode();
	}
	return 0;
}

public String getHashCode(SBOLInteractionSummary interactionSummary, URI uri)
{
	String result="";
	if (interactionSummary!=null )
	{
		boolean uriExist=false;
		for (URI participation:interactionSummary.getComponentDefs())
		{
			result+=participation.toString();
			if (uri!=null && uri.equals(participation))
			{
				uriExist=true;
			}
		}
		if (!uriExist)
		{
			result+=uri;
		}
		return result;//.hashCode();
	}
	return null;
}

public void putInCache( SBOLDocument doc, Interaction interaction)  throws VPRException
{
    int key = getHashCode(interaction);
    
    try
    {
    	putInCache(doc, key);    
    }
    catch (VPRException e)
    {
    	throw new VPRException (e.getMessage() + " Interaction:" + interaction.getIdentity(),e);
    }
}

public void putInCache( SBOLDocument doc, Object key)  throws VPRException
{
    
    try 
    {
        cache.put( key, doc );
    }
    catch ( CacheException e ) 
    {
        throw new VPRException( String.format( "Problem putting the document in the cache, for key %s", key), e);
    }
}

public SBOLDocument retrieveFromCache( Object key ) 
{
    return cache.get( key );
}
}
