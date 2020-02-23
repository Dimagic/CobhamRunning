package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class TestsDao implements UniversalDao{
	public Tests findById(int id) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Tests tests = session.get(Tests.class, id);
        session.close();
        return tests;
    }

    public List<Tests> getTestsByDevice(Device device){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List testsList = session.createSQLQuery("select * from public.tests where device_id = :dev_id order by testdate")
                .addEntity(Tests.class)
                .setParameter("dev_id", device.getId()).list();
        session.close();
        return testsList;

    }

	public List<Tests> findAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Tests> testsList = (List<Tests>)  session.createQuery("From tests").list();
        session.close();
        return testsList;
    }

}
