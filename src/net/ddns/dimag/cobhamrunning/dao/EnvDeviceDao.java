package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.EnvDevice;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvDeviceDao implements UniversalDao {
    public EnvDevice findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvDevice envDevice = session.get(EnvDevice.class, id);
        session.close();
        return envDevice;
    }

    public List<EnvDevice> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvDevice> envList = (List<EnvDevice>)  session.createQuery("From envdevice").list();
        session.close();
        return envList;
    }
}
