package net.ddns.dimag.cobhamrunning.utils;

import java.io.InputStream;
import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Platform;
import net.ddns.dimag.cobhamrunning.MainApp;
import net.ddns.dimag.cobhamrunning.view.TestsViewController;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSSHClient implements MsgBox, SystemCommands {
	private static final Logger LOGGER = LogManager.getLogger(JSSHClient.class.getName());
	private MainApp mainApp;
	private String host;
	private String user;
	private String password;
	private JSch jsch;
	private Session session;
	private TestsViewController controller;
	private Channel channel;

	public JSSHClient(String host, String user, String password, MainApp mainApp) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.controller = mainApp.getController();
	}

	public HashMap<String, String> send(String command) throws CobhamRunningException {
		HashMap<String, String> result = new HashMap<String, String>();
		try {
			command = findPathCmd(command);
			session = getSession();
			writeConsole(command);
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
		} catch (Exception e) {
			LOGGER.error(e);
			throw new CobhamRunningException(e);
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

	public boolean isSshConected() throws CobhamRunningException {
		try {
			session = getSession();
		} catch (JSchException e) {
			throw new CobhamRunningException(e);
		}
		return session.isConnected();
	}

	public <K, V> Map<K, V> cobhamGetSwv() throws CobhamRunningException {
		List<String> keys = Arrays.asList("system", "common", "target");
		String tmp = send(swvCmd).get("result");
		List<String> resList = Arrays.asList(tmp.replace("\" \"", "_")
				.replace("\"", "").split("_"));
		return (Map<K, V>) zipToMap(keys, resList);
	}

	public String cobhamGetAsis() throws CobhamRunningException {
		String tmp = send("serialdump").get("result");
		return StringUtils.substringBetween(tmp, "TARGET=", " ");
	}

	private <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
		return IntStream.range(0, keys.size()).boxed()
				.collect(Collectors.toMap(keys::get, values::get));
	}

	public void writeConsole(String val) {
		Platform.runLater(() -> {
			controller.writeConsole(val);
		});
	}

	@Override
	public String toString() {
		return "JSSHClient [host=" + host + ", user=" + user + ", password=" + password + "]";
	}

}
