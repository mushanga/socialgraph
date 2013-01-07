package com.debatree.task;

import java.util.ArrayList;

import org.neo4j.graphdb.Node;

import com.google.gson.annotations.Expose;


public abstract class TreeNode  {
	protected TreeNode parent;
	protected int depth = 0;
	
	@Expose
	private long id = 0;

	@Expose
	String name;
	@Expose
	private int size = (int) (Math.random()*5000);
	@Expose
	private ArrayList<TreeNode> children = new ArrayList<TreeNode>();
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}

	public int getDepth() {
		return depth;
	}
	
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public void addChild(TreeNode dtn){
		getChildren().add(dtn);
	}
	
	public void removeChild(TreeNode dtn){
		getChildren().remove(dtn);		
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}


	public TreeNode getParent() {
		return parent;
	}

	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	
	public TreeNode getRoot(){
		if(this.getParent()==null){
			return this;
		}else{
			return this.getParent().getRoot();
		}
	}

	public abstract void setProperties(Node node);

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
