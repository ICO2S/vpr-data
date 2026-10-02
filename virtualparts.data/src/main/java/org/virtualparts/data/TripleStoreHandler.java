package org.virtualparts.data;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFactory;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.ResultSetRewindable;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.ResultSetMgr;
import org.apache.jena.riot.resultset.ResultSetLang;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.virtualparts.VPRException;

public class TripleStoreHandler {
	private String endPointURL;
	private String token;

	public TripleStoreHandler(String endPointURL,String token) {
		this.endPointURL = endPointURL;
		this.token = token;
	}
	    
	public ResultSet executeSparql(String sparqlQuery, String token)
	        throws VPRException {

	    CloseableHttpClient client = null;
	    CloseableHttpResponse response = null;

	    try {
	        String url = this.endPointURL
	                + "?query="
	                + URLEncoder.encode(sparqlQuery, "UTF-8");

	        HttpGet request = new HttpGet(url);

	        if (token != null && !token.isEmpty()) {
	            request.setHeader("X-authorization", token);
	        }

	        client = HttpClients.createDefault();
	        response = client.execute(request);

	        int status = response.getStatusLine().getStatusCode();

	        if (status < 200 || status >= 300) {
	            throw new VPRException(
	                "SPARQL query failed: " + response.getStatusLine()
	            );
	        }

	        ResultSet results = ResultSetFactory.fromXML(
	            response.getEntity().getContent()
	        );

	        // Copy everything into memory before the HTTP stream is closed.
	        ResultSetRewindable copiedResults =
	            ResultSetFactory.copyResults(results);

	        return copiedResults;

	    } catch (VPRException e) {
	        throw e;

	    } catch (Exception e) {
	        throw new VPRException(e.getMessage(), e);

	    } finally {
	        try {
	            if (response != null) {
	                response.close();
	            }
	        } catch (Exception e) {
	            // ignore
	        }

	        try {
	            if (client != null) {
	                client.close();
	            }
	        } catch (Exception e) {
	            // ignore
	        }
	    }
	}
	
	public String executeSparqlWithJson(
	        String sparqlQuery,
	        String token) throws VPRException {

	    try {
	        ResultSet results = executeSparql(sparqlQuery, token);

	        ByteArrayOutputStream outputStream =
	            new ByteArrayOutputStream();

	        ResultSetFormatter.outputAsJSON(
	            outputStream,
	            results
	        );

	        return new String(
	            outputStream.toByteArray(),
	            "UTF-8"
	        );

	    } catch (Exception e) {
	        throw new VPRException(e.getMessage(), e);
	    }
	}
	
	public String executeConstructSparql(String sparqlQuery, String token) throws VPRException {
		try
		{
			Model model=executeConstructSparqlAsModel(sparqlQuery, token);
			return getRdfString(model, null, null);
		}
		catch (Exception e)
		{
			throw new VPRException("Could not execute the CONTSRUCT query. " + e.getMessage(),e);
		}		
	}
	
	public Model executeConstructSparqlAsModel(
	        String sparqlQuery,
	        String token) throws VPRException {

	    try {
	        String url = this.endPointURL
	                + "?query="
	                + URLEncoder.encode(sparqlQuery, "UTF-8");

	        HttpGet request = new HttpGet(url);

	        request.setHeader(
	                "X-authorization",
	                token
	        );

	        // Ask the endpoint for RDF/XML
	        request.setHeader(
	                "Accept",
	                "application/rdf+xml"
	        );

	        try (CloseableHttpClient client = HttpClients.createDefault();
	             CloseableHttpResponse response = client.execute(request)) {

	            int status = response.getStatusLine().getStatusCode();

	            if (status < 200 || status >= 300) {
	                throw new VPRException(
	                    "SPARQL query failed: "
	                    + response.getStatusLine()
	                );
	            }

	            Model model = ModelFactory.createDefaultModel();

	            try (InputStream in =
	                    response.getEntity().getContent()) {

	                model.read(in, null);
	            }

	            return model;
	        }

	    } catch (VPRException e) {
	        throw e;
	    } catch (Exception e) {
	        throw new VPRException(e.getMessage());
	    }
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
			InputStream stream= new TripleStoreHandler("","").getClass().getClassLoader().getResourceAsStream(fileName);
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
