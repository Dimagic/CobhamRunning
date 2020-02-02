package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class LabelTemplateDao implements UniversalDao{
	public LabelTemplate findById(Long id) {
		LabelTemplate labelTemplate = null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery("from LabelTemplate where id = :id");
			query.setParameter("id", id);
			labelTemplate = (LabelTemplate) query.uniqueResult();
			transaction.commit();
		} catch (Exception e) {
			labelTemplate = null;
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			session.close();
		}
		return labelTemplate;		
//		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(LabelTemplate.class, id);
	}
	
	public List<LabelTemplate> findAllByArticle(ArticleHeaders articleHeaders) {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		String query = String.format("SELECT * FROM public.articleheaders_labeltemplate WHERE articleheaders_id = %s", articleHeaders.getId());
		List<LabelTemplate> labelTemplates = session.createSQLQuery(query).addEntity(ArticleHeaders.class).list(); 
		session.close();
        return labelTemplates;
    }
	
	public List<LabelTemplate> findAllWithoutMac() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		String query = "SELECT * FROM public.labeltemplate WHERE template NOT LIKE '%<MAC>%'";
		List<LabelTemplate> labelTemplates = session.createSQLQuery(query).addEntity(LabelTemplate.class).list(); 
		session.close();
        return labelTemplates;
    }
	
	public List<LabelTemplate> findAll() {
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List<LabelTemplate> labelTemplates = session.createQuery("From LabelTemplate").list();
		session.close();
		
//		List<LabelTemplate> labelTemplates = (List<LabelTemplate>) HibernateSessionFactoryUtil.getSessionFactory().openSession()
//				.createQuery("From LabelTemplate").list();

		return labelTemplates;
	}
	
}
