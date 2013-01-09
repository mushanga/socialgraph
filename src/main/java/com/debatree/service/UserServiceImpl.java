package com.debatree.service;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.amazonbird.util.Util;
import com.debatree.data.User;

public class UserServiceImpl {
	
	public void saveUser(User user) {
		Session session = HibernateUtil.getSession();
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
		session.close();
	}

	public User getUser(long id) {
		Session session = HibernateUtil.getSession();
		User t = (User) session.get(User.class, new Long(id));
	
		session.close();
		return t;
	}
	public User getUserByName(String name) {
		Session session = HibernateUtil.getSession();
		Criteria criteria = session.createCriteria(User.class);
		List results = criteria.add(Restrictions.eq("screenName", name)).list();
		
		if(Util.getInstance().isListValid(results)){
			return (User) results.get(0);
		}else{
			return null;
		}
		
	}

}
