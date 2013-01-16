package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user_token_status", schema="debatree")
public class UserTokenStatus extends HibernateObject {

	public static String IN_USE = "In Use";
	public static String FREE = "Free";
	public static String INVALID = "Invalid";
	public static String RATE_LIMITED = "Rate Limited";
	
	@Id
	private long id = -1;
	
	@Column(name="status")
	private  String status = FREE;
	
	@Column(name="reset_time_in_secs")
	private  int resetTimeInSecs = 0;
	
	
	
	public UserTokenStatus() {
		super();
	}
	
	public UserTokenStatus(long id, String status, int resetTimeInSecs) {
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

		
		UserTokenStatus user = (UserTokenStatus) obj;

		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getStatus()!=null){
			this.setStatus(user.getStatus());
		}
		if(user.getResetTimeInSecs()>-1){
			this.setResetTimeInSecs(user.getResetTimeInSecs());
			
		}	
		
	
	}

	public int getResetTimeInSecs() {
		return resetTimeInSecs;
	}

	public void setResetTimeInSecs(int resetTimeInSecs) {
		this.resetTimeInSecs = resetTimeInSecs;
	}
}
