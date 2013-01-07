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

package com.amazonbird.announce;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Comment;
import com.amazonbird.util.Util;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class AnnouncerMgrImpl {

	private static final int FIXED_ANNOUNCER_COUNT = 200;
	Util util = Util.getInstance();
	private static Logger logger = Logger.getLogger(AnnouncerMgrImpl.class);

	private static AnnouncerMgrImpl instance = new AnnouncerMgrImpl();


	private AnnouncerMgrImpl() {

	}

	public static final String CONFIG_ACTION_PERIOD = "action-period";
	public static final String CONFIG_RETWEET_FAVORITE_PERIOD = "retweet-favorite-period";
	public static final String CONFIG_ANNOUNCE_PERIOD = "announce-period";
	public static final String CONFIG_FOLLOW_PERIOD = "follow-period";

	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	public static final String LOAD_ANNOUNCER = "select * from systemtwitteruser";
	private static String ADD_ANNOUNCER = " insert into systemtwitteruser(id,screenName,consumerKey,consumerSecret,accessToken,accessTokenSecret, name, surname, email, password, maxFamousAccount2Follow,authtoken,sesid, description, url, longName, location, pictureUrl ) values "
			+ " (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, cast( rand() * 7 + 3 as unsigned), ?, ?, ?, ?, ?, ?, ?) ";
	private static String UPDATE_ANNOUNCER = " update systemtwitteruser set consumerKey = ?,consumerSecret = ?,accessToken = ?,accessTokenSecret = ?,suspended=?,training=?,authtoken=?,sesid=?, description = ?, url = ?, longName = ?, location = ?, pictureUrl = ?, screenName = ? where id = ?";
	private static String DELETE_ANNOUNCER = " delete from systemtwitteruser where id = ? ";

	public static final String ANNOUNCER_COUNT = " select count(*) from systemtwitteruser ";

	public void removeAnnouncer(long id) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(DELETE_ANNOUNCER);
			ps.setLong(1, id);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	public boolean announcerIsvalid(Announcer announcer) {
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(announcer.getConsumerKey(),
					announcer.getConsumerSecret());
			twitter.setOAuthAccessToken(new AccessToken(announcer
					.getAccessToken(), announcer.getAccessTokenSecret()));

		} catch (Exception ex) {
			return false;
		}
		return true;

	}

	public Announcer addAnnouncer(Announcer announcer) {
		Connection connection = null;
		PreparedStatement ps = null;

		if (!announcerIsvalid(announcer)) {
			// return;
		}
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ADD_ANNOUNCER,
					Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, announcer.getId());
			ps.setString(2, announcer.getScreenName());
			ps.setString(3, announcer.getConsumerKey());
			ps.setString(4, announcer.getConsumerSecret());
			ps.setString(5, announcer.getAccessToken());
			ps.setString(6, announcer.getAccessTokenSecret());
			ps.setString(7, announcer.getName());
			ps.setString(8, announcer.getSurname());
			ps.setString(9, announcer.getEmail());
			ps.setString(10, announcer.getPassword());
			ps.setString(11, announcer.getAuthToken());
			ps.setString(12, announcer.getSesId());
			
			ps.setString(13, announcer.getDescription());
			ps.setString(14, "");
			ps.setString(15, "");
			ps.setString(16, announcer.getLocation());
			ps.setString(17, announcer.getPictureUrl());
			
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
		    rs.next();
		    int id = rs.getInt(1);
		    announcer.setId(id);
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {
			logger.warn("DB: Announcer already exists - Announcer:"
					+ announcer.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return announcer;
	}

	public void updateAnnouncer(Announcer announcer) {
		Connection connection = null;
		PreparedStatement ps = null;

		if (!announcerIsvalid(announcer)) {
			// return;
		}
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_ANNOUNCER);
			ps.setString(1, announcer.getConsumerKey());
			ps.setString(2, announcer.getConsumerSecret());
			ps.setString(3, announcer.getAccessToken());
			ps.setString(4, announcer.getAccessTokenSecret());
			ps.setBoolean(5, announcer.isSuspended());
			ps.setBoolean(6, announcer.isTraining());
			ps.setString(7, announcer.getAuthToken());
			ps.setString(8, announcer.getSesId());
			
			ps.setString(9, announcer.getDescription());
			ps.setString(10, "");
			ps.setString(11, "");
			ps.setString(12, announcer.getLocation());
			
			ps.setString(13, announcer.getPictureUrl());
			ps.setString(14, announcer.getScreenName());
			
			ps.setLong(15, announcer.getId());
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}


	public List<Announcer> getActiveAnnouncers() {
		List<Announcer> announcerList = new ArrayList<Announcer>();
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("select * from systemtwitteruser where suspended = 0 order by (select timesent from announcement where announcerid = systemtwitteruser.id order by timesent desc limit 1) desc, creationtime asc");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Announcer announcer = new Announcer();
				announcer.getDataFromResultSet(rs);
				announcerList.add(announcer);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return announcerList;
	}

	public ArrayList<Long> getActiveAnnouncersIDs() {
		ArrayList<Long> announcerList = new ArrayList<Long>();
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("select * from systemtwitteruser where suspended = 0 order by (select timesent from announcement where announcerid = systemtwitteruser.id order by timesent desc limit 1) desc, creationtime asc");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				announcerList.add(rs.getLong("id"));
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return announcerList;
	}

	
	public static AnnouncerMgrImpl getInstance() {
		return instance;
	}


	private void setAnnouncerSuspended(long announcerId, boolean suspended) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("update systemtwitteruser set suspended = ?, suspensiontime = "
							+ ((suspended) ? " NOW() " : " null ")
							+ " where id = ?");
			ps.setBoolean(1, suspended);
			ps.setLong(2, announcerId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
			String downOrResurrected = (suspended) ? "down" : "resurrected";
			logger.info("**********************"
					+ getAnnouncer(announcerId).getScreenName() + " is "
					+ downOrResurrected + "!" + "**********************");

		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void setAnnouncerSuspended(long announcerId) {
		setAnnouncerSuspended(announcerId, true);
	}

	public void setAnnouncerUnsuspended(long announcerId) {
		setAnnouncerSuspended(announcerId, false);
	}

	public List<Announcer> getAllAnnouncers() {
		List<Announcer> announcerList = new ArrayList<Announcer>();
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement(LOAD_ANNOUNCER);
							
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Announcer announcer = new Announcer();
				announcer.getDataFromResultSet(rs);
				announcerList.add(announcer);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return announcerList;
	}


	public Announcer getAnnouncer(long id) {
		Announcer announcer = null;
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("select * from systemtwitteruser where id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				announcer = new Announcer();
				announcer.getDataFromResultSet(rs);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return announcer;
	}


	public String getCookie(long id) {
		Connection connection = null;
		PreparedStatement ps = null;
		String cookie=null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("select cookie from systemtwitteruser where id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				cookie = rs.getString("cookie");
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return cookie;
	}

	public void setCookie(long id,String cookie) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("update systemtwitteruser set cookie = ? where id = ?");
			ps.setString(1, cookie);
			ps.setLong(2, id);
			ps.executeUpdate();
		
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	
	public Announcer getAnnouncerByScreenName(String screenName) {
		Announcer announcer = null;
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection
					.prepareStatement("select * from systemtwitteruser where screenName = ?");
			ps.setString(1, screenName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				announcer = new Announcer();
				announcer.getDataFromResultSet(rs);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return announcer;
	}

	public boolean containsAnnouncer(long id) {
		return getAnnouncer(id) == null ? false : true;
	}

	
	
	public static void main(String[] args) {
		AnnouncerMgrImpl announcementMgrImpl = new AnnouncerMgrImpl();
	}


}
