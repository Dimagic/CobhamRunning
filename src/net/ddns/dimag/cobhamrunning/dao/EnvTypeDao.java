package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.EnvType;
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

    public List findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List envTypeList =  session.createQuery("From EnvType", EnvType.class).getResultList();
        session.close();
        return envTypeList;
    }
}
