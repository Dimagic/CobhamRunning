package net.ddns.dimag.cobhamrunning.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.LabelTemplate;
import net.ddns.dimag.cobhamrunning.services.LabelTemplateService;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;

import java.util.Collections;
import java.util.List;

public class LabelTemplatesViewController implements MsgBox {
    private Stage dialogStage;
    private MainApp mainApp;
    private ObservableList<LabelTemplate> templateList;
    private LabelTemplateService labelTemplateService = new LabelTemplateService();
    private LabelTemplate currentTemlate = null;
    private boolean isSaveClicked = false;

    @FXML
    private TableView<LabelTemplate> tTemplates;
    @FXML
    private TableColumn<LabelTemplate, String> nameColumn;
    @FXML
    private TextField templateName;
    @FXML
    private TextArea template;

    @FXML
    private Button saveBtn;

    public LabelTemplatesViewController() {

    }

    @FXML
    private void initialize() {
        initItemMenu();
        nameColumn.prefWidthProperty().bind(tTemplates.widthProperty().divide(1));
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        saveBtn.setDisable(true);

        showTemplate(null);
        tTemplates.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showTemplate(newValue));

//		template.textProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(final ObservableValue<? extends String> observable, final String oldValue,
//					final String newValue) {
//				updateTemplate();
//			}
//		});

        templateName.textProperty().addListener((observable, oldValue, newValue) -> {
            currentTemlate.setName(newValue);
            saveBtn.setDisable(false);
        });
        template.textProperty().addListener((observable, oldValue, newValue) -> {
            currentTemlate.setTemplate(newValue);
            saveBtn.setDisable(false);
        });
    }

    @FXML
    private void addTemplate() {
        String name = MsgBox.msgInputString("Enter template name:");
        if (name.isEmpty() && name != null) {
            MsgBox.msgWarning("Add new label template", "You have set name of template.");
            return;
        }
        String template = MsgBox.msgIntutText("Enter template:");
        if (template.isEmpty() && template != null) {
            MsgBox.msgWarning("Add new label template", "Template field should be filling.");
            return;
        }
        if (name == null){
            return;
        }
        LabelTemplate labelTemplate = new LabelTemplate(name, template);
        try {
            labelTemplateService.saveLabelTemplate(labelTemplate);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }
        fillTable();
    }

    private void updateTemplate() {
        if (currentTemlate != null) {
            currentTemlate.setName(templateName.getText());
            currentTemlate.setTemplate(template.getText());
        }
    }

    private void showTemplate(LabelTemplate labelTemplate) {
        saveBtn.setDisable(true);
        if (labelTemplate != null) {
            currentTemlate = labelTemplate;
            templateName.setText(labelTemplate.getName());
            template.setText(labelTemplate.getTemplate());
        } else {
            templateName.setText("");
            template.setText("");
        }

    }

    private void fillTable() {
        try {
            List<LabelTemplate> templateNameList = labelTemplateService.findAllLabelTemplate();
            templateList = FXCollections.observableArrayList(templateNameList);
            tTemplates.setItems(templateList);
        } catch (CobhamRunningException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void saveTemplate() {
        for (LabelTemplate template : templateList) {
            try {
                labelTemplateService.updateLabelTemplate(template);
            } catch (Exception e) {
                try {
                    labelTemplateService.saveLabelTemplate(template);
                } catch (CobhamRunningException ex) {
                    ex.printStackTrace();
                    isSaveClicked = false;
                }
            }
        }
        dialogStage.close();
        isSaveClicked = true;
    }

    private void initItemMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem mDel = new MenuItem("Delete");
        mDel.setOnAction((ActionEvent event) -> {
            LabelTemplate template = tTemplates.getSelectionModel().getSelectedItem();
            try {
                new LabelTemplateService().deleteLabelTemplate(template);
            } catch (CobhamRunningException e) {
                e.printStackTrace();
            }
            templateList.remove(template);
			fillTable();
//            tTemplates.refresh();
        });
        menu.getItems().add(mDel);
        tTemplates.setContextMenu(menu);
    }

    public boolean isSaveClicked() {
        return isSaveClicked;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        fillTable();
    }

}
