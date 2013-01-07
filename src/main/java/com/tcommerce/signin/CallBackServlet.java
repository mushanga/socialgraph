/**
	TwitStreet - Twitter Stock Market Game
    Copyright (C) 2012  Engin Guller (bisanthe@gmail.com), Cagdas Ozek (cagdasozek@gmail.com)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.tcommerce.signin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.tcommerce.graph.GraphDatabase;

import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@SuppressWarnings("serial")
public class CallBackServlet extends HttpServlet {
	private static Logger logger = Logger.getLogger(CallBackServlet.class);
	public static final String COOKIE_ID = "id";
	public static final String COOKIE_OAUTHTOKEN = "oauthtoken";
	public static final String REQUEST_TOKEN = "requestToken";
	private static final String OAUTH_VERIFIER = "oauth_verifier";

	private static final int COOKIE_EXPIRE = 30 * 24 * 60 * 60;
	ConfigMgr configMgr = ConfigMgrImpl.getInstance();
	AnnouncerMgrImpl anncAnnouncerMgrImpl = AnnouncerMgrImpl.getInstance();

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0
		response.setDateHeader("Expires", 0); // prevents caching at the proxy
												// server

		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(configMgr.getConsumerKey(),
				configMgr.getConsumerSecret());

		RequestToken requestToken = (RequestToken) request.getSession()
				.getAttribute(REQUEST_TOKEN);
		String verifier = request.getParameter(OAUTH_VERIFIER);
		try {
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
					verifier);
			long userId = accessToken.getUserId();
			String screenName = accessToken.getScreenName();
			String oauthToken = accessToken.getToken();
			String oauthTokenSecret = accessToken.getTokenSecret();
			Announcer announcer = null;
			announcer = anncAnnouncerMgrImpl.getAnnouncer(userId);
			// new user
			if (announcer == null) {
				twitter4j.User twUser = twitter.showUser(userId);
				announcer = new Announcer();
				announcer.setId(userId);
				announcer.setScreenName(screenName);
				announcer.setAccessToken(oauthToken);
				announcer.setAccessTokenSecret(oauthTokenSecret);
				announcer.setPictureUrl(twUser.getProfileImageURL()
						.toExternalForm());
				announcer.setConsumerKey(configMgr.getConsumerKey());
				announcer.setConsumerSecret(configMgr.getConsumerSecret());

				announcer.setDescription(twUser.getDescription());
				announcer.setLongName(twUser.getName());
				announcer.setLocation(twUser.getLocation());
				announcer.setUrl(twUser.getURL() == null ? null : twUser
						.getURL().toExternalForm());

				anncAnnouncerMgrImpl.addAnnouncer(announcer);
				addFriends2Db(announcer);

			} else {
				// existing user logging in again
				twitter4j.User twUser = twitter.showUser(userId);
				announcer = new Announcer();
				announcer.setId(userId);
				announcer.setScreenName(screenName);
				announcer.setAccessToken(oauthToken);
				announcer.setAccessTokenSecret(oauthTokenSecret);
				announcer.setPictureUrl(twUser.getProfileImageURL()
						.toExternalForm());
				announcer.setConsumerKey(configMgr.getConsumerKey());
				announcer.setConsumerSecret(configMgr.getConsumerSecret());

				announcer.setDescription(twUser.getDescription());
				announcer.setLongName(twUser.getName());
				announcer.setLocation(twUser.getLocation());
				announcer.setUrl(twUser.getURL() == null ? null : twUser
						.getURL().toExternalForm());
				anncAnnouncerMgrImpl.updateAnnouncer(announcer);
				addFriends2Db(announcer);

			}
			request.getSession().setAttribute(Announcer.ANNOUNCER, announcer);
			Cookie cookies[] = createCookie(userId, oauthToken);
			writeCookies(response, cookies);
		} catch (TwitterException e) {
			throw new ServletException(e);
		}
		response.sendRedirect(request.getContextPath() + "/");
	}

	public void addFriends2Db(Announcer announcer) {
		long cursor = -1;
		IDs ids;
		Twitter twitter;
		ResponseList<User> userResponseList = null;
		long[] allIds = new long[0];
		try {
			twitter = announcer.getTwitterProxy();
			do {
				ids = twitter.getFriendsIDs(cursor);
				long[] idArr = ids.getIDs();
				allIds = ArrayUtils.addAll(allIds, idArr);
				if(idArr != null){
					if(idArr.length > 100){
						for(int i = 0 ; i < idArr.length; i+=100){
							long[] subArray = Arrays.copyOfRange(idArr, i, (i+100) >= idArr.length ?  (idArr.length - 1) : (i+100) ); 
							if(userResponseList == null){
								userResponseList = twitter.lookupUsers(subArray);
							}
							else{
								userResponseList.addAll(twitter.lookupUsers(subArray));
							}
						}
					}
					else{
						userResponseList =  twitter.lookupUsers(idArr);
					}
				}

			} while ((cursor = ids.getNextCursor()) != 0);
			
			saveResponseList2Db(userResponseList);
			GraphDatabase.addAnnouncerWithFriends(announcer.getId(), allIds);
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public void saveResponseList2Db(ResponseList<User> userResponseList){
		for(User user : userResponseList){
			Announcer announcer = convertUser2Announcer(user);
			anncAnnouncerMgrImpl.addAnnouncer(announcer);
		}
	}
	
	public Announcer convertUser2Announcer(User user){
		Announcer announcer = new Announcer();
		
		announcer = new Announcer();
		announcer.setId(user.getId());
		announcer.setScreenName(user.getScreenName());

		announcer.setPictureUrl(user.getProfileImageURL()
				.toExternalForm());
		announcer.setConsumerKey(configMgr.getConsumerKey());
		announcer.setConsumerSecret(configMgr.getConsumerSecret());

		announcer.setDescription(user.getDescription());
		announcer.setLongName(user.getName());
		announcer.setLocation(user.getLocation());
		announcer.setUrl(user.getURL() == null ? null : user
				.getURL().toExternalForm());
		
		return announcer;
	}

	public Cookie[] createCookie(long userId, String oauthToken) {
		Cookie ck1 = new Cookie(COOKIE_ID, String.valueOf(userId));
		Cookie ck2 = new Cookie(COOKIE_OAUTHTOKEN, oauthToken);
		ck1.setMaxAge(COOKIE_EXPIRE);
		ck2.setMaxAge(COOKIE_EXPIRE);
		return new Cookie[] { ck1, ck2 };
	}

	public void writeCookies(HttpServletResponse response, Cookie[] cookies) {
		for (Cookie cookie : cookies) {
			response.addCookie(cookie);
		}

	}
}
