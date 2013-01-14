package com.debatree.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.neo4j.graphdb.Node;

import com.debatree.data.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJSONImpl extends UserBasicJSONImpl {
	@Expose
	@SerializedName("followers_count")
	private int followersCount = -1;
	
	@Expose
	@SerializedName("friends_count")
	private int friendsCount = -1;
	
	@Expose
	@SerializedName("profile_image_url_https")
	private String picUrl = null;

	public int getFollowersCount() {
		return followersCount;
	}

	public int getFriendsCount() {
		return friendsCount;
	}

	@Override
	public void getDataFromGDBNode(Node node) {
		super.getDataFromGDBNode(node);

		try {

			setFollowersCount(((Integer) node.getProperty("followers_count")));

		} catch (Exception e) {

		}
		try {

			setFriendsCount(((Integer) node.getProperty("friends_count")));

		} catch (Exception e) {

		}

	}

	@Override
	public void setDataToGDBNode(Node node) {

		super.setDataToGDBNode(node);
		try {
			node.setProperty("followers_count", new Integer(getFollowersCount()));
		} catch (Exception e) {
		}
		try {
			node.setProperty("friends_count", new Integer(getFriendsCount()));
		} catch (Exception e) {
		}
	}

	public void getDataFromDBObject(User user) {
		setId(user.getId());
		setScreenName(user.getScreenName());
		setFollowersCount(user.getFollowersCount());
		setFriendsCount(user.getFriendsCount());
		setPicUrl(user.getPicUrl());
	}

	public void setDataToDBObject(User user) {
		user.setId(this.getId());
		user.setScreenName(this.getScreenName());
		user.setFollowersCount(this.getFollowersCount());
		user.setFriendsCount(this.getFriendsCount());
		user.setPicUrl(this.getPicUrl());
	}

	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

}
