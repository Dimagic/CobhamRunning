package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.MeasurementsService;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.PersistenceException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MeasureViewController {
    private Stage dialogStage;
    private MainApp mainApp;
    private Device device;
    private ObservableList<String> testsNameList = FXCollections.observableArrayList();
    private ObservableList<Measurements> staticList = FXCollections.observableArrayList();
    private ObservableList<Measurements> measList;
    private List<Tests> testsList;
    private Method method;
    private boolean hidePass = false;

    @FXML
    private TableView<Measurements> tMeasure;

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
    private Label testTime_lbl;


    @FXML
    private void initialize() {
        tMeasure.setRowFactory(this::rowFactory);

        addColumn("Name", "getMeasName");
        addColumn("Min", "getMeasMin");
        addColumn("Measure", "getMeasVal");
        addColumn("Max", "getMeasMax");
        addColumn("Status", "getStringMeasStatus");

        testsChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                Tests test = null;
                tMeasure.getItems().clear();
                try {
                    test = testsList.get(testsChoiceBox.getSelectionModel().getSelectedIndex());
                    measList = getMeasureList(test);
                } catch (PersistenceException e) {
                    measList = FXCollections.observableArrayList(
                            Utils.asSortedList(device.getTests().iterator().next().getMeas(),
                                    Utils.COMPARE_BY_MEASNUM));
                } catch (NullPointerException ignore){}
                testTime_lbl.setText(Utils.formatHMSM(test.getTestTime()));
                FXCollections.sort(measList, Comparator.comparingLong(Measurements::getMeasureNumber));
                staticList.addAll(measList);
                tMeasure.setItems(measList);
                tMeasure.refresh();
            }
        });
    }

    private TableRow<Measurements> rowFactory(TableView<Measurements> view) {
        TableRow<Measurements> row = new TableRow<>();
        return row;
    }

    private void addColumn(String label, String dataIndex) {
        TableColumn<Measurements, String> column = new TableColumn<>(label);
        column.prefWidthProperty().bind(tMeasure.widthProperty().divide(5));
        column.setCellValueFactory(
                (TableColumn.CellDataFeatures<Measurements, String> param) -> {
                    ObservableValue<String> result = new ReadOnlyStringWrapper("");
                    if (param.getValue() != null) {
                        try {
                            method = param.getValue().getClass().getMethod(dataIndex);
                            result = new ReadOnlyStringWrapper("" + method.invoke(param.getValue()));
                        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                            return result;
                        }
                    }
                    return result;
                }
        );

        column.setCellFactory(new Callback<TableColumn<Measurements, String>, TableCell<Measurements, String>>() {
            @Override
            public TableCell<Measurements, String> call(TableColumn<Measurements, String> param) {
                return new TableCell<Measurements, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        Measurements rowDataItem = (Measurements) getTableRow().getItem();
                        if (!isEmpty() && rowDataItem != null) {
                            if (column.getText().equalsIgnoreCase("Measure") && (Utils.isItAsis(item))
                                && StringUtils.containsIgnoreCase(rowDataItem.getMeasName(), "serial")) {
                                final ContextMenu menu = new ContextMenu();
                                MenuItem mSearchItem = new MenuItem("Search in RMV");
                                mSearchItem.setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        MsgBox.msgInfo("CobhamRunning", "This function is disabled.\n" +
                                                "Use RMV journal please.");
                                    }
                                });
                                menu.getItems().addAll(mSearchItem);
                                setTextFill(Color.BLUE);
                                setContextMenu(menu);
                                getContextMenu().setAutoHide(true);
                            } else {
                                setContextMenu(null);
                                setTextFill(Color.BLACK);
                            }

                            if (column.getText().equalsIgnoreCase("Status")) {
                                if (rowDataItem.getMeasStatus() == 0) {
                                    setTextFill(Color.GREEN);
                                    setContextMenu(null);
                                } else {
                                    setTextFill(Color.RED);
                                    final ContextMenu fMenu = new ContextMenu();
                                    MenuItem mHidePass = hidePass ? new MenuItem("Show all results"):
                                                                    new MenuItem("Hide pass results");
                                    mHidePass.setOnAction(new EventHandler<ActionEvent>() {
                                        @Override
                                        public void handle(ActionEvent event) {
                                            hidePass = !hidePass;
                                            ObservableList<Measurements> measListNew = FXCollections.observableArrayList();
                                            if (hidePass){
                                                for (Measurements m: measList){
                                                    if (m.getMeasStatus() == 0){
                                                        measListNew.add(m);
                                                    }
                                                }
                                                tMeasure.getItems().clear();
                                                tMeasure.setItems(measListNew);
                                            } else {
                                                tMeasure.getItems().clear();
                                                measList.addAll(staticList);
                                                tMeasure.setItems(measList);
                                            }
                                            tMeasure.refresh();
                                        }
                                    });
                                    fMenu.getItems().addAll(mHidePass);
                                    setContextMenu(fMenu);
                                    getContextMenu().setAutoHide(true);
                                }
                            }
                            setText(item);
                        }
                    }
                };
            }
        });
        tMeasure.getColumns().add(column);
    }

    private void setTableMeasure(Device device) {
        this.device = device;
        testsNameList.clear();
        TestsService testsService = new TestsService();
        try {
            testsList = testsService.getTestsByDevice(device);
        } catch (PersistenceException e) {
            testsChoiceBox.setDisable(true);
            testsList = new ArrayList<>();
            testsList.addAll(device.getTests());
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
        testsList.forEach((c) -> testsNameList.add(c.getName()));
        testsNameList.sorted();
        testsChoiceBox.setItems(FXCollections.observableArrayList(testsNameList));
        if (testsNameList.size() != 0) {
            testsChoiceBox.getSelectionModel().selectFirst();
        }
        article_lbl.setText(device.getAsis().getArticleHeaders().getArticle());
        asis_lbl.setText(device.getAsis().getAsis());
        sn_lbl.setText(device.getSn());
    }

    private ObservableList<Measurements> getMeasureList(Tests test) {
        dateTest_lbl.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(test.getDateTest()));
        MeasurementsService measurementsService = new MeasurementsService();
        ObservableList<Measurements> measList = null;
        try {
            measList = FXCollections.observableArrayList(measurementsService.getMeasureSetByTest(test));
//            testTime_lbl.setText(Utils.formatHMSM(measurementsService.getTestRunningTime(test)));
//            testTime_lbl.setText(Utils.formatHMSM(test.getTestTime()));
            return measList;
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
        return null;
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
