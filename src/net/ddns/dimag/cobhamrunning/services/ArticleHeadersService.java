package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import org.hibernate.Session;
import org.postgresql.util.ServerErrorMessage;

import net.ddns.dimag.cobhamrunning.dao.ArticleHeadersDao;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class ArticleHeadersService {
	private ArticleHeadersDao articleHeadersDao = new ArticleHeadersDao();
	
	public ArticleHeadersService() {	
	}
	
	public ArticleHeaders findArticle(int id) {
        return articleHeadersDao.findById(id);
    }

	public List<ArticleHeaders> findArticleByName(String article) {
		return articleHeadersDao.findArticleHeadersByName(article);
	}
	
    public void saveArticle(ArticleHeaders articleHeaders) {
    	articleHeadersDao.save(articleHeaders);
    }
    
    public void deleteArticle(ArticleHeaders articleHeaders) {
    	articleHeadersDao.delete(articleHeaders);
    }

    public void updateArticle(ArticleHeaders articleHeaders) {
    	articleHeadersDao.update(articleHeaders);
    }

    public List<ArticleHeaders> findAllArticleHeaders() {
        return articleHeadersDao.findAll();
    }
}
