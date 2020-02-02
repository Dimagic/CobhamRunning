package net.ddns.dimag.cobhamrunning.utils;

import java.net.InetAddress;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.ddns.dimag.cobhamrunning.view.RunningTestController;

public class NetworkScanner extends Thread{
	private static final Logger LOGGER = LogManager.getLogger(NetworkScanner.class.getName());
	private int i;
	
	public NetworkScanner(int i){
		this.i = i;
	}
	
	public void run(){
		try{			
			String currHost = String.format("11.0.0.%d", i);
            InetAddress address = InetAddress.getByName(currHost);
            boolean reachable = address.isReachable(2000);
            if (reachable) {
            	System.out.println(String.format("Found available system in LAN with IP: %s", currHost));
            }        
		} catch (SocketException e){
			LOGGER.error(e);
			run();
        } catch (Exception e){
            e.printStackTrace();
        }
	}
}
