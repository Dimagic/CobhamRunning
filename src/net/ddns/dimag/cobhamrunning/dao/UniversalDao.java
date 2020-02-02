package net.ddns.dimag.cobhamrunning.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public interface UniversalDao {
	public default void save(Object obj) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(obj);
        tx1.commit();
        session.close();
    }

    public default void update(Object obj) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession(); 
        Transaction tx1 = session.beginTransaction();
        session.update(obj);
        tx1.commit();
        session.close();
    }
    
    public default void delete(Object obj) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(obj);
        tx1.commit();
        session.close();
    }

}
