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
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Message;
import com.amazonbird.db.data.Product;
import com.amazonbird.db.data.ProductMessage;
import com.amazonbird.util.Util;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class MessageMgrImpl {

	
	private static Logger logger = Logger.getLogger(MessageMgrImpl.class);
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	ShortLinkCacheMgrImpl shortLinkCacheMgrImpl = ShortLinkCacheMgrImpl.getInstance();
	private static MessageMgrImpl instance = new MessageMgrImpl();

	private MessageMgrImpl() {

	}

	Util util = Util.getInstance();
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	public static String SELECT_MESSAGES = " select * from message ";
	public static String ADD_MESSAGE = " insert into message(text) values " + " (?) ";
	public static String UPDATE_MESSAGE = " update message set text = ? where id = ?";
	public static String SET_MESSAGE_ACTIVE = " update message set active = ? where id = ?";
	public static String DELETE_MESSAGE = " delete from message where id = ? ";
	
	public static String SELECT_LEAST_USED_MESSAGE_FOR_ANNOUNCER = 
			" select * from message where active is true " +
			" order by (select count(*) from announcement where announcerid=? and timestampdiff(hour,timesent,now())<24 and message.id = messageid and status='"+Announcement.STATUS_SENT+"')" +
					", (select count(*) from announcement where message.id = messageid and timestampdiff(hour,timesent,now())<24 and status='"+Announcement.STATUS_SENT+"')";

	

	public static String SELECT_MESSAGE_FOR_PRODUCT = " select * from productmessage where productid = ? ";
	public static String ADD_MESSAGE_FOR_PRODUCT = " insert into productmessage(text,productid) values " + " (?,?)";
	public static String DELETE_MESSAGE_FOR_PRODUCT = " delete from productmessage where productid = ? ";
	
	public Message getMessageById(long id) {
		List<Message> messageList = selectMessages(" where id = " + id, null);
		return messageList.size() > 0 ? messageList.get(0) : null;
	}

	public Message getMessageById(String id) {
		return getMessageById(Long.valueOf(id));
	}

	public ArrayList<Message> selectMessages(String where, String orderBy) {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Message> messageList = new ArrayList<Message>();
		String whereStr = (Util.stringIsValid(where)) ? where : "";
		String orderbyStr = (Util.stringIsValid(orderBy)) ? orderBy : "";
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SELECT_MESSAGES + " " + whereStr + " " + orderbyStr);
			rs = ps.executeQuery();

			while (rs.next()) {
				Message message = new Message();
				message.getDataFromResultSet(rs);
				messageList.add(message);

			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return messageList;

	}

	public void removeMessage(long id) {

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(DELETE_MESSAGE);
			ps.setLong(1, id);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}

	private static String[] variables = {"product","price","customer"};
	
	
	private String applyParameters(String text, long anId) throws Exception {
		Announcement an = announcementMgr.getAnnouncementById(anId);
		Product product = productMgr.getProductById(an.getProductId());
		Announcer customer = announcerMgr.getAnnouncer(an.getCustomerId());
		//Reason reason = reasonMgr.getReasonById(an.getReasonId());

		if(product==null){
			throw new Exception("Product is null for announcement: "+anId);
		}

		if(customer==null){
			throw new Exception("Customer is null for announcement: "+anId);
		}

		
		Object[] params = new Object[]{
				product.getName(),
				LocaleMgrImpl.getInstance().getCurrency(product.getLocale()) + util.formattedMoneyString(product.getPrice()),
				"@"+customer.getName()};
	
		int i = 0;
		for (Object obj : params) {

			String iStr = "{" + i + "}";
			String iStrAlt = "{" + variables[i] + "}";
			text = text.replace(iStr, obj.toString());
			text = text.replace(iStrAlt, obj.toString());
			i++;

		}
		return text;
	}
	public Message generateMessageForAnnouncement(long anId) throws Exception{
		String msg = "";
		
		
		Announcement an = announcementMgr.getAnnouncementById(anId);
		
		Announcer customer = announcerMgr.getAnnouncer(an.getCustomerId());

		// tweey check
		String customerName = customer.getName();
		
		String url = util.shortenURLString("http://www.isirket.com:8080/?announcement="+ an.getId());
		if(url == null){
			url = shortLinkCacheMgrImpl.getUrlFromCache(an.getProductId());
			logger.info("URL retrieved from cache: " + url);
		}
		else{
			shortLinkCacheMgrImpl.saveUrl(an.getProductId(), url);
		}
//		String url = "http://www.gujum.com/?a="+ an.getId();
		
		
		
		if(!Util.stringIsValid(url)){
			throw new Exception("Can't generate link...");
		}
		Message message = null;
		
		message = getMessageForProduct(an.getProductId());
		if(message==null){

			message = getMessageForAnnouncer(an.getAnnouncerId());
		}
		
		msg = message.getText();
		
		msg = applyParameters(msg, an.getId());
		if(!msg.contains("@"+customerName)){
			msg = "@" + customerName +" "+msg;
		}
	
	
		msg = msg + " "+url;
		
		if(msg.length()>140){
			throw new Exception("Generated message exceeds 140 character limit."+msg);
		}
		
		message.setTextWithParametersAdded(msg);
		return message;
	}

	private void getUrlFromCache(long productId) {
		// TODO Auto-generated method stub
		
	}

	private Message getMessageForAnnouncer(long announcerId) throws Exception {


		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Message message = null;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(SELECT_LEAST_USED_MESSAGE_FOR_ANNOUNCER);
			ps.setLong(1, announcerId);
			rs = ps.executeQuery();
			if (rs.next()) {
				message = new Message();
				message.getDataFromResultSet(rs);
			} else {
				throw new Exception("Can't find message for announcer: " + announcerId);
			}

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return message;

	
	}

	public ProductMessage getMessageForProduct(long productId) {


		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ProductMessage message = null;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(SELECT_MESSAGE_FOR_PRODUCT);
			ps.setLong(1, productId);
			rs = ps.executeQuery();
			if (rs.next()) {
				message = new ProductMessage();
				message.getDataFromResultSet(rs);
			} 
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return message;

	
	}

	private Message deleteMessageForProduct(long productId) throws Exception {

		
		Connection connection = null;
		PreparedStatement ps = null;
		Message message = null;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(DELETE_MESSAGE_FOR_PRODUCT);
			ps.setLong(1, productId);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return message;

	
	}

	public Message setMessageForProduct(long productId, String text){

		Connection connection = null;
		PreparedStatement ps = null;
		Message message = null;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(DELETE_MESSAGE_FOR_PRODUCT);
			ps.setLong(1, productId);
			ps.executeUpdate();

			ps = connection.prepareStatement(ADD_MESSAGE_FOR_PRODUCT);
			ps.setString(1, text);
			ps.setLong(2, productId);
			ps.executeUpdate();
		
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
		return message;

	
	}

	public long addMessage(Message message) {
		Connection connection = null;
		PreparedStatement ps = null;

		ResultSet rs = null;
		long id = -1;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ADD_MESSAGE, Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, message.getText());

			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {

			logger.warn("DB: Message already exists - Message:" + message.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return id;
	}

	public void updateMessage(long id, String text) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_MESSAGE);
			ps.setString(1, text);
			ps.setLong(2, id);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	public void setMessageActive(long id) {

		setMessageActiveInactive(id, true);

	}

	public void setMessageInactive(long id) {

		setMessageActiveInactive(id, false);

	}

	private void setMessageActiveInactive(long id, boolean active) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SET_MESSAGE_ACTIVE);
			ps.setBoolean(1, active);
			ps.setLong(2, id);
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	public static MessageMgrImpl getInstance() {
		return instance;
	}

	public ArrayList<Message> getAllMessages() {
		return selectMessages(null, " order by active desc, text asc ");
	}
	public ArrayList<Message> getActiveMessages() {
		return selectMessages(" where active is true ", " order by text asc ");
	}

}
