package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvModel;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvModelDao implements UniversalDao {

    public EnvModel findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvModel envModel = session.get(EnvModel.class, id);
        session.close();
        return envModel;
    }

    public EnvModel findEnvModelByName(String name) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvModel envModel = (EnvModel) session.createSQLQuery("SELECT * FROM public.envmodel WHERE name = :name")
                .addEntity(EnvModel.class).setParameter("name", name).getSingleResult();
        session.close();
        return envModel;
    }

    public List<EnvModel> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvModel> envList = (List<EnvModel>)  session.createQuery("From EnvModel").list();
        session.close();
        return envList;
    }
}
