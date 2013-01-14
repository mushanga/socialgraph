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

package com.amazonbird.db.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.MessageMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.util.ExceptionUtil;
import com.amazonbird.util.Util;

@XmlRootElement
public class Announcer implements DataObjectIF {
	public static final String ANNOUNCER = "announcer";
	MessageMgrImpl msgMgr = MessageMgrImpl.getInstance();

	ExceptionUtil exutil = ExceptionUtil.getInstance();
	Util util = Util.getInstance();
	private static Logger logger = Logger.getLogger(Announcer.class);
	long id;
	private int resetTimeInSecs;
	String screenName;
	String consumerKey = null;
	String consumerSecret = null;
	String accessToken = null;
	String accessTokenSecret = null;
	String name;
	String surname;
	String email;
	String password;
	String longName;
	String description;
	String location;
	String url;
	String pictureUrl;

	private boolean suspended;
	private boolean training;
	private String sesId = null;
	private String authToken = null;
	int maxFamousPeople2Follow = 0;
	int famousPeopleFollowed = 0;
	private int following = 0;
	private int follower = 0;

	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	Twitter twitter = null;

	private long creationTime;
	private long suspensionTime;

	public List<Announcer> followingList = new ArrayList<Announcer>();

	@Override
	public void getDataFromResultSet(ResultSet rs) throws SQLException {
		this.setId(rs.getLong("id"));
		this.setScreenName(rs.getString("screenName"));
		this.setConsumerKey(rs.getString("consumerKey"));
		this.setConsumerSecret(rs.getString("consumerSecret"));
		this.setAccessToken(rs.getString("accessToken"));
		this.setAccessTokenSecret(rs.getString("accessTokenSecret"));
		this.setName(rs.getString("name"));
		this.setSurname(rs.getString("surname"));
		this.setEmail(rs.getString("email"));
		this.setPassword(rs.getString("password"));
		this.setSuspended(rs.getBoolean("suspended"));
		this.setTraining(rs.getBoolean("training"));
		this.setMaxFamousPeople2Follow(rs.getInt("maxFamousAccount2Follow"));
		this.setFamousPeopleFollowed(rs.getInt("famousAccountFollowed"));
		this.setFollower(rs.getInt("follower"));
		this.setFollowing(rs.getInt("following"));
		this.setAuthToken(rs.getString("authtoken"));
		this.setSesId(rs.getString("sesid"));
		this.setPictureUrl(rs.getString("pictureUrl"));
		this.setLongName(rs.getString("longName"));
		this.setDescription(rs.getString("description"));
		this.setLocation(rs.getString("location"));
		this.setUrl(rs.getString("url"));
		this.setResetTimeInSecs(rs.getInt("resetTimeInSecs"));

		try {
			this.setCreationTime(rs.getTimestamp("creationtime").getTime());
		} catch (Exception ex) {

		}
		try {
			this.setSuspensionTime(rs.getTimestamp("suspensiontime").getTime());
		} catch (Exception ex) {

		}

	}

	public Twitter getTwitterProxy() throws TwitterException {
		if (twitter == null) {

			TwitterFactory tf = new TwitterFactory();
			twitter = tf.getInstance();

			twitter.setOAuthConsumer(getConsumerKey(), getConsumerSecret());
			twitter.setOAuthAccessToken(new AccessToken(getAccessToken(),
					getAccessTokenSecret()));
		}
		return twitter;
	}

