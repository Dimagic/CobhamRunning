package net.ddns.dimag.cobhamrunning.utils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.util.regex.Matcher;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.services.DeviceInfoService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;

public class RmvUtils {

	private static String JDBC_URL = "jdbc:sqlserver://lhr9-pur-sql005;databaseName=RMV;user=rmv_user;password=RMV;";

	private static List<HashMap<String, Object>> getMeasuresById(Object object)
			throws SQLException, ClassNotFoundException {
		List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
		String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = %s", object);
		List<HashMap<String, Object>> res = sendQuery(q);
		for (HashMap<String, Object> measResult : res) {
			rows.add(measResult);
		}
		return rows;
	}

	private static Collection<Object> getHeaderTest(String asis)
			throws ClassNotFoundException, SQLException, ParseException {
		String q = String
				.format("select * from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
		HashMap<String, Object> headerTest = new HashMap<>();
		Date objDate;
		Date savedDate;

		for (HashMap<String, Object> currTest: sendQuery(q)){
			objDate = strToDate(currTest.get("TestDate"));
			
			if (headerTest.get(currTest.get("Configuration")) == null){
				headerTest.put(currTest.get("Configuration").toString(), currTest);
				continue;
			} else {
				HashMap<String, Object> savedTest = (HashMap<String, Object>) headerTest.get(currTest.get("Configuration"));
				savedDate = strToDate(savedTest.get("TestDate"));
			}

			if (objDate.getTime() > savedDate.getTime()){
				headerTest.put(currTest.get("Configuration").toString(), currTest);
			}	
		}
		System.out.println(headerTest);
		return headerTest.values();
	}
	
	private static Date strToDate(Object date) throws ParseException{
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		return formatter.parse(date.toString());

	}

	private static List<HashMap<String, Object>> sendQuery(String q) throws ClassNotFoundException, SQLException {
		System.out.println(q);
		HashMap<String, Object> row;
		List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();

		Statement statement = getStatment();
		ResultSet rs = statement.executeQuery(q);
		ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			row = new HashMap<String, Object>();
			int numColumns = rsmd.getColumnCount();
			for (int i = 1; i <= numColumns; i++) {
				String column_name = rsmd.getColumnName(i);
				row.put(column_name, rs.getObject(column_name));
			}
			rows.add(row);
		}
		return rows;
	}

	private static Statement getStatment() throws SQLException, ClassNotFoundException {
		Connection conn = getDbConnection();
		return conn.createStatement();
	}

	private static Connection getDbConnection() throws SQLException, ClassNotFoundException {
		try {
			appendToPath();
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return DriverManager.getConnection(JDBC_URL);
		} catch (SQLException | ClassNotFoundException e) {
			throw e;
		}
	}

	private static void appendToPath() {
		String dir = "e:\\Work\\JProject\\CobhamRunning\\lib\\";
		String path = System.getProperty("java.library.path");
		path = dir + ";" + path;
		System.setProperty("java.library.path", path);
		try {
			final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
			sysPathsField.setAccessible(true);
			sysPathsField.set(null, null);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static DeviceInfo getDeviceInfo(Device dev) throws ParseException {
		String systemVer = null;
		String commonVer = null;
		String targetVer = null;
		Matcher mCommonVer;
		Matcher mSystemVer;
		Matcher mTargetVer;
		Pattern pSystemVer = Pattern.compile("(system([a-zA-Z ])+version)");
		Pattern pCommonVer = Pattern.compile("(common([a-zA-Z ])+version)");
		Pattern pTargetVer = Pattern.compile("(target([a-zA-Z ])+version)");
		try {
	
			List<HashMap<String, Object>> testList = new ArrayList(getHeaderTest(dev.getAsis()));
			for (HashMap<String, Object> test : testList) {
				System.out.println(String.format("Test: %s Date: %s", test.get("Configuration"), test.get("TestDate")));
				List<HashMap<String, Object>> testRes = getMeasuresById(test.get("HeaderID"));
				for (HashMap<String, Object> meas : testRes) {
					String descript = (String) meas.get("Description");
					mCommonVer = pCommonVer.matcher(descript.toLowerCase());
					mSystemVer = pSystemVer.matcher(descript.toLowerCase());
					mTargetVer = pTargetVer.matcher(descript.toLowerCase());
					if (mCommonVer.matches()) {
						commonVer = (String) meas.get("Result");
					}
					if (mSystemVer.matches()) {
						systemVer = (String) meas.get("Result");
					}
					if (mTargetVer.matches()) {
						targetVer = (String) meas.get("Result");
					}
				}
		
			}
			return new DeviceInfo(systemVer, commonVer, targetVer);
		
			
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public static void main(String[] args){
		DeviceService devServ = new DeviceService();
		DeviceInfoService devInfoServ = new DeviceInfoService();
		try {
			Device device = devServ.findDeviceByAsis("PHTV").get(0);
			DeviceInfo deviceInfo = getDeviceInfo(device);
			device.setDeviceInfo(deviceInfo);
			System.out.println(device.getDeviceInfo());
			devInfoServ.saveOrUpdateDeviceInfo(deviceInfo);
			

			
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
