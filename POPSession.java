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
            String serverInput = null;
	    writeServer("user " + user);
            serverInput = read(false);
            if (! checkResponseCode(serverInput,"+OK")) {
                return false;
            }
	    writeServer("pass " + pass);
            serverInput = read(false);
            if (! checkResponseCode(serverInput,"+OK")) {
                return false;
            }
            return true;
    }

    public int getMessageCount() {//returns amount of messages in inbox
        writeServer("list");
	String serverInput=read(true);
	int end=0;
	for (int i=5;i<serverInput.length();i++){
	    if (serverInput.substring(i,i+1).equals(" ")){
		end =i;
	    }
	}
	return Integer.parseInt(serverInput.substring(5,end));
    }
    
    public boolean delete(int messageNum){//deletes specified message
	writeServer("dele "+messageNum);
	String serverInput=read(false);
	return checkResponseCode(serverInput, "+OK");
    }

    public String retrieve(int messageNum){
	writeServer("retr "+messageNum);
	String message=read(true);
	return message;
    }
	
    public void close() {//closses all connections
	try{
	    serverReader.close();
	    serverWriter.close();
	    secureSocket.close();
	}catch(IOException e){
	    System.err.println(e.toString());
	}
    }

    private boolean checkResponseCode(String response, String code){//makes sure client is recieveing correct response
        return (response != null && response.length() >= 3 && response.substring(0,3).equals(code));
    }

    private boolean writeServer(String userLine){//writes specified userLine to the server
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
    public String read(boolean multi){//reads server responses, can read multiline or single line responses depending on value of multi
	String message=null;
	boolean tryRead=true;
	String serverInput=null;
	try{
	    if (multi){
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
	    }else{
		message=serverReader.readLine();
	    }
	}catch(IOException e){
	    System.err.println(e.toString());
	}
	return message;
    }
}
