/* GioMhail by Coolgle
 - Session
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
 - [DONE] Copy all necessary/shared methods and variables from POPSession
 - [DONE] Modify methods for interoperability with SMTPSession
 - [DONE] Modify login
 - [DONE] Store user and pass
 - [DONE] Add user and pass methods
 - Change API methods to return known String statuses based on errors/successes
 */

/* CODE STRUCTURE
 - Instance vars (server, login, socket, debug)
 - Constructor
 - Connection methods
 - Login methods
 - Response methods
 - Read & Write methods
 */

import java.util.*;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import javax.xml.bind.DatatypeConverter;

public class Session {
    //Server-related
    private String host;
    private int port;

    //Login related
    private String user;
    private String pass;

    //Socket-related
    private SSLSocketFactory mainFactory; //Main secureSocket factory
    private SSLSocket secureSocket = null;
    private BufferedReader serverReader = null; //Read from server
    private BufferedWriter serverWriter = null; //Write to server
    private String protocol; //Protocol type

    //Debug Setting
    private boolean debugP = true; //print client updates to console

    //Session constructor
    public Session(String host, int port, String prot) {
        this.host = host;
        this.port = port;
        protocol = prot;
        user = "";
        pass = "";
        mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    }

    //-----CONNECTION-----

    //Connect to POP/SMTP server based on port
    public boolean connect() {
        if ((port == 465) || (port == 995)) {
            // SSL/TLS port for SMTP or POP
            return secureConnect();
        } else if ((port == 587) || (port == 2525) || (port == 25)) {
            //Unsec. SMTP (587, 2525, 25)
            return plainSMTPConnect();
        } else if (port == 110){
            //Unsec. POP (110)
            return plainPOPConnect();
        } else{ //Unsupported port
            return false;
        }
    }

