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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Product;
import com.amazonbird.util.Util;

public class AnnouncerProvider implements Runnable{

	private static Logger logger = Logger.getLogger(AnnouncerProvider.class);


	public static int TASK_PERIOD_IN_MILSECS = 1000;
	
	Util util = Util.getInstance(); 
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	
	ArrayList<Product> productList = new ArrayList<Product>();
	HashMap<Long, Product> productMap = new HashMap<Long, Product>();
	@Override
	public void run() {

		while(true){
			long startTime = System.currentTimeMillis();
			
			try{
			
				// logger.info("Announcer found: "+ancr.getScreenName());
				ArrayList<Announcement> anList = announcementMgr.getAnnouncementsByStatus(Announcement.STATUS_WAITING_FOR_ANNOUNCER, 0, 1);
				if (Util.isListValid(anList)) {
					Announcement an = anList.get(0);
					long anId = an.getId();

					Announcer ancr = findAnnouncerForProduct(an.getProductId());

					if (ancr != null) {
						// logger.info("Announcement found to send: "+an);
						an.setAnnouncerId(ancr.getId());
						an.setStatus(Announcement.STATUS_READY);

						announcementMgr.updateSetAnnouncer(anId, ancr.getId());
						announcementMgr.updateSetStatus(anId, an.getStatus());
					}
					// an = announcementMgr.getAnnouncementById(an.getId());

					// logger.info("Announcement is ready to send: "+an);
				}
				
			}catch(Exception ex){
				
				logger.error("Announce Task: Main Loop", ex);
				
			}finally{

				util.sleepIfNeeded(startTime, TASK_PERIOD_IN_MILSECS);
			}
			
		}
		
		
	}
	private Announcer findAnnouncerForProduct(long productId) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;


		Announcer announcer = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(AnnouncerMgrImpl.LOAD_ANNOUNCER + " where id not in " +
											" (select announcerid from ( " + AnnouncementMgrImpl.SELECT_ANNOUNCEMENTS+
											"	where timestampdiff(minute,timesent,now()) < ? or status=? or status=? ) "+
											"   as anc) " +
											" and suspended != true and id in (select announcerid from product where id = ? ) limit 1 ");
			
		//	ps.setInt(1, announcerMgr.getAnnouncePeriodInMinutes());
			ps.setString(2, Announcement.STATUS_READY);
			ps.setString(3,  Announcement.STATUS_SENDING);
			ps.setLong(4, productId);
			//ps.setInt(4,  FOLLOWER_THRESHOLD_TO_ANNOUNCE);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				announcer = new Announcer();
				announcer.getDataFromResultSet(rs);				
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return announcer;
	}
	
	public void feedAnnouncer(){
		Connection connection = null;
		PreparedStatement ps = null;


		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement("update announcer set training = 0 where suspended = 0 and training = 1 and id in (select id from announcer where traning = 1 and suspended = 0 limit (200 - (select count(*) from announcer where training = 0 and suspended = 0)))");
			
			
			//ps.setInt(4,  FOLLOWER_THRESHOLD_TO_ANNOUNCE);
			
			ps.executeUpdate();
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}
	
	
}