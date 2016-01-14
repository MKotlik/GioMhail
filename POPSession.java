/* GioMhail by Coolgle
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class POPSession {
    private String POPHost;
    private int POPPort;

    //Console communication
    private static BufferedReader sysIn;//Read from console
    private static PrintStream sysOut; //Print to console

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
        sysIn = new BufferedReader(new InputStreamReader(System.in));
        sysOut = System.out;
    }

    //Secure connect and start handshake
    public boolean connect() {
        try {
            secureSocket = (SSLSocket) mainFactory.createSocket(POPHost, POPPort);
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            String serverInput = read(false);
            return checkOK(serverInput);
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Use writeServer from SMTPConsole (copy the code) to send messages to server
    public boolean login(String user, String pass) {
        String serverInput = null;
        writeServer("user " + user);
        serverInput = read(false);
        if (!checkOK(serverInput)) {
            return false;
        }
        writeServer("pass " + pass);
        serverInput = read(false);
        return checkOK(serverInput);
    }

    //This could probably be replaced by a different command, such as last or stat
    public int getMessageCount() {//returns amount of messages in inbox
        writeServer("list");
        String serverInput = read(true);
        int end = 0;
        for (int i = 4; i < serverInput.length(); i++) {
            if (serverInput.substring(i, i + 1).equals(" ")) {
                end = i;
            }
        }
        return Integer.parseInt(serverInput.substring(4, end));
    }

    public boolean delete(int messageNum) {//deletes specified message
        writeServer("dele " + messageNum);
        String serverInput = read(false);
        return checkOK(serverInput);
    }

    public String retrieve(int messageNum) {
        writeServer("retr " + messageNum);
        String message = read(true);
        return message;
    }

    //Close all connections
    private void close() {
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
            if (sysIn != null) {
                sysIn.close();
            }
            if (sysOut != null) {
                sysOut.close();
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private boolean checkResponseCode(String response, String code) {//makes sure client is recieveing correct response
        int length = code.length();
        return (response != null && response.length() >= 3 && response.substring(0, length).equals(code));
    }

    private boolean checkOK(String response) { //Check if response starts with +OK
        return (response != null && response.length() >= 3 && response.substring(0, 3).equals("+OK"));
    }

	/*
    private boolean checkERR(String response) { //Check if response starts with -ERR
		return (response != null && response.length() >= 3 && response.substring(0,4).equals("-ERR"));
	}
	*/

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
    public String read(boolean multi) {
        String message = null;
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
                    message += serverInput;
                    //Check if multiline or if error
                    tryRead = !(serverInput.equals(".") || checkResponseCode(serverInput, "-ERR"));
                }
            } else {
                message = serverReader.readLine();
            }
            return message;
        } catch (IOException e) {
            System.err.println(e.toString());
            return null; //NOTE, implementations need to check that message is not null
        }
    }
}
