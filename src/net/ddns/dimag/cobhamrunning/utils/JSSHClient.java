package net.ddns.dimag.cobhamrunning.utils;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSSHClient implements MsgBox, SystemCommands {
	private static final Logger LOGGER = LogManager.getLogger(JSSHClient.class.getName());
	private String host;
	private String user;
	private String password;
	private JSch jsch;
	private Session session;
	private Channel channel;

	public JSSHClient(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}

	public HashMap<String, String> send(String command) throws Exception {
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			command = findPathCmd(command);
			session = getSession();
			System.out.println(command);
			if (!session.isConnected()) {
				return null;
			}

			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);

			InputStream in = channel.getInputStream();
			channel.connect();

			byte[] tmp = new byte[4096];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 4096);
					if (i < 0)
						break;
					result.put("result", new String(tmp, 0, i).trim());

				}
				if (channel.isClosed()) {
					result.put("exit-status", Integer.toString(channel.getExitStatus()));
					break;
				}
				Thread.sleep(1000);
			}
			channel.disconnect();
			session.disconnect();
		} catch (ConnectException | InterruptedException e) {
			LOGGER.error(e);
			throw e;
		} 
		return result;
	}

	private Session getSession() throws JSchException {
		if (session != null && session.isConnected()) {
			return session;
		} else {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			jsch = new JSch();
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			return session;
		}
	}

	private String findPathCmd(String cmd) throws Exception {
		String cmdForSearch = cmd.split(" ")[0];
		if (cmdForSearch.equals("find")) {
			return cmd;
		}
		String[] cmdPathArr = send(String.format("find / -name \"%s\"", cmdForSearch)).get("result").split("\n");
		for(String path: cmdPathArr){
			if (path.toLowerCase().contains("/cmd/")){
				return cmd.replace(cmdForSearch, path);
			}		
		}
		return cmd.replace(cmdForSearch, cmdPathArr[cmdPathArr.length - 1]);
	}

	public boolean isSshConected() throws NullPointerException{
		return session.isConnected();
	}

	@Override
	public String toString() {
		return "JSSHClient [host=" + host + ", user=" + user + ", password=" + password + "]";
	}

}
