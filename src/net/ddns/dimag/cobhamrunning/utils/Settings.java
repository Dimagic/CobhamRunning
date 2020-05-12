package net.ddns.dimag.cobhamrunning.utils;

import net.ddns.dimag.cobhamrunning.utils.MsgBox;
import net.ddns.dimag.cobhamrunning.view.SettingsViewController;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;

@XmlRootElement(name = "settings")
public class Settings extends SettingsViewController implements MsgBox {
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
	private String addr_db;
	private String port_db;
	private String name_db;
	private String user_db;
	private String pass_db;
	private String addr_rmv;
	private String name_rmv;
	private String user_rmv;
	private String pass_rmv;
	private String update_path;
	
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
	
	public String getAddr_db() {
		return addr_db;
	}

	public void setAddr_db(String addr_db) {
		this.addr_db = addr_db;
	}

	public String getPort_db() {
		return port_db;
	}

	public void setPort_db(String port_db) {
		this.port_db = port_db;
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

	public String getAddr_rmv() {
		return addr_rmv;
	}

	public void setAddr_rmv(String addr_rmv) {
		this.addr_rmv = addr_rmv;
	}

	public String getName_rmv() {
		return name_rmv;
	}

	public void setName_rmv(String name_rmv) {
		this.name_rmv = name_rmv;
	}

	public String getUser_rmv() {
		return user_rmv;
	}

	public void setUser_rmv(String user_rmv) {
		this.user_rmv = user_rmv;
	}

	public String getPass_rmv() {
		return pass_rmv;
	}

	public void setPass_rmv(String pass_rmv) {
		this.pass_rmv = pass_rmv;
	}

	public String getUpdate_path() {
		return update_path;
	}

	public void setUpdate_path(String update_path) {
		this.update_path = update_path;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Settings{");
		sb.append("ip_telnet_combo='").append(ip_telnet_combo).append('\'');
		sb.append(", ip_ssh_combo='").append(ip_ssh_combo).append('\'');
		sb.append(", com_combo='").append(com_combo).append('\'');
		sb.append(", baud_combo='").append(baud_combo).append('\'');
		sb.append(", gen_combo='").append(gen_combo).append('\'');
		sb.append(", sa_combo='").append(sa_combo).append('\'');
		sb.append(", prnt_combo='").append(prnt_combo).append('\'');
		sb.append(", login_telnet='").append(login_telnet).append('\'');
		sb.append(", login_ssh='").append(login_ssh).append('\'');
		sb.append(", pass_telnet='").append(pass_telnet).append('\'');
		sb.append(", pass_ssh='").append(pass_ssh).append('\'');
		sb.append(", addr_db='").append(addr_db).append('\'');
		sb.append(", port_db='").append(port_db).append('\'');
		sb.append(", name_db='").append(name_db).append('\'');
		sb.append(", user_db='").append(user_db).append('\'');
		sb.append(", pass_db='").append(pass_db).append('\'');
		sb.append(", addr_rmv='").append(addr_rmv).append('\'');
		sb.append(", name_rmv='").append(name_rmv).append('\'');
		sb.append(", user_rmv='").append(user_rmv).append('\'');
		sb.append(", pass_rmv='").append(pass_rmv).append('\'');
		sb.append(", update_path='").append(update_path).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
