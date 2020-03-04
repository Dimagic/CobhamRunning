package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class ArticleHeadersDao implements UniversalDao {
	public ArticleHeaders findById(int id) throws CobhamRunningException {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		ArticleHeaders articleHeaders = session.get(ArticleHeaders.class, id);
		session.close();
        return articleHeaders;
    }
	
	public ArticleHeaders findArticleHeadersByName(String article) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		ArticleHeaders articleHeaders = (ArticleHeaders) session.createSQLQuery("SELECT * FROM articleHeaders WHERE article = :name")
				.addEntity(ArticleHeaders.class)
				.setParameter("name", article).getSingleResult();
		session.close();
		return articleHeaders;
	}

	public List findArticleHeadersListByName(String name) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List articleHeadersList = session.createSQLQuery("SELECT * FROM articleHeaders WHERE article LIKE :name")
				.addEntity(ArticleHeaders.class)
				.setParameter("name", "%"+name+"%").list();
		session.close();
		return articleHeadersList;
	}
	
    public List findAll() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List articleHeadersList = session.createSQLQuery("SELECT * FROM articleHeaders").addEntity(ArticleHeaders.class).list();
		session.close();
        return articleHeadersList;
    }

}
