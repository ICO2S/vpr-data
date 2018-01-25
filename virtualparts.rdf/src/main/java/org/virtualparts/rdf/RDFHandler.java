package org.virtualparts.rdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class RDFHandler {
	private Model model = null;
	private String baseUri = null;	
	
	public final static String OWL_SAME_AS_URI="http://www.w3.org/2002/07/owl#sameAs";

	public RDFHandler(InputStream stream, URI uri) {
		this(uri);
		if (stream != null) {
			this.model.read(stream, RDFS.getURI());
		}
	}
	
	public RDFHandler(URI uri)
    {      
       this.model = ModelFactory.createDefaultModel();
       setBaseURI(uri.toString());
    }
	

	public RDFHandler(Model model)
    {      
       this.model = model;
    }
	
	private void setBaseURI(String uri)
	{
		this.baseUri=uri;
		if (uri != null && uri.length() > 0) {
			model.setNsPrefix("", uri);
		}
	}
	
	public URI getBaseURI()
	{
		try
		{
			return new URI(this.baseUri);
		}
		catch (Exception exception)
		{
			return null;
		}
	}

	public void addNameSpace(String nameSpacePrefix, URI nameSpace)
	{
			this.model.setNsPrefix(nameSpacePrefix, nameSpace.toString());
		
	}

	public Resource createResource(URI resourceUri) {
		Resource resource = this.model.createResource(resourceUri.toString());
		return resource;
	}
	
	public Resource createResource(URI resourceUri, URI type) {
		Resource resource = this.model.createResource(resourceUri.toString());
		Resource typeResource=model.createResource(type.toString());	
		addProperty(resource, RDF.type, typeResource);
		return resource;
	}
	
	 public static URI createURI(URI baseURI, String itemName)
	    {
	    	try
	    	{
	    		return new URI (baseURI.toString() + "#" + itemName);
	    	}
	    	catch (URISyntaxException exception)
	    	{
	    		return null;
	    	}
	    }

	public void addProperty(Resource resource, URI propertyURI,
			String propertyValue) {
		Property property = this.model.createProperty(propertyURI.toString());
		addProperty(resource, property, propertyValue);
	}

	public void addProperty(Resource resource, URI propertyURI,
			Resource propertyValue) {
		Property property = this.model.createProperty(propertyURI.toString());
		addProperty(resource, property, propertyValue);
	}

	public void addProperty(Resource resource, Property property,
			Resource propertyValue) {		
		resource.addProperty(property, propertyValue);
	}
	
	public void addProperty(Resource resource, URI property,
			URI value) {
		Resource resourceValue = this.model.createResource(value.toString());
		addProperty(resource, property, resourceValue);
	}

	public void addProperty(Resource resource, Property property,
			String propertyValue) {
		resource.addProperty(property, propertyValue);
	}
	
	public void addComment(Resource resource, String value) {
		 Literal literal = this.model.createLiteral(value);
         resource.addProperty(RDFS.comment, literal);		
	}
	
	public void addType(Resource resource, URI type) {
		Resource typeResource=model.createResource(type.toString());		
        resource.addProperty(RDF.type, typeResource);		
	}
	
	   public void addLabel(Resource resource, String value) throws Exception
	   {
	    
	      Literal literal = this.model.createLiteral(value);
	      resource.addProperty(RDFS.label, literal);
	    
	   }
	
	   public void addSameIndividualRelationship(Resource resource, Resource sameAsResource)
	   {
		   try
		   {
			   addProperty(resource, new URI(OWL_SAME_AS_URI), sameAsResource);
		   }
		   catch (URISyntaxException ignored){			   
		   }
	   }
	   
	
	
	public void setPropertyValue(Resource resource, URI propertyURI,String value)
	{
		Property property=this.model.getProperty(propertyURI.toString());   
		Literal literal=this.model.createLiteral(value);
		Statement stmt=resource.getProperty(property);
		stmt.changeObject(literal);
						   
	}
	
	public static String toLiteralString(RDFNode node)
	{
		if (node.isLiteral())
		{
			return node.asLiteral().getValue().toString();
		}
		else
		{
			return node.asResource().getURI();
		}
	}			         

	public String getRdfString(String format, Resource[] topLevelResources)
			throws Exception {
		String rdfData = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (format == null || format.length() == 0) {
			format = getDefaultFormat();
		}
		try {
			RDFWriter writer = this.model.getWriter(format);
			// fasterWriter.setProperty("allowBadURIs","true");
			// fasterWriter.setProperty("relativeURIs","");
			writer.setProperty("tab", "3");
			if (topLevelResources != null && topLevelResources.length > 0) {
				writer.setProperty("prettyTypes", topLevelResources);
			}
			writer.write(this.model, stream, this.baseUri);
			rdfData = new String(stream.toString());
		} finally {
			if (stream != null) {
				stream.close();
				stream = null;
			}
		}
		return rdfData;
	}

	public static void save(Model rdfModel, String filePath) throws IOException, FileNotFoundException {
		save(rdfModel, filePath,getDefaultFormat());
	}	
	  
	public static void save(Model rdfModel, String filePath, String format) throws IOException, FileNotFoundException {
		if (format == null || format.length() == 0) {
			format = getDefaultFormat();
		}
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(new File(filePath));
			rdfModel.write(stream, format);
		} 
		finally {
			if (stream != null) {
				stream.close();
				stream = null;
			}
		}
	}

	
	public static String getDefaultFormat() {
		return "RDF/XML-ABBREV";
	}
	
	public Model getModel()
    {
        return this.model;
    }
	
	 public void addModel(Model model)
	    {
	        getModel().add(model);
	    }
	 
	 public  static Model executeSPARQLConstructQuery(Model model,String query, Syntax syntax) {

	       System.out.println ("Executing the query" + query + "...");
	       Query q = QueryFactory.read(query,syntax);

	        QueryExecution qe = QueryExecutionFactory.create(q, model);

	        return qe.execConstruct();
	    }

	    public Model executeSPARQLConstructQuery(String query) {

	       return RDFHandler.executeSPARQLConstructQuery(getModel(),query,Syntax.syntaxARQ);
	    }
	    public Model executeSPARQLConstructQuery(String query,Syntax syntax) {

		       return RDFHandler.executeSPARQLConstructQuery(getModel(),query,syntax);
		    }
	    
	    public  ResultSet executeSPARQLSelectQuery(String query, Syntax syntax) {

		      return RDFHandler.executeSPARQLSelectQuery(getModel(),query,syntax);
		   }
	    
	    
	  

	    
	    public static ResultSet executeSPARQLSelectQuery(Model model, String query, Syntax syntax)
	    {

		       System.out.println ("Executing the query 2" + query + "...");
		       Query q = QueryFactory.read(query,syntax);
		        QueryExecution qe = QueryExecutionFactory.create(q, model);

		        ResultSet rs = qe.execSelect();

		        // Print the query for better understanding
		        //System.out.println(q.toString(Syntax.syntaxSPARQL));
		        //ResultSetFormatter.out(rs); //TODO Open

		        // Print the query for better understanding
		       // System.out.println("Row count:" + rs.getRowNumber());

		        return rs;		          
	    }
	    
	    /**
	     * The first column of the given ResultSet object is used to create a Set.
	     * @param rs ResultSet containing a SPARQL query result.
	     * @return A set of values corresponding to the first column. Only the resources are returned, literal values are discarded.
	     */
	    public static Set<String> resultsToSet(ResultSet rs)
	    {
	        HashSet<String> results=new HashSet<String>();
	 
	        String resultColumnName=rs.getResultVars().get(0);
	        while (rs.hasNext())
	        {
	            QuerySolution solution=rs.next();
	            RDFNode node=solution.get(resultColumnName);
	            if (node.isResource())
	            {
	                String URI=node.asResource().getURI();
	                if (!URI.endsWith("Nothing"))
	                {
	                    results.add(URI);
	                }
	            }
	        }
	        return results;
	     }
	    
	    /**
	     * The first two column of the given ResultSet object is used to create a Map.
	     * @param rs ResultSet containing a SPARQL query result.
	     * @return A map of key/value pairs corresponding to the first and second columns respectively. Rows that are literals in the first column are discarded.
	     */
	    public static Map<String,String> resultsToMap(ResultSet rs)
	    {
	        HashMap<String,String> results=new HashMap<String,String>();
	        int count=0;

	        String name=rs.getResultVars().get(0);
	        String valueColumnName=rs.getResultVars().get(1);
	        while (rs.hasNext())
	        {
	            QuerySolution solution=rs.next();
	            RDFNode node=solution.get(name);
	            if (node.isResource())
	            {
	                String URI=node.asResource().getURI();
	                if (!URI.endsWith("Nothing"))
	                {
	                    RDFNode valueNode=solution.get(valueColumnName);
	                    String value="";
	                    if (valueNode.isResource())
	                    {
	                        value=valueNode.asResource().getURI();
	                    }
	                    else
	                    {
	                         value=valueNode.asLiteral().toString();
	                    }
	                    results.put(URI,value);
	                    count++;
	                }
	            }
	        }
	        return results;
	     }
	   
	    
	    /**
	     * The first two column of the given ResultSet object is used to create a Map.
	     * @param rs ResultSet containing a SPARQL query result.
	     * @return A map of key/value pairs corresponding to the first and second columns respectively. Rows that are literals in the first column are discarded.
	     */
	    public static MultivaluedMap<String,String> resultsToMultiValuedMap(ResultSet rs)
	    {
	    	MultivaluedMap<String,String> results=new MultivaluedHashMap<>();
	    	int count=0;

	        String name=rs.getResultVars().get(0);
	        String valueColumnName=rs.getResultVars().get(1);
	        while (rs.hasNext())
	        {
	            QuerySolution solution=rs.next();
	            RDFNode node=solution.get(name);
	            if (node.isResource())
	            {
	                String URI=node.asResource().getURI();
	                if (!URI.endsWith("Nothing"))
	                {
	                    RDFNode valueNode=solution.get(valueColumnName);
	                    String value="";
	                    if (valueNode.isResource())
	                    {
	                        value=valueNode.asResource().getURI();
	                    }
	                    else
	                    {
	                         value=valueNode.asLiteral().toString();
	                    }
	                    results.add(URI,value);
	                    count++;
	                }
	            }
	        }
	        return results;
	     }
	   
	    
	  
	    /**
	     * Gets the  property values for a given property and a resource.
	     * @param model Model to search the property for
	     * @param resource Resource to search the property values for 
	     * @param propertyURI the URI of the property
	     * @return List of String objects with the corresponding values
	     */
	    public static List<String> getPropertyValues(Model rdfModel, Resource resource, URI propertyURI) 
	    {
	        ArrayList<String> values=new ArrayList<String>();
	        Property property=rdfModel.getProperty(propertyURI.toString());
	        for (StmtIterator iterator=resource.listProperties(property);iterator.hasNext();)
	        {
	        	Statement stmt=iterator.next();
	        	RDFNode object=stmt.getObject();
	        	String value="";
	        	if (object.isResource())
                 {
                     value=object.asResource().getURI();
                 }
                 else
                 {
                      value=object.asLiteral().toString();
                 }	        	 	        	
	        	values.add(value);
	        }
	        return values;
	    }
	    
	   
	    
	    /**
	     * Gets the  property values for a given property and a resource.
	     * @param model Model to search the property for
	     * @param resource Resource to search the property values for 
	     * @param propertyURI the URI of the property
	     * @return List of Resource objects with the corresponding values
	     */
	    public static List<Resource> getPropertyValueAsResource(Model rdfModel, Resource resource, URI propertyURI) 
	    {
	        ArrayList<Resource> values=new ArrayList<Resource>();
	        Property property=rdfModel.getProperty(propertyURI.toString());
	        for (StmtIterator iterator=resource.listProperties(property);iterator.hasNext();)
	        {
	        	Statement stmt=iterator.next();
	        	Resource value=stmt.getObject().asResource();
	        	values.add(value);
	        }
	        return values;
	    }
	    
	    /**
	     * Gets the  property values for a given property and a resource.
	     * @param model Model to search the property for
	     * @param resource Resource to search the property values for 
	     * @param propertyURI the URI of the property
	     * @return List of String objects with the corresponding values
	     */
	    /**
	     * Gets the  property values for a given property and a resource.
	     * @param rdfModel
	     * @param resource
	     * @param propertyURI
	     * @return
	     */
	    public static String getPropertySingleValue(Model rdfModel, Resource resource, URI propertyURI) 
	    {
	    	String value=null;
	    	ArrayList<String> values=new ArrayList<String>();
	        Property property=rdfModel.getProperty(propertyURI.toString());
	        Statement propertyStatement=resource.getProperty(property);
	        if (propertyStatement!=null)
	        {
	        	value=propertyStatement.getObject().asLiteral().getValue().toString();	     
	        }
	        return value;
	    }
	    
		public static boolean hasType(Model rdfModel,Resource resource, URI type)
		{
			boolean result=false;
			Resource typeResource=rdfModel.createResource(type.toString());	
			result=resource.hasProperty (RDF.type, typeResource);
		    return result;
		}
		
		public static List<String> getTypes(Model rdfModel,Resource resource)
		{
			return getPropertyValues(rdfModel,resource, URI.create(RDF.type.getURI()));
		}
		
		public static Resource getPropertyResourceValue(Model rdfModel,Resource resource, URI propertyURI)
		{
			Property property=rdfModel.getProperty(propertyURI.toString());
			Resource value=resource.getPropertyResourceValue(property);			
			return value;
		}
		
		public static List<Resource> getSubjects(Model rdfModel,Resource resource, URI propertyURI)
		{
			List<Resource> resources=new ArrayList<Resource>();
			Property property=rdfModel.getProperty(propertyURI.toString());
			StmtIterator iterator=rdfModel.listStatements(null, property, resource);
			while(iterator.hasNext())
			{
				Statement stmt=iterator.next();
				resources.add(stmt.getSubject());
			}				
			return resources;
		}
	    
		  public static List<Resource> getResourcesOfType(Model rdfModel,URI type) throws Exception
		   {
		    	Resource typeResource=rdfModel.createResource(type.toString());
		        ArrayList<Resource> resources=new ArrayList<Resource>();        
		        for (ResIterator iterator =  rdfModel.listResourcesWithProperty(RDF.type, typeResource);iterator.hasNext();)
		        {
		           Resource resource=iterator.next();
		          resources.add(resource);
		        }
		        return resources;
		   }
		  
		  public static RDFNode getPropertyValue(Model rdfModel,Resource resource, URI propertyURI) {
				Property property=rdfModel.getProperty(propertyURI.toString());   
				Statement stmt = resource.getProperty(property);
			    return stmt.getObject();
			}
		  
		  public static String getPropertyValueAsString(Model rdfModel, Resource resource, URI propertyURI) {
				Property property=rdfModel.getProperty(propertyURI.toString());   
				Statement stmt = resource.getProperty(property);
			    if (stmt!=null && stmt.getObject()!=null)
			    {
			    	return toLiteralString(stmt.getObject());
			    }
			    else
			    {
			    	return null;
			    }	
			}
		  
		  public static Resource getResource(Model rdfModel,URI resourceURI) {
				return rdfModel.getResource(resourceURI.toString());			
		  }
			
		  
		  
		/*
	    public static boolean hasType(Resource individual, URI type)
		{
			boolean result=false;
			StmtIterator iterator= individual.listProperties(RDF.type);
			while (iterator.hasNext())
			{
				Statement stmt=iterator.next();
				if (stmt.getObject().isResource())
				{
					String uri=stmt.getObject().asResource().getURI();
					if (uri.equals(type.toString()))
					{
						result=true;
					}				
				}
				
			}
			return result;
		}*/

}
