package net.ddns.dimag.cobhamrunning.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.services.AsisService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.CobhamSystem;
import net.ddns.dimag.cobhamrunning.models.DeviceInfo;
import net.ddns.dimag.cobhamrunning.services.DeviceInfoService;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.NetworkUtils;
import net.ddns.dimag.cobhamrunning.utils.RmvUtils;

public class RunningTestController implements MsgBox {
    private ObservableList<Device> systemData = FXCollections.observableArrayList();
    private static final Logger LOGGER = LogManager.getLogger(RunningTestController.class.getName());

    @FXML
    private TableView<Device> tSysToRun;
    @FXML
    private TableColumn<CobhamSystem, String> articleColumn;
    @FXML
    private TableColumn<CobhamSystem, String> asisColumn;
    @FXML
    private TableColumn<CobhamSystem, String> macColumn;
    @FXML
    private TableColumn<CobhamSystem, String> ipColumn;
    @FXML
    private TableColumn<CobhamSystem, String> statusColumn;
    @FXML
    private TableColumn<CobhamSystem, Double> progressColumn;
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

//		articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
        macColumn.setCellValueFactory(cellData -> cellData.getValue().macProperty());
        ipColumn.setCellValueFactory(cellData -> cellData.getValue().ipProperty());

        statusColumn.setCellValueFactory(new PropertyValueFactory<CobhamSystem, String>("message"));
        progressColumn.setCellValueFactory(new PropertyValueFactory<CobhamSystem, Double>("progress"));

        progressColumn.setCellFactory(ProgressBarTableCell.<CobhamSystem>forTableColumn());
    }

    @FXML
    private void addSystemForTest() {
		DeviceService deviceService = new DeviceService();
		AsisService asisService = new AsisService();

        List<String> currSys = MsgBox.msgScanSystemBarcode();
		Asis asis = asisService.findByName(currSys.get(1));

		try {
			System.out.println(deviceService.findDeviceByAsis(asis.getAsis()));
		} catch (Exception e) {

//			String currMac = asis.getMacAddress().getMac();
//			Device device = new Device(asis, "12345678");
//			writeConsole(currSys.toString());
//			writeConsole(currMac);
//			new DeviceService().saveDevice(device);
		}
//				mainApp.setSystemData(new CobhamSystem(currSys.get(0), currSys.get(1), currMac, this));
        System.out.println(getSystemData());
        tSysToRun.setItems(getSystemData());
//			}
//		} catch (Exception e) {
//			LOGGER.warn("addSystemForTest", e);
//			MsgBox.msgException(e);
//		} 
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
//			for (CobhamSystem cobhamSystem : tSysToRun.getItems()) {
//				System.out.println(cobhamSystem);
//				cobhamSystem.setLocalStopTestFlag(stopTestFlag);
//				DeviceInfoService deviceInfoService = new DeviceInfoService();
//				DeviceInfo devInfo = RmvUtils.getDeviceInfo(cobhamSystem.getDevice());
//				System.out.println(devInfo);
//				cobhamSystem.getDevice().setDeviceInfo(devInfo);
//				deviceInfoService.saveDeviceInfo(devInfo);
//				handleRunTest(cobhamSystem);
//			}
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
