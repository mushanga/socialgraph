package com.debatree.json;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TweetJSONImpl {
	private long id;
	private String text;
	private UserBasicJSONImpl user;
	
    @JsonProperty("created_at")
	private String createdAtStr;
	
    @JsonProperty("to_user")
	private String toUser;
    
    @JsonProperty("to_user_id")
	private long toUserId;
    
	@JsonProperty("from_user")
	private String fromUser;
    
    @JsonProperty("from_user_id")
	private long fromUserId;

    @JsonProperty("in_reply_to_user_id")
	private long inReplyToUserId;
    @JsonProperty("in_reply_to_screen_name")
	private String inReplyToScreenName;
    
    @JsonProperty("in_reply_to_status_id")
	private long inReplyToStatusId;
    
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}
	
    public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public UserBasicJSONImpl getUser() {
		return user;
	}

	public void setUser(UserBasicJSONImpl user) {
		this.user = user;
	}

	public long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}

	public long getInReplyToUserId() {
		return inReplyToUserId;
	}

	public void setInReplyToUserId(long inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	public String getCreatedAtStr() {
		return createdAtStr;
	}

	public void setCreatedAtStr(String createdAtStr) {
		this.createdAtStr = createdAtStr;
	}

	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

}
