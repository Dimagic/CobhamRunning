package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.EnvManuf;
import net.ddns.dimag.cobhamrunning.models.environment.EnvModel;
import net.ddns.dimag.cobhamrunning.models.environment.EnvType;
import net.ddns.dimag.cobhamrunning.services.environment.EnvManufService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvModelService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvTypeService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvModelJournalController {
    private static final Logger LOGGER = LogManager.getLogger(EnvModelJournalController.class.getName());
    private final EnvModelService envModelService = new EnvModelService();
    private final EnvManufService envManufService = new EnvManufService();
    private final EnvTypeService envTypeService = new EnvTypeService();
    private Stage dialogStage;
    private MainApp mainApp;

    @FXML
    private TableView<EnvModel> tEnvMod;
    @FXML
    private TableColumn<EnvModel, String> manufColumn;
    @FXML
    private TableColumn<EnvModel, String> typeColumn;
    @FXML
    private TableColumn<EnvModel, String> modelColumn;
    @FXML
    private Button addModelBtn;
    @FXML
    private Button addTypeBtn;
    @FXML
    private ComboBox<String> manufCombo;
    @FXML
    private ComboBox<String> typeCombo;


    @FXML
    private void initialize() throws CobhamRunningException {
        fillTable();
        manufCombo.setItems(getManufList());

        addModelBtn.setDisable(true);
        typeCombo.setDisable(true);
        addTypeBtn.setDisable(true);

        manufColumn.prefWidthProperty().bind(tEnvMod.widthProperty().divide(3));
        typeColumn.prefWidthProperty().bind(tEnvMod.widthProperty().divide(3));
        modelColumn.prefWidthProperty().bind(tEnvMod.widthProperty().divide(3));

        manufColumn.setCellValueFactory(cellData -> cellData.getValue().manufProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        modelColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        manufCombo.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            typeCombo.setDisable(newValue == null);
            typeCombo.setItems(getTypeList());
            addTypeBtn.setDisable(newValue == null);
            addModelBtn.setDisable(newValue == null);
        });

        typeCombo.valueProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
            addModelBtn.setDisable(newValue == null);
        });


    }

    @FXML
    private void addEnvModel() throws CobhamRunningException {
        String modelName = MsgBox.msgInputString("Add new model");
        if (!modelName.isEmpty()) {
            EnvManuf envManuf = envManufService.findEnvManufByName(manufCombo.valueProperty().getValue());
            EnvType envType = envTypeService.findEnvTypeByName(typeCombo.valueProperty().getValue());
            EnvModel envModel = new EnvModel(modelName, envManuf, envType);
            envModelService.saveEnvModel(envModel);
            fillTable();
        }
    }

    @FXML
    private void addEnvManuf() throws CobhamRunningException {
        String manufName = MsgBox.msgInputString("Add new manufacturer");
        if (manufName != null){
            if (!manufName.isEmpty()){
                EnvManuf envManuf = new EnvManuf(manufName);
                envManufService.saveEnvManuf(envManuf);
            }
            manufCombo.setItems(getManufList());
        }
    }

    @FXML
    private void addEnvType() throws CobhamRunningException {
        String typeName = MsgBox.msgInputString("Add new type");
        if (typeName != null){
            if (!typeName.isEmpty()){
                EnvType envType = new EnvType(typeName) ;
                envTypeService.saveEnvType(envType);
            }
            typeCombo.setItems(getTypeList());
        }
    }

    private void fillTable() throws CobhamRunningException {
        ObservableList<EnvModel> modelList = FXCollections.observableArrayList();
        for (EnvModel envModel: envModelService.findAllEnvModel()){
            modelList.add(envModel);
        }
        tEnvMod.setItems(modelList);
    }

    private ObservableList<String> getManufList() throws CobhamRunningException {
        ObservableList<String> manufList = FXCollections.observableArrayList();
        try {
            for (EnvManuf envManuf: envManufService.findAllEnvManuf()){
                manufList.add(envManuf.getName());
            }
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
        return manufList;
    }

    private ObservableList<String> getTypeList(){
        ObservableList<String> typeList = FXCollections.observableArrayList();
        try {
            for (EnvType envType: envTypeService.findAllEnvType()){
                typeList.add(envType.getName());
            }
        } catch (CobhamRunningException e) {
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
        return typeList;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
