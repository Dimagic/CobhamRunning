package net.ddns.dimag.cobhamrunning;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import net.ddns.dimag.cobhamrunning.models.CobhamSystem;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.Settings;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.NetworkUtils;
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
    private Stage primaryStage;
    private Stage runningTestStage;
    private Stage asisCreatorStage;
    private Stage articlesStage;
    private BorderPane rootLayout;
	private Settings currentSettings;
	private TestsViewController testsViewController;
	private ArticlesViewController articlesViewController;
    private AsisCreatorController asisCreatorController;
    private SettingsViewController settingsViewController;
	private static final Logger LOGGER = LogManager.getLogger(MainApp.class.getName());
	private ObservableList<CobhamSystem> systemData = FXCollections.observableArrayList();
	private Image favicon = new Image("file:src/resources/images/cobham_C_64x64.png");
    
    public MainApp(){
    	
    }
       
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        String VERSION = "0.0.9";
        this.primaryStage.setTitle(String.format("CobhamRunning %s", VERSION));
        this.primaryStage.getIcons().add(favicon);
        try {	
        	// Load the root layout from the fxml file
           	FXMLLoader loader = new FXMLLoader(getClass().getResource("view/RootLayout.fxml"));
			rootLayout = loader.load();

			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			
			RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
		
			primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        showTestsView();
        loadSettings();
        HibernateSessionFactoryUtil.getSessionFactory();
	        
    }
    
    public ObservableList<CobhamSystem> getSystemData() {
        return systemData;
    }
    
    public void setSystemData(CobhamSystem tmp){
    	DeviceService devServ = new DeviceService();
    	List<Device> deviceList = new ArrayList<Device>();
    	for (CobhamSystem item: systemData){
    		if (item.equals(tmp)){
//    			MsgBox.msgInfo("Append system to running", String.format("System: %s with ASIS: %s already present in table", item.articleProperty().get(), item.asisProperty().get()));
    			return;
    		}
//    		devServ.saveDevice(item.getDevice());
//    		deviceList = devServ.findDeviceByAsis(item.asisProperty().getValue());
//    		for (Device dev: deviceList){
//    			System.out.println(dev.toString());
//    		}
    	}
	    systemData.add(tmp);
    }
      
    public void showTestsView() {
        try {
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/TestsView.fxml"));
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
            loader.setLocation(MainApp.class.getResource("view/ShippingView.fxml"));
            AnchorPane page = loader.load();
            Stage shippingStage = new Stage();
            shippingStage.setTitle("Shipping journal");
            shippingStage.getIcons().add(favicon);
            shippingStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(page);
            shippingStage.setScene(scene);
            ShippingViewController controller = loader.getController();
            controller.setMainApp(this);
            controller.setDialogStage(shippingStage);     
            shippingStage.showAndWait();
        } catch (IOException e) {
        	e.printStackTrace();
            MsgBox.msgException(e);
        }
    }
    
    public void showArticleCreatorView(){
    	try {
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/AsisCreatorView.fxml"));
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
    
    public void showArticlesView(){
    	try {
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ArticlesView.fxml"));
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
    
    public boolean showArticleEditView(ArticleHeaders articleHeaders){
    	try {
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/ArticleEditView.fxml"));
            AnchorPane page = loader.load();
            Stage articleEditStage = new Stage();
            if (articleHeaders == null){
            	articleEditStage.setTitle("New article");
            } else {
            	articleEditStage.setTitle(articleHeaders.getArticle());
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
        } catch (IOException e) {
        	e.printStackTrace();
            MsgBox.msgException(e);
        }
    	return false;
    }
    
    public void showPrintAsisView(){
    	try {
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PrintAsisView.fxml"));
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
    
    public void showLabelTemplatesView(){
    	try {
    		FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/LabelTemplatesView.fxml"));
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
    	if (this.runningTestStage != null){
    		return;
    	}
    	try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RunningTest.fxml"));
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
            	systemData.forEach(item -> items.add(item.getDevice().getAsis()));
            	for (Thread t : threads) {
            		if (items.contains(t.getName())){
            			if (t.isAlive()){
            				t.stop();
            			}
            			System.out.println(String.format("Thread %d _. Status: %s", t.getName(), t.getState()));
            		}
            		
            	}
                systemData.clear();
            });
            
            runningTestStage.showAndWait();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean showSettingsDialog() {
    	try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/SettingsView.fxml"));
            AnchorPane page = loader.load();

            Stage settingsStage = new Stage();
            settingsStage.setTitle("Settings");
            settingsStage.getIcons().add(favicon);
            settingsStage.initModality(Modality.WINDOW_MODAL);
            settingsStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            settingsStage.setScene(scene);
            settingsStage.setResizable(false);
            settingsViewController = loader.getController();
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
			FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/CalibrationView.fxml"));
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
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	public void loadSettings() {
		File file = new File("./settings.xml");
		if (!file.exists()){
		    MsgBox.msgWarning("Load settings", "Settings file not found.\nWill create a new settings file.\nPlease fill settings form.");
            if (!showSettingsDialog()){
                return;
            }
        }

	    try {    	
	        JAXBContext context = JAXBContext.newInstance(Settings.class);
	        Unmarshaller um = context.createUnmarshaller();
//	        return  (Settings) um.unmarshal(file);
            setCurrentSettings((Settings) um.unmarshal(file));
	    } catch (Exception e) { 
	    	MsgBox.msgException(e);
	    }

	}
	
	public void setCurrentSettings(Settings currentSettings) {
		this.currentSettings = currentSettings;
	}
	
	public Settings getCurrentSettings() {
		return currentSettings;
	}
	
    public static void main(String[] args) {
        launch(args);
    }
    
    public TestsViewController getController(){
    	return testsViewController;
    }
}
