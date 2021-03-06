package net.ddns.dimag.cobhamrunning.utils;

import javafx.application.Platform;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.services.*;
import net.ddns.dimag.cobhamrunning.view.ShippingViewController;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ShippingJournalData extends Thread{
    private ShippingViewController controller;
    private Device device;
    private MainApp mainApp;
    private RmvUtils rmvUtils;

public ShippingJournalData(ShippingViewController controller, Device device) {
        this.controller = controller;
        this.device = device;
    }

    @Override
    public void run() {
        try {
            Settings settings = this.controller.getSettings();
            rmvUtils = new RmvUtils(settings.getAddr_rmv(), settings.getName_rmv(), settings.getUser_rmv(), settings.getPass_rmv());
            ShippingJournalService shippingJournalService = new ShippingJournalService();
            DeviceService deviceService = new DeviceService();
            TestsService testsService = new TestsService();
            Asis asis = device.getAsis();
            boolean isShippingSistemPresent = shippingJournalService.isDeviceInJournal(device);
            boolean isUpdateSystem = false;
            if (isShippingSistemPresent) {
                if (!MsgBox.msgConfirm("Warning", String.format("System with ASIS: %s article: %s\n" +
                                "and SN:%s already shipped.\nDo you want update test results?",
                        asis.getAsis(), asis.getArticleHeaders().getArticle(), device.getSn()))){
                    return;
                }
                isUpdateSystem = true;
            }
            isSystemInRmv(asis);
            HashMap<String, String> testMap = rmvUtils.getTestsMapWithDate(asis.getAsis());
            Set<Tests> setTests = new HashSet<Tests>();
            HashMap<Tests, Set<Measurements>> testMeasMap = new HashMap<>();

            for (String testName: testMap.keySet()){
                HashMap<String, Object> testHeader = rmvUtils.getLastTestResult(asis.getAsis(), testName);
                try {
                    if (testHeader.get("TestStatus") == null){
                        if (!MsgBox.msgConfirm("Warning", String.format("Test %s in RMV DB is not finished.\n" +
                                "Are you sure want to continue?", testName)))
                            return;
                    }else if ((int) testHeader.get("TestStatus") != 0){
                        if (!MsgBox.msgConfirm("Warning", String.format("Test %s in RMV DB is FAIL.\n" +
                                    "Are you sure want to continue?", testName)))
                        return;
                    }
                } catch (NullPointerException e){
                    MsgBox.msgWarning("Warning", String.format("Measures for test: %s not found", testName));
                    return;
                }

                Tests tests = new Tests();
                tests.setName(testName);
                tests.setDevice(device);
                tests.setDateTest(stringToDate(testHeader.get("TestDate").toString()));
                tests.setHeaderID((Long) testHeader.get("HeaderID"));
                tests.setTestTime((Integer) testHeader.get("TestTime"));

                writeConsole(String.format("Searching measures for test: %s", testName));
                Measurements meas;
                Set<Measurements> setMeas = new HashSet<>();
                for (HashMap<String, Object> measure: rmvUtils.getMeasuresById(testHeader.get("HeaderID"))){
                    System.out.println(measure);
                    writeConsole(String.format("Found measure: %s", measure.get("Description")));
                    meas = new Measurements();
                    meas.setMeasName(measure.get("Description").toString());
                    meas.setMeasMax(measure.get("MaxLim").toString());
                    meas.setMeasMin(measure.get("MinLim").toString());
                    meas.setMeasVal(measure.get("Result").toString());
                    meas.setMeasureNumber((int) measure.get("MeasureNumber"));
                    meas.setMeasDate(stringToDate(measure.get("MeasureDate").toString()));
                    meas.setTest(tests);
                    setMeas.add(meas);
                }
                testMeasMap.put(tests, setMeas);
            }
            ShippingSystem shippingSystem;

            DeviceInfo deviceInfo = getDeviseInfo(device);
            DeviceInfoService deviceInfoService = new DeviceInfoService();
            deviceInfoService.saveOrUpdateDeviceInfo(deviceInfo);
            device.setDeviceInfo(deviceInfo);
            deviceService.updateDevice(device);
            MeasurementsService measurementsService = new MeasurementsService();
            if (isUpdateSystem){
                shippingSystem = shippingJournalService.getShippingSystemByDevice(device);
                testsService.deleteTestsByDevice(device);
            } else {
                shippingSystem = new ShippingSystem();
                shippingSystem.setDevice(device);
                shippingSystem.setDateShip(new Date());
            }

            for (Tests tests: testMeasMap.keySet()){
                testsService.saveTest(tests);
                measurementsService.saveSet(testMeasMap.get(tests));
                tests.setMeas(testMeasMap.get(tests));

            }
            device.setTests(setTests);
            shippingJournalService.saveOrUpdateShippingJournal(shippingSystem);

            if (!isUpdateSystem){
                controller.setShippingSystems(shippingSystem);
            }
            writeConsole("Done");
            MsgBox.msgInfo("Add new shipping system", "Done");
        } catch (SQLException | ClassNotFoundException e) {
            MsgBox.msgException(e);
            return;
        } catch (CobhamRunningException e) {
            writeConsole(e.getMessage());
            MsgBox.msgWarning("Shipping journal", e.getMessage());
            return;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Date stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(date);
    }

    private ArticleHeaders getArticle(String article) {
        ArticleHeadersService articleHeadersService = new ArticleHeadersService();
        ArticleHeaders articleHeaders;
        try {
            articleHeaders = articleHeadersService.findArticleByName(article);
            return articleHeaders;
        } catch (IndexOutOfBoundsException e) {
            MsgBox.msgWarning("Warning", String.format("Article %s not found", article));
            return null;
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void getTestMeasures(Device device){
//        try {
//            String asis = device.getAsis().getAsis();
////            List testList = RmvUtils.getTestNamesByAsis(asis);
//            TestsService testsService = new TestsService();
//            for(Object testName: testList){
//                HashMap<Integer, Object> measList = RmvUtils.getLastMeasureByNameAndAsis(testName.toString(), asis);
//
//                System.out.println(measList);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (CobhamRunningException e) {
//            e.printStackTrace();
//        }
//        RmvUtils.getMeasuresByDevice(device);
    }

    private DeviceInfo getDeviseInfo(Device device) throws ParseException {
        writeConsole("Getting SW versions");
        return rmvUtils.getDeviceInfo(device);
    }

    private Asis getSystemAsis(String asisString, String articleString) throws CobhamRunningException {
        writeConsole(String.format("Searching system with ASIS: %s on DB", asisString));
        AsisService asisService = new AsisService();
        Asis asis = asisService.findByName(asisString);
        if (asis == null) {
            writeConsole(String.format("System with ASIS: %s not found on DB", asisString));
            ArticleHeadersService articleHeadersService = new ArticleHeadersService();
            writeConsole(String.format("Searching article %s on DB", articleString));
            ArticleHeaders articleHeaders = articleHeadersService.findArticleByName(articleString);
            if (articleHeaders == null) {
                writeConsole(String.format("Article %s not found on DB", articleString));
                throw new CobhamRunningException(String.format("Article %s not found.\nPlease add article and try again.", articleString));
            }
            writeConsole(String.format("Article %s found on DB", articleString));
            writeConsole(String.format("Creating new ASIS record"));
            asis = new Asis(asisString, articleHeaders);
            asis.setImported(true);
            new AsisService().saveAsis(asis);
        }
        writeConsole(String.format("Found system with ASIS: %s on DB", asisString));
        return asis;
    }

    private void isSystemInRmv(Asis asis) throws CobhamRunningException, SQLException, ClassNotFoundException {
        writeConsole(String.format("Searching test data for system %s in RMV base", asis.getAsis()));
        if (rmvUtils.checkRmvByAsis(asis.getAsis(), asis.getArticleHeaders().getArticle())) {
            writeConsole(String.format("System %s found in RMV base", asis.getAsis()));
        } else {
            String msg = String.format("System %s found in RMV base", asis.getAsis());
            writeConsole(msg);
            throw new CobhamRunningException(msg);
        }
    }

    private void writeConsole(String val){
        Platform.runLater(() -> controller.writeConsole(val));
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}