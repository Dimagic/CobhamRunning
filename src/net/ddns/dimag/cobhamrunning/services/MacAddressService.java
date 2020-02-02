package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.MacAddressDao;
import net.ddns.dimag.cobhamrunning.models.MacAddress;



public class MacAddressService {
	private MacAddressDao macDao = new MacAddressDao();

	public MacAddressService() {	
	}

	public MacAddress findAsis(int id) {
		return macDao.findById(id);
	}
	
	public int getAvailableMacCount(){
		return macDao.getAvailableMacCount();
	}

	public void saveMac(MacAddress mac) {
		macDao.save(mac);
	}

	public void deleteMac(MacAddress mac) {
		macDao.delete(mac);
	}

	public void updateMac(MacAddress mac) {
		macDao.update(mac);
	}
	
	public MacAddress getFirstAvailableMac(){
		return macDao.getFirstAvailableMac();
	}
	
	public List<MacAddress> findAllMac() {
		return macDao.findAll();
	}

}
