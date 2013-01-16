package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user_friend_list_state", schema="debatree")
public class UserFriendListStatus extends HibernateObject {

	public static String IN_PROGRESS = "In Progress";
	public static String WAITING = "Waiting...";
	public static String COMPLETED = "Completed";
	public static String PROTECTED = "Protected";
	
	@Id
	private long id = -1;
	
	public UserFriendListStatus() {
		super();
	}
	
	public UserFriendListStatus(long id, String status) {
		super();
		this.id = id;
		this.status = status;
	}


	@Column(name="status")
	private  String status = WAITING;
	
	
	public boolean isWaiting(){
		return this.getStatus().equals(WAITING);				
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

		
		UserFriendListStatus user = (UserFriendListStatus) obj;
	
		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getStatus()!=null){
			this.setStatus(user.getStatus());
		}
		
	
	}
}
