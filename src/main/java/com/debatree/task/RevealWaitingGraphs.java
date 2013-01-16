package com.debatree.task;

import java.util.List;

import org.apache.log4j.Logger;

import com.amazonbird.util.Util;
import com.debatree.data.UserGraphStatus;
import com.debatree.exception.DebatreeException;
import com.debatree.twitter.TwitterClient;

public class RevealWaitingGraphs extends TaskBase {

	private static Logger logger = Logger.getLogger(RevealWaitingGraphs.class);

	@Override
	public String getName() {
		return "Reveal Waiting Users";
	}

	@Override
	public void process() throws DebatreeException {
		
			UserGraphStatus graph = ugsMgr.getWaitingGraph();
			if(graph!=null){
				tc.setFriendsOfFriendsGraph(graph.getScreenName());
				
			}
			
	}

	@Override
	public long getTaskPeriod() {
		return 1000;
	}

	@Override
	protected boolean isLogEnabled() {
		return false;
	}

}