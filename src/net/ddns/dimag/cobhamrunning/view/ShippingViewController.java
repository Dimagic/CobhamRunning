package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.services.*;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.RmvUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.postgresql.util.PSQLException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShippingViewController implements MsgBox {
    private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
    private ObservableList<ShippingSystem> shippingSystems = FXCollections.observableArrayList();
    private MainApp mainApp;
    private Stage dialogStage;

    @FXML
    private ChoiceBox<String> tableTypeBox;
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
        initDate();

    }

    @FXML
    private void handeleAddBtn() {
        Device device;
        DeviceService devServ = new DeviceService();
        List<String> currSys = MsgBox.msgScanSystemBarcode();
        String articleString = currSys.get(0);
        String asisString = currSys.get(1);

        try {
            device = devServ.findDeviceByAsis(asisString).get(0);
        } catch (IndexOutOfBoundsException e) {
            AsisService asisService = new AsisService();
            Asis asis = getAsis(asisString, articleString);
            if (asis == null) {
                return;
            }
            String snString = MsgBox.msgInputSN();
            device = new Device(asis, snString);
            try {
                devServ.saveDevice(device);
            } catch (Throwable ex){
                MsgBox.msgWarning("Doublecate data", ex.toString());
                return;
            }
            asis.setDevice(device);
        }
        try{
            int recCount = Integer.valueOf(String.valueOf(RmvUtils.checkRmvByAsis(asisString).get(0).get("rec_count")));
            if (recCount < 1){
                MsgBox.msgWarning("RMV data", String.format("Test data for ASIS: %s not found in RMV data base", asisString));
                new DeviceService().deleteDevice(device);
                return;
            }

            DeviceInfo deviceInfo = RmvUtils.getDeviceInfo(device);
            DeviceInfoService deviceInfoService = new DeviceInfoService();
            deviceInfoService.saveDeviceInfo(deviceInfo);
            device.setDeviceInfo(deviceInfo);
        } catch (Exception e){
            LOGGER.error(e);
            e.printStackTrace();
            MsgBox.msgError("Error", e.getMessage());
            return;
        }

        ShippingSystem shippingSystem = new ShippingSystem();
        shippingSystem.setDevice(device);
        shippingSystem.setDateShip(new Date());
        ShippingJournalService shippingJournalService = new ShippingJournalService();
        shippingJournalService.saveShippingJournal(shippingSystem);

        setSystemData(shippingSystem);
    }

    private void setSystemData(ShippingSystem system) {
        for (ShippingSystem item : shippingSystems) {
            if (item.equals(system)) {
                return;
            }
        }
        shippingSystems.add(system);
        tSysToShip.setItems(shippingSystems);
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

    private void setShippingSystems(ShippingSystem tmp) {
//        DeviceService devServ = new DeviceService();
//        List<Device> deviceList = new ArrayList<Device>();
//        for (ShippingSystem item : shippingSystems) {
//            if (item.equals(tmp)) {
////				MsgBox.msgInfo("Append system to running",
////						String.format("System: %s with ASIS: %s already present in table", item.articleProperty().get(),
////								item.asisProperty().get()));
//                return;
//            }
//            // devServ.saveDevice(item.getDevice());
//            // deviceList =
//            // devServ.findDeviceByAsis(item.asisProperty().getValue());
//            // for (Device dev: deviceList){
//            // System.out.println(dev.toString());
//            // }
//        }
//        shippingSystems.add(tmp);
    }

    @FXML
    private void handleSaveBtn() {

    }

    private Asis getAsis(String name, String article) {
        Asis asis;
        MacAddress macAddress;
        ArticleHeaders articleHeaders;
        AsisService asisService = new AsisService();

        asis = asisService.findByName(name);
        if (asis == null) {
            articleHeaders = getArticle(article);
            if (articleHeaders == null) {
                return null;
            }
            asis = new Asis(name, articleHeaders);
            asis.setImported(true);
            asisService.saveAsis(asis);
            if (articleHeaders.getNeedmac()) {
                MacAddressService macAddressService = new MacAddressService();
                String macString = MsgBox.msgScanMac();
                if (macString == null) {
                    return null;
                }
                macAddress = new MacAddress(macString);
                macAddress.setAsis(asis);
                macAddressService.saveMac(macAddress);
                asis.setMacAddress(macAddress);
            }
        }
        return asis;
    }

    private ArticleHeaders getArticle(String article) {
        ArticleHeadersService articleHeadersService = new ArticleHeadersService();
        ArticleHeaders articleHeaders;
        try {
            articleHeaders = articleHeadersService.findArticleByName(article).get(0);
        } catch (IndexOutOfBoundsException e) {
            MsgBox.msgWarning("Warning", String.format("Article %s not found", article));
            return null;
        }
        return articleHeaders;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        ShippingJournalService shippingJournalService = new ShippingJournalService();
        tSysToShip.setItems(FXCollections.observableArrayList(shippingJournalService
                .getJournalByDate(java.sql.Date.valueOf(dateFrom.getValue()), java.sql.Date.valueOf(dateTo.getValue()))));
    }
}
