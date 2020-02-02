package net.ddns.dimag.cobhamrunning.dao;

import java.util.List;

import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;

public class TestsDao implements UniversalDao{
	public Tests findById(int id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(Tests.class, id);
    }
	
	public List<Tests> findAll() {
        List<Tests> tests = (List<Tests>)  HibernateSessionFactoryUtil.getSessionFactory().openSession().createQuery("From tests").list();
        return tests;
    }

}
