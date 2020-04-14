package net.ddns.dimag.cobhamrunning.services;

import net.ddns.dimag.cobhamrunning.dao.EnvDeviceDao;
import net.ddns.dimag.cobhamrunning.models.EnvDevice;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvDeviceService {
    private EnvDeviceDao envDeviceDao = new EnvDeviceDao();

    public EnvDevice findEnvDevice(int id) throws CobhamRunningException {
        return envDeviceDao.findById(id);
    }

    public void saveEnvDevice(EnvDevice envDevice) throws CobhamRunningException {
        envDeviceDao.save(envDevice);
    }

    public void deleteEnvDevice(EnvDevice envDevice) throws CobhamRunningException {
        envDeviceDao.delete(envDevice);
    }

    public void updateEnvDevice(EnvDevice envDevice) throws CobhamRunningException {
        envDeviceDao.update(envDevice);
    }

    public List<EnvDevice> findAllEnvDevice() throws CobhamRunningException {
        return envDeviceDao.findAll();
    }
}
