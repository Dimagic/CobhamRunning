package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.*;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.AsisService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.MacAddressService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.CobhamSystem;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.NetworkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RunningTestController implements MsgBox {
    private ObservableList<Device> systemData = FXCollections.observableArrayList();
    private static final Logger LOGGER = LogManager.getLogger(RunningTestController.class.getName());

    @FXML
    private TableView<Device> tSysToRun;
    @FXML
    private TableColumn<Device, String> articleColumn;
    @FXML
    private TableColumn<Device, String> asisColumn;
    @FXML
    private TableColumn<Device, String> macColumn;
    @FXML
    private TableColumn<Device, String> ipColumn;
    @FXML
    private TableColumn<Device, String> statusColumn;
    @FXML
    private TableColumn<Device, Double> progressColumn;
    @FXML
    private CheckBox testCheck;
    @FXML
    private TextArea console;

    private MainApp mainApp;
    private Stage dialogStage;
    private HashMap ipMacMap;

    public boolean stopTestFlag;

    public RunningTestController() {
        this.stopTestFlag = false;
    }

    @FXML
    private void initialize() {
        console.setEditable(false);
        initItemMenu();

        articleColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));
        asisColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));
        macColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));
        ipColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));
        statusColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));
        progressColumn.prefWidthProperty().bind(tSysToRun.widthProperty().divide(6));

        articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
        macColumn.setCellValueFactory(cellData -> cellData.getValue().macProperty());
