package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvServiceDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvServiceService {
    private EnvServiceDao envServiceDao = new EnvServiceDao();

    public void saveEnvService(EnvService envService) throws CobhamRunningException {
        envServiceDao.save(envService);
    }

    public void deleteEnvService(EnvService envService) throws CobhamRunningException {
        envServiceDao.delete(envService);
    }

    public void updateEnvService(EnvService envService) throws CobhamRunningException {
        envServiceDao.update(envService);
    }

    public EnvService find(int id) throws CobhamRunningException {
        return envServiceDao.findById(id);
    }

    public EnvService findEnvServiceByName(String name) throws CobhamRunningException {
        return envServiceDao.findEnvServiceByName(name);
    }

    public List<EnvService> findAllEnvService() throws CobhamRunningException {
        return envServiceDao.findAll();
    }
}
