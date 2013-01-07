package com.debatree.twitter;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.debatree.json.IDsJSONImpl;
import com.debatree.json.TweetJSONImpl;
import com.debatree.json.UserJSONImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Twitter4JProxy {
	HttpClient client = null;
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	Twitter twitter = null;
	Announcer announcer = null;
	private static boolean DETAILED_LOG = false;

	private static Logger logger = Logger.getLogger(Twitter4JProxy.class);

	private void loadCredentials() throws DebatreeException {
	}

	private void getCredentials() throws DebatreeException {
		announcer = announcerMgr.getAnnouncer(announcer.getId());
		// logger.info("getCredentials:" + announcer.getScreenName());

		if (announcer.getAuthToken() == null || announcer.getSesId() == null) {
			// logger.info("getCredentials-2:" + announcer.getScreenName());
			loadCredentials();
			// logger.info("getCredentials-3:" + announcer.getScreenName());

		}

	}

	public Twitter4JProxy(long userId) throws DebatreeException {
		announcer = announcerMgr.getAnnouncer(userId);

		if (announcer == null) {
			throw new DebatreeException("No such a user with id: " + userId);
		}
		twitter = new TwitterFactory().getInstance(new AccessToken(announcer.getAccessToken(), announcer.getAccessTokenSecret()));

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

		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("id", String.valueOf(id)));
		nvps.add(new BasicNameValuePair("cursor", String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/friends/ids.json", nvps);

		IDsJSONImpl ids = (IDsJSONImpl) Util.convertJSONToObj(response, IDsJSONImpl.class);
		return ids.getIdList();

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
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();

		if (idList.size() > 100) {
			idList = idList.subList(0, 99);
		}
		String ids = DBMgrImpl.getIdListAsCommaSeparatedString4In(idList);
		nvps.add(new BasicNameValuePair("user_id", ids));
		// nvps.add(new BasicNameValuePair("include_entities",
		// String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/users/lookup.json", nvps);
		Type listOfTestObject = new TypeToken<List<UserJSONImpl>>() {
		}.getType();

		Gson gson = new Gson();

		List<UserJSONImpl> users = (List<UserJSONImpl>) gson.fromJson(response, listOfTestObject);
		return users;

	}

	public UserJSONImpl getUser(String screenName) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("screen_name", screenName));
		// nvps.add(new BasicNameValuePair("include_entities",
		// String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/users/show.json", nvps);

		Gson gson = new Gson();

		UserJSONImpl user = (UserJSONImpl) gson.fromJson(response, UserJSONImpl.class);
		return user;

	}

	public UserJSONImpl getUser(long id) throws DebatreeException {
		ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("user_id", String.valueOf(id)));
		// nvps.add(new BasicNameValuePair("include_entities",
		// String.valueOf(cursor)));
		String response = get("https://api.twitter.com/1/users/show.json", nvps);

		UserJSONImpl user = (UserJSONImpl) Util.convertJSONToObj(response, UserJSONImpl.class);
		return user;

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

	public IDs getBidirectionalFriends(long id) throws DebatreeException {
		IDs ids = null;
		try {
			ids = twitter.getFriendsIDs(id);
		} catch (TwitterException e) {
			throw new DebatreeException(e);
		}
//		
//		if(ids!=null){
//			for()
//		}
		return ids;
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
		// httpReq.setHeader("Host", "api.twitter.com");
		httpReq.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:13.0) Gecko/20100101 Firefox/13.0.1");
		httpReq.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		httpReq.setHeader("Accept-Language", "en-us,en;q=0.5");
		httpReq.setHeader("Accept-Encoding", "gzip, deflate");
		httpReq.setHeader("Connection", "keep-alive");
		httpReq.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		httpReq.setHeader("X-PHX", "true");
		httpReq.setHeader("X-Requested-With", "XMLHttpRequest");
		// httpReq.setHeader("Referer",
		// "https://api.twitter.com/receiver.html");

		 httpReq.setHeader("Cookie", "_twitter_sess=" + announcer.getSesId() +
		 "; ");

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

	public static Twitter4JProxy getDefaultClient() throws DebatreeException {
		List<Announcer> anList = AnnouncerMgrImpl.getInstance().getAllAnnouncers();

		return new Twitter4JProxy(anList.get(0).getId());
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
