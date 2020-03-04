package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.DeviceInfoDao;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class DeviceInfoService {
	private DeviceInfoDao deviceInfoDao = new DeviceInfoDao();
	
	public DeviceInfoService() {	
	}
	
	public DeviceInfo findDeviceInfoById(Long id) throws CobhamRunningException {
        return deviceInfoDao.findById(id);
    }
	
    public void saveDeviceInfo(DeviceInfo deviceInfo) throws CobhamRunningException {
    	deviceInfoDao.save(deviceInfo);    	
    }

    public void deleteDeviceInfo(DeviceInfo deviceInfo) throws CobhamRunningException {
    	deviceInfoDao.delete(deviceInfo);
    }

    public void updateDeviceInfo(DeviceInfo deviceInfo) throws CobhamRunningException {
    	deviceInfoDao.update(deviceInfo);
    }
    
    public void saveOrUpdateDeviceInfo(DeviceInfo deviceInfo) throws CobhamRunningException {
    	deviceInfoDao.saveOrUpdate(deviceInfo);
    }

    public List<DeviceInfo> findAllDevice() throws CobhamRunningException {
        return deviceInfoDao.findAll();
    }
}
