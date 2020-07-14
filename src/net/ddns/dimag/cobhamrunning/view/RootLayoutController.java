package net.ddns.dimag.cobhamrunning.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.models.Tests;
import net.ddns.dimag.cobhamrunning.services.TestsService;
import net.ddns.dimag.cobhamrunning.utils.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            MsgBox.msgWarning("CobhamRunning", e.getLocalizedMessage());
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

    @FXML
    private void handleFixTestStatus(){
        Thread thread = new Thread(){
            public void run(){
                try {
                    RmvUtils rmvUtils = new RmvUtils(mainApp);
                    TestsService testsService = new TestsService();
                    for (Tests test: testsService.findAllTests()){
                        Integer testTime = rmvUtils.getTestTimeByDateTest(test.getDateTest());
                        test.setTestTime(testTime);
                        testsService.updateTest(test);
                        writeConsole(String.format("System: %s ASIS: %s\nTest time: %s Test name: %s",
                                test.getDevice().getAsis().getArticleHeaders().getArticle(),
                                test.getDevice().getAsis().getAsis(), testTime, test.getName()));
                    }
                    writeConsole("Complete");
                } catch (CobhamRunningException | SQLException | ClassNotFoundException e) {
                    writeConsole(String.format("ERROR: %s", e.getMessage()));
                    MsgBox.msgException(e);
                }
            }
        };
        thread.start();

    }

    private void writeConsole(String s){
        Platform.runLater(() -> {
            mainApp.getController().writeConsole(s);
        });
    }

    public RootLayoutController getController() {
        return this;
    }
}