    //Attempt to make secure SSL/TLS connection
    private boolean secureConnect() {
        try {
            secureSocket = (SSLSocket) mainFactory.createSocket(host, port);
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            ArrayList<String> serverInput = read(false);
            if (protocol.equals("POP")) { //Connected to POP server
                return checkOK(serverInput.get(0));
            } else { //Connected to SMTP server
                return checkResponseCode(serverInput.get(0), "220");
            }
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    private boolean plainPOPConnect() {
        //Attempt to make plain socket & start TLS negotiation
        try {
            Socket plainSocket = new Socket(host, port);
            serverReader = new BufferedReader(new InputStreamReader(plainSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(plainSocket.getOutputStream()));
            //Check for server greeting
            ArrayList<String> serverInput = read(false);
            if (!checkOK(serverInput.get(0))) { //If server didn't greet
                return false;
            }

            //Send STARTTLS to server
            writeServer("STARTTLS");
            //Check for STARTTLS response
            serverInput = read(false);
            if (!checkOK(serverInput.get(0))) { //If server didn't accept STARTTLS
                return false;
            }

            //Upgrade connection
            secureSocket = (SSLSocket) mainFactory.createSocket(
                    plainSocket,
                    plainSocket.getInetAddress().getHostAddress(),
                    plainSocket.getPort(),
                    true);
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));

            //Check if connected to server correctly
            serverInput = read(false);
            return checkOK(serverInput.get(0));
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Make an unsecured SMTP connection and attempt upgrade
    private boolean plainSMTPConnect() {
        //Attempt to make plain socket & start TLS negotiation
        try {
            Socket plainSocket = new Socket(host, port);
            serverReader = new BufferedReader(new InputStreamReader(plainSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(plainSocket.getOutputStream()));
            //Check for server greeting
            ArrayList<String> serverInput = read(false);
            if (!checkResponseCode(serverInput.get(0), "220")) { //If server didn't greet
                return false;
            }

            //Send HELO to server
            writeServer("HELO " + host);
            //Check for HELO response
            serverInput = read(false);
            if (!checkResponseCode(serverInput.get(0), "250")) { //Server didn't answer HELO correctly
                return false;
            }

            //Send STARTTLS to server
            writeServer("STARTTLS");
            //Check for STARTTLS response
            serverInput = read(false);
            if (!checkResponseCode(serverInput.get(0), "220")) { //If server didn't accept STARTTLS
                return false;
            }

            //Upgrade connection
            secureSocket = (SSLSocket) mainFactory.createSocket(
                    plainSocket,
                    plainSocket.getInetAddress().getHostAddress(),
                    plainSocket.getPort(),
                    true);
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));

            //Send EHLO to server over upgraded connection
            writeServer("EHLO " + host);
            serverInput = read(true); //EHLO has multiline capa output
            return checkResponseCode(serverInput.get(0), "250"); //Output whether connection success
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

    //Send quit command, check response
    //Close resources if true
    public boolean disconnect() {
        writeServer("QUIT");
        ArrayList<String> serverInput = read(false);
        if ((protocol.equals("POP") && checkOK(serverInput.get(0))) ||
                (protocol.equals("SMTP") && checkResponseCode(serverInput.get(0), "221"))) {
            close();
            return true;
        } else {
            return false;
        }
    }

    //Close all connections/resources
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

    //-----LOGIN-----

    //Attempts different auth/login methods for POP
    public boolean POPLogin() {
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

    //Attempts different auth/login methods for SMTP
    //SMTP doesn't support user/pass login w/o auth
    public boolean SMTPLogin() {
        if (AuthPlain(user, pass)) {
            return true; //else if AUTH PLAIN unsupported
        } else if (AuthLogin(user, pass)) {
            return true; //else if AUTH LOGIN unsupported
        } else {
            return false; //user/pass combo must be wrong
        }
    }

    //Logs in using AUTH PLAIN (POP & SMTP)
    //Returns true if AUTH PLAIN supported and user/pass correct
    //Returns false if AUTH PLAIN unsupported or user/pass wrong
    private boolean AuthPlain(String user, String pass) {
        String encodedPlain = encodeBase64("\0" + user + "\0" + pass);
        //DEBUG NOTE, I confirmed that this is encoding correctly, compared output to known Yahoo pass.
        if (encodedPlain.equals("ENCODING_ERROR")) {
            return false;
        }
        writeServer("AUTH PLAIN " + encodedPlain);
        ArrayList<String> serverInput = read(false);
        if (protocol.equals("POP")) {
            return checkOK(serverInput.get(0));
        } else { //SMTP
            return checkResponseCode(serverInput.get(0), "235"); //SMTP auth acceptance code is 235
        }
    }

    //Logs in using AUTH LOGIN (POP & SMTP)
    //Returns true if AUTH LOGIN supported and user/pass correct
    //Returns false if AUTH LOGIN unsupported or user/pass wrong
    private boolean AuthLogin(String user, String pass) {
        String encodedLogin = encodeBase64(user);
        String encodedPass = encodeBase64(pass);
        //Check if encoded properly
        if (encodedLogin.equals("ENCODING_ERROR") || encodedPass.equals("ENCODING_ERROR")) {
            return false;
        }
        writeServer("AUTH LOGIN"); //Confirm AUTH LOGIN capa, start login
        ArrayList<String> serverInput = read(false);
        if (protocol.equals("POP")) {
            if (checkERR(serverInput.get(0))) { //ERR if unsupported
                return false;
            }
        } else { //SMTP
            if (! checkResponseCode(serverInput.get(0), "334")) { //If not code 334, auth login unsupported
                return false;
            }
        }
        writeServer(encodedLogin); //Send username
        serverInput = read(false);
        if (protocol.equals("POP")) {
            if (checkERR(serverInput.get(0))) { //ERR if username bad format
                return false;
            }
        } else { //SMTP
            if (! checkResponseCode(serverInput.get(0), "334")) { //If not code 334, username bad format
                return false;
            }
        }
        writeServer(encodedPass); //Send password
        serverInput = read(false);
        //Simplify as checkOK(read(false));
        if (protocol.equals("POP")) {
            return checkOK(serverInput.get(0));
        } else { //SMTP
            return checkResponseCode(serverInput.get(0), "235"); //SMTP auth acceptance code is 235
        }
    }

    //Logs in using unencrypted USER & PASS cmds (POP)
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

    //Get username
    public String getUser() {
        return user;
    }

    //Set username
    public void setUser(String newUser) {
        user = newUser;
    }

    //Get password
    public String getPass() {
        return pass;
    }

    //Set password
    public void setPass(String newPass) {
        pass = newPass;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    //-----RESPONSE CHECKING-----

    //Checks server response code
    protected boolean checkResponseCode(String response, String code) {
        return (response != null && response.startsWith(code));
    }

    //Check if response starts with +OK
    protected boolean checkOK(String response) {
        return (response != null && response.startsWith("+OK"));
    }

    //Check if response starts with -ERR
    protected boolean checkERR(String response) {
        return (response != null && response.startsWith("-ERR"));
    }

    //-----READ & WRITE-----

    //Reads server response, returns ArrayList<String> with response lines
    protected ArrayList<String> read(boolean multi) {
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
                    if (protocol.equals("POP")) {
                        tryRead = !(serverInput.equals(".") || checkERR(serverInput));
                    } else { //SMTP
                        tryRead = serverInput.length() > 3 &&
                                serverInput.substring(3, 4).equals("-"); //SMTP multiline responses have - after code
                    }
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

    //writes specified userLine to the server
    protected boolean writeServer(String userLine) {
        try {
            serverWriter.write(userLine, 0, userLine.length()); //Writing to server
            //serverWriter.newLine();
            serverWriter.write("\r\n"); //Always writes a <CRLF>
            serverWriter.flush();
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }
}
