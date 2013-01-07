package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.debatree.json.TweetJSONImpl;

@Entity
public class Tweet {
	@GeneratedValue(strategy=GenerationType.AUTO) @Id long id;
	@Column(name="screen_name", unique=true) String screenName;
	
	@Column(name="created_at")  String createdAt;
	@Column(name="favorited")   boolean favorited;
	 
	@Column(name="text")     String text;
	
	  
	@Column(name="retweet_count")   int retweetCount;
	@Column(name="retweeted")   boolean retweeted;
	@Column(name="in_reply_to_user_id")   long inReplyToUserId;
	@Column(name="user_id")  long userId;
	@Column(name="in_reply_to_screen_name")  String inReplyToScreenName;
	@Column(name="in_reply_to_status_id")   long inReplyToStatusId;

	public Tweet(){
		
	}
	public Tweet(TweetJSONImpl tweetJson){
		id = tweetJson.getId();
		try{
			screenName = (tweetJson.getFromUser()!=null)?tweetJson.getFromUser():tweetJson.getUser().getScreenName();
			userId = (tweetJson.getFromUserId()>0)?tweetJson.getFromUserId():tweetJson.getUser().getId();
		}catch(Exception ex){
			
		}
		
		text = tweetJson.getText();
		inReplyToUserId = (tweetJson.getInReplyToUserId()>0)?tweetJson.getInReplyToUserId():tweetJson.getToUserId();
		inReplyToScreenName = (tweetJson.getToUser()!=null)?tweetJson.getToUser():tweetJson.getInReplyToScreenName();
		inReplyToStatusId = tweetJson.getInReplyToStatusId();
		
	}
	//@Transient File profileImage;
	
}
