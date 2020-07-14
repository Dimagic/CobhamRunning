package net.ddns.dimag.cobhamrunning.services;

import java.util.*;

import net.ddns.dimag.cobhamrunning.dao.MeasurementsDao;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class MeasurementsService {
	private MeasurementsDao measDao = new MeasurementsDao();
	
	public MeasurementsService() {	
	}
	
	public Measurements findMeas(int id) throws CobhamRunningException {
        return measDao.findById(id);
    }

    public void saveMeas(Measurements meas) throws CobhamRunningException {
    	measDao.save(meas);
    }

    public boolean saveSet(Set<Measurements> measSet) throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            for (Measurements meas: measSet){
                meas.setMeasStatus();
                session.save(meas);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            tx.rollback();
            MsgBox.msgWarning("Import articles", e.getMessage());
            return false;
        } finally {
            session.close();
        }
    }

    public List<Measurements> getMeasureSetByTest(Tests test) throws CobhamRunningException {
	    return measDao.getMeasureSetByTest(test);
    }

//    public long getTestRunningTime(Tests test) throws CobhamRunningException {
//	    Date minDate = null;
//	    Date maxDate = null;
//	    for (Measurements meas: getMeasureSetByTest(test)){
//	        if (minDate == null){
//	            minDate = meas.getMeasDate();
//            } else if (minDate.getTime() > meas.getMeasDate().getTime()) {
//	            minDate = meas.getMeasDate();
//            }
//	        if (maxDate == null){
//	            maxDate = meas.getMeasDate();
//            } else if (maxDate.getTime() < meas.getMeasDate().getTime()) {
//	            maxDate = meas.getMeasDate();
//            }
//        }
//	    if (minDate != null && maxDate != null) {
//            return maxDate.getTime() - minDate.getTime();
//        }
//	    MsgBox.msgWarning("Test running time", "Can`t get test running time");
//	    return 0;
//    }

    public void deleteMeas(Measurements meas) throws CobhamRunningException {
    	measDao.delete(meas);
    }

    public void updateMeas(Measurements meas) throws CobhamRunningException {
    	measDao.update(meas);
    }

    public List<Measurements> findAllMeas() throws CobhamRunningException {
        return measDao.findAll();
    }
}
