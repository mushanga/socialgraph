package com.debatree.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.neo4j.graphdb.Node;

import com.google.gson.annotations.SerializedName;
import com.tcommerce.graph.GraphDataObjectIF;


@JsonIgnoreProperties(ignoreUnknown=true)
public class UserJSONImpl extends UserBasicJSONImpl{
	@SerializedName("followers_count")
    @JsonProperty("followers_count")
	private int followersCount;

	@SerializedName("friends_count")
    @JsonProperty("friends_count")
	private int friendsCount;
	public int getFollowersCount() {
		return followersCount;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	@Override
	public void getDataFromGDBNode(Node node) {
		super.getDataFromGDBNode(node);
		setFollowersCount(((Integer) node.getProperty("followers_count")));
		setFriendsCount(((Integer) node.getProperty("friends_count")));		
	}
	@Override
	public void setDataToGDBNode(Node node) {

		super.setDataToGDBNode(node);	
		node.setProperty("followers_count",new Integer(getFollowersCount()));		
		node.setProperty("friends_count",new Integer(getFriendsCount()));		
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	



}
