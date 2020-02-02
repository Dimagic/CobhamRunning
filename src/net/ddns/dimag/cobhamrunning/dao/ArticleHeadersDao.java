package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import org.hibernate.Session;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class ArticleHeadersDao implements UniversalDao {
	public ArticleHeaders findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(ArticleHeaders.class, id);
    }
	
	public List<ArticleHeaders> findArticleHeadersByName(String name){
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<ArticleHeaders> articleHeadersList = session.createSQLQuery("SELECT * FROM articleHeaders WHERE article LIKE :name")
				.addEntity(ArticleHeaders.class)
				.setParameter("name", "%"+name+"%").list();
		session.close();
		return articleHeadersList;
	}
	
    public List<ArticleHeaders> findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<ArticleHeaders> articleHeadersList = session.createSQLQuery("SELECT * FROM articleHeaders").addEntity(ArticleHeaders.class).list(); 
		session.close();
        return articleHeadersList;
    }

}
