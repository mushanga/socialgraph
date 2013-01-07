package com.amazonbird.announce;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;

public class ShortLinkCacheMgrImpl {
	private static Logger logger = Logger.getLogger(ShortLinkCacheMgrImpl.class);
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	private static final ShortLinkCacheMgrImpl instance = new ShortLinkCacheMgrImpl();
	public static ShortLinkCacheMgrImpl getInstance() {
		return instance;
	}
	public String getUrlFromCache(long productId) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String url = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement("select url from shortlinkcache where productId = ? order by rand() limit 0,1");
			ps.setLong(1, productId);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				url = rs.getString("url");
			}
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, rs);
		}
		return url;
	}
	public void saveUrl(long productId, String url) {
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = dbMgr.getConnection();
			ps = connection.prepareStatement("insert into shortlinkcache(productId, url) values(?,?)");
			ps.setLong(1, productId);
			ps.setString(2, url);
			ps.execute();
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + ps.toString());
		} catch (SQLException ex) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + ps.toString(), ex);

		} finally {
			dbMgr.closeResources(connection, ps, null);
		}
	}
}
