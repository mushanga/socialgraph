package com.debatree.service;

import org.hibernate.Session;

import com.debatree.data.Tweet;

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
