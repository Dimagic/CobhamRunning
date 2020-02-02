package net.ddns.dimag.cobhamrunning.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.sun.istack.NotNull;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class DeviceDao implements UniversalDao {
	public Device findById(int id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Device.class, id);
	}

	public List<Device> findDeviceByAsis(String asis) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<Device> deviceList = session.createSQLQuery("SELECT * FROM device WHERE asis = :asis ")
				.addEntity(Device.class).setParameter("asis", asis).list();
		return deviceList;
	}

	public List<Device> findAll() {
		List<Device> devices = (List<Device>) HibernateSessionFactoryUtil.getSessionFactory().openSession()
				.createQuery("From Device").list();
		return devices;
	}

}
