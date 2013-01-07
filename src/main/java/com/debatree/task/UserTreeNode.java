package com.debatree.task;

import org.neo4j.graphdb.Node;

import com.tcommerce.graph.GraphDatabase;



public class UserTreeNode extends TreeNode {
	private String userName;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void setProperties(Node node) {
		if (this.getId() < 1) {
			this.setId((Long) node.getProperty(GraphDatabase.USER_ID));
			this.setUserName((String) node.getProperty(GraphDatabase.USER_NAME));
		}else{

			node.setProperty(GraphDatabase.USER_ID, this.getId());
			node.setProperty(GraphDatabase.USER_NAME, this.getUserName());
		}
	}
	
		
}
