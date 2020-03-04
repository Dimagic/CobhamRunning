package net.ddns.dimag.cobhamrunning.view;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.ArticleHeaders;
import net.ddns.dimag.cobhamrunning.models.Asis;
import net.ddns.dimag.cobhamrunning.models.AsisPrintJob;
import net.ddns.dimag.cobhamrunning.models.MacAddress;
import net.ddns.dimag.cobhamrunning.services.ArticleHeadersService;
import net.ddns.dimag.cobhamrunning.services.AsisPrintJobService;
import net.ddns.dimag.cobhamrunning.services.AsisService;
import net.ddns.dimag.cobhamrunning.services.MacAddressService;
import net.ddns.dimag.cobhamrunning.utils.HibernateSessionFactoryUtil;
import net.ddns.dimag.cobhamrunning.utils.ImportData;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;

public class AsisCreatorController implements MsgBox {
    private AsisService asisService;
    private MacAddressService macService;
    private AsisPrintJobService asisPrintJobService;
    private ArticleHeadersService articleHeadersService;
    private ArticleHeaders currArticle;
    private Stage dialogStage;
    private MainApp mainApp;

    @FXML
    private TextField searchField;
    @FXML
    private TextField countField;
    @FXML
    private Label selectedArticleLbl;
    @FXML
    private Button createBtn;
    @FXML
    private Label avalAsisLbl;
    @FXML
    private Label avalMacLbl;
    @FXML
    private Label unprintesLbl;

    @FXML
    private TableView<ArticleHeaders> tArticle;
    @FXML
    private TableColumn<ArticleHeaders, String> articleColumn;
    @FXML
    private TableColumn<ArticleHeaders, String> shotrDescriptColumn;
    @FXML
    private TableColumn<ArticleHeaders, String> longDescriptColumn;
    @FXML
    private TableColumn<ArticleHeaders, Boolean> macColumn;


    public AsisCreatorController() {
        super();
    }

