package org.virtualparts.data;

import java.net.URI;
import java.util.List;

public class QueryParameters {
/*private boolean initializePartConnectome=false;
public boolean isInitializePartConnectome() {
	return initializePartConnectome;
}
public void setInitializePartConnectome(boolean initializePartConnectome) {
	this.initializePartConnectome = initializePartConnectome;
}*/
public List<URI> getCollectionURIs() {
	return collectionURIs;
}
public void setCollectionURIs(List<URI> collectionURIs) {
	this.collectionURIs = collectionURIs;
}
private List<URI> collectionURIs=null;
}
