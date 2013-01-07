package com.debatree.service;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.debatree.data.Tweet;
import com.sun.jna.platform.win32.Advapi32Util.Account;

public class TweetServiceImpl {
	
	public void saveTweet(Tweet tweet) {
		Session session = HibernateUtil.getSession();
		session.beginTransaction();
		session.save(tweet);
		session.getTransaction().commit();
		session.close();
	}
	
	public Tweet getTweet(long id) {
		Session session = HibernateUtil.getSession();
		Tweet t = (Tweet) session.get(Tweet.class, new Long(id));
	
		session.close();
		return t;
	}

}
