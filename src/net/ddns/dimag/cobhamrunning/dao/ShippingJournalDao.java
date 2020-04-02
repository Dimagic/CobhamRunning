package net.ddns.dimag.cobhamrunning.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
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
        return journal.size() > 0;
    }

    public List getJournalByFilter(String filter, Date dateFrom, Date dateTo) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = formatter.parse(String.format("%s 00:00:00", dateFrom));
            Date stopDate = formatter.parse(String.format("%s 23:59:59", dateTo));
            List journal = session.createSQLQuery("select * from public.shippingjournal where " +
                    "(device_id in (select id from public.device where asis_id " +
                    "in (select id from public.asis where articleheaders_id " +
                    "in (select id from public.articleheaders where article like :article))) or " +
                    "device_id in (select id from public.device where asis_id " +
                    "in (select id from public.asis where asis like :asis)) or " +
                    "device_id in (select id from public.device where sn like :sn)) and " +
                    "dateship BETWEEN :from AND :to")
                    .addEntity(ShippingSystem.class)
                    .setParameter("article", String.format("%%%s%%", filter.toUpperCase()))
                    .setParameter("asis", String.format("%%%s%%", filter.toUpperCase()))
                    .setParameter("sn", String.format("%%%s%%", filter.toUpperCase()))
                    .setParameter("from", startDate)
                    .setParameter("to", stopDate).list();
            session.close();
            return journal;
        } catch (ParseException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
            return null;
        }

    }

    public List getJournalByDate(Date dateFrom, Date dateTo) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate;
        Date stopDate;
        try {
            startDate = formatter.parse(String.format("%s 00:00:00", dateFrom));
            stopDate = formatter.parse(String.format("%s 23:59:59", dateTo));
        } catch (ParseException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
            return null;
        }

        List journal = session.createSQLQuery("SELECT * FROM public.shippingjournal WHERE dateship BETWEEN :from AND :to")
                .addEntity(ShippingSystem.class)
                .setParameter("from", startDate)
                .setParameter("to", stopDate).list();
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
