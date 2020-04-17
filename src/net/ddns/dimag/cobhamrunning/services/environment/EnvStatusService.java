package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvStatusDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvStatus;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvStatusService {
    private EnvStatusDao envStatusDao = new EnvStatusDao();

    public void saveEnvStatus(EnvStatus envStatus) throws CobhamRunningException {
        envStatusDao.save(envStatus);
    }

    public void deleteEnvStatus(EnvStatus envStatus) throws CobhamRunningException {
        envStatusDao.delete(envStatus);
    }

    public void updateEnvStatus(EnvStatus envStatus) throws CobhamRunningException {
        envStatusDao.update(envStatus);
    }

    public EnvStatus find(int id) throws CobhamRunningException {
        return envStatusDao.findById(id);
    }

    public EnvStatus findEnvStatusByName(String status) throws CobhamRunningException {
        return envStatusDao.findEnvStatusByName(status);
    }

    public List<EnvStatus> findAllEnvStatus() throws CobhamRunningException {
        return envStatusDao.findAll();
    }
}
