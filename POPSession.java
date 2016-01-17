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
   - [DONE] public boolean disconnect() (convert from close())
   - public boolean reconnect() ??? (rather than making new socket, call connect() and handshake() on existing?)
   - Implememt server connection checks in write/read functions
   - Implement exception throwing/catching (needs to work with Client's simplified error output)
   - [DONE] remove sysIn and sysOut
   - Comment all methods (at least w/ function headers)
   - organize code (group methods)

   MAJOR:
   - Create new class Session, transfer common SMTP & POP methods/variables to it
   - Create new class Header, acting as container for message header
   - Create new class Message, acting as container for a Header obj and body
   - [DONE] Modify retrieve & getHeader to return a Message and a Header respectively
   - [DONE] Create getHeaderList and getMessageList to return ArrayList<Header> and ArrayList<Messages>
   - [DONE] Modify read to return ArrayList<String>
   - [DONE] Modify all methods using read to support new return method
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
        } else if (AuthLogin(user, pass)) {
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

    //Uses STAT to get number of messages in inbox
    public int getMessageCount() {
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

    //Deletes a message specified by num in inbox
    public boolean delete(int messageNum) {
        writeServer("DELE " + messageNum);
        ArrayList<String> serverInput = read(false);
        return checkOK(serverInput.get(0));
    }

    //Returns ArrayList of the latest numMessages of Headers
    public ArrayList<Header> getHeaderList(int numMessages) {
        int totalMsgs = getMessageCount();
        return getHeaderList(totalMsgs - numMessages, totalMsgs);
    }

    //Returns ArrayList of Headers between minMsg and maxMsg, inclusive
    public ArrayList<Header> getHeaderList(int minMsg, int maxMsg) {
        ArrayList<Header> HeaderList = new ArrayList<Header>();
        for (int i = minMsg; i <= maxMsg; i++) {
            HeaderList.add(getHeader(i));
        }
        return HeaderList;
    }

    //Gets and returns a Header object for an email by num in inbox
    //Attempts to use TOP, then RETR
    //Returns null if error
    public Header getHeader(int messageNum) {
        //Attempt TOP for header
        writeServer("TOP " + messageNum + " 0");
        ArrayList<String> serverInput = read(true);
        if (serverInput == null) {
            return null; //Return null if error in read
        }
        if (checkOK(serverInput.get(0))) { //If TOP supported, create Header object
            trimHeader(serverInput);
            Header retHeader = new Header(serverInput);
            return retHeader;
        } else { //Use RETR if TOP unsupported by server
            //Attempt RETR for header
            writeServer("RETR " + messageNum);
            serverInput = read(true);
            if (serverInput == null) {
                return null; //Return null if error in read
            }
            if (checkOK(serverInput.get(0))) {
                trimHeader(serverInput);
                Header retHeader = new Header(serverInput);
                return retHeader;
            } else {
                return null; //Return null if TOP and RETR failed. Client must check.
            }
        }
    }

    //Trims TOP & RETR responses down to the header
    //Removes +OK lines and any lines following last header attribute
    //Should just modify the argument, with no need for returning
    private void trimHeader(ArrayList<String> longResponse) {
        //Trim first line if +OK ...
        if (longResponse.get(0).startsWith("+OK")) {
            longResponse.remove(0);
        }
        //Starting from first line/index, find blank line and remove all lines from it to end
        int i = 0;
        boolean trimmed = false;
        while (!trimmed && i < longResponse.size() - 1) {
            if (longResponse.get(i).equals("\n") || longResponse.get(i).equals("\r\n")) {
                longResponse.removeRange(i, longResponse.size());
                trimmed = true;
            }
            i++;
        }
        //Just in case, check if last line is "." and trim accordingly
        if (longResponse.get(longResponse.size() - 1).equals(".")) {
            longResponse.remove(longResponse.size() - 1);
        }
    }

    //Returns ArrayList of the latest numMessages of Messages
    public ArrayList<Message> getMessageList(int numMessages) {
        int totalMsgs = getMessageCount();
        return getMessageList(totalMsgs - numMessages, totalMsgs);
    }

    //Returns ArrayList of Messages between minMsg and maxMsg, inclusive
    public ArrayList<Message> getMessageList(int minMsg, int maxMsg) {
        ArrayList<Message> MessageList = new ArrayList<Message>();
        for (int i = minMsg; i <= maxMsg; i++) {
            MessageList.add(getMessage(i));
        }
        return MessageList;
    }

    //Returns Message object for email specified by messageNum using RETR
    public Message getMessage(int messageNum) {
        writeServer("RETR " + messageNum);
        ArrayList<String> serverInput = read(true);
        if (checkERR(serverInput.get(0))) {
            return null; //return null if error in read/getting email
        }
        trimMessage(serverInput);
        Message retEmail = new Message(serverInput);
        return retEmail;
    }

    //Trims RETR response down to header attributes and message body
    //Removes first +OK line and last . line
    private void trimMessage(ArrayList<String> longMsg) {
        if (longMsg.get(0).startsWith("+OK")) {
            longMsg.remove(0);
        }
        if (longMsg.get(longMsg.size() - 1).equals(".")) {
            longMsg.remove(longMsg.size() - 1);
        }
    }

    //Send quit command, check response
    //Close resources if true
    public boolean disconnect() {
        writeServer("QUIT");
        ArrayList<String> serverInput = read(false);
        if (checkOK(serverInput.get(0))) {
            close();
            return true;
        } else {
            return false;
        }
    }

    //Close all connections/resources
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
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    //Checks server response code
    private boolean checkResponseCode(String response, String code) {
        return (response != null && response.startsWith(code));
    }

    //Check if response starts with +OK
    private boolean checkOK(String response) {
        return (response != null && response.startsWith("+OK"));
    }

    //Check if response starts with -ERR
    private boolean checkERR(String response) {
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

    //writes specified userLine to the server
    private boolean writeServer(String userLine) {
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

    //Reads server response, returns ArrayList<String> with response lines
    private ArrayList<String> read(boolean multi) {
        ArrayList<String> response = new ArrayList<String>();
        boolean tryRead = true;
        String serverInput = null;
        try {
            if (multi) {
                while (tryRead) {
                    serverInput = serverReader.readLine();
                    if (serverInput == null) { //If serverReader gets closed/connection broken
                        tryRead = false;
                        break;
                        //CHANGE to return null?
                    }
                    response.add(serverInput);
                    //Check if multiline or if error
                    tryRead = !(serverInput.equals(".") || checkERR(serverInput));
                }
            } else {
                response.add(serverReader.readLine());
                //Modify this to return null if closed connection?
            }
            return response;
        } catch (IOException e) {
            System.err.println(e.toString());
            return null; //NOTE, implementations need to check that message is not null
            //Could also add "READ ERROR" to arraylist and check for that
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
