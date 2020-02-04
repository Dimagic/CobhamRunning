package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.dao.AsisDao;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;


public class AsisService {
	private AsisDao asisDao = new AsisDao();

	public AsisService() {	
	}

	public Asis findAsis(int id) {
		return asisDao.findById(id);
	}

//	public List<Asis> findAsdisByName(String asis) {
//		return asisDao.findArticleHeadersByName(asis);
//	}

	public List<Asis> getAvaliableAsisRange(int count){
		return asisDao.getAvaliableAsisRange(count);
	}

	public Asis findByName(String name) {
		return asisDao.findByName(name);
	}
	
	public void saveAsis(Asis asis) {
		asisDao.save(asis);
	}

	public void deleteAsis(Asis asis) {
		asisDao.delete(asis);
	}

	public void updateAsis(Asis asis) {
		asisDao.update(asis);
	}

	public List<Asis> findAllAsis() {
		return asisDao.findAll();
	}
	
	public int getAvaliableAsisCount() {
		return asisDao.getAvaliableAsisCount();
	}
		
	public int getUnprintedCount() {
		return asisDao.getUnprintedCount();
	}
}
