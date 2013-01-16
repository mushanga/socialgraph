package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TwitstreetUser extends HibernateObject{

	@Id long id = -1;
	@Column(name="userName", unique=true) String screenName = null;
	
	@Column(name="oauthToken")
	private String oauthToken = null;
	
	@Column(name="oauthTokenSecret")
	private String oauthTokenSecret = null;
	
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
	
	@Override
	public void copyPropertiesFrom(Object obj){
		
		TwitstreetUser user = (TwitstreetUser) obj;
		
		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getOauthToken()!=null){
			this.setOauthToken(user.getOauthToken());
		}

		if(user.getOauthTokenSecret()!=null){
			this.setOauthTokenSecret(user.getOauthTokenSecret());
		}
	
		
	}
	public String getOauthToken() {
		return oauthToken;
	}
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}
	public void setOauthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}
	
}
