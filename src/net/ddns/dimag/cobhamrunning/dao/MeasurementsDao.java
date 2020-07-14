package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;
import java.util.Set;

import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class MeasurementsDao implements UniversalDao{
	public Measurements findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Measurements measurements = session.get(Measurements.class, id);
        session.close();
        return measurements;
    }

    public List<Measurements> getMeasureSetByTest(Tests test) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List measList = session.createSQLQuery("select * from public.measurements where test_id = :test_id order by measurenumber")
                .addEntity(Measurements.class)
                .setParameter("test_id", test.getId()).list();
        session.close();
        return measList;
    }

	public List<Measurements> findAll() throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Measurements> meases = (List<Measurements>)  session.createQuery("From Measurements").list();
        session.close();
        return meases;
    }

}
