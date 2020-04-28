package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.SettingsDao;
import net.ddns.dimag.cobhamrunning.utils.Settings;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class SettingsService {
	private SettingsDao settingsDao = new SettingsDao();
	
	public SettingsService() {	
	}
	
	public Settings findSetting(int id) throws CobhamRunningException {
        return settingsDao.findById(id);
    }

    public void saveSetting(Settings setting) throws CobhamRunningException {
    	settingsDao.save(setting);
    }
    
    public void deleteSetting(Settings setting) throws CobhamRunningException {
    	settingsDao.delete(setting);
    }

    public void updateSetting(Settings setting) throws CobhamRunningException {
    	settingsDao.update(setting);
    }

    public List<Settings> findAllSettings() throws CobhamRunningException {
        return settingsDao.findAll();
    }

}
