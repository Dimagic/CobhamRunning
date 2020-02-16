package net.ddns.dimag.cobhamrunning.view;

import com.sun.javafx.binding.StringFormatter;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.services.*;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.RmvUtils;
import net.ddns.dimag.cobhamrunning.utils.ShippingJournalData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.postgresql.util.PSQLException;

import javax.persistence.NoResultException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;

public class ShippingViewController implements MsgBox {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
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
    private Button addBtn;
    @FXML
    private Button delBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private TextArea console;

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



//        console.textProperty().addListener(new ChangeListener<Object>() {
//            @Override
//            public void changed(ObservableValue<?> observable, Object oldValue,
//                                Object newValue) {
//                console.setScrollTop(Double.MAX_VALUE);
//            }
//        });

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
        MenuItem mDel = new MenuItem("Delete");
        menu.getItems().add(mDel);
        tSysToShip.setContextMenu(menu);

        mDel.setOnAction((ActionEvent event) -> {
            ShippingSystem shippingSystem = tSysToShip.getSelectionModel().getSelectedItem();
            new DeviceService().deleteDevice(shippingSystem.getDevice());
            shippingSystems.remove(shippingSystem);
            tSysToShip.refresh();
        });
    }

    @FXML
    private void handeleAddBtn() {
        List<String> currSys = MsgBox.msgScanSystemBarcode();
        String articleString = currSys.get(0);
        String asisString = currSys.get(1);
        String snString = MsgBox.msgInputSN();
        console.clear();
//        Caller caller = new Caller();
//        new GetData(caller, this, asisString, articleString, snString).start();
        Thread thread = new ShippingJournalData(this, asisString, articleString, snString);
        thread.start();


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

    public ObservableList<ShippingSystem> getShippingSystems(){
        return shippingSystems;
    }

    private void initDate() {
        dateFrom.setShowWeekNumbers(true);
        dateTo.setShowWeekNumbers(true);

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
    private void handleSaveBtn() {

    }


    public void writeConsole(String val) {
        console.appendText(val + "\n");
        console.selectPositionCaret(console.getLength());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public ObservableList<ShippingSystem> getDeviceData() {
        return shippingSystems;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        ShippingJournalService shippingJournalService = new ShippingJournalService();
        shippingSystems = FXCollections.observableArrayList(shippingJournalService
                .getJournalByDate(java.sql.Date.valueOf(dateFrom.getValue()), java.sql.Date.valueOf(dateTo.getValue())));
        tSysToShip.setItems(shippingSystems);
    }

//    static class Caller implements Callback{
//        private ArrayList<ShippingSystem> systems = new ArrayList<>();
//
//        public ArrayList<ShippingSystem> getSystems(){
//            return systems;
//        }
//        @Override
//        public void callMeBack(ShippingSystem system){
//            synchronized (systems) {
//                systems.add(system);
//            }
//        }
//    }
//
//    interface Callback {
//        void callMeBack(ShippingSystem system);
//    }


}
