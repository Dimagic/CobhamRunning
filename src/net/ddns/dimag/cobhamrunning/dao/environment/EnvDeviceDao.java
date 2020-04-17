package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
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

    public List findAllByLocation(EnvLocation envLocation) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        String query = String.format("select * from public.envdevice where envlocation_id = %s", envLocation.getId());
        List deviceList = session.createSQLQuery(query).addEntity(EnvDevice.class).list();
        session.close();
        return deviceList;
    }

    public List<EnvDevice> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvDevice> envList = (List<EnvDevice>)  session.createQuery("From EnvDevice").list();
        session.close();
        return envList;
    }
}
