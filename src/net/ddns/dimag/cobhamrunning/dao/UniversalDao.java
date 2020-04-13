package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.postgresql.util.PSQLException;

import javax.persistence.PersistenceException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public interface UniversalDao {
    Logger LOGGER = LogManager.getLogger(UniversalDao.class.getName());

    default void save(Object obj) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        try {
            session.save(obj);
            tx1.commit();
        } catch (Exception e){
            tx1.rollback();
            session.close();
            if (e.getCause() instanceof org.postgresql.util.PSQLException){
                throw new CobhamRunningException(e.getCause().getMessage());
            }
            throw new CobhamRunningException(e);
        }
    }

    default void update(Object obj) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(obj);
        tx1.commit();
        session.close();
    }

    default void saveOrUpdate(Object obj) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.saveOrUpdate(obj);
        tx1.commit();
        session.close();
    }

    default void delete(Object obj) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(obj);
        try {
            tx1.commit();
        } catch (PersistenceException hibernateEx) {
            LOGGER.error(hibernateEx);
            try {
                tx1.rollback();
            } catch (RuntimeException runtimeEx) {
                MsgBox.msgException("Couldn’t Roll Back Transaction", runtimeEx);
            }
//            hibernateEx.printStackTrace();
        } finally {
            if (session != null) {
                session.close();
            }
            MsgBox.msgWarning("Delete operation", "Can't delete this object.\nFor more information see log file");
        }
        session.close();
    }

    default Date convertDatePeriod(Date date, int format){
        SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (format == 0){
                return formatter.parse(String.format("%s 00:00:00", date));
            } else if (format == 1){
                return formatter.parse(String.format("%s 23:59:59", date));
            } else {
                throw new CobhamRunningException("Incorrect format number");
            }
        } catch (ParseException | CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
            return null;
        }
    }
}
