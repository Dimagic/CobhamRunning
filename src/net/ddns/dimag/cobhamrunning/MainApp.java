package net.ddns.dimag.cobhamrunning;

import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.environment.EnvDevice;
import net.ddns.dimag.cobhamrunning.utils.*;
import net.ddns.dimag.cobhamrunning.view.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainApp extends Application implements MsgBox {
    private final String VERSION = "0.1.3.7";
    private String currUrl;
    private Stage primaryStage;
    private Stage runningTestStage;
    private Stage asisCreatorStage;
    private Stage articlesStage;
    private Stage shippingStage;
    private Stage envJournalStage;
    private BorderPane rootLayout;
    private Settings currentSettings;
    private TestsViewController testsViewController;
    private ArticlesViewController articlesViewController;
    private AsisCreatorController asisCreatorController;
    private RootLayoutController rootLayoutController;
    private DeviceJournalController deviceJournalController;
    private Thread telebotServer;
    private static final Logger LOGGER = LogManager.getLogger(MainApp.class.getName());

    private Image favicon = new Image("file:src/resources/images/cobham_C_64x64.png");

    public MainApp() {

    }

    @Override
    public void init() {
        /** test DB connection*/
        LOGGER.info("Start init");
        LOGGER.info(String.format("Current program version: %s", VERSION));
        loadSettings();
        LOGGER.info("Finish init");
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle(String.format("CobhamRunning %s %s", VERSION, Utils.getComputerName()));
        this.primaryStage.getIcons().add(favicon);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("RootLayout.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            rootLayoutController = loader.getController();
            rootLayoutController.setMainApp(this);
            rootLayoutController.setDbNameLbl(currUrl);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showTestsView();
    }

    public void showTestsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("TestsView.fxml"));
            AnchorPane overviewPage = loader.load();
            rootLayout.setCenter(overviewPage);
            testsViewController = loader.getController();
            testsViewController.setMainApp(this);

            loadSettings();

//            telebotServer = new Thread(new TelebotListenerServer(this));
//            telebotServer.start();

            if (!new File(currentSettings.getUpdate_path()).exists()) {
                MsgBox.msgInfo("Update", String.format("Path: %s not available", currentSettings.getUpdate_path()));
                return;
            } else {
                Thread update = new Thread(new Updater(this, testsViewController));
                update.start();
            }
        } catch (CobhamRunningException e){
            MsgBox.msgWarning("CobhamRunning", e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    public void showShippingView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("ShippingView.fxml"));
            AnchorPane page = loader.load();
            shippingStage = new Stage();
            shippingStage.setTitle("Shipping journal");
            shippingStage.getIcons().add(favicon);
            shippingStage.initModality(Modality.WINDOW_MODAL);
            shippingStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            shippingStage.setScene(scene);
            ShippingViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(shippingStage);
            shippingStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            MsgBox.msgException(e);
        }
    }

    public void showDevicesJournalView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("DeviceJournal.fxml"));
            AnchorPane page = loader.load();
            Stage devicesJournalStage = new Stage();
            devicesJournalStage.setTitle("Devices journal");
            devicesJournalStage.getIcons().add(favicon);
            devicesJournalStage.initModality(Modality.WINDOW_MODAL);
            devicesJournalStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            devicesJournalStage.setScene(scene);
            deviceJournalController = loader.getController();
            deviceJournalController.setMainApp(this);
            deviceJournalController.setDialogStage(devicesJournalStage);
            devicesJournalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            MsgBox.msgException(e);
        }
    }

    public void showRmvJournalView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("RmvJournal.fxml"));
            AnchorPane page = loader.load();
            Stage rmvJournalStage = new Stage();
            rmvJournalStage.setTitle("RMV journal");
            rmvJournalStage.getIcons().add(favicon);
            rmvJournalStage.initModality(Modality.WINDOW_MODAL);
            rmvJournalStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            rmvJournalStage.setScene(scene);
            RmvJournalController rmvJournalController;
            rmvJournalController = loader.getController();
            rmvJournalController.setMainApp(this);
            rmvJournalController.setDialogStage(rmvJournalStage);
            rmvJournalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            MsgBox.msgException(e);
        }
    }

    public void showEnvJournalView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("EnvJournalView.fxml"));
            BorderPane page = loader.load();
            envJournalStage = new Stage();
            envJournalStage.setTitle("Environment journal");
            envJournalStage.getIcons().add(favicon);
            envJournalStage.initModality(Modality.WINDOW_MODAL);
            envJournalStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            envJournalStage.setScene(scene);
            EnvJournalController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(envJournalStage);
            envJournalStage.showAndWait();
        } catch (IOException | CobhamRunningException e) {
            e.printStackTrace();
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    public void showEnvModelJournalView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("EnvModelJournalView.fxml"));
            AnchorPane page = loader.load();
            Stage envModelJournalStage = new Stage();
            envModelJournalStage.setTitle("Model journal");
            envModelJournalStage.getIcons().add(favicon);
            envModelJournalStage.initModality(Modality.WINDOW_MODAL);
            envModelJournalStage.initOwner(envJournalStage);
            Scene scene = new Scene(page);
            envModelJournalStage.setScene(scene);
            EnvModelJournalController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(envModelJournalStage);
            envModelJournalStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e);
            MsgBox.msgException(e);
        }
    }

    public void showArticleCreatorView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("AsisCreatorView.fxml"));
            BorderPane page = loader.load();
            asisCreatorStage = new Stage();
            asisCreatorStage.setTitle("ASIS creator");
            asisCreatorStage.getIcons().add(favicon);
            asisCreatorStage.initModality(Modality.WINDOW_MODAL);
            asisCreatorStage.initOwner(primaryStage);
            asisCreatorStage.setResizable(false);
            Scene scene = new Scene(page);
            asisCreatorStage.setScene(scene);
            asisCreatorController = loader.getController();
            asisCreatorController.setMainApp(this);
            asisCreatorController.setDialogStage(asisCreatorStage);
            asisCreatorStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    public void showArticlesView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("ArticlesView.fxml"));
            AnchorPane page = loader.load();
            articlesStage = new Stage();
            articlesStage.setTitle("Articles");
            articlesStage.getIcons().add(favicon);
            articlesStage.initModality(Modality.WINDOW_MODAL);
            articlesStage.initOwner(asisCreatorStage);
            articlesStage.setResizable(false);
            Scene scene = new Scene(page);
            articlesStage.setScene(scene);
            articlesViewController = loader.getController();
            articlesViewController.setMainApp(this);
            articlesViewController.setDialogStage(articlesStage);
            articlesStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    public void showMeasureView(Device device, Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MeasureView.fxml"));
            AnchorPane page = loader.load();
            Stage measureStage = new Stage();
            measureStage.setTitle("Measures");
            measureStage.getIcons().add(favicon);
            measureStage.initModality(Modality.WINDOW_MODAL);
            measureStage.initOwner(stage);
            Scene scene = new Scene(page);
            measureStage.setScene(scene);
            MeasureViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDevice(device);
            controller.setDialogStage(measureStage);
            measureStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showArticleEditView(ArticleHeaders articleHeaders) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("ArticleEditView.fxml"));
            System.out.println(loader.getLocation());
            AnchorPane page = loader.load();
            Stage articleEditStage = new Stage();
            if (articleHeaders == null) {
                articleEditStage.setTitle("New article");
            } else {
                if (articleHeaders.getArticle() != null && articleHeaders.getRevision() == null) {
                    articleEditStage.setTitle(String.format("New revision for %s", articleHeaders.getArticle()));
                } else {
                    articleEditStage.setTitle(articleHeaders.getArticle());
                }
            }
            articleEditStage.getIcons().add(favicon);
            articleEditStage.initModality(Modality.WINDOW_MODAL);
            articleEditStage.initOwner(articlesStage);
            articleEditStage.setResizable(false);
            Scene scene = new Scene(page);
            articleEditStage.setScene(scene);
            ArticleEditViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(articleEditStage);
            controller.setArticleHeaders(articleHeaders);
            articleEditStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    articlesViewController.fillTable();
                    System.out.println(we);
                }
            });
            articleEditStage.showAndWait();
            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
        return false;
    }

    public boolean showEnvDeviceView(EnvDevice envDevice) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("EnvDeviceView.fxml"));
            AnchorPane page = loader.load();
            Stage envDeviceStage = new Stage();
            if (envDevice == null) {
                envDeviceStage.setTitle("New device");
            } else {
                envDeviceStage.setTitle(String.format("%s %s %s SN: %s",
                        envDevice.getTypeDev(), envDevice.getManuf(),
                        envDevice.getModel(), envDevice.getSn()));
            }
            envDeviceStage.getIcons().add(favicon);
            envDeviceStage.initModality(Modality.WINDOW_MODAL);
            envDeviceStage.initOwner(envJournalStage);
            envDeviceStage.setResizable(false);
            Scene scene = new Scene(page);
            envDeviceStage.setScene(scene);
            EnvDeviceViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(envDeviceStage);
            controller.setEnvDevice(envDevice);
            envDeviceStage.showAndWait();

            return controller.isSaveClicked();
        } catch (IOException | IllegalStateException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
        return false;
    }

    public void showEnvHistoryView(EnvDevice envDevice) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("EnvHistoryView.fxml"));
            AnchorPane page = loader.load();
            Stage envHistoryStage = new Stage();
            envHistoryStage.setTitle("History");
            envHistoryStage.getIcons().add(favicon);
            envHistoryStage.initModality(Modality.WINDOW_MODAL);
            envHistoryStage.initOwner(envJournalStage);
            envHistoryStage.setResizable(false);
            Scene scene = new Scene(page);
            envHistoryStage.setScene(scene);
            EnvHistoryViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(envHistoryStage);
            controller.setEnvDevice(envDevice);
            envHistoryStage.showAndWait();
        } catch (IOException | IllegalStateException | CobhamRunningException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    public void showPrintAsisView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("PrintAsisView.fxml"));
            AnchorPane page = loader.load();
            Stage printAsisStage = new Stage();
            printAsisStage.setTitle("Print ASIS");
            printAsisStage.getIcons().add(favicon);
            printAsisStage.initModality(Modality.WINDOW_MODAL);
            printAsisStage.initOwner(asisCreatorStage);
            Scene scene = new Scene(page);
            printAsisStage.setScene(scene);
            PrintAsisViewController printAsisController = loader.getController();
            printAsisController.setMainApp(this);
            printAsisController.setDialogStage(printAsisStage);
            printAsisStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    asisCreatorController.setStatistic();
                }
            });
            printAsisStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
    }

    public void showPrintCustomLabelView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("PrintCustomLabelView.fxml"));
            BorderPane page = loader.load();
            Stage printCustomLabelStage = new Stage();
            printCustomLabelStage.setTitle("Print custom label");
            printCustomLabelStage.getIcons().add(favicon);
            printCustomLabelStage.initModality(Modality.WINDOW_MODAL);
