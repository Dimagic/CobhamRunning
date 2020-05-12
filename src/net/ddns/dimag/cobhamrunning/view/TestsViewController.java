package net.ddns.dimag.cobhamrunning.view;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.utils.JComClient;
import net.ddns.dimag.cobhamrunning.utils.JTelnetClient;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.ZebraPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestsViewController implements MsgBox {
	private static final Logger LOGGER = LogManager.getLogger(TestsViewController.class.getName());

	private MainApp mainApp;

	@FXML
	private TextArea console;
		
	public TestsViewController(){

	}

	@FXML
    private void initialize() {
		console.setEditable(false);
    }

	@FXML
	private void handleNewTest(){

//		try {
//			PyVisaClient pvc = new PyVisaClient("localhost", 10000);
//			System.out.println(pvc.getInstrumentAddressList());
//			for(String addr: pvc.getInstrumentAddressList()){
//				writeConsole(String.format("%s >> %s", addr, pvc.getInstumentName(addr)));
//			}
//
//			Generator gen = new Generator("GPIB0::19::INSTR", "Hewlett-Packard, ESG-D4000B, GB40051087, B.03.60");
//			gen.setVisa(pvc);
//			for(int i = 850; i < 2000; i+=5){
//				gen.setFreq((float) i);
////				writeConsole(Float.toString(gen.getAmpl()));
//
//			}
//		} catch (Exception e) {
//			MsgBox.msgException(e);
//		}

//    	pvc.sendCmd(cmd.getText());
	}

	@FXML
    private void handleComPort() {
		try {
			Service<Void> service = new Service<Void>() {
	            @Override
	            protected Task<Void> createTask() {
	                return new Task<Void>() {
	                    @Override
	                    protected Void call(){
	                    	JComClient comPort = new JComClient();
	                		comPort.setMainApp(mainApp);
	                		String tx_val = "txtxtxt";
	                		System.out.println(tx_val);
	                		try {
                                System.out.println(comPort.write(tx_val));
							} catch (Exception e) {
                                System.out.println(e.getLocalizedMessage());
							}
							return null;
	                    }
	                };
	            }
	        };
	        service.start();
		}catch (Exception e) {
			MsgBox.msgException(e);
		}
	}
	
	@FXML
    private void handleSettings() {
        boolean saveClicked = mainApp.showSettingsDialog();
    }
	
	@FXML
    private void handleRunningTest() {
		mainApp.showRunningTestView();
	}
	
	@FXML
    private void handleShipping() {
		mainApp.showShippingView();
	}
	
	@FXML
	private void handleAsisCreator(){
		mainApp.showArticleCreatorView();
	}

	@FXML
	private void handleDevicesJournal(){
		mainApp.showDevicesJournalView();
	}

	@FXML
	private void handleEnvJournal(){
		mainApp.showEnvJournalView();
	}

	@FXML
	private void handlePrintCustomLabel(){
		mainApp.showPrintCustomLabelView();
	}
		
	@FXML 
	private void handleAsisBtn(){
	
//		AsisGenerator aGen = new AsisGenerator();
//		System.out.println(aGen.getNewAsisRange(5));
//		getPrinterByName("ZDesigner");
////		PrintService printService=PrintServiceLookup
////				   .lookupDefaultPrintService();
//		stringToBarcode();
//		PrintService printService = getPrinterByName("ZDesigner");
//		if (printService != null){
//			for (String asis: aGen.getNewAsisRange(2))
//			aGen.printLabel(printService, asis);
//		} else {
//			MsgBox.msgWarning("Print label", "Printer not found");
//		}
		
	
	}
	
	@FXML
    private void handleHtmlParser() {
		System.out.println("zebra");
		new ZebraPrint();
		
//        HtmlParser parser = new HtmlParser();
//        parser.setMainApp(mainApp);
//        System.out.println(parser.login());
    }
	

	
	@FXML
    private void handleCalibration() {
		Boolean calibration = mainApp.showCalibrationView();	
    }
	
	@FXML
    private void handleTelnet() {
		JComClient comClient = new JComClient();
		comClient.setMainApp(mainApp);
		String[] portList = comClient.getComPortList();
		for (int i = 0; i < portList.length; i++){
		    System.out.println(portList[i]);

		}
		try {
			Service<Void> service = new Service<Void>() {
	            @Override
	            protected Task<Void> createTask() {
	                return new Task<Void>() {
	                    @Override
	                    protected Void call() throws Exception {
	                    	JTelnetClient telnet = new JTelnetClient(getController(), "192.168.1.253", "root", "dekoroot");
	                    	telnet.sendCommand("/usr/dekolink/bin/ccd fullstop");
	                    	telnet.sendCommand("telnet.sendCommand(\"/usr/dekolink/bin/ccd fullstop\");");
	                    	telnet.disconnect();
	                    	return null;
	                    }
	                };
	            }
	        };
	        service.start();
		}catch (Exception e) {
			MsgBox.msgException(e);
		}	
    }

	public void writeConsole(String val) {
		console.appendText(val + "\n");
		console.selectPositionCaret(console.getLength());
	}

	public TestsViewController getController() {
		return this;
	}
	
	public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        
    }
}
