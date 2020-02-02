package net.ddns.dimag.cobhamrunning.services;

import java.util.Collection;
import java.util.List;

import net.ddns.dimag.cobhamrunning.dao.LabelTemplateDao;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;

public class LabelTemplateService {
	private LabelTemplateDao labelTemplateDao = new LabelTemplateDao();
	
	public LabelTemplateService() {	
	}
	
	public LabelTemplate findLabelTemplateById(long id) {
        return labelTemplateDao.findById(id);
    }
	
    public void saveLabelTemplate(LabelTemplate labelTemplate) {
    	labelTemplateDao.save(labelTemplate);    	
    }

    public void deleteLabelTemplate(LabelTemplate labelTemplate) {
    	labelTemplateDao.delete(labelTemplate);
    }

    public void updateLabelTemplate(LabelTemplate labelTemplate) {
    	labelTemplateDao.update(labelTemplate);
    }

    public List<LabelTemplate> findAllLabelTemplate() {
        return labelTemplateDao.findAll();
    }
    
    public List<LabelTemplate> findAllWithoutMac() {
    	return labelTemplateDao.findAllWithoutMac();
    }

	public List<LabelTemplate> findAllByArticle(ArticleHeaders articleHeaders) {
		return labelTemplateDao.findAllByArticle(articleHeaders);
	}
}
