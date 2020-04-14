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
import net.ddns.dimag.cobhamrunning.models.Settings;
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
    private String currUrl;
    private Stage primaryStage;
    private Stage runningTestStage;
    private Stage asisCreatorStage;
    private Stage articlesStage;
    private Stage shippingStage;
    private Stage devicesJournalStage;
    private Stage envJournalStage;
    private BorderPane rootLayout;
    private Settings currentSettings;
    private TestsViewController testsViewController;
    private ArticlesViewController articlesViewController;
    private AsisCreatorController asisCreatorController;
    private RootLayoutController rootLayoutController;
    private static final Logger LOGGER = LogManager.getLogger(MainApp.class.getName());

    private Image favicon = new Image("file:src/resources/images/cobham_C_64x64.png");

    public MainApp() {
    }

    @Override
    public void init() {
        /** test DB connection*/
        LOGGER.info("Start init");
        loadSettings();
        LOGGER.info("Finish init");
    }

    @Override
    public void stop() throws Exception {
        System.out.println("stop");
    }

    @Override
    public void start(Stage primaryStage) {
//        LauncherImpl.launchApplication(MainApp.class, CobhamPreloader.class, args);
        this.primaryStage = primaryStage;
        String VERSION = "0.0.31";
        this.primaryStage.setTitle(String.format("CobhamRunning %s", VERSION));
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
        } catch (IOException e) {
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
            devicesJournalStage = new Stage();
            devicesJournalStage.setTitle("Devices journal");
            devicesJournalStage.getIcons().add(favicon);
            devicesJournalStage.initModality(Modality.WINDOW_MODAL);
            devicesJournalStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            devicesJournalStage.setScene(scene);
            DeviceJournalController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(devicesJournalStage);
            devicesJournalStage.showAndWait();
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
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
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

    public void showMeasureView(Device device) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("MeasureView.fxml"));
            AnchorPane page = loader.load();
            Stage measureStage = new Stage();
            measureStage.setTitle("Measures");
            measureStage.getIcons().add(favicon);
            measureStage.initModality(Modality.WINDOW_MODAL);
            measureStage.initOwner(shippingStage);
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
//            printAsisStage.setResizable(false);
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
            AnchorPane page = loader.load();
            Stage printCustomLabelStage = new Stage();
            printCustomLabelStage.setTitle("Print custom label");
            printCustomLabelStage.getIcons().add(favicon);
            printCustomLabelStage.initModality(Modality.WINDOW_MODAL);
            printCustomLabelStage.initOwner(primaryStage);
            printCustomLabelStage.setResizable(false);
            Scene scene = new Scene(page);
            printCustomLabelStage.setScene(scene);
            PrintCustomLabelViewController printCustomLabelViewController = loader.getController();
            printCustomLabelViewController.setMainApp(this);
//            printCustomLabelViewController.setDialogStage(printCustomLabelStage);
            printCustomLabelStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error(e.toString());
            MsgBox.msgException(e);
        }
    }

    public void showLabelTemplatesView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("LabelTemplatesView.fxml"));
            AnchorPane page = loader.load();
            Stage labelTemplatesStage = new Stage();
            labelTemplatesStage.setTitle("Label templates");
            labelTemplatesStage.getIcons().add(favicon);
            labelTemplatesStage.initModality(Modality.WINDOW_MODAL);
            labelTemplatesStage.initOwner(asisCreatorStage);
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
//            dialogStage.initOwner(primaryStage);
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
//            	systemData.forEach(item -> items.add(item.getDevice().getAsis().getAsis()));
                for (Thread t : threads) {
                    if (items.contains(t.getName())) {
                        if (t.isAlive()) {
                            t.stop();
                        }
                        System.out.println(String.format("Thread %d _. Status: %s", t.getName(), t.getState()));
                    }
                }
            });
            runningTestStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean showSettingsDialog() {
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

            return settingsViewController.isSaveClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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

            // Set the person into the controller
            CalibrationViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(calibrationStage);

            calibrationStage.showAndWait();

            return false;

        } catch (IOException e) {
            // Exception gets thrown if the fxml file could not be loaded
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
                if (!showSettingsDialog()) {
                    return false;
                }
            }

            JAXBContext context = JAXBContext.newInstance(Settings.class);
            Unmarshaller um = context.createUnmarshaller();
            setCurrentSettings((Settings) um.unmarshal(file));
            currUrl = HibernateSessionFactoryUtil.getConnectionInfo().get("DataBaseUrl");
            return true;
        } catch (Exception e) {
            MsgBox.msgException(e);
        }

        return false;
    }

    public void setDbUrl() {
        try {
            HibernateSessionFactoryUtil.restartSessionFactory();
            rootLayoutController.setDbNameLbl(HibernateSessionFactoryUtil.getConnectionInfo().get("DataBaseUrl"));
        } catch (CobhamRunningException e) {
            MsgBox.msgWarning("Warning", e.getMessage());
        }

    }

    public void setCurrentSettings(Settings currentSettings) {
        this.currentSettings = currentSettings;
    }

    public Settings getCurrentSettings() {
        return currentSettings;
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(MainApp.class, CobhamPreloader.class, args);
    }

    public TestsViewController getController() {
        return testsViewController;
    }
}
