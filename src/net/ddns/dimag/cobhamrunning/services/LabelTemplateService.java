package net.ddns.dimag.cobhamrunning.services;

import java.util.Collection;
import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.LabelTemplateDao;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;

public class LabelTemplateService {
	private LabelTemplateDao labelTemplateDao = new LabelTemplateDao();
	
	public LabelTemplateService() {	
	}
	
	public LabelTemplate findLabelTemplateById(long id) throws CobhamRunningException {
        return labelTemplateDao.findById(id);
    }

    public LabelTemplate findByName(String name) throws CobhamRunningException {
        return labelTemplateDao.findByName(name);
    }
	
    public void saveLabelTemplate(LabelTemplate labelTemplate) throws CobhamRunningException {
    	labelTemplateDao.save(labelTemplate);    	
    }

    public void deleteLabelTemplate(LabelTemplate labelTemplate) throws CobhamRunningException {
    	labelTemplateDao.delete(labelTemplate);
    }

    public void updateLabelTemplate(LabelTemplate labelTemplate) throws CobhamRunningException {
    	labelTemplateDao.update(labelTemplate);
    }

    public List<LabelTemplate> findAllLabelTemplate() throws CobhamRunningException {
        return labelTemplateDao.findAll();
    }
    
    public List<LabelTemplate> findAllWithoutMac() throws CobhamRunningException {
    	return labelTemplateDao.findAllWithoutMac();
    }

	public List<LabelTemplate> findAllByArticle(ArticleHeaders articleHeaders) throws CobhamRunningException {
		return labelTemplateDao.findAllByArticle(articleHeaders);
	}
}
