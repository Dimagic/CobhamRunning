package net.ddns.dimag.cobhamrunning.utils;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

public class HibernateUtils implements MsgBox {
    
	private static SessionFactory sessionFactory;
	private static final ThreadLocal<Session> threadlocal=new ThreadLocal<Session>();

	static{
		try {
			Configuration configuration=new Configuration().configure();
		 	sessionFactory=configuration.buildSessionFactory();
		} catch (Throwable ex) { 
			throw new ExceptionInInitializerError(ex);
		} 
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static Session getSession() throws HibernateException{
		Session session=threadlocal.get();
		if (session==null || !session.isOpen()) {
			if (sessionFactory==null) {
				rebuildSessionFactory();
			}
			session=(sessionFactory!=null)?sessionFactory.openSession():null;
			threadlocal.set(session);
		}
		return session;  
	}
	
	public static void closeSession() throws HibernateException{
		Session session=threadlocal.get();
		threadlocal.set(null);
		if (session!=null) {
			session.close();
		}
	}
	
	public static void rebuildSessionFactory(){
		try {
		 Configuration configuration= new Configuration().configure();
		 sessionFactory=configuration.buildSessionFactory();
		} catch (Exception e) {
			MsgBox.msgException(e);
		}
	}
	
	public static List<?> findAllRecords(Object o) {	
		Session session = getSession();
	    CriteriaBuilder cb = session.getCriteriaBuilder();
	    CriteriaQuery<Object> cq = (CriteriaQuery<Object>) cb.createQuery(o.getClass());
	    Root<Object> rootEntry = (Root<Object>) cq.from(o.getClass());
	    CriteriaQuery<Object> all = cq.select(rootEntry); 
	    TypedQuery<Object> allQuery = session.createQuery(all);
	    return allQuery.getResultList();
	}
	
//	public static List<CurrentAtrRfb> findAllCurrentAtrRfb() {
//		Session session = getSession();
//	    CriteriaBuilder cb = session.getCriteriaBuilder();
//	    CriteriaQuery<CurrentAtrRfb> cq = cb.createQuery(CurrentAtrRfb.class);
//	    Root<CurrentAtrRfb> rootEntry = cq.from(CurrentAtrRfb.class);
//	    CriteriaQuery<CurrentAtrRfb> all = cq.select(rootEntry); 
//	    TypedQuery<CurrentAtrRfb> allQuery = session.createQuery(all);
//	    return allQuery.getResultList();
//	}
//
//	public static List<AtrParameter> findAllAtrParameter(long atr_id) {
//		Session session = getSession();
//		String hql = String.format("FROM AtrParameter WHERE atr_id = %s", atr_id);
//		Query query = session.createQuery(hql);
//		List results = query.list();
//		return results;
//	}
//	
//	public static void SaveOrUpdateAtrParameter(AtrParameter s) {
//		Session session = getSession();
//		session.beginTransaction();
//		session.update(s);
//		session.getTransaction().commit();
//		session.close();
//	} 
//	
//	public static CurrentAtrRfb getAtrRfbByName(String name){
//		Session session = getSession();
//		String hql = String.format("FROM CurrentAtrRfb WHERE boardname = '%s'", name);
//		Query query = session.createQuery(hql);
//		CurrentAtrRfb result = (CurrentAtrRfb) query.getSingleResult();
//		return result;
//	}
	
	public static void shutdown(){
		getSessionFactory().close();
	}

	
}
