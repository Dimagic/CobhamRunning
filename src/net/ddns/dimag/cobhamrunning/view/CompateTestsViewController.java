package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Measurements;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class CompateTestsViewController {
    private Stage dialogStage;
    private MainApp mainApp;
    private Tests first;
    private Tests second;
    private Method method;
    private ObservableList<Measurements> measLeft = FXCollections.observableArrayList();
    private ObservableList<Measurements> measRight = FXCollections.observableArrayList();

    @FXML
    private TableView<Measurements> tLeft;
    @FXML
    private TableView<Measurements> tRight;
    @FXML
    private CheckBox hideEquals;

    @FXML
    private Label lArticleLbl;
    @FXML
    private Label lAsisLbl;
    @FXML
    private Label lTestNameLbl;
    @FXML
    private Label lDateNameLbl;
    @FXML
    private Label rArticleLbl;
    @FXML
    private Label rAsisLbl;
    @FXML
    private Label rTestNameLbl;
    @FXML
    private Label rDateNameLbl;


    @FXML
    private void initialize() {
        TableView[] tables = {tLeft, tRight};
        tLeft.setRowFactory(this::rowMeasFactoryTab);
        tRight.setRowFactory(this::rowMeasFactoryTab);
        for (TableView t: tables){
            addMeasColumnTab(t,"Name", "getMeasName");
            addMeasColumnTab(t,"Min", "getMeasMin");
            addMeasColumnTab(t,"Measure", "getMeasVal");
            addMeasColumnTab(t,"Max", "getMeasMax");
            addMeasColumnTab(t,"Status", "getStringMeasStatus");
        }

        hideEquals.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                System.out.println(measLeft.size());
                ObservableList<Measurements> tmpLeft = tLeft.getItems();
                ObservableList<Measurements> tmpRight = tRight.getItems();
                ObservableList<Measurements> forDel = FXCollections.observableArrayList();
                tmpLeft.forEach(l -> {
                    tmpRight.forEach(m -> {
                        if (l.equals(m)){
                            forDel.add(l);
                        }
                    });
                });
                tmpLeft.removeAll(forDel);
                tmpRight.removeAll(forDel);
                tLeft.setItems(tmpLeft);
                tRight.setItems(tmpRight);
            } else {
                tLeft.setItems(getMeasList(first));
                tRight.setItems(getMeasList(second));
            }
            System.out.println(newValue);

        });


    }

    private TableRow<Measurements> rowMeasFactoryTab(TableView<Measurements> view) {
        return new TableRow<>();
    }

    private void addMeasColumnTab(TableView<Measurements> tableView, String label, String dataIndex) {
        TableColumn<Measurements, String> column = new TableColumn<>(label);
        column.prefWidthProperty().bind(tableView.widthProperty().divide(5));
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
        column.setCellFactory(param -> new TableCell<Measurements, String>() {
            @Override
            public void updateItem(String item, boolean empty) {
                Measurements rowDataItem = (Measurements) getTableRow().getItem();
                if (column.getText().equalsIgnoreCase("Measure")){
                    setTooltip(new Tooltip(getText()));
                }
                if (column.getText().equalsIgnoreCase("Status")) {
                    if (rowDataItem != null && rowDataItem.getMeasStatus() == 0) {
                        setTextFill(Color.GREEN);
                    } else {
                        setTextFill(Color.RED);
                    }
                }
                setText(item);
            }
        });
        tableView.getColumns().add(column);
    }

    public void setTests(Set<Tests> testsSet){
        List<Tests> targetList = new ArrayList<>(testsSet);
        this.first = targetList.get(0);
        this.second = targetList.get(1);
        lArticleLbl.setText(first.getArticle());
        lAsisLbl.setText(first.getAsis());
        lTestNameLbl.setText(first.getName());
        lDateNameLbl.setText(Utils.getFormattedDate(first.getDateTest()));

        rArticleLbl.setText(second.getArticle());
        rAsisLbl.setText(second.getAsis());
        rTestNameLbl.setText(second.getName());
        rDateNameLbl.setText(Utils.getFormattedDate(second.getDateTest()));

        tLeft.setItems(getMeasList(first));
        tRight.setItems(getMeasList(second));
    }

    private ObservableList<Measurements> getMeasList(Tests tests){
        ObservableList<Measurements> tmp = FXCollections.observableList(new ArrayList<>(tests.getMeas()));
        FXCollections.sort(tmp, Comparator.comparingLong(Measurements::getMeasureNumber));
        return tmp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

    }
}
