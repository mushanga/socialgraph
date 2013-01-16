package com.debatree.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.amazonbird.util.Util;
import com.debatree.data.HibernateObject;
import com.debatree.data.User;
import com.debatree.data.UserFriendListStatus;

public class UserFriendsStatusImpl extends HibernateService {

	private static Logger logger = Logger.getLogger(UserFriendsStatusImpl.class);

	public List<Long> getProtectedUsers() {
		List<HibernateObject> list = getListByField("status", UserFriendListStatus.PROTECTED);

		return usersToIdList(list);
	}

	@Override
	public UserFriendListStatus getById(long id) {
		return (UserFriendListStatus) super.getById(id);
	}
	public List<Long> getWaitingUsers() {
		List<HibernateObject> list = getListByField("status", UserFriendListStatus.WAITING);

		return usersToIdList(list);
	}

	

	public void setUserProtected(long id) {
		UserFriendListStatus ufls = new UserFriendListStatus(id, UserFriendListStatus.PROTECTED);
		save(ufls, id);
	}

	@Override
	public Class<? extends HibernateObject> getEntityClass() {
		return UserFriendListStatus.class;
	}
	private List<Long> usersToIdList(List<HibernateObject> list) {

		List<Long> ids = new ArrayList<Long>();
		for (HibernateObject obj : list) {

			UserFriendListStatus ufls = (UserFriendListStatus) obj;

			ids.add(ufls.getId());
		}
		return ids;
	}

	public boolean userIsProtected(long userId) {

		UserFriendListStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserFriendListStatus.PROTECTED);
		} catch (Exception e) {
			
		}
		return false;
		
	}
	public boolean userIsWaiting(long userId) {

		UserFriendListStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserFriendListStatus.WAITING);
		} catch (Exception e) {
			setUserWaiting(userId);
		}
		return true;
		
	}

	public void setUserCompleted(long userId) {
		save(new UserFriendListStatus(userId, UserFriendListStatus.COMPLETED), userId);
	}
	public void setUserInProgress(long userId) {
		save(new UserFriendListStatus(userId, UserFriendListStatus.IN_PROGRESS), userId);
	}


	public boolean userIsCompleted(long userId) {

		UserFriendListStatus b = null;
		
		try {
			b = getById(userId);
			return b.getStatus().equals(UserFriendListStatus.COMPLETED);
		} catch (Exception e) {
			
		}
		return false;
		
	}

	public void setUserWaiting(long id) {
		save(new UserFriendListStatus(id, UserFriendListStatus.WAITING), id);
	}
}
