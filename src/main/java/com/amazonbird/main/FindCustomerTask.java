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

package com.amazonbird.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;

import com.amazonaws.http.HttpResponse;
import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Product;
import com.amazonbird.db.data.Reason;
import com.amazonbird.util.Util;
import twitter4j.internal.http.HttpResponseEvent;

public class FindCustomerTask extends Thread {
	private static int THRESHOLD_FOR_TWEET_DATE_IN_HOURS = 24;

	private Announcement announcement;

	private long id = 0;
	static int threadCount = 0;
	private static long idIndex = 0;
	int timeoutInMins;

	boolean customerFound = false;

	private int THREAD_THRESHOLD_TO_REPORT = 150;

	private static Logger logger = Logger.getLogger(FindCustomerTask.class);

	TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
	Twitter twitter = new TwitterFactory().getInstance();
	Util util = Util.getInstance();
	private static int CUSTOMER_ANNOUNCE_AGAIN_MIN_PERIOD_IN_DAYS = 7;
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();

	private int penaltyInSeconds = 0;

	public FindCustomerTask(Announcement announcement, int timeoutInMins,
			int penaltyInSecs) {

		this.timeoutInMins = timeoutInMins;
		this.announcement = announcement;
		this.penaltyInSeconds = penaltyInSecs;
		//HttpResponseEvent evet = new HttpResponseEvent(null, null, null);
	}

	public FindCustomerTask(Announcement announcement, int timeoutInMins) {

		this.timeoutInMins = timeoutInMins;
		this.announcement = announcement;
	}

	public class ShutdownManager extends Thread {

