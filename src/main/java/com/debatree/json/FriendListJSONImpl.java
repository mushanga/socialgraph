package com.debatree.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class FriendListJSONImpl{
	@SerializedName("next_cursor")
	private String nextCursor;
	@SerializedName("user")
	private UserJSONImpl user;

	
	@SerializedName("friends")
	private List<UserJSONImpl> friends;



	

	public FriendListJSONImpl(String nextCursor, UserJSONImpl user, List<UserJSONImpl> friends) {
		super();
		this.nextCursor = nextCursor;
		this.user = user;
		this.friends = friends;
	}


	public List<UserJSONImpl> getFriends() {
		return friends;
	}


	public void setFriends(List<UserJSONImpl> friends) {
		this.friends = friends;
	}




	public String getNextCursor() {
		return nextCursor;
	}




	public void setNextCursor(String nextCursor) {
		this.nextCursor = nextCursor;
	}


	public UserJSONImpl getUser() {
		return user;
	}


	public void setUser(UserJSONImpl user) {
		this.user = user;
	}



}
