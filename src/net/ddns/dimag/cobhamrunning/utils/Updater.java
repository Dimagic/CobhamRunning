package net.ddns.dimag.cobhamrunning.utils;

import javafx.application.Platform;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.view.RootLayoutController;
import net.ddns.dimag.cobhamrunning.view.TestsViewController;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Updater {
    private static final Logger LOGGER = LogManager.getLogger(Updater.class.getName());
    private MainApp mainApp;
    private String remotePath;
    private TestsViewController controller;
    private String remoteFile;
    private String currPath;
    private String currentFile;
    private String newFile;

    public Updater(MainApp mainApp, TestsViewController controller) throws CobhamRunningException {
        this.remotePath = mainApp.getCurrentSettings().getUpdate_path() + "\\";
        this.controller = controller;
        this.currPath = getCurrentPath();
        this.currentFile = String.format(getCurrentFile(), currPath);
        this.newFile = String.format("%snewver.exe", currPath);
        this.remoteFile = String.format("%s%s", remotePath, currentFile);
    }

    public void isNeedUpdate() throws CobhamRunningException{
        try {
            if (isReadyForUpdate()){
                writeConsole("Ready for update.");
                startUpdate();
            } else if (hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(remoteFile))){
                writeConsole(String.format("Found new version: %s", ExeFileInfo.getStringVersion(remoteFile)));
                if (copyFile(remoteFile, newFile)){
                    writeConsole("Copy new version complete.");
                    startUpdate();

                }
            } else {
                writeConsole("New version not found");
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error(e);
            throw new CobhamRunningException(e);
        }
    }

    private boolean copyFile(String original, String copy) {
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

    public boolean hasNewVersion() throws CobhamRunningException {
        return hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(remoteFile));
    }

    public String getRemoteVersion() throws CobhamRunningException {
        return ExeFileInfo.getStringVersion(remoteFile);
    }

    private boolean hasNewVersion(int[] currVer, int[] newVer) {
        int intCurrVer = getIntVersion(currVer);
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
        return new File(newFile).exists() && hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(newFile));
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
        try {
            writeConsole("Starting update");
            new ProcessBuilder("update.exe","CobhamRunning.exe","newver.exe").start();
            Platform.exit();
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    private void writeConsole(String val){
        Platform.runLater(() -> controller.writeConsole(val));
    }


}
