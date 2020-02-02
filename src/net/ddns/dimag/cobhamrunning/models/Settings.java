package net.ddns.dimag.cobhamrunning.models;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.view.SettingsViewController;

@XmlRootElement(name = "settings")
public class Settings extends SettingsViewController{
	private String ip_telnet_combo;
	private String ip_ssh_combo;
	private String com_combo;
	private String baud_combo;
	private String gen_combo;
	private String sa_combo;
	private String prnt_combo;
	private String login_telnet;
	private String login_ssh;
	private String pass_telnet;
	private String pass_ssh;
	private String path_db;
	private String name_db;
	private String user_db;
	private String pass_db;
	
	public Settings(){	
	}
	
	public Settings(HashMap<String, String> args) {
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
	
	public static Settings loadSettings() {
		File file = new File("./settings.xml");
	    try {    	
	        JAXBContext context = JAXBContext.newInstance(Settings.class);
	        Unmarshaller um = context.createUnmarshaller();
	        return  (Settings) um.unmarshal(file);
	    } catch (Exception e) { 
	    	MsgBox.msgException(e);
	    }
		return null;
	}

	public String getIp_telnet() {
		return ip_telnet_combo;
	}

	public void setIp_telnet(String ip_telnet_combo) {
		this.ip_telnet_combo = ip_telnet_combo;
	}

	public String getIp_ssh() {
		return ip_ssh_combo;
	}

	public void setIp_ssh(String ip_ssh_combo) {
		this.ip_ssh_combo = ip_ssh_combo;
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
	
	public void setBaud_combo(String baud_combo){
		this.baud_combo = baud_combo;
	}

	public void setGen_combo(String gen_combo) {
		this.gen_combo = gen_combo;
	}
	
	public String getGen_combo() {
		return gen_combo;
	}

	public void setSa_combo(String sa_combo) {
		this.sa_combo = sa_combo;
	}
	
	public String getSa_combo() {
		return sa_combo;
	}
	
	public String getPrnt_combo() {
		return prnt_combo;
	}

	public void setPrnt_combo(String prnt_combo) {
		this.prnt_combo = prnt_combo;
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
	
	public String getPath_db() {
		return path_db;
	}

	public void setPath_db(String path_db) {
		this.path_db = path_db;
	}

	public String getName_db() {
		return name_db;
	}

	public void setName_db(String name_db) {
		this.name_db = name_db;
	}

	public String getUser_db() {
		return user_db;
	}

	public void setUser_db(String user_db) {
		this.user_db = user_db;
	}

	public String getPass_db() {
		return pass_db;
	}

	public void setPass_db(String pass_db) {
		this.pass_db = pass_db;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Settings [ip_telnet_combo=");
		builder.append(ip_telnet_combo);
		builder.append(", ip_ssh_combo=");
		builder.append(ip_ssh_combo);
		builder.append(", com_combo=");
		builder.append(com_combo);
		builder.append(", baud_combo=");
		builder.append(baud_combo);
		builder.append(", gen_combo=");
		builder.append(gen_combo);
		builder.append(", sa_combo=");
		builder.append(sa_combo);
		builder.append(", prnt_combo=");
		builder.append(prnt_combo);
		builder.append(", login_telnet=");
		builder.append(login_telnet);
		builder.append(", login_ssh=");
		builder.append(login_ssh);
		builder.append(", pass_telnet=");
		builder.append(pass_telnet);
		builder.append(", pass_ssh=");
		builder.append(pass_ssh);
		builder.append(", path_db=");
		builder.append(path_db);
		builder.append(", name_db=");
		builder.append(name_db);
		builder.append(", user_db=");
		builder.append(user_db);
		builder.append(", pass_db=");
		builder.append(pass_db);
		builder.append("]");
		return builder.toString();
	}

		
}
