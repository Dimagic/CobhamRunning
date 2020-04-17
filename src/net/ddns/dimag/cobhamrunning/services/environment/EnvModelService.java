package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvModelDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvModel;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvModelService {
    private EnvModelDao envModelDao = new EnvModelDao();

    public void saveEnvModel(EnvModel envModel) throws CobhamRunningException {
        envModelDao.save(envModel);
    }

    public void deleteEnvModel(EnvModel envModel) throws CobhamRunningException {
        envModelDao.delete(envModel);
    }

    public void updateEnvModel(EnvModel envModel) throws CobhamRunningException {
        envModelDao.update(envModel);
    }

    public EnvModel find(int id) throws CobhamRunningException {
        return envModelDao.findById(id);
    }

    public EnvModel findEnvModelByName(String name) throws CobhamRunningException {
        return envModelDao.findEnvModelByName(name);
    }

    public List<EnvModel> findAllEnvModel() throws CobhamRunningException {
        return envModelDao.findAll();
    }
}
