package net.ddns.dimag.cobhamrunning.services;

import net.ddns.dimag.cobhamrunning.dao.EnvTypeDao;
import net.ddns.dimag.cobhamrunning.models.EnvType;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvTypeService {
    private EnvTypeDao envTypeDao = new EnvTypeDao();

    public void saveEnvType(EnvType envType) throws CobhamRunningException {
        envTypeDao.save(envType);
    }

    public List<EnvType> findAllEnvType() throws CobhamRunningException {
        return envTypeDao.findAll();
    }
}
