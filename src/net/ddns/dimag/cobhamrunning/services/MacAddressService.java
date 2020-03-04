package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.MacAddressDao;
import net.ddns.dimag.cobhamrunning.models.MacAddress;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;


public class MacAddressService {
	private MacAddressDao macDao = new MacAddressDao();

	public MacAddressService() {	
	}

	public MacAddress findAsis(int id) throws CobhamRunningException {
		return macDao.findById(id);
	}
	
	public int getAvailableMacCount() throws CobhamRunningException {
		return macDao.getAvailableMacCount();
	}

	public void saveMac(MacAddress mac) throws CobhamRunningException {
		macDao.save(mac);
	}

	public void deleteMac(MacAddress mac) throws CobhamRunningException {
		macDao.delete(mac);
	}

	public void updateMac(MacAddress mac) throws CobhamRunningException {
		macDao.update(mac);
	}
	
	public MacAddress getFirstAvailableMac() throws CobhamRunningException {
		return macDao.getFirstAvailableMac();
	}
	
	public List<MacAddress> findAllMac() throws CobhamRunningException {
		return macDao.findAll();
	}

}
