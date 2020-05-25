package net.ddns.dimag.cobhamrunning.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.utils.CobhamRunningException;
import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.utils.ProcessDeamon;
import net.ddns.dimag.cobhamrunning.utils.Updater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class RootLayoutController {
    private static final Logger LOGGER = LogManager.getLogger(RootLayoutController.class.getName());
    private MainApp mainApp;

    @FXML
    private Label dbNameLbl;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setDbNameLbl(String val) {
        this.dbNameLbl.setText(val);
    }

    @FXML
    private void handleSettings() {
        mainApp.showSettingsDialog();
    }

    @FXML
    private void handleUpdate() {
        try{
            Updater updater = new Updater(mainApp, mainApp.getController());
            updater.isNeedUpdate();
        } catch (CobhamRunningException e) {
            MsgBox.msgException(e);
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleStartTest() {
        File f = new File("C:\\AdemBurn\\DnaInterface.exe");
        if (f.exists()){
//            ProcessDeamon processDeamon = new ProcessDeamon("C:\\\\AdemBurn\\DnaInterface.exe a -s COM1 -f DnaAutomaticConfig.ini");
//            Thread t = new Thread(processDeamon);
//            t.start();
            String command = "C:\\\\AdemBurn\\DnaInterface.exe a -s COM1 -f DnaAutomaticConfig.ini";
            ProcessDeamon pd = new ProcessDeamon(command);
            Thread t = new Thread(pd);
            t.start();
        } else {
            MsgBox.msgWarning("AdemBurn", "File StormInterface.exe not found");
        }
    }

    public RootLayoutController getController() {
        return this;
    }
}
