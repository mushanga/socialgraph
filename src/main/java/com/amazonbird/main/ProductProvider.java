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
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Product;
import com.amazonbird.util.Util;

public class ProductProvider implements Runnable {

	private static Logger logger = Logger.getLogger(ProductProvider.class);

	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();

	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();

	Util util = Util.getInstance();

	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	// private static String GET_LEAST_ANNOUNCED_PRODUCT = LOAD_PRODUCTS+" where

	ArrayList<Product> productList = new ArrayList<Product>();
	HashMap<Long, Product> productMap = new HashMap<Long, Product>();

	public static int TASK_PERIOD_IN_MILSECS = 1000;

	@Override
	public void run() {
		//Thread fixProductPricesThread = new FixProductPricesThread();
		//fixProductPricesThread.start();

		while (true) {
			long startTime = System.currentTimeMillis();
			try {
				removeInactiveProductFromQueue();
				Product product = findProduct();
				if (product != null) {
					ArrayList<Announcement> anList = announcementMgr.getAnnouncementsByStatus(Announcement.STATUS_INITIALIZED, 0, 1);

					if (Util.isListValid(anList)) {
						Announcement an = anList.get(0);
						long productId = product.getId();
						an.setProductId(productId);
						announcementMgr.updateSetProduct(an.getId(), productId);
						an.setStatus(Announcement.STATUS_WAITING_FOR_CUSTOMER);
						announcementMgr.updateSetStatus(an.getId(), an.getStatus());
					}
				}
			} catch (Exception ex) {
				logger.error("Announce Task: Main Loop", ex);
			} finally {
				util.sleepIfNeeded(startTime, TASK_PERIOD_IN_MILSECS);
			}
		}
	}

	private void removeInactiveProductFromQueue(){
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement("update announcement set status = ? where status != ? and status !=? and productid in (select id from product where active != true) ");
			
			ps.setString(1, Announcement.STATUS_ERROR);
			ps.setString(2, Announcement.STATUS_SENT);
			ps.setString(3, Announcement.STATUS_ERROR);
			ps.executeUpdate();

			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			if (ps != null) {
				logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
			} else {
				logger.error(DBConstants.QUERY_EXECUTION_FAIL, ex);
			}

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}	
		
	}
	
	private Product findProduct() {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		Product product = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ProductMgrImpl.LOAD_PRODUCTS + " where active is true and id in (select productid from reasonproduct) " +
					" and id not in " +
						" ( select productid from ( " + AnnouncementMgrImpl.SELECT_ANNOUNCEMENTS + "	where status != ? and status !=? ) as anc )" +
					" limit 1 ");
			ps.setString(1, Announcement.STATUS_SENT);
			ps.setString(2, Announcement.STATUS_ERROR);
			rs = ps.executeQuery();

			if (rs.next()) {
				product = new Product();
				product.getDataFromResultSet(rs);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			if (ps != null) {
				logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
			} else {
				logger.error(DBConstants.QUERY_EXECUTION_FAIL, ex);
			}

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return product;
	}
}
