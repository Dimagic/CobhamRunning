package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class ShippingJournalDao implements UniversalDao {
	public ShippingSystem findById(int id) {
		return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(ShippingSystem.class, id);
	}

	public List<ShippingSystem> findAll() {
		List<ShippingSystem> journal = (List<ShippingSystem>) HibernateSessionFactoryUtil.getSessionFactory().openSession()
				.createQuery("From ShippingJournal").list();
		return journal;
	}
}
