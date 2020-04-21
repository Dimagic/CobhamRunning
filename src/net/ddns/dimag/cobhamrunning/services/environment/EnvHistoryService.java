package net.ddns.dimag.cobhamrunning.services.environment;

import net.ddns.dimag.cobhamrunning.dao.environment.EnvHistoryDao;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvHistory;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

import java.util.List;

public class EnvHistoryService {
    private EnvHistoryDao envHistoryDao = new EnvHistoryDao();

    public void saveEnvHistory(EnvHistory envHistory) throws CobhamRunningException {
        envHistoryDao.save(envHistory);
    }

    public void deleteEnvHistory(EnvHistory envHistory) throws CobhamRunningException {
        envHistoryDao.delete(envHistory);
    }

    public void updateEnvHistory(EnvHistory envHistory) throws CobhamRunningException {
        envHistoryDao.update(envHistory);
    }

    public List findEnvHistoryByDevice(EnvDevice envDevice) throws CobhamRunningException {
        return envHistoryDao.findEnvHistoryByDevice(envDevice);
    }

    public EnvHistory find(int id) throws CobhamRunningException {
        return envHistoryDao.findById(id);
    }
}
