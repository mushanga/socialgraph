package com.debatree.json;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class FriendListJSONImpl extends UserBasicJSONImpl{
	@SerializedName("next_cursor")
	private long nextCursor;

	
	@SerializedName("friends")
	private List<UserJSONImpl> friends;


	public FriendListJSONImpl(List<UserJSONImpl> friends, long next_cursor) {
		super();
		this.setNextCursor(next_cursor);
		this.friends = friends;
	}


	

	public List<UserJSONImpl> getFriends() {
		return friends;
	}


	public void setFriends(List<UserJSONImpl> friends) {
		this.friends = friends;
	}




	public long getNextCursor() {
		return nextCursor;
	}




	public void setNextCursor(long nextCursor) {
		this.nextCursor = nextCursor;
	}



}
