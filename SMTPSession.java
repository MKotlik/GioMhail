/* GioMhail by Coolgle
   - SMTPSession
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
 - [DONE] Complete all helper methods
 - [DONE] Technically, the \n in the messageBody should be \r\n (CRLF)?
 - [DONE] If a . is found in the messageBody, prepend it with ">", so that it doesn't break DATA
 - Also, techically in all methods that send to server, we should check afterwards that we don't get a 420 or 421
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

    //Unknown server SMTPSession constructor
    public SMTPSession() {
        super("SMTP");
    }

    //Sends a message to a server
    //Returns a String indicated an error or success
    //Possible error Strings: "NO FROM", "NO TO", "BAD FROM", "BAD TO", "MISMATCHED FROM", "DATA REFUSED", "BAD DATA"
    //On success, returns "SUCCESS"
    //"NO FROM" or "NO TO" - headers missing. Client should ALWAYS prevent this from happening.
    //"BAD FROM" - if brackets are not in <...> format
    //"BAD TO" - if addresses are not in pairs of <...> brackets,
    //"MISMATCHED FROM" - if header From address doesn't match the user/server combo
    //^NOTE, when asking user for From Address, tell them it must match the address of their account on the server
    //"DATA REFUSED" or "BAD DATA" - Server error?

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
        String from = ParseUtils.parseFrom(msgHeaders.getFrom());
        if (from.equals("BAD FROM")) {
            return "BAD FROM";
        }
        ArrayList<String> toList = ParseUtils.parseTo(msgHeaders.getTo());
        if (toList.get(0).equals("BAD TO")) {
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
        for (int i = 0; i < toList.size(); i++) {
            writeServer("RCPT TO: " + toList.get(i));
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

    //Sends header lines during DATA session
    private void sendHeaders(HeaderStore msgHeaders) {
        String[] headerKeys = msgHeaders.getKeyArray();
        for (int i = 0; i < headerKeys.length; i++) {
            ArrayList<String> headerValueList = msgHeaders.getHeaderValue(headerKeys[i]);
            for (int j = 0; j < headerValueList.size(); j++) {
                String line = headerKeys[i] + ": " + headerValueList.get(j);
                writeServer(line);
            }
        }
    }
}