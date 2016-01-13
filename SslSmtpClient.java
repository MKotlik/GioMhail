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

public class SslSmtpClient {
    public static void main(String[] args) {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
        SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault(); //Get default SSL socket factory
        try {
            SSLSocket clientSocket = (SSLSocket) mainFactory.createSocket("smtp.live.com", 465); //create, connect, start handshake
            printSocketInfo(clientSocket); //Print connection info
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); //Write to server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Read from server

            String serverInput = null; //Stores latest line from server
            String userInput = ""; //Stores lastest input line from user
            boolean tryRead = true; //Whether to read next line from serverReader (prevents blocking on multiline SMTP responses)

            //The below booleans, used to successully close the connection, might be unnecessary
            boolean openRead = true; //Whether serverReader is still open (serverInput != null)
            boolean openSocket = true; //Whether clientSocket is still open (clientSocket != null)
            //boolean quitUser = false; //Whether the user has entered quit, might be unnecessary

            //SMTP input variables
            boolean sendingData = false;
            String cmdMode = "regular";

            //Main connection loop
            while (openSocket && openRead) {
                if (clientSocket == null) { //Break if socket is closed
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
                if (serverInput != null && serverInput.substring(0, 3).equals("221")) {
                    break;
                }
                //Enter data mode if data command sent and 354 confirmation received
                if (userInput.equalsIgnoreCase("data") && serverInput.substring(0, 3).equals("354")) {
                    sendingData = true;
                    cmdMode = "data";
                }
                //In data mode (multi-line) and sending data (. not entered)
                while (sendingData && cmdMode.equals("data")) {
                    userInput = sysIn.readLine();
                    serverWriter.write(userInput, 0, userInput.length()); //Writing to server
                    serverWriter.newLine();
                    serverWriter.flush();
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
                    serverWriter.write(userInput, 0, userInput.length()); //Writing to server
                    serverWriter.newLine();
                    serverWriter.flush();
                    tryRead = true;
                }
                cmdMode = "regular"; //Force reset to regular mode after response-command cycle ends
            }
            //Clean up all connection objects
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
