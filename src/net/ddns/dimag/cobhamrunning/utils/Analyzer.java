package net.ddns.dimag.cobhamrunning.utils;

public class Analyzer {
	private String name;
	private String address;
	private PyVisaClient visa;
	
	public Analyzer(String name, String address) {
		this.name = name;
		this.address = address;
	}
	
//	self.sa.write(":SYST:PRES")
//    self.sa.write(":SENSE:FREQ:center " + str(freq) + " MHz")
//    self.sa.write(":SENSE:FREQ:span " + str(int(self.config.getConfAttr('instruments', 'sa_span'))) + " MHz")
//    self.sa.write("DISP:WIND:TRAC:Y:RLEV:OFFS " + str(int(self.currParent.saAtten.text())))
//    self.sa.write("DISP:WIND:TRAC:Y:DLIN -50 dBm")
//    self.sa.write("DISP:WIND:TRAC:Y:DLIN:STAT 1")
//    self.sa.write("CALC:MARK:CPS 1")
//    self.sa.write(":CALC:MARK1:STAT 1")
//    self.sa.write("BAND:VID " + str(int(self.config.getConfAttr('instruments', 'sa_videoBw'))) + " KHZ")
//    self.sa.write(":CAL:AUTO ON")
	
	public void setBW(float center, int bw){
		send_cmd(":SYST:PRES");
		send_cmd(String.format(":SENSE:FREQ:center %s MHz", Integer.valueOf(bw)));
		send_cmd("DISP:WIND:TRAC:Y:RLEV:OFFS 30");
	}
	
	private void send_cmd(String cmd){
		visa.send_cmd(address, cmd);
	}
	
	public void setVisa(PyVisaClient visa){
		this.visa = visa;
	}

}
