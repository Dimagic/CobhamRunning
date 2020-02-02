package net.ddns.dimag.cobhamrunning.utils;

public interface SystemCommands {
	String uptimeCmd = "awk '{print $1}' /proc/uptime";
	String measEnvCmd = "measurements dump env --json";
	
	
	
	


}
