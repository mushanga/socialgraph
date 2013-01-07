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

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.db.data.Click;
import com.amazonbird.db.data.Product;
import com.amazonbird.route.ClickMgrImpl;
import com.amazonbird.servlet.FileUtil;
import com.amazonbird.util.Util;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

public class ProductMgrImpl {

	private static Logger logger = Logger.getLogger(ProductMgrImpl.class);

	private static ProductMgrImpl instance = new ProductMgrImpl();

	private ProductMgrImpl() {

	}
Util util = Util.getInstance();
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	public static String LOAD_PRODUCTS = "select * from product";
	public static String ADD_PRODUCT = " insert into product(name,dateAdded,price,destination, alternativeDestination, locale,announcerid) values " + " (?,NOW(),?,?,?,?,?) ";
	public static String UPDATE_PRODUCT = " update product set name = ?, price = ?, destination=?, alternativeDestination = ?, locale = ?, announcerid = ? where id = ?";
	public static String SET_PRODUCT_ACTIVE = " update product set active = ? where id = ?";
	public static String DELETE_PRODUCT = " delete from product where id = ? ";
	public static String COUNT_ACTIVE_PRODUCT = "select count(*) from product where active is true";
	
	public static String LOAD_PRODUCT_PICTURES = "select * from productpicture where productid = ? ";
	public static String ADD_PRODUCT_PICTURE = "insert into productpicture(productid,imageurl) values(?,?) ";
	
	
	public Product getProductById(long id) {
		List<Product> productList = selectProducts(" where id = "+id, null);
		return productList.size() > 0 ? productList.get(0) : null;
	}

	public Product getProductById(String id) {
		return getProductById(Long.valueOf(id));
	}
	
	public ArrayList<Product> selectProducts(String where, String orderBy) {

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<Product> productList = new ArrayList<Product>();
		String whereStr = (Util.stringIsValid(where))?where:"";
		String orderbyStr = (Util.stringIsValid(orderBy))?orderBy:"";
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(LOAD_PRODUCTS+" "+whereStr+" "+orderbyStr);
			rs = ps.executeQuery();

			while (rs.next()) {
				Product product = new Product();
				product.getDataFromResultSet(rs);
				productList.add(product);
				

			}
	
			
			for(Product product: productList){	
				ps = connection.prepareStatement(LOAD_PRODUCT_PICTURES);			
				ps.setLong(1, product.getId());
				rs = ps.executeQuery();

				
				ArrayList<String> pictureUrlList = new ArrayList<String>();
				while (rs.next()) {
					pictureUrlList.add(rs.getString("imageurl"));
				}
				String[] urlArr = new String[pictureUrlList.size()];
				int i = 0;
				for(String url : pictureUrlList){
					urlArr[i] =FileUtil.getInstance().getFilePathLogical() + url;
					i++;
				}
				product.setPictureUrls(urlArr);
			}
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
			
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		
		
		
		
		return productList;

	}
	
	public List<Product> getProductsForAnnouncer(long announcerId){
		
		return selectProducts(" where announcerid =  "+announcerId, null);
	}
	
	public int getActiveProductCount(){
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int count = 0;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement(COUNT_ACTIVE_PRODUCT);
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

	public void removeProduct(long id) {

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(DELETE_PRODUCT);
			ps.setLong(1, id);

			ps.executeUpdate();

			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}

	}


	public Product addProduct(Product product) {
		Connection connection = null;
		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ADD_PRODUCT, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPrice());
			ps.setString(3, product.getDestination());
			ps.setString(4, product.getAlternativeDestionation());
			ps.setString(5, product.getLocale());
			ps.setLong(6, product.getAnnouncerId());

			ps.executeUpdate();
			
			rs = ps.getGeneratedKeys();
	        if (rs.next()){
	            long productId = rs.getLong(1);
	            product.setId(productId);
	        }
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (MySQLIntegrityConstraintViolationException e) {

			logger.error("Error: "+e.getMessage()+"\nProduct:" + product.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return product;
	}

	public void updateProduct(Product product) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(UPDATE_PRODUCT);
			ps.setString(1, product.getName());
			ps.setDouble(2, product.getPrice());
			ps.setString(3, product.getDestination());
			ps.setString(4, product.getAlternativeDestionation());
			ps.setString(5, product.getLocale());
			ps.setLong(6, product.getAnnouncerId());
			ps.setLong(7, product.getId());
			ps.executeUpdate();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}

	public void setProductActive(long id) {

		setProductActiveInactive(id, true);

	}

	public void setProductInactive(long id) {

		setProductActiveInactive(id, false);

	}

	private void setProductActiveInactive(long id, boolean active) {
		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(SET_PRODUCT_ACTIVE);
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

	public static ProductMgrImpl getInstance() {
		return instance;
	}

	public ArrayList<Product> getAllProducts() {
		return selectProducts(null,  " order by active desc, dateadded desc ");
	}
	
	public void addProductPicture(long productId, String url){

		Connection connection = null;
		PreparedStatement ps = null;

		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(ADD_PRODUCT_PICTURE);
			ps.setLong(1, productId);
			ps.setString(2, url);
			ps.executeUpdate();
		
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);
		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
	
	}
	
