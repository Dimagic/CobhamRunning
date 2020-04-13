package net.ddns.dimag.cobhamrunning.services;

import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.DeviceDao;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.NoResultException;

public class DeviceService {
    private final Logger LOGGER = LogManager.getLogger(this.getClass().getName());
	private DeviceDao deviceDao = new DeviceDao();
	
	public DeviceService() {	
	}
	
	public Device findDeviceById(int id) throws CobhamRunningException {
        return deviceDao.findById(id);
    }
	
	public Device findDeviceByAsis(String asis) {
	    try {
            return deviceDao.findDeviceByAsis(asis);
        } catch (NoResultException | CobhamRunningException e){}
	    return null;
    }

    public Device findDeviceBySn(String sn) {
        try {
            return deviceDao.findDeviceBySn(sn);
        } catch (NoResultException | CobhamRunningException e){}
        return null;
    }

    public void saveDevice(Device device) throws CobhamRunningException {
    	deviceDao.save(device);    	
    }

    public void saveOrUpdateDevice(Device device) throws CobhamRunningException {
	    deviceDao.saveOrUpdate(device);
    }

    public void deleteDevice(Device device) throws CobhamRunningException {
            deviceDao.delete(device);
    }

    public void updateDevice(Device device) throws CobhamRunningException {
    	deviceDao.update(device);
    }

    public List<Device> findDeviceByDateCreate(Date dateFrom, Date dateTo) throws CobhamRunningException{
	    return deviceDao.findDeviceByDateCreate(dateFrom, dateTo);
    }

    public List<Device> findDeviceByTestDate(Date dateFrom, Date dateTo) throws CobhamRunningException{
        return deviceDao.findDeviceByTestDate(dateFrom, dateTo);
    }

    public List<Device> findAllDevice() throws CobhamRunningException {
        return deviceDao.findAll();
    }
    
    
}
