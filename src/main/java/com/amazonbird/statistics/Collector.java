package com.amazonbird.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.db.base.DBMgrImpl;

/**
Collect data for most active users, products and announcers for every hour and log results
 */
public final class Collector {

	private HashSet<StatGroup> statData;
	private DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	private Connection connection = null;
	private PreparedStatement ps = null;
	private static Logger logger = Logger.getLogger(Collector.class);
	
	// PRIVATE 
	private ScheduledExecutorService fScheduler;
	private long fInitialDelay = 0;
	private long fDelayBetweenRuns = 0;
	private int logNumber;

	private static final int NUM_THREADS = 1;
	
	private static Collector instance = new Collector();
	
	public static Collector getInstance(){
		return instance;
	}

	public Collector(){
		statData = new HashSet<StatGroup>();
		statData.add(new StatGroup("product","SELECT pr.name, count( * ) FROM click cl, announcement an, product pr WHERE an.id = cl.announcementid and pr.id = an.productid GROUP BY cl.announcementid"));
		fInitialDelay = PropsConfigMgrImpl.getInstance().getStatisticLogNumber();
		fDelayBetweenRuns = PropsConfigMgrImpl.getInstance().getStatisticPeriod();
		//statData.add(new StatGroup("product","SELECT pr.name, SUM(an.clickcount) AS field1 FROM announcement an, product pr WHERE pr.id = an.productid GROUP BY an.productid"));
		//statData.add(new StatGroup("customer","SELECT c.name, SUM(an.clickcount) AS field1 FROM announcement an, customer c WHERE c.id = an.customerid GROUP BY an.customerid"));
		//statData.add(new StatGroup("announcer","SELECT a.screenName, SUM(an.clickcount) AS field1 FROM announcement an, announcer a WHERE a.id = an.announcerid GROUP BY an.announcerid"));		
		try {
			fInitialDelay = 0;
			fDelayBetweenRuns = 120;
			fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);   
			logNumber = 5;
		} catch (Exception e) {
			logger.error("Statistics init problem: "+e.getMessage());
		}
	}

	
	public void setfDelayBetweenRuns(long fDelayBetweenRuns) {
		this.fDelayBetweenRuns = fDelayBetweenRuns;
	}

	public void setLogNumber(int logNumber) {
		this.logNumber = logNumber;
	}

	public void activate(){
		Runnable collectorTask = new collectorTask();
		fScheduler.scheduleWithFixedDelay(collectorTask, fInitialDelay, fDelayBetweenRuns, TimeUnit.MINUTES);
	}

	private final class collectorTask implements Runnable {
		
		public void run() {
			try {
				connection = dbMgr.getConnection();
				logger.debug("---Category , Name , current , diff---");
				for(StatGroup statGrp:statData){

					ps = connection.prepareStatement(statGrp.getQuery());
					statGrp.processStatGroup(ps.executeQuery());
					statGrp.logResult(logNumber);

				}
				
			} catch (SQLException e) {
				logger.error("db fail: "+e.getMessage());
			} 

		}

	}

} 