/* GioMhail by Coolgle
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

/* TODO
 - [DONE] private boolean AuthPlain()
 - [DONE] private boolean AuthLogin()
 - [DONE] private boolean UserPassLogin()
 - [DONE] Modify login() to reflect changes
 - [DONE] public boolean isConnected() (based on isClosed of socket)
 - public boolean disconnect() (convert from close())
 - public boolean reconnect() ??? (rather than making new socket, call connect() and handshake() on existing?)
 - [DONE] remove sysIn and sysOut
 - Comment all methods (at least w/ function headers)
 - organize code (group methods)

 MAJOR:
 - Create new class Session, transfer common SMTP & POP methods/variables to it
 - Create new class Headers, acting as container for message headers
 - Create new class Message, acting as container for a Headers obj and body
 - Modify retrieve & getHeader to return a Message and a Headers respectively
 - Modify read to return String array or arrayList
 - Modify all methods using read to support new return method
 */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;

public class POPSession {
    private String POPHost;
    private int POPPort;

    //Socket-related
    private SSLSocketFactory mainFactory; //Main secureSocket factory
    private SSLSocket secureSocket = null;
    private BufferedReader serverReader = null; //Read from server
    private BufferedWriter serverWriter = null; //Write to server

    //Debug Setting
    private boolean debugP = true; //print client updates to console

