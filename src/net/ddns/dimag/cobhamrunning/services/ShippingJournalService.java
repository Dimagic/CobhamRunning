package net.ddns.dimag.cobhamrunning.services;

import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.ShippingJournalDao;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class ShippingJournalService {
private ShippingJournalDao shippingJournalDao = new ShippingJournalDao();
	
	public ShippingJournalService() {	
	}
	
	public ShippingSystem findShippingJournalById(int id) throws CobhamRunningException {
        return shippingJournalDao.findById(id);
    }
	
    public void saveShippingJournal(ShippingSystem shippingJournal) throws CobhamRunningException {
    	shippingJournalDao.save(shippingJournal);
    }

    public void saveOrUpdateShippingJournal(ShippingSystem shippingJournal) throws CobhamRunningException {
        shippingJournalDao.saveOrUpdate(shippingJournal);
    }

    public void deleteShippingJournal(ShippingSystem shippingJournal) throws CobhamRunningException {
    	shippingJournalDao.delete(shippingJournal);
    }

    public void updateShippingJournal(Device shippingJournal) throws CobhamRunningException {
    	shippingJournalDao.update(shippingJournal);
    }

    public List<ShippingSystem> getJournalByDate(Date dateFrom, Date dateTo) throws CobhamRunningException {
	    return shippingJournalDao.getJournalByDate(dateFrom, dateTo);
    }

    public List<ShippingSystem> getJournalByFilter(String filter, Date dateFrom, Date dateTo) throws CobhamRunningException{
	    return shippingJournalDao.getJournalByFilter(filter, dateFrom, dateTo);
    }

    public boolean isDeviceInJournal(Device device) throws CobhamRunningException {
	    return shippingJournalDao.isDeviceInJournal(device);
    }

    public ShippingSystem getShippingSystemByDevice(Device device) throws CobhamRunningException {
	    return shippingJournalDao.getShippingSystemByDevice(device);
    }

    public List<ShippingSystem> findAllDevice() throws CobhamRunningException {
        return shippingJournalDao.findAll();
    }
}
