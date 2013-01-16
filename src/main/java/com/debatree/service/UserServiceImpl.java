package com.debatree.service;

import org.apache.log4j.Logger;

import com.debatree.data.HibernateObject;
import com.debatree.data.User;

public class UserServiceImpl extends HibernateService {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);


	public User getUser(long id){
		
		return (User) getById(id);
	}
	public User getUserByName(String name) {
		
		return (User) getByField("screenName", name);
		
	}

	@Override
	public Class<? extends HibernateObject> getEntityClass() {
		return User.class;
	}

}