    //Session constructor
    public POPSession(int port, String host) {
        POPPort = port;
        POPHost = host;
        mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    //Secure connect and start handshake
    public boolean connect() {
        try {
            secureSocket = (SSLSocket) mainFactory.createSocket(POPHost, POPPort);
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            ArrayList<String> serverInput = read(false);
            return checkOK(serverInput.get(0));
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Returns whether or not the socket is open
    //This implies, by architecture, that connection is open and all streams, writers, & readers are open
    public boolean isConnected() {
        return (secureSocket != null && !secureSocket.isClosed());
    }

    //Attempts different auth/login methods
    public boolean login(String user, String pass) {
        if (AuthPlain(user, pass)) {
            return true; //else if AUTH PLAIN unsupported
        } else if (AuthLogin(user, pass)){
            return true; //else if AUTH LOGIN unsupported
        } else if (loginUserPass(user, pass)) {
            return true; //always works, true if user/pass combo correct
        } else {
            return false; //user/pass combo must be wrong
        }
    }

    //Logs in using AUTH PLAIN
    private boolean AuthPlain(String user, String pass) {
        String encodedPlain = encodeBase64("\0" + user + "\0" + pass);
        if (encodedPlain.equals("ENCODING_ERROR")) {
            return false;
        }
        writeServer("AUTH PLAIN " + encodedPlain);
        ArrayList<String> serverInput = read(false);
        return checkOK(serverInput.get(0)); //Simplify as checkOK(read(false));
    }

    //Logs in using AUTH LOGIN
    private boolean AuthLogin(String user, String pass) {
        String encodedLogin = encodeBase64(user);
        String encodedPass = encodeBase64(pass);
        //Check if encoded properly
        if (encodedLogin.equals("ENCODING_ERROR") || encodedPass.equals("ENCODING_ERROR")) {
            return false;
        }
        writeServer("AUTH LOGIN"); //Confirm AUTH LOGIN capa, start login
        ArrayList<String> serverInput = read(false);
        if (checkERR(serverInput.get(0))) { //ERR if unsupported
            return false;
        }
        writeServer(encodedLogin); //Send username
        serverInput = read(false);
        if (checkERR(serverInput.get(0))) { //ERR if username in bad format
            return false;
        }
        writeServer(encodedPass); //Send password
        serverInput = read(false);
        //Simplify as checkOK(read(false));
        return checkOK(serverInput.get(0)); //ERR if password in bad format or incorrect user/pass
    }

    //Logs in using unencrypted USER & PASS cmds
    private boolean loginUserPass(String user, String pass) {
        writeServer("USER " + user);
        ArrayList<String> serverInput = read(false);
        if (checkERR(serverInput.get(0))) {
            return false;
        }
        writeServer("PASS " + pass);
        serverInput = read(false);
        return checkOK(serverInput.get(0));
    }

    public int getMessageCount() {//returns amount of messages in inbox
        writeServer("STAT");
        ArrayList<String> serverInput = read(false);
        int end = 0;
        for (int i = 4; i < serverInput.get(0).length(); i++) {
            if (serverInput.get(0).substring(i, i + 1).equals(" ")) {
                end = i;
            }
        }
        return Integer.parseInt(serverInput.get(0).substring(4, end));
    }

    public boolean delete(int messageNum) {//deletes specified message
        writeServer("DELE " + messageNum);
        ArrayList<String> serverInput = read(false);
        return checkOK(serverInput.get(0));
    }

    public Header getHeader(int messageNum) {
        writeServer("TOP " + messageNum + " 0");
        ArrayList<String> serverInput = read(true);
	if (checkOK(serverInput.get(0))){
	    Header h=new Header(serverInput);
        } else {
            writeServer("RETR " + messageNum);
            serverInput = read(true);
            if (checkOK(serverInput.get(0))) {
                for (int i = 0; i < serverInput.size(); i++) {
                    if (serverInput.get(i).equals("\n")) {
			serverInput=serverInput.removeRange(i,serverInput.size());
                        Header h=new Header(serverInput);
		    }
                }
            }
        }
    }
    public ArrayList<Header> getHeaderList(int numMessages){
	ArrayList<Header> inbox=new ArrayList();
	for(int i=getMessageCount()-numMessages;i<=getMessageCount();i++){
	    inbox.add(getHeader(i));
	}
	return inbox;
    }
    public ArrayList<String> retrieve(int messageNum) {
        writeServer("RETR " + messageNum);
        ArrayList<String> message = read(true);
        return message;
    }

    //Close all connections
    public void close() {
        try {
            if (serverWriter != null) {
                serverWriter.close();
            }
            if (serverReader != null) {
                serverReader.close();
            }
            //Note, no plainSocket for POP, only secureSocket
            if (secureSocket != null) {
                secureSocket.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private boolean checkResponseCode(String response, String code) {//makes sure client is recieveing correct response
        return (response != null && response.startsWith(code));
    }

    private boolean checkOK(String response) { //Check if response starts with +OK
        return (response != null && response.startsWith("+OK"));
    }

    private boolean checkERR(String response) { //Check if response starts with -ERR
        return (response != null && response.startsWith("-ERR"));
    }

    //Convert plaintext UTF-8 string to base64 string
    //Confirmed working! (Encodes correctly in practice)
    private String encodeBase64(String decodedStr) {
        try {
            byte[] decodedBytes = decodedStr.getBytes("UTF-8");
            String encodedStr = DatatypeConverter.printBase64Binary(decodedBytes);
            return encodedStr;
        } catch (UnsupportedEncodingException e) { //Exception thrown by getBytes(...)
            return "ENCODING_ERROR";
        }
    }

    private boolean writeServer(String userLine) {//writes specified userLine to the server
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

    //reads server responses, can read multiline or single line responses depending on value of multi
    private ArrayList<String> read(boolean multi) {
        ArrayList<String> message = new ArrayList();
        boolean tryRead = true;
        String serverInput = null;
        try {
            if (multi) {
                while (tryRead) {
                    serverInput = serverReader.readLine();
                    if (serverInput == null) { //If serverReader gets closed/connection broken
                        tryRead = false;
                        break;
                    }
                    message.add(serverInput);
                    //Check if multiline or if error
                    tryRead = !(serverInput.equals(".") || checkERR(serverInput));
                }
            } else {
                message.add(serverReader.readLine());
            }
            return message;
        } catch (IOException e) {
            System.err.println(e.toString());
            return null; //NOTE, implementations need to check that message is not null
        }
    }
}

/*Deprecated code
//LAST & unread related commands
//LAST is rarely supported by servers
//No method of unread tracking unless client stores data locally

    public int unreadMessages(){
            return getMessageCount()-getLastAccessedNumber();
    }

    public int getLastAccessedNumber(){
        writeServer("last");
        String serverInput=read(false);
        int end=0;
            for (int i = 4; i < serverInput.length(); i++) {
                if (serverInput.substring(i, i + 1).equals(" ")) {
                    end = i;
                }
            }
            return Integer.parseInt(serverInput.substring(4, end));
        }
 */
