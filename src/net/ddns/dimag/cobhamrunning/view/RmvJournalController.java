package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.RmvUtils;
import net.ddns.dimag.cobhamrunning.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class RmvJournalController {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final DeviceService deviceService = new DeviceService();
    private final ToggleGroup rmvSearchRadioGroup = new ToggleGroup();
    private ObservableList<Device> devices = FXCollections.observableArrayList();
    private ObservableList<Tests> allTests = FXCollections.observableArrayList();
    private RmvUtils rmvUtils;
    private Stage dialogStage;
    private MainApp mainApp;
    private Method method;
    private RmvSearchTestsTask rmvSearchTestsTask;
    private RmvSearchMeasTask rmvSearchMeasTask;

    @FXML
    private TableView<Measurements> tMeasures;
    @FXML
    private TableView<Tests> tTests;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TextField rmvSearchField;
    @FXML
    private TextField filterField;
    @FXML
    private RadioButton asisSearch;
    @FXML
    private RadioButton articleSearch;
    @FXML
    private CheckBox getAssemblyChek;
    @FXML
    private Button rmvSearchBtn;

    @FXML
    private ProgressIndicator rmvPI;

    @FXML
    private void initialize() {
        getAssemblyChek.setDisable(true);
        initDate();
        initItemMenu();
        asisSearch.setToggleGroup(rmvSearchRadioGroup);
        articleSearch.setToggleGroup(rmvSearchRadioGroup);
        rmvSearchBtn.setDisable(true);
        asisSearch.setSelected(true);
        rmvPI.setVisible(false);


        tTests.setRowFactory(this::rowTestsFactoryTab);
        addTestColumnTab("Article", "getArticle");
        addTestColumnTab("Date", "getFormattedDateTest");
        addTestColumnTab("ASIS", "getAsis");
        addTestColumnTab("Test", "getName");
        addTestColumnTab("Time", "getTestTimeHMSM");
        addTestColumnTab("User", "getUserName");
        addTestColumnTab("Station", "getComputerName");
        addTestColumnTab("Status", "getStringTestStatus");

        tMeasures.setRowFactory(this::rowMeasFactoryTab);
        addMeasColumnTab("Name", "getMeasName");
        addMeasColumnTab("Min", "getMeasMin");
        addMeasColumnTab("Measure", "getMeasVal");
        addMeasColumnTab("Max", "getMeasMax");
        addMeasColumnTab("Status", "getStringMeasStatus");

        tTests.setRowFactory(tv -> {
            TableRow<Tests> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.getItem() != null)
                    fillMeasTable(row.getItem());
            });
            return row;
        });

        rmvSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            rmvSearchField.setText(newValue.replaceAll("\\s+", ""));
            rmvSearchBtn.setDisable(newValue.isEmpty());
        });

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<Tests> tt = filterTests(newValue);
            tTests.setItems(tt);
        });


    }

    private ObservableList<Tests> filterTests(String val) {
        ObservableList<Tests> forReturn = FXCollections.observableArrayList();
        String currVal;
        for (Tests t : allTests) {
            currVal = val.toLowerCase();
            boolean isArticle = t.getArticle().toLowerCase().contains(currVal);
            boolean isName = t.getName().toLowerCase().contains(currVal);
            boolean isAsis = t.getAsis().toLowerCase().contains(currVal);
            boolean isUser = t.getUserName().toLowerCase().contains(currVal);
            boolean isStation = t.getComputerName().toLowerCase().contains(currVal);
            if (isArticle || isName || isAsis || isUser || isStation) {
                forReturn.add(t);
            }
        }
        return forReturn;
    }

    private TableRow<Tests> rowTestsFactoryTab(TableView<Tests> view) {
        return new TableRow<>();
    }

    private TableRow<Measurements> rowMeasFactoryTab(TableView<Measurements> view) {
        return new TableRow<>();
    }

    private void addTestColumnTab(String label, String dataIndex) {
        TableColumn<Tests, String> column = new TableColumn<>(label);
        column.prefWidthProperty().bind(tTests.widthProperty().divide(8));
        column.setCellValueFactory(
                (TableColumn.CellDataFeatures<Tests, String> param) -> {
                    ObservableValue<String> result = new ReadOnlyStringWrapper("");
                    if (param.getValue() != null) {
                        try {
                            method = param.getValue().getClass().getMethod(dataIndex);
                            result = new ReadOnlyStringWrapper("" + method.invoke(param.getValue()));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            return result;
                        }
                    }
                    return result;
                }
        );
        column.setCellFactory(param -> new TableCell<Tests, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Tests rowDataItem = (Tests) getTableRow().getItem();
                if (column.getText().equalsIgnoreCase("Status")) {
                    if (rowDataItem != null && rowDataItem.getTestStatus() == 0) {
                        setTextFill(Color.GREEN);
                    } else {
                        setTextFill(Color.RED);
                    }
                }
                setText(item);
            }
        });
        tTests.getColumns().add(column);
    }

    private void addMeasColumnTab(String label, String dataIndex) {
        TableColumn<Measurements, String> column = new TableColumn<>(label);
        column.prefWidthProperty().bind(tTests.widthProperty().divide(5));
        column.setCellValueFactory(
                (TableColumn.CellDataFeatures<Measurements, String> param) -> {
                    ObservableValue<String> result = new ReadOnlyStringWrapper("");
                    if (param.getValue() != null) {
                        try {
                            method = param.getValue().getClass().getMethod(dataIndex);
                            result = new ReadOnlyStringWrapper("" + method.invoke(param.getValue()));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            return result;
                        }
                    }
                    return result;
                }
        );
        column.setCellFactory(param -> new TableCell<Measurements, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Measurements rowDataItem = (Measurements) getTableRow().getItem();
                if (column.getText().equalsIgnoreCase("Measure") && rowDataItem != null
                        && (Utils.isItAsis(item))
                        && StringUtils.containsIgnoreCase(rowDataItem.getMeasName(), "serial")) {
                    final ContextMenu menu = new ContextMenu();
                    MenuItem mSearchItem = new MenuItem("Search in RMV");
                    mSearchItem.setOnAction(event -> {
                        rmvSearchField.setText(item);
                        asisSearch.setSelected(true);
                        runSearch();
                    });
                    menu.getItems().addAll(mSearchItem);
                    setTextFill(Color.BLUE);
                    setContextMenu(menu);
                    getContextMenu().setAutoHide(true);
                } else {
                    setContextMenu(null);
                    setTextFill(Color.BLACK);
                }

                if (column.getText().equalsIgnoreCase("Status")) {
                    if (rowDataItem != null && rowDataItem.getMeasStatus() == 0) {
                        setTextFill(Color.GREEN);
                    } else {
                        setTextFill(Color.RED);
                    }
                }
                setText(item);
            }
        });
        tMeasures.getColumns().add(column);
    }

    @FXML
    private void runSearch() {
        if (getAssemblyChek.isSelected()){
            getAssembly();
        } else {
            fillTestTable();
        }

    }

    private void fillTestTable(boolean isStart) {
        rmvSearchBtn.setDisable(true);
        filterField.clear();
        rmvPI.setVisible(true);
        if (rmvPI.progressProperty().isBound()) {
            rmvPI.progressProperty().unbind();
        }
        rmvPI.setProgress(0);
        if (isStart) {
            rmvSearchTestsTask = new RmvSearchTestsTask(rmvSearchRadioGroup, isStart);
        } else {
            rmvSearchTestsTask = new RmvSearchTestsTask(rmvSearchRadioGroup);
        }

        rmvPI.progressProperty().unbind();
        rmvPI.progressProperty().bind(rmvSearchTestsTask.progressProperty());

        rmvSearchTestsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    rmvSearchBtn.setDisable(false);
                    if (rmvPI.getProgress() == -1) {
                        rmvPI.setVisible(false);
                    }
                    ObservableList<Tests> res = rmvSearchTestsTask.getValue();
                    tTests.setItems(res);
                });

        tTests.getItems().clear();
        tMeasures.getItems().clear();
        new Thread(rmvSearchTestsTask).start();
    }

    private void fillTestTable() {
        fillTestTable(false);
    }

    private void fillMeasTable(Tests currTest) {
        rmvSearchBtn.setDisable(true);
        tTests.setDisable(true);
        rmvPI.setVisible(true);
        if (rmvPI.progressProperty().isBound()) {
            rmvPI.progressProperty().unbind();
        }
        rmvPI.setProgress(0);
        rmvSearchMeasTask = new RmvSearchMeasTask(currTest);
        rmvPI.progressProperty().unbind();
        rmvPI.progressProperty().bind(rmvSearchMeasTask.progressProperty());
        rmvSearchMeasTask.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED,
                t -> {
                    rmvSearchBtn.setDisable(false);
                    tTests.setDisable(false);
                    if (rmvPI.getProgress() == -1) {
                        rmvPI.setVisible(false);
                    }
                    ObservableList<Measurements> res = rmvSearchMeasTask.getValue();
                    FXCollections.sort(res, Comparator.comparingLong(Measurements::getMeasureNumber));
                    tMeasures.setItems(res);
                });
        tMeasures.getItems().clear();
        new Thread(rmvSearchMeasTask).start();
    }

    private void getAssembly(){
        Set<String> asisSet = new HashSet<>();
        Set<String> newAsisSet = new HashSet<>();
        List<HashMap<String, Object>> tmpTests = rmvUtils.getTestsByInnerAsis(rmvSearchField.getText());
        tmpTests.forEach(c -> asisSet.add((String) c.get("Serial")));
        System.out.println(asisSet);
        while (asisSet.size() != newAsisSet.size()){
            asisSet.addAll(newAsisSet);
            for (String s: asisSet){
                tmpTests = rmvUtils.getTestsByInnerAsis(s);
                tmpTests.forEach(c -> newAsisSet.add((String) c.get("Serial")));
            }
            System.out.println(newAsisSet);
        }


    }

    @FXML
    public void refreshJournal() {

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
        LocalDate start = curDate.minusYears(1);
        LocalDate stop = curDate.withDayOfMonth(curDate.lengthOfMonth());
        dateFrom.setValue(start);
        dateTo.setValue(stop);
    }

    private void initItemMenu() {
//        ContextMenu menu = new ContextMenu();
//        MenuItem mRmvSearch = new MenuItem("Search in RMV");
//
//        menu.getItems().add(mRmvSearch);
//        tDevices.setContextMenu(menu);
//
//        mRmvSearch.setOnAction(event -> {
//            Device tmp = tDevices.getSelectionModel().getSelectedItem();
//            if (tmp != null){
////                searchByAsis(tmp.getAsis().getAsis());
//            }
//        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Stage getDialogStage() {
        return this.dialogStage;
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

    private class RmvSearchTestsTask extends Task<ObservableList<Tests>> {
        private ToggleGroup tGroup;
        private boolean isStart = false;
        List<HashMap<String, Object>> res;

        RmvSearchTestsTask(ToggleGroup tGroup) {
            this.tGroup = tGroup;
        }

        RmvSearchTestsTask(ToggleGroup tGroup, boolean isStart) {
            this.tGroup = tGroup;
            this.isStart = this.isStart;
        }

        @Override
        protected ObservableList<Tests> call() throws Exception {
            this.updateMessage("Getting data from RMV");

            if (isStart) {
                res = rmvUtils.getTestsWithLimit(50);
            } else if (tGroup.getSelectedToggle().equals(asisSearch)) {
                if (getAssemblyChek.isSelected()) {
                    res = rmvUtils.getTestsByInnerAsis(rmvSearchField.getText());
                } else {
                    res = rmvUtils.getTestsByAsisBetweenDate(rmvSearchField.getText(),
                            java.sql.Date.valueOf(dateFrom.getValue()),
                            java.sql.Date.valueOf(dateTo.getValue()));
                }
            } else {
                res = rmvUtils.getTestsByArticleBetweenDate(rmvSearchField.getText(),
                        java.sql.Date.valueOf(dateFrom.getValue()),
                        java.sql.Date.valueOf(dateTo.getValue()));
            }
            if (res.size() == 0) {
                rmvPI.progressProperty().unbind();
            }


            ArticleHeadersService articleHeadersService = new ArticleHeadersService();
            int count = res.size();
            ObservableList<Tests> forReturn = FXCollections.observableArrayList();
            this.updateMessage("Creating output table");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (HashMap<String, Object> item : res) {
                try {
                    TestCreator task = new TestCreator(tGroup, articleHeadersService, item);
                    task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent t) {
                            forReturn.add(task.getValue());
                        }
                    });
                    executor.execute(task);
                } catch (Exception e) {
                    LOGGER.error("Create test table error", e);
                    MsgBox.msgException(e);
                    return null;
                }
            }
            while (executor.getActiveCount() > 0){
                this.updateProgress(executor.getCompletedTaskCount(), count);
            }
            this.updateProgress(1, 1);
            executor.shutdown();
            this.updateMessage("Found records: " + count);
            FXCollections.sort(forReturn, Comparator.comparingLong(Tests::getDateTestMilliseconds).reversed());
            allTests = forReturn;
            return forReturn;
        }
    }

    private class RmvSearchMeasTask extends Task<ObservableList<Measurements>> {
        private Long headerID;
        private Tests test;

        RmvSearchMeasTask(Tests test) {
            this.test = test;
            this.headerID = test.getHeaderID();
        }

        @Override
        protected ObservableList<Measurements> call() throws Exception {
            List<HashMap<String, Object>> res = rmvUtils.getMeasuresByHeaderList(Collections.singletonList(headerID));
            int count = res.size();
            int i = 0;
            ObservableList<Measurements> forReturn = FXCollections.observableArrayList();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (HashMap<String, Object> item : res) {
                MeasureCreator task = new MeasureCreator(test, item);
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        forReturn.add(task.getValue());

                    }
                });
                executor.execute(task);
            }
            while (executor.getActiveCount() > 0){
                this.updateProgress(executor.getCompletedTaskCount(), count);
            }
            this.updateProgress(1, 1);
            executor.shutdown();
            FXCollections.sort(forReturn, Comparator.comparingInt(Measurements::getMeasureNumber));
            return forReturn;
        }
    }

    private class MeasureCreator extends Task<Measurements> {
        Measurements meas;
        Tests test;
        HashMap<String, Object> item;

        public MeasureCreator(Tests test, HashMap<String, Object> item) {
            this.test = test;
            this.item = item;
        }

        @Override
        protected Measurements call() throws Exception {
            meas = new Measurements();
            meas.setTest(test);
            meas.setMeasMin((String) item.get("MinLim"));
            meas.setMeasMax((String) item.get("MaxLim"));
            meas.setMeasVal((String) item.get("Result"));
            meas.setMeasDate((Date) item.get("MeasureDate"));
            meas.setMeasName((String) item.get("Description"));
            meas.setMeasureNumber((Integer) item.get("MeasureNumber"));
            meas.setMeasStatus(((Short) item.get("TestStatus")).intValue());
            return meas;
        }
    }

    private class TestCreator extends Task<Tests> {
        private ToggleGroup tGroup;
        private HashMap<String, Object> item;
        private ArticleHeadersService articleHeadersService;

        public TestCreator(ToggleGroup tGroup, ArticleHeadersService articleHeadersService, HashMap<String, Object> item) {
            this.tGroup = tGroup;
            this.articleHeadersService = articleHeadersService;
            this.item = item;
        }

        @Override
        protected Tests call() throws Exception {
            String articleName = String.format("%s%s", item.get("Article"), item.get("Revision"));
            ArticleHeaders articleHeaders = articleHeadersService.findArticleByName(articleName);
            if (articleHeaders == null) {
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
            } catch (NullPointerException ignored) {
            }
            Device device = new Device(asis, sn);
            if (!devices.contains(device))
                devices.add(device);
            Tests test = new Tests();
            test.setDateTest((Date) item.get("TestDate"));
            test.setTestTime((Integer) item.get("TestTime"));
            if (item.get("TestStatus") != null) {
                test.setTestStatus((Integer) item.get("TestStatus"));
            } else {
                test.setTestStatus(-1);
            }
            test.setName((String) item.get("Configuration"));
            test.setHeaderID((Long) item.get("HeaderID"));
            test.setUserName((String) item.get("UserName"));
            test.setComputerName((String) item.get("ComputerName"));
            test.setDevice(device);
            return test;
        }
    }
}
