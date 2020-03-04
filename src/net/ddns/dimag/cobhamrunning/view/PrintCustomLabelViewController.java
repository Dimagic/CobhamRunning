package net.ddns.dimag.cobhamrunning.view;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.services.LabelTemplateService;
import net.ddns.dimag.cobhamrunning.utils.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javafx.collections.FXCollections.*;

public class PrintCustomLabelViewController implements MsgBox {
    private Stage dialogStage;
    private MainApp mainApp;
    private LabelTemplateService labelTemplateService = new LabelTemplateService();
    private List<LabelTemplate> labelList = new ArrayList<>();

    private final ObservableList<RowData> rowDataList = FXCollections.observableArrayList();

    @FXML
    private ChoiceBox<String> templatesBox;
    @FXML
    private Button printBtn;
    @FXML
    private TableView<RowData> tPrintJob;
    @FXML
    private TableColumn<RowData, String> fieldColumn;
    @FXML
    private TableColumn<RowData, String> dataColumn;

    @FXML
    private void initialize() {
        fillLabelBox();
        printBtn.setDisable(true);
        tPrintJob.setEditable(true);
        fieldColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(2));
        dataColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(2));

        fieldColumn.setCellValueFactory(cellData -> cellData.getValue().fieldNameProperty());
        dataColumn.setCellValueFactory(cellData -> cellData.getValue().fieldValueProperty());
        dataColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        dataColumn.setOnEditCommit(
                new EventHandler<TableColumn.CellEditEvent<RowData, String>>() {
                    @Override
                    public void handle(TableColumn.CellEditEvent<RowData, String> t) {
                        ((RowData) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setFieldValue(t.getNewValue());
                        Boolean isNotAllFill = false;
                        for (RowData row : rowDataList) {
                            if (row.fieldValue.equals("")) {
                                isNotAllFill = true;
                            }
                        }
                        printBtn.setDisable(isNotAllFill);
                    }
                }
        );

        tPrintJob.setItems(rowDataList);

        templatesBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                rowDataList.clear();
                printBtn.setDisable(true);
                fillTable(labelList.get((int) newValue).toString());
            }
        });
    }

    @FXML
    private void printLabel() {
        LabelTemplate labelTemplate = null;
        try {
            labelTemplate = labelTemplateService.findByName(templatesBox.getValue());
            String templ = labelTemplate.getTemplate();
            for (RowData row : rowDataList) {
                templ = templ.replaceAll(String.format("<:%s:>", row.getFieldName()), row.getFieldValue());
                System.out.println(String.format("%s : %s", row.getFieldName(), row.getFieldValue()));
            }
            ZebraPrint zebraPrint = new ZebraPrint(mainApp.getCurrentSettings().getPrnt_combo());
            zebraPrint.printTemplate(templ);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }

    }

    private void fillLabelBox() {
        try {
            labelList = labelTemplateService.findAllLabelTemplate();

            List<String> namesList = new ArrayList<>();
            for (LabelTemplate template : labelList) {
                namesList.add(template.getName());
            }
            templatesBox.setItems(FXCollections.observableArrayList(namesList));
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
    }

    private void initItemMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mDel = new MenuItem("Delete");
        mDel.setOnAction((ActionEvent event) -> {
            RowData template = tPrintJob.getSelectionModel().getSelectedItem();
            labelList.remove(template);
        });
        menu.getItems().add(mDel);
        tPrintJob.setContextMenu(menu);
    }

    public void fillTable(String template) {
        Matcher m = Pattern.compile("(?<=\\<:)(.*?)(?=\\:>)").matcher(template);
        while (m.find()) {
            rowDataList.add(new RowData(m.group()));
        }
        tPrintJob.setItems(rowDataList);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public static class RowData {
        private StringProperty fieldName;
        private StringProperty fieldValue;

        public RowData(String fieldName, String fieldValue) {
            this.fieldName = new SimpleStringProperty(fieldName);
            this.fieldValue = new SimpleStringProperty(fieldValue);
        }

        public RowData(String fieldName) {
            this.fieldName = new SimpleStringProperty(fieldName);
            this.fieldValue = new SimpleStringProperty("");
        }

        public String getFieldName() {
            return fieldName.get();
        }

        public StringProperty fieldNameProperty() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName.set(fieldName);
        }

        public String getFieldValue() {
            return fieldValue.get();
        }

        public StringProperty fieldValueProperty() {
            return fieldValue;
        }

        public void setFieldValue(String fieldValue) {
            this.fieldValue.set(fieldValue);
        }
    }
}
