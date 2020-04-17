package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvDeviceDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
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

    public List<EnvDevice> findAllByLocation(EnvLocation envLocation) throws CobhamRunningException {
        return envDeviceDao.findAllByLocation(envLocation);
    }

    public List<EnvDevice> findAllEnvDevice() throws CobhamRunningException {
        return envDeviceDao.findAll();
    }
}
