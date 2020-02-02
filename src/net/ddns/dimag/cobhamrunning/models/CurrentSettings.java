package net.ddns.dimag.cobhamrunning.models;

import java.lang.reflect.Field;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;

import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.view.SettingsViewController;

@XmlRootElement(name = "settings")
public class CurrentSettings extends SettingsViewController{
	private String ip_telnet;
	private String ip_ssh;
	private String com_combo;
	private String baud_combo;
	private String gen_combo;
	private String sa_combo;
	private String login_telnet;
	private String login_ssh;
	private String pass_telnet;
	private String pass_ssh;
	
	public CurrentSettings(){	
	}
	
	public CurrentSettings(HashMap<String, String> args) {
		try {
			Field[] fields = this.getClass().getDeclaredFields();
			for(Field field: fields) {
				field.setAccessible(true);
				field.set(this, args.get(field.getName()));
			}
		} catch (SecurityException
                | IllegalArgumentException 
                | IllegalAccessException e) {
            MsgBox.msgException(e);
		}
	}

	public String getIp_telnet() {
		return ip_telnet;
	}

	public void setIp_telnet(String ip_telnet) {
		this.ip_telnet = ip_telnet;
	}

	public String getIp_ssh() {
		return ip_ssh;
	}

	public void setIp_ssh(String ip_ssh) {
		this.ip_ssh = ip_ssh;
	}

	public String getCom_combo() {
		return com_combo;
	}

	public void setCom_combo(String com_combo) {
		this.com_combo = com_combo;
	}

	public String getBaud_combo() {
		return baud_combo;
	}

	public void setBaud_combo(String baud_combo) {
		this.baud_combo = baud_combo;
	}
	
	public String getGen_combo() {
		return gen_combo;
	}

	public void setGen_combo(String gen_combo) {
		this.gen_combo = gen_combo;
	}
	
	public String getSa_combo() {
		return sa_combo;
	}

	public void setSa_combo(String sa_combo) {
		this.sa_combo = sa_combo;
	}

	public String getLogin_telnet() {
		return login_telnet;
	}

	public void setLogin_telnet(String login_telnet) {
		this.login_telnet = login_telnet;
	}

	public String getLogin_ssh() {
		return login_ssh;
	}

	public void setLogin_ssh(String login_ssh) {
		this.login_ssh = login_ssh;
	}

	public String getPass_telnet() {
		return pass_telnet;
	}

	public void setPass_telnet(String pass_telnet) {
		this.pass_telnet = pass_telnet;
	}

	public String getPass_ssh() {
		return pass_ssh;
	}

	public void setPass_ssh(String pass_ssh) {
		this.pass_ssh = pass_ssh;
	}

	@Override
	public String toString() {
		return "CurrentSettings [ip_telnet=" + ip_telnet + ", ip_ssh=" + ip_ssh + ", com_combo=" + com_combo
				+ ", baud_combo=" + baud_combo + ", login_telnet=" + login_telnet + ", login_ssh=" + login_ssh
				+ ", pass_telnet=" + pass_telnet + ", pass_ssh=" + pass_ssh + "]";
	}
}
