package net.ddns.dimag.cobhamrunning.view;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.services.AsisPrintJobService;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.ZebraPrint;

public class PrintAsisViewController implements MsgBox {
	private Stage dialogStage;
	private MainApp mainApp;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD hh:mm:ss");
	private ObservableList<AsisPrintJob> tableJob = FXCollections.observableArrayList();
	private AsisPrintJob selectedJob;
	private AsisPrintJobService asisPrintJobService;

	@FXML
	private TableView<AsisPrintJob> tPrintJob;
	@FXML
	private TableColumn<AsisPrintJob, String> articleColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> descriptionColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> startColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> stopColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> countColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> dateCreateColumn;
	@FXML
	private TableColumn<AsisPrintJob, String> datePrintColumn;
	@FXML
	private TableColumn<AsisPrintJob, Boolean> macColumn;

	@FXML
	private TextField singleField;
	@FXML
	private TextField rangeStartField;
	@FXML
	private TextField rangeStopField;

	@FXML
	private CheckBox showAll;
	@FXML
	private Button printSelectedBtn;

	public PrintAsisViewController() {
		super();
	}

	@FXML
	private void initialize() {
		printSelectedBtn.setDisable(true);
		articleColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		descriptionColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		startColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		stopColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		countColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		dateCreateColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		datePrintColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));
		macColumn.prefWidthProperty().bind(tPrintJob.widthProperty().divide(8));

		articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
		descriptionColumn.setCellValueFactory(cellData -> cellData.getValue().desctiptionProperty());
		startColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
		stopColumn.setCellValueFactory(cellData -> cellData.getValue().stopProperty());
		countColumn.setCellValueFactory(cellData -> cellData.getValue().countProperty());
		dateCreateColumn.setCellValueFactory(cellData -> cellData.getValue().dateCreateProperty());
		datePrintColumn.setCellValueFactory(cellData -> cellData.getValue().datePrintProperty());
		macColumn.setCellValueFactory(cellData -> cellData.getValue().getArticle().isNeedMacProperty());
		
		showAll.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				fillTable();
			}
		});
		tPrintJob.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null) {
				printSelectedBtn.setDisable(true);
			} else {
				printSelectedBtn.setDisable(false);
			}
			selectedJob = newValue;
		});
		singleField.textProperty().addListener((observable, oldValue, newValue) -> {
			singleField.setText(stringLimit(newValue));
			rangeStartField.setText("");
			rangeStopField.setText("");
		});
		rangeStartField.textProperty().addListener((observable, oldValue, newValue) -> {
			rangeStartField.setText(stringLimit(newValue));
			startLessStop();
			singleField.setText("");
		});
		rangeStopField.textProperty().addListener((observable, oldValue, newValue) -> {
			rangeStopField.setText(stringLimit(newValue));
			startLessStop();
			singleField.setText("");
		});
	}

	@FXML
	private void printSelected() {
		try {
			ZebraPrint zebraPrint = new ZebraPrint(selectedJob, mainApp.getCurrentSettings().getPrnt_combo());
			if (zebraPrint.printLabel()) {
				if (selectedJob.getDatePrint() == null) {
					AsisPrintJobService asisPrintJobService = new AsisPrintJobService();
					selectedJob.setDatePrint(new Date());
					asisPrintJobService.updatePrintJob(selectedJob);
					fillTable();
				}
			}
		} catch (NullPointerException e) {
			MsgBox.msgWarning("Print label", String.format("Printer %s not available in system now.",
					mainApp.getCurrentSettings().getPrnt_combo()));
			return;
		}
	}

	private void fillTable() {
		asisPrintJobService = new AsisPrintJobService();
		if (showAll.isSelected()) {
			tableJob = FXCollections.observableArrayList(asisPrintJobService.findAllPrintJobs());
		} else {
			tableJob = FXCollections.observableArrayList(asisPrintJobService.findAllUnprinted());
		}
		tPrintJob.setItems(tableJob);
		tPrintJob.refresh();
	}

	private String stringLimit(String value) {
		int maxLength = 4;
		if (value.length() > maxLength) {
			return value.substring(0, maxLength).toUpperCase();
		}
		return value.toUpperCase();
	}

	private boolean startLessStop() {
		if (rangeStartField.lengthProperty().get() == 4 && rangeStopField.lengthProperty().get() == 4) {
			if (rangeStartField.getText().compareTo(rangeStopField.getText()) > 0) {
				MsgBox.msgWarning("Incorrect range", "Start ASIS biggest then stop ASIS.");
				return false;
			}
		}
		return true;
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		fillTable();
	}
}
