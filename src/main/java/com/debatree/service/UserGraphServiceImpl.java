package com.debatree.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonbird.util.Util;
import com.debatree.data.HibernateObject;
import com.debatree.data.UserGraphStatus;

public class UserGraphServiceImpl extends HibernateService {

	private static Logger logger = Logger.getLogger(UserGraphServiceImpl.class);

	public List<Long> getProtectedUsers() {
		List<HibernateObject> list = getListByField("status", UserGraphStatus.PROTECTED);

		return usersToIdList(list);
	}

	@Override
	public UserGraphStatus getById(long id) {
		return (UserGraphStatus) super.getById(id);
	}
	public List<Long> getWaitingGraphs() {
		List<HibernateObject> list = getListByField("status", UserGraphStatus.WAITING);

		return usersToIdList(list);
	}
	public UserGraphStatus getWaitingGraph() {
		List<HibernateObject> list = getListByField("status", UserGraphStatus.WAITING);
		if(Util.isListValid(list)){
			return (UserGraphStatus) list.get(0);
			
		}else{
			return null;
			
		}
	}

	

	public void setGraphProtected(long id) {
		UserGraphStatus ufls = new UserGraphStatus(id, UserGraphStatus.PROTECTED);
		save(ufls, id);
	}

	@Override
	public Class<? extends HibernateObject> getEntityClass() {
		return UserGraphStatus.class;
	}
	private List<Long> usersToIdList(List<HibernateObject> list) {

		List<Long> ids = new ArrayList<Long>();
		for (HibernateObject obj : list) {

			UserGraphStatus ufls = (UserGraphStatus) obj;

			ids.add(ufls.getId());
		}
		return ids;
	}

	public boolean graphIsProtected(long userId) {

		UserGraphStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserGraphStatus.PROTECTED);
		} catch (Exception e) {
			
		}
		return false;
		
	}
	public boolean graphIsWaiting(long userId) {

		UserGraphStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserGraphStatus.WAITING);
		} catch (Exception e) {
			setGraphWaiting(userId);
		}
		return true;
		
	}

	public void setGraphCompleted(long userId) {
		save(new UserGraphStatus(userId, UserGraphStatus.COMPLETED), userId);
	}
	public void setGraphInProgress(long userId) {
		save(new UserGraphStatus(userId, UserGraphStatus.IN_PROGRESS), userId);
	}


	public boolean graphIsCompleted(long userId) {

		UserGraphStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserGraphStatus.COMPLETED);
		} catch (Exception e) {
			
		}
		return false;
		
	}

	public void setGraphWaiting(long id) {
		save(new UserGraphStatus(id, UserGraphStatus.WAITING), id);
	}

	public void setGraphName(long id, String screenName) {
		UserGraphStatus existing = getById(id);
		existing.setScreenName(screenName);
		save(existing, id);
		
	}
}
