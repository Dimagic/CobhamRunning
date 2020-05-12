package net.ddns.dimag.cobhamrunning.view;

import java.io.File;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import javafx.stage.DirectoryChooser;
import net.ddns.dimag.cobhamrunning.utils.*;
import org.apache.commons.codec.digest.DigestUtils;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.utils.Settings;

public class SettingsViewController implements MsgBox {
	boolean saveClicked = false;
	@FXML
	private TitledPane telnet_pane;
	@FXML
	private TitledPane ssh_pane;
	@FXML
	private TitledPane com_pane;
	@FXML
	private TitledPane db_pane;
	@FXML
	private TitledPane prnt_pane;
	@FXML
	private TitledPane rmv_pane;
	
	@FXML
	private Button cancel_btn;
	@FXML 
	private Button save_btn;
	@FXML 
	private Button newAtr_btn;
	
	@FXML
	private ComboBox<String> com_combo;
	@FXML
	private ComboBox<String> baud_combo;
	@FXML
	private ComboBox<String> ip_telnet_combo;
	@FXML
	private ComboBox<String> ip_ssh_combo;
	@FXML
	private ComboBox<String> prnt_combo;
	
	@FXML
	private TextField login_telnet;
	@FXML
	private TextField login_ssh;
	@FXML
	private TextField addr_db;
	@FXML
	private TextField port_db;
	@FXML
	private TextField name_db;
	@FXML
	private TextField user_db;
	@FXML
	private TextField addr_rmv;
	@FXML
	private TextField name_rmv;
	@FXML
	private TextField user_rmv;
	@FXML
	private TextField update_path;
	
	@FXML
	private PasswordField pass_db;
	@FXML
	private PasswordField pass_rmv;
	@FXML
	private PasswordField pass_telnet;
	@FXML
	private PasswordField pass_ssh;
	
	private final String[] baudList = {"110", "300", "600", "1200", 
								 "2400", "4800", "9600", "14400", 
								 "19200", "38400", "57600", 
								 "115200", "128000", "256000"};
	private MainApp mainApp;
	private Stage dialogStage;
	private PyVisaClient pvc;
	private Settings currSettings;
	
	public SettingsViewController(){

	}
	
	@FXML
    private void initialize(){
		pvc = new PyVisaClient("localhost", 10000);
		telnet_pane.setCollapsible(false);
		ssh_pane.setCollapsible(false);
		com_pane.setCollapsible(false);
		db_pane.setCollapsible(false);
		prnt_pane.setCollapsible(false);
		rmv_pane.setCollapsible(false);
		fillComCombo();
		fillBaudCombo();
		fillEthCombo();
		fillPrntCombo();
	}

    @FXML
    private void cancelBtnClick(){
    	dialogStage.close();
    }
    
    @FXML
    private void saveBtnClick(){
    	if (saveSettings()){
			mainApp.setDbUrl();
    		mainApp.loadSettings();
    	}
    }
    
    @FXML
    private void testDBconn(){
		try {
			saveSettings();
			mainApp.loadSettings();
			HibernateUtils.getSession();
		} catch (Exception e) {
//			while (e.getCause() != null){
//				String exName;
//				e = (Exception) e.getCause();
//				exName = e.getCause().getClass().getName();
//				if (exName.endsWith("org.postgresql.util.PSQLException")){
//					MsgBox.msgError("DB connection", "Connection error", e.getCause().getMessage());
//				}
//			}
			MsgBox.msgError("DB connection fail", e.getMessage());
			return;
		}
		MsgBox.msgInfo("DB connection", "Connection established");
    }

    @FXML
	private void testRmvConn(){
		RmvUtils rmvUtils = new RmvUtils(addr_rmv.getText(), name_rmv.getText(), user_rmv.getText(), pass_rmv.getText());
		try {
			Connection conn = rmvUtils.getConnection();
			conn.close();
			MsgBox.msgInfo("RMV connection", "Connection established");
		} catch (SQLException | ClassNotFoundException e) {
			MsgBox.msgError("RMV connection", e.getLocalizedMessage());
		}
	}
          
