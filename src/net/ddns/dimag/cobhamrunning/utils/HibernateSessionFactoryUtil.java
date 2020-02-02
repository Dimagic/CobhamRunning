package net.ddns.dimag.cobhamrunning.utils;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.models.MacAddress;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.models.Tests;

public class HibernateSessionFactoryUtil implements MsgBox{
	private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {}

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.addAnnotatedClass(Device.class);
                configuration.addAnnotatedClass(Tests.class);
                configuration.addAnnotatedClass(Measurements.class);
                configuration.addAnnotatedClass(ArticleHeaders.class);
                configuration.addAnnotatedClass(LabelTemplate.class);
                configuration.addAnnotatedClass(DeviceInfo.class);
                configuration.addAnnotatedClass(ShippingSystem.class);
                configuration.addAnnotatedClass(Asis.class);  
                configuration.addAnnotatedClass(MacAddress.class);  
                configuration.addAnnotatedClass(AsisPrintJob.class);  
                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
