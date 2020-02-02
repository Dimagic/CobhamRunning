package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class DeviceDao implements UniversalDao {
	public Device findById(int id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Device.class, id);
	}

	public List findDeviceByAsis(String asis) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		return session.createSQLQuery("SELECT * FROM device WHERE asis = :asis ")
				.addEntity(Device.class).setParameter("asis", asis).list();
	}

	public List findAll() {
		List devices = HibernateSessionFactoryUtil.getSessionFactory().openSession()
				.createQuery("From Device").list();
		return devices;
	}

}
