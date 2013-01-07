package com.amazonbird.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


import twitter4j.TwitterException;

public class ExceptionUtil {

	public static int INVALID_REQUEST = 400;

	public static int UNABLE_TO_FOLLOW = 1000;
	public static int LONG_TWEET = 1001;
	public static int SHARING_NOT_PERMISSABLE = 1002;
	public static int AREADY_FAVORITED = 1003;
	public static int STATUS_DUPLICATE = 1004;
	

	public static int PROXY_DOWN = 2000;

	public static int TIME_OUT = 2001;
	public static int JSON_ERROR = 2002;
	
	public static int UNAUTHORIZED = 401;
	public static int NOT_FOUND = 404;
	public static int CACHE_ACCESS_DENIED = 407;
	public static int UNDERSTOOD_BUT_ERROR = 403;
	public static int RATE_LIMIT_EXCEEDED = 420;
	public static int TEMP_UNAVAILABLE = 307;
	public static int TWITTER_DOWN = 502;
	public static int TWITTER_SERVERS_OVERLOADED = 503;
	public static int NOT_IMPLEMENTED = 501;
	public static int GATEWAY_TIMEOUT = 504;

	private static Logger logger = Logger.getLogger(ExceptionUtil.class);
	private static ExceptionUtil instance = new ExceptionUtil();

	private HashMap<String, Integer> identifierMap = new HashMap<String, Integer>();

	private ExceptionUtil() {
		identifierMap.put("You are unable to follow more people at this time", UNABLE_TO_FOLLOW);
		identifierMap.put("Could not authenticate with OAuth", UNAUTHORIZED);
		identifierMap.put("Invalid / expired Token", UNAUTHORIZED);
		identifierMap.put("The text of your tweet is too long", LONG_TWEET);
		identifierMap.put("sharing is not permissable for this status", SHARING_NOT_PERMISSABLE);
		identifierMap.put("You have already favorited this status",AREADY_FAVORITED);
		identifierMap.put("Status is a duplicate",STATUS_DUPLICATE);
		identifierMap.put("Twitter is down or being upgraded",TWITTER_DOWN);
		identifierMap.put("The URI requested is invalid or the resource requested, such as a user, does not exist.",PROXY_DOWN);
		identifierMap.put("connect timed out",TIME_OUT);
		identifierMap.put("A JSONObject text must begin with",JSON_ERROR);
		identifierMap.put("Read timed out",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("Connection reset",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("Connection refused",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("Unexpected end of file from server",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("Not in GZIP format",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("EOF Exception",UNDERSTOOD_BUT_ERROR);
		identifierMap.put("Connection to",UNDERSTOOD_BUT_ERROR);

		identifierMap.put("peer not authenticated",UNDERSTOOD_BUT_ERROR);
		
		
		//identifierMap.put("User has been suspended",UNAUTHORIZED);
		identifierMap.put("Your account may not be allowed to perform this action.",UNAUTHORIZED);
		identifierMap.put("Your account is suspended",UNAUTHORIZED);
		
	}

	public static ExceptionUtil getInstance() {

		return instance;
	}

	public void handleError(TwitterException e, Object param) {

		ArrayList<Object> params = null;
		if (param != null) {
			params = new ArrayList<Object>();

			params.add(param);
		}
		handleError(e, params);

	}

	public void handleError(TwitterException e){
		handleError(e, null);
	}

	private int resolveErrorCode(String eMsg) {
		if (eMsg != null) {
			for (String msg : identifierMap.keySet()) {
				if (eMsg.contains(msg)) {
					return identifierMap.get(msg);
				}
			}
		}
		return 0;
	}

	public void handleError(TwitterException e, ArrayList<Object> params){
		String paramsStr = "";
		if (params != null) {
			for (Object obj : params) {
				paramsStr = paramsStr + obj.toString() + ", ";
			}
		}

		int errorCode = resolveErrorCode(e.getMessage());
		if (errorCode == 0) {
			errorCode = e.getStatusCode();
		}
		if (errorCode == NOT_FOUND) {
			logger.debug("Twitter: User not found. Params: " + paramsStr);
		} else if (errorCode == UNDERSTOOD_BUT_ERROR) {
			logger.info("Twitter: "+paramsStr +"\n"+ e.getMessage());
		}else if (errorCode == TWITTER_SERVERS_OVERLOADED) {
			logger.info("Twitter: The Twitter servers are up, but overloaded with requests. Try again later.");
		} else if (errorCode == RATE_LIMIT_EXCEEDED) {
			logger.error("Twitter: Rate limit exceeded.");
		} else if (errorCode == UNAUTHORIZED) {
			logger.error(e.getMessage());
		} else if (errorCode == UNABLE_TO_FOLLOW) {
			logger.error("Twitter: You are unable to follow more people at this time.");
		} else if (errorCode == INVALID_REQUEST) {
			logger.info("Twitter: The request was invalid. Possible reason: Query string may be including empty character. ");
		} else if (errorCode == LONG_TWEET) {
			logger.info("Twitter: The text of your tweet is too long.");
		} else if (errorCode == SHARING_NOT_PERMISSABLE) {
			logger.info("Twitter: Sharing is not permissable for this status.");
		} else if (errorCode == AREADY_FAVORITED) {
			logger.info("Twitter: You have already favorited this status.");
		}else if (errorCode == STATUS_DUPLICATE) {
			logger.info("Twitter: Status is a duplicate.");
		} else if (errorCode == TWITTER_DOWN) {
			logger.info("Twitter: Twitter is down or being upgraded.");
		} else if (errorCode == PROXY_DOWN) {
			logger.info("Twitter: The URI requested is invalid or the resource requested, such as a user, does not exist.");
		}  else if (errorCode == TIME_OUT) {
			logger.info("Twitter: Connection timed out.");
		}  else if (errorCode == JSON_ERROR) {
			logger.info("Twitter: A JSONObject text must begin with...");
		} else if (errorCode == CACHE_ACCESS_DENIED) {
			logger.info("Twitter: Cache access denied...");
		}else if (errorCode == TEMP_UNAVAILABLE) {
			logger.info("Twitter: 307 - The server may be temporarily unavailable...");
		}else if (errorCode == NOT_IMPLEMENTED) {
			logger.info("Twitter: 501 - Not implemented...");
		}else if (errorCode == GATEWAY_TIMEOUT) {
			logger.info("Twitter: 504 - Gateway timeout...");
		} else {
			logger.error("Twitter: Unhandled twitter exception.", e);
		}
	}
}
