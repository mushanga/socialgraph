package com.amazonbird.main;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.db.data.Announcement;
import com.amazonbird.db.data.Announcer;
import com.amazonbird.util.ExceptionUtil;
import com.amazonbird.util.Util;

public class AnnounceTask implements Runnable {
	ExceptionUtil exutil = ExceptionUtil.getInstance();

	Util util = Util.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	private static Logger logger = Logger.getLogger(AnnounceTask.class);

	String amazonTag = "gujum";


	public static int TASK_PERIOD_IN_MILSECS = 1000;

	@Override
	public void run() {

		logger.info("Anounce Task starting...");

		logger.info("Setting announcements stuck at 'Sending...' to 'Error'");
		setHangingAnnouncementsToError();

		while (true) {

			try {
				long startTime = System.currentTimeMillis();

				
				fillQueue();			
				setOldAnnouncementsTimedOut();
				announceNext();
				
				util.sleepIfNeeded(startTime, TASK_PERIOD_IN_MILSECS);

			} catch (Exception ex) {
				logger.error("Announce Task: Main Loop", ex);
			}
		}
	}

	private void setOldAnnouncementsTimedOut() {

		announcementMgr.setAnnouncementsToErrorOlderThan(24);

	}

	private void announceNext() throws Exception {

		ArrayList<Announcement> anList = announcementMgr.getAnnouncementsByStatus(Announcement.STATUS_READY, 0, 1);
		if (util.listIsValid(anList)) {

			Announcement readyAn = anList.get(0);

			Announcer ancr = announcerMgr.getAnnouncer(readyAn.getAnnouncerId());
			if (ancr != null) {
				try {

					readyAn.setStatus(Announcement.STATUS_SENDING);
					announcementMgr.updateSetStatus(readyAn.getId(), readyAn.getStatus());

					long statusId = announcementMgr.getAnnouncementReasonStatus(readyAn.getId());

					if (statusId != 0) {

						ancr.reply(readyAn.getId(), statusId);
					} else {

						ancr.announce(readyAn.getId());
					}

					announcementMgr.setSent(readyAn.getId());

					logger.info("Announcement Sent! " + readyAn);

				} catch (Exception e) {
					logger.error("Error in announce! Setting announcement status to error...\n"+readyAn, e);
					announcementMgr.updateSetStatus(readyAn.getId(), Announcement.STATUS_ERROR);

				}
			} else {
				logger.info("Announcer not found for: " + readyAn);
				announcementMgr.updateSetStatus(readyAn.getId(), Announcement.STATUS_ERROR);
			}
		}

	}

	private void fillQueue() {

		ArrayList<Announcement> anList = announcementMgr.getAnnouncementsInProgress(0, Integer.MAX_VALUE);

		int queueSize = productMgr.getActiveProductCount();

		while (!Util.isListValid(anList) || anList.size() <= queueSize) {
			Announcement newAn = new Announcement();
			newAn.setStatus(Announcement.STATUS_INITIALIZED);
			announcementMgr.addAnnouncement(newAn);
			anList = announcementMgr.getAnnouncementsInProgress(0, Integer.MAX_VALUE);
		}
	}

	private void setHangingAnnouncementsToError() {

		announcementMgr.updateSetStatusWhereStatus(Announcement.STATUS_SENDING, Announcement.STATUS_ERROR);
	}

}
