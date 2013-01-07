package com.amazonbird.main;

import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncerMgrImpl;

public class FeedAnnouncerTask implements Runnable {
	private static Logger logger = Logger.getLogger(FeedAnnouncerTask.class);
	AnnouncerMgrImpl announcerMgrImpl = AnnouncerMgrImpl.getInstance();
	public void run() {
		while(true){
			try{
//				announcerMgrImpl.feedAnnouncer();
			}
			catch (Exception e) {
				logger.error("Error in announcer feed", e);
			}
			try {
				Thread.sleep(1000 * 60 * 10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
