package com.debatree.exception;

import twitter4j.TwitterException;

public class DebatreeException extends Exception {

	public DebatreeException(Exception ex) {
		super(ex);
	}
	public DebatreeException(String string) {
		super(string);
	}

	
	
}
