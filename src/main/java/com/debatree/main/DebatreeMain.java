package com.debatree.main;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.LocaleMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.main.AnnounceTask;
import com.amazonbird.main.AnnouncerProvider;
import com.amazonbird.main.CustomerProvider;
import com.amazonbird.main.ProductProvider;
import com.amazonbird.monitor.JVMMonitor;
import com.amazonbird.statistics.Collector;
import com.amazonbird.util.Util;
import com.debatree.task.FindDebateTask;
import com.debatree.task.TaskBase;
import com.tcommerce.graph.GraphDatabase;

public class DebatreeMain implements ServletContextListener {
	Util util = Util.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	
	
	
	DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	private static Logger logger = Logger.getLogger(DebatreeMain.class);

	private static ServletContext servletContext;

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		GraphDatabase.getInstance().shutDown();
	}

	private void initialize() {
//		startJVMMonitor();
//		loadCache();
//		startStatistics();
//		startTasks();
		startTasks();
		
	}

	private void startJVMMonitor() {
		JVMMonitor.getInstance().activate();
	}

	private void loadCache() {
		LocaleMgrImpl.getInstance().loadLocales();
	}

	private void startStatistics() {
		Collector.getInstance().activate();
	}

	private void startTasks() {
		ArrayList<TaskBase> tasks = new ArrayList<TaskBase>();
		
		FindDebateTask findDebateTask = new FindDebateTask();
		tasks.add(findDebateTask);
		for(TaskBase task : tasks){

			Thread findDebateThread = new Thread(findDebateTask);
			findDebateThread.setName(task.getName());
			findDebateThread.start();
		}


	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		util.setContextPath(servletContextEvent.getServletContext()
				.getContextPath());

		System.setProperty("twitter4j.loggerFactory",
				"twitter4j.internal.logging.NullLoggerFactory");
		servletContext = servletContextEvent.getServletContext();

		String fileLocation = System.getProperty("user.home")
				+ "/.debatree/debatree.properties";
		File f = new File(fileLocation);
		logger.debug("Checking config file at: " + fileLocation);
		if (f.exists()) {
			initialize();
			logger.info(" Config file exist. Initialization completed.");

		} else {
			logger.info(" Config does not exist at " + fileLocation);
		}
	}

	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext2) {
		servletContext = servletContext2;
	}
}
