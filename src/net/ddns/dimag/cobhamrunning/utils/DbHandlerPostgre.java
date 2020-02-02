package net.ddns.dimag.cobhamrunning.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Settings;

public class DbHandlerPostgre {
//	private static final Settings currentSettings = Settings.loadSettings();
//	private static final String CON_STR = String.format("jdbc:postgresql://%s:45432/%s", currentSettings.getPass_db(), currentSettings.getName_db());
//	private static final String LOGIN = currentSettings.getUser_db();
//	private static final String PASSWORD = currentSettings.getPass_db();
//
//	private static DbHandlerPostgre instance = null;
//	
//	public static synchronized DbHandlerPostgre getInstance() throws SQLException {
//        if (instance == null)
//            instance = new DbHandlerPostgre();
//        return instance;
//    }
//	
//	private Connection connection;
//	
//	private DbHandlerPostgre() throws SQLException {
//		try {
//			Class.forName("org.postgresql.Driver");
//		} catch (ClassNotFoundException e) {
//			MsgBox.msgError("DB init error", "PostgreSQL JDBC Driver is not found. Include it in your library path ");
//			e.printStackTrace();
//			return;
//		}
//        this.connection = DriverManager.getConnection(CON_STR, LOGIN, PASSWORD);
//    }
//	
//	public List<Settings> getAllProducts() {
//        try (Statement statement = this.connection.createStatement()) {
//            List<Settings> products = new ArrayList<Settings>();
//            ResultSet resultSet = statement.executeQuery("SELECT id, name FROM settings");
//            while (resultSet.next()) {
//                products.add(new Settings());
//            }
//            return products;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return Collections.emptyList();
//        }
//    }
}
