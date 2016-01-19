/* GioMhail by Coolgle
   - SMTPSession
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
 - Technically, the \n in the messageBody should be \r\n (CRLF)?
 - If a . is found in the messageBody, prepend it with ">", so that it doesn't break DATA
 - Also, techically in all classes that send to server, we should check afterwards that we don't get a 420 or 421
    connection error. As in, we check the response code from the server, expecting a confirmation, but if we get one
    of those codes, we know we have a connection error and return as such.
 */

import java.util.*;

public class SMTPSession extends Session {
    private HeaderStore header;

    //SMTPSession constructor
    public SMTPSession(String host, int port) {
        super(host, port, "SMTP");
    }

    //Sends a message to a server
    //Returns a String indicated an error or success
    //Possible error Strings: "NO FROM", "NO TO", "BAD FROM", "BAD TO", "MISMATCHED FROM", "DATA REFUSED", "BAD DATA"
    //On success, returns "SUCCESS"
    public String sendMessage(Message newMsg) {
        HeaderStore msgHeaders = newMsg.getHeaderStore();
        //Check presence of From and To headers
        if (msgHeaders.getFrom() == null) {
            return "NO FROM";
        }
        if (msgHeaders.getTo() == null) {
            return "NO TO";
        }
        //Parsse From and To headers and return errors of bad format
        String from = parseFrom(msgHeaders.getFrom());
        if (from.equals("BAD FROM")) {
            return "BAD FROM";
        }
        ArrayList<String> to = parseTo(msgHeaders.getTo());
        if (to.get(0).equals("BAD TO")) {
            return "BAD TO";
        }

        //Send MAIL FROM
        writeServer("MAIL FROM: " + from);
        ArrayList<String> serverInput = read(false);
        //NOTE, a server can also give us 251 & 252 codes, which are also OK. Should check for them as well.
        //Hence, check if response starts with 25 instead of 250
        if (!checkResponseCode(serverInput.get(0), "25")) { //If address not accepted
            if (checkResponseCode(serverInput.get(0), "553")) {
                return "MISMATCHED FROM"; //This means from addresses doesn't match user@server
            } else {
                return "BAD FROM"; //Most likely bad from format
            }
        } //Otherwise, MAIL FROM accepted

        //Send RCPT TO(s)
        for (int i = 0; i < to.size(); i++) {
            writeServer("RCPT TO: " + to.get(i));
            serverInput = read(false);
            if (!checkResponseCode(serverInput.get(0), "250")) { //If address not accepted
                return "BAD TO"; //Bad To format (most likely error 501)
            }
        }

        //Start sending headers & messageBody
        writeServer("DATA");
        serverInput = read(false);
        if (!checkResponseCode(serverInput.get(0), "354")) { //If server not accepting DATA
            return "DATA REFUSED"; //Client will know it's a server error
        } //Otherwise, proceed with sending data
        sendHeaders(msgHeaders);
        writeServer(""); //Send a blank line separating headers and body
        writeServer(newMsg.getMessageBody());  //Send messageBody to server
        writeServer("\r\n."); //Sends a <CRLF>.<CRLF> to server to end DATA (writeServer appends a \r\n)
        serverInput = read(false);
        if (!checkResponseCode(serverInput.get(0), "250")) { //If server not okaying message
            return "BAD DATA";
        } else {
            return "SUCCESS"; //Indicates message sent
        }
    }

    private String parseFrom(String fromHeader) {
        //This method parses the fromHeader
        //The header normally looks like: "Misha Kotlik <mikekotlik@gmail.com>"
        //This function has to count the number of brackets. If it doesn't match 2, return "BAD FROM"
        //Otherwise,  the address with the brackets
        return "<fromAdress>";
    }

    private ArrayList<String> parseTo(String toHeader) {
        //This method parses the toHeader
        //The header normally looks like: "Gio Topa <giotopa@gmail.com>, Gio Kotlik <giokotlik@yahoo.com>"
        //This function has to count the number of brackets. It has to be at least 2, and even.
        //If the above condition isn't met, return "BAD TO" as first element of ArrayList or String[]
        //Otherwise, add the addresses with the brackets to an ArrayList and return it
        //Alternatively, could use a String[]
        ArrayList<String> toAddresses = new ArrayList<String>();
        toAddresses.add("<aToAdress>");
        toAddresses.add("<anotherToAdress>");
        return toAddresses;
    }

    private void sendHeaders(HeaderStore msgHeaders) {
        String[] headerKeys = msgHeaders.getKeyArray();
        for (int i = 0; i < headerKeys.length; i++) {
            String line = headerKeys[i] + ": " + msgHeaders.getHeaderValue(headerKeys[i]);
            writeServer(line);
        }
    }
}

/* Deprecated sendMail code

    public String sendMail(Message newMsg) {
        header = newMsg.getHeaderStore();
        if (!header.getTo() = null) {
            writeServer("MAIL From: " + header.getFrom());
            String serverInput = read(false).get(0);
            if (checkResponseCode(serverInput, "250")) {
                if (!header.getTo() = null) {
                    writeServer("RCPT To: " + header.getTo());
                    serverInput = read(false).get(0);
                    if (checkResponseCode(serverInput, "250")) {
                        writeServer("DATA");
                        serverInput = read(false).get(0);
                        if (checkResponseCode(serverInput, "354")) {
                            String head = "";
                            String[] headersAvailable = header.getKeyArray();
                            for (int i = 0; i < headerAvailable.length; i++) {
                                head += headerAvailable[i] + ": " + header.getHeaderValue(headerAvailable[i]) + "\n";
                            }
                            writeServer(head + newMsg.getMessageBody() + "\n.");
                            serverInput = read(true).get(0);
                            if (checkResponseCode(serverInput, "250")) {
                                return "Mail sent";
                            } else {
                                return "Please enter valid message body";
                            }
                        } else {
                            return "Connection issues";
                        }
                    } else {
                        return "Please enter valid recipient";
                    }
                } else {
                    return "Please enter a recpipient";
                }
            } else {
                return "Please enter valid sender information";
            }
        } else {
            return "Please enter sender info";
        }
    }
 */
