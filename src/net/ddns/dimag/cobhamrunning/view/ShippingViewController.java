package net.ddns.dimag.cobhamrunning.view;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.CobhamSystem;
import net.ddns.dimag.cobhamrunning.models.Device;
import net.ddns.dimag.cobhamrunning.models.ShippingSystem;
import net.ddns.dimag.cobhamrunning.services.DeviceService;
import net.ddns.dimag.cobhamrunning.services.ShippingJournalService;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;

public class ShippingViewController implements MsgBox {
	private static final Logger LOGGER = LogManager.getLogger(ShippingViewController.class.getName());
	private ObservableList<ShippingSystem> journal = FXCollections.observableArrayList();
	private ObservableList<CobhamSystem> shippingSystems = FXCollections.observableArrayList();
	private MainApp mainApp;
	private Stage dialogStage;

	@FXML
	private ChoiceBox<String> tableTypeBox;
	@FXML
	private TableView<CobhamSystem> tSysToShip;
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
	private TableColumn<CobhamSystem, String> dateShipColumn;
	@FXML
	private TableColumn<CobhamSystem, String> articleColumn;
	@FXML
	private TableColumn<CobhamSystem, String> asisColumn;
	@FXML
	private TableColumn<CobhamSystem, String> snColumn;
	@FXML
	private TableColumn<CobhamSystem, String> macColumn;
	@FXML
	private TableColumn<CobhamSystem, String> commonVerColumn;
	@FXML
	private TableColumn<CobhamSystem, String> systemVerColumn;
	@FXML
	private TableColumn<CobhamSystem, String> targetVerColumn;

	public ShippingViewController() {
		super();
	}

	@FXML
	private void initialize() {
//		articleColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(6));
//		asisColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(6));
//		macColumn.prefWidthProperty().bind(tSysToShip.widthProperty().divide(6));
		
		
//		dateShipColumn.setCellValueFactory(cellData -> cellData.getValue().dateShipProperty());
//		articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		asisColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());	
//		snColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
//		commonVerColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
//		systemVerColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
//		targetVerColumn.setCellValueFactory(cellData -> cellData.getValue().asisProperty());
		
		
		tableTypeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
				if (newValue.intValue() == 0) {
					addBtn.setVisible(false);
					delBtn.setVisible(false);
					saveBtn.setVisible(false);
				}
				if (newValue.intValue() == 1) {
					addBtn.setVisible(true);
					delBtn.setVisible(true);
					saveBtn.setVisible(true);
				}
				initColumns(newValue.intValue());
			}
		});
		ObservableList<String> tableTypes = FXCollections.observableArrayList("Journal", "Shipping");
		tableTypeBox.setItems(tableTypes);
		tableTypeBox.getSelectionModel().select(0);
		initDate();
	}

	@FXML
	private void handeleAddBtn() {
		DeviceService devServ = new DeviceService();
		List<String> currSys = MsgBox.msgScanSystemBarcode();
		String article = currSys.get(0);
		String asis = currSys.get(1);
		List<Device> listDev = devServ.findDeviceByAsis(asis);
		if ( listDev.size() == 0){
			String sn = MsgBox.msgInputSN();
			String mac = null;
			setShippingSystems(new CobhamSystem(article, asis, mac));
		} else {
			setShippingSystems(new CobhamSystem(listDev.get(0)));
		}
		
		
		System.out.println(shippingSystems);
		tSysToShip.setItems(shippingSystems);
		// tSysToRun.setItems(mainApp.getSystemData());
	}

