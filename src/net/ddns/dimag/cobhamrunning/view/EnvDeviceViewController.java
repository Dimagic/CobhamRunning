package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.SnapshotResult;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.EnvType;
import net.ddns.dimag.cobhamrunning.services.EnvTypeService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;

import java.util.List;

public class EnvDeviceViewController {
    private Stage dialogStage;
    private MainApp mainApp;
    private EnvDevice envDevice;
    private boolean saveClicked = false;
    private final ObservableList<String> initList = FXCollections.observableArrayList("Add new...");

    @FXML
    private ComboBox<String> typeBox;
    @FXML
    private ComboBox<String> manufBox;
    @FXML
    private ComboBox<String> modelBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private ComboBox<String> locationBox;
    @FXML
    private TextField serialField;

    public EnvDeviceViewController() {

    }

    public EnvDeviceViewController(EnvDevice envDevice) {
        this.envDevice = envDevice;
    }

    @FXML
    private void initialize() throws CobhamRunningException {
        typeBox.setItems(getTypeComboData());
        manufBox.setItems(initList);
        modelBox.setItems(initList);
        statusBox.setItems(initList);
        locationBox.setItems(initList);

        typeBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (getComboIndex(typeBox) == 0){
                String newType = MsgBox.msgInputString("Add new environment type");
                if (newType != null){
                    try {
                        new EnvTypeService().saveEnvType(new EnvType(newType));
                        typeBox.setItems(getTypeComboData());
                    } catch (CobhamRunningException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        manufBox.valueProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Selected value : " + newValue));

        modelBox.valueProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Selected value : " + newValue));

        statusBox.valueProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Selected value : " + newValue));

        locationBox.valueProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Selected value : " + newValue));
    }

    private ObservableList<String> getTypeComboData() throws CobhamRunningException {
        EnvTypeService typeService = new EnvTypeService();
        ObservableList<String> typeList = initList;
        List<EnvType> dbList = typeService.findAllEnvType();
        if (dbList.size() != 0 && dbList != null){
            for (EnvType s: dbList){
                typeList.add(s.getType());
            }
        }
        return typeList;

    }

    private int getComboIndex(ComboBox combo){
        return combo.getSelectionModel().getSelectedIndex();
    }

    @FXML
    private boolean handleSaveBtn(){
        return false;
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
