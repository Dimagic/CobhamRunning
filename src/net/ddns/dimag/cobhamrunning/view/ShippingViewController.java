package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.utils.Settings;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.services.ShippingJournalService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.ReportGenerator;
import net.ddns.dimag.cobhamrunning.utils.ShippingJournalData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ShippingViewController implements MsgBox {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final ShippingJournalService shippingJournalService = new ShippingJournalService();
    private ObservableList<ShippingSystem> shippingSystems = FXCollections.observableArrayList();
    private MainApp mainApp;
    private Stage dialogStage;

    @FXML
    private TableView<ShippingSystem> tSysToShip;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TextArea console;
    @FXML
    private TextField filterField;

    @FXML
    private TableColumn<ShippingSystem, String> dateShipColumn;
    @FXML
    private TableColumn<ShippingSystem, String> articleColumn;
    @FXML
    private TableColumn<ShippingSystem, String> asisColumn;
    @FXML
    private TableColumn<ShippingSystem, String> snColumn;
    @FXML
    private TableColumn<ShippingSystem, String> commonVerColumn;
    @FXML
    private TableColumn<ShippingSystem, String> systemVerColumn;
    @FXML
    private TableColumn<ShippingSystem, String> targetVerColumn;

    public ShippingViewController() {
        super();
    }

    @FXML
    private void initialize() {
        initItemMenu();
        initDate();

        console.setEditable(false);
        dateShipColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        articleColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        asisColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        snColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        commonVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        systemVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        targetVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));


        dateShipColumn.setCellValueFactory(cellData -> cellData.getValue().dateShipProperty());
        articleColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().asisProperty());
        snColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().snProperty());
        commonVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().commonVerProperty());
        systemVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().systemVerProperty());
        targetVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().targetVerProperty());

        tSysToShip.setRowFactory(tv -> {
            TableRow<ShippingSystem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    mainApp.showMeasureView(row.getItem().getDevice());
                }
            });
            return row;
        });

        filterField.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                String tmp = newValue.toString().toUpperCase();
                try {
                    if (!tmp.isEmpty()) {
                        shippingSystems = FXCollections.observableArrayList(
                                shippingJournalService.getJournalByFilter(tmp, java.sql.Date.valueOf(dateFrom.getValue()),
                                        java.sql.Date.valueOf(dateTo.getValue())));
                        tSysToShip.setItems(shippingSystems);
                    } else {
                        refreshJournal();
                    }
                } catch (CobhamRunningException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initItemMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mEdit = new MenuItem("Edit");
        menu.getItems().add(mEdit);
        tSysToShip.setContextMenu(menu);

        mEdit.setOnAction((ActionEvent event) -> {
            try {
                System.out.println(tSysToShip.getSelectionModel().getSelectedItem().toString());
            } catch (Exception e) {
                LOGGER.error(e.getClass() + ": " + e.getMessage(), e);
                MsgBox.msgException(e);
            }
        });
    }

    @FXML
    private void handleAddBtn() {
        try {
            Device device = new Device().getDeviceByAsisSn();
            if (device == null){
                return;
            }
            console.clear();
            Thread thread = new ShippingJournalData(this, device);
            thread.start();
        } catch (NullPointerException e) {
            LOGGER.error(e.getMessage());
            return;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MsgBox.msgException(e);
        }
    }

    @FXML
    private void handleExcelReport() {
        ReportGenerator reportGenerator =
                new ReportGenerator(dateFrom.getValue(), dateTo.getValue(), filterField.getText(), shippingSystems);
        if (!reportGenerator.isOfficeInstalled()){
            MsgBox.msgInfo("Shipping system report", "Microsoft Excel not found in the system");
        } else {
            if (reportGenerator.shippingSystemReportToExcell()) {
                MsgBox.msgInfo("Shipping system report", "Report generation complete");
            } else {
                MsgBox.msgError("Shipping system report", "Report generation failure");
            }
        }
    }

    public void setShippingSystems(ShippingSystem system) {
        for (ShippingSystem item : shippingSystems) {
            if (item.equals(system)) {
                return;
            }
        }
        shippingSystems.add(system);
        tSysToShip.setItems(getDeviceData());
        tSysToShip.refresh();
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
    public void refreshJournal() {
        try {
            filterField.setText("");
            shippingSystems = FXCollections.observableArrayList(shippingJournalService
                    .getJournalByDate(java.sql.Date.valueOf(dateFrom.getValue()), java.sql.Date.valueOf(dateTo.getValue())));
            tSysToShip.setItems(shippingSystems);
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }

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

    public void writeConsole(String val) {
        console.appendText(val + "\n");
        console.selectPositionCaret(console.getLength());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private ObservableList<ShippingSystem> getDeviceData() {
        return shippingSystems;
    }

    public Settings getSettings(){
        return mainApp.getCurrentSettings();
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        refreshJournal();
    }
}