	public int getViewCountForProduct(long productId){
		//
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		int count = 0;
		try {
			connection = dbMgr.getConnection();

			ps = connection.prepareStatement("select count(*) from announcement, product, click where click.announcementid = announcement.id and announcement.productid = product.id and product.id = ?");
			ps.setLong(1, productId);
			
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
	
	public List<Announcer> getAnnouncerWhoViewedProduct(long productId){
		ArrayList<Announcer> announcerList = new ArrayList<Announcer>();
		String query = "select distinct announcer.id as id, announcer.consumerKey as consumerKey, " + 
				"announcer.consumerSecret as consumerSecret, announcer.accessToken as accessToken, announcer.accessTokenSecret as accessTokenSecret, " + 
				"announcer.name as name, announcer.surname as surname, announcer.password as password, " + 
				"announcer.email as email, announcer.screenName as screenName, announcer.suspended as suspended, " + 
				"announcer.creationtime as creationtime, announcer.training as training, announcer.maxFamousAccount2Follow as maxFamousAccount2Follow, " + 
				"announcer.famousAccountFollowed as famousAccountFollowed, announcer.following as following, announcer.follower as follower, " + 
				"announcer.authtoken as authtoken, announcer.sesid as sesid, announcer.suspensiontime as suspensiontime, announcer.description as description, " + 
				"announcer.url as url, announcer.longName as longName, announcer.location as location, announcer.pictureUrl as pictureUrl " + 
				" from announcer, announcement, product, click where click.announcementid = announcement.id and announcement.productid = product.id and announcement.customerid = announcer.id  and product.id = ?";
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(query);
			ps.setLong(1, productId);
			rs = ps.executeQuery();
			while (rs.next()) {
				Announcer announcer = new Announcer();
				announcer.getDataFromResultSet(rs);
				announcerList.add(announcer);
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return announcerList;
	}
	
	public static void main(String[] args) {
		//ArrayList<Product> productList =  ProductMgrImpl.getInstance().getAllProducts();
		//List<Announcer> announcerList = AnnouncerMgrImpl.getInstance().getAllAnnouncers();
		
		//Announcement announcement = new Announcement();
		//announcement.setCustomerId(announcerList.get(0).getId());
		//announcement.setAnnouncerId(announcerList.get(0).getId());
		
		//AnnouncementMgrImpl.getInstance().addAnnouncement(announcement);
		
		//Click click
		Announcer announcer = AnnouncerMgrImpl.getInstance().getAnnouncer(392768645);
		Announcer customer = AnnouncerMgrImpl.getInstance().getAnnouncer(467241262);
		
		Product product = ProductMgrImpl.getInstance().getProductById(174);
		
		Announcement announcement = new Announcement();
		announcement.setCustomerId(customer.getId());
		announcement.setProductId(product.getId());
		announcement.setAnnouncerId(announcer.getId());
		announcement.setStatus(Announcement.STATUS_INITIALIZED);
		
		AnnouncementMgrImpl.getInstance().addAnnouncement(announcement);
		
	    announcement = AnnouncementMgrImpl.getInstance().getAnnouncementById(83670);
	    
	    Click click = new Click(announcement.getId(), "127.0.0.01");
	    ClickMgrImpl.getInstance().addClick(click);
	    
	    long viewCount = ProductMgrImpl.getInstance().getViewCountForProduct(174);
	    List<Announcer> viewList = ProductMgrImpl.getInstance().getAnnouncerWhoViewedProduct(174);
	    System.out.println("stop");
		
	}
	
	public List<Product> getProductsByKeyWord(String keyword){
		List<Product> searchResultList = new ArrayList<Product>();
		String query = "select product.id as id, " +
				"product.amazonid as amazonid, " +
				"product.dateadded as dateadded, " +
				"product.price as price, " +
				"product.name as name, " +
				"product.active as active, " +
				"product.image as image, " +
				"product.customdestination as customdestination, " +
				"product.destination as destination, " +
				"product.alternativeDestination as alternativeDestination," +
				"product.locale as locale, " +
				"product.announcerid as announcerid " +
				"from productmessage, product, reason, reasonproduct " +
				"where product.id = productmessage.productid " +
				"and reason.id = reasonproduct.reasonid " +
				"and reasonproduct.productid = product.id " +
				"and reasonproduct.productid = productmessage.productid " +
				"and (reason.value like ? or productmessage.text like ?) " +
				"order by active desc, dateadded desc ;";
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, "%"+keyword+"%");
			ps.setString(2, "%"+keyword+"%");
			rs = ps.executeQuery();
			while (rs.next()) {
				Product product = new Product();
				product.getDataFromResultSet(rs);
				searchResultList.add(product);
			}
			
			
			for(Product product: searchResultList){	
				ps = connection.prepareStatement(LOAD_PRODUCT_PICTURES);			
				ps.setLong(1, product.getId());
				rs = ps.executeQuery();

				
				ArrayList<String> pictureUrlList = new ArrayList<String>();
				while (rs.next()) {
					pictureUrlList.add(rs.getString("imageurl"));
				}
				String[] urlArr = new String[pictureUrlList.size()];
				int i = 0;
				for(String url : pictureUrlList){
					urlArr[i] =FileUtil.getInstance().getFilePathLogical() + url;
					i++;
				}
				product.setPictureUrls(urlArr);
			}
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return searchResultList;
	}
}
