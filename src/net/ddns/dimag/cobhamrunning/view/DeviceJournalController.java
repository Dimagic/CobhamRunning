package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Settings;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DeviceJournalController {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final DeviceService deviceService = new DeviceService();
    private final TestsService testsService = new TestsService();
    private ObservableList<Device> devices = FXCollections.observableArrayList();
    private ObservableList<Tests> tests = FXCollections.observableArrayList();
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
    private TableColumn<Device, String> articleColumn;
    @FXML
    private TableColumn<Device, String> revColumn;
    @FXML
    private TableColumn<Device, String> asisColumn;
    @FXML
    private TableColumn<Device, String> snColumn;

    @FXML
    private TableColumn<Tests, String> testNameColumn;
    @FXML
    private TableColumn<Tests, String> testDateColumn;
    @FXML
    private TableColumn<Tests, String> testResultColumn;

    @FXML
    private void initialize() {
        articleColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));
        asisColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));
        snColumn.prefWidthProperty().bind(tDevices.widthProperty().divide(3));

        testNameColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));
        testDateColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));
        testResultColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));

        articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
        snColumn.setCellValueFactory(cellData -> cellData.getValue().snProperty());

        testNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        testDateColumn.setCellValueFactory(cellData -> cellData.getValue().testDateProperty());
//        testResultColumn.setCellValueFactory(cellData -> cellData.getValue().);

        tDevices.setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                fillTestsTable(row.getItem());
            });
            return row;
        });
    }

    @FXML
    public void refreshJournal() {
        try {
            devices = FXCollections.observableArrayList(deviceService.findAllDevice());
            tDevices.setItems(devices);
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    private void fillTestsTable(Device device){
        if (device == null) {
            tTests.getItems().clear();
            return;
        }
        try {
            tests = FXCollections.observableArrayList(testsService.getTestsByDevice(device));
            System.out.println(tests);
            tTests.setItems(tests);
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    @FXML
    private void handleAddBtn() {
        System.out.println("ADD");
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

    private Settings getSettings(){
        return mainApp.getCurrentSettings();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        refreshJournal();
    }
}
