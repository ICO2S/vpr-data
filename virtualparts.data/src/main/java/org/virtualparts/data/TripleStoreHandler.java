package org.virtualparts.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.virtualparts.VPRException;

public class TripleStoreHandler {
	private String endPointURL;

	public TripleStoreHandler(String endPointURL) {
		this.endPointURL = endPointURL;
	}
	    
	public ResultSet executeSparql(String sparqlQuery)  throws VPRException{
		try
		{
			//Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxSPARQL_11);

			QueryEngineHTTP httpQuery = new QueryEngineHTTP (this.endPointURL, sparqlQuery);
			ResultSet results = httpQuery.execSelect();			
		/*
		 * while (results.hasNext()) { QuerySolution solution = results.next();
		 * // get the value of the variables in the select clause String
		 * expressionValue = solution.get("Component").toString();
		 * System.out.println(expressionValue); }
		 */
		return results;
		}
		catch (Exception e)
		{
			throw new VPRException(e.getMessage(),e);
		}
	}
	
	public String executeSparqlWithJson(String sparqlQuery)  throws VPRException{
		try
		{			
			QueryEngineHTTP httpQuery = new QueryEngineHTTP (this.endPointURL, sparqlQuery);
			ResultSet results = httpQuery.execSelect();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(outputStream, results);
			String json = new String(outputStream.toByteArray());		
			return json;
		}
		catch (Exception e)
		{
			throw new VPRException(e.getMessage(),e);
		}
	}
	
	public String executeConstructSparql(String sparqlQuery) throws VPRException {
		try
		{
			Model model=executeConstructSparqlAsModel(sparqlQuery);
			return getRdfString(model, null, null);
		}
		catch (Exception e)
		{
			throw new VPRException("Could not execute the CONTSRUCT query. " + e.getMessage(),e);
		}		
	}
	
	public Model executeConstructSparqlAsModel(String sparqlQuery) throws VPRException {
		Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxSPARQL_11);
		// we want to bind the ?uniprotAccession variable in the query
		// to the URI for Q16850 which is http://purl.uniprot.org/uniprot/Q16850
		// QuerySolutionMap querySolutionMap = new QuerySolutionMap();
		// querySolutionMap.add("uniprotAccession", new
		// ResourceImpl("http://purl.uniprot.org/uniprot/Q16850"));
		// ParameterizedSparqlString parameterizedSparqlString = new
		// ParameterizedSparqlString(query.toString(), querySolutionMap);

		QueryEngineHTTP httpQuery = new QueryEngineHTTP (this.endPointURL, query);
		Model model= httpQuery.execConstruct();
		/*
		 * while (results.hasNext()) { QuerySolution solution = results.next();
		 * // get the value of the variables in the select clause String
		 * expressionValue = solution.get("Component").toString();
		 * System.out.println(expressionValue); }
		 */
		return model;
	}
	
	public String getRdfString(Model model, String format, Resource[] topLevelResources)
			throws VPRException {
		String rdfData = null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (format == null || format.length() == 0) {
			format = getDefaultFormat();
		}
		try {
			RDFWriter writer = model.getWriter(format);
			// fasterWriter.setProperty("allowBadURIs","true");
			// fasterWriter.setProperty("relativeURIs","");
			writer.setProperty("tab", "3");
			if (topLevelResources != null && topLevelResources.length > 0) {
				writer.setProperty("prettyTypes", topLevelResources);
			}
			writer.write(model, stream, null);
			rdfData = new String(stream.toString());
		} finally {
			if (stream != null) {
				try
				{
					stream.close();
				}
				catch(Exception e){}
				stream = null;
			}
		}
		return rdfData;
	}
	
	public static String getDefaultFormat() {
		//return "RDF/XML-ABBREV";
		return "RDF/XML";
		
	}
	
	public String getSparqlQuery(String fileName) throws VPRException
    {
	 
    	/*ClassLoader classLoader = this.getClass().getClassLoader();
    	File file=new File (classLoader.getResource(fileName).getFile());
    	String sparql=null;
    	try
    	{
    		sparql=FileUtils.readFileToString(file);
    	}
    	catch (IOException ex)
    	{
    		throw new WebApplicationException(ex.getMessage(),ex);
    	
    	}*/
		try
		{
			InputStream stream= new TripleStoreHandler("").getClass().getClassLoader().getResourceAsStream(fileName);
	    	StringWriter writer = new StringWriter();
	    	IOUtils.copy(stream, writer);
	    	String sparql = writer.toString();
	    	return sparql;
		}
    	catch (Exception e)
    	{
    		throw new VPRException("Could not find the resource " + fileName + "." + e.getMessage(), e);
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
	
}