//            printCustomLabelStage.initOwner(primaryStage);
            printCustomLabelStage.setResizable(false);
            Scene scene = new Scene(page);
            printCustomLabelStage.setScene(scene);
            PrintCustomLabelViewController printCustomLabelViewController = loader.getController();
            printCustomLabelViewController.setMainApp(this);
            printCustomLabelViewController.setDialogStage(printCustomLabelStage);
            printCustomLabelStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            MsgBox.msgException(e);
        }
    }

    public boolean showLabelTemplatesView(Stage parentStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("LabelTemplatesView.fxml"));
            AnchorPane page = loader.load();
            Stage labelTemplatesStage = new Stage();
            labelTemplatesStage.setTitle("Label templates");
            labelTemplatesStage.getIcons().add(favicon);
            labelTemplatesStage.initModality(Modality.WINDOW_MODAL);
            labelTemplatesStage.initOwner(parentStage);
            labelTemplatesStage.setResizable(false);
            Scene scene = new Scene(page);
            labelTemplatesStage.setScene(scene);
            LabelTemplatesViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(labelTemplatesStage);
            labelTemplatesStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.msgException(e);
        }
        return true;
    }

    public void showRunningTestView() {
        try {
            currentSettings.getIp_ssh();
        } catch (NullPointerException e) {
            MsgBox.msgWarning("Settings warning", "The network interface is not specified in the settings");
            return;
        }
        if (!NetworkUtils.isIpInSystem(currentSettings.getIp_ssh())) {
            MsgBox.msgWarning("Network scanner", String.format(
                    "Network interface with ip address %s not found in system. Please check the settings.", currentSettings.getIp_ssh()));
            return;
        }
        if (this.runningTestStage != null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("RunningTest.fxml"));
            AnchorPane page = loader.load();

            runningTestStage = new Stage();
            runningTestStage.setTitle("Running test");
            runningTestStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            runningTestStage.setScene(scene);

            RunningTestController controller = loader.getController();
            controller.setMainApp(this);

            controller.setDialogStage(runningTestStage);

            runningTestStage.setOnCloseRequest(event ->
            {

                System.out.println("Clearing systemData");
                runningTestStage = null;
                controller.stopTestFlag = true;
                Set<Thread> threads = Thread.getAllStackTraces().keySet();
                List<String> items = new ArrayList<String>();
                for (Thread t : threads) {
                    if (items.contains(t.getName())) {
                        if (t.isAlive()) {
                            t.stop();
                        }
                        System.out.println(String.format("Thread %s _. Status: %s", t.getName(), t.getState()));
                    }
                }
            });
            runningTestStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showSettingsDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("SettingsView.fxml"));
            AnchorPane page = loader.load();

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.getIcons().add(favicon);
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            settingsStage.setScene(scene);
            settingsStage.setResizable(false);
            SettingsViewController settingsViewController = loader.getController();
            settingsViewController.setMainApp(this);
            settingsViewController.fillSettings();
            settingsViewController.setDialogStage(settingsStage);
            settingsStage.showAndWait();
            if (settingsViewController.isSaveClicked()) {
                loadSettings();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showCalibrationView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("CalibrationView.fxml"));
            AnchorPane page = loader.load();
            Stage calibrationStage = new Stage();
            calibrationStage.setTitle("Calibration");
            calibrationStage.getIcons().add(favicon);
            calibrationStage.initModality(Modality.WINDOW_MODAL);
            calibrationStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            calibrationStage.setScene(scene);
            CalibrationViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(calibrationStage);
            calibrationStage.showAndWait();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Returns the main stage.
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public boolean loadSettings() {
        try {
            File file = new File("settings.xml");
            if (!file.exists()) {
                MsgBox.msgWarning("Load settings", "Settings file not found.\nWill create a new settings file.\nPlease fill settings form.");
            }
            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller um = context.createUnmarshaller();
            setCurrentSettings((Settings) um.unmarshal(file));
            currUrl = HibernateSessionFactoryUtil.getConnectionInfo().get("DataBaseUrl");
            return true;
        } catch (Exception e) {
            MsgBox.msgWarning("Load settings", e.getMessage());
        }

        return false;
    }

    public void setCurrentSettings(Settings currentSettings) {
        this.currentSettings = currentSettings;
    }

    public Settings getCurrentSettings() {
        return currentSettings;
    }

    public TestsViewController getController() {
        return testsViewController;
    }



    public String getVERSION() {
        return VERSION;
    }

    public void setDbUrl() {
        try {
            HibernateSessionFactoryUtil.restartSessionFactory();
            rootLayoutController.setDbNameLbl(HibernateSessionFactoryUtil.getConnectionInfo().get("DataBaseUrl"));
        } catch (CobhamRunningException e) {
            MsgBox.msgWarning("Warning", e.getMessage());
        }

    }

    public void setDbNameLbl(String lbl){
        System.out.println(lbl);
        rootLayoutController.setDbNameLbl(lbl);
    }

    public DeviceJournalController getDeviceJournalController(){
        return deviceJournalController;
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, CobhamPreloader.class, args);
    }

    /**---------------------------------------------------------------------*/
}
