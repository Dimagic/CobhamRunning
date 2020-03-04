package net.ddns.dimag.cobhamrunning.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class ShippingJournalDao implements UniversalDao {
    public ShippingSystem findById(int id) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        ShippingSystem shippingSystem = session.get(ShippingSystem.class, id);
        session.close();
        return shippingSystem;
    }

    public boolean isDeviceInJournal(Device device) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List journal = session.createSQLQuery("select * from public.shippingjournal where device_id in " +
				"(select id from public.device where asis_id in " +
				"(select id from public.asis where asis = :asis)) or device_id in " +
				"(select id from public.device where sn = :sn)")
                .addEntity(ShippingSystem.class)
                .setParameter("asis", device.getAsis().getAsis())
                .setParameter("sn", device.getSn()).list();
        session.close();
        if (journal.size() > 0) {
            return true;
        }
        return false;
    }

    public List getJournalByDate(Date dateFrom, Date dateTo) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List journal = session.createSQLQuery("SELECT * FROM public.shippingjournal WHERE dateship BETWEEN :from AND :to")
                .addEntity(ShippingSystem.class)
                .setParameter("from", dateFrom)
                .setParameter("to", dateTo).list();
        session.close();
        return journal;
    }

    public List<ShippingSystem> findAll() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<ShippingSystem> journal = (List<ShippingSystem>) session.createQuery("From shippingjournal").list();
        session.close();
        return journal;
    }
}
