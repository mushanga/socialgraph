
package com.debatree.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.amazonbird.config.PropsConfigMgrImpl;


/**
 * A very simple Hibernate helper class that holds the SessionFactory as a singleton.
 * <p>
 * The only job of this helper class is to give your application code easy
 * access to the <tt>SessionFactory</tt>. It initializes the <tt>SessionFactory</tt>
 * when it is loaded (static initializer) and you can easily open new
 * <tt>Session</tt>s. Only really useful for trivial applications.
 *
 * @author christian@hibernate.org
 */
public class HibernateUtil {
	private static Log log = LogFactory.getLog(HibernateUtil.class);

	private static SessionFactory sessionFactory = null;
	
	public static void initSessionFactory(String userName, String password){
		try {
			AnnotationConfiguration cfg = new AnnotationConfiguration();
			//cfg.addAnnotatedClass(com.debatree.data.Tweet.class);
			cfg.addAnnotatedClass(com.debatree.data.User.class);
			cfg.addAnnotatedClass(com.debatree.data.UserFriendListStatus.class);
			cfg.addAnnotatedClass(com.debatree.data.TwitstreetUserTokenStatus.class);
			cfg.addAnnotatedClass(com.debatree.data.UserGraphStatus.class);
			
			cfg.setProperty("hibernate.connection.username", userName);
			cfg.setProperty("hibernate.connection.password", password);
			cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
			cfg.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/debatree");
			cfg.setProperty("hibernate.connection.pool_size", "10");
			cfg.setProperty("hibernate.hbm2ddl.auto", "update");
			cfg.setProperty("hibernate.show_sql", "false");

			
			
			sessionFactory = cfg.buildSessionFactory();
		} catch (Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			log.error("Building SessionFactory failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	private static void initSessionFactory(){
		initSessionFactory(PropsConfigMgrImpl.getInstance().getDbUserName(), PropsConfigMgrImpl.getInstance().getDbPassword());
	}

	

	public static Session getSession()
		throws HibernateException {
		if(sessionFactory == null){
			initSessionFactory();
		}
		return sessionFactory.openSession();
	}
}