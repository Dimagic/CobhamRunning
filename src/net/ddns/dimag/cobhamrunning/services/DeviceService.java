package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.DeviceDao;
import net.ddns.dimag.cobhamrunning.models.Device;

public class DeviceService {
	private DeviceDao deviceDao = new DeviceDao();
	
	public DeviceService() {	
	}
	
	public Device findDeviceById(int id) {
        return deviceDao.findById(id);
    }
	
	public List<Device> findDeviceByAsis(String asis) {
        return deviceDao.findDeviceByAsis(asis);
    }

    public void saveDevice(Device device) {
    	deviceDao.save(device);    	
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
