package com.debatree.task;

public class DebateTree {

	private TweetTreeNode root;

	public DebateTree(TweetTreeNode root) {
		setRoot(root);
	}

	public TweetTreeNode getRoot() {
		return root;
	}

	public void setRoot(TweetTreeNode root) {
		this.root = root;
	}

}
