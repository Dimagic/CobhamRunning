package net.ddns.dimag.cobhamrunning.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import net.ddns.dimag.cobhamrunning.MainApp;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Updater {
    private static final Logger LOGGER = LogManager.getLogger(Updater.class.getName());
    private final String remotePath = "//11.0.0.10/CobhamRunning/";
    private final String remoteFile = "//11.0.0.10/CobhamRunning/CobhamRunning.exe";
//    private final String currPath = "Z:\\CobhamRunning\\";
    private String currPath;
    private String currentFile;
    private String oldFile;
    private String newFile;

    public Updater() throws CobhamRunningException {
        this.currPath = getCurrentPath();
        this.currentFile = String.format("%sCobhamRunning.exe", currPath);
        this.newFile = String.format("%snewver.exe", currPath);
        this.oldFile = String.format("%soldver.exe", currPath);
    }

    public void isNeedUpdate() throws CobhamRunningException{
        try {
            System.out.println(ExeFileInfo.getVersionInfo(currentFile));
            System.out.println(ExeFileInfo.getVersionInfo(remoteFile));


            sendMsg("Current path", getCurrentPath());
//            sendMsg("Current file", getCurrentFile());
            if (isReadyForUpdate()){
                sendMsg("Ready for update");
                startOldVersion();
            } else if (hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(remoteFile))){
                sendMsg("Has new version");
                if (copyFile(remoteFile, newFile) && copyFile(currentFile, oldFile)){
                    sendMsg("Copy new version complete\nCopy current version complete");
                }

            } else if (isUpdateComplete()){
                sendMsg("Update complete");
            } else {
                sendMsg("New version not found");
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
            e.printStackTrace();
        }
        return false;
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

    private String getProgramPath() throws CobhamRunningException{
        String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodePath = null;
        try {
            decodePath = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e);
            throw new CobhamRunningException(e);
        }
        return decodePath;
    }

    private void startOldVersion() throws CobhamRunningException {
        try {
            ArrayList<String> command = new ArrayList<String>();
            command.add("cmd.exe");
            command.add("/c");
            command.add(oldFile);
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
            System.exit(0);
        } catch (IOException e) {
            LOGGER.error(e);
            throw new CobhamRunningException(e);
        }
    }

    private void startNewVer() throws IOException {
//        ArrayList<String> command = new ArrayList<String>();
//        command.add("cmd.exe");
//        command.add("/c");
//        command.add(newFile);
//        ProcessBuilder builder = new ProcessBuilder(command);
//        builder.start();
//        System.exit(0);
    }

    private boolean isReadyForUpdate(){
        return new File(newFile).exists() && hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(newFile));
    }

    private boolean isUpdateComplete(){
        return new File(oldFile).exists() && new File(newFile).exists() &&
                !hasNewVersion(ExeFileInfo.getVersionInfo(currentFile), ExeFileInfo.getVersionInfo(oldFile));
    }

    public String getCurrentPath() throws CobhamRunningException {
        String path = null;
        try {
            path = new File(".").getCanonicalPath();
        } catch (IOException e) {
            LOGGER.error(e);
            throw new CobhamRunningException(e);
        }
        return path+ "\\";
    }

    public String getCurrentFile() throws CobhamRunningException {
        String path = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String file = URLDecoder.decode(path, "UTF-8");
            sendMsg(file);
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

    public void sendMsg(String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Program update");
        alert.setContentText(msg);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }

    public void sendMsg(String header, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Program update");
        alert.setHeaderText(header);
        alert.setContentText(msg);
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) {
                System.out.println("Pressed OK.");
            }
        });
    }
}
