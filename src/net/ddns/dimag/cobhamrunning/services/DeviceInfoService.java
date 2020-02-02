package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.DeviceInfoDao;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;

public class DeviceInfoService {
	private DeviceInfoDao deviceInfoDao = new DeviceInfoDao();
	
	public DeviceInfoService() {	
	}
	
	public DeviceInfo findDeviceInfoById(Long id) {
        return deviceInfoDao.findById(id);
    }
	
    public void saveDeviceInfo(DeviceInfo deviceInfo) {
    	deviceInfoDao.save(deviceInfo);    	
    }

    public void deleteDeviceInfo(DeviceInfo deviceInfo) {
    	deviceInfoDao.delete(deviceInfo);
    }

    public void updateDeviceInfo(DeviceInfo deviceInfo) {
    	deviceInfoDao.update(deviceInfo);
    }
    
    public void saveOrUpdateDeviceInfo(DeviceInfo deviceInfo){
    	try {
    		saveDeviceInfo(deviceInfo);
    	} catch (Exception e) {
    		
    		deviceInfoDao.update(deviceInfo);

		}
    }

    public List<DeviceInfo> findAllDevice() {
        return deviceInfoDao.findAll();
    }
}
