package com.debatree.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class FriendListJSONImpl{
	@SerializedName("next_cursor")
	private String nextCursor;

	
	@SerializedName("friends")
	private List<UserJSONImpl> friends;


	public FriendListJSONImpl(List<UserJSONImpl> friends, String nextCursor) {
		super();
		this.setNextCursor(nextCursor);
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



}
