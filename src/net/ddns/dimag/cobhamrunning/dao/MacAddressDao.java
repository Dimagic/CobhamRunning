package net.ddns.dimag.cobhamrunning.dao;

import java.math.BigInteger;
import java.util.List;

import org.apache.regexp.recompile;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.MacAddress;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class MacAddressDao implements UniversalDao{
	public MacAddress findById(int id) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		MacAddress macAddress = session.get(MacAddress.class, id);
		session.close();
        return macAddress;
    }
	
    public List findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List macList = session.createSQLQuery("SELECT * FROM macaddress").addEntity(MacAddress.class).list();
		session.close();
        return macList;
    }
    
    public MacAddress getFirstAvailableMac(){
    	Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();  	
    	List<MacAddress> rows = (List<MacAddress>) session.createSQLQuery("SELECT * FROM public.macaddress WHERE asis_id IS NULL ORDER BY id ASC FETCH FIRST 1 ROWS ONLY")
    			.addEntity(MacAddress.class).list();
 		try {
 			return rows.get(0);
 		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			session.close();
		}
		return null;
    }
    
    public int getAvailableMacCount() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		int count = ((BigInteger) session.createSQLQuery("SELECT count(*) FROM public.macaddress WHERE asis_id IS NULL").uniqueResult()).intValue();
		session.close();
		return count;
	}

}
