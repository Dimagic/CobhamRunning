package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.utils.Settings;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class SettingsDao {
	public Settings findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Settings settings = session.get(Settings.class, id);
        session.close();
        return settings;
    }
	
	public void save(Settings setting) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(setting);
        tx1.commit();
        session.close();
    }

    public void update(Settings setting) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession(); 
        Transaction tx1 = session.beginTransaction();
        session.update(setting);
        tx1.commit();
        session.close();
    }
    
    public void delete(Settings setting) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(setting);
        tx1.commit();
        session.close();
    }

    public List<Settings> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Settings> settingsList = session.createQuery("From Settings").list();
        session.close();
        return settingsList;
    }

}
