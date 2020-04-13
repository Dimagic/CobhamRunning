package net.ddns.dimag.cobhamrunning.dao;

import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

import java.util.List;

public class DeviceInfoDao implements UniversalDao {
	public DeviceInfo findById(Long id) throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		DeviceInfo deviceInfo = session.get(DeviceInfo.class, id);
		session.close();
		return deviceInfo;
	}

	public List<DeviceInfo> findAll() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<DeviceInfo> deviceInfoList = session.createQuery("From DeviceInfo").list();
		session.close();
		return deviceInfoList;
	}
}
