package net.ddns.dimag.cobhamrunning.utils;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.view.TestsViewController;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Updater extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(Updater.class.getName());
    private MainApp mainApp;
    private String remotePath;
    private TestsViewController controller;
    private String remoteFile;
    private String currPath;
    private String currentFile;
    private String newFile;
    private int[] currVersion;

    public Updater(MainApp mainApp, TestsViewController controller) throws CobhamRunningException {
        this.remotePath = mainApp.getCurrentSettings().getUpdate_path() + "\\";
        this.controller = controller;
        this.currPath = getCurrentPath();
        this.currentFile = String.format(getCurrentFile(), currPath);
        this.newFile = String.format("%snewver.exe", currPath);
        this.remoteFile = String.format("%s%s", remotePath, currentFile);
        this.currVersion = Arrays.asList(mainApp.getVERSION().split("[.]")).stream().mapToInt(Integer::parseInt).toArray();
    }

    public void run() {

        try {
//            mainApp.setDbUrl();
            writeConsole("Start updater");
            writeConsole("Trying connect to DB");
            writeConsole(String.format("Connected to DB: %s", HibernateSessionFactoryUtil.getConnectionInfo().get("DataBaseUrl")));
            currentFile = String.format(getCurrentFile(), currPath);
            isNeedUpdate();
        } catch (CobhamRunningException e){
            LOGGER.error(e);
            MsgBox.msgException(e);
        }


    }

    public void isNeedUpdate() throws CobhamRunningException {
        try {
            if (isReadyForUpdate()){
                writeConsole("Ready for update.");
                startUpdate();
            } else if (hasNewVersion(ExeFileInfo.getVersionInfo(remoteFile))){
                writeConsole(String.format("Found new version: %s", ExeFileInfo.getStringVersion(remoteFile)));
                if (copyFile(remoteFile, newFile)){
                    writeConsole("Download new version complete.");
                    startUpdate();
                }
            } else {
                writeConsole("New version not found");
            }
        } catch (IllegalArgumentException e) {
            throw new CobhamRunningException(e);
        }
    }

    private boolean copyFile(String original, String copy) {
        writeConsole("Downloading new version");
        File origFile = new File(original);
        File copyFile = new File(copy);
        try {
            FileUtils.copyFile(origFile, copyFile);
            return FileUtils.contentEquals(origFile, copyFile);
        } catch (IOException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
        return false;
    }

    private boolean hasNewVersion(int[] newVer) {
        int intCurrVer = getIntVersion(currVersion);
        int intNewVer = getIntVersion(newVer);
        return intNewVer > intCurrVer;
    }

    private int getIntVersion(int[] ver) {
        int intVer = 0;
        int n = 100;
        for (int i = ver.length - 1; i >= 0; i--) {
            intVer += ver[i] * n;
            n *= 100;
        }
        return intVer;
    }

    private boolean isReadyForUpdate() throws CobhamRunningException {
        return new File(newFile).exists() && hasNewVersion(ExeFileInfo.getVersionInfo(newFile));
    }

    private String getCurrentPath() throws CobhamRunningException {
        String path = null;
        try {
            path = new File(".").getCanonicalPath();
        } catch (IOException e) {
            LOGGER.error(e);
            throw new CobhamRunningException(e);
        }
        return path+ "\\";
    }

    private String getCurrentFile() throws CobhamRunningException {
        String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String file = URLDecoder.decode(path, "UTF-8");
            Pattern pattern = Pattern.compile("[a-zA-Z]+(.exe)$");
            Matcher matcher = pattern.matcher(file);
            if(matcher.find()) {
                return matcher.group();
            } else {
                throw new CobhamRunningException("Can't get current file name");
            }
        } catch (UnsupportedEncodingException e) {
            throw new CobhamRunningException(e);
        }
    }

    private void startUpdate(){
        if (MsgBox.msgConfirm("Updater", "Press Ok for start update")) {
            try {
                writeConsole("Starting update");
                new ProcessBuilder("update.exe","CobhamRunning.exe","newver.exe").start();
                Platform.exit();
            } catch (IOException e) {
                LOGGER.error(e);
            }
        }
    }

    private void writeConsole(String val){
        Platform.runLater(() -> controller.writeConsole(val));
    }

    public String getRemotePath() {
        return remotePath;
    }
}
