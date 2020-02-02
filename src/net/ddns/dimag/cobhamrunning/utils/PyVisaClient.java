package net.ddns.dimag.cobhamrunning.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.view.TestsViewController;


public class PyVisaClient {
	private String address;
	private int port;
	private List<String> deviceAddressList;
	private TestsViewController controller;
	
	public PyVisaClient(String address, int port){
		this.address = address;
		this.port = port;
	}
	
	private JSONObject reader(InputStream reader){
		JSONParser parser = new JSONParser();
        StringBuilder data = new StringBuilder();                      
        JSONObject json = null;
        int character;
        try {
			while ((character = reader.read()) != -1) {
				data.append((char) character);
				try{
					json = (JSONObject) parser.parse(data.toString());
					break;
				} catch (Exception e) {
					continue;
				}        	
			}
		} catch (IOException e) {
			MsgBox.msgException(e);
		}
        return json;
	}
	
	
	public List<String> getInstrumentAddressList() throws Exception{
		List<String> instruments = new ArrayList<String>();
		JSONObject obj = new JSONObject();
		obj.put("cmd", "get_rm_list");
		Socket socket = getSocket();
		OutputStream output = socket.getOutputStream();
		output.flush();
		PrintWriter writer = new PrintWriter(output, true);
		writer.println(obj);
		
		JSONObject json = reader(socket.getInputStream());
        
        String tmp = json.get("value").toString();
        String[] split = tmp.split("', '|','|\\('|'\\)|',\\)");
        
        // remove nullable elements from array            
        split = ArrayUtils.removeElement(split, "");
        if (split.length < 1){
        	MsgBox.msgInfo("Instruments", "Instruments not found");
        	return null;
        }
        
        for (String s: split) {
        	instruments.add(s); 	
        }
        socket.close();
		return instruments;
	}
	
	public JSONObject send_query(String address, String cmd) {
		System.out.println(String.format("%s >> %s", address, cmd));
		OutputStream output;
		PrintWriter writer;
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		obj.put("cmd", "send_query");
		obj.put("arg1", address);
		obj.put("arg2", cmd);
    
		try {
			Socket socket = getSocket();
			System.out.println(socket);
			System.out.println(socket.isConnected());
			output = socket.getOutputStream();
			output.flush();
			writer = new PrintWriter(output, true);
			writer.println(obj);
			
			JSONObject json = reader(socket.getInputStream());
            if(!Boolean.valueOf((boolean) json.get("type"))){
            	MsgBox.msgError("VISA run cmd error", String.format("%s >>> %s", address, cmd), json.get("value").toString()); 
            	return null;
            }
	        System.out.println(json.get("value"));
	        
	        socket.close();
			return json;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void send_cmd(String address, String cmd){
		OutputStream output;
		InputStream reader;
		PrintWriter writer;
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		obj.put("cmd", "send_cmd");
		obj.put("arg1", address);
		obj.put("arg2", cmd);
		try{      
			Socket socket = getSocket();
			output = socket.getOutputStream();
			reader = socket.getInputStream();
			output.flush();
			writer = new PrintWriter(output, true);
			writer.println(obj);
			socket.close();
		}catch(Exception e){
			System.out.println(e);
		}  
	}
	
//	public Map<String, String> getListInstruments(String instr_addr){
//		Map<String, String> instr = new HashMap<String, String>();
//		for(String addr: getInstrumentAddressList()){
//			instr.put(addr, getInstumentName(addr));
//		}
//		
//		return null;
//	}
	
	public Socket getSocket(){
		Socket socket = new Socket();
		if(socket != null && socket.isConnected()){
			return socket;
		} else {
			try{
				return new Socket(this.address, this.port);
			} catch (IOException e) {
				String msg = String.format("PJVISA server %s:%s not found", address, port);
				MsgBox.msgWarning("PyVisaClient", msg);
				return null;
			}
		}
		
	}
	
	public String getInstumentName(String addr){
		String res = (String) send_query(addr, "*IDN?").get("value");
		return res;
	}
	
	public void setController(TestsViewController controller){
		this.controller = controller;
	}

	
}
