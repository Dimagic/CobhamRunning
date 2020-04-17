package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
import net.ddns.dimag.cobhamrunning.models.environment.EnvModel;
import net.ddns.dimag.cobhamrunning.models.environment.EnvStatus;
import net.ddns.dimag.cobhamrunning.services.environment.EnvDeviceService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvLocationService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvModelService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvStatusService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class EnvDeviceViewController {
    private static final Logger LOGGER = LogManager.getLogger(EnvDeviceViewController.class.getName());
    private final EnvDeviceService envDeviceService = new EnvDeviceService();
    private final EnvModelService envModelService = new EnvModelService();
    private final EnvStatusService envStatusService = new EnvStatusService();
    private final EnvLocationService envLocationService = new EnvLocationService();
    private Stage dialogStage;
    private MainApp mainApp;
    private EnvDevice envDevice;
    private boolean saveClicked = false;

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

    public EnvDeviceViewController() {

    }

    public EnvDeviceViewController(EnvDevice envDevice) {
        this.envDevice = envDevice;
    }

    @FXML
    private void initialize() throws CobhamRunningException {
        setDatePickerFormat(calibrDate);
        modelBox.setItems(getModelList());
        statusBox.setItems(getStatusList());
        locationBox.setItems(getLocationList());

        modelBox.valueProperty().addListener((observable, oldValue, newValue) -> {
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
                System.out.println("Selected value : " + newValue));

        locationBox.valueProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Selected value : " + newValue));
    }


    private int getComboIndex(ComboBox combo){
        return combo.getSelectionModel().getSelectedIndex();
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
                saveClicked = true;
                this.dialogStage.close();
            }
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
    }

    @FXML
    private void addTwoYears() {
        if (calibrDate.getValue() == null) {
            calibrDate.setValue(getDateNow().plusYears(2).minusDays(1));
        } else {
            calibrDate.setValue(calibrDate.getValue().plusYears(2).minusDays(1));
        }
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

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
