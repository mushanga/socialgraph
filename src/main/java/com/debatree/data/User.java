package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User {

	@Id long id;
	@Column(name="screen_name", unique=true) String screenName;
	

	@Column(name="pictureurl")
	private  String picUrl;
	@Column(name="friends_count")  int friendsCount;
	@Column(name="followers_count")  int followersCount;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	
	public void retrieveValuesFrom(User user){
		this.setFollowersCount(user.getFollowersCount());
		this.setFriendsCount(user.getFriendsCount());
		this.setId(user.getId());
		this.setPicUrl(user.getPicUrl());
		this.setScreenName(user.getScreenName());
	}
	
}
