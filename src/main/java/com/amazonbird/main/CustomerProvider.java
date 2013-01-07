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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.TwitterException;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Reason;
import com.amazonbird.util.Util;

public class CustomerProvider implements Runnable{

	private static String FIND_ANNOUNCEMENT_WAITING_FOR_CUSTOMER = AnnouncementMgrImpl.SELECT_ANNOUNCEMENTS+ " where status=? " +
			" order by timemodified asc limit 1 ";

	Util util = Util.getInstance(); 

	private static Logger logger = Logger.getLogger(CustomerProvider.class);

	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	

	public static int TASK_PERIOD_IN_MILSECS = 1000;

	public static int TIMEOUT_FOR_FIND_CUSTOMER_IN_MINUTES = 1;   
	public static int SLEEP_TIME_FOR_NO_ANNOUNCER_ERROR_IN_MINUTES = 1;   
	
	@Override
	public void run() {}

	public Announcement findAnnouncement(){
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Announcement an = null;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(FIND_ANNOUNCEMENT_WAITING_FOR_CUSTOMER);
			ps.setString(1, Announcement.STATUS_WAITING_FOR_CUSTOMER);
			
			rs = ps.executeQuery();

			if (rs.next()) {
				an = new Announcement();
				an.getDataFromResultSet(rs);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return an;

	
	}

	
}