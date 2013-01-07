package com.debatree.task;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.util.ExceptionUtil;
import com.amazonbird.util.Util;
import com.debatree.exception.DebatreeException;
import com.debatree.twitter.TwitterClient;

public abstract class TaskBase implements Runnable {
	ExceptionUtil exutil = ExceptionUtil.getInstance();
Twitter t = new TwitterFactory().getInstance();
	Util util = Util.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	private Logger logger = Logger.getLogger(getClass());

private String TASK_LINE_SYM = " ---------------- ";
	
	TwitterClient tc = null;
	public static int TASK_PERIOD_IN_MILSECS = 60 * 60 * 1000;

	public TaskBase(){
		 try {
			tc = TwitterClient.getDefaultClient();
		} catch (DebatreeException e) {
			logger.error(e.getMessage());
		}
	}
	public abstract String getName();
	public long getTaskPeriod(){
		return TASK_PERIOD_IN_MILSECS;
	}
	
	public abstract void process() throws DebatreeException;
	
	@Override
	public void run() {

		logger.info(TASK_LINE_SYM+getName() + " starting..."+TASK_LINE_SYM);

		while (true) {
			long startTime = System.currentTimeMillis();
			logger.info(getName() + " starting the unit process...");

			try {
				
				process();
				
				
				logger.info(getName() + " finished the unit process successfully.");
				
			} catch (DebatreeException ex) {
				logger.error(getName() + " failed to finish the unit process.",ex);
				
			}catch (Exception ex) {
				logger.error(getName() + " failed to finish the unit process."+TASK_LINE_SYM,ex);
				
			}finally{
				util.sleepIfNeeded(startTime, getTaskPeriod());

			}
		}
	}



}
