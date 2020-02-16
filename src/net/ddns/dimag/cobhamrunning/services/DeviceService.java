package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.DeviceDao;
import net.ddns.dimag.cobhamrunning.models.Device;

import javax.persistence.NoResultException;

public class DeviceService {
	private DeviceDao deviceDao = new DeviceDao();
	
	public DeviceService() {	
	}
	
	public Device findDeviceById(int id) {
        return deviceDao.findById(id);
    }
	
	public Device findDeviceByAsis(String asis) {
	    try {
            return deviceDao.findDeviceByAsis(asis);
        } catch (NoResultException e){}
	    return null;
    }

    public Device findDeviceBySn(String sn) {
        try {
            return deviceDao.findDeviceBySn(sn);
        } catch (NoResultException e){}
        return null;
    }

    public void saveDevice(Device device) {
    	deviceDao.save(device);    	
    }

    public void saveOrUpdateDevice(Device device){
	    deviceDao.saveOrUpdate(device);
    }

    public void deleteDevice(Device device) {
    	deviceDao.delete(device);
    }

    public void updateDevice(Device device) {
    	deviceDao.update(device);
    }

    public List<Device> findAllDevice() {
        return deviceDao.findAll();
    }
    
    
}
