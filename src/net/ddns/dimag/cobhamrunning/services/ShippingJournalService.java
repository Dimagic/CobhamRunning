package net.ddns.dimag.cobhamrunning.services;

import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.ShippingJournalDao;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;

public class ShippingJournalService {
private ShippingJournalDao shippingJournalDao = new ShippingJournalDao();
	
	public ShippingJournalService() {	
	}
	
	public ShippingSystem findShippingJournalById(int id) {
        return shippingJournalDao.findById(id);
    }
	
    public void saveShippingJournal(ShippingSystem shippingJournal) {
    	shippingJournalDao.save(shippingJournal);    	
    }

    public void deleteShippingJournal(ShippingSystem shippingJournal) {
    	shippingJournalDao.delete(shippingJournal);
    }

    public void updateShippingJournal(Device shippingJournal) {
    	shippingJournalDao.update(shippingJournal);
    }

    public List<ShippingSystem> getJournalByDate(Date dateFrom, Date dateTo){
	    return shippingJournalDao.getJournalByDate(dateFrom, dateTo);
    }

    public List<ShippingSystem> findAllDevice() {
        return shippingJournalDao.findAll();
    }
}
