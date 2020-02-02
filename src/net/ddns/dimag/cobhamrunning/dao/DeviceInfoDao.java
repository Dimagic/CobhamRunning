package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class DeviceInfoDao implements UniversalDao {
	public DeviceInfo findById(Long id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(DeviceInfo.class, id);
	}

	public List<DeviceInfo> findAll() {
		List<DeviceInfo> devicesInfo = (List<DeviceInfo>) HibernateSessionFactoryUtil.getSessionFactory().openSession()
				.createQuery("From DeviceInfo").list();
		return devicesInfo;
	}
}
