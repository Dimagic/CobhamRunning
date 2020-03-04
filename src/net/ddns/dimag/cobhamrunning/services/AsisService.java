package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.dao.AsisDao;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;


public class AsisService {
	private AsisDao asisDao = new AsisDao();

	public AsisService() {	
	}

	public Asis findAsis(int id) throws CobhamRunningException {
		return asisDao.findById(id);
	}

//	public List<Asis> findAsdisByName(String asis) {
//		return asisDao.findArticleHeadersByName(asis);
//	}

	public List<Asis> getAvaliableAsisRange(int count) throws CobhamRunningException {
		return asisDao.getAvaliableAsisRange(count);
	}

	public Asis findByName(String name) throws CobhamRunningException {
		return asisDao.findByName(name);
	}
	
	public void saveAsis(Asis asis) throws CobhamRunningException {
		asisDao.save(asis);
	}

	public void deleteAsis(Asis asis) throws CobhamRunningException {
		asisDao.delete(asis);
	}

	public void updateAsis(Asis asis) throws CobhamRunningException {
		asisDao.update(asis);
	}

	public List<Asis> findAllAsis() throws CobhamRunningException {
		return asisDao.findAll();
	}
	
	public int getAvaliableAsisCount() throws CobhamRunningException {
		return asisDao.getAvaliableAsisCount();
	}
		
	public int getUnprintedCount() throws CobhamRunningException {
		return asisDao.getUnprintedCount();
	}
}
