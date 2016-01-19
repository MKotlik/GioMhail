/* GioMhail by Coolgle
   - SMTPSession
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
 - [DONE] Complete all helper methods
 - [DONE] Technically, the \n in the messageBody should be \r\n (CRLF)?
 - If a . is found in the messageBody, prepend it with ">", so that it doesn't break DATA
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
        String from = parseFrom(msgHeaders.getFrom());
        if (from.equals("BAD FROM")) {
            return "BAD FROM";
        }
        ArrayList<String> toList = parseTo(msgHeaders.getTo());
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

    //This method parses the fromHeader
    //The header normally looks like: "Misha Kotlik <mikekotlik@gmail.com>"
    //This function has to count the number of brackets. If it doesn't match 2, return "BAD FROM"
    //Otherwise,  the address with the brackets
    private String parseFrom(String fromHeader) {
        if (countChar(fromHeader, '<') != 1 || countChar(fromHeader, '>') != 1) {
            return "BAD FROM";
        }
        int bracket1Ind = fromHeader.indexOf('<');
        int bracket2Ind = fromHeader.indexOf('>');
        if (bracket2Ind <= bracket1Ind) {
            return "BAD FROM";
        }
        return fromHeader.substring(bracket1Ind, bracket2Ind + 1);
    }

    //This method parses the toHeader
    //The header normally looks like: "Gio Topa <giotopa@gmail.com>, Gio Kotlik <giokotlik@yahoo.com>"
    //Returns "BAD TO" as first element
    //If the above condition isn't met, return "BAD TO" as first element of ArrayList
    //Otherwise, add the addresses with the brackets to an ArrayList and return it
    private ArrayList<String> parseTo(String toHeader) {
        ArrayList<String> toAddresses = new ArrayList<String>();
        int LBrCount = countChar(toHeader, '<');
        int RBrCount = countChar(toHeader, '>');
        if ((LBrCount < 1) || (RBrCount < 1) || (LBrCount != RBrCount)) {
            toAddresses.add("BAD TO");
            return toAddresses;
        }
        String addressLine = toHeader;
        for (int i = 0; i < LBrCount; i++) {
            int bracket1Ind = addressLine.indexOf('<');
            int bracket2Ind = addressLine.indexOf('>');
            if (bracket2Ind <= bracket1Ind) {
                toAddresses.add("BAD TO"); //Need this to insure always has 0 index
                toAddresses.set(0, "BAD TO");
                return toAddresses;
            }
            //Technically could check that there are at least 3 chars within all chars, and that there is a @
            toAddresses.add(addressLine.substring(bracket1Ind, bracket2Ind + 1));
            if (bracket2Ind != addressLine.length() - 1) { //If > isn't at the end
                addressLine = addressLine.substring(bracket2Ind + 1, addressLine.length()); //Get string after >
            }
        }
        return toAddresses;
    }

    //Counts the number of target chars in given input string
    //0 if none
    private int countChar(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
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
