package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class MeasurementsDao implements UniversalDao{
	public Measurements findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Measurements.class, id);
    }
	
	public List<Measurements> findAll() {
        List<Measurements> meases = (List<Measurements>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From measurements").list();
        return meases;
    }

}
