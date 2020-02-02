package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.SettingsDao;
import net.ddns.dimag.cobhamrunning.dao.TestsDao;
import net.ddns.dimag.cobhamrunning.models.Tests;

public class TestsService {
	private TestsDao testsDao = new TestsDao();
	
	public TestsService() {	
	}
	
	public Tests findTest(int id) {
        return testsDao.findById(id);
    }

    public void saveTest(Tests tests) {
    	System.out.println(tests);
    	testsDao.save(tests);
    }
    
    public void deleteTest(Tests tests) {
    	testsDao.delete(tests);
    }

    public void updateTest(Tests tests) {
    	testsDao.update(tests);
    }

    public List<Tests> findAllTests() {
        return testsDao.findAll();
    }
}
