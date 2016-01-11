//GioMhail by Coolgle
//Misha Kotlik & Gio Topa
//SSLSocketClient test
//Based on work of author attributed below

/* SslSocketClient.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */
 
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
public class SSLSocketClient {
	public static void main(String[] args) {
		//java.lang.System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
		//Exerimenting with no_renegotiation error
		BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
		PrintStream sysOut = System.out;
		SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		try {
			SSLSocket clientSocket = (SSLSocket) mainFactory.createSocket("smtp.gmail.com", 465);
			printSocketInfo(clientSocket);
			//clientSocket.startHandshake(); //This has been causing no_renegotiation error
			//Apparently handshake is automatically started after connection
			printConnectionInfo(clientSocket);
			BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			InputStream clientInputStream = clientSocket.getInputStream();
			BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			/*
			String m = null;
			while ((m=r.readLine())!= null) {
				out.println(m);
				m = in.readLine();
				w.write(m,0,m.length());
				w.newLine();
				w.flush();
			}
			*/
			String serverInput;
			String userInput = "";
			boolean tryRead = true;
			boolean quitUser = false;
			boolean openRead = true;
			boolean openSocket = true;
			while (openSocket && openRead && ! quitUser) {
			    if (clientSocket == null) {
				openSocket = false;
				break;
			    }
			    //Display server response/message
			    while (tryRead) {
				serverInput = serverReader.readLine();
				if (serverInput == null) {
				    openRead = false;
				    break;
				}
				sysOut.println(serverInput);
				if (serverInput.substring(3,4).equals("-")) {
				    tryRead = true;
				} else {
				    tryRead = false;
				}
			    }
			    if (openSocket == false || openRead == false) {
				break;
			    }
			    //Read user input, display prompt if blank enter, otherwise send to server
			    while (userInput.equals("")) {
				sysOut.print("C: ");
				userInput = sysIn.readLine();
			    }
			    serverWriter.write(userInput, 0, userInput.length());
			    serverWriter.newLine();
			    serverWriter.flush();
			    userInput = "";
			    tryRead = true;
			}
			/*
			while ((serverInput = serverReader.readLine()) != null && clientSocket != null) {
				sysOut.println(serverInput);
				//sysOut.println("C: ");
				userInput = sysIn.readLine();
				if (! userInput.equals("")) {
				    serverWriter.write(userInput, 0, userInput.length());
				    serverWriter.newLine();
				    serverWriter.flush();
				    System.out.println("Sent something");
				} 
			}
			*/
			serverWriter.close();
			serverReader.close();
			clientSocket.close();
			sysIn.close();
			sysOut.close();
		} catch (IOException e) {
			System.err.println(e.toString());
		}
    }
   private static void printSocketInfo(SSLSocket s) {
      System.out.println("Socket class: "+s.getClass());
      System.out.println("   Remote address = "
         +s.getInetAddress().toString());
      System.out.println("   Remote port = "+s.getPort());
      System.out.println("   Local socket address = "
         +s.getLocalSocketAddress().toString());
      System.out.println("   Local address = "
         +s.getLocalAddress().toString());
      System.out.println("   Local port = "+s.getLocalPort());
      System.out.println("   Need client authentication = "
         +s.getNeedClientAuth());
      SSLSession ss = s.getSession();
      System.out.println("   Cipher suite = "+ss.getCipherSuite());
      System.out.println("   Protocol = "+ss.getProtocol());
   }
   
   private static void printConnectionInfo(SSLSocket s) {
	   SSLSession currentSession = s.getSession();
	   System.out.println("Protocol: " + currentSession.getProtocol());
	   System.out.println("Cipher Suite: " + currentSession.getCipherSuite());
	   System.out.println("Host: " + currentSession.getPeerHost());
	   System.out.println("Host Port: " + currentSession.getPeerPort());
	   
}
   }
