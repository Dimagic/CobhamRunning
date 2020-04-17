package net.ddns.dimag.cobhamrunning.dao.environment;

import net.ddns.dimag.cobhamrunning.dao.UniversalDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvStatus;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "envstatus", uniqueConstraints = { @UniqueConstraint(columnNames = "status")})
public class EnvStatusDao implements UniversalDao {
    public EnvStatus findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvStatus envStatus = session.get(EnvStatus.class, id);
        session.close();
        return envStatus;
    }

    public EnvStatus findEnvStatusByName(String status) throws CobhamRunningException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        EnvStatus envStatus = (EnvStatus) session.createSQLQuery("SELECT * FROM public.envstatus WHERE status = :status")
                .addEntity(EnvStatus.class).setParameter("status", status).getSingleResult();
        session.close();
        return envStatus;
    }

    public List<EnvStatus> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<EnvStatus> envList = (List<EnvStatus>)  session.createQuery("From EnvStatus").list();
        session.close();
        return envList;
    }
}
