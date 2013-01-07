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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.AnnouncementListCriteria;
import com.amazonbird.util.Util;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class AnnouncementMgrImpl {

	private static Logger logger = Logger.getLogger(AnnouncementMgrImpl.class);

	private static AnnouncementMgrImpl instance = new AnnouncementMgrImpl();

	private AnnouncementMgrImpl() {

	}

	Util util = Util.getInstance();

	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	public static String SELECT_ANNOUNCEMENTS = "select * from announcement";
	public static String COUNT_ANNOUNCEMENTS = "select count(*) from announcement";
	public static String ADD_ANNOUNCEMENT = " insert into announcement(customerid,announcerid,amazontag,timesent,clickcount,message,reasonid,timemodified,status,productid) " + " values " + " (?,?,?,?,?,?,?,?,?,?) ";
	public static String UPDATE_ANNOUNCEMENT = " update announcement set " + "				customerid =?,announcerid =?,amazontag=?,timesent=?,clickcount=?,message=?,reasonid=?,timemodified=NOW(),status=?,productid=? " + "  where id=? ";
	public static String UPDATE_SET_MESSAGE = " update announcement set " + " message=?, messageid=?   where id=? ";
	public static String UPDATE_SET_STATUS = " update announcement set " + " status=?, timemodified=now()   where id=? ";
	public static String UPDATE_SET_TWEET_ID = " update announcement set " + " tweetid=?   where id=? ";
	public static String UPDATE_SET_ANNOUNCER = " update announcement set " + " announcerid=?   where id=? ";
	public static String UPDATE_SET_CUSTOMER = " update announcement set " + " customerid=?   where id=? ";
	public static String UPDATE_SET_PRODUCT = " update announcement set " + " productid=?   where id=? ";
	public static String UPDATE_SET_REASON = " update announcement set " + " reasonid=?   where id=? ";
	public static String UPDATE_SET_OLD_ANNOUNCEMENTS_TO_ERROR = " update announcement set message='Timeout', status='"+Announcement.STATUS_ERROR+"'   where status='"+Announcement.STATUS_WAITING_FOR_ANNOUNCER+"' and timestampdiff(hour,timemodified,now()) > ? ";
	public static String UPDATE_SET_CLICK_COUNT = " update announcement set " + " clickcount=?   where id=? ";
	private static String CHANGE_STATUS = " update announcement set status=?  where status=? ";
	public static String SET_STATUS_SENT = " update announcement set timesent=now(), status=?  where id=? ";

	public static String SELECT_ANNOUNCEMENT_BY_ID = SELECT_ANNOUNCEMENTS + " where id = ?";
	public static String DELETE_ANNOUNCEMENT = " delete from announcement where id = ? ";

	public static String GET_REASON_STATUS_FOR_ANNOUNCEMENT = " select statusid from reasonannouncement where announcementid = ? ";

	public static String GET_REASON_STATUS_TEXT_FOR_ANNOUNCEMENT = " select tweet from reasonannouncement where announcementid = ? ";

	public static String ADD_REASON_STATUS_FOR_ANNOUNCEMENT = " insert into reasonannouncement(announcementid, statusid, tweet ) values (?,?,?) " + " on duplicate key update statusid=?, tweet=? ";


	public static String UPDATE_SET_STATUS_WHERE_STATUS = " update announcement set  status=?, timemodified=now()   where status=? ";
	
	public Announcement getAnnouncementById(long id) {

		AnnouncementListCriteria alc = new AnnouncementListCriteria();
		alc.getSelectedIds().add(id);

		ArrayList<Announcement> announcements = getAnnouncements(alc);
		if (Util.isListValid(announcements)) {
			return announcements.get(0);
		} else {
			return null;
		}
	}

	public Announcement getAnnouncementById(String id) {
		return getAnnouncementById(Long.valueOf(id));
	}

	public ArrayList<Announcement> getAnnouncements(AnnouncementListCriteria alc) {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		ArrayList<Announcement> announcementList = new ArrayList<Announcement>();

		try {
			connection = dbMgr.getConnection();

			String where = (alc.sqlHasCondition()) ? " where " + alc.sqlGetConditions() : "";
			ps = connection.prepareStatement(SELECT_ANNOUNCEMENTS + where + " order by" + alc.sqlGetOrderBy() + " limit " + alc.sqlGetOffsetAndLimit());
			rs = ps.executeQuery();

			while (rs.next()) {
				Announcement announcement = new Announcement();
				announcement.getDataFromResultSet(rs);
				announcementList.add(announcement);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return announcementList;

	}

	public int getAnnouncementCount(AnnouncementListCriteria alc) {

		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;

		try {
			connection = dbMgr.getConnection();

			String where = (alc.sqlHasCondition()) ? " where " + alc.sqlGetConditions() : "";
			ps = connection.prepareStatement("select count(*) from (" + SELECT_ANNOUNCEMENTS + where + " order by" + alc.sqlGetOrderBy() + " limit " + alc.sqlGetOffsetAndLimit() + " ) as announcementlist");
			rs = ps.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return count;

	}
	public int getAnnouncementCount() {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int count = 0;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(COUNT_ANNOUNCEMENTS);
			rs = ps.executeQuery();

			if (rs.next()) {
				count = rs.getInt(1);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return count;

	}

	public void addStatusForAnnouncement(long anId, long statusId, String text) {

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(ADD_REASON_STATUS_FOR_ANNOUNCEMENT);
			ps.setLong(1, anId);
			ps.setLong(2, statusId);
			ps.setString(3, text);
			ps.setLong(4, statusId);
			ps.setString(5, text);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {
			logger.warn("DB: Status Id already exists for announcement");
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public long getAnnouncementReasonStatus(long anId) {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(GET_REASON_STATUS_FOR_ANNOUNCEMENT);
			ps.setLong(1, anId);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return 0;
	}
	public String getAnnouncementReasonStatusText(long anId) {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(GET_REASON_STATUS_TEXT_FOR_ANNOUNCEMENT);
			ps.setLong(1, anId);
			rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString(1);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return null;
	}

	public void removeAnnouncement(long id) {

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(DELETE_ANNOUNCEMENT);
			ps.setLong(1, id);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void removeAnnouncement(String id) {

		removeAnnouncement(Long.valueOf(id));

	}

	public long addAnnouncement(Announcement announcement) {
		Connection connection = null;
		PreparedStatement ps = null;

		long announcementId = -1;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(ADD_ANNOUNCEMENT, Statement.RETURN_GENERATED_KEYS);
			ps.setLong(1, announcement.getCustomerId());
			ps.setLong(2, announcement.getAnnouncerId());
			ps.setString(3, announcement.getAmazonTag());
			ps.setTimestamp(4, announcement.getTimeSent());
			ps.setInt(5, announcement.getClickCount());
			ps.setString(6, announcement.getMessage());
			ps.setLong(7, announcement.getReasonId());
			ps.setTimestamp(8, announcement.getTimeModified());
			ps.setString(9, announcement.getStatus());
			ps.setLong(10, announcement.getProductId());

			ps.executeUpdate();
			try {

				ResultSet rs = ps.getGeneratedKeys();
				if (rs.next()) {

					announcementId = rs.getInt(1);
				}
			} catch (Exception ex) {

			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {
			logger.warn("DB: Announcement already exists - Announcement:" + announcement.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

		return announcementId;
	}

	public static AnnouncementMgrImpl getInstance() {
		return instance;
	}

	public void setAnnouncementsToErrorOlderThan(int thresholdInHours) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(UPDATE_SET_OLD_ANNOUNCEMENTS_TO_ERROR);
			ps.setInt(1, thresholdInHours);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public ArrayList<Announcement> getAnnouncementsByStatus(String status, int offset, int count) {
		return getAnnouncementsByStatusIsOrNot(status, true, offset, count);
	}

	public ArrayList<Announcement> getAnnouncementsByStatusNot(String status, int offset, int count) {
		return getAnnouncementsByStatusIsOrNot(status, false, offset, count);
	}

	private ArrayList<Announcement> getAnnouncementsByStatusIsOrNot(String status, boolean is, int offset, int count) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Announcement> anList = new ArrayList<Announcement>();
		String operator = "=";
		if (!is) {
			operator = "!=";
		}

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_ANNOUNCEMENTS + " where status " + operator + " ? order by timemodified asc limit ?,? ");
			ps.setString(1, status);
			ps.setInt(2, offset);
			ps.setInt(3, count);

			rs = ps.executeQuery();

			while (rs.next()) {
				Announcement announcement = new Announcement();
				announcement.getDataFromResultSet(rs);
				anList.add(announcement);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}

		return anList;
	}

	public ArrayList<Announcement> getAnnouncementsInProgress(int offset, int count) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Announcement> anList = new ArrayList<Announcement>();

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_ANNOUNCEMENTS + " where status != ? and status!= ? limit ?,? ");
			ps.setString(1, Announcement.STATUS_ERROR);
			ps.setString(2, Announcement.STATUS_SENT);
			ps.setInt(3, offset);
			ps.setInt(4, count);

			rs = ps.executeQuery();

			while (rs.next()) {
				Announcement announcement = new Announcement();
				announcement.getDataFromResultSet(rs);
				anList.add(announcement);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}

		return anList;
	}

	public void setSent(long id) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(SET_STATUS_SENT);
			ps.setString(1, Announcement.STATUS_SENT);
			ps.setLong(2, id);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void changeStatuses(String statusSrc, String statusTrg) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(CHANGE_STATUS);

			ps.setString(1, statusTrg);
			ps.setString(2, statusSrc);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetMessage(long anId, String message, long msgId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_MESSAGE);
			ps.setString(1, message);
			ps.setLong(2, msgId);
			ps.setLong(3, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetAnnouncer(long anId, long announcerId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_ANNOUNCER);
			ps.setLong(1, announcerId);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetStatus(long anId, String status) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_STATUS);
			ps.setString(1, status);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetStatusWhereStatus(String fromStatus, String toStatus) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_STATUS_WHERE_STATUS);
			ps.setString(1, toStatus);
			ps.setString(2, fromStatus);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetCustomer(long anId, long customerId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_CUSTOMER);
			ps.setLong(1, customerId);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetProduct(long anId, long productId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_PRODUCT);
			ps.setLong(1, productId);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetReason(long anId, long reasonId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_REASON);
			ps.setLong(1, reasonId);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public void updateSetTweetId(long anId, long tweetId) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_SET_TWEET_ID);
			ps.setLong(1, tweetId);
			ps.setLong(2, anId);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	public Announcement getLastAnnouncementForAnnouncer(long announcerId) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Announcement announcement = null;

		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(SELECT_ANNOUNCEMENTS + " where announcerid =? and status = ? order by timesent desc limit 1 ");
			ps.setLong(1, announcerId);
			ps.setString(2, Announcement.STATUS_SENT);
			rs = ps.executeQuery();

			while (rs.next()) {
				announcement = new Announcement();
				announcement.getDataFromResultSet(rs);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return announcement;

	}

	public int getAnnouncementCountForProduct(long productId) {

		AnnouncementListCriteria alc = new AnnouncementListCriteria();
		ArrayList<Long> productIds = new ArrayList<Long>();
		productIds.add(productId);
		alc.setProductIds(productIds);
		alc.setSelectedStatuses(new ArrayList<String>(Arrays.asList((new String[] { Announcement.STATUS_SENT }))));

		return getAnnouncementCount(alc);

	}

	public int getAnnouncementCountForProduct(long productId, int forHour) {

		AnnouncementListCriteria alc = new AnnouncementListCriteria();
		ArrayList<Long> productIds = new ArrayList<Long>();
		productIds.add(productId);
		alc.setProductIds(productIds);
		alc.setSelectedStatuses(new ArrayList<String>(Arrays.asList((new String[] { Announcement.STATUS_SENT }))));
		alc.getCustomConditions().add(" (timestampdiff(hour,timesent,now()) < "+forHour+")");
		return getAnnouncementCount(alc);

	}

}
