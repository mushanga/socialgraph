package com.debatree.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.debatree.data.HibernateObject;
import com.debatree.data.UserTokenStatus;

public class UserTokenServiceImpl extends HibernateService {

	private static Logger logger = Logger.getLogger(UserTokenServiceImpl.class);

	public List<Long> getAvailableTokens() {
		List<HibernateObject> list = getListByField("status", UserTokenStatus.FREE);

		return usersToIdList(list);
	}

	@Override
	public UserTokenStatus getById(long id) {
		return (UserTokenStatus) super.getById(id);
	}

	@Override
	public Class<? extends HibernateObject> getEntityClass() {
		return UserTokenStatus.class;
	}
	private List<Long> usersToIdList(List<HibernateObject> list) {

		List<Long> ids = new ArrayList<Long>();
		for (HibernateObject obj : list) {

			UserTokenStatus ufls = (UserTokenStatus) obj;

			ids.add(ufls.getId());
		}
		return ids;
	}

	public boolean tokenIsAvailable(long userId) {

		UserTokenStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserTokenStatus.FREE);
		} catch (Exception e) {
			
		}
		return false;
		
	}

	private void setToken(long userId, String status, int resetTimeInSecs) {
		save(new UserTokenStatus(userId, status, resetTimeInSecs), userId);
	}
	public void setTokenAvailable(long userId) {
		setToken(userId, UserTokenStatus.FREE,0);
	}
	public void setTokenRateLimited(long userId, int resetTimeInSecs) {
		setToken(userId, UserTokenStatus.RATE_LIMITED,resetTimeInSecs);
	}


}
