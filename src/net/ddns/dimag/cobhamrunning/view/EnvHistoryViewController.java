package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.models.environment.EnvHistory;
import net.ddns.dimag.cobhamrunning.services.environment.EnvHistoryService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnvHistoryViewController {
    private static final Logger LOGGER = LogManager.getLogger(ArticlesViewController.class.getName());
    private final EnvHistoryService envHistoryService = new EnvHistoryService();
    private ObservableList<EnvHistory> historyList;
    private Stage dialogStage;
    private MainApp mainApp;

    @FXML
    private Label typeLbl;
    @FXML
    private Label manufLbl;
    @FXML
    private Label modelLbl;
    @FXML
    private Label serialLbl;

    @FXML
    private TableView<EnvHistory> tHistory;

    @FXML
    private void initialize() {
        TableColumn<EnvHistory, String> dateColumn = new TableColumn<>("Date");
        TableColumn<EnvHistory, String> fieldColumn = new TableColumn<>("Field");
        TableColumn<EnvHistory, String> valueColumn = new TableColumn<>("Value");

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        fieldColumn.setCellValueFactory(cellData -> cellData.getValue().fieldProperty());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        tHistory.getColumns().add(dateColumn);
        tHistory.getColumns().add(fieldColumn);
        tHistory.getColumns().add(valueColumn);

        int columnSize = tHistory.getColumns().size();
        for (TableColumn column: tHistory.getColumns()){
            column.prefWidthProperty().bind(tHistory.widthProperty().divide(columnSize));
        }
    }

    public void setEnvDevice(EnvDevice envDevice) throws CobhamRunningException {
        typeLbl.setText(envDevice.getTypeDev());
        manufLbl.setText(envDevice.getManuf());
        modelLbl.setText(envDevice.getModel());
        serialLbl.setText(envDevice.getSn());
        historyList = FXCollections.observableArrayList(envHistoryService.findEnvHistoryByDevice(envDevice));
        tHistory.setItems(historyList);
    }

    @FXML
    private void handleClose(){
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
