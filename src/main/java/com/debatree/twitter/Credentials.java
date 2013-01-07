package com.debatree.twitter;

public class Credentials {

	public Credentials(String sessionId, String pautId) {
		super();
		this.sessionId = sessionId;
		this.pautId = pautId;
	}
	String sessionId;
	String pautId;
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getPautId() {
		return pautId;
	}
	public void setPautId(String pautId) {
		this.pautId = pautId;
	}
	
}
