package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.models.Measurements;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RmvUtils {
    private final String JDBC_URL;
    private Connection connection;
    private Statement statment;
    private MainApp mainApp;

    public RmvUtils(String rmvAddr, String rmvDbName, String rmvUser, String rmvPassw) throws CobhamRunningException {
        if (!isServerAvailable(rmvAddr))
            throw new CobhamRunningException(String.format("Server: %s not available", rmvAddr));

        this.JDBC_URL = String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;", rmvAddr, rmvDbName, rmvUser, rmvPassw);
    }

    public RmvUtils(MainApp mainApp) throws CobhamRunningException {
        this.mainApp = mainApp;
        Settings settings = mainApp.getCurrentSettings();
        if (!isServerAvailable(settings.getAddr_rmv()))
            throw new CobhamRunningException(String.format("Server: %s not available", settings.getAddr_rmv()));
        this.JDBC_URL = String.format("jdbc:sqlserver://%s;databaseName=%s;user=%s;password=%s;", settings.getAddr_rmv(),
                settings.getName_rmv(), settings.getUser_rmv(), settings.getPass_rmv());
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
            throws CobhamRunningException {
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = %s", object);
        List<HashMap<String, Object>> res = sendQuery(q);
        for (HashMap<String, Object> measResult : res) {
            rows.add(measResult);
        }
        return rows;
    }

    public List<HashMap<String, Object>> getMeasuresByHeaderList(List<Long> headerList)
            throws CobhamRunningException {
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID in (%s) order by MeasureDate",
                headerList.stream().map(Object::toString).collect(Collectors.joining(",")));
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
        } catch (CobhamRunningException e) {
            MsgBox.msgException(e);
            return null;
        }
    }

    /**
     * ********************************************************************************************************
     */

    public List<HashMap<String, Object>> getTestsByInnerAsis(Object object) {
        List<HashMap<String, Object>> rows = new ArrayList<HashMap<String, Object>>();
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header where HeaderID in " +
                "(select HeaderID from RMV.dbo.tbl_RMV_MeasureData where Result = '%s') " +
                "or HeaderID in (select HeaderID from RMV.dbo.tbl_RMV_Header where Serial = '%s')", object, object);
        List<HashMap<String, Object>> res = sendQuery(q);
        for (HashMap<String, Object> measResult : res) {
            rows.add(measResult);
        }
        return rows;
    }

    private String getLastTestDateByName(String asis, String testName) throws SQLException, ClassNotFoundException, CobhamRunningException {
        String q = String
                .format("select max(TestDate) as TestDate from RMV.dbo.tbl_RMV_Header where Configuration = '%s' and Serial = '%s'", testName, asis);
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.get(0).get("TestDate") == null) {
            throw new CobhamRunningException(String.format("Test %s for system %s not found", testName, asis));
        }
        return res.get(0).get("TestDate").toString();
    }

    public int getTestStatusByDateTest(Date date) throws CobhamRunningException {
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header where TestDate = '%s'", date);
        List<HashMap<String, Object>> res = sendQuery(q);
        try {
            return (int) res.get(0).get("TestStatus");
        } catch (Exception e) {
        }
        return -1;
    }

    public Integer getTestTimeByDateTest(Date date) throws CobhamRunningException {
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header where TestDate = '%s'", date);
        List<HashMap<String, Object>> res = sendQuery(q);
        try {
            return (Integer) res.get(0).get("TestTime");
        } catch (Exception e) {
        }
        return -1;
    }

    private HashMap<Date, HashMap<String, Object>> getTestsResultWithDate(String asis) throws CobhamRunningException, ParseException {
        String q = String
                .format("select * from RMV.dbo.tbl_RMV_Header where Serial = '%s'", asis);
        List<HashMap<String, Object>> tmp = sendQuery(q);
        HashMap<Date, HashMap<String, Object>> res = new HashMap<>();
        for (HashMap<String, Object> obj : tmp) {
            res.put(strToDate(obj.get("TestDate")), obj);
        }
        return res;
    }

    public HashMap<Date, Object> getAllTestsStatusWithDate(String asis) throws CobhamRunningException, ParseException {
        HashMap<Date, Object> res = new HashMap<>();
        HashMap<Date, HashMap<String, Object>> dateMap = getTestsResultWithDate(asis);
        List<Date> dateList = new ArrayList<Date>(dateMap.keySet());
        Collections.sort(dateList);
        for (Date d : dateList) {
            res.put(d, dateMap.get(d));
        }
        return res;
    }

    public HashMap<String, Object> getLastTestsStatusWithDate(String asis) throws CobhamRunningException, ParseException {
        HashMap<String, Object> res = new HashMap<>();
        HashMap<Date, HashMap<String, Object>> dateMap = getTestsResultWithDate(asis);
        List<Date> dateList = new ArrayList<Date>(dateMap.keySet());
        Collections.sort(dateList);
        for (Date d : dateList) {
            res.put(dateMap.get(d).get("Configuration").toString(), dateMap.get(d));
        }
        return res;
    }

    private List getTestNamesByAsis(String asis) throws CobhamRunningException {
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

    public HashMap<HashMap<String, Object>, List<HashMap<String, Object>>>
    getTestsWithMeasureByArticleBetweenDate(String art, Date start, Date stop)
            throws CobhamRunningException {
        List<HashMap<String, Object>> testList = getTestsByArticleBetweenDate(art, start, stop);
        Set<String> headSet = new HashSet<>();
        testList.forEach(item -> headSet.add(Long.toString((Long) item.get("HeaderID"))));
        List<HashMap<String, Object>> measList = getMeasuresByHeaderId(headSet);
        HashMap<HashMap<String, Object>, List<HashMap<String, Object>>> resMap = new HashMap<>();
        for (HashMap<String, Object> test: testList){
            List<HashMap<String, Object>> resMeas = measList.stream().filter(c ->
                    c.get("HeaderID").equals(test.get("HeaderID"))).collect(Collectors.toList());
            resMap.put(test, resMeas);
        }
        return resMap;
    }

    public List<HashMap<String, Object>> getTestsByArticleBetweenDate(String art, Date start, Date stop)
            throws CobhamRunningException {
        String a = "%" + art + "%";
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header where Article like '%s' " +
                "and TestDate between '%s' and '%s' order by TestDate DESC", a, start, stop);
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.size() == 0) {
            MsgBox.msgInfo("RMV utils", String.format("Tests for system with article\nlike %s not found", art));
        }
        return res;
    }

    public List<HashMap<String, Object>> getTestsByAsisBetweenDate(String asis, Date start, Date stop) {
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header where Serial = '%s' " +
                "and TestDate between '%s' and '%s' order by TestDate DESC", asis.toUpperCase(), start, stop);
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.size() == 0) {
            boolean qRes = MsgBox.msgConfirm("RMV utils", String.format("Tests for system with ASIS: %s\n" +
                    "not found in period from %s to %s.\n" +
                    "Do you want search in all DB?", asis.toUpperCase(), start, stop));
            if (qRes){
                return getTestsByAsis(asis);
            }
        }
        return res;
    }

    public List<HashMap<String, Object>> getAllTestsToday(){
        String q = "select * from RMV.dbo.tbl_RMV_Header " +
                "where CAST(TestDate as Date) = CAST(GETDATE() as Date) order by TestDate DESC ";
        List<HashMap<String, Object>> res = sendQuery(q);
        return res;
    }

    public List<HashMap<String, Object>> getTestsByAsis(String asis){
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header " +
                "where Serial = '%s' order by TestDate DESC", asis.toUpperCase());
        List<HashMap<String, Object>> res = sendQuery(q);
        if (res.size() == 0) {
             MsgBox.msgInfo("RMV utils", String.format("Tests for system with ASIS: %s not found.",
                     asis.toUpperCase()));
        }
        return res;
    }

    public List<HashMap<String, Object>> getTestsWithLimit(int limit){
        String q = String.format("select * from RMV.dbo.tbl_RMV_Header limit %s order by TestDate DESC", limit);
        List<HashMap<String, Object>> res = sendQuery(q);
        return res;
    }

    private List<HashMap<String, Object>> getMeasuresByHeaderId(Set headList) throws CobhamRunningException {
        String s = String.join(",", headList);
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID in (%s) order by HeaderID, MeasureID", s);
        return sendQuery(q);
    }

    private Collection<Object> getHeaderTest(String asis)
            throws CobhamRunningException, ParseException {
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

        return headerTest.values();
    }

    private HashMap<Integer, Object> getLastMeasureByNameAndAsis(String name, String asis) throws CobhamRunningException {
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

    private void appendToPath() {
        String dir = "Y:\\Projects\\Java\\Intellij\\CobhamRunning\\lib\\";
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
        } catch (CobhamRunningException | ParseException e) {
            e.printStackTrace();
        }

    }

    private List getMeasuresById(int measId) throws CobhamRunningException {
        String q = String.format("select * from RMV.dbo.tbl_RMV_MeasureData where HeaderID = %s", measId);
        List<HashMap<String, Object>> measures = new ArrayList<>();
        for (HashMap<String, Object> measure : sendQuery(q)) {
            measures.add(measure);
        }
        return measures;
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

    private Date strToDate(Object date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return formatter.parse(date.toString());

    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isServerAvailable(String address) {
        try {
            InetAddress srvAddr = InetAddress.getByName(address);
            return srvAddr.isReachable(5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<HashMap<String, Object>> sendQuery(String q) {
        System.out.println(q);
        List<HashMap<String, Object>> rows = new ArrayList<>();
        try {
            HashMap<String, Object> row;
            Statement statement = getStatment();
            ResultSet rs = statement.executeQuery(q);
            ResultSetMetaData rsmd = rs.getMetaData();
            while (rs.next()) {
                row = new HashMap<>();
                int numColumns = rsmd.getColumnCount();
                for (int i = 1; i <= numColumns; i++) {
                    String column_name = rsmd.getColumnName(i);
                    row.put(column_name, rs.getObject(column_name));
                }
                rows.add(row);
            }
        } catch (ClassNotFoundException | SQLException e){
            MsgBox.msgException(e);
        } finally {
            try {
                if (!statment.isClosed()){
                    statment.close();
                }
            } catch (SQLException e) {
                MsgBox.msgException(e);
            }
        }
        return rows;
    }
}


