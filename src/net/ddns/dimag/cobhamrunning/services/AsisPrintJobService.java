package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.AsisPrintJobDao;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;

public class AsisPrintJobService {
private AsisPrintJobDao asisPrintJobDao = new AsisPrintJobDao();
	
	public AsisPrintJobService() {	
	}
	
	public AsisPrintJob findAsisPrintJob(int id) {
        return asisPrintJobDao.findById(id);
    }
	
	public int getUnprintedAsisCount(){
		return asisPrintJobDao.getUnprintedAsisCount();
	}
	
    public void savePrintJob(AsisPrintJob asisPrintJob) {
    	asisPrintJobDao.save(asisPrintJob);
    }
    
    public void deletePrintJob(AsisPrintJob asisPrintJob) {
    	asisPrintJobDao.delete(asisPrintJob);
    }

    public void updatePrintJob(AsisPrintJob asisPrintJob) {
    	asisPrintJobDao.update(asisPrintJob);
    }
    
    public List<AsisPrintJob> findAllUnprinted() {
        return asisPrintJobDao.findAllUnprinted();
    }

    public List<AsisPrintJob> findAllPrintJobs() {
        return asisPrintJobDao.findAll();
    }

}
