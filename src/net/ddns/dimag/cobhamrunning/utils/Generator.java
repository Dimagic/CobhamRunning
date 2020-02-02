package net.ddns.dimag.cobhamrunning.utils;

import org.json.simple.JSONObject;

public class Generator {
	private String name;
	private String address;
	private PyVisaClient visa;

	public Generator(String address, String name) {
		this.address = address;
		this.name = name;
	}

	public void setAmpl(float ampl){
		String cmd = String.format("POW:AMPL %s dBm", Float.toString(ampl));
		visa.send_query(address, cmd);
	}
	
	public void setFreq(float freq){
		String cmd = String.format(":FREQ:FIX %s MHz", Float.toString(freq));
		visa.send_cmd(address, cmd);
	}
	
	public float getAmpl() {		
		try {
			JSONObject json = visa.send_query(this.address, "POW:AMPL?");
			return Float.parseFloat((String) json.get("value"));
	    }
	    catch (NumberFormatException nfe)
	    {
	      nfe.printStackTrace();
	    }
		return 0;
	}
	
	public void setVisa(PyVisaClient visa){
		this.visa = visa;
	}
}
