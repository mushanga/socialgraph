package com.debatree.task;

import org.neo4j.graphdb.Node;

import com.tcommerce.graph.GraphDatabase;



public class TweetTreeNode extends TreeNode {
	private String tweetText;
	private String tweetUserName;
	private long tweetUserId;
	
	public String getTweetText() {
		return tweetText;
	}

	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}

	public String getTweetUserName() {
		return tweetUserName;
	}

	public void setTweetUserName(String tweetUserName) {
		this.tweetUserName = tweetUserName;
	}

	public long getTweetUserId() {
		return tweetUserId;
	}

	public void setTweetUserId(long tweetUserId) {
		this.tweetUserId = tweetUserId;
	}

	

	@Override
	public void setProperties(Node node) {

		if (this.getId() < 1) {

			this.setTweetUserId((Long) node.getProperty(GraphDatabase.USER_ID));
			this.setTweetUserName((String) node.getProperty(GraphDatabase.USER_NAME));
			this.setId((Long) node.getProperty(GraphDatabase.TWEET_ID));
			this.setTweetText((String) node.getProperty(GraphDatabase.TEXT));
		}else{
			node.setProperty(GraphDatabase.TWEET_ID,this.getId());
			node.setProperty(GraphDatabase.USER_ID,this.getTweetUserId());
			node.setProperty(GraphDatabase.TEXT,this.getTweetText());
			node.setProperty(GraphDatabase.USER_NAME,this.getTweetUserName());
			
		}
	}
	
}
