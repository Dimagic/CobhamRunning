package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvManuf;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvManufDao implements UniversalDao {
    public EnvManuf findEnvManufByName(String name) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvManuf envManuf = (EnvManuf) session.createSQLQuery("SELECT * FROM public.envmanuf WHERE name = :name")
                .addEntity(EnvManuf.class).setParameter("name", name).getSingleResult();
        session.close();
        return envManuf;
    }

    public EnvManuf findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvManuf envManuf = session.get(EnvManuf.class, id);
        session.close();
        return envManuf;
    }

    public List<EnvManuf> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvManuf> envList = (List<EnvManuf>)  session.createQuery("From EnvManuf").list();
        session.close();
        return envList;
    }
}
