package net.ddns.dimag.cobhamrunning.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.*;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.LabelTemplateService;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;

public class ArticleEditViewController implements MsgBox{
	private Stage dialogStage;
	private MainApp mainApp; 
	private ArticleHeaders articleHeaders;
	private ObservableList<LabelTemplate> tableList = FXCollections.observableArrayList();
	private List<LabelTemplate> templList;
	private boolean saveClicked = false;
	
	@FXML
	private TextField articleField;
	@FXML
	private TextField revisionField;
	@FXML
	private TextField shortDescriptField;
	@FXML
	private TextField longDescriptField;
	@FXML
	private CheckBox isNeedMac;
	@FXML
	private ChoiceBox<String> labelTemplBox;
	@FXML
	private Button addTemplateBtn;
	@FXML
	private TableView<LabelTemplate> tTemplates;
	@FXML
	private TableColumn<LabelTemplate, String> nameColumn;
	
	public ArticleEditViewController(){
		super();
	}
	
	public ArticleEditViewController(ArticleHeaders articleHeaders){
		super();	
	}
	
	@FXML
	private void initialize() {
		initItemMenu();
		addTemplateBtn.setDisable(true);
		revisionField.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
	        	try {
					if (revisionField.getText().length() > 1) {
						String s = revisionField.getText().substring(0, 1);
						revisionField.setText(s);
					}
				} catch (NullPointerException e) {
					isCopy();
				}

	        }
	    });
		articleField.textProperty().addListener(new ChangeListener<String>() {
	        @Override
	        public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
	        	try {
					if (articleField.getText().length() > 7) {
						String s = articleField.getText().substring(0, 7);
						articleField.setText(s);
					}
				} catch (NullPointerException e){
	        		e.getMessage();
				}

	        }
	    });
		labelTemplBox.getSelectionModel().selectedItemProperty().addListener((o, oldVal, newVal) -> {
			if (newVal == null) {
				addTemplateBtn.setDisable(true);
			} else {
				addTemplateBtn.setDisable(false);
			}
		});

		LabelTemplateService labelTemplateService = new LabelTemplateService();
		templList = new ArrayList<LabelTemplate>();
		try {
			labelTemplateService.findAllLabelTemplate().forEach(item->templList.add(item));
		} catch (CobhamRunningException e) {
			e.printStackTrace();
		}

		List<String> templNameList = new ArrayList<String>();
		templList.forEach(item->templNameList.add(item.getName()));

		labelTemplBox.setItems(FXCollections.observableArrayList(templNameList));
		
		nameColumn.prefWidthProperty().bind(tTemplates.widthProperty().divide(1));
		
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

	}
	
	@FXML
	private boolean handleSaveBtn(){
		if (articleField.getText().isEmpty() || shortDescriptField.getText().isEmpty() 
				|| longDescriptField.getText().isEmpty() || revisionField.getText().isEmpty()){
			MsgBox.msgWarning("Add new article", "Not all fields are field");
			return false;
		}
		ArticleHeaders articleHeaders = new ArticleHeaders();
		articleHeaders.setName(articleField.getText().toUpperCase());
		articleHeaders.setRevision(revisionField.getText().toUpperCase());
		articleHeaders.setShortDescript(shortDescriptField.getText());
		articleHeaders.setLongDescript(longDescriptField.getText());
		articleHeaders.setNeedmac(isNeedMac.isSelected());
		articleHeaders.setTemplates(new HashSet<>( tTemplates.getItems()));
		ArticleHeadersService articleHeadersService = new ArticleHeadersService();
		try {
			if (this.articleHeaders == null){
				articleHeadersService.saveArticle(articleHeaders);
			} else {
				try {
					articleHeaders.setId(this.articleHeaders.getId());
					articleHeadersService.updateArticle(articleHeaders);
				} catch (TransientObjectException e) {
					articleHeadersService.saveArticle(articleHeaders);
				}

			}
			saveClicked = true;
			dialogStage.close();
			return saveClicked;
		} catch (ConstraintViolationException e) {
			// ToDo: catch PSQLException
			MsgBox.msgError("Save new article", String.format("Article %s already exist", articleHeaders.getArticle()));
		} catch (Exception e) {
			MsgBox.msgException(e);
			e.printStackTrace();
		}
		return false;
	}
	
	@FXML
	private void addTemplate(){
		for (LabelTemplate item: templList){		
			if (item.getName().equals(labelTemplBox.getSelectionModel().getSelectedItem().toString())){
				for (LabelTemplate labelTemplate: tableList){
					if (labelTemplate.getName() == labelTemplBox.getSelectionModel().getSelectedItem()){
						return;
					}
				}
				this.tableList.add(item);
			}
		}
		fillTable();
	}
	
	private void fillTable() {
		tTemplates.setItems(tableList);
		tTemplates.refresh();
	}
	
	public void setArticleHeaders(ArticleHeaders articleHeaders){
		if (articleHeaders != null){
			this.articleHeaders = articleHeaders;
			this.tableList = FXCollections.observableArrayList(articleHeaders.getTemplates());
			articleField.setText(articleHeaders.getArticle());
			revisionField.setText(articleHeaders.getRevision());
			shortDescriptField.setText(articleHeaders.getShortDescript());
			longDescriptField.setText(articleHeaders.getLongDescript());
			isNeedMac.setSelected(articleHeaders.isNeedMacProperty().get());
			articleField.setEditable(false);
			if (articleHeaders.getRevision() == null) {
				isCopy();
			} else {
				revisionField.setEditable(false);
			}
			tTemplates.setItems(tableList);
		}	
	
	}
		
	private void initItemMenu(){
		ContextMenu menu = new ContextMenu();
		MenuItem mDel = new MenuItem("Delete");
		mDel.setOnAction((ActionEvent event) -> {
		    tableList.remove(tTemplates.getSelectionModel().getSelectedItem());
		    fillTable();
		});
		menu.getItems().add(mDel);
		tTemplates.setContextMenu(menu);
	}
	
	public boolean isSaveClicked() {
        return saveClicked;
    }

    private void isCopy(){
		articleField.setEditable(false);
		revisionField.setEditable(true);
		shortDescriptField.setEditable(false);
		longDescriptField.setEditable(false);
		isNeedMac.setDisable(true);
		labelTemplBox.setDisable(true);
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		tTemplates.setItems(tableList);
	}
	
	
}
