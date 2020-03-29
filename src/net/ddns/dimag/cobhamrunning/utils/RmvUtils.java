package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.models.Measurements;

import java.lang.reflect.Field;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RmvUtils {
    private final String JDBC_URL;
    private Connection connection;
    private Statement statment;
//  "jdbc:sqlserver://lhr9-pur-sql005;databaseName=RMV;user=rmv_user;password=RMV;"

    public RmvUtils(String rmvAddr, String rmvDbName, String rmvUser, String rmvPassw) {
        this.JDBC_URL = String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;", rmvAddr, rmvDbName, rmvUser, rmvPassw);
    }

    public HashMap<String, String> getTestsMapWithDate(String asis) throws CobhamRunningException, SQLException, ClassNotFoundException {
        HashMap<String, String> testList = new HashMap<>();
        List testNames = getTestNamesByAsis(asis);
        for (Object testName : testNames) {
            testList.put(testName.toString(), getLastTestDateByName(asis, testName.toString()));
        }
        return testList;
    }

    public HashMap<String, Object> getLastTestResult(String asis, String testName) throws SQLException, ClassNotFoundException, CobhamRunningException {
        String testDate = getLastTestDateByName(asis, testName);
        String q = String
                .format("select * from RMV.dbo.tbl_RMV_Header where Serial = '%s' and Configuration = '%s' and TestDate = '%s'", asis, testName, testDate);
        return sendQuery(q).get(0);
    }

    public List<HashMap<String, Object>> getMeasuresById(Object object)
            throws SQLException, ClassNotFoundException {
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = %s", object);
        List<HashMap<String, Object>> res = sendQuery(q);
        for (HashMap<String, Object> measResult : res) {
            rows.add(measResult);
        }
        return rows;
    }

    public boolean checkRmvByAsis(String asis, String article) throws SQLException, ClassNotFoundException, CobhamRunningException {
        boolean isAsis, isArticle = false;
        String q = String
                .format("select count(*) as rec_count from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
        if (Integer.parseInt(sendQuery(q).get(0).get("rec_count").toString()) > 0) {
            isAsis = true;
        } else {
            throw new CobhamRunningException(String.format("System %s not found in RMV base", asis));
        }
        q = String.format("select top 1 Article, Revision from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
        HashMap<String, Object> res = sendQuery(q).get(0);
        String dbArticle = String.format("%s%s", res.get("Article"), res.get("Revision"));
        if (!article.equals(dbArticle)) {
            throw new CobhamRunningException(String.format("In RMV base system %s has another article %s", asis, dbArticle));
        }
        return true;
    }

    public DeviceInfo getDeviceInfo(Device dev) throws ParseException {
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
            List<HashMap<String, Object>> testList = new ArrayList(getHeaderTest(dev.getAsis().getAsis()));
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

    /**
    *********************************************************************************************************
    */



    private String getLastTestDateByName(String asis, String testName) throws SQLException, ClassNotFoundException, CobhamRunningException {
        String q = String
                .format("select max(TestDate) as TestDate from RMV.dbo.tbl_RMV_Header where Configuration = '%s' and Serial = '%s'", testName, asis);
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.get(0).get("TestDate") == null) {
            throw new CobhamRunningException(String.format("Test %s for system %s not found", testName, asis));
        }
        return res.get(0).get("TestDate").toString();
    }

    private List getTestNamesByAsis(String asis) throws SQLException, ClassNotFoundException, CobhamRunningException {
        String q = String
                .format("select DISTINCT Configuration from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.size() == 0) {
            throw new CobhamRunningException(String.format("Tests for system %s not found", asis));
        }
        List<String> resList = new ArrayList<>();
        res.forEach(item -> resList.add(item.get("Configuration").toString()));
        return resList;
    }



    private Collection<Object> getHeaderTest(String asis)
            throws ClassNotFoundException, SQLException, ParseException {
        String q = String
                .format("select * from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
        HashMap<String, Object> headerTest = new HashMap<>();
        Date objDate;
        Date savedDate;

        for (HashMap<String, Object> currTest : sendQuery(q)) {
            objDate = strToDate(currTest.get("TestDate"));

            if (headerTest.get(currTest.get("Configuration")) == null) {
                headerTest.put(currTest.get("Configuration").toString(), currTest);
                continue;
            } else {
                HashMap<String, Object> savedTest = (HashMap<String, Object>) headerTest.get(currTest.get("Configuration"));
                savedDate = strToDate(savedTest.get("TestDate"));
            }

            if (objDate.getTime() > savedDate.getTime()) {
                headerTest.put(currTest.get("Configuration").toString(), currTest);
            }
        }
        System.out.println(headerTest);
        return headerTest.values();
    }

    private HashMap<Integer, Object> getLastMeasureByNameAndAsis(String name, String asis) throws SQLException, ClassNotFoundException {
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = (" +
                "select HeaderID from RMV.dbo.tbl_RMV_Header where Serial = '%s' " +
                "and Configuration = '%s' and TestDate = (" +
                "select max(TestDate) as TestDate from RMV.dbo.tbl_RMV_Header " +
                "where Configuration = '%s' and Serial = '%s'))", asis, name, name, asis);
        HashMap<Integer, Object> measures = new HashMap<>();
        for (HashMap<String, Object> currMeasure : sendQuery(q)) {
            measures.put((int) currMeasure.get("MeasureNumber"), currMeasure);
        }
        return measures;
    }

    private Date strToDate(Object date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.parse(date.toString());

    }

//    private Connection getDbConnection() throws SQLException, ClassNotFoundException {
////		try {
//        appendToPath();
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//        return DriverManager.getConnection(JDBC_URL);
////		} catch (SQLException | ClassNotFoundException e) {
////			throw e;
////		}
//}

    private void appendToPath() {
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

    private void getMeasuresByDevice(Device dev) {
        List<HashMap<String, Object>> testList = null;
        List<Measurements> measList = new ArrayList<>();
        try {
            testList = new ArrayList(getHeaderTest(dev.getAsis().getAsis()));
            for (HashMap<String, Object> test : testList) {
                for (HashMap<String, Object> meas : getMeasuresById(test.get("HeaderID"))) {
                    measList.add(new Measurements());
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private List getMeasuresById(int measId) throws SQLException, ClassNotFoundException {
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = %s", measId);
        List<HashMap<String, Object>> measures = new ArrayList<>();
        for (HashMap<String, Object> measure : sendQuery(q)) {
            measures.add(measure);
        }
        return measures;
    }

    private List<HashMap<String, Object>> sendQuery(String q) throws ClassNotFoundException, SQLException {
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

    public Statement getStatment() throws SQLException, ClassNotFoundException {
        if (statment == null || statment.isClosed()) {
            Connection conn = getConnection();
            setStatment(conn.createStatement());
        }
        return statment;
    }

    public void setStatment(Statement statment) {
        this.statment = statment;
    }

    public Connection getConnection() throws ClassNotFoundException, SQLException {
        if (connection == null || connection.isClosed()) {
            appendToPath();
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            setConnection(DriverManager.getConnection(JDBC_URL));
        }
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}


