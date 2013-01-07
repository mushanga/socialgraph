package com.amazonbird.main;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import com.amazonbird.announce.AnnouncementMgrImpl;
import com.amazonbird.announce.AnnouncerMgrImpl;
import com.amazonbird.announce.LocaleMgrImpl;
import com.amazonbird.announce.ProductMgrImpl;
import com.amazonbird.announce.ReasonMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;
import com.amazonbird.db.base.DBMgrImpl;
import com.amazonbird.monitor.JVMMonitor;
import com.amazonbird.statistics.Collector;
import com.amazonbird.util.Util;
import com.tcommerce.graph.GraphDatabase;

public class AmazonBird implements ServletContextListener {
	Util util = Util.getInstance();
	ReasonMgrImpl reasonMgr = ReasonMgrImpl.getInstance();
	
	
	
	DBMgrImpl dbMgr = DBMgrImpl.getInstance();
	
	AnnouncerMgrImpl announcerMgr = AnnouncerMgrImpl.getInstance();
	ProductMgrImpl productMgr = ProductMgrImpl.getInstance();
	AnnouncementMgrImpl announcementMgr = AnnouncementMgrImpl.getInstance();
	private static Logger logger = Logger.getLogger(AmazonBird.class);

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
		

		if (!PropsConfigMgrImpl.getInstance().isDev()) {
			Thread announceTaskThread = new Thread(new AnnounceTask());
			announceTaskThread.setName("Announce Task");
			announceTaskThread.start();
		}

		Thread productProviderTaskThread = new Thread(new ProductProvider());
		productProviderTaskThread.setName("Product Provider Task");
		productProviderTaskThread.start();

		Thread customerProviderTaskThread = new Thread(new CustomerProvider());
		customerProviderTaskThread.setName("Customer Provider Task");
		customerProviderTaskThread.start();

		Thread announcerProviderTaskThread = new Thread(new AnnouncerProvider());
		announcerProviderTaskThread.setName("Announcer Provider Task");
		announcerProviderTaskThread.start();

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
			logger.info(" Config file exist. Amazon Bird initialization completed.");

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
