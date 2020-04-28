package net.ddns.dimag.cobhamrunning.utils;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import net.ddns.dimag.cobhamrunning.MainApp;

public class JComClient implements MsgBox{
	private static SerialPort serialPort;
	private Settings currentSettings;
	private MainApp mainApp;


	public JComClient(){
		if (getComPortList().length == 0) {
		    MsgBox.msgInfo("There are no serial-ports :(", "You can use an emulator, such ad VSPE, \n"
		    												+ "to create a virtual serial port.");
		    return;
		}
	}
	
	public String[] getComPortList(){
        return SerialPortList.getPortNames();
	}
	
	public String write(String val) throws Exception{
		serialPort = new SerialPort(currentSettings.getCom_combo());
	    try {
	        serialPort.openPort();
	        serialPort.setParams(Integer.parseInt(currentSettings.getBaud_combo()),
	                             SerialPort.DATABITS_8,
	                             SerialPort.STOPBITS_1,
	                             SerialPort.PARITY_NONE);
	        serialPort.setFlowControlMode(SerialPort.PURGE_RXCLEAR | 
	                                      SerialPort.PURGE_TXCLEAR);
	        //Устанавливаем ивент лисенер и маску
//	        serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
	        serialPort.writeString(val);     
	        while (true){
	        	if (serialPort.getOutputBufferBytesCount() == 0) {
	        		if (serialPort.getInputBufferBytesCount() != 0) {
	        			String rx_val = serialPort.readHexString(serialPort.getInputBufferBytesCount(), 1);
	        			serialPort.closePort();
	        			return rx_val;
	        		}
	        	}
	        }
	    }
	    catch (Exception e) {
	    	throw e;
	    } 
	}
	
	private static class PortReader implements SerialPortEventListener {
        public void serialEvent(SerialPortEvent event) {
            if(event.isRXCHAR() && event.getEventValue() > 0){
                try {
                    String data = serialPort.readString(event.getEventValue());
//                    System.out.println(data.length());
//                    serialPort.writeString("Get data >> " + data);
                    System.out.println("RX >> " + data);
                }
                catch (SerialPortException e) {
                    MsgBox.msgException(e);
                }
            }
        }
    }
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		this.currentSettings = mainApp.getCurrentSettings();
	}
}
