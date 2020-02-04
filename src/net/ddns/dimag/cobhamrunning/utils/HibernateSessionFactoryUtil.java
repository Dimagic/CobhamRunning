package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.models.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import org.hibernate.internal.util.config.ConfigurationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class HibernateSessionFactoryUtil implements MsgBox {
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {
    }

    public static SessionFactory getSessionFactory() {
        Settings settings = getSettings();
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration()
                        .addAnnotatedClass(Device.class)
                        .addAnnotatedClass(Tests.class)
                        .addAnnotatedClass(Measurements.class)
                        .addAnnotatedClass(ArticleHeaders.class)
                        .addAnnotatedClass(LabelTemplate.class)
                        .addAnnotatedClass(DeviceInfo.class)
                        .addAnnotatedClass(ShippingSystem.class)
                        .addAnnotatedClass(Asis.class)
                        .addAnnotatedClass(MacAddress.class)
                        .addAnnotatedClass(AsisPrintJob.class)
                        .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
                        .setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect")
                        .setProperty("hibernate.connection.url", String.format("jdbc:postgresql://%s:%s/%s",
                                settings.getAddr_db(), settings.getPort_db(), settings.getName_db()))
                        .setProperty("hibernate.connection.username", settings.getUser_db())
                        .setProperty("hibernate.connection.password", settings.getPass_db())
                        .setProperty("log4j.logger.org.hibernate.type", "DEBUG")
                        .setProperty("hibernate.show_sql", "true")
                        .setProperty("hibernate.hbm2ddl.auto", "update")
                        .setProperty("hibernate.enable_lazy_load_no_trans", "true")
                        .setProperty("hibernate.connection.autocommit", "false")
                        .configure();


                StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
                sessionFactory = configuration.buildSessionFactory(builder.build());
            } catch (ConfigurationException e) {
                MsgBox.msgError("DB connection", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                MsgBox.msgException(e);
            }
        }
        return sessionFactory;
    }

    private static Settings getSettings(){
        File file = new File("./settings.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller um = context.createUnmarshaller();
            return (Settings) um.unmarshal(file);
        } catch (Exception e) {
            MsgBox.msgException(e);
        }
        return null;
    }
}
