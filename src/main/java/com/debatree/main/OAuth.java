package com.debatree.main;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.util.Util;
import com.debatree.json.UserJSONImpl;
import com.debatree.service.UserServiceImpl;
import com.google.gson.Gson;

public class OAuth {
	private static PropsConfigMgrImpl propsMgr = PropsConfigMgrImpl.getInstance();
	private static String API_KEY = propsMgr.getApiKey();
	private static String API_SECRET =  propsMgr.getApiSecret();
	private static String CALLBACK_URL = "http://127.0.0.1:8080/debatree/callback";


	public static String COOKIE_NAME = "access_token";
	
	private static OAuth instance = new OAuth();
	OAuthService service;

	UserServiceImpl userSvc = new UserServiceImpl();

	public static OAuth getInstance() {
		return instance;
	}

	private OAuth() {
		service = new ServiceBuilder().provider(TwitterApi.class).apiKey(API_KEY).apiSecret(API_SECRET).callback(CALLBACK_URL).build();

	}

	public Token getRequestToken() {
		return service.getRequestToken();
	}

	public String getAuthUrl() {
		String authUrl = service.getAuthorizationUrl(service.getRequestToken());
		return authUrl;
	}

	public Announcer handle(HttpServletRequest request, HttpServletResponse response) {
		Announcer announcer = null;
		if(request.getCookies() != null){
			for(Cookie co : request.getCookies()){
				if(co.getName().equals(COOKIE_NAME)){
					announcer = AnnouncerMgrImpl.getInstance().getAnnouncerByAccessToken(co.getValue());
					
				}
			}
				
		}

		String oAuthToken = request.getParameter("oauth_token");
		String oAuthVerifier = request.getParameter("oauth_verifier");
		if (Util.stringIsValid(oAuthVerifier)) {

			Verifier v = new Verifier(oAuthVerifier);
			Token accessToken = service.getAccessToken(new Token(oAuthToken, API_SECRET), v);

			accessToken.getToken();

			OAuthRequest req = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
			service.signRequest(accessToken, req); // the access token from step
													// 4
			Response resp = req.send();
			System.out.println(resp.getBody());
			Gson gson = new Gson();

			UserJSONImpl userjson = gson.fromJson(resp.getBody(), UserJSONImpl.class);

			Announcer newAnnouncer = new Announcer();
			newAnnouncer.setId(userjson.getId());
			newAnnouncer.setScreenName(userjson.getScreenName());
			newAnnouncer.setFollower(userjson.getFollowersCount());
			newAnnouncer.setFollowing(userjson.getFriendsCount());
			newAnnouncer.setAccessToken(accessToken.getToken());
			newAnnouncer.setAccessTokenSecret(accessToken.getSecret());
			newAnnouncer.setPictureUrl(userjson.getPicUrl());

			AnnouncerMgrImpl.getInstance().addAnnouncer(newAnnouncer);
			request.setAttribute("access_token", accessToken.getToken());
		
			
			announcer = newAnnouncer;
		}
		return announcer;
		
	}
}
