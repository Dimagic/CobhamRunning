package net.ddns.dimag.cobhamrunning.dao;

import java.util.Date;
import java.util.List;

import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import org.hibernate.Session;

public class ShippingJournalDao implements UniversalDao {
	public ShippingSystem findById(int id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(ShippingSystem.class, id);
	}

	public List getJournalByDate(Date dateFrom, Date dateTo){
		Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
		return session.createSQLQuery("SELECT * FROM public.shippingjournal WHERE dateship BETWEEN :from AND :to")
				.addEntity(ShippingSystem.class)
				.setParameter("from", dateFrom)
				.setParameter("to", dateTo).list();
	}

	public List<ShippingSystem> findAll() {
		List<ShippingSystem> journal = (List<ShippingSystem>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From shippingjournal").list();
		return journal;
	}
}
