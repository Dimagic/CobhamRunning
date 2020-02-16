package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class DeviceDao implements UniversalDao {
	public Device findById(int id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Device.class, id);
	}

	public Device findDeviceByAsis(String asis) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Device device = (Device) session.createSQLQuery("SELECT * FROM public.device WHERE asis_id = (SELECT id FROM public.asis WHERE asis = :asis)")
				.addEntity(Device.class).setParameter("asis", asis).getSingleResult();
		session.close();
		return device;
	}

	public Device findDeviceBySn(String sn) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		Device device = (Device) session.createSQLQuery("SELECT * FROM public.device WHERE sn = :sn")
				.addEntity(Device.class).setParameter("sn", sn).getSingleResult();
		session.close();
		return device;
	}

	public List findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List devices = session.createQuery("From Device").list();
		session.close();
		return devices;
	}

}
