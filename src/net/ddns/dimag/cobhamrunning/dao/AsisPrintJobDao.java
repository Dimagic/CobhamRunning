package net.ddns.dimag.cobhamrunning.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class AsisPrintJobDao implements UniversalDao {
	public AsisPrintJob findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(AsisPrintJob.class, id);
    }
	
	public int getUnprintedAsisCount() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = 0;
		try {
			count = ((BigInteger) session.createSQLQuery("SELECT sum(count) FROM public.asisprintjob WHERE dateprint IS NULL").uniqueResult()).intValue();
		} catch (NullPointerException e) {
			return 0;
		} finally {
			session.close();
		}
		return count;
	}
	
	public List<AsisPrintJob> findAllUnprinted() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<AsisPrintJob> printJobList = session.createSQLQuery("SELECT * FROM public.asisprintjob WHERE dateprint IS NULL").addEntity(AsisPrintJob.class).list(); 
		session.close();
        return printJobList;
    }
	
    public List<AsisPrintJob> findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<AsisPrintJob> printJobList = session.createSQLQuery("SELECT * FROM public.asisprintjob").addEntity(AsisPrintJob.class).list(); 
		session.close();
        return printJobList;
    }

}
