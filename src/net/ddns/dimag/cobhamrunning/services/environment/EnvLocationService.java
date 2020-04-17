package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvLocationDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvLocationService {
    private EnvLocationDao envLocationDao = new EnvLocationDao();

    public void saveEnvLocation(EnvLocation envLocation) throws CobhamRunningException {
        envLocationDao.save(envLocation);
    }

    public void deleteEnvLocation(EnvLocation envLocation) throws CobhamRunningException {
        envLocationDao.delete(envLocation);
    }

    public void updateEnvLocation(EnvLocation envLocation) throws CobhamRunningException {
        envLocationDao.update(envLocation);
    }

    public EnvLocation find(int id) throws CobhamRunningException {
        return envLocationDao.findById(id);
    }

    public EnvLocation findEnvLocationByName(String location) throws CobhamRunningException {
        return envLocationDao.findEnvLocationByName(location);
    }

    public List<EnvLocation> findAllEnvLocation() throws CobhamRunningException {
        return envLocationDao.findAll();
    }
}
