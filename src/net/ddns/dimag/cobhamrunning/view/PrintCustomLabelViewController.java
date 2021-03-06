package net.ddns.dimag.cobhamrunning.view;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.services.LabelTemplateService;
import net.ddns.dimag.cobhamrunning.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintCustomLabelViewController implements MsgBox, SystemCommands {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private MainApp mainApp;
    private Stage dialogStage;
    private TestsViewController controller;
    private LabelTemplateService labelTemplateService = new LabelTemplateService();
    private List<LabelTemplate> labelList = new ArrayList<>();
    private final ObservableList<RowData> rowDataList = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox<String> templatesBox;
    @FXML
    private CheckBox useRmvData;
    @FXML
    private CheckBox svwViaIp;
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
                        boolean isNotAllFill = false;
                        for (RowData row : rowDataList) {
                            if (row.fieldValue.getValue().trim().isEmpty()) {
                                isNotAllFill = true;
                                break;
                            }
                        }
                        printBtn.setDisable(isNotAllFill);
                    }
                }
        );

        useRmvData.selectedProperty().addListener((observable, oldValue, newValue) -> {
            rowDataList.clear();
            if (svwViaIp.isSelected())
                svwViaIp.setSelected(!newValue);
            templatesBox.getSelectionModel().clearSelection();
            templatesBox.setDisable(newValue);
            printBtn.setDisable(!newValue);
            if (newValue) {
                printLabel();
            }
        });

        svwViaIp.selectedProperty().addListener((observable, oldValue, newValue) -> {
            rowDataList.clear();
            if (useRmvData.isSelected())
                useRmvData.setSelected(false);
            templatesBox.getSelectionModel().clearSelection();
            templatesBox.setDisable(newValue);
            printBtn.setDisable(!newValue);
            if (newValue) {
                printLabel();
            }
        });

        templatesBox.getSelectionModel().selectedIndexProperty().addListener((ChangeListener) (observable, oldValue, newValue) -> {
            if ((int) newValue >= 0) {
                rowDataList.clear();
                printBtn.setDisable(true);
                fillTable(labelList.get((int) newValue).toString());
            }
        });

        tPrintJob.setItems(rowDataList);
    }

    private void printSwv() {
        controller = mainApp.getController();
        Settings settings = mainApp.getCurrentSettings();
        String user = settings.getLogin_ssh();
        String pass = settings.getPass_ssh();
        if (user == null || pass == null) {
            MsgBox.msgInfo("SSH connection", "Not all settings are filling");
            return;
        }
        String host = MsgBox.msgInputIP();
        if (host == null) {
            return;
        }
        JSSHClient jsshClient = new JSSHClient(host, user, pass, mainApp);
        try {
            if (jsshClient.isSshConected()) {
                String asis = jsshClient.cobhamGetAsis();
                Map<String, String> swv = jsshClient.cobhamGetSwv();
                String swvTemplate = String.format(swvTemplateCmd, asis, swv.get("target"),
                        swv.get("common"), swv.get("system"));
                ZebraPrint zebraPrint = new ZebraPrint(mainApp.getCurrentSettings().getPrnt_combo());
                zebraPrint.printTemplate(swvTemplate);
            } else {
                writeConsole("Not connected");
            }
        } catch (CobhamRunningException e) {
            MsgBox.msgException(e);
        }
    }

    @FXML
    private void printLabel() {
        String articleString;
        String asisString;
        if (useRmvData.isSelected()) {
            HashMap<String, Object> rmvRes = new HashMap<>();
            List<String> currSys = MsgBox.msgScanSystemBarcode();
            try {
                articleString = currSys.get(0);
                asisString = currSys.get(1);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                return;
            }
            try {
                RmvUtils rmvUtils = new RmvUtils(mainApp);
                rmvRes = rmvUtils.getLastTestsStatusWithDate(asisString);
            } catch (ParseException | CobhamRunningException e) {
                LOGGER.error(e);
                MsgBox.msgWarning("Print label", e.getLocalizedMessage());
                return;
            }
            if (rmvRes.keySet().isEmpty()) {
                MsgBox.msgInfo("Print label", String.format("Tests result for system with ASIS: %s not found", asisString));
                return;
            }
            String status;
            String date;
            HashMap<String, Object> test;
            for (String k : rmvRes.keySet()) {
                test = (HashMap<String, Object>) rmvRes.get(k);
                status = getStringTestStatus(test);
                date = dateFormat.format(test.get("TestDate"));
                writeConsole(String.format("Found test: %s Date: %s Status: %s", test.get("Configuration"),
                        date, status));
            }
            String testName = MsgBox.msgChoice(String.format("Select test for system with ASIS: %s", asisString),
                    "Tests:", new ArrayList<String>(rmvRes.keySet()));
            if (testName != null) {
                for (String k : rmvRes.keySet()) {
                    test = (HashMap<String, Object>) rmvRes.get(k);
                    if (test.get("Configuration").equals(testName)) {
                        if (test.get("TestStatus") == null || Integer.parseInt(String.valueOf(test.get("TestStatus"))) != 0) {
                            boolean q = MsgBox.msgConfirm2("Print label", String.format("Test: %s has status FAIL.\n" +
                                    "Do you want print this label?", testName));
                            if (!q)
                                return;
                        }
                    }
                }
                HashMap<String, Object> currTest = (HashMap<String, Object>) rmvRes.get(testName);
                String rmvTemplate = String.format(rmvTemplateCmd, String.format("%s/%s", articleString, asisString), currTest.get("Configuration"),
                        getStringTestStatus(currTest), dateToString((Date) currTest.get("TestDate")));
                ZebraPrint zebraPrint = new ZebraPrint(mainApp.getCurrentSettings().getPrnt_combo());
                zebraPrint.printTemplate(rmvTemplate);
            }
        } else if (svwViaIp.isSelected()) {
            printSwv();
        } else {
            LabelTemplate labelTemplate = null;
            try {
                labelTemplate = labelTemplateService.findByName(templatesBox.getValue());
                String templ = labelTemplate.getTemplate();
                for (RowData row : rowDataList) {
                    templ = templ.replaceAll(String.format("<:%s:>", row.getFieldName()), row.getFieldValue());
                }
                ZebraPrint zebraPrint = new ZebraPrint(mainApp.getCurrentSettings().getPrnt_combo());
                zebraPrint.printTemplate(templ);
            } catch (CobhamRunningException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void openEditLabelTemplateView() {
        if (mainApp.showLabelTemplatesView(this.dialogStage)) {
            fillLabelBox();
            rowDataList.clear();
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

    private void fillTable(String template) {
        Matcher m = Pattern.compile("(?<=\\<:)(.*?)(?=\\:>)").matcher(template);
        HashSet<String> valSet = new HashSet<>();
        while (m.find()) {
            valSet.add(m.group());
        }
        for (String s : valSet) {
            rowDataList.add(new RowData(s));
        }
        tPrintJob.setItems(rowDataList);
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private void writeConsole(String val) {
        mainApp.getController().writeConsole(val);
    }

    private String getStringTestStatus(HashMap<String, Object> test){
        return (int) test.get("TestStatus") == 0 ? "PASS": "FAIL";
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
