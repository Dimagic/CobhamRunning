package net.ddns.dimag.cobhamrunning.view;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

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
import net.ddns.dimag.cobhamrunning.models.Settings;
import net.ddns.dimag.cobhamrunning.utils.HibernateUtils;
import net.ddns.dimag.cobhamrunning.utils.JComClient;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.NetworkUtils;
import net.ddns.dimag.cobhamrunning.utils.PyVisaClient;

public class SettingsViewController implements MsgBox {
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
	private TextField path_db;
	@FXML
	private TextField name_db;
	@FXML
	private TextField user_db;
	
	@FXML
	private PasswordField pass_db;
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
    		dialogStage.close();
    	}	
    }
    
    @FXML
    private void testDBconn(){
		try {
			HibernateUtils.getSession();
			MsgBox.msgInfo("DB connection", "Connection established");
		} catch (Throwable e) {
			while (e.getCause() != null){
				String exName;
				e = (Exception) e.getCause();
				exName = e.getCause().getClass().getName();
				if (exName.endsWith("org.postgresql.util.PSQLException")){
					MsgBox.msgError("DB connection", "Connection error", e.getCause().getMessage());
				}
			}		
			MsgBox.msgException(e);
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
    		path_db.setText(currSet.getPath_db());
    		name_db.setText(currSet.getName_db());
    		user_db.setText(currSet.getUser_db());
    		pass_db.setText(currSet.getPass_db());
    		prnt_combo.setValue(currSet.getPrnt_combo());
    	} catch (NullPointerException e) {
			return;
		}
    	
    }
	
	public boolean isSaveClicked() {
		return false;
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
		
	public boolean saveSettings() {
//		SettingsService settingsService = new SettingsService();
    	
		HashMap<String, String> sett = new HashMap<>();    	
    	Field[] fields = this.getClass().getDeclaredFields();
		for(Field field: fields) {
			try {
				if (field.getType() == javafx.scene.control.TextField.class) {
					System.out.println(field.getName() + " >>> " + field.toString());
					TextField textField = (TextField) field.get(this);
					sett.put(field.getName(), textField.getText());
//					Settings set = new Settings(field.getName(), textField.getText());
//			    	settingsService.updateSetting(set);
				}
				else if (field.getType() == javafx.scene.control.PasswordField.class) {
					System.out.println(field.getName() + " >>> " + field.toString());
					PasswordField passwordField = (PasswordField) field.get(this);
					System.out.println(md5Apache(passwordField.getText()));
					sett.put(field.getName(), passwordField.getText());
				}
				else if (field.getType() == javafx.scene.control.ComboBox.class) {
					System.out.println(field.getName() + " >>> " + field.toString());
					ComboBox<String> comboField = (ComboBox<String>) field.get(this);
					sett.put(field.getName(), comboField.getValue());
//					Settings set = new Settings(field.getName(), comboField.getValue());
//			    	settingsService.updateSetting(set);
				}

				/**
				 * Write settings to the currentSettings in MainApp.
				 */
				mainApp.setCurrentSettings(new Settings(sett));
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
	        dialogStage.close();
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
    
    public void setDialogStage(Stage dialogStage) {
    	this.dialogStage = dialogStage;
    }
}
