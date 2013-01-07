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
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Product;
import com.amazonbird.db.data.Reason;
import com.amazonbird.util.Util;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class ReasonMgrImpl {
Util util = Util.getInstance();	
	private static Logger logger = Logger.getLogger(ReasonMgrImpl.class);

	private static ReasonMgrImpl instance = new ReasonMgrImpl();

	private ReasonMgrImpl() {

	}

	

	
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	private static String SELECT_REASONS = "select * from reason";
	private static String ADD_REASON = " insert into reason(type,value) values " + " (?,?) ";
	private static String SELECT_REASON_BY_ID = SELECT_REASONS+" where id = ?";
	private static String SELECT_REASON_BY_TYPE_AND_VALUE = SELECT_REASONS+" where type = ? and value=? ";
	private static String DELETE_REASON = " delete from reason where id = ? ";
	

	private static String GET_REASONS_FOR_PRODUCT = "select * from reasonproduct where productid = ? ";
	private static String REMOVE_REASONS_FROM_PRODUCT = " delete from reasonproduct where productid = ? ";
	private static String ADD_REASON_TO_PRODUCT = " insert into reasonproduct(productid,reasonid) values (?,?) ";
	
	public Reason getReasonById(long id) {

		Reason reason = null;
		try{
			reason = selectReasons(" where id="+id, "").get(0);
		}catch(Exception ex){
			
		}
		return reason;
	
	}

	private ArrayList<Reason> selectReasons(String where, String orderBy){


		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Reason> reasonList = new ArrayList<Reason>();
		String whereStr = (Util.stringIsValid(where))?where:"";
		String orderbyStr = (Util.stringIsValid(orderBy))?orderBy:"";
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_REASONS+" "+whereStr+" "+orderbyStr);
			rs = ps.executeQuery();

			while (rs.next()) {
				Reason reason = new Reason();
				reason.getDataFromResultSet(rs);
				reasonList.add(reason);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return reasonList;

	
	}
	public Reason getReasonById(String id) {
		return getReasonById(Long.valueOf(id));
	}

	public Reason getReasonByTypeAndValue(int type, String value) {
		Reason reason = null;
		try{
			value.replace("'", "\'");
			reason = selectReasons("where type="+type+" and value ='"+value+"' ","").get(0);
		}catch(Exception ex){
			
		}
		return reason;
	}

	public void setReasonForProduct(Reason reason, long productId) {
		ArrayList<Reason> reasonList = new ArrayList<Reason>();
			
		reasonList.add(reason);
		setReasonsForProduct(reasonList, productId);
	}

	public void setReasonsForProduct(ArrayList<Reason> reasonList, long productId) {
		
		for (int i=0; i<reasonList.size(); i++) {
			Reason reason = reasonList.get(i);
			Reason existing = getReasonByTypeAndValue(reason.getType(), reason.getValue());
			
			if (existing == null) {
				reason.setId(addReason(reason));
			} else {
				reason.setId(existing.getId());
			}
		}
		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(REMOVE_REASONS_FROM_PRODUCT);
			ps.setLong(1, productId);

			ps.executeUpdate();
			
			
			for (int i=0; i<reasonList.size(); i++) {
				Reason reason = reasonList.get(i);

				ps = connection.prepareStatement(ADD_REASON_TO_PRODUCT);
				ps.setLong(1, productId);
				ps.setLong(2, reason.getId());

				ps.executeUpdate();
			}
		
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {
			logger.warn("DB: Reason already exists - Exception:" + e.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	public ArrayList<Reason> getReasonsForProduct(long productId) {
		
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Reason> reasonList = new ArrayList<Reason>();
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(GET_REASONS_FOR_PRODUCT);
			ps.setLong(1, productId);
			rs = ps.executeQuery();

			while (rs.next()) {
				long reasonId = rs.getLong("reasonid");
				
				reasonList.add(getReasonById(reasonId));

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return reasonList;
	}

	
	
	public long addReason(Reason reason) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		long newId = -1;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ADD_REASON,Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, reason.getType());
			ps.setString(2, reason.getValue());

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				newId=rs.getInt(1);
			}
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {
			logger.warn("DB: Reason already exists - Reason:" + reason.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return newId;

	}


	public static ReasonMgrImpl getInstance() {
		return instance;
	}

	public ArrayList<Reason> getAllReasons() {
		return selectReasons("", "");
	}

}
