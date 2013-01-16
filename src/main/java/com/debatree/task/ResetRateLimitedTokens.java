package com.debatree.task;

import org.apache.log4j.Logger;

import com.debatree.exception.DebatreeException;

public class ResetRateLimitedTokens extends TaskBase {

	private static Logger logger = Logger.getLogger(ResetRateLimitedTokens.class);

	@Override
	public String getName() {
		return "Reset Rate Limited Tokens";
	}

	@Override
	public void process() throws DebatreeException {
		tsuMgr.resetRateLimitedTokens();
	}

	@Override
	public long getTaskPeriod() {
		return 60 * 1000;
	}

	@Override
	protected boolean isLogEnabled() {
		return false;
	}

}