package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvHistory;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class EnvHistoryDao implements UniversalDao {
    public EnvHistory findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvHistory envHistory = session.get(EnvHistory.class, id);
        session.close();
        return envHistory;
    }

    public List findEnvHistoryByDevice(EnvDevice envDevice) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List journal = session.createSQLQuery("select * from public.envhistory where envdevice_id = :dev_id order by date")
                .addEntity(EnvHistory.class)
                .setParameter("dev_id", envDevice.getId()).list();
        session.close();
        return journal;
    }
}
