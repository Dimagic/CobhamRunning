package net.ddns.dimag.cobhamrunning.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class AsisDao implements UniversalDao{
	public Asis findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Asis.class, id);
    }
	
	public List<Asis> getAvaliableAsisRange(int count) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<Asis> asisList = session.createSQLQuery("SELECT * FROM public.asis WHERE articleheaders_id is null ORDER BY id ASC FETCH FIRST :count ROWS ONLY")
				.addEntity(Asis.class)
				.setParameter("count", count).list();
		session.close();
		return asisList;
	}
//	
//	public List<Asis> getRangeWithMac(int count) {
//		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
//		List<Asis> asisList = session.createSQLQuery("SELECT * FROM public.asis WHERE articleheaders_id is null and macAddress_id not null ORDER BY id ASC FETCH FIRST :count ROWS ONLY")
//				.addEntity(Asis.class)
//				.setParameter("count", count).list();
//		session.close();
//		return asisList;
//	}
	
	public int getAvaliableAsisCount() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.asis WHERE articleheaders_id is null").uniqueResult()).intValue();
		session.close();
		return count;
	}
	
//	public int getAvaliableMacCount() {
//		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
//		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.macaddress WHERE id NOT IN (SELECT macaddress_id FROM public.asis WHERE macaddress_id IS NOT NULL)").uniqueResult()).intValue();
//		session.close();
//		return count;
//	}
	
	
	public int getUnprintedCount() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.asis WHERE datecreate is not null and printjob_id is null").uniqueResult()).intValue();
		session.close();
		return count;
	}
	
    public List<Asis> findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<Asis> asisList = session.createSQLQuery("SELECT * FROM asis").addEntity(Asis.class).list(); 
		session.close();
        return asisList;
    }

}
