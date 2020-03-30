package net.ddns.dimag.cobhamrunning.utils;

import ch.vorburger.exec.ManagedProcess;
import ch.vorburger.exec.ManagedProcessBuilder;
import ch.vorburger.exec.ManagedProcessException;
import net.ddns.dimag.cobhamrunning.MainApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ProcessDeamon extends Thread {
    private static final Logger LOGGER = LogManager.getLogger(MainApp.class.getSimpleName());
    private final Runtime rt = Runtime.getRuntime();
    private final String[] cmdArr;
    private String cmd;

    public ProcessDeamon(String cmd) {
        this.cmd = cmd;
        this.cmdArr = getCmdArray(cmd);

    }

    public void run() {
        try {
            ManagedProcess proc = new ManagedProcessBuilder("C:\\\\AdemBurn\\StormInterface.exe")
                    .addArgument("a")
                    .addArgument("-s")
                    .addArgument("COM1")
                    .addArgument("-f")
                    .addArgument("DnaAutomaticConfig.ini")
                    .setWorkingDirectory(new File("C:\\\\AdemBurn\\"))
                    .setDestroyOnShutdown(true)
                    .build();

//            proc.start();
            proc.startAndWaitForConsoleMessageMaxMs("started!", 7000);
            while (proc.isAlive()){
                System.out.println(proc.getConsole());
            }
            int status = proc.waitForExit();
//            int status = proc.waitForExitMaxMsOrDestroy(3000);
//            String output = proc.getConsole();



            proc.destroy();
        } catch (ManagedProcessException e) {
            LOGGER.error(e);
            e.printStackTrace();
        }
    }

    private String[] getCmdArray(String cmdString) {
        String[] tmp = cmdString.split("\\s+");
        return tmp;
    }


}






