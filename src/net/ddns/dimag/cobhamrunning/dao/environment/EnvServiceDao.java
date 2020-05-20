package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvServiceDao implements UniversalDao {
    public EnvService findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvService envService = session.get(EnvService.class, id);
        session.close();
        return envService;
    }

    public EnvService findEnvServiceByName(String name) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvService envService = (EnvService) session.createSQLQuery("SELECT * FROM public.envservice WHERE name = :name")
                .addEntity(EnvService.class).setParameter("name", name).getSingleResult();
        session.close();
        return envService;
    }

    public List<EnvService> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvService> envList = (List<EnvService>)  session.createQuery("From EnvService").list();
        session.close();
        return envList;
    }
}
