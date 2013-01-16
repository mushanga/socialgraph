package com.debatree.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="user_graph_status", schema="debatree")
public class UserGraphStatus extends HibernateObject {

	public static String IN_PROGRESS = "In Progress";
	public static String WAITING = "Waiting...";
	public static String COMPLETED = "Completed";
	public static String PROTECTED = "Protected";
	
	@Id
	private long id = -1;
	
	public UserGraphStatus() {
		super();
	}
	
	public UserGraphStatus(long id, String status) {
		super();
		this.id = id;
		this.status = status;
	}


	@Column(name="status")
	private  String status = WAITING;

	@Column(name="content", length=1000000)
	private String content;
	

	@Column(name="screenName")
	private  String screenName;
	
	
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

		
		UserGraphStatus user = (UserGraphStatus) obj;
	
		if(user.getId()>-1){
			this.setId(user.getId());
			
		}	
		if(user.getStatus()!=null){
			this.setStatus(user.getStatus());
		}
		if(user.getScreenName()!=null){
			this.setScreenName(user.getScreenName());
		}

		if(user.getContent()!=null){
			this.setContent(user.getContent());
		}
		
	
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isCompleted() {
		return this.status.equals(COMPLETED);
	}
}
