package com.debatree.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.amazonbird.util.Util;
import com.debatree.data.User;

public class UserServiceImpl {

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);
	public void saveUser(User user) {
		Session session = HibernateUtil.getSession();
	
		try {
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
		
		} catch (HibernateException e) {
			logger.error(e.getMessage(),e);
		}finally{
			session.close();
		}
	}

	public User getUser(long id){
		
		Session session = HibernateUtil.getSession();
		
		try {
			User t = (User) session.get(User.class, new Long(id));

			return t;
		} catch (HibernateException e) {
			
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		return null;
	}
	public User getUserByName(String name) {
		Session session = HibernateUtil.getSession();
		
		try {
			Criteria criteria = session.createCriteria(User.class);
			List results = criteria.add(Restrictions.eq("screenName", name)).list();
			
			if(Util.getInstance().isListValid(results)){
				return (User) results.get(0);
			}else{
				return null;
			}
		} catch (HibernateException e) {
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		return null;
		
	}

}
