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
    public int POPPort;
    public String POPHost;
    public BufferedWriter serverWriter;
    public BufferedReader serverReader;

    public POPSession(int port, String host) {
        POPPort = port;
        POPHost = host;
    }

    public boolean connect() {
        //Modify this later, since variables will be declared as globals
        //Only need to instantiate
        SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault(); //Get default SSL socket factory
        try {
            SSLSocket clientSocket = (SSLSocket) mainFactory.createSocket(POPHost, POPPort); //create, connect, start handshake
            if (clientSocket == null) {
                return false;
            }
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); //Write to server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Read from server
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }

    }

    //Use writeServer from SMTPConsole (copy the code) to send messages to server
    public boolean login(String user, String pass) {
        try {
            String serverInput = null;
            serverWriter.write("user " + user, 0, 5 + user.length());
            serverWriter.newLine();
            serverWriter.flush();
            serverInput = serverReader.readLine();
            if (!serverInput.substring(0, 3).equalsIgnoreCase("+OK")) {
                return false;
            }
            serverWriter.write("pass " + pass, 0, 5 + pass.length());
            serverWriter.newLine();
            serverWriter.flush();
            serverWriter.flush();
            serverInput = serverReader.readLine();
            //USE checkResponseCode from SMTPConsole
            if (!serverInput.substring(0, 3).equalsIgnoreCase("+OK")) {
                return false;
            }
            return true;
        } catch (IOException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public int getMessageCount() {
        //Returns number of messages in server mailbox (looks at number of latest message/last line of list)
    }

    //Actually just copy and modify the methods used in here,
    //http://inetjava.sourceforge.net/lectures/part1_sockets/InetJava-1.8-Email-Examples.html
    //It seems like this is actually good code. Just make sure that it functions correctly when you adapt it.

    public void close() {
        //Close any sockets, printstreams, and bufferedreaders here
        //Will need to encapsulate code in try block catching IOExceptions and printing to error
    }

    private boolean checkResponseCode(String response, String code){
        //Copy from SMTPConsole
    }

    private boolean writeServer(String userLine){
        //Copy from SMTPConsole
    }
}
