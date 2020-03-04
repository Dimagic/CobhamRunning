package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.AsisPrintJobDao;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class AsisPrintJobService {
private AsisPrintJobDao asisPrintJobDao = new AsisPrintJobDao();
	
	public AsisPrintJobService() {	
	}
	
	public AsisPrintJob findAsisPrintJob(int id) throws CobhamRunningException {
        return asisPrintJobDao.findById(id);
    }
	
	public int getUnprintedAsisCount() throws CobhamRunningException {
		return asisPrintJobDao.getUnprintedAsisCount();
	}
	
    public void savePrintJob(AsisPrintJob asisPrintJob) throws CobhamRunningException {
    	asisPrintJobDao.save(asisPrintJob);
    }
    
    public void deletePrintJob(AsisPrintJob asisPrintJob) throws CobhamRunningException {
    	asisPrintJobDao.delete(asisPrintJob);
    }

    public void updatePrintJob(AsisPrintJob asisPrintJob) throws CobhamRunningException {
    	asisPrintJobDao.update(asisPrintJob);
    }
    
    public List<AsisPrintJob> findAllUnprinted() throws CobhamRunningException {
        return asisPrintJobDao.findAllUnprinted();
    }

    public List<AsisPrintJob> findAllPrintJobs() throws CobhamRunningException {
        return asisPrintJobDao.findAll();
    }

}
