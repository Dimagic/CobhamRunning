package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
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
import java.util.stream.Collectors;


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
    private CheckBox includeMeasCheck;
    @FXML
    private Button rmvSearchBtn;

    /*
    Statistic
    */
   @FXML
    private CheckBox showPassCheck;
    @FXML
    private CheckBox showFailCheck;
    @FXML
    private CheckBox showIncomplCheck;
    @FXML
    private Label totalCount;
    @FXML
    private Label uniqueCount;
    @FXML
    private Label passCount;
    @FXML
    private Label failCount;
    @FXML
    private Label incomplCount;
    @FXML
    private Label passPercent;
    @FXML
    private Label failPercent;
    @FXML
    private Label incomplPercent;

    @FXML
    private ProgressIndicator rmvPI;

    @FXML
    private void initialize() {
        showPassCheck.setSelected(true);
        showFailCheck.setSelected(true);
        showIncomplCheck.setSelected(true);
        initDate();
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
                if (event.getButton() == MouseButton.PRIMARY && row.getItem() != null)
                    fillMeasTable(row.getItem());
            });
            return row;
        });

        rmvSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            rmvSearchField.setText(newValue.replaceAll("\\s+", ""));
            if (newValue.isEmpty()) {
                rmvSearchBtn.setText("Get today's tests");
            } else {
                rmvSearchBtn.setText("Run search");
            }
        });

        rmvSearchField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER) && !rmvSearchField.getText().trim().isEmpty())
            {
                runSearch();
            }
        });

        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTests();
        });

        rmvSearchRadioGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (new_toggle.equals(asisSearch)){
                includeMeasCheck.setDisable(false);
            } else {
                includeMeasCheck.setDisable(true);
                includeMeasCheck.setSelected(false);
            }
        });
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
                if (column.getText().equalsIgnoreCase("ASIS") && rowDataItem != null) {
                    final ContextMenu menu = new ContextMenu();
                    MenuItem mFilterItem = new MenuItem("Apply to filter");
                    mFilterItem.setOnAction(event -> filterField.setText(rowDataItem.getAsis()));
                    menu.getItems().addAll(mFilterItem);
                    setContextMenu(menu);
                    getContextMenu().setAutoHide(true);
                }
                if (column.getText().equalsIgnoreCase("Status")) {
                    if (rowDataItem != null) {
                        if (rowDataItem.getTestStatus() == 0) {
                            setTextFill(Color.GREEN);
                        } else if (rowDataItem.getTestStatus() == 1){
                            setTextFill(Color.RED);
                        } else {
                            setTextFill(Color.DARKORANGE);
                        }
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
                        includeMeasCheck.setSelected(true);
                        fillTestTable();
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
        fillTestTable();
    }

    private void fillTestTable() {
        allTests = FXCollections.observableArrayList();
        rmvSearchBtn.setDisable(true);
        filterField.clear();
        rmvPI.setVisible(true);
        if (rmvPI.progressProperty().isBound()) {
            rmvPI.progressProperty().unbind();
        }
        rmvPI.setProgress(0);
        if (rmvSearchField.getText().isEmpty()){
            rmvSearchTestsTask = new RmvSearchTestsTask(rmvSearchRadioGroup, true);
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
                    FXCollections.sort(res, Comparator.comparingLong(Tests::getDateTestMilliseconds).reversed());
                    tTests.setItems(res);
                    setStatistic(res);
                });
        rmvSearchTestsTask.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, t -> {
            MsgBox.msgWarning("Getting RMV data", "Something went wrong.\nPlease try again.");
            rmvSearchBtn.setDisable(false);
        });

        tTests.getItems().clear();
        tMeasures.getItems().clear();
        new Thread(rmvSearchTestsTask).start();
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
        rmvSearchMeasTask.addEventFilter(WorkerStateEvent.WORKER_STATE_FAILED,
                t -> {
                    rmvSearchBtn.setDisable(false);
                    tTests.setDisable(false);
                    MsgBox.msgWarning("Getting RMV data", "Something went wrong.\nPlease try again.");
                });
        tMeasures.getItems().clear();
        new Thread(rmvSearchMeasTask).start();
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

    @FXML
    private void filterTests() {
        ObservableList<Tests> tt = FXCollections.observableArrayList();
        String filter = filterField.getText().toLowerCase();
        if (showPassCheck.isSelected())
            tt.addAll(allTests.stream().filter(c -> c.getTestStatus() == 0).collect(Collectors.toList()));
        if (showFailCheck.isSelected())
            tt.addAll(allTests.stream().filter(c -> c.getTestStatus() == 1).collect(Collectors.toList()));
        if (showIncomplCheck.isSelected())
            tt.addAll(allTests.stream().filter(c -> c.getTestStatus() != 1 && c.getTestStatus() != 0).collect(Collectors.toList()));
        for (Tests t : allTests) {
            boolean isArticle = t.getArticle().toLowerCase().contains(filter);
            boolean isName = t.getName().toLowerCase().contains(filter);
            boolean isAsis = t.getAsis().toLowerCase().contains(filter);
            boolean isUser = t.getUserName().toLowerCase().contains(filter);
            boolean isStation = t.getComputerName().toLowerCase().contains(filter);
            boolean res = isArticle || isName || isAsis || isUser || isStation;
            if (!res) {
                tt.remove(t);
            }
        }
        setStatistic(tt);
        FXCollections.sort(tt, Comparator.comparingLong(Tests::getDateTestMilliseconds).reversed());
        tTests.setItems(tt);
    }

    private void setStatistic(ObservableList<Tests> testList){
        int testCount = testList.size();
        int testPass = 0;
        int testFail = 0;
        int testIncompl = 0;
        Set<String> asisSet = new HashSet<>();
        for (Tests test: testList){
            asisSet.add(test.getAsis());
            if (test.getTestStatus() == 0){
                testPass += 1;
            } else if (test.getTestStatus() == 1){
                testFail += 1;
            } else {
                testIncompl += 1;
            }
        }
        double pass = (double) (testPass * 100)/testCount;
        double fail = (double) (testFail * 100)/testCount;
        double incompl = (double) (testIncompl * 100)/testCount;
        totalCount.setText(Integer.toString(testCount));
        uniqueCount.setText(Integer.toString(asisSet.size()));
        passCount.setText(Integer.toString(testPass));
        failCount.setText(Integer.toString(testFail));
        incomplCount.setText(Integer.toString(testIncompl));
        passPercent.setText(String.format("%.2f %%", pass));
        failPercent.setText(String.format("%.2f %%", fail));
        incomplPercent.setText(String.format("%.2f %%", incompl));

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
            fillTestTable();
        } catch (CobhamRunningException e) {
            MsgBox.msgException(e);
            dialogStage.close();
        }
        refreshJournal();
    }

    private class RmvSearchTestsTask extends Task<ObservableList<Tests>> {
        private ToggleGroup tGroup;
        private boolean isStart;
        List<HashMap<String, Object>> res;

        RmvSearchTestsTask(ToggleGroup tGroup, boolean isStart) {
            this.tGroup = tGroup;
            this.isStart = isStart;
        }

        RmvSearchTestsTask(ToggleGroup tGroup) {
            this.tGroup = tGroup;
            this.isStart = false;
        }

        @Override
        protected ObservableList<Tests> call() throws Exception {
            this.updateMessage("Getting data from RMV");

            if (isStart) {
                res = rmvUtils.getAllTestsToday();
            } else if (tGroup.getSelectedToggle().equals(asisSearch)) {
                if (includeMeasCheck.isSelected()) {
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

            int count = res.size();
            ObservableList<Tests> forReturn = FXCollections.observableArrayList();
            this.updateMessage("Creating output table");
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
            List<Task<Tests>> notComplited = new ArrayList<>();
            for (HashMap<String, Object> item : res) {
                try {
                    TestCreator task = new TestCreator(tGroup, item);
                    task.setOnSucceeded(t -> forReturn.add(task.getValue()));
                    task.setOnFailed(t -> notComplited.add(task));
                    executor.execute(task);
                } catch (Exception e) {
                    LOGGER.error("Create test table error", e);
                    MsgBox.msgException(e);
                    return null;
                }
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                this.updateProgress(executor.getCompletedTaskCount(), count);
            }
            if (!notComplited.isEmpty()){
                MsgBox.msgWarning("Tests creator", "Not all tests created complete.");
            }
            this.updateProgress(1, 1);
            this.updateMessage("Found records: " + count);
            allTests = forReturn;
            return forReturn;
        }
    }

    private class TestCreator extends Task<Tests> {
        private ToggleGroup tGroup;
        private HashMap<String, Object> item;

        TestCreator(ToggleGroup tGroup, HashMap<String, Object> item) {
            this.tGroup = tGroup;
            this.item = item;
        }

        @Override
        protected Tests call() {
            ArticleHeaders articleHeaders = new ArticleHeaders();
            articleHeaders.setName(item.get("Article").toString());
            articleHeaders.setRevision(item.get("Revision").toString());
            articleHeaders.setLongDescript("HeadDescription");
            articleHeaders.setShortDescript("HeadDescription");

            Asis asis = new Asis(item.get("Serial").toString(), articleHeaders);
            String sn = "";
            Device device = new Device(asis, sn);
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
            ObservableList<Measurements> forReturn = FXCollections.observableArrayList();
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (HashMap<String, Object> item : res) {
                MeasureCreator task = new MeasureCreator(test, item);
                task.setOnSucceeded(t -> forReturn.add(task.getValue()));
                executor.execute(task);
            }
            executor.shutdown();
            while (!executor.isTerminated()){
                this.updateProgress(executor.getCompletedTaskCount(), count);
            }
            this.updateProgress(1, 1);
            FXCollections.sort(forReturn, Comparator.comparingInt(Measurements::getMeasureNumber));
            return forReturn;
        }
    }

    private class MeasureCreator extends Task<Measurements> {
        Measurements meas;
        Tests test;
        HashMap<String, Object> item;

        MeasureCreator(Tests test, HashMap<String, Object> item) {
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
}
