package net.ddns.dimag.cobhamrunning.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.services.ShippingJournalService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.ShippingJournalData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShippingViewController implements MsgBox {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private final ShippingJournalService shippingJournalService = new ShippingJournalService();
    private ObservableList<ShippingSystem> shippingSystems = FXCollections.observableArrayList();
    private Image procImage = new Image("file:src/resources/images/process.gif");
    private Thread getDataThread;
    private MainApp mainApp;
    private Stage dialogStage;

    @FXML
    private TableView<ShippingSystem> tSysToShip;
    @FXML
    private ButtonBar bar;
    @FXML
    private DatePicker dateFrom;
    @FXML
    private DatePicker dateTo;
    @FXML
    private TextArea console;
    @FXML
    private TextField filterField;

    @FXML
    private TableColumn<ShippingSystem, String> dateShipColumn;
    @FXML
    private TableColumn<ShippingSystem, String> articleColumn;
    @FXML
    private TableColumn<ShippingSystem, String> asisColumn;
    @FXML
    private TableColumn<ShippingSystem, String> snColumn;
    @FXML
    private TableColumn<ShippingSystem, String> commonVerColumn;
    @FXML
    private TableColumn<ShippingSystem, String> systemVerColumn;
    @FXML
    private TableColumn<ShippingSystem, String> targetVerColumn;

    public ShippingViewController() {
        super();
    }

    @FXML
    private void initialize() {
        initItemMenu();
        initDate();

        console.setEditable(false);
        dateShipColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        articleColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        asisColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        snColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        commonVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        systemVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));
        targetVerColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(7));


        dateShipColumn.setCellValueFactory(cellData -> cellData.getValue().dateShipProperty());
        articleColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().asisProperty());
        snColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().snProperty());
        commonVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().commonVerProperty());
        systemVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().systemVerProperty());
        targetVerColumn.setCellValueFactory(cellData -> cellData.getValue().getDevice().targetVerProperty());

        tSysToShip.setRowFactory(tv -> {
            TableRow<ShippingSystem> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
//                    cm.show(tArticle, event.getScreenX(), event.getScreenY());
                } else if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    mainApp.showMeasureView(row.getItem().getDevice());
                }
            });
            return row;
        });


        filterField.textProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldValue,
                                Object newValue) {
                String tmp = newValue.toString().toUpperCase();
                try {
                    shippingSystems = FXCollections.observableArrayList(
                            shippingJournalService.getJournalByFilter(tmp, java.sql.Date.valueOf(dateFrom.getValue()),
                                    java.sql.Date.valueOf(dateTo.getValue())));
                    tSysToShip.setItems(shippingSystems);
                  } catch (CobhamRunningException e) {
                    e.printStackTrace();
                }


            }
        });

//		tableTypeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//				if (newValue.intValue() == 0) {
//					addBtn.setVisible(false);
//					delBtn.setVisible(false);
//					saveBtn.setVisible(false);
//				}
//				if (newValue.intValue() == 1) {
//					addBtn.setVisible(true);
//					delBtn.setVisible(true);
//					saveBtn.setVisible(true);
//				}
//				initColumns(newValue.intValue());
//			}
//		});
//		ObservableList<String> tableTypes = FXCollections.observableArrayList("Journal", "Shipping");
//		tableTypeBox.setItems(tableTypes);
//		tableTypeBox.getSelectionModel().select(0);

    }

    private void initItemMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mEdit = new MenuItem("Edit");
        menu.getItems().add(mEdit);
        tSysToShip.setContextMenu(menu);

        mEdit.setOnAction((ActionEvent event) -> {
            try {
                System.out.println(tSysToShip.getSelectionModel().getSelectedItem());
//                ShippingSystem shippingSystem = tSysToShip.getSelectionModel().getSelectedItem();
//                Device device = shippingSystem.getDevice();
//                DeviceService deviceService = new DeviceService();
//                deviceService.deleteDevice(device);
//                shippingSystems.remove(shippingSystem);
//                tSysToShip.refresh();
            } catch (Exception e) {
                LOGGER.error(e.getClass() + ": " + e.getMessage(), e);
                MsgBox.msgException(e);
            }
        });
    }

    @FXML
    private void handeleAddBtn() {
        try {
            List<String> currSys = MsgBox.msgScanSystemBarcode();
            String articleString = currSys.get(0);
            String asisString = currSys.get(1);
            String snString = MsgBox.msgInputSN();
            console.clear();
            Thread thread = new ShippingJournalData(this, asisString, articleString, snString);
            thread.start();
        } catch (NullPointerException e) {
            return;
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            MsgBox.msgException(e);
        }
    }

    public void setShippingSystems(ShippingSystem system) {
        for (ShippingSystem item : shippingSystems) {
            if (item.equals(system)) {
                return;
            }
        }
        shippingSystems.add(system);
        tSysToShip.setItems(getDeviceData());
        tSysToShip.refresh();
    }

    public ObservableList<ShippingSystem> getShippingSystems() {
        return shippingSystems;
    }

    private void initDate() {
        dateFrom.setShowWeekNumbers(true);
        dateFrom.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                dateFrom.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        dateTo.setShowWeekNumbers(true);
        dateTo.setConverter(new StringConverter<LocalDate>() {
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                dateTo.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date input = cal.getTime();
        LocalDate curDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate start = curDate.withDayOfMonth(1);
        LocalDate stop = curDate.withDayOfMonth(curDate.lengthOfMonth());
        dateFrom.setValue(start);
        dateTo.setValue(stop);
    }

    @FXML
    public void refreshJournal() {
        try {
            filterField.setText("");
            ShippingJournalService shippingJournalService = new ShippingJournalService();
            shippingSystems = FXCollections.observableArrayList(shippingJournalService
                    .getJournalByDate(java.sql.Date.valueOf(dateFrom.getValue()), java.sql.Date.valueOf(dateTo.getValue())));
            tSysToShip.setItems(shippingSystems);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void addYear() {
        dateTo.setValue(dateFrom.getValue().plusYears(1));
        refreshJournal();
    }

    @FXML
    private void addMonth() {
        dateTo.setValue(dateFrom.getValue().plusMonths(1));
        refreshJournal();
    }

    public void writeConsole(String val) {
        console.appendText(val + "\n");
        console.selectPositionCaret(console.getLength());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    private ObservableList<ShippingSystem> getDeviceData() {
        return shippingSystems;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        refreshJournal();
    }
}
