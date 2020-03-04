package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.SettingsDao;
import net.ddns.dimag.cobhamrunning.dao.TestsDao;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class TestsService {
	private TestsDao testsDao = new TestsDao();
	
	public TestsService() {	
	}
	
	public Tests findTest(int id) throws CobhamRunningException {
        return testsDao.findById(id);
    }

    public void saveTest(Tests tests) throws CobhamRunningException {
    	testsDao.save(tests);
    }

    public List<Tests> getTestsByDevice(Device device) throws CobhamRunningException {
	    return testsDao.getTestsByDevice(device);
    }

    public void deleteTest(Tests tests) throws CobhamRunningException {
    	testsDao.delete(tests);
    }

    public void updateTest(Tests tests) throws CobhamRunningException {
    	testsDao.update(tests);
    }

    public List<Tests> findAllTests() throws CobhamRunningException {
        return testsDao.findAll();
    }
}
