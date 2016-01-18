/* GioMhail by Coolgle
 - SMTPConsole
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

/* SslSocketClient.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class SMTPConsole {
    //CONNECTION SETTINGS
    private String SMTPHost = "smtp.mail.yahoo.com";
    private int SMTPPort = 587;
    private boolean debug = true;

    //Console communication
    private static BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
    private static PrintStream sysOut = System.out; //Print to console

    //Socket-related
    private SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private Socket plainSocket = null; //regular socket (587)
    private SSLSocket secureSocket = null; //SSL/TLS socket (secured 587 or 465)
    private BufferedReader serverReader = null; //Read from server
    private BufferedWriter serverWriter = null; //Write to server

    //Constructor
    public SMTPConsole() {
        //There is nothing actually happening here, since everything got instantiated before
    }

    //Main
    public static void main(String[] args) {
        SMTPConsole mainConsole = new SMTPConsole();
        //Run different connection method based on port, print msg and quit if unacceptable port
        if (mainConsole.SMTPPort == 465) {
            if (mainConsole.newSecureConnection()) {
                mainConsole.communicationLoop();
            }
        } else if (mainConsole.SMTPPort == 587) {
            if (mainConsole.plainConnect()) {
                if (mainConsole.upgradeSecureConnection()) {
                    mainConsole.communicationLoop();
                }
            }
        } else {
            sysOut.println("Client ERROR: Incompatible port. Please use only 465 or 587.");
        }
        //Run main connection loop
    }

    //Make an unsecured connection over 587 and attempt upgrade
    private boolean plainConnect() {
        //Attempt to make plain socket & start TLS negotiation
        try {
            plainSocket = new Socket(SMTPHost, SMTPPort);
            serverReader = new BufferedReader(new InputStreamReader(plainSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(plainSocket.getOutputStream()));
            String serverInput = null;

            if (debug) {
                sysOut.println("Client SUCCESS: Plain connection to " + SMTPHost + " on port " + SMTPPort);
            }

            //Check for server greeting
            serverInput = serverReader.readLine();
            if (!checkResponseCode(serverInput, "220")) {
                sysOut.println("Client ERROR: Server didn't properly greet.");
                return false;
            }
            if (debug) {
                sysOut.println("Client SUCCESS: Server greeted 220.");
            }

            //Send HELO to server
            writeServer("HELO " + SMTPHost);
            //Check for HELO response
            serverInput = serverReader.readLine();
            if (!checkResponseCode(serverInput, "250")) {
                sysOut.println("Client ERROR: Server didn't properly respond to HELO.");
                return false;
            }
            if (debug) {
                sysOut.println("Client SUCCESS: Server answered HELO 250.");
            }

            //Send STARTTLS to server
            writeServer("STARTTLS");
            //Check for STARTTLS response
            serverInput = serverReader.readLine();
            if (!checkResponseCode(serverInput, "220")) {
                sysOut.println("Client ERROR: Server didn't properly respond to STARTTLS.");
                return false;
            }
            if (debug) {
                sysOut.println("Client SUCCESS: Server started TLS 220.");
            }
            //Successfully started TLS, so method completed
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Upgrade plain connection to secure connection and do handshake
    private boolean upgradeSecureConnection() {
        try {
            secureSocket = (SSLSocket) mainFactory.createSocket(
                    plainSocket,
                    plainSocket.getInetAddress().getHostAddress(),
                    plainSocket.getPort(),
                    true);
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));
            if (debug) {
                sysOut.println("Client SUCCESS: Upgraded to secure connection.");
            }
            printSocketInfo(secureSocket); //Print connection info
            writeServer("EHLO " + SMTPHost);
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Initialize new secure connection and do handshake
    private boolean newSecureConnection() {
        try {
            secureSocket = (SSLSocket) mainFactory.createSocket(SMTPHost, SMTPPort);
            serverReader = new BufferedReader(new InputStreamReader(secureSocket.getInputStream()));
            serverWriter = new BufferedWriter(new OutputStreamWriter(secureSocket.getOutputStream()));
            if (debug) {
                sysOut.println("Client SUCCESS: Started new secure connection to " + SMTPHost + " on port " + SMTPPort);
            }
            printSocketInfo(secureSocket); //Print connection info
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    //Main loop for communicating with server
    private void communicationLoop() {
        String serverInput = null; //Stores latest line from server
        String userInput = ""; //Stores latest input line from user
        boolean tryRead = true; //Whether to read next line from serverReader (prevents multiline blocking)

        //The below booleans, used to successully close the connection, might be unnecessary
        boolean openRead = true; //Whether serverReader is still open (serverInput != null)
        boolean openSocket = true; //Whether clientSocket is still open (clientSocket != null)
        //boolean quitUser = false; //Whether the user has entered quit, might be unnecessary

        //SMTP input variables
        boolean sendingData = false;
        String cmdMode = "regular";

        //Main connection loop
        try {
            while (openSocket && openRead) {
                if (secureSocket == null) { //Break if socket is closed
                    openSocket = false;
                    sysOut.println("Connection closed unexpectedly.");
                    break;
                }
                //Display server response/message
                while (tryRead) {
                    serverInput = serverReader.readLine();
                    if (serverInput == null) { //If serverReader gets closed/connection broken
                        openRead = false;
                        tryRead = false;
                        break;
                    }
                    sysOut.println(serverInput);
                    if (serverInput.substring(3, 4).equals("-")) { //Check if multiline response
                        tryRead = true;
                    } else {
                        tryRead = false;
                    }
                }
                //Exit client if connection lost/closed prematurely
                if (openSocket == false || openRead == false) {
                    sysOut.println("Connection closed unexpectedly.");
                    break;
                }
                //Exit client if 221 stream close msg received
                if (checkResponseCode(serverInput, "221")) {
                    break;
                }
                //Enter data mode if data command sent and 354 confirmation received
                if (userInput.equalsIgnoreCase("data") && checkResponseCode(serverInput, "354")) {
                    sendingData = true;
                    cmdMode = "data";
                }
                //In data mode (multi-line) and sending data (. not entered)
                while (sendingData && cmdMode.equals("data")) {
                    userInput = sysIn.readLine();
                    writeServer(userInput);
                    if (userInput.equals(".")) { //End of message
                        sendingData = false;
                        tryRead = true;
                    }
                }
                //In regular command mode (single-line)
                if (cmdMode.equals("regular")) {
                    userInput = ""; //Reset userInput to show prompt
                    //Read user input, display prompt if blank enter, otherwise send to server
                    while (userInput.equals("")) {
                        sysOut.print("C: ");
                        userInput = sysIn.readLine();
                    }
                    writeServer(userInput);
                    tryRead = true;
                }
                cmdMode = "regular"; //Force reset to regular mode after response-command cycle ends
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    //Check if server response string contains wanted response code
    private boolean checkResponseCode(String response, String code) {
        return (response != null && response.length() >= 3 && response.substring(0, 3).equals(code));
    }

    //Send message/command line to the server
    private boolean writeServer(String userLine) {
        //NOTE, implement these error catching properties later
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

    //Close all connections
    private void close() {
        try {
            if (serverWriter != null) {
                serverWriter.close();
            }
            if (serverReader != null) {
                serverReader.close();
            }
            if (plainSocket != null) {
                plainSocket.close();
            }
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

    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = "
                + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = "
                + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = " + ss.getCipherSuite());
        System.out.println("   Protocol = " + ss.getProtocol());
    }

    private static void printConnectionInfo(SSLSocket s) {
        SSLSession currentSession = s.getSession();
        System.out.println("Protocol: " + currentSession.getProtocol());
        System.out.println("Cipher Suite: " + currentSession.getCipherSuite());
        System.out.println("Host: " + currentSession.getPeerHost());
        System.out.println("Host Port: " + currentSession.getPeerPort());

    }
}
