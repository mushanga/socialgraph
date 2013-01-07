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

package com.amazonbird.db.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;

public class DBMgrImpl implements DBMgr {

	String dbHost;
	int dbPort;
	String userName;
	String password;
	String dbName;
	String connectURI;
	private DataSource dataSource;
	private static Logger logger = Logger.getLogger(DBMgrImpl.class);
	private static DBMgrImpl instance = null;
	private boolean initialized = false;
	PropsConfigMgrImpl configMgr = PropsConfigMgrImpl.getInstance();
	public static DBMgrImpl getInstance(){
		if(instance == null){
			instance = new DBMgrImpl();
		}
		return instance;
	}
	private DBMgrImpl(){

	}
	/* (non-Javadoc)
	 * @see com.twitstreet.db.base.DBMgr#init()
	 */
	@Override
	public void init() {
		setupDataSource();
		initialized = true;
		logger.debug("Database Manager initialized.");
	}

	private void setupDataSource() {
		PoolProperties p = new PoolProperties();
		p.setUrl(getConnectionURL());
		p.setDriverClassName(DBConstants.DRIVER);
		p.setUsername(configMgr.getDbUserName());
		p.setPassword(configMgr.getDbPassword());
		p.setJmxEnabled(true);
		p.setTestWhileIdle(false);
		p.setTestOnBorrow(true);
		p.setValidationQuery(DBConstants.VALIDATION_QUERY);
		p.setTestOnReturn(false);
		p.setValidationInterval(DBConstants.VALIDATION_INTERVAL);
		p.setTimeBetweenEvictionRunsMillis(DBConstants.EVICTION_RUN_MILLIS);
		p.setMaxActive(DBConstants.MAX_ACTIVE);
		p.setInitialSize(DBConstants.INITIAL_SIZE);
		p.setMaxWait(DBConstants.MAX_WAIT);
		p.setRemoveAbandonedTimeout(DBConstants.ABANDONED_TIMEOUT);
		p.setMinEvictableIdleTimeMillis(DBConstants.MIN_EVICTABLE_IDLE_TIME);
		p.setMinIdle(DBConstants.MIN_IDLE);
		p.setLogAbandoned(false);
		p.setRemoveAbandoned(true);
		p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
				"org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
		dataSource = new DataSource();
		dataSource.setPoolProperties(p); 
	}

	private String getConnectionURL(){
		return "jdbc:mysql://"+configMgr.getDbHost()+":"+configMgr.getDbPort()+ "/"+configMgr.getDbName() + "?characterEncoding=utf-8&useUnicode=true";
	}

	/* (non-Javadoc)
	 * @see com.twitstreet.db.base.DBMgr#getConnection()
	 */
	@Override
	public synchronized Connection getConnection() throws SQLException{
		if(!initialized){
			init();
		}
		return dataSource.getConnection();
	}

	@Override
	public boolean closeResources(Connection c, Statement stmt, ResultSet rs) {
		try {
			if (rs != null && !rs.isClosed()) {
				rs.close();
			}
			if (stmt != null && !stmt.isClosed()) {
				stmt.close();
			}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (SQLException e) {
			logger.error(DBConstants.RESOURCES_NOT_CLOSED, e);
			return false;
		}
		return true;
	}

	@Override
	public boolean closeResources(Connection c, Statement[] stmts, ResultSet[] rss) {
		try {
			for(ResultSet rs:rss){
				if (rs != null && !rs.isClosed()) {
					rs.close();
				}
			}
			for(Statement stmt:stmts)
				if (stmt != null && !stmt.isClosed()) {
					stmt.close();
				}
			if (c != null && !c.isClosed()) {
				c.close();
			}
		} catch (SQLException e) {
			logger.error(DBConstants.RESOURCES_NOT_CLOSED, e);
			return false;
		}
		return true;
	}

	public static String getIdListAsCommaSeparatedString(List<Long> idList){
		String idListStr = "";
		for(int i= 0;i <idList.size(); i++){
			if(i!=0){
				idListStr = idListStr +",";
			}
			idListStr = idListStr + "("+String.valueOf(idList.get(i)+(")"));
		}
		return idListStr;
	}

	public static String getIdListAsCommaSeparatedString4In(List<Long> idList){
		String idListStr = "";
		for(int i= 0;i <idList.size(); i++){
			if(i!=0){
				idListStr = idListStr +",";
			}
			idListStr = idListStr + String.valueOf(idList.get(i) );
		}
		return idListStr;
	}
}
