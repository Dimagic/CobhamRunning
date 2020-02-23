package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.MeasurementsService;
import net.ddns.dimag.cobhamrunning.services.TestsService;

import java.text.SimpleDateFormat;
import java.util.List;

public class MeasureViewController {
    private Stage dialogStage;
    private MainApp mainApp;
    private Device device;
    private ObservableList<String> testsNameList = FXCollections.observableArrayList();
    private ObservableList<Measurements> measList;
    private List<Tests> testsList;

    @FXML
    private TableView<Measurements> tMeasure;
    @FXML
    private TableColumn<Measurements, String> measNameColumn;
    @FXML
    private TableColumn<Measurements, String> measMinColumn;
    @FXML
    private TableColumn<Measurements, String> measValColumn;
    @FXML
    private TableColumn<Measurements, String> measMaxColumn;
    @FXML
    private TableColumn<Measurements, String> measStatusColumn;

    @FXML
    private ChoiceBox<String> testsChoiceBox;

    @FXML
    private Label article_lbl;
    @FXML
    private Label asis_lbl;
    @FXML
    private Label sn_lbl;
    @FXML
    private Label dateTest_lbl;


    @FXML
    private void initialize() {
        measNameColumn.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));
        measMinColumn.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));
        measValColumn.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));
        measMaxColumn.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));
        measStatusColumn.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));

        measNameColumn.setCellValueFactory(cellData -> cellData.getValue().measNameProperty());
        measMinColumn.setCellValueFactory(cellData -> cellData.getValue().measMinProperty());
        measValColumn.setCellValueFactory(cellData -> cellData.getValue().measValProperty());
        measMaxColumn.setCellValueFactory(cellData -> cellData.getValue().measMaxProperty());
        measStatusColumn.setCellValueFactory(cellData -> getMeasStatus(cellData.getValue()));

        testsChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                measList = getMeasureList(testsList.get(testsChoiceBox.getSelectionModel().getSelectedIndex()));
                tMeasure.setItems(measList);
            }
        });
    }

    private void setTableMeasure(Device device){
        testsNameList.clear();
        TestsService testsService = new TestsService();
        testsList = testsService.getTestsByDevice(device);
        testsList.forEach((c) -> testsNameList.add(c.getName()));
        testsChoiceBox.setItems(FXCollections.observableArrayList(testsNameList));
        if (testsNameList.size() != 0){
            testsChoiceBox.getSelectionModel().selectFirst();
        }

        article_lbl.setText(device.getAsis().getArticleHeaders().getArticle());
        asis_lbl.setText(device.getAsis().getAsis());
        sn_lbl.setText(device.getSn());
    }

    private ObservableList<Measurements> getMeasureList(Tests test) {
        dateTest_lbl.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(test.getDateTest()));
        MeasurementsService measurementsService = new MeasurementsService();
        ObservableList<Measurements> measList = FXCollections.observableArrayList(measurementsService.getMeasureSetByTest(test));
        return measList;
    }

    private SimpleStringProperty getMeasStatus(Measurements measure){
        String status = "FAIL";
        if (measure.getMeasMin().equals("\"\"") &&  measure.getMeasMax().equals("\"\"")){
            status = "PASS";
            return new SimpleStringProperty(status);
        } else {
            try {
                double measVal = Double.parseDouble(measure.getMeasVal());
                double measMin = Double.parseDouble(measure.getMeasMin());
                double measMax = Double.parseDouble(measure.getMeasMax());
                if (measMin <= measVal || measVal <= measMax){
                    status = "PASS";
                }
            } catch (Exception e){
                if (measure.getMeasMin().equals(measure.getMeasMax()) &&  measure.getMeasMin().equals(measure.getMeasVal())){
                    status = "PASS";
                }
            } finally {
                return new SimpleStringProperty(status);
            }
        }
    }

    public void setDevice(Device device) {
        this.device = device;
        setTableMeasure(device);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
}
