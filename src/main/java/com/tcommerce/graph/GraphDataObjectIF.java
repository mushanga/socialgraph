package com.tcommerce.graph;

import org.neo4j.graphdb.Node;


public interface GraphDataObjectIF {

	public void getDataFromGDBNode(Node node);
	public void setDataToGDBNode(Node node);
}
