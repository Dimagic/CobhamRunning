package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvLocationDao implements UniversalDao {
    public EnvLocation findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvLocation envModel = session.get(EnvLocation.class, id);
        session.close();
        return envModel;
    }

    public EnvLocation findEnvLocationByName(String location) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvLocation envLocation = (EnvLocation) session.createSQLQuery("SELECT * FROM public.envlocation WHERE location = :location")
                .addEntity(EnvLocation.class).setParameter("location", location).getSingleResult();
        session.close();
        return envLocation;
    }

    public List<EnvLocation> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvLocation> envList = (List<EnvLocation>)  session.createQuery("From EnvLocation").list();
        session.close();
        return envList;
    }
}
