package net.ddns.dimag.cobhamrunning.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.AsisService;

public class ArticlesViewController {
	private static final Logger LOGGER = LogManager.getLogger(ArticlesViewController.class.getName());
	private Stage dialogStage;
	private MainApp mainApp;
	private ArticleHeadersService articleService;
	private ObservableList<ArticleHeaders> articleList;

	@FXML
	private TableView<ArticleHeaders> tArticle;
	@FXML
	private TableColumn<ArticleHeaders, String> articleColumn;
	@FXML
	private TableColumn<ArticleHeaders, String> shotrDescriptColumn;
	@FXML
	private TableColumn<ArticleHeaders, String> longDescriptColumn;
	@FXML
	private TableColumn<ArticleHeaders, String> revisionColumn;
	@FXML
	private TableColumn<ArticleHeaders, Boolean> isNeedMacColumn;
	
	@FXML
	private TextField searchField;
	
	public ArticlesViewController(){
		super();	
	}
	
	@FXML
	private void initialize() {
		tArticle.setRowFactory( tv -> {
		    TableRow<ArticleHeaders> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
		            if (mainApp.showArticleEditView(row.getItem())){
		            	fillTable();
		            };
		        }
		    });
		    return row ;
		});
		
		searchField.textProperty().addListener((observable, oldValue, newValue) -> fillTable());
		
		articleColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(5));
		revisionColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(5));
		shotrDescriptColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(5));
		longDescriptColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(5));
		isNeedMacColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(5));	
		
		articleColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		revisionColumn.setCellValueFactory(cellData -> cellData.getValue().revisionProperty());
		shotrDescriptColumn.setCellValueFactory(cellData -> cellData.getValue().shortDescriptProperty());
		longDescriptColumn.setCellValueFactory(cellData -> cellData.getValue().longDescriptProperty());
		isNeedMacColumn.setCellValueFactory(cellData -> cellData.getValue().isNeedMacProperty());
	}
	
	@FXML
	private void handleAddBtn() {
		if (mainApp.showArticleEditView(null)){
			fillTable();
		}
	}
	
	public void fillTable(){
		tArticle.getSelectionModel().clearSelection();
		tArticle.setItems(getArticleList());
	}
	
	private ObservableList<ArticleHeaders> getArticleList(){
		String val = searchField.getText().toUpperCase();
		ObservableList<ArticleHeaders> headersList = FXCollections.observableArrayList(getArticleSerice().findArticleByName(val));
		return headersList;
	}
	
	private ArticleHeadersService getArticleSerice() {
		if (articleService != null) {
			return articleService;
		}
		setArticleService(new ArticleHeadersService());
		return articleService;
	}

	private void setArticleService(ArticleHeadersService servise) {
		this.articleService = servise;
	}
		
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		fillTable();
	}
}
