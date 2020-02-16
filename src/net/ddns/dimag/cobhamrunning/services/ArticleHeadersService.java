package net.ddns.dimag.cobhamrunning.services;

import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.postgresql.util.ServerErrorMessage;

import net.ddns.dimag.cobhamrunning.dao.ArticleHeadersDao;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

import javax.persistence.NoResultException;
import javax.transaction.Transactional;

public class ArticleHeadersService {
	private ArticleHeadersDao articleHeadersDao = new ArticleHeadersDao();
	
	public ArticleHeadersService() {	
	}
	
	public ArticleHeaders findArticle(int id) {
        return articleHeadersDao.findById(id);
    }

	public ArticleHeaders findArticleByName(String article) {
	    try {
            return articleHeadersDao.findArticleHeadersByName(article);
        } catch (NoResultException e){
	        return null;
        }
	}

	public List findArticleHeadersListByName(String article){
	    return articleHeadersDao.findArticleHeadersListByName(article);
    }
	
    public void saveArticle(ArticleHeaders articleHeaders) {
    	articleHeadersDao.save(articleHeaders);
    }

    public boolean saveListArticle(List<ArticleHeaders> articleHeadersList){
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        StringBuilder stringBuilder = new StringBuilder();
        Transaction tx = session.beginTransaction();
        try {
            for (ArticleHeaders articleHeaders: articleHeadersList){
                session.save(articleHeaders);
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
