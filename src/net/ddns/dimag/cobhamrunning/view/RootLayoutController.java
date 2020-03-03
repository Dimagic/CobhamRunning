package net.ddns.dimag.cobhamrunning.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import net.ddns.dimag.cobhamrunning.MainApp;

public class RootLayoutController {
	private MainApp mainApp;

	@FXML
	private Label dbNameLbl;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDbNameLbl(String val){
        this.dbNameLbl.setText(val);
    }
    
    @FXML
    private void handleSettings() {
        boolean saveClicked = mainApp.showSettingsDialog();
    }
    
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    public RootLayoutController getController() {
		return this;
	}
   
}
