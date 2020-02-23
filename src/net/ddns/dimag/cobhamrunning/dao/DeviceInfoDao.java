package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class DeviceInfoDao implements UniversalDao {
	public DeviceInfo findById(Long id) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		DeviceInfo deviceInfo = session.get(DeviceInfo.class, id);
		session.close();
		return deviceInfo;
	}

	public List<DeviceInfo> findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<DeviceInfo> deviceInfoList = session.createQuery("From DeviceInfo").list();
		session.close();
		return deviceInfoList;
	}
}
