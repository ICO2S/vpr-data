package uk.ac.ncl.ico2s;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtils {

	 public static String getSVPDesign(String base, String design)
	    {
	    	String[] parts=design.split(";");
	    	StringBuilder builder=new StringBuilder();
	    	for (String part:parts)
	    	{
	    		String[] details=part.split(":");
	    		if (builder.length()>0)
	    		{
	    			builder.append(";");
	    		}
	    		builder.append(String.format("<%s%s>:%s", base, details[0],details[1])) ;
	    	}
	    	return builder.toString();		
	    }
	 

	    public static String getOutputDir()
	    {
	    	Path currentRelativePath = Paths.get("");
		    String homeDirectory = currentRelativePath.toAbsolutePath().getParent().toString();
		    
	    	return homeDirectory + File.separator  + "data" + File.separator + "sbol2_interactions" + File.separator;
	    		
	    }
}
