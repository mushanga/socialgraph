package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="twitstreet_users", schema="debatree")
public class TwitstreetUserTokenStatus extends HibernateObject {

	public static String IN_USE = "In Use";
	public static String FREE = "Free";
	public static String INVALID = "Invalid";
	public static String RATE_LIMITED = "Rate Limited";
	
	@Id
	private long id = -1;

	@Column(name="status")
	private  String status = FREE;

	@Column(name="userName")
	private  String screenName;
	
	@Column(name="resetTimeInSecs")
	private  long resetTimeInSecs = 0;

	@Column(name="oAuthToken")
	private  String oAuthToken;
	
	@Column(name="oAuthTokenSecret")
	private  String oAuthTokenSecret;
	
	
	
	public TwitstreetUserTokenStatus() {
		super();
	}
	
	public TwitstreetUserTokenStatus(long id, String status, int resetTimeInSecs) {
		super();
		this.id = id;
		this.status = status;
		this.resetTimeInSecs = resetTimeInSecs;
	}

	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	@Override
	public void copyPropertiesFrom(Object obj) {

		
		TwitstreetUserTokenStatus user = (TwitstreetUserTokenStatus) obj;

		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getStatus()!=null){
			this.setStatus(user.getStatus());
		}
		if(user.getScreenName()!=null){
			this.setScreenName(user.getScreenName());
		}
		if(user.getoAuthToken()!=null){
			this.setoAuthToken(user.getoAuthToken());
		}
		if(user.getoAuthTokenSecret()!=null){
			this.setoAuthTokenSecret(user.getoAuthTokenSecret());
		}
		if(user.getResetTimeInSecs()>-1){
			this.setResetTimeInSecs(user.getResetTimeInSecs());
			
		}	
		
	
	}

	public long getResetTimeInSecs() {
		return resetTimeInSecs;
	}

	public void setResetTimeInSecs(long resetTimeInSecs) {
		this.resetTimeInSecs = resetTimeInSecs;
	}

	public String getoAuthTokenSecret() {
		return oAuthTokenSecret;
	}

	public void setoAuthTokenSecret(String oAuthTokenSecret) {
		this.oAuthTokenSecret = oAuthTokenSecret;
	}

	public String getoAuthToken() {
		return oAuthToken;
	}

	public void setoAuthToken(String oAuthToken) {
		this.oAuthToken = oAuthToken;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
}
