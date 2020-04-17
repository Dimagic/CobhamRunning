package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvTypeDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvType;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvTypeService {
    private EnvTypeDao envTypeDao = new EnvTypeDao();

    public void saveEnvType(EnvType envType) throws CobhamRunningException {
        envTypeDao.save(envType);
    }

    public EnvType findEnvTypeByName(String name) throws CobhamRunningException {
        return envTypeDao.findEnvTypeByName(name);
    }

    public List<EnvType> findAllEnvType() throws CobhamRunningException {
        return envTypeDao.findAll();
    }
}
