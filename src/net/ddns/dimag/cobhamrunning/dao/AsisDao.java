package net.ddns.dimag.cobhamrunning.dao;

import java.math.BigInteger;
import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class AsisDao implements UniversalDao{

	public Asis findById(int id) throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Asis asis = session.get(Asis.class, id);
		session.close();
        return asis;
    }

    public Asis findByName(String name) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List asisList = session.createSQLQuery("SELECT * FROM public.asis WHERE asis = :name")
				.addEntity(Asis.class)
				.setParameter("name", name).list();
		session.close();
		if (asisList.isEmpty()){
			return null;
		}
		return (Asis) asisList.get(0);
	}
	
	public List getAvaliableAsisRange(int count) throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List asisList = session.createSQLQuery("SELECT * FROM public.asis WHERE articleheaders_id is null ORDER BY id ASC FETCH FIRST :count ROWS ONLY")
				.addEntity(Asis.class)
				.setParameter("count", count).list();
		session.close();
		return asisList;
	}

	public int getAvaliableAsisCount() throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.asis WHERE articleheaders_id is null").uniqueResult()).intValue();
		session.close();
		return count;
	}

	public int getUnprintedCount() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.asis WHERE datecreate is not null and printjob_id is null").uniqueResult()).intValue();
		session.close();
		return count;
	}
	
    public List findAll() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List asisList = session.createSQLQuery("SELECT * FROM asis").addEntity(Asis.class).list();
		session.close();
        return asisList;
    }

}
