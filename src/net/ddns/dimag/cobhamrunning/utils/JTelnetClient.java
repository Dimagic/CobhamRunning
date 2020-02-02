package net.ddns.dimag.cobhamrunning.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.ddns.dimag.cobhamrunning.view.TestsViewController;
import org.apache.commons.net.telnet.TelnetClient;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;

public class JTelnetClient extends Observable {
    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    private String prompt = "$";
    private TestsViewController controller;
    StringProperty val = new SimpleStringProperty();

    public JTelnetClient(TestsViewController controller, String server, String user, String password) throws Exception {
        try {
        	this.controller = controller;
            // Connect to the specified server
        	telnet.setSocketFactory(new TimeoutSockectFactory());
            telnet.setDefaultTimeout(1000);
            telnet.connect(server, 23);

            // Get input and output stream references
            in = telnet.getInputStream();
            out = new PrintStream(telnet.getOutputStream());

            // Log the user on
            readUntil("login: ");
            write(user);
            readUntil("Password: ");
            write(password);

            // Advance to a prompt
            readUntil(prompt + " ");
        } catch (Exception e) {
        	throw e;
        }
    }

    public void su(String password) throws Exception {
        try {
            write("su");
            readUntil("Password: ");
            write(password);
            prompt = "$";
            readUntil(prompt + " ");
        } catch (Exception e) {
            throw e;
        }
    }

    @SuppressWarnings("finally")
	public String readUntil(String pattern) throws Exception {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
//            boolean found = false;
            char ch = (char) in.read();

            while (true) {
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                    	controller.writeConsole(sb.toString());
                    	System.out.println(sb.toString());
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            throw e;
        } finally {
        	return null;
        }
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
        } catch (Exception e) {
            throw e;
        }
    }

    public String sendCommand(String command) {
        try {
            write(command);
            return readUntil(prompt + " ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void disconnect() {
        try {
            telnet.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TimeoutSockectFactory extends SocketFactory {
      public Socket createSocket(String hostname, int port) throws IOException
      {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostname, port), 1000);
        return socket;
      }

      public Socket createSocket(InetAddress hostAddress, int port) throws IOException
      {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(hostAddress, port), 1000);
        return socket;
      }

      public Socket createSocket(String remoteHost, int remotePort, InetAddress localAddress, int localPort) throws IOException
      {
        return new Socket();
      }

      public Socket createSocket(InetAddress remoteAddress, int remotePort, InetAddress localAddress, int localPort) throws IOException
      {
        return new Socket();
      }

      public ServerSocket createServerSocket(int port) throws IOException
      {
        return new ServerSocket();
      }

      public ServerSocket createServerSocket(int port, int backlog) throws IOException
      {
        return new ServerSocket();
      }

      public ServerSocket createServerSocket(int port, int backlog, InetAddress bindAddress) throws IOException
      {
        return new ServerSocket();
      }
    }
}