    public void fillSettings() {
    	try {
    		Settings currSet = mainApp.getCurrentSettings();
    		com_combo.setValue(currSet.getCom_combo());
    		baud_combo.setValue(currSet.getBaud_combo());
    		ip_telnet_combo.setValue(currSet.getIp_telnet());
    		ip_ssh_combo.setValue(currSet.getIp_ssh());
    		login_telnet.setText(currSet.getLogin_telnet());
    		login_ssh.setText(currSet.getLogin_ssh());
    		pass_telnet.setText(currSet.getPass_telnet());
    		pass_ssh.setText(currSet.getPass_ssh());
    		addr_db.setText(currSet.getAddr_db());
    		port_db.setText(currSet.getPort_db());
    		name_db.setText(currSet.getName_db());
    		user_db.setText(currSet.getUser_db());
    		pass_db.setText(currSet.getPass_db());
    		prnt_combo.setValue(currSet.getPrnt_combo());
    		addr_rmv.setText(currSet.getAddr_rmv());
    		name_rmv.setText(currSet.getName_rmv());
    		user_rmv.setText(currSet.getUser_rmv());
    		pass_rmv.setText(currSet.getPass_rmv());
    		update_path.setText(currSet.getUpdate_path());
    	} catch (NullPointerException e) {
			return;
		}
    }

    @FXML
	private void selectDir(){
		DirectoryChooser dir_chooser = new DirectoryChooser();
		File file = dir_chooser.showDialog(dialogStage);
		if (file != null) {
			update_path.setText(file.getAbsolutePath());
		}
	}
	
	public boolean isSaveClicked() {
		return saveClicked;
	}
	
	private void fillBaudCombo(){
		baud_combo.setItems(FXCollections.observableArrayList(baudList));
	}
	
	private void fillComCombo(){
		JComClient comClient = new JComClient();
		if (comClient != null){
			String[] portList = comClient.getComPortList();
			com_combo.setItems(FXCollections.observableArrayList(portList));
		}
			
	}
	
	private void fillEthCombo(){
		ArrayList<String> ipList = new NetworkUtils().getInterfaceIp();
		ip_telnet_combo.setItems(FXCollections.observableArrayList(ipList));
		ip_ssh_combo.setItems(FXCollections.observableArrayList(ipList));    
	}
	
	private void fillPrntCombo(){
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		List<String> prntList = new ArrayList<>();
		for (PrintService printer : printServices){
			prntList.add(printer.getName());
		}
		prnt_combo.setItems(FXCollections.observableArrayList(prntList));
	}

	public Settings getCurrSettings() {
		return currSettings;
	}

	public void setCurrSettings(Settings currSettings) {
		this.currSettings = currSettings;
	}

	public boolean saveSettings() {
//		SettingsService settingsService = new SettingsService();
    	
		HashMap<String, String> sett = new HashMap<>();    	
    	Field[] fields = this.getClass().getDeclaredFields();
		for(Field field: fields) {
			try {
				if (field.getType() == javafx.scene.control.TextField.class) {
					TextField textField = (TextField) field.get(this);
					sett.put(field.getName(), textField.getText());
				}
				else if (field.getType() == javafx.scene.control.PasswordField.class) {
					System.out.println(field.getName() + " >>> " + field.toString());
					PasswordField passwordField = (PasswordField) field.get(this);
					sett.put(field.getName(), passwordField.getText());
				}
				else if (field.getType() == javafx.scene.control.ComboBox.class) {
					System.out.println(field.getName() + " >>> " + field.toString());
					ComboBox<String> comboField = (ComboBox<String>) field.get(this);
					sett.put(field.getName(), comboField.getValue());
				}

				/**
				 * Write settings to the currentSettings in MainApp.
				 */
//				mainApp.setCurrentSettings(new Settings(sett));
				setCurrSettings(new Settings(sett));
				saveClicked = true;
			} catch (NullPointerException e) {
				System.out.printf("Field %s not found\n", field.getName());
				continue;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				MsgBox.msgException(e);
			}
		}

    	
		File file = new File("./settings.xml");  	
	    try {
	    	System.out.println(sett);
	        JAXBContext context = JAXBContext
	                .newInstance(Settings.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	        Settings wrapper = new Settings(sett);
	        System.out.println(wrapper);
	        m.marshal(wrapper, file);
//	        dialogStage.close();
	        MsgBox.msgInfo("Save settings", "Save settings successfully complete.");
	        return true;
	    } catch (Exception e) { 
	    	MsgBox.msgException(e);
	        return false;
	    }
	}

	private String md5Apache(String st) {
	    String md5Hex = DigestUtils.md5Hex(st);
	    return md5Hex;
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        pvc.setController(mainApp.getController());
    }

	public Settings getSettings(){
		return mainApp.getCurrentSettings();
	}


	public void setDialogStage(Stage dialogStage) {
    	this.dialogStage = dialogStage;
    }
}
