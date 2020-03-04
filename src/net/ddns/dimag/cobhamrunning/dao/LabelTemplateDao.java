package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class LabelTemplateDao implements UniversalDao{
	public LabelTemplate findById(Long id) throws CobhamRunningException {
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
			if (session != null) {
				session.close();
			}
		}
		return labelTemplate;
	}
	
	public List findAllByArticle(ArticleHeaders articleHeaders) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		String query = String.format("SELECT * FROM public.articleheaders_labeltemplate WHERE articleheaders_id = %s", articleHeaders.getId());
		List labelTemplates = session.createSQLQuery(query).addEntity(ArticleHeaders.class).list();
		session.close();
        return labelTemplates;
    }

	public LabelTemplate findByName(String name) throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		String query = String.format("SELECT * FROM public.labeltemplate WHERE name = '%s'", name);
		LabelTemplate labelTemplate = (LabelTemplate) session.createSQLQuery(query).addEntity(LabelTemplate.class).getSingleResult();
		session.close();
		return labelTemplate;
	}
	
	public List findAllWithoutMac() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		String query = "SELECT * FROM public.labeltemplate WHERE template NOT LIKE '%<MAC>%'";
		List labelTemplates = session.createSQLQuery(query).addEntity(LabelTemplate.class).list();
		session.close();
        return labelTemplates;
    }
	
	public List findAll() throws CobhamRunningException{
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		List labelTemplates = session.createQuery("From LabelTemplate").list();
		session.close();
		return labelTemplates;
	}
	
}
