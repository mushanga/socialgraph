package com.amazonbird.route;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Click;

public class ClickMgrImpl {

	private static String ADD_CLICK = "insert into click(announcementid,srcaddress,hostname) values (?,?,?)";
	private static String REMOVE_CLICK = "delete from click where srcaddress = ? ";
	private static String CHECK_CLICK = "select * from click where announcementid <> ? AND srcaddress = ? ";
	private static String ADD_BLACK = "insert into blacklist(srcaddress,hostname) values (?,?)";
	private static String CHECK_BLACK = "select * from blacklist where srcaddress = ?";

	private static String CLICK_COUNT_FOR_PRODUCT = "select count(*) from (select distinct announcementid from click where announcementid in (select id from announcement where productid = ?) " + " and timestampdiff(hour, timeclicked,now())< ? ) as clickforproduct ";

	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	private static Logger logger = Logger.getLogger(ClickMgrImpl.class);

	private static ClickMgrImpl instance = new ClickMgrImpl();

	public static ClickMgrImpl getInstance() {
		return instance;
	}

	public int getClickCountForProduct(long id) {
		return getClickCountForProduct(id, Integer.MAX_VALUE);
	}

	public int getClickCountForProduct(long id, int forHour) {

		int count = 0;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(CLICK_COUNT_FOR_PRODUCT);
			ps.setLong(1, id);
			ps.setInt(2, forHour);
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

	// This method is not thread safe. However one threaded service executer
	// makes it safe
	public boolean addClick(Click click) {
		Connection connection = null;
		PreparedStatement addSt = null;
		PreparedStatement checkClickSt = null;
		PreparedStatement removeSt = null;
		PreparedStatement blackSt = null;
		PreparedStatement checkBlackSt = null;

		Statement[] allSTs = new Statement[] { checkClickSt, addSt, removeSt, blackSt, checkBlackSt };

		ResultSet clickRS = null;
		ResultSet blackRS = null;

		ResultSet[] allRSs = new ResultSet[] { clickRS, blackRS };

		try {
			connection = dbMgr.getConnection();

			checkBlackSt = connection.prepareStatement(CHECK_BLACK);
			checkBlackSt.setString(1, click.getSrcAddress());
			blackRS = checkBlackSt.executeQuery();
			if (blackRS.next()) {
				// This source address in black list. return
				dbMgr.closeResources(connection, allSTs, allRSs);
				return false;
			}

			checkClickSt = connection.prepareStatement(CHECK_CLICK);
			checkClickSt.setLong(1, click.getAnnouncementId());
			checkClickSt.setString(2, click.getSrcAddress());
			clickRS = checkClickSt.executeQuery();

			if (clickRS.next()) {
				// This source already clicked another announcement. Put it into
				// blacklist and remove other

				removeSt = connection.prepareStatement(REMOVE_CLICK);
				removeSt.setString(1, click.getSrcAddress());
				removeSt.executeUpdate();

				blackSt = connection.prepareStatement(ADD_BLACK);
				blackSt.setString(1, click.getSrcAddress());
				blackSt.setString(2, clickRS.getString(5));
				blackSt.executeUpdate();

				dbMgr.closeResources(connection, allSTs, allRSs);
				return false;
			}

			long clickId = -1;
			try {
				click.retrieveHostname();
			} catch (Exception e) {
				logger.info("Unable to retrieve hostanem for " + click.getSrcAddress());
			}
			addSt = connection.prepareStatement(ADD_CLICK, Statement.RETURN_GENERATED_KEYS);
			addSt.setLong(1, click.getAnnouncementId());
			addSt.setString(2, click.getSrcAddress());
			addSt.setString(3, click.getHostname());
			addSt.executeUpdate();
			try {

				ResultSet rs = addSt.getGeneratedKeys();
				if (rs.next()) {

					clickId = rs.getInt(1);
				}
			} catch (Exception ex) {

			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + addSt.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + addSt.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, allSTs, allRSs);
		}
		return true;
	}

}
