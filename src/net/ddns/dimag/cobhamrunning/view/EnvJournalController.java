package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
import net.ddns.dimag.cobhamrunning.models.environment.EnvStatus;
import net.ddns.dimag.cobhamrunning.services.environment.EnvDeviceService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvLocationService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvStatusService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EnvJournalController {
    private static final Logger LOGGER = LogManager.getLogger(EnvJournalController.class.getName());
    private final EnvDeviceService envDeviceService = new EnvDeviceService();
    private final EnvLocationService envLocationService = new EnvLocationService();
    private final EnvStatusService envStatusService = new EnvStatusService();

    private Stage dialogStage;
    private MainApp mainApp;

    private ObservableList<EnvDevice> envDevices = FXCollections.observableArrayList();

    @FXML
    private TreeTableView<EnvDevice> tEnv;

    @FXML
    private void initialize() throws CobhamRunningException {

    }

    @FXML
    public void handleAddButton(){
        mainApp.showEnvDeviceView(null);
    }

    @FXML
    public void refreshJournal() throws CobhamRunningException {
        TreeItem<EnvDevice> root = new TreeItem<>(new EnvDevice(new EnvLocation("Locations")));
        getLocationList().stream().forEach((location) -> {
            TreeItem<EnvDevice> childRoot = new TreeItem<>(new EnvDevice(new EnvLocation(location.getLocation())));
            root.getChildren().add(childRoot);
            try {
                for (EnvDevice device : getDeviceListByLocation(location)) {
                    childRoot.getChildren().add(new TreeItem<>(device));
                    childRoot.setExpanded(true);
                }
            } catch (CobhamRunningException e){
                MsgBox.msgException(e);
            }
        });
        tEnv.setRoot(root);
        tEnv.setShowRoot(false);

        TreeTableColumn<EnvDevice, String> locationColumn = new TreeTableColumn<>("Location");
        TreeTableColumn<EnvDevice, String> manufColumn = new TreeTableColumn<>("Manufacturer");
        TreeTableColumn<EnvDevice, String> modelColumn = new TreeTableColumn<>("Model");
        TreeTableColumn<EnvDevice, String> snColumn = new TreeTableColumn<>("Serial");
        TreeTableColumn<EnvDevice, String> statusColumn = new TreeTableColumn<>("Status");
        TreeTableColumn<EnvDevice, String> calDateColumn = new TreeTableColumn<>("Next calibr.");

        locationColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<EnvDevice, String> p) -> {
            try {
                return  new ReadOnlyStringWrapper(p.getValue().getValue().getEnvLocation().getLocation());
            } catch (NullPointerException e){
                return new ReadOnlyStringWrapper("");
            }
        });
        manufColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<EnvDevice, String> p) -> {
            try {
                return  new ReadOnlyStringWrapper(p.getValue().getValue().getEnvModel().getEnvManuf().getName());
            } catch (NullPointerException e){
                return new ReadOnlyStringWrapper("");
            }
        });
        modelColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<EnvDevice, String> p) -> {
            try {
                return  new ReadOnlyStringWrapper(p.getValue().getValue().getEnvModel().getName());
            } catch (NullPointerException e){
                return new ReadOnlyStringWrapper("");
            }
        });
        snColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<EnvDevice, String> p) -> {
            try {
                return  new ReadOnlyStringWrapper(p.getValue().getValue().getSn());
            } catch (NullPointerException e){
                return new ReadOnlyStringWrapper("");
            }
        });
        statusColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<EnvDevice, String> p) -> {
            try {
                return  new ReadOnlyStringWrapper(p.getValue().getValue().getEnvStatus().getStatus());
            } catch (NullPointerException e){
                return new ReadOnlyStringWrapper("");
            }
        });

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
    private void menuAddModel(){
        mainApp.showEnvModelJournalView();
    }

    @FXML
    private void menuAddStatus() throws CobhamRunningException {
        String statusName = MsgBox.msgInputString("Add new status");
        if (!statusName.isEmpty() && statusName != null) {
            EnvStatus envStatus = new EnvStatus(statusName);
            envStatusService.saveEnvStatus(envStatus);
        }
    }

    @FXML
    private void menuAddLocation() throws CobhamRunningException {
        String locationName = MsgBox.msgInputString("Add new location");
        if (!locationName.isEmpty() && locationName != null) {
            EnvLocation envLocation = new EnvLocation(locationName);
            envLocationService.saveEnvLocation(envLocation);
        }
    }

    private List<EnvLocation> getLocationList() throws CobhamRunningException{
        return envLocationService.findAllEnvLocation();
    }

    private List<EnvDevice> getDeviceList() throws CobhamRunningException{
        return envDeviceService.findAllEnvDevice();
    }

    private List<EnvDevice> getDeviceListByLocation(EnvLocation envLocation) throws CobhamRunningException{
        return envDeviceService.findAllByLocation(envLocation);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) throws CobhamRunningException {
        this.mainApp = mainApp;
        refreshJournal();
    }
}
