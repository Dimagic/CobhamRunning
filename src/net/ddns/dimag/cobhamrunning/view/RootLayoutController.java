package net.ddns.dimag.cobhamrunning.view;

import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import net.ddns.dimag.cobhamrunning.MainApp;

public class RootLayoutController {

	private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
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
