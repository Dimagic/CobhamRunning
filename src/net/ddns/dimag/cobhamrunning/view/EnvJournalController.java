package net.ddns.dimag.cobhamrunning.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvHistory;
import net.ddns.dimag.cobhamrunning.models.environment.EnvLocation;
import net.ddns.dimag.cobhamrunning.models.environment.EnvStatus;
import net.ddns.dimag.cobhamrunning.services.environment.EnvDeviceService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvHistoryService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvLocationService;
import net.ddns.dimag.cobhamrunning.services.environment.EnvStatusService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class EnvJournalController {
    private static final Logger LOGGER = LogManager.getLogger(EnvJournalController.class.getName());
    private final EnvDeviceService envDeviceService = new EnvDeviceService();
    private final EnvLocationService envLocationService = new EnvLocationService();
    private final EnvStatusService envStatusService = new EnvStatusService();
    private final EnvHistoryService envHistoryService = new EnvHistoryService();

    private Stage dialogStage;
    private MainApp mainApp;

    private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

    private Method method;
    private TreeItem<EnvDevice> root;
    private Timeline scrolltimeline = new Timeline();
    private double scrollDirection = 0;

    @FXML
    private TreeTableView<EnvDevice> tEnv;

    @FXML
    private void initialize() throws CobhamRunningException {
        tEnv.setShowRoot(false);
        tEnv.setRowFactory(this::rowFactory);
        addColumn("Location", "getLocation");
        addColumn("Manufacturer", "getManuf");
        addColumn("Model", "getModel");
        addColumn("Serial", "getSn");
        addColumn("Status", "getStatus");
        addColumn("Next calibr.", "getCalibrDate");
        setupData();
    }

    @FXML
    public void handleAddButton(){
        mainApp.showEnvDeviceView(null);
    }

    @FXML
    private void setupData() throws CobhamRunningException {
        root = new TreeItem<>(new EnvDevice(new EnvLocation("Locations")));
        setupScrolling();
        initItemMenu();
        tEnv.setRoot(root);
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
        int columnSize = tEnv.getColumns().size();
        for (TreeTableColumn column: tEnv.getColumns()){
            column.prefWidthProperty().bind(tEnv.widthProperty().divide(columnSize));
        }
    }

    private void addColumn(String label, String dataIndex) {
        TreeTableColumn<EnvDevice, String> column = new TreeTableColumn<>(label);
        column.setCellValueFactory(
                (TreeTableColumn.CellDataFeatures<EnvDevice, String> param) -> {
                    ObservableValue<String> result = new ReadOnlyStringWrapper("");
                    if (param.getValue().getValue() != null) {
                        try {
                            method = param.getValue().getValue().getClass().getMethod(dataIndex);
                            result = new ReadOnlyStringWrapper("" + method.invoke(param.getValue().getValue()));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            return result;
                        }
                    }
                    return result;
                }
        );
        tEnv.getColumns().add(column);
    }

    private void setupScrolling() {
        scrolltimeline.setCycleCount(Timeline.INDEFINITE);
        scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scroll", (ActionEvent) -> { dragScroll();}));
        tEnv.setOnDragExited(event -> {
            if (event.getY() > 0) {
                scrollDirection = 1.0 / tEnv.getExpandedItemCount();
            }
            else {
                scrollDirection = -1.0 / tEnv.getExpandedItemCount();
            }
            scrolltimeline.play();
        });
        tEnv.setOnDragEntered(event -> {
            scrolltimeline.stop();
        });
        tEnv.setOnDragDone(event -> {
            scrolltimeline.stop();
        });

    }

    private void dragScroll() {
        ScrollBar sb = getVerticalScrollbar();
        if (sb != null) {
            double newValue = sb.getValue() + scrollDirection;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            sb.setValue(newValue);
        }
    }

    private ScrollBar getVerticalScrollbar() {
        ScrollBar result = null;
        for (Node n : tEnv.lookupAll(".scroll-bar")) {
            if (n instanceof ScrollBar) {
                ScrollBar bar = (ScrollBar) n;
                if (bar.getOrientation().equals(Orientation.VERTICAL)) {
                    result = bar;
                }
            }
        }
        return result;
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

    private TreeTableRow<EnvDevice> rowFactory(TreeTableView<EnvDevice> view) {
        TreeTableRow<EnvDevice> row = new TreeTableRow<>();
        row.setOnDragDetected(event -> {
            if (!row.isEmpty()) {
                Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(row.snapshot(null, null));
                ClipboardContent cc = new ClipboardContent();
                cc.put(SERIALIZED_MIME_TYPE, row.getIndex());
                db.setContent(cc);
                event.consume();
            }
        });

        row.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (acceptable(db, row)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                event.consume();
            }
        });

        row.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (acceptable(db, row)) {
                int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
                TreeItem item = tEnv.getTreeItem(index);
                item.getParent().getChildren().remove(item);
                getTarget(row).getChildren().add(item);
                try {
                    EnvDevice envTarget = (EnvDevice) getTarget(row).getValue();
                    EnvDevice envSource = (EnvDevice) item.getValue();
                    EnvLocation envLocation = envLocationService.findEnvLocationByName(envTarget.getLocation());
                    EnvHistory envHistory = new EnvHistory(envSource, "Location", envLocation.getLocation());
                    envHistoryService.saveEnvHistory(envHistory);
                    envSource.setEnvLocation(envLocation);
                    envDeviceService.updateEnvDevice(envSource);
                } catch (CobhamRunningException e){
                    LOGGER.error(e);
                    MsgBox.msgException(e);
                }
                event.setDropCompleted(true);
                tEnv.getSelectionModel().select(item);
                event.consume();
            }
        });
        return row;
    }

    private boolean acceptable(Dragboard db, TreeTableRow<EnvDevice> row) {
        boolean result = false;
        if (db.hasContent(SERIALIZED_MIME_TYPE)) {
            int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
            if (row.getIndex() != index) {
                TreeItem target = getTarget(row);
                EnvDevice targetDev = (EnvDevice) target.getValue();
                TreeItem source = tEnv.getTreeItem(index);
                EnvDevice sourceDev = (EnvDevice) source.getValue();;
                boolean targetIsLocation = targetDev.getId() == null;
                boolean sourceIsLocation = sourceDev.getId() == null;
                boolean targetIsNotRoot = target.getParent() == null;
                boolean isSameLocation = targetDev.getLocation().equals(sourceDev.getLocation());
                result = targetIsLocation && !sourceIsLocation && !isSameLocation && !targetIsNotRoot;
            }
        }
        return result;
    }

    private TreeItem getTarget(TreeTableRow<EnvDevice> row) {
        TreeItem target = tEnv.getRoot();
        if (!row.isEmpty()) {
            target = row.getTreeItem();
        }
        return target;
    }

    // prevent loops in the tree
    private boolean isParent(TreeItem parent, TreeItem child) {
        boolean result = false;
        while (!result && child != null) {
            result = child.getParent() == parent;
            child = child.getParent();
        }
        return result;
    }

    private void initItemMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mEdit = new MenuItem("Edit");
        MenuItem mHistory = new MenuItem("History");
        menu.getItems().add(mEdit);
        menu.getItems().add(mHistory);
        tEnv.setContextMenu(menu);

        mEdit.setOnAction((ActionEvent event) -> {
            try {
                EnvDevice tmp = tEnv.getSelectionModel().getSelectedItem().getValue();
                if (tmp.getId() != null){
                    if(mainApp.showEnvDeviceView(tmp))
                        setupData();
                }
            } catch (Exception e) {
                LOGGER.error(e.getClass() + ": " + e.getMessage(), e);
                MsgBox.msgException(e);
            }
        });
        mHistory.setOnAction((ActionEvent event) -> {
            try {
                mainApp.showEnvHistoryView(tEnv.getSelectionModel().getSelectedItem().getValue());
            } catch (Exception e) {
                LOGGER.error(e.getClass() + ": " + e.getMessage(), e);
                MsgBox.msgException(e);
            }
        });
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
    }
}
