package com.amazonbird.monitor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.amazonbird.config.ConfigMgr;
import com.amazonbird.config.ConfigMgrImpl;
import com.amazonbird.config.PropsConfigMgrImpl;

public class JVMMonitor {

	private static long MB = 1024*1024;
	private static int NUM_THREADS = 1;
	private ScheduledExecutorService fScheduler;
	private static Logger logger = Logger.getLogger(JVMMonitor.class);
	
	private int period = 0;
	private int diff = 0;
	
	private static JVMMonitor instance = new JVMMonitor();
	
	public JVMMonitor(){
		period = PropsConfigMgrImpl.getInstance().getJVMMonitorPeriod();;
		diff = PropsConfigMgrImpl.getInstance().getJVMMonitorDiff();
	}
	
	public static JVMMonitor getInstance(){
		if(instance == null){
			instance = new JVMMonitor();
		}
		return instance;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

	public void activate(){
		Runnable monitorTask = new MonitorTask();
		fScheduler = Executors.newScheduledThreadPool(NUM_THREADS);   
		fScheduler.scheduleWithFixedDelay(monitorTask,0, period, TimeUnit.SECONDS);
	}

	private final class MonitorTask implements Runnable {

		private boolean underLimit = false;
		private long free = 0;
		private long total = 0;
		private long used = 0;
		
		public void run() {
	        free = Runtime.getRuntime().freeMemory()/MB;
	        total = Runtime.getRuntime().totalMemory()/MB;
	        used = total - free;
	        int per = (int) ((free*100)/total);
	        if(per<diff){
	        	if(!underLimit){
	        		String log = "Free memory dropped under %"+diff+" => total: "+total+" free: "+free+" used: "+used+" (MB)";
	        		underLimit = true;
	        		logger.error(log);
	        	}
	        }else if(per<(2*diff) ){
	        	//Do nothing. Just a gap
	        }else{
	        	if(underLimit){
	        		int usedLimit = 100 - 2*diff;
	        		String log = "Used memory dropped under %"+usedLimit+" => total: "+total+" free: "+free+" used: "+used+" (MB)";
	        		underLimit = false;
		        	logger.info(log);
	        	}	
	        }

		}

	}

}
