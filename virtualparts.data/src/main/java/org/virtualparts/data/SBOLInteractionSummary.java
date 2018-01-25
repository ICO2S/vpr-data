package org.virtualparts.data;

import java.net.URI;
import java.util.List;

public class SBOLInteractionSummary {
	public SBOLInteractionSummary(URI uri, List<URI> types, List<URI> componentDefs) {
		super();
		this.uri = uri;
		this.types = types;
		this.componentDefinitions = componentDefs;
	}
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	public List<URI> getTypes() {
		return types;
	}
	public void setTypes(List<URI> types) {
		this.types = types;
	}
	public List<URI> getComponentDefs() {
		return componentDefinitions;
	}
	public void setComponentDefs(List<URI> componentDefs) {
		this.componentDefinitions = componentDefs;
	}
	private URI uri;	
	private List<URI> types;
	private List<URI> componentDefinitions;		
	public List<String> getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	public List<String> getNames() {
		return names;
	}
	public void setNames(List<String> names) {
		this.names = names;
	}
	private List<String> descriptions;
	private List<String> names;
	
	
}
