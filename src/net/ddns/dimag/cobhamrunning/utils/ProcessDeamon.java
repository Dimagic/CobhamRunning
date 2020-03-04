package net.ddns.dimag.cobhamrunning.utils;

import javafx.concurrent.Task;

import java.io.*;
import java.util.Arrays;

public class ProcessDeamon extends Task<Void> {
    String name;

    public ProcessDeamon(String name){
        this.name = name;
    }

    @Override
    protected Void call() throws IOException {
        Process process = new ProcessBuilder("ping", "8.8.8.8", "-t").start();
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

//        System.out.printf("Output of running %s is:", Arrays.toString(args));

        while ((line = br.readLine()) != null) {
            System.out.println(String.format("Thread #%s : %s", name, line));
        }
        return null;
    }
}