//	private void initJournalTable() {
//		List<String> columns = Arrays.asList("Date ship", "Article", "Asis", "SN", "Common ver.", "System ver.",
//				"Target ver.");
//		initColumns();
//	}
//
//	private void initSippingTable() {
//		List<String> columns = Arrays.asList("Article", "Asis", "SN", "Common ver.", "System ver.", "Target ver.");
//		initColumns();
//	}

	private void initDate() {
		dateFrom.setShowWeekNumbers(true);
		dateTo.setShowWeekNumbers(true);
		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		};
		dateFrom.setConverter(converter);
		dateFrom.setPromptText("dd-MM-yyyy");
		dateTo.setConverter(converter);
		dateTo.setPromptText("dd-MM-yyyy");

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
		Date input = cal.getTime();
		LocalDate curDate = input.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate start = curDate.withDayOfMonth(1);
		LocalDate stop = curDate.withDayOfMonth(curDate.lengthOfMonth());
		System.out.println(String.format("%s\n%s ", start, stop));
		dateFrom.setValue(start);
		dateTo.setValue(stop);
	}

	private void initColumns(int type) {
		
//		CobhamSystem cobhamClass = new CobhamSystem();
//		Method[] methods = cobhamClass.getClass().getMethods();
//		if (type == 0){
//			TableColumn[] tableColumns = new TableColumn[columns.size()];
//			for (String key: columns.keySet()){
//				if (Arrays.asList(methods).stream().anyMatch(str -> str.getName().equals(columns.get(key).get(1)))){
//					try {
//						Field col = this.getClass().getDeclaredField(columns.get(key).get(0));
//						col.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//						System.out.println();
//					} catch (NoSuchFieldException | SecurityException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} 
//				}
//			}
//		}
//		CobhamSystem cobhamClass = new CobhamSystem();
	
//		TableColumn[] tableColumns = new TableColumn[columns.size()];
//		Method[] methods = cobhamClass.getClass().getMethods();	
//		for (String key: columns.keySet()){
//			if (Arrays.asList(methods).stream().anyMatch(str -> str.getName().equals(columns.get(key)))){
//				TableColumn col = new TableColumn(key);
//				col.prefWidthProperty().bind(tSysToShip.widthProperty().divide(columns.size()));
//				articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//			}			
//		}
		
		
//		Arrays.asList("Date ship", "Article", "Asis", "SN", "Common ver.", "System ver.", "Target ver.");
		
				
//		tSysToShip.getColumns().clear();
//		if (type == 0){
//			dateShipColumn.setCellValueFactory(cellData -> cellData.getValue().dateShipProperty());
//		}
//		articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		asisColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		snColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		macColumn.setCellValueFactory(cellData -> cellData.getValue().macProperty());
//		commonVerColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		systemVerColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
//		targetVerColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
		
	
//		TableColumn[] tableColumns = new TableColumn[columns.size()];
//		int columnIndex = 0;
//		for (int i = 0; i < columns.size(); i++) {
//			final int j = i;
//			TableColumn col = new TableColumn(columns.get(i));
//			col.prefWidthProperty().bind(tSysToShip.widthProperty().divide(columns.size()));
//			col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
//				public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
//					return new SimpleStringProperty(param.getValue().get(j).toString());
//				}
//			});
//			tSysToShip.getColumns().addAll(col);
//		}
	}

	private void setShippingSystems(CobhamSystem tmp) {
		DeviceService devServ = new DeviceService();
		List<Device> deviceList = new ArrayList<Device>();
		for (CobhamSystem item : shippingSystems) {
			if (item.equals(tmp)) {
//				MsgBox.msgInfo("Append system to running",
//						String.format("System: %s with ASIS: %s already present in table", item.articleProperty().get(),
//								item.asisProperty().get()));
				return;
			}
			// devServ.saveDevice(item.getDevice());
			// deviceList =
			// devServ.findDeviceByAsis(item.asisProperty().getValue());
			// for (Device dev: deviceList){
			// System.out.println(dev.toString());
			// }
		}
		shippingSystems.add(tmp);
	}
	
	@FXML
	private void handleSaveBtn(){
		ShippingJournalService shipServ = new ShippingJournalService();
		for (CobhamSystem system: shippingSystems){
			ShippingSystem shipSys = new ShippingSystem(system.getDevice());
			shipServ.saveShippingJournal(shipSys);
		}
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
