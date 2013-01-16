package com.debatree.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.debatree.data.HibernateObject;
import com.debatree.data.TwitstreetUserTokenStatus;

public class TwitstreetUserServiceImpl extends HibernateService {
	private static Logger logger = Logger.getLogger(TwitstreetUserServiceImpl.class);


	public TwitstreetUserTokenStatus getToken(long id){
		
		return (TwitstreetUserTokenStatus) getById(id);
	}
	public TwitstreetUserTokenStatus getAvailabeToken() {
		TwitstreetUserTokenStatus token = null;
//		try
		
		try {
			
			ArrayList<Criterion> criteria = new ArrayList<Criterion>();
			criteria.add(Restrictions.lt("resetTimeInSecs", new Date().getTime()/1000));
			criteria.add(Restrictions.eq("status", TwitstreetUserTokenStatus.FREE));
			
			token = (TwitstreetUserTokenStatus) getListByCriteria(criteria).get(0);
		} catch (Exception e) {
			logger.info("No available token");
		}
		
		return token;
		
	}

	@Override
	public Class<? extends HibernateObject> getEntityClass() {
		return TwitstreetUserTokenStatus.class;
	}
	public void setTokenInvalid(long id) {
		setToken(id, TwitstreetUserTokenStatus.INVALID, -1);
	}

	private void setToken(long userId, String status, int resetTimeInSecs) {
		save(new TwitstreetUserTokenStatus(userId, status, resetTimeInSecs), userId);
	}
	public void setTokenAvailable(long userId) {
		setToken(userId, TwitstreetUserTokenStatus.FREE,0);
	}
	public void setTokenRateLimited(long userId, int resetTimeInSecs) {
		setToken(userId, TwitstreetUserTokenStatus.RATE_LIMITED,resetTimeInSecs);
	}
	public void resetRateLimitedTokens() {
		
		List<HibernateObject> tokens = new ArrayList<HibernateObject>();
	try {
			
			ArrayList<Criterion> criteria = new ArrayList<Criterion>();
			criteria.add(Restrictions.lt("resetTimeInSecs", new Date().getTime()/1000));
			criteria.add(Restrictions.eq("status", TwitstreetUserTokenStatus.RATE_LIMITED));
			
			tokens = (List<HibernateObject>) getListByCriteria(criteria);
			
			for(HibernateObject obj : tokens){
				TwitstreetUserTokenStatus tsts = (TwitstreetUserTokenStatus) obj;
				setTokenAvailable(tsts.getId());
			}
		} catch (Exception e) {
			logger.info("No rate limited token found to reset");
		}
	}
}
