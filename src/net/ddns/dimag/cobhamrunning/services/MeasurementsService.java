package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.MeasurementsDao;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;

public class MeasurementsService {
	private MeasurementsDao measDao = new MeasurementsDao();
	
	public MeasurementsService() {	
	}
	
	public Measurements findMeas(int id) {
        return measDao.findById(id);
    }

    public void saveMeas(Measurements meas) {
    	measDao.save(meas);
    }
    
    public void deleteMeas(Measurements meas) {
    	measDao.delete(meas);
    }

    public void updateMeas(Tests tests) {
    	measDao.update(tests);
    }

    public List<Measurements> findAllMeas() {
        return measDao.findAll();
    }
}
