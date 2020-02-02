package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.models.Settings;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class SettingsDao {
	public Settings findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Settings.class, id);
    }
	
	public void save(Settings setting) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(setting);
        tx1.commit();
        session.close();
    }

    public void update(Settings setting) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession(); 
        Transaction tx1 = session.beginTransaction();
        session.update(setting);
        tx1.commit();
        session.close();
    }
    
    public void delete(Settings setting) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(setting);
        tx1.commit();
        session.close();
    }

    public List<Settings> findAll() {
        List<Settings> settings = (List<Settings>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From Settings").list();
        return settings;
    }

}
