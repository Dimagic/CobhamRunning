package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvType;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvTypeDao implements UniversalDao {
    public EnvType findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvType envType = session.get(EnvType.class, id);
        session.close();
        return envType;
    }

    public EnvType findEnvTypeByName(String name) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvType envType = (EnvType) session.createSQLQuery("SELECT * FROM public.envtype WHERE name = :name")
                .addEntity(EnvType.class).setParameter("name", name).getSingleResult();
        session.close();
        return envType;
    }

    public List findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List envTypeList =  session.createQuery("From EnvType", EnvType.class).getResultList();
        session.close();
        return envTypeList;
    }
}
