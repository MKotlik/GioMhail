/* GioMhail by Coolgle
 - POPConsole
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

/* TODO
 - Fix socket write error/exception on undetected inactivity timeout
 - [DONE] Rename to POPConsole
 - Update code format
 - Ask user for host address & port in main
 */

public class POPConsole {
    public static void main(String[] args) {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
        SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault(); //Get default SSL socket factory
        try {
            SSLSocket clientSocket = (SSLSocket) mainFactory.createSocket("pop.mail.yahoo.com", 995); //create, connect, start handshake
            printSocketInfo(clientSocket); //Print connection info
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); //Write to server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Read from server

            String serverInput = null; //Stores latest line from server
            String userInput = ""; //Stores lastest input line from user
            boolean tryRead = true; //Whether to read next line from serverReader (prevents blocking on multiline SMTP responses)

            //The below booleans, used to successully close the connection, might be unnecessary
            boolean quitUser = false; //Whether the user has entered quit, might be unnecessary
            boolean openRead = true; //Whether serverReader is still open (serverInput != null)
            boolean openSocket = true; //Whether clientSocket is still open (clientSocket != null)

            //SMTP input variables
            boolean sendingData = false;
            boolean multi = false;

            //Main connection loop
            while (openSocket && openRead && !quitUser) {
                if (clientSocket == null) { //Break if socket is closed
                    openSocket = false;
                    break;
                }
                //Display server response/message
                if (multi) {
                    while (tryRead) {
                        serverInput = serverReader.readLine();
                        if (serverInput == null) { //If serverReader gets closed/connection broken
                            openRead = false;
                            tryRead = false;
                            break;
                        }
                        sysOut.println(serverInput);
                        //Check for multiline response or error
                        if (serverInput.equals(".") ||
                                (serverInput.length() >= 4 && serverInput.startsWith("-ERR"))) {
                            tryRead = false;
                        } else {
                            tryRead = true;
                        }
                    }
                } else {
                    serverInput = serverReader.readLine();
                    if (serverInput == null) { //If serverReader gets closed/connection broken
                        openRead = false;
                        tryRead = false;
                        break;
                    }
                    sysOut.println(serverInput);
                }
                multi = false;
                //Exit client if connection lost/closed prematurely
                if (openSocket == false || openRead == false) {
                    break;
                }
                //If user previously entered quit
                if (userInput.length() >= 4 && userInput.substring(0, 4).equalsIgnoreCase("quit")) {
                    quitUser = true;
                    break;
                }
                //Get user input
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
                //Prepare for multi-line response if list, uidl, retr, or top
                if (userInput.equalsIgnoreCase("list") ||
                        userInput.equalsIgnoreCase("uidl") ||
                        userInput.equalsIgnoreCase("auth") ||
                        (userInput.length() >= 4 && userInput.substring(0, 4).equalsIgnoreCase("capa")) ||
                        (userInput.length() >= 4 && userInput.substring(0, 4).equalsIgnoreCase("retr")) ||
                        (userInput.length() >= 4 && userInput.substring(0, 3).equalsIgnoreCase("top"))) {
                    multi = true;
                }
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
