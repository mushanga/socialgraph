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
package com.amazonbird.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.amazonbird.db.base.DBConstants;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.db.data.Config;

public class ConfigMgrImpl implements ConfigMgr{
	private static Logger logger = Logger.getLogger(ConfigMgrImpl.class);
	DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	private HashMap<String, String> configMap = new HashMap<String, String>();
	
	int serverCount;
	int serverId;
	String mailRecipientsArr[];

	
	
	boolean initialized = false;

	private static ConfigMgrImpl instance= new ConfigMgrImpl();
	public static ConfigMgrImpl getInstance(){
		return instance;
	}
	private ConfigMgrImpl(){
	}
	
	private void init(){
		loadConfigFromProps();
		loadConfigFromDb();
		initialized = true;
	}

	private void loadConfigFromProps(){

	}
	private void loadConfigFromDb() {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = dbMgr.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select id, parm, val from config");
			
			while(rs.next()){
				configMap.put(rs.getString(Config.PARM), rs.getString(Config.VAL));
			}
			
			logger.debug(DBConstants.QUERY_EXECUTION_SUCC + stmt.toString());
			logger.debug("Config manager initialized successfully.");

		} catch (SQLException e) {
			logger.error(DBConstants.QUERY_EXECUTION_FAIL + stmt == null ? "Query is null" : stmt.toString(), e);
			logger.error("Config manager initialization failed.");
		}
		finally{
			dbMgr.closeResources(connection, stmt, rs);
		}
	}
	
	
	public String get(String parm){
		if(!initialized){
			init();
		}
		String val = configMap.get(parm);
		return val == null ? "" : val;
	}
	@Override
	public String getConsumerKey() {
		return "3v8qt93hi8KOFSxHWF17A";
	}
	@Override
	public String getConsumerSecret() {
		return "GB95bsGAIjf0RoQbEWBdu8zex4YIw11HJq51PjW9z8";
	}
}
