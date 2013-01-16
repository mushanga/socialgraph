package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Relation {

	@Id long id = -1;
	@Column(name="screen_name", unique=true) String screenName = null;
	

	@Column(name="pictureurl")
	private  String picUrl = null;
	@Column(name="friends_count")  int friendsCount = -1;
	@Column(name="followers_count")  int followersCount = -1;
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
	
	public void retrieveValuesFrom(Relation user){
		if(user.getFriendsCount()>-1){
			this.setFriendsCount(user.getFriendsCount());
			
		}	
		if(user.getFollowersCount()>-1){
			this.setFollowersCount(user.getFollowersCount());
			
		}
		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getPicUrl()!=null){
			this.setPicUrl(user.getPicUrl());
		}

		if(user.getScreenName()!=null){
			this.setScreenName(user.getScreenName());
		}
	
		
	}
	
}