		public void run() {

			try {
				sleep(timeoutInMins * 60 * 1000);

				Announcement an = announcementMgr
						.getAnnouncementById(announcement.getId());
				if (Announcement.STATUS_SEARCHING_FOR_CUSTOMER
						.equalsIgnoreCase(an.getStatus())) {

					an.setStatus(Announcement.STATUS_WAITING_FOR_CUSTOMER);
					announcementMgr.updateSetStatus(an.getId(), an.getStatus());
				}

				twitterStream.shutdown();

				threadCount--;

				if (threadCount > THREAD_THRESHOLD_TO_REPORT) {
					logger.info("-- Thread(" + id + "), count:" + threadCount);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void run() {

		threadCount++;
		idIndex++;
		id = idIndex;

		if (threadCount > THREAD_THRESHOLD_TO_REPORT) {
			logger.info("++ Thread(" + id + "), count:" + threadCount);
		}
		try {

			Reason reason = reasonMgr.getReasonById(announcement.getReasonId());
			if (reason == null) {
				throw new Exception("Reason null for announcement:"
						+ announcement.getId());
			}

			Product product = productMgr.getProductById(announcement
					.getProductId());
			if (product == null) {
				throw new Exception("Product null for announcement:"
						+ announcement.getId());
			}

			if (this.penaltyInSeconds > 0) {
				logger.info("Recently failed to find a customer for "
						+ reason.getValue() + "/" + product.getName()
						+ ". Waiting for " + this.penaltyInSeconds
						+ " seconds to process again");
				Thread.sleep(1000 * this.penaltyInSeconds);

			}

			ShutdownManager sm = new ShutdownManager();
			sm.start();

			Announcer announcer = announcerMgr.getAnnouncer(announcement
					.getAnnouncerId());
			if (announcer == null) {
				throw new Exception(
						"Can't find any results for random announcer data");
			}

			Query q = new Query();
			q.setQuery(reason.getValue() + " -http -https");
			q.setLang("en");
			q.setRpp(100);
			q.setResultType(Query.RECENT);
			// int page = 1;
			// q.setPage(page);
			QueryResult qr = twitter.search(q);
			List<Tweet> tweets = null;
			tweets = qr.getTweets();
			int i = 0;

			for (; i < tweets.size(); i++) {
				try {

					Tweet t = tweets.get(i);

					String tweet = t.getText();
					String userName = t.getFromUser();
					long userId = t.getFromUserId();

					validateTweet(tweet, t.getCreatedAt().getTime());
					validateCustomer(userId);

					//Announcer newAnnouncer = new Announcer();
					//newAnnouncer.setId(userId);
					//newAnnouncer.set
					Announcer  newAnnouncer = announcerMgr.getAnnouncer(userId);
					if(newAnnouncer == null){
						User user = twitter.showUser(userId);
						newAnnouncer = new Announcer();
						newAnnouncer.setId(userId);
						newAnnouncer.setScreenName(user.getScreenName());
						newAnnouncer.setAccessToken(null);
						newAnnouncer.setAccessTokenSecret(null);
						newAnnouncer.setPictureUrl(user.getProfileImageURL().toExternalForm());
						newAnnouncer.setConsumerKey(ConfigMgrImpl.getInstance().getConsumerKey());
						newAnnouncer.setConsumerSecret(ConfigMgrImpl.getInstance().getConsumerSecret());
						
						newAnnouncer.setDescription(user.getDescription());
						newAnnouncer.setLongName(user.getName());
						newAnnouncer.setLocation(user.getLocation());
						newAnnouncer.setUrl(user.getURL() == null ? null : user.getURL().toExternalForm());
					}
					customerFound(newAnnouncer, t.getId(), t.getText());

					// logger.info("Customer found: " + userName + ": " +
					// t.getText());
					break;
				} catch (Exception ex) {
					if (i == tweets.size()) {
						logger.info("Couldn't find a customer through "
								+ tweets.size() + " tweets...\n" + "Message: "
								+ ex.getMessage());
					}
				}

				if (!customerFound) {

					throw new Exception("Customer not found! "
							+ reason.getDescription());

				}
			}
		} catch (Exception ex) {

			logger.error(ex.getMessage());
			Announcement an = announcementMgr.getAnnouncementById(announcement
					.getId());

			an.setStatus(Announcement.STATUS_WAITING_FOR_CUSTOMER);
			announcementMgr.updateSetStatus(an.getId(), an.getStatus());
		}
	}

	public void validateCustomer(long customerId) throws Exception {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean proper = false;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement(AnnouncementMgrImpl.SELECT_ANNOUNCEMENTS
							+ " where customerid=? and " + "	(status = '"
							+ Announcement.STATUS_SENT + "'"
							+ " and timestampdiff(day,timesent,now()) < ? "
							+ " or " + " status = '"
							+ Announcement.STATUS_WAITING_FOR_ANNOUNCER + "'"
							+ " )  ");
			ps.setLong(1, customerId);
			ps.setInt(2, CUSTOMER_ANNOUNCE_AGAIN_MIN_PERIOD_IN_DAYS);

			rs = ps.executeQuery();
			proper = !rs.next();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());

		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}

		if (!proper) {
			throw new Exception("This customer has been tweeted recently: "
					+ customerId);
		}
	}

	public boolean validateTweet(String tweet, long date) throws Exception {

		if (!Util.stringIsValid(tweet) || tweet.contains("http://")) {
			throw new Exception(
					"Owner of this tweet is not possible customer: " + tweet);
		}
		if (tweet.startsWith("RT")) {
			throw new Exception("Retweet..." + tweet);
		}
		if (new Date().getTime() - date > THRESHOLD_FOR_TWEET_DATE_IN_HOURS * 60 * 60 * 1000) {
			throw new Exception("Too late for reply: "
					+ util.dateToString(new Date(date)));
		}

		return true;

	}

	public void customerFound(Announcer announcer, long statusId,
			String text) {
		announcerMgr.addAnnouncer(announcer);

		Announcement an = announcementMgr.getAnnouncementById(announcement
				.getId());
		an.setCustomerId(id);
		an.setStatus(Announcement.STATUS_WAITING_FOR_ANNOUNCER);
		long anId = an.getId();
		long customerId = id;

		announcementMgr.updateSetCustomer(anId, customerId);
		announcementMgr.updateSetStatus(anId, an.getStatus());
		customerFound = true;

		announcementMgr.addStatusForAnnouncement(an.getId(), statusId, text);

	}

	public void addCustomerFailed(Exception ex) {

		Announcement an = announcementMgr.getAnnouncementById(announcement
				.getId());
		logger.info(ex.getMessage());
		an.setStatus(Announcement.STATUS_WAITING_FOR_CUSTOMER);
		announcementMgr.updateSetStatus(an.getId(), an.getStatus());

	}

}
