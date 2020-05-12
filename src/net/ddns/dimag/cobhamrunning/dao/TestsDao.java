package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class TestsDao implements UniversalDao{
	public Tests findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Tests tests = session.get(Tests.class, id);
        session.close();
        return tests;
    }

    public List<Tests> getTestsByDevice(Device device) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List testsList = session.createSQLQuery("select * from public.tests where device_id = :dev_id order by testdate")
                .addEntity(Tests.class)
                .setParameter("dev_id", device.getId()).list();
        session.close();
        return testsList;
    }

    public void deleteTestsByDevice(Device device) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        session.createSQLQuery("delete from public.tests where device_id = :dev_id")
                .addEntity(Tests.class)
                .setParameter("dev_id", device.getId());
        session.close();
    }

	public List<Tests> findAll() throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Tests> testsList = (List<Tests>)  session.createQuery("From tests").list();
        session.close();
        return testsList;
    }

}
