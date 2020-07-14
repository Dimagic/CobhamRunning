package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DeviceJournalController {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final DeviceService deviceService = new DeviceService();
    private final TestsService testsService = new TestsService();
    private final ToggleGroup datesRadioGroup = new ToggleGroup();
    private ObservableList<Device> devices = FXCollections.observableArrayList();
    private ObservableList<Tests> tests = FXCollections.observableArrayList();
    private HashMap<Date, Object> allTests;
    private RmvUtils rmvUtils;
    private boolean rmvSearch = false;
    private Stage dialogStage;
    private MainApp mainApp;

    @FXML
    private TableView<Device> tDevices;
    @FXML
    private TableView<Tests> tTests;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TextField filterField;
    @FXML
    private RadioButton useDevDate;
    @FXML
    private RadioButton useTestDate;
    @FXML
    private TableColumn<Device, String> articleColumn;
    @FXML
    private TableColumn<Device, String> asisColumn;
    @FXML
    private TableColumn<Device, String> snColumn;

    @FXML
    private TableColumn<Tests, String> testNameColumn;
    @FXML
    private TableColumn<Tests, String> testDateColumn;
    @FXML
    private TableColumn<Tests, String> testTimeColumn;
    @FXML
    private TableColumn<Tests, String> testResultColumn;

    @FXML
    private void initialize() {
        initDate();
        useDevDate.setToggleGroup(datesRadioGroup);
        useTestDate.setToggleGroup(datesRadioGroup);
        useDevDate.setSelected(true);

        articleColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));
        asisColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));
        snColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));

        testNameColumn.prefWidthProperty().bind(tTests.widthProperty().divide(4));
        testDateColumn.prefWidthProperty().bind(tTests.widthProperty().divide(4));
        testTimeColumn.prefWidthProperty().bind(tTests.widthProperty().divide(4));
        testResultColumn.prefWidthProperty().bind(tTests.widthProperty().divide(4));

        articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
        snColumn.setCellValueFactory(cellData -> cellData.getValue().snProperty());

        testNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        testDateColumn.setCellValueFactory(cellData -> cellData.getValue().testDateProperty());
        testTimeColumn.setCellValueFactory(cellData -> cellData.getValue().testTimeProperty());
        testResultColumn.setCellValueFactory(cellData -> cellData.getValue().testStatusProperty());

        tDevices.setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                fillTestsTable(row.getItem());
            });
            return row;
        });

        tDevices.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                if (newValue != null){
                    fillTestsTable((Device) newValue);
                }
            }
        });

        tTests.setRowFactory(tv -> {
            TableRow<Tests> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())){
                    Device device = row.getItem().getDevice();
                    Tests tests = row.getItem();
                    Set<Tests> testSet = new HashSet<>();
                    testSet.add(tests);
                    device.setTests(testSet);
                    mainApp.showMeasureView(device, dialogStage);
                }
            });
            return row;
        });

        datesRadioGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov,
                                Toggle old_toggle, Toggle new_toggle) {
                if (datesRadioGroup.getSelectedToggle() != null) {
                    refreshJournal();
                }
            }
        });

        filterField.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                String tmp = newValue.toString().toUpperCase();
                if (!tmp.isEmpty()) {
                    ObservableList<Device> devicesAfter = FXCollections.observableArrayList();
                    for (Device device: devices){
                        if (device.getAsis().getAsis().contains(tmp.toUpperCase()) ||
                            device.getSn().contains(tmp.toUpperCase()) ||
                            device.getAsis().getArticleHeaders().getArticle().contains(tmp.toUpperCase())){
                            devicesAfter.add(device);
                        }
                    }
                    tDevices.setItems(devicesAfter);
                } else {
                    tDevices.setItems(devices);
                }
                tTests.getItems().clear();
            }
        });
    }

    @FXML
    public void refreshJournal() {
        rmvSearch = false;
        try {
            tTests.getItems().clear();
            devices.clear();
            if (useDevDate.isSelected()){
                devices = FXCollections.observableArrayList(
                        deviceService.findDeviceByDateCreate(
                                java.sql.Date.valueOf(dateFrom.getValue()),
                                java.sql.Date.valueOf(dateTo.getValue())));
            } else if (useTestDate.isSelected()){
                devices = FXCollections.observableArrayList(
                        deviceService.findDeviceByTestDate(
                                java.sql.Date.valueOf(dateFrom.getValue()),
                                java.sql.Date.valueOf(dateTo.getValue())));
            }
            tDevices.setItems(devices);
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    private void fillTestsTable(Device device){
        allTests = new HashMap<Date, Object>();
        if (device == null) {
            tTests.getItems().clear();
            return;
        }
        if (!rmvSearch){
            try {
                tests = FXCollections.observableArrayList(testsService.getTestsByDevice(device));
                tTests.setItems(tests);
            } catch (CobhamRunningException e) {
                LOGGER.error(e);
                MsgBox.msgException(e);
            }
        } else {
            try {
                allTests = rmvUtils.getAllTestsStatusWithDate(device.getAsis().getAsis());
                List<Tests> testList = new ArrayList<>();
                System.out.println(allTests);
                for (Date d: allTests.keySet()){
                    HashMap<String, Object> testObj = (HashMap<String, Object>) allTests.get(d);
                    Tests test = new Tests();
                    test.setName(testObj.get("Configuration").toString());
                    test.setDevice(device);
                    test.setDateTest(d);
                    test.setHeaderID((Long) testObj.get("HeaderID"));
                    test.setTestTime((Integer) testObj.get("TestTime"));
                    try {
                        test.setTestStatus((Integer) testObj.get("TestStatus"));
                    } catch (NullPointerException e){
                        test.setTestStatus(-1);
                    }
                    test.setMeas(Utils.getMeasListByTest(rmvUtils, test));
                    testList.add(test);
                }

                tests = FXCollections.observableArrayList(Utils.asSortedList(testList, Utils.COMPARE_BY_TESTDATE));
                tTests.setItems(tests);
            } catch (SQLException | ClassNotFoundException | ParseException e) {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void handleAddBtn() {
        Device device = new Device();
        System.out.println(device.getDeviceByAsisSn());
    }

    @FXML
    private void addYear() {
        dateTo.setValue(dateFrom.getValue().plusYears(1).minusDays(1));
        refreshJournal();
    }

    @FXML
    private void addMonth() {
        dateTo.setValue(dateFrom.getValue().plusMonths(1).minusDays(1));
        refreshJournal();
    }

    @FXML
    private void mouseMoveEvent(final MouseEvent mouseEvent){
        System.out.println(mouseEvent);
    }

    private void initDate() {
        dateFrom.setShowWeekNumbers(true);
        dateFrom.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                dateFrom.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        dateTo.setShowWeekNumbers(true);
        dateTo.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            {
                dateTo.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date input = cal.getTime();
        LocalDate curDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate start = curDate.withDayOfMonth(1);
        LocalDate stop = curDate.withDayOfMonth(curDate.lengthOfMonth());
        dateFrom.setValue(start);
        dateTo.setValue(stop);
    }

    @FXML
    private void searchByAsis(){
        try {
            String asisForSearch = MsgBox.msgInputString("Enter ASIS for search").toUpperCase();
            Pattern pattern = Pattern.compile("[A-Z0-9]{4}");
            Matcher matcher = pattern.matcher(asisForSearch);
            if (!matcher.matches()){
                MsgBox.msgWarning("Search by ASIS", "Incorrect ASIS");
                searchByAsis();
            }
            List<HashMap<String, Object>> res = rmvUtils.getTestsByInnerAsis(asisForSearch);
            System.out.println(res);
            tTests.getItems().clear();
            devices.clear();
            rmvSearch = true;
            ArticleHeadersService articleHeadersService = new ArticleHeadersService();
            for (HashMap<String, Object> item: res){
                String articleName = String.format("%s%s", item.get("Article"), item.get("Revision"));
                ArticleHeaders articleHeaders = articleHeadersService.findArticleByName(articleName);
                if (articleHeaders == null){
                    articleHeaders = new ArticleHeaders();
                    articleHeaders.setName(item.get("Article").toString());
                    articleHeaders.setRevision(item.get("Revision").toString());
                    articleHeaders.setLongDescript("HeadDescription");
                    articleHeaders.setShortDescript("HeadDescription");
                }
                Asis asis = new Asis(item.get("Serial").toString(), articleHeaders);
                String sn = "";
                try {
                    sn = deviceService.findDeviceByAsis(asis.getAsis()).getSn();
                } catch (NullPointerException e){

                }
                Device device = new Device(asis, sn);
                if (!devices.contains(device))
                devices.add(device);
            }
        } catch (NullPointerException e){
            return;
        } catch (CobhamRunningException e) {
            MsgBox.msgWarning("Search by ASIS", e.getMessage());
        } catch (SQLException | ClassNotFoundException e) {
            MsgBox.msgException(e);
        }


    }

    private Settings getSettings(){
        return mainApp.getCurrentSettings();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        try {
            rmvUtils = new RmvUtils(mainApp);
        } catch (CobhamRunningException e) {
            MsgBox.msgException(e);
        }
        refreshJournal();
    }
}
