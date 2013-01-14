package com.debatree.twitter;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.util.Util;
import com.debatree.exception.DebatreeException;
import com.debatree.json.FriendListJSONImpl;
import com.debatree.json.IDsJSONImpl;
import com.debatree.json.TweetJSONImpl;
import com.debatree.json.UserJSONImpl;
import com.debatree.main.GraphState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.tcommerce.graph.GraphDatabase;

public class TwitterClient {
	GraphDatabase gdb = GraphDatabase.getInstance();
	Util util = Util.getInstance();
	HttpClient client = null;
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	Twitter twitter = new TwitterFactory().getInstance();
	Announcer announcer = null;
	private static boolean DETAILED_LOG = false;

	private static HashMap<String, List<UserJSONImpl>> cursorUserListMap = new HashMap<String, List<UserJSONImpl>>();
	private static HashMap<String, String> cursorNextCursorMap = new HashMap<String, String>();

	private static Logger logger = Logger.getLogger(TwitterClient.class);

	public TwitterClient(long userId) throws DebatreeException {

		announcer = announcerMgr.getAnnouncer(userId);

		twitter.setOAuthConsumer(announcer.getConsumerKey(), announcer.getConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(announcer.getAccessToken(), announcer.getAccessTokenSecret()));

		if (announcer == null) {
			throw new DebatreeException("No such a user with id: " + userId);
		}

	}

	private void loadCredentials() throws DebatreeException {
		try {

			// Protocol easyhttps = new Protocol("https", new
			// EasySSLProtocolSocketFactory(), 443);
			// Protocol.registerProtocol("https", easyhttps);
			logger.info("loadCredentials - 1");
			HttpClient client = null;
			client = new DefaultHttpClient();
			client = WebClientDevWrapper.wrapClient(client);

			// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// getProxyAsHttpHost());

			HttpGet httpGet = new HttpGet("https://twitter.com");

			HttpResponse response = client.execute(httpGet);
			logger.info("loadCredentials - 2");
			Header[] headers = response.getAllHeaders();

			InputStream input = response.getEntity().getContent();

			InputStreamReader isr = new InputStreamReader(input);
			BufferedReader br = new BufferedReader(isr);

			String content = "";
			String line = null;
			while ((line = br.readLine()) != null) {
				content = content + "\n" + line;
			}
			logger.info("loadCredentials - 3");
			// System.out.println(content);

			String paut = Util.parse(content, " value=\"", "\"", "name=\"redirect_after_login\" value=\"/\">", "name=\"authenticity_token\"");
			logger.info("loadCredentials - 4, authToken: " + paut);
			// System.out.println(paut);

			HttpPost httpPost = new HttpPost("https://twitter.com/sessions");
			BasicNameValuePair[] params = { new BasicNameValuePair("session[username_or_email]", announcer.getScreenName()), new BasicNameValuePair("session[password]", announcer.getPassword()), new BasicNameValuePair("authenticity_token", paut), };
			UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(Arrays.asList(params));
			urlEncodedFormEntity.setContentEncoding(HTTP.UTF_8);
			httpPost.setEntity(urlEncodedFormEntity);

			response = client.execute(httpPost);
			Header[] cookieArray = response.getHeaders("Set-Cookie");
			String sessIdCk = "";
			String autIdCk = "";
			String cookieStr = "";
			for (Header cookie : cookieArray) {
				if (cookie.getValue().contains("_twitter_sess")) {

					sessIdCk = cookie.getValue();
				}
				cookieStr = cookieStr + cookie.getValue() + ";";

			}

			announcer.setAuthToken(paut);
			announcerMgr.updateAnnouncer(announcer);
			announcerMgr.setCookie(announcer.getId(), cookieStr);
			announcer = announcerMgr.getAnnouncer(announcer.getId());

		} catch (Exception ex) {
			throw new DebatreeException(ex);
		}

	}

	private void getCredentials() throws DebatreeException {
		announcer = announcerMgr.getAnnouncer(announcer.getId());
		if (!util.stringIsValid(announcer.getAuthToken()) || !util.stringIsValid(announcerMgr.getCookie(announcer.getId()))) {
			loadCredentials();
		}

	}

	public void follow(long userId) throws DebatreeException {

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		post("https://twitter.com/i/user/follow", nvps);
	}

	public void unfollow(long userId) throws DebatreeException {

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", String.valueOf(userId)));
		post("https://twitter.com/i/user/unfollow", nvps);
	}

	public long tweet(String tweet) throws DebatreeException {

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("status", tweet));
		String resp = post("https://twitter.com/i/tweet/create", nvps);

		return getTweetId(resp);

	}

	private long getTweetId(String response) {

		String tweetIdStr = Util.parse(response, "\"", "\"", "data-tweet-id", null);
		tweetIdStr = tweetIdStr.replace("\\", "");
		return Long.valueOf(tweetIdStr);

	}

	public void retweet(long statusId) throws DebatreeException {

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("id", String.valueOf(statusId)));
		post("https://twitter.com/i/tweet/retweet", nvps);

	}

	public void favorite(long statusId) throws DebatreeException {

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("id", String.valueOf(statusId)));
		post("https://twitter.com/i/tweet/favorite", nvps);

	}

	public long reply(String message, long statusId) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("status", message));
		nvps.add(new BasicNameValuePair("in_reply_to_status_id", String.valueOf(statusId)));

		String resp = post("https://twitter.com/i/tweet/create", nvps);

		return getTweetId(resp);
	}

	public List<Long> getFollowingsIDs(long id, int cursor) throws DebatreeException {
		List<Long> idsList = new ArrayList<Long>();
		IDsJSONImpl ids = new IDsJSONImpl();
		try {
			ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			nvps.add(new BasicNameValuePair("id", String.valueOf(id)));
			nvps.add(new BasicNameValuePair("cursor", String.valueOf(cursor)));
			String response = get("https://api.twitter.com/1/friends/ids.json", nvps);

			ids = (IDsJSONImpl) Util.convertJSONToObj(response, IDsJSONImpl.class);
			idsList = ids.getIdList();
		} catch (Exception e) {
			logger.info(e.getMessage());

		}
		return idsList;

	}

	public class TwitterCrawler implements Runnable {
		private UserJSONImpl user;

		public TwitterCrawler(UserJSONImpl user, String dataCursor) {
			super();
			this.user = user;
			this.dataCursor = dataCursor;
		}

		String dataCursor;

		@Override
		public void run() {
			while (Util.stringIsValid(dataCursor) && !cursorUserListMap.containsKey(user.getScreenName() + dataCursor)) {

				try {

					FriendListJSONImpl a = saveFriends(user, dataCursor);
					dataCursor = a.getNextCursor();
				} catch (DebatreeException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	private FriendListJSONImpl saveFriends(UserJSONImpl user, String dataCursor) throws DebatreeException {
		FriendListJSONImpl friendListJSON = null;

		String nextCursor = "-1";
		List<UserJSONImpl> friends = new ArrayList<UserJSONImpl>();

		List<String> strList = new ArrayList<String>();

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		if (!util.stringIsValid(dataCursor)) {
			dataCursor = "-1";
		}
		nvps.add(new BasicNameValuePair("cursor", dataCursor));
		nvps.add(new BasicNameValuePair("is_forward", "true"));
		String response = get("https://twitter.com/" + user.getScreenName() + "/following/users", nvps);
		response = response.replace("\\", "");
		List<String> idList = Util.parse(response, "data-user-id=\"", "\"", null, null, "data-name");
		List<String> screenNameList = Util.parse(response, "data-screen-name=\"", "\"", null, null, "data-item-type");
		List<String> imgList = Util.parse(response, "src=\"", "\"", null, null, "img class");

		ArrayList<UserJSONImpl> usList = new ArrayList<UserJSONImpl>();
		for (int i = 0; i < idList.size(); i++) {

			UserJSONImpl newUser = new UserJSONImpl();
			newUser.setScreenName(screenNameList.get(i));
			newUser.setId(Long.valueOf(idList.get(i)));
			newUser.setPicUrl(imgList.get(i));
			gdb.addOrUpdateNode(newUser);
			usList.add(newUser);
		}
		try {
			nextCursor = Util.parse(response, "\"cursor\":\"", "\"", null, null);
		} catch (Exception ex) {

		}

		friends = usList;
		// friends = getUsers(idList);

		// GraphDatabase.getInstance().addFriendships(user, friends);
		logger.info(friends.size() + " friends of " + user.getScreenName() + " added to the graph.");
		friendListJSON = new FriendListJSONImpl(nextCursor, user, friends);

		cursorNextCursorMap.put(user.getScreenName() + dataCursor, nextCursor);

		cursorUserListMap.put(user.getScreenName() + dataCursor, friends);

		logger.info("dataCursor: " + dataCursor + ", " + "nextCursor: " + nextCursor);
		return friendListJSON;
	}

	// public FriendListJSONImpl getBidirectionalLinks(String userName) throws
	// DebatreeException {
	// UserJSONImpl user = getUser(userName,true);
	// FriendListJSONImpl friendListJSON = null;
	// String nextCursor = "-1";
	//
	// if (cursorUserListMap.containsKey(userName+dataCursor)) {
	// List<UserJSONImpl> friends = new ArrayList<UserJSONImpl>();
	// List<UserJSONImpl> friendList =
	// cursorUserListMap.get(userName+dataCursor);
	// for(int i = 0; i<friendList.size(); i++){
	// UserJSONImpl u = friendList.get(i);
	// UserJSONImpl dbu = getUser(u.getScreenName(), false);
	// friendList.set(i, dbu);
	//
	// }
	//
	// friends.addAll(friendList);
	// nextCursor= cursorNextCursorMap.get(userName+dataCursor);
	// friendListJSON = new FriendListJSONImpl(nextCursor, user, friends);
	// } else {
	// friendListJSON = saveFriends(user, dataCursor);
	// }
	// Thread nextCursorObtainer = new Thread(new
	// TwitterCrawler(user,friendListJSON.getNextCursor()));
	// nextCursorObtainer.start();
	// return friendListJSON;
	// }

	public class GraphExpander extends Thread {

		String userName;
		int secsUntilReset;
		int resetTimeInSecs;

		public GraphExpander(String userName, int secs, int resetTimeInSecs) {
			super();
			this.secsUntilReset = secs;
			this.userName = userName;
			this.resetTimeInSecs = resetTimeInSecs;
		}

		@Override
		public void run() {
			super.run();
			try {
				logger.info("Sleeping for " + secsUntilReset + " seconds...");
				try {
					Thread.sleep(secsUntilReset * 1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}

				searchesInAction.remove(userName.toLowerCase());
				logger.info("Continuing...");
				setFriendsOfFriendsGraph(userName);
			} catch (DebatreeException e) {
				logger.error(e.getMessage(), e);
			}

		}
	}

	private static ArrayList<String> searchesInAction = new ArrayList<String>();

	public void setFriendsOfFriendsGraph(String userName) throws DebatreeException {

		if (searchesInAction.contains(userName.toLowerCase())) {
			return;
		} else {
			searchesInAction.add(userName.toLowerCase());
		}
		UserJSONImpl user = getUser(userName, true);

		List<Long> friendsIdList = gdb.getFriends(user.getId());
		if (!util.listIsValid(friendsIdList)) {
			friendsIdList = new ArrayList<Long>();
			try {
				IDs a = twitter.getFriendsIDs(user.getId(), -1);

				for (Long friendId : a.getIDs()) {
					friendsIdList.add(friendId);
				}

				gdb.addFriendships(user.getId(), friendsIdList);
			} catch (TwitterException e1) {

				Thread ge = new GraphExpander(userName, e1.getRateLimitStatus().getSecondsUntilReset(), e1.getRateLimitStatus().getResetTimeInSeconds());
				handleTwitterException(e1, ge);
				
			}
		}

		for (Long id : friendsIdList) {

			List<Long> friendsOfFriendList = gdb.getFriends(id);

			if (!util.listIsValid(friendsOfFriendList)) {
				friendsOfFriendList = new ArrayList<Long>();
				try {
					for (Long friendOfFriendId : twitter.getFriendsIDs(id, -1).getIDs()) {
						friendsOfFriendList.add(friendOfFriendId);
					}
					gdb.addFriendships(id, friendsOfFriendList);
				} catch (TwitterException e1) {
					Thread ge = new GraphExpander(userName, e1.getRateLimitStatus().getSecondsUntilReset(), e1.getRateLimitStatus().getResetTimeInSeconds());
					handleTwitterException(e1, ge);
					
				}
			}
		}

		searchesInAction.remove(userName.toLowerCase());

	}
	private void handleTwitterException(TwitterException e1) throws DebatreeException {
		handleTwitterException(e1, null);
	}

	private void handleTwitterException(TwitterException e1, Thread job) throws DebatreeException {

		if (e1.exceededRateLimitation()) {

			int secs = e1.getRateLimitStatus().getSecondsUntilReset();
			
			announcer.setResetTimeInSecs(e1.getRateLimitStatus().getResetTimeInSeconds());
			announcerMgr.updateAnnouncer(announcer);
			if (job != null) {
				job.start();
			}
			throw new DebatreeException(e1);
		} else {
			logger.error(e1.getMessage(), e1);

		}
	}

	public GraphState getGraphForUser(String userName) throws DebatreeException {
		
		setFriendsOfFriendsGraph(userName);
		return getGraphForUser(getUser(userName, false).getId());
	}

	public GraphState getGraphForUser(long userId) {

		GraphState graph = new GraphState();

		List<Long> friendsIdList = gdb.getFriends(userId);

		for (Long friend : friendsIdList) {
			graph.addLink(userId, friend);
			List<Long> friendsOfFriendList = gdb.getFriends(friend);

			for (Long friendOfFriend : friendsOfFriendList) {

				graph.addLink(friend, friendOfFriend);
			}

		}

		graph.process();
		return graph;

	}

	public FriendListJSONImpl getFriends(String userName, String dataCursor) throws DebatreeException {
		UserJSONImpl user = getUser(userName, true);

		FriendListJSONImpl friendListJSON = null;
		String nextCursor = "-1";

		if (cursorUserListMap.containsKey(userName + dataCursor)) {

			nextCursor = dataCursor;
			List<UserJSONImpl> friendList = new ArrayList<UserJSONImpl>();
			while (cursorUserListMap.containsKey(userName + nextCursor)) {
				friendList.addAll(cursorUserListMap.get(userName + nextCursor));
				nextCursor = cursorNextCursorMap.get(userName + nextCursor);
			}

			for (int i = 0; i < friendList.size(); i++) {
				UserJSONImpl u = friendList.get(i);
				UserJSONImpl dbu = getUser(u.getScreenName(), false);
				friendList.set(i, dbu);

			}

			friendListJSON = new FriendListJSONImpl(nextCursor, user, friendList);
		} else {
			friendListJSON = saveFriends(user, dataCursor);
		}
		Thread nextCursorObtainer = new Thread(new TwitterCrawler(user, friendListJSON.getNextCursor()));
		nextCursorObtainer.start();
		return friendListJSON;
	}

	public String getFriendsAsJSON(String screenName, String dataCursor) throws DebatreeException {
		FriendListJSONImpl friendList = getFriends(screenName, dataCursor);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String s = gson.toJson(friendList, FriendListJSONImpl.class);

		return s;

	}

	private String get(String url) throws DebatreeException {
		return get(url, null);
	}

	public IDsJSONImpl getFollowingsIDsObj(long id, int cursor) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("id", String.valueOf(id)));
		nvps.add(new BasicNameValuePair("cursor", String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/friends/ids.json", nvps);

		IDsJSONImpl ids = (IDsJSONImpl) Util.convertJSONToObj(response, IDsJSONImpl.class);
		return ids;

	}

	public List<UserJSONImpl> getUsers(List<Long> idList) throws DebatreeException {
		List<UserJSONImpl> users = new ArrayList<UserJSONImpl>();

		if (idList.size() > 100) {
			int size = idList.size();
			users.addAll(getUsers(idList.subList(0, size / 2)));
			users.addAll(getUsers(idList.subList((size / 2), size)));

		} else {

			if (idList.size() != users.size()) {
				ArrayList<Long> foundUserIds = new ArrayList<Long>();

				for (UserJSONImpl user : users) {
					foundUserIds.add(user.getId());
				}
				idList.removeAll(foundUserIds);

				ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
				String ids = DBMgrImpl.getIdListAsCommaSeparatedString4In(idList);
				nvps.add(new BasicNameValuePair("user_id", ids));
				String response = get("https://api.twitter.com/1/users/lookup.json", nvps);
				Type listOfTestObject = new TypeToken<List<UserJSONImpl>>() {
				}.getType();

				Gson gson = new Gson();

				users.addAll((List<UserJSONImpl>) gson.fromJson(response, listOfTestObject));
			}

		}

		return users;

	}

	public UserJSONImpl getUser(String screenName, boolean reloadIfneeds) throws DebatreeException {

		UserJSONImpl user = gdb.getUserByName(screenName);
		if (user == null || (reloadIfneeds && user.getFollowersCount() < 0)) {
			String response = get("https://twitter.com/" + screenName);
			String id = Util.parse(response, "data-user-id=\"", "\"", "js-mini-profile-stats", "</ul>");
			String friendsCount = Util.parse(response, "<strong>", "<", "data-element-term=\"following_stats\"", "/strong>").replace(",", "");
			String followersCount = Util.parse(response, "<strong>", "<", "data-element-term=\"follower_stats\"", "/strong>").replace(",", "");
			String image = Util.parse(response, "img src=\"", "\"", "profile-header-inner-overlay", "</a>");

			user = new UserJSONImpl();
			user.setScreenName(screenName);
			user.setPicUrl(image);
			user.setId(Long.valueOf(id));
			user.setFriendsCount(Integer.valueOf(friendsCount));
			user.setFollowersCount(Integer.valueOf(followersCount));
			gdb.addOrUpdateNode(user);
		}

		return user;

	}

	HashMap<Long, String> idNameMap = new HashMap<Long, String>();

	public UserJSONImpl getUser(long id) throws DebatreeException {

		String screenName = idNameMap.get(id);
		if (!Util.stringIsValid(screenName)) {
			ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			nvps.add(new BasicNameValuePair("id", String.valueOf(id)));
			String response = get("http://twitstreet.com/stock", nvps);
			screenName = Util.getBetween(response, "http://twitter.com/#!/", "\"");
			if (Util.stringIsValid(screenName)) {
				idNameMap.put(id, screenName);
			}

		}

		return getUser(screenName, true);

	}

	public TweetJSONImpl getTweet(long id) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("id", String.valueOf(id)));
		// nvps.add(new BasicNameValuePair("include_entities",
		// String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/statuses/show.json", nvps);

		TweetJSONImpl tweet = (TweetJSONImpl) Util.convertJSONToObj(response, TweetJSONImpl.class);
		return tweet;

	}

	public List<TweetJSONImpl> getUserTimeline(String screenName, int count, boolean includeEntities, boolean includeRTs) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("screen_name", String.valueOf(screenName)));
		nvps.add(new BasicNameValuePair("count", String.valueOf(count)));

		nvps.add(new BasicNameValuePair("include_entities", String.valueOf(includeEntities)));

		nvps.add(new BasicNameValuePair("include_rts", String.valueOf(includeRTs)));

		String response = get("https://api.twitter.com/1/statuses/user_timeline.json", nvps);

		List<TweetJSONImpl> tweets = (List<TweetJSONImpl>) Util.convertJSONToObjList(response, TweetJSONImpl.class);
		return tweets;
	}

	public List<TweetJSONImpl> getUserMentions(String screenName, int count, boolean includeEntities, boolean includeRTs) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("screen_name", String.valueOf(screenName)));
		nvps.add(new BasicNameValuePair("count", String.valueOf(count)));

		nvps.add(new BasicNameValuePair("include_entities", String.valueOf(includeEntities)));

		nvps.add(new BasicNameValuePair("include_rts", String.valueOf(includeRTs)));

		String response = get("https://api.twitter.com/1/statuses/user_timeline.json", nvps);

		List<TweetJSONImpl> tweets = (List<TweetJSONImpl>) Util.convertJSONToObjList(response, TweetJSONImpl.class);
		return tweets;
	}

	public List<Long> getFollowersIDs(long id, int cursor) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("screen_name", String.valueOf(this.announcer.getScreenName())));
		nvps.add(new BasicNameValuePair("cursor", String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/followers/ids.json", nvps);

		IDsJSONImpl ids = (IDsJSONImpl) Util.convertJSONToObj(response, IDsJSONImpl.class);
		return ids.getIdList();

	}

	private void setHeaders(HttpRequestBase httpReq) {
		httpReq.setHeader("host", httpReq.getURI().getHost());
		httpReq.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:13.0) Gecko/20100101 Firefox/13.0.1");

		httpReq.setHeader("Accept-Language", "en-us,en;q=0.5");
		httpReq.setHeader("Accept-Encoding", "gzip, deflate");

		httpReq.setHeader("scheme", "https");
		httpReq.setHeader("version", "HTTP/1.1");
		httpReq.setHeader("Cookie", announcerMgr.getCookie(announcer.getId()));
		httpReq.setHeader("Pragma", "no-cache");
		httpReq.setHeader("Cache-Control", "no-cache");
	}

	private String post(String url, ArrayList<BasicNameValuePair> parameters) throws DebatreeException {
		return post(url, parameters, true);

	}

	private String post(String url, ArrayList<BasicNameValuePair> parameters, boolean auth) throws DebatreeException {
		return method("post", url, parameters, 1);
	}

	private String get(String url, List<BasicNameValuePair> nvps) throws DebatreeException {
		return method("get", url, nvps, 1);
	}

	public boolean isUserSuspended(String screenName) {

		String resp = "";
		try {
			resp = get("https://www.twitter.com/" + screenName, null);
		} catch (DebatreeException e) {
			logger.error(e);
		}

		return resp.contains("Sorry, that user is suspended.") || resp.contains("Your account is suspended");
	}

	int MAX_TRY = 5;

	private String method(String method, String url, List<BasicNameValuePair> nvps, int tryNumber) throws DebatreeException {
		HttpClient client = null;

		String responseContent = "";
		try {
			client = new DefaultHttpClient();

			client = WebClientDevWrapper.wrapClient(client);
			getCredentials();

			// client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
			// getProxyAsHttpHost());

			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();

			params.add(new BasicNameValuePair("authenticity_token", announcer.getAuthToken()));

			if (Util.isListValid(nvps)) {
				params.addAll(nvps);
			}

			HttpRequestBase httpRequest = null;
			if (method.equalsIgnoreCase("post")) {
				httpRequest = new HttpPost(url);

				UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
				urlEncodedFormEntity.setContentEncoding(HTTP.UTF_8);
				((HttpPost) httpRequest).setEntity(urlEncodedFormEntity);
			} else {
				String paramString = "";
				if (Util.isListValid(nvps)) {
					params.addAll(nvps);
					paramString = URLEncodedUtils.format(nvps, "utf-8");
				}

				httpRequest = new HttpGet(url + "?" + paramString);
				// httpRequest = new
				// HttpGet("http://search.twitter.com/1/search.json?q=%4006melihgokcek&result_type=recent");

			}
			setHeaders(httpRequest);

			HttpResponse response = client.execute(httpRequest);

			InputStream input = response.getEntity().getContent();
			GZIPInputStream gzip = new GZIPInputStream(input);
			InputStreamReader isr = new InputStreamReader(gzip);
			BufferedReader br = new BufferedReader(isr);

			String line = null;
			while ((line = br.readLine()) != null) {
				responseContent = responseContent + "\n" + line;
			}

			client.getConnectionManager().shutdown();
		} catch (EOFException ex) {
			DebatreeException abe = new DebatreeException("EOF Exception");

			throw abe;
		} catch (Exception ex) {
			throw new DebatreeException(ex);
		}

		if (DETAILED_LOG) {
			logger.info("Announcer: " + announcer.getScreenName());
			logger.info("REQUEST URL:" + url);

			int maxChar = 100;

			String responseForLog = Util.getShortenedString(responseContent, maxChar);
			logger.info("RESPONSE:" + responseForLog);

		}

		if (responseContent.contains("You are being") && responseContent.contains("redirected") && tryNumber <= MAX_TRY) {

			logger.info("loading credentials again...");
			logger.info("old credentials:" + announcer.getAuthToken() + "\n" + announcer.getSesId());
			loadCredentials();
			logger.info("new credentials:" + announcer.getAuthToken() + "\n" + announcer.getSesId());
			responseContent = method(method, url, nvps, tryNumber + 1);
		} else if (responseContent.contains("Your account may not be allowed to perform this action.")) {
			throw new DebatreeException("Your account may not be allowed to perform this action.");
		} else if (responseContent.contains("Your account is suspended")) {
			announcerMgr.setAnnouncerSuspended(this.announcer.getId());
		}
		return responseContent;
	}

	public static TwitterClient getDefaultClient() throws DebatreeException {
		List<Announcer> anList = AnnouncerMgrImpl.getInstance().getAllAnnouncers();

		return new TwitterClient(anList.get(0).getId());
	}

	public List<TweetJSONImpl> search(String str) throws DebatreeException {

		return searchWithParameters(str, null);
	}

	private List<TweetJSONImpl> searchWithParameters(String str, List<BasicNameValuePair> nvps) throws DebatreeException {
		int page = 1;
		List<TweetJSONImpl> tweets = new ArrayList<TweetJSONImpl>();
		List<TweetJSONImpl> tweetsInPage = null;

		do {
			List<BasicNameValuePair> tempNvps = new ArrayList<BasicNameValuePair>();
			tempNvps.add(new BasicNameValuePair("q", str + " -RT"));
			tempNvps.add(new BasicNameValuePair("result_type", "recent"));
			tempNvps.add(new BasicNameValuePair("rpp", "100"));
			tempNvps.add(new BasicNameValuePair("page", String.valueOf(page)));
			if (nvps != null) {
				tempNvps.addAll(nvps);
			}

			String response = get("http://search.twitter.com/search.json", tempNvps);
			response = Util.getBetween(response, "\"results\":", ",\"results_per_page\"");
			tweetsInPage = (List<TweetJSONImpl>) Util.convertJSONToObjList(response, TweetJSONImpl.class);
			if (Util.isListValid(tweetsInPage)) {
				tweets.addAll(tweetsInPage);
			}

			page++;
		} while (Util.isListValid(tweetsInPage));

		return tweets;
	}

	public List<TweetJSONImpl> searchRepliesForTweet(long tweetId, String userName) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("since_id", String.valueOf(tweetId)));

		List<TweetJSONImpl> possibleReplies = searchWithParameters("@" + userName, nvps);
		List<TweetJSONImpl> list = new ArrayList<TweetJSONImpl>();

		for (TweetJSONImpl posReply : possibleReplies) {
			Long inReplyToStatusId = posReply.getInReplyToStatusId();
			if (tweetId == inReplyToStatusId) {

				list.add(posReply);
			}
		}
		return list;
	}

	// public List<Tweet> search(String str){
	// ArrayList<Tweet> results = new ArrayList<Tweet>();
	// try {
	//
	//
	// Query q = new Query();
	// q.setQuery(str);
	// q.setRpp(100);
	// q.setResultType(Query.RECENT);
	// QueryResult qr = twitter.search(q);
	// List<Tweet> tweets = null;
	// tweets = qr.getTweets();
	// int i = 0;
	//
	// for (; i < tweets.size(); i++) {
	// try {
	//
	// Tweet t = tweets.get(i);
	// results.add(t);
	//
	// } catch (Exception ex) {
	//
	// }
	// }
	// } catch (Exception ex) {
	//
	// }
	//
	//
	// return results;
	//
	// }
	//
}