    @FXML
    private void initialize() {
        articleColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(4));
        shotrDescriptColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(4));
        longDescriptColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(3));
        macColumn.prefWidthProperty().bind(tArticle.widthProperty().divide(6));

        articleColumn.setCellValueFactory(cellData -> cellData.getValue().articleProperty());
        shotrDescriptColumn.setCellValueFactory(cellData -> cellData.getValue().shortDescriptProperty());
        longDescriptColumn.setCellValueFactory(cellData -> cellData.getValue().longDescriptProperty());
        macColumn.setCellValueFactory(cellData -> cellData.getValue().isNeedMacProperty());

        tArticle.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue == null) {
                        currArticle = null;
                        setSelectedArticleLbl(null);
                    } else {
                        currArticle = newValue;
                        setSelectedArticleLbl(currArticle.getArticle());
                    }

                });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.toString().isEmpty()) {
                tArticle.setItems(FXCollections.emptyObservableList());
            } else {
                fillTable();
            }
        });
        countField.textProperty().addListener((observable, oldValue, newValue) -> {
            int maxLength = 2;
            if (countField.getText().length() > maxLength) {
                String s = countField.getText().substring(0, maxLength);
                countField.setText(s);
            }
            if (!newValue.matches("\\d*")) {
                countField.setText(newValue.replaceAll("[^\\d]", ""));
            }

            try {
                int currCount = Integer.parseInt(countField.getText());
                String msgText = "";
                if (currArticle.isNeedMacProperty().getValue()) {
                    if (currCount > Integer.parseInt(avalAsisLbl.getText()) || currCount > Integer.parseInt(avalMacLbl.getText()))
                        msgText = "There are no enough available ASIS or MAC addresses.";
                } else {
                    if (currCount > Integer.parseInt(avalAsisLbl.getText()))
                        msgText = "Not enough available ASIS.";
                }
                if (!msgText.isEmpty()) {
                    MsgBox.msgWarning("Warning", msgText);
                    createBtn.setDisable(true);
                } else {
                    createBtn.setDisable(false);
                }
            } catch (NumberFormatException e) {
            }
        });
    }

    private void fillTable() {
        ObservableList<ArticleHeaders> articleList = null;
        try {
            articleList = FXCollections
                    .observableArrayList(getArticleHeadersService().findArticleHeadersListByName(searchField.getText().toUpperCase()));
            tArticle.setItems(articleList);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleCreateBtn() throws CobhamRunningException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            Date dateCreate = new Date();
            ArticleHeaders articleHeaders = tArticle.getSelectionModel().selectedItemProperty().getValue();
            List<Asis> asisList = getAsisSerice().getAvaliableAsisRange(Integer.parseInt(countField.getText()));
            AsisPrintJobService asisPrintJobService = new AsisPrintJobService();
            MacAddressService macAddressService = new MacAddressService();
            AsisPrintJob asisPrintJob = new AsisPrintJob();
            asisPrintJob.setAsisSet(asisList.stream().collect(Collectors.toSet()));
            asisPrintJob.setArticle(articleHeaders);
            asisPrintJob.setStart(asisList.get(0).getAsis());
            asisPrintJob.setStop(asisList.get(asisList.size() - 1).getAsis());
            asisPrintJob.setCount(asisList.size());
            asisPrintJob.setDateCreate(dateCreate);
            asisPrintJobService.savePrintJob(asisPrintJob);

            for (Asis asis : asisList) {
                asis.setArticleHeaders(articleHeaders);
                asis.setDateCreate(dateCreate);
                asis.setPrintJob(asisPrintJob);
                if (asis.getArticleHeaders().getNeedmac()) {
                    MacAddress mac = macAddressService.getFirstAvailableMac();
                    asis.setMacAddress(mac);
                    mac.setAsis(asis);
                    macAddressService.updateMac(mac);
                }
                getAsisSerice().updateAsis(asis);
            }
            tx.commit();
            setStatistic();
            MsgBox.msgInfo("Create ASIS", String.format("New ASIS range created\nRange: %s-%s\nCount: %s",
                    asisList.get(0).getAsis(),
                    asisList.get(asisList.size() - 1).getAsis(),
                    asisList.size()));

        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
            MsgBox.msgException(e);
        } finally {
            session.close();
        }

    }

    @FXML
    private void handleUnprintedBtn() {
        mainApp.showPrintAsisView();
    }

    @FXML
    private void handlePrintBtn() {

    }

    @FXML
    private void handleArticlesMenu() {
        mainApp.showArticlesView();
    }

    @FXML
    private void handleLabelsMenu() {
        mainApp.showLabelTemplatesView();
    }

    @FXML
    private void handleImportAsisMenu() {
		try {
			new ImportData().importAsis();
			setStatistic();
		} catch (CobhamRunningException e) {
			e.printStackTrace();
		}

    }

    @FXML
    private void handleImportArticleMenu() {
		try {
			new ImportData().importArticles();
			setStatistic();
		} catch (CobhamRunningException e) {
			e.printStackTrace();
		}

    }

    @FXML
    private void handleGenerateMacMenu() {
		try {
			new ImportData().generateMac();
			setStatistic();
		} catch (CobhamRunningException e) {
			e.printStackTrace();
		}

    }

    private void setSelectedArticleLbl(String s) {
        if (s != null) {
            countField.setDisable(false);
            createBtn.setDisable(false);
            selectedArticleLbl.setText(s);
        } else {
            countField.setText("");
            countField.setDisable(true);
            createBtn.setDisable(true);
            selectedArticleLbl.setText("");
        }

    }

    public void setStatistic() {
        try {
            avalAsisLbl.setText(Integer.toString(getAsisSerice().getAvaliableAsisCount()));
            avalMacLbl.setText(Integer.toString(getMacSerice().getAvailableMacCount()));
            unprintesLbl.setText(Integer.toString(getAsisPrintJobSerice().getUnprintedAsisCount()));
            ;
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
    }

    private AsisService getAsisSerice() {
        if (asisService != null) {
            return asisService;
        }
        setAsisService(new AsisService());
        return asisService;
    }

    private void setAsisService(AsisService servise) {
        this.asisService = servise;
    }

    private MacAddressService getMacSerice() {
        if (macService != null) {
            return macService;
        }
        setMacService(new MacAddressService());
        return macService;
    }

    private void setMacService(MacAddressService servise) {
        this.macService = servise;
    }

    private AsisPrintJobService getAsisPrintJobSerice() {
        if (asisPrintJobService != null) {
            return asisPrintJobService;
        }
        setAsisPrintJobSerice(new AsisPrintJobService());
        return asisPrintJobService;
    }

    private void setAsisPrintJobSerice(AsisPrintJobService servise) {
        this.asisPrintJobService = servise;
    }

    private ArticleHeadersService getArticleHeadersService() {
        if (articleHeadersService == null) {
            articleHeadersService = new ArticleHeadersService();
            return articleHeadersService;
        }
        return articleHeadersService;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        setSelectedArticleLbl(null);
        setStatistic();
    }
}