//        ipColumn.setCellValueFactory(cellData -> cellData.getValue().ipProperty());

        statusColumn.setCellValueFactory(new PropertyValueFactory<Device, String>("message"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<Device, Double>("progress"));

        progressColumn.setCellFactory(ProgressBarTableCell.<Device>forTableColumn());
    }

    @FXML
    private boolean addSystemForTest() {
        Device deviceObj;
        try {
            deviceObj = getDevice();
            setSystemData(deviceObj);
            tSysToRun.setItems(systemData);
            tSysToRun.refresh();
        } catch (CobhamRunningException e) {
            MsgBox.msgWarning("Running test", e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @FXML
    private void handleRunAllTests() {
        if (tSysToRun.getItems().size() == 0) {
            MsgBox.msgError("Running test error", "You have add one system minimum");
            return;
        }
        try {
            ipMacMap = NetworkUtils.getArpMap(mainApp.getCurrentSettings().getIp_ssh());
            stopTestFlag = false;
			for (Device deviceObj : tSysToRun.getItems()) {
			    CobhamSystem cobhamSystem = new CobhamSystem(deviceObj);
				System.out.println(deviceObj);
//                deviceObj.setLocalStopTestFlag(stopTestFlag);
//				DeviceInfoService deviceInfoService = new DeviceInfoService();
//				DeviceInfo devInfo = RmvUtils.getDeviceInfo(cobhamSystem.getDevice());
//				System.out.println(devInfo);
//				cobhamSystem.getDevice().setDeviceInfo(devInfo);
//				deviceInfoService.saveDeviceInfo(devInfo);
				handleRunTest(cobhamSystem);
			}
        } catch (Exception e) {
            LOGGER.error("handleRunAllTests()", e);
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    private void handleRunTest(CobhamSystem cobhamSystem) {
//		DeviceService devServ = new DeviceService();
//		Device devDb = (Device) devServ.findDeviceByAsis(cobhamSystem.getDevice().getAsis());
//		System.out.println(devDb.equals(cobhamSystem.getDevice()));
//		System.out.println(devDb.hashCode() == cobhamSystem.getDevice().hashCode());
        String ip;
        try {
            ip = ipMacMap.get(cobhamSystem.macProperty().getValue()).toString();
            cobhamSystem.setIp(ip);
            cobhamSystem.setMainApp(mainApp);
            tSysToRun.refresh();

            if (!cobhamSystem.isRunning() && !cobhamSystem.isDone()) {
                Thread t = new Thread(cobhamSystem);
                t.setName(cobhamSystem.asisProperty().getValue());
                t.start();
            }
        } catch (NullPointerException e) {
//			writeConsole(String.format("%s:%s Sytem not found in LAN",
//					cobhamSystem.articleProperty().getValue(), cobhamSystem.asisProperty().getValue()));
        } catch (Exception e) {
            System.out.println(String.format("EXCEPTION -> %s", e.toString()));
        }
    }

    private void initItemMenu() {
        MenuItem mStart = new MenuItem("Start");
        MenuItem mStop = new MenuItem("Stop");
        MenuItem mDel = new MenuItem("Delete");

        mStart.setOnAction((ActionEvent event) -> {
            try {
                ipMacMap = NetworkUtils.getArpMap(mainApp.getCurrentSettings().getIp_ssh());
//				handleRunTest(tSysToRun.getSelectionModel().getSelectedItem());
                tSysToRun.getSelectionModel().clearSelection();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
//		mStop.setOnAction((ActionEvent event) -> {
//			tSysToRun.getSelectionModel().getSelectedItem().setLocalStopTestFlag(true);
//			tSysToRun.getSelectionModel().clearSelection();
//		});
        mDel.setOnAction((ActionEvent event) -> {
            getSystemData().remove(tSysToRun.getSelectionModel().getSelectedItem());
            tSysToRun.refresh();
        });

//		tSysToRun.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
//			if (newSelection != null) {
//				ContextMenu menu = new ContextMenu();
//				if (newSelection.isRunning()){
//					menu.getItems().add(mStop);
//				} else {
//					menu.getItems().add(mStart);
//					menu.getItems().add(mDel);
//				}
//				tSysToRun.setContextMenu(menu);
//			}
//		});
    }

    public ObservableList<Device> getSystemData() {
        return systemData;
    }

    public void setSystemData(Device tmp) {
        DeviceService devServ = new DeviceService();
        List<Device> deviceList = new ArrayList<Device>();
        for (Device item : systemData) {
            if (item.equals(tmp)) {
                return;
            }
        }
        systemData.add(tmp);
    }

    private Device getDevice() throws CobhamRunningException {
        Device deviceObj;
        Asis asisObj;
        String articleName;
        String asisName;
        List<String> currSys = MsgBox.msgScanSystemBarcode();
        if (currSys != null && currSys.size() == 2) {
            articleName = currSys.get(0);
            asisName = currSys.get(1);
        } else {
            return null;
        }
        DeviceService deviceService = new DeviceService();
        deviceObj = deviceService.findDeviceByAsis(asisName);
        if (deviceObj != null) {
            String art = deviceObj.getAsis().getArticleHeaders().getArticle();
            if (!art.equalsIgnoreCase(articleName)) {
                throw new CobhamRunningException(String.format("ASIS: %s already has another article: %s", asisName, art));
            }
            return deviceObj;
        }
        asisObj = getAsisObj(articleName, asisName);
        String currSn = MsgBox.msgInputSN();
        deviceObj = new Device(asisObj, currSn);
        deviceService.saveDevice(deviceObj);
        return deviceObj;
    }

    private Asis getAsisObj(String articleName, String asisName) throws CobhamRunningException {
        AsisService asisService = new AsisService();

        Asis asisObj;
        ArticleHeaders articleObj;


        asisObj = asisService.findByName(asisName);
        if (asisObj != null) {
            return asisObj;
        }

        articleObj = getArticleObj(articleName);
        if (articleObj == null) {
            MsgBox.msgWarning("Running test", String.format("Article %s not found.", articleName));
            return null;
        }

        asisObj = new Asis(asisName);
        asisObj.setArticleHeaders(articleObj);
        asisObj.setImported(true);
        asisService.saveAsis(asisObj);
        if (articleObj.getNeedmac()) {
            MacAddress macObj;
            macObj = getMacObj(asisObj);
            asisObj.setMacAddress(macObj);
        }

        return asisObj;
    }

    private ArticleHeaders getArticleObj(String articleName) {
        ArticleHeadersService articleHeadersService = new ArticleHeadersService();
        ArticleHeaders articleObj;
        try {
            articleObj = articleHeadersService.findArticleByName(articleName);
            if (articleObj != null) {
                return articleObj;
            }
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MacAddress getMacObj(Asis asis) {
        MacAddressService macService = new MacAddressService();
        MacAddress macObj;
        String mac;
        mac = MsgBox.msgScanMac();
        macObj = new MacAddress(mac, asis);
        try {
            macService.saveMac(macObj);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
            return null;
        }
        return macObj;
    }

    private MacAddress getRmvMac(String asisName) {
//        AsisDbUtils asisDbUtils = new AsisDbUtils(mainApp);
//        asisDbUtils.getMacByAsis(asisName);
        return null;
    }

    private MacAddress getSelfMac() {
        return null;
    }

    public void writeConsole(String val) {
        console.appendText(val + "\n");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
