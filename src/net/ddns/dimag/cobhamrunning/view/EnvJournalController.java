package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.EnvType;
import net.ddns.dimag.cobhamrunning.services.EnvDeviceService;
import net.ddns.dimag.cobhamrunning.services.EnvTypeService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvJournalController {
    private static final Logger LOGGER = LogManager.getLogger(EnvJournalController.class.getName());
    private Stage dialogStage;
    private MainApp mainApp;

    private ObservableList<EnvDevice> envDevices = FXCollections.observableArrayList();

    @FXML
    private TreeTableView<EnvDevice> tEnv;

    @FXML
    private void initialize() {
        TreeTableColumn<EnvDevice, String> locationColumn = new TreeTableColumn<>("Location");
        TreeTableColumn<EnvDevice, String> manufColumn = new TreeTableColumn<>("Manufacturer");
        TreeTableColumn<EnvDevice, String> modelColumn = new TreeTableColumn<>("Model");
        TreeTableColumn<EnvDevice, String> snColumn = new TreeTableColumn<>("Serial");
        TreeTableColumn<EnvDevice, String> statusColumn = new TreeTableColumn<>("Status");
        TreeTableColumn<EnvDevice, String> calDateColumn = new TreeTableColumn<>("Next calibr.");

//        treeTableColumn1.setCellValueFactory(new TreeItemPropertyValueFactory<>("brand"));
//        treeTableColumn2.setCellValueFactory(new TreeItemPropertyValueFactory<>("model"));

        tEnv.getColumns().add(locationColumn);
        tEnv.getColumns().add(manufColumn);
        tEnv.getColumns().add(modelColumn);
        tEnv.getColumns().add(snColumn);
        tEnv.getColumns().add(statusColumn);
        tEnv.getColumns().add(calDateColumn);

        int columnSize = tEnv.getColumns().size();
        for (TreeTableColumn column: tEnv.getColumns()){
            column.prefWidthProperty().bind(tEnv.widthProperty().divide(columnSize));
        }
    }

    @FXML
    public void handleAddButton(){
        mainApp.showEnvDeviceView(null);
//        try {
//            EnvDevice envDevice = new EnvDevice("12345678");
//            System.out.println(envDevice);
//            EnvDeviceService envDeviceService = new EnvDeviceService();
//            envDeviceService.saveEnvDevice(envDevice);
//
//
//        } catch (CobhamRunningException e) {
//            e.printStackTrace();
//        }

    }

    @FXML
    public void refreshJournal() {

    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        refreshJournal();
    }
}
