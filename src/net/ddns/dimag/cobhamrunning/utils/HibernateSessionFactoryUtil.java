package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.models.environment.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import org.hibernate.internal.util.config.ConfigurationException;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.stat.Statistics;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;

public class HibernateSessionFactoryUtil implements MsgBox {
    private static final Logger LOGGER = LogManager.getLogger(HibernateSessionFactoryUtil.class.getName());
    private static SessionFactory sessionFactory;

    private HibernateSessionFactoryUtil() {
    }

    public static SessionFactory getSessionFactory() throws CobhamRunningException {
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
                        .addAnnotatedClass(EnvDevice.class)
                        .addAnnotatedClass(EnvManuf.class)
                        .addAnnotatedClass(EnvHistory.class)
                        .addAnnotatedClass(EnvLocation.class)
                        .addAnnotatedClass(EnvModel.class)
                        .addAnnotatedClass(EnvStatus.class)
                        .addAnnotatedClass(EnvType.class)
                        .addAnnotatedClass(EnvService.class)
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
                Statistics stats = sessionFactory.getStatistics();
                stats.setStatisticsEnabled(true);
            } catch (ConfigurationException | ServiceException e) {
                LOGGER.error(e);
                throw new CobhamRunningException("Can't establish connection with DB.\n" +
                        "Please check settings and try again.");
            } catch (Exception e) {
                LOGGER.error(e);
                throw new CobhamRunningException(e.getCause().getCause().getMessage());
            }
        }
        return sessionFactory;
    }

    public static SessionFactory restartSessionFactory() throws CobhamRunningException {
        sessionFactory = null;
        return getSessionFactory();
    }

    public static HashMap<String, String> getConnectionInfo() throws CobhamRunningException {
        HashMap<String, String> connInfoMap = new HashMap<>();
        SessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory == null) {
            throw new CobhamRunningException("Can't get session factory. Try fix the problem and try again.");
        }
        Session session = sessionFactory.openSession();
        ConnectionInfo connectionInfo = new ConnectionInfo();
        session.doWork(connectionInfo);
        connInfoMap.put("DataBaseProductName", connectionInfo.getDataBaseProductName());
        connInfoMap.put("DataBaseUrl", connectionInfo.getDataBaseUrl());
        connInfoMap.put("DriverName", connectionInfo.getDriverName());
        connInfoMap.put("Username", connectionInfo.getUsername());
        for (String c : connInfoMap.keySet()) {
            LOGGER.info(String.format("%s : %s", c, connInfoMap.get(c)));
        }
        session.close();
        return connInfoMap;
    }

    private static Settings getSettings() {
        File file = new File("./settings.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller um = context.createUnmarshaller();
            return (Settings) um.unmarshal(file);
        } catch (Exception e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
        return null;
    }
}
