package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.SettingsDao;
import net.ddns.dimag.cobhamrunning.models.Settings;

public class SettingsService {
	private SettingsDao settingsDao = new SettingsDao();
	
	public SettingsService() {	
	}
	
	public Settings findSetting(int id) {
        return settingsDao.findById(id);
    }

    public void saveSetting(Settings setting) {
    	settingsDao.save(setting);
    }
    
    public void deleteSetting(Settings setting) {
    	settingsDao.delete(setting);
    }

    public void updateSetting(Settings setting) {
    	settingsDao.update(setting);
    }

    public List<Settings> findAllSettings() {
        return settingsDao.findAll();
    }

}
