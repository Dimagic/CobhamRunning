package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.*;
import net.ddns.dimag.cobhamrunning.services.environment.*;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class EnvDeviceViewController {
    private static final Logger LOGGER = LogManager.getLogger(EnvDeviceViewController.class.getName());
    private final EnvDeviceService envDeviceService = new EnvDeviceService();
    private final EnvModelService envModelService = new EnvModelService();
    private final EnvStatusService envStatusService = new EnvStatusService();
    private final EnvLocationService envLocationService = new EnvLocationService();
    private final EnvHistoryService envHistoryService = new EnvHistoryService();
    private Stage dialogStage;
    private MainApp mainApp;
    private EnvDevice envDevice;
    private boolean saveClicked = false;
    private boolean locationChanged = false;
    private boolean statusChanged = false;
    private boolean dateChanged = false;

    @FXML
    private Label typeLbl;
    @FXML
    private Label manufLbl;
    @FXML
    private ComboBox<String> modelBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private ComboBox<String> locationBox;
    @FXML
    private TextField serialField;
    @FXML
    private DatePicker calibrDate;
    @FXML
    private Button plus1Y;
    @FXML
    private Button plus2Y;
    @FXML
    private Button saveBtn;

    public EnvDeviceViewController() {

    }

    @FXML
    private void initialize() throws CobhamRunningException {
        setDatePickerFormat(calibrDate);
        modelBox.setItems(getModelList());
        statusBox.setItems(getStatusList());
        locationBox.setItems(getLocationList());

        modelBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            setSaveEnable();
            try {
                EnvModel envModel = envModelService.findEnvModelByName(newValue);
                manufLbl.setText(envModel.getName());
                typeLbl.setText(envModel.getEnvType().getName());
            } catch (CobhamRunningException e) {
                LOGGER.error(e);
                MsgBox.msgException(e);
            }
        });

        statusBox.valueProperty().addListener((observable, oldValue, newValue) ->
                setSaveEnable());

        locationBox.valueProperty().addListener((observable, oldValue, newValue) ->
                setSaveEnable());

        serialField.textProperty().addListener((observable, oldValue, newValue) ->
                setSaveEnable());

    }

    private int getComboIndex(ComboBox combo){
        return combo.getSelectionModel().getSelectedIndex();
    }

    private void setSaveEnable(){
        boolean model = modelBox.getSelectionModel().isEmpty();
        boolean status = statusBox.getSelectionModel().isEmpty();
        boolean location = locationBox.getSelectionModel().isEmpty();
        boolean serial = serialField.getText().isEmpty();
        saveBtn.setDisable(model && status && location && serial);
    }

    private ObservableList<String> getModelList() throws CobhamRunningException {
        ObservableList<String> modelList = FXCollections.observableArrayList();
        for (EnvModel envModel: envModelService.findAllEnvModel()){
            modelList.add(envModel.getName());
        }
        modelBox.setItems(modelList);
        return modelList;
    }

    private ObservableList<String> getStatusList() throws CobhamRunningException {
        ObservableList<String> statusList = FXCollections.observableArrayList();
        for (EnvStatus envStatus: envStatusService.findAllEnvStatus()){
            statusList.add(envStatus.getStatus());
        }
        return statusList;
    }

    private ObservableList<String> getLocationList() throws CobhamRunningException {
        ObservableList<String> locationList = FXCollections.observableArrayList();
        for (EnvLocation envLocation: envLocationService.findAllEnvLocation()){
            locationList.add(envLocation.getLocation());
        }
        return locationList;
    }

    @FXML
    private void handleSaveBtn() {
        try {
            if (this.envDevice == null){
                EnvModel envModel = envModelService.findEnvModelByName(modelBox.valueProperty().getValue());
                EnvStatus envStatus = envStatusService.findEnvStatusByName(statusBox.valueProperty().getValue());
                EnvLocation envLocation = envLocationService.findEnvLocationByName(locationBox.valueProperty().getValue());
                EnvDevice envDevice = new EnvDevice(serialField.getText(),
                        envModel, envLocation, envStatus, java.sql.Date.valueOf(calibrDate.getValue()));
                envDeviceService.saveEnvDevice(envDevice);
            } else {
                this.envDevice.setEnvModel(envModelService.findEnvModelByName(modelBox.valueProperty().getValue()));
                this.envDevice.setEnvStatus(envStatusService.findEnvStatusByName(statusBox.valueProperty().getValue()));
                this.envDevice.setEnvLocation(envLocationService.findEnvLocationByName(locationBox.valueProperty().getValue()));
                this.envDevice.setEnvCalibrDate(java.sql.Date.valueOf(calibrDate.getValue()));
                envDeviceService.updateEnvDevice(this.envDevice);

                if (locationChanged){
                    EnvHistory envHistory = new EnvHistory(this.envDevice, "Location", this.envDevice.getLocation());
                    envHistoryService.saveEnvHistory(envHistory);
                }
                if (statusChanged){
                    EnvHistory envHistory = new EnvHistory(this.envDevice, "Status", this.envDevice.getStatus());
                    envHistoryService.saveEnvHistory(envHistory);
                }
            }
            saveClicked = true;
            this.dialogStage.close();
        } catch (Exception e){
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    @FXML
    private void addOneYear() {
        if (calibrDate.getValue() == null) {
            calibrDate.setValue(getDateNow().plusYears(1).minusDays(1));
        } else {
            calibrDate.setValue(calibrDate.getValue().plusYears(1).minusDays(1));
        }
        dateChanged = true;
    }

    @FXML
    private void addTwoYears() {
        if (calibrDate.getValue() == null) {
            calibrDate.setValue(getDateNow().plusYears(2).minusDays(1));
        } else {
            calibrDate.setValue(calibrDate.getValue().plusYears(2).minusDays(1));
        }
        dateChanged = true;
    }

    private void setDatePickerFormat(DatePicker picker){
        picker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                picker.setPromptText(pattern.toLowerCase());
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
    }

    public void setEnvDevice(EnvDevice envDevice){
        if (envDevice != null){
            this.envDevice = envDevice;
            modelBox.getSelectionModel().select(envDevice.getModel());
            serialField.setText(envDevice.getSn());
            statusBox.getSelectionModel().select(envDevice.getStatus());
            locationBox.getSelectionModel().select(envDevice.getLocation());
            calibrDate.setValue(LocalDate.parse(envDevice.getCalibrDate()));
            modelBox.setDisable(true);
            serialField.setDisable(true);
        }
    }

    private LocalDate getDateNow(){
        String pattern = "yyyy-MM-dd";
        String date = new SimpleDateFormat(pattern).format(Calendar.getInstance().getTime());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDate localDate = LocalDate.parse(date , formatter);
        return localDate;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private void closeWindowEvent(WindowEvent event) {
        if (wasChanged()){
            if(MsgBox.msgCloseWindow(dialogStage, dialogStage.getTitle(), "Current device was changed.\n" +
                    "Close without saving?")) {
                event.consume();
            }
        }
    }

    @FXML
    private void locationChanged(){
        this.locationChanged = true;
    }

    @FXML
    private void statusChanged(){
        this.statusChanged = true;
    }

    @FXML
    private boolean wasChanged(){
        return locationChanged || statusChanged || dateChanged;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
