/* GioMhail by Coolgle
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */
import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class POPSession {
    //Copy the variable declarations from SMTPConsole, you'll need all of them anyway
    //Just note that userInput and serverInput need to get defined separately in every method that would use them
    //As in, don't define them as globals
    private String POPHost;
    private int POPPort;

    //Console communication
    private static BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
    private static PrintStream sysOut = System.out; //Print to console

    //Socket-related
    private SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket secureSocket = null;
    private BufferedReader serverReader = null; //Read from server
    private BufferedWriter serverWriter = null; //Write to server

    public POPSession(int port, String host) {
        POPPort = port;
        POPHost = host;
    }

    public boolean connect() {
	SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault(); //Get default SSL socket factory
        try {
            SSLSocket secureSocket = (SSLSocket) mainFactory.createSocket(POPHost, POPPort); //create, connect, start handshake
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream())); //Write to server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream())); //Read from server
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
	return true;
    }

    //Use writeServer from SMTPConsole (copy the code) to send messages to server
    public boolean login(String user, String pass) {
        try {
            String serverInput = null;
	    writeServer("user " + user);
            serverInput = serverReader.readLine();
            if (! checkResponseCode(serverInput,"+OK")) {
                return false;
            }
	    writeServer("pass " + pass);
            serverInput = serverReader.readLine();
            if (! checkResponseCode(serverInput,"+OK")) {
                return false;
            }
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public int getMessageCount() {
        writeServer("list");
	String message=null;
	boolean tryRead=true;
	String serverInput=null;
	try{
	    while (tryRead) {
		serverInput = serverReader.readLine();
		if (serverInput == null) { //If serverReader gets closed/connection broken
		    tryRead = false;
		    break;
		}
		message+=serverInput;
		if (serverInput.equals(".")) { //Check if multiline response
		    tryRead = false;
		} else {
		    tryRead = true;
		}
	    }
	}catch(IOException e){
	    System.err.println(e.toString());
	}
	return message.charAt(4)-48;		
    }

    public void close() {
	try{
	    serverReader.close();
	    serverWriter.close();
	    secureSocket.close();
	}catch(IOException e){
	    System.err.println(e.toString());
	}
    }

    private boolean checkResponseCode(String response, String code){
        return (response != null && response.length() >= 3 && response.substring(0,3).equals(code));
    }

    private boolean writeServer(String userLine){
        try {
            serverWriter.write(userLine, 0, userLine.length()); //Writing to server
            serverWriter.newLine();
            serverWriter.flush();
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
