package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvManufDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvManuf;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvManufService {
    private EnvManufDao envManufDao = new EnvManufDao();

    public EnvManuf findEnvManufByName(String name) throws CobhamRunningException {
        return envManufDao.findEnvManufByName(name);
    }

    public void saveEnvManuf(EnvManuf envManuf) throws CobhamRunningException {
        envManufDao.save(envManuf);
    }

    public void deleteEnvManuf(EnvManuf envManuf) throws CobhamRunningException {
        envManufDao.delete(envManuf);
    }

    public void updateEnvManuf(EnvManuf envManuf) throws CobhamRunningException {
        envManufDao.update(envManuf);
    }

    public EnvManuf find(int id) throws CobhamRunningException {
        return envManufDao.findById(id);
    }

    public List<EnvManuf> findAllEnvManuf() throws CobhamRunningException {
        return envManufDao.findAll();
    }
}
