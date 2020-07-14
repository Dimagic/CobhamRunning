package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;
import java.util.Set;

import com.sun.xml.internal.ws.api.model.wsdl.WSDLPortType;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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
        Transaction transaction = session.beginTransaction();
        try{
            for (Tests test: getTestsByDevice(device)){
                session.delete(test);
            }
            transaction.commit();
        } catch (Throwable t) {
            transaction.rollback();
            MsgBox.msgException(t);
        } finally {
            session.close();
        }
    }

	public List<Tests> findAll() throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Tests> testsList = (List<Tests>)  session.createQuery("From Tests").list();
        session.close();
        return testsList;
    }

}