	public String getScreenName() {
		return screenName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setScreenName(String name) {
		this.screenName = name;
	}

	public String getConsumerKey() {
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getMaxFamousPeople2Follow() {
		return maxFamousPeople2Follow;
	}

	public void setMaxFamousPeople2Follow(int maxFamousPeople2Follow) {
		this.maxFamousPeople2Follow = maxFamousPeople2Follow;
	}

	public int getFamousPeopleFollowed() {
		return famousPeopleFollowed;
	}

	public void setFamousPeopleFollowed(int famousPeopleFollowed) {
		this.famousPeopleFollowed = famousPeopleFollowed;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String toString() {

		String str = "\n" + "screenName=" + screenName + "\n" + "consKey="
				+ consumerKey + "\n" + "consSecret=" + consumerSecret + "\n"
				+ "accToken=" + accessToken + "\n" + "accSecret="
				+ accessTokenSecret + "\n" + "name=" + name + "\n" + "surname="
				+ surname + "\n" + "email=" + email + "\n" + "training="
				+ training + "\n";
		return str;

	}

	public void tweet(String tweet) throws TwitterException {
		getTwitterProxy().updateStatus(tweet);
	}

	public void follow(long userId) throws TwitterException {
		getTwitterProxy().createFriendship(userId);
	}



	public boolean unfollow(long userId) {
		boolean result = false;
		try {
			getTwitterProxy().destroyFriendship(userId);
			result = true;

		} catch (TwitterException e) {
			logger.error("Error while unfollowing: " + userId + " Announcer: "
					+ screenName);
			ExceptionUtil.getInstance().handleError(e);
			result = false;
		}
		return result;
	}


	public void favorite(long statusId) throws Exception {
		try {
			getTwitterProxy().createFavorite(statusId);
		} catch (TwitterException e) {
			logger.error("Error while creating favorite: " + statusId
					+ " Announcer: " + screenName);
		}
	}

	public long reply(String message, long statusId) throws Exception {
		long replyStatus = -1;
		try {
			Status status = getTwitterProxy().updateStatus(new StatusUpdate("message").inReplyToStatusId(statusId));
			replyStatus =  status.getId();
		} catch (TwitterException e) {
			logger.error("Error while replying: " + statusId + " Announcer: "
					+ screenName);
		}
		return replyStatus;
	}

	public void retweet(long statusId) throws Exception {
		try {
			getTwitterProxy().retweetStatus(statusId);
		} catch (TwitterException e) {
			logger.error("Error while retweeting: " + statusId + " Announcer: "
					+ screenName);
		}
	}

	public void announce(long anId){
		//
		// Announcement an = announcementMgr.getAnnouncementById(anId);


		try {
			String tweet = "";

			Message msg = msgMgr.generateMessageForAnnouncement(anId);
			tweet = msg.getTextWithParametersAdded();
			announcementMgr.updateSetMessage(anId, tweet, msg.getId());
			logger.info(getScreenName() + " is tweeting :" + tweet);
			tweet(tweet);
		} catch (TwitterException e) {
			ExceptionUtil.getInstance().handleError(e);
		}
		catch (Exception e) {
			logger.error("Error", e);
		}

	}


	public void reply(long anId, long statusId) throws Exception {

		Announcement an = announcementMgr.getAnnouncementById(anId);

		Announcer customer = announcerMgr.getAnnouncer(an.getCustomerId());

		if (customer == null) {

			throw new Exception("Customer not found for announcement: "
					+ an.getId());
		}
		// Product product = productMgr.getProductById(an.getProductId());
		String cause = announcementMgr.getAnnouncementReasonStatusText(anId);

		// tweey check
		String customerName = customer.getName();
		String tweet = "";
		Message msg = msgMgr.generateMessageForAnnouncement(an.getId());
		tweet = msg.getTextWithParametersAdded();
		announcementMgr.updateSetMessage(an.getId(), tweet, msg.getId());
		//
		// logger.info(getScreenName() + " is following " + customerName +
		// " before replying...");
		// follow(an.getCustomerId());

		try {

			long tweetId = -1;
			logger.info(getScreenName() + " is replying " + customerName
					+ " : " + tweet + "\nBecause of: @" + customerName + ":"
					+ cause);
			tweetId = reply(tweet, statusId);
			announcementMgr.updateSetTweetId(anId, tweetId);

		} catch (TwitterException e) {
			ExceptionUtil.getInstance().handleError(e);
		}

	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		this.suspended = suspended;
	}

	/**
	 * @return the creationTime
	 */
	public long getCreationTime() {
		return creationTime;
	}

	public String getCreationTimeStr() {
		return util.timeToString(creationTime);
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(int following) {
		this.following = following;
	}

	public int getFollower() {
		return follower;
	}

	public void setFollower(int follower) {
		this.follower = follower;
	}

	/**
	 * @param creationTime
	 *            the creationTime to set
	 */
	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @return the training
	 */
	public boolean isTraining() {
		return training;
	}

	/**
	 * @param training
	 *            the training to set
	 */
	public void setTraining(boolean training) {
		this.training = training;
	}

	public boolean canFollowMoreFamousPeople() {
		return maxFamousPeople2Follow > famousPeopleFollowed;
	}

	/**
	 * @return the sesId
	 */
	public String getSesId() {
		return sesId;
	}

	/**
	 * @param sesId
	 *            the sesId to set
	 */
	public void setSesId(String sesId) {
		this.sesId = sesId;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken
	 *            the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the suspensionTime
	 */
	public long getSuspensionTime() {
		return suspensionTime;
	}

	/**
	 * @param suspensionTime
	 *            the suspensionTime to set
	 */
	public void setSuspensionTime(long suspensionTime) {
		this.suspensionTime = suspensionTime;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public List<Announcer> getFollowingList() {
		return followingList;
	}

	public void setFollowingList(List<Announcer> followingList) {
		this.followingList = followingList;
	}

	public int getResetTimeInSecs() {
		return resetTimeInSecs;
	}

	public void setResetTimeInSecs(int resetTimeInSecs) {
		this.resetTimeInSecs = resetTimeInSecs;
	}

}