package net.ddns.dimag.cobhamrunning.models;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.jcraft.jsch.JSchException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.MeasurementsService;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.JSSHClient;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.SystemCommands;
import net.ddns.dimag.cobhamrunning.view.RunningTestController;
import net.ddns.dimag.cobhamrunning.view.ShippingViewController;

public class CobhamSystem extends Task<Void> implements SystemCommands {
	private static final Logger LOGGER = LogManager.getLogger(CobhamSystem.class.getName());
	final BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(1);
	private Device device;
	private Tests test;
	private JSSHClient jssh;

	private RunningTestController controller;
	private StringProperty ip;

	private boolean localStopTestFlag = false;

	private MainApp mainApp;
	
	public CobhamSystem(){};

	public CobhamSystem(String article, String asis, String mac, RunningTestController controller) {
		super();
		ArticleHeadersService articleServ = new ArticleHeadersService();
//		ToDo: rebuild all
//		this.device = new Device(articleServ.findArticleByName(article), asis, mac);
		this.device = null;
		
		DeviceService devServ = new DeviceService();
        List<Device> devList = devServ.findDeviceByAsis(asis);
        if (devList.size() > 0){
//        	ToDo: if size > 1
        	if (this.device.hashCode() == devList.get(0).hashCode()){
        		device = devList.get(0);
			} else {
//				If device found but != this device
			}
        	
        } else {
//        	If device not found
        	devServ.saveDevice(this.device);

        }
        
        
		
		this.controller = controller;
	}

	public CobhamSystem(Device dev){
		super();
		this.device = dev;
	}
	
	public CobhamSystem(String article, String asis, String mac) {
		super();
		ArticleHeadersService articleServ = new ArticleHeadersService();
//		ToDo: rebuild all
//		this.device = new Device(articleServ.findArticleByName(article), asis, mac);
		this.device = null;
		DeviceService devServ = new DeviceService();
        List<Device> devList = devServ.findDeviceByAsis(asis);
        if (devList.size() > 0){
//        	ToDo: if size > 1
        	if (this.device.hashCode() == devList.get(0).hashCode()){
        		device = devList.get(0);
			} else {
//				If device found but != this device
			}
        	
        } else {
//        	If device not found
        	devServ.saveDevice(this.device);
        }

	}

	public void setIp(String ip) {
		this.ip = new SimpleStringProperty(ip);
	}

//	public StringProperty articleProperty() {
//		return new SimpleStringProperty(device.getArticle().getName());
//	}

	public StringProperty asisProperty() {
		return new SimpleStringProperty(device.getAsis().getAsis());
	}

	public StringProperty macProperty() {
		return new SimpleStringProperty(device.getAsis().getAsis());
	}

	public StringProperty ipProperty() {
		return ip;
	}
	
	public StringProperty dateShipProperty() {
		// TODO Auto-generated method stub
		return new SimpleStringProperty("dateShip");
	}

	public Device getDevice() {
		return device;
	}

	public void setLocalStopTestFlag(boolean localStopTestFlag) {
		this.localStopTestFlag = localStopTestFlag;
	}

	@Override
	protected Void call()  {
		try {
			this.updateMessage("Starting...");
			jssh = new JSSHClient(this.ip.getValue(), mainApp.getCurrentSettings().getLogin_ssh(),
					mainApp.getCurrentSettings().getPass_ssh());		
				
			testRunning();
		} catch (ConnectException e) {
			LOGGER.error("Running test Ex1", e); 
			MsgBox.msgException(e);
		} catch (IOException e) {
			LOGGER.error("Running test Ex2", e);
			MsgBox.msgException(e);
		} catch (InterruptedException e) {
			LOGGER.error("Running test Ex3", e);
			MsgBox.msgException(e);
		} catch (JSchException e) {
			this.updateMessage("Auth fail");
			this.updateProgress(1, 1);
		} catch (ParseException e) {
			LOGGER.error("Running test Ex4", e);
			MsgBox.msgException(e);
		} catch (Exception e) {
			LOGGER.error("Running test Ex5", e);
			MsgBox.msgException(e);
		        			
		} 
		return null;
	}

	private void testRunning() throws Exception{
//		Platform.runLater(() -> controller.writeConsole(String.format("%s %s started", this.articleProperty().getValue(), this.asisProperty().getValue())));
////		ToDo: stop test after close window/prorgamm
//		TestsService testServ = new TestsService();
//		test = new Tests();
//		test.setDevice(this.device);
//		testServ.saveTest(test);
//		controller.writeConsole(String.format("%s/%s -> %s", this.articleProperty().getValue(), this.asisProperty().getValue(), jssh.send("axsh get mdl").get("result")));
//		while (true){
//			if (localStopTestFlag){
//				break;
//			}
//			getUptime();
//			
//			MeasurementsService measServ = new MeasurementsService();
//			Measurements meas = new Measurements();
//			meas.setMeasName("measDump");
//			meas.setMeasVal(testMeasureMSDH().toString());
//			meas.setTest(test);
//			measServ.saveMeas(meas);
//
//			TimeUnit.SECONDS.sleep(5);
//		}
	}
	
	private JSONObject testMeasureMSDH() throws Exception {
		try {
			String tmp = jssh.send("measurements dump env --json").get("result");
			return jsonToObject(tmp);	
		} catch (Exception e) {
			throw e;
		}
	}

	private boolean getUptime() throws Exception {
		MeasurementsService measServ = new MeasurementsService();
		int uptime;
		String currentTime;
		int need = 3 * 3600;

		uptime = (int) Double.parseDouble(jssh.send(uptimeCmd).get("result"));
		HashMap<String, String> t = secondsToTime(uptime);
		currentTime = String.format("%s:%s:%s", t.get("H"), t.get("M"), t.get("S"));
		System.out.println(currentTime);
		this.updateMessage(String.format("uptime: %s", currentTime));
		
		Measurements meas = new Measurements();
		meas.setMeasName("uptime");
		meas.setMeasMax(Integer.toString(need));
		meas.setMeasVal(Integer.toString(uptime));
		meas.setTest(test);
		measServ.saveMeas(meas);
		
		if (uptime >= need) {
			return true;
		}
		this.updateProgress((1.0 * uptime) / need, 1);
		return false;
	}

	private HashMap<String, String> secondsToTime(int s) {
		String H, M, S;
		HashMap<String, String> t = new HashMap<String, String>();
		H = Integer.valueOf((s / 60) / 60).toString();
		M = Integer.valueOf((s / 60) % 60).toString();
		S = Integer.valueOf(s % 60).toString();

		H = (H.length() == 1 ? String.format("0%s", H) : H);
		M = (M.length() == 1 ? String.format("0%s", M) : M);
		S = (S.length() == 1 ? String.format("0%s", S) : S);

		t.put("H", H);
		t.put("M", M);
		t.put("S", S);
		return t;
	}

//	@Override
//	public boolean equals(Object o) {
//		if (this == o) {
//			return true;
//		}
//
//		if (o == null || getClass() != o.getClass()) {
//			return false;
//		}
//
//		CobhamSystem currSystem = (CobhamSystem) o;
//		return device.getArticle().equals(currSystem.device.getArticle())
//				&& device.getAsis().equals(currSystem.device.getAsis());
//	}

	public JSONObject jsonToObject(String val) throws ParseException {
		try {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(val);
			System.out.println(json.toJSONString());
			return json;
		} catch (ParseException e) {
			throw e;
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

//	@Override
//	public String toString() {
//		return "CobhamSystem [article=" + device.getArticle().getName() + ", asis=" + device.getAsis() + ", mac="
//				+ device.getMac() + ", ip=" + ip + "]";
//	}

	

}
