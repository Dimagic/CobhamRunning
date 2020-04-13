package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.Date;
import java.util.List;

public class DeviceDao implements UniversalDao {
	public Device findById(int id) throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Device device = session.get(Device.class, id);
		session.close();
		return device;
	}

	public Device findDeviceByAsis(String asis) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Device device = (Device) session.createSQLQuery("SELECT * FROM public.device WHERE asis_id = (SELECT id FROM public.asis WHERE asis = :asis)")
				.addEntity(Device.class).setParameter("asis", asis).getSingleResult();
		session.close();
		return device;
	}

	public Device findDeviceBySn(String sn) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Device device = (Device) session.createSQLQuery("SELECT * FROM public.device WHERE sn = :sn")
				.addEntity(Device.class).setParameter("sn", sn).getSingleResult();
		session.close();
		return device;
	}

	public List findDeviceByDateCreate(Date dateFrom, Date dateTo) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Date startDate = convertDatePeriod(dateFrom, 0);
		Date stopDate = convertDatePeriod(dateTo, 1);
		List journal = session.createSQLQuery("SELECT * FROM public.device WHERE datecreate BETWEEN :from AND :to")
				.addEntity(Device.class)
				.setParameter("from", startDate)
				.setParameter("to", stopDate).list();
		session.close();
		return journal;
	}

	public List findDeviceByTestDate(Date dateFrom, Date dateTo) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Date startDate = convertDatePeriod(dateFrom, 0);
		Date stopDate = convertDatePeriod(dateTo, 1);
		List journal = session.createSQLQuery("select * from public.device where id in " +
				"(select device_id from public.tests where testdate between :from and :to)")
				.addEntity(Device.class)
				.setParameter("from", startDate)
				.setParameter("to", stopDate).list();
		session.close();
		return journal;
	}

	public List findAll() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List devices = session.createQuery("From Device").list();
		session.close();
		return devices;
	}

}
