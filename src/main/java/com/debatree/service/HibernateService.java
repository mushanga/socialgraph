package com.debatree.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import com.amazonbird.util.Util;
import com.debatree.data.HibernateObject;

public abstract class HibernateService {

	private static Logger logger = Logger.getLogger(HibernateService.class);
	public abstract Class<? extends HibernateObject> getEntityClass();

	public synchronized void save(HibernateObject obj, long id) {
		Session session = HibernateUtil.getSession();

		HibernateObject t = null;
		if(id>-1){
			t = (HibernateObject) session.get(obj.getClass(), id);
		}
		
		try {
			session.beginTransaction();
			if(t!=null){
				t.copyPropertiesFrom(obj);
				session.update(t);
			}else{
				session.save(obj);
				
			}
			session.getTransaction().commit();
		
		} catch (HibernateException e) {
			session.getTransaction().rollback();
			logger.error("Id: "+id+"\nObject: \n"+obj.toString());
			logger.error(e.getMessage(),e);
		}finally{
			session.close();
		}
	}

	
	public HibernateObject getById(long id){
		
		Session session = HibernateUtil.getSession();
		
		try {
			HibernateObject t = (HibernateObject) session.get(getEntityClass(), new Long(id));

			return t;
		} catch (HibernateException e) {
			
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		return null;
	}
	public HibernateObject getByField(String fieldName,Object fieldValue) {
		HibernateObject obj = null;
		try{
			obj = getListByField(fieldName, fieldValue).get(0);
		}catch(Exception ex){
			
		}
		return obj;
		
	}
	public List<HibernateObject> getListByField(String fieldName,Object fieldValue) {
		Session session = HibernateUtil.getSession();
		List<HibernateObject> results = null;
		try {
			Criteria criteria = session.createCriteria(getEntityClass());
			results = criteria.add(Restrictions.eq(fieldName, fieldValue)).list();
			
			
		} catch (HibernateException e) {
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		Util.getInstance();
		if(!Util.isListValid(results)){
			results = new ArrayList<HibernateObject>();
		}
		
		return results;
	}

	public List<HibernateObject> getListByCriterion(Criterion criterion) {
		Session session = HibernateUtil.getSession();
		List<HibernateObject> results = null;
		try {
			Criteria criteria = session.createCriteria(getEntityClass());
			results = criteria.add(criterion).list();
			
			
		} catch (HibernateException e) {
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		Util.getInstance();
		if(!Util.isListValid(results)){
			results = new ArrayList<HibernateObject>();
		}
		
		return results;
	}

	public List<HibernateObject> getListByCriteria(List<Criterion> criteria) {
		Session session = HibernateUtil.getSession();
		List<HibernateObject> results = null;
		try {
			Criteria crit = session.createCriteria(getEntityClass());

			for(Criterion criterion : criteria){
				crit.add(criterion);
			}
			results = crit.list();
		} catch (HibernateException e) {
			logger.error(e.getMessage(),e);
		}finally{

			session.close();
		}
		Util.getInstance();
		if(!Util.isListValid(results)){
			results = new ArrayList<HibernateObject>();
		}
		
		return results;
	}
	
	 

}
