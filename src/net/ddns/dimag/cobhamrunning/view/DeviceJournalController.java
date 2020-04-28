package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.Settings;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class DeviceJournalController {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final DeviceService deviceService = new DeviceService();
    private final TestsService testsService = new TestsService();
    private final ToggleGroup datesRadioGroup = new ToggleGroup();
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

        testNameColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));
        testDateColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));
        testResultColumn.prefWidthProperty().bind(tTests.widthProperty().divide(3));

        articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
        snColumn.setCellValueFactory(cellData -> cellData.getValue().snProperty());

        testNameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        testDateColumn.setCellValueFactory(cellData -> cellData.getValue().testDateProperty());
        testResultColumn.setCellValueFactory(cellData -> cellData.getValue().testStatusProperty());

        tDevices.setRowFactory(tv -> {
            TableRow<Device> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                fillTestsTable(row.getItem());
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
    }

    @FXML
    public void refreshJournal() {
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
