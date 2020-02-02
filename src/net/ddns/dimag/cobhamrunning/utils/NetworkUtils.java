package net.ddns.dimag.cobhamrunning.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;


public class NetworkUtils implements MsgBox {
	
	public static HashMap<String, String> getArpMap(String ip) throws InterruptedException, IOException {
		
		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 1; i < 255; i++) {
			es.execute(new NetworkScanner(i));
		}
		es.shutdown();
		
		es.awaitTermination(5, TimeUnit.SECONDS);
		Scanner s;
		String n = "";
		s = new Scanner(Runtime.getRuntime().exec(String.format("arp /a /n %s", ip)).getInputStream())
				.useDelimiter("\\A");
		while (s.hasNext()) {
			n = s.next();
		}
		ArrayList<String> arp = new ArrayList<>(Arrays.asList(n.trim().split("([\n])")));
		HashMap<String, String> ipMacMap = new HashMap<String, String>();
		arp.forEach(items -> {
			Matcher matcherIp;
			Matcher matcherMac;
			String ipTmp = null;
			String macTmp = null;
			for (String tmp : items.split("[ \t]+")) {
				matcherIp = patternIp.matcher(tmp);
				matcherMac = patternMac.matcher(tmp);
				if (ipTmp == null) {
					ipTmp = matcherIp.find() ? tmp.substring(matcherIp.start(), matcherIp.end()) : null;
				}
				if (macTmp == null) {
					macTmp = matcherMac.find() ? tmp.substring(matcherMac.start(), matcherMac.end()).toUpperCase()
							: null;
				}

				if (ipTmp != null && macTmp != null) {
					ipMacMap.put(macTmp, ipTmp);
					break;
				}

			}
			ipTmp = null;
			macTmp = null;

		});
		return ipMacMap;
	}

	public static ArrayList<String> getInterfaceIp() {
		try {
			Matcher matcher;
			String currAddress;
			ArrayList<String> ipAddressList = new ArrayList<>();
			Enumeration<NetworkInterface> nets;
			nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)){
		        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
		        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
		        	currAddress = inetAddress.toString().replace("/", "");
		        	matcher = patternIp.matcher(currAddress);
		        	if (matcher.find() && !inetAddress.isLoopbackAddress()){
		        		ipAddressList.add(currAddress);
		        	}    
		        }
			}
			return ipAddressList;
		} catch (SocketException e) {
			MsgBox.msgException(e);
		} 
		return null;
     }
	
	public static boolean isIpInSystem(String ip){
		boolean found = false;
		for (String s : getInterfaceIp()) {
			if (s.equals(ip)) {
				found = true;
				break;
			}
		}
		return found;
	}
}
