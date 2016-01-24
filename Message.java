/* GioMhail by Coolgle
   - Message
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

import java.util.*;
import java.io.*;

import org.apache.commons.codec.binary.Base64;

/* TODO
 - [DONE] public void fillMessage(ArrayList<String> messageLines)
 - [DONE] Modify constructor to call fillMessage
 - [DONE] Add blank message constructor
 - [DONE] public String getMessageBody()
 - [DONE] public void setMessageBody(String newBody)
 - [DONE] public HeaderStore getHeaderStore()
 - [DONE] public void setHeaderStore()
 - [DONE] Make variables private
 - [DONE] Technically, the \n in the messageBody should be \r\n (CRLF)?
 - Check messageBody for \r\n.\r\n, which will break DATA, and prepend with >
*/

public class Message {
    private HeaderStore messageHeaderStore; //HeaderStore for headers
    private String messageBody; //Actual message content
    private int messageNum; //The message number of the email in the inbox
    private String fileBody;
    private HashMap<String, String> MIMEInfo;
    private String fileName;

    //Sending constructor
    public Message() {
        messageHeaderStore = new HeaderStore();
        messageBody = "";
    }

    //Receiving constructor
    public Message(ArrayList<String> messageLines) {
        messageHeaderStore = new HeaderStore();
        messageBody = "";
        fillMessage(messageLines);
    }

    //Receiving constructor + messagen number
    public Message(ArrayList<String> messageLines, int msgNum) {
        this(messageLines);
        messageNum = msgNum;
        messageHeaderStore.setMessageNum(messageNum);
    }

    //-----GENERAL MESSAGE METHODS-----

    //Fill messageHeaderStore and messageBody from trimmed output of RETR call
    public void fillMessage(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        ArrayList<String> headerLines = new ArrayList<String>(messageLines.subList(0, firstLine - 1));
        messageHeaderStore = new HeaderStore(headerLines); //Create HeaderStore from headers in ArrayList
        if (messageHeaderStore.getMIMEConfirm() == "") {
            for (int i = firstLine; i < messageLines.size(); i++) {
                messageBody += messageLines.get(i) + "\r\n"; //Add lines from ArrayList, appending \n
            }
            cleanMsgBody(); //Converts any \r\n.\r\n to \r\n>.\r\n, preventing data breakage
        } else {
            fillMultipartMessage(messageLines);
        }
    }

    //Find the first blank like between the email headers and the body
    private int findBlankLine(ArrayList<String> messageLines) {
        for (int i = 0; i < messageLines.size(); i++) {
            if (messageLines.get(i).equals("")) { //Blank line
                return i;
            }
        }
        System.out.println(-1);
        return -1; //If blank line not found
    }

    public void fillMessageFile(String fileName, String folderName) {
        Storage s1 = new Storage();
        fillMessage(s1.getMsg(fileName, folderName));
    }

    //-----BODY METHODS-----

    private void setBody(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        for (int i = firstLine; i < messageLines.size(); i++) {
            messageBody += messageLines.get(i) + "\r\n"; //Add lines from ArrayList, appending \n
        }
    }

    //Return the message body (no headers)
    public String getMessageBody() {
        return messageBody;
    }

    //Set the message body
    public void setMessageBody(String newBody) {
        messageBody = newBody;
        cleanMsgBody();
    }

    //Prevent DATA breaking on \r\n.\r\n
    private void cleanMsgBody() {
        int badPeriod = 0;
        while ((badPeriod = messageBody.indexOf("\r\n.\r\n")) != -1) {
            messageBody = messageBody.substring(0, badPeriod + 4) + ">" +
                    messageBody.substring(badPeriod + 4, messageBody.length());
        }
    }

    //-----HEADERSTORE METHODS-----

    //Get the message's HeaderStore
    public HeaderStore getHeaderStore() {
        return messageHeaderStore;
    }

    ///Set the message's HeaderStore
    public void setHeaderStore(HeaderStore newMsgHeaderStore) {
        messageHeaderStore = newMsgHeaderStore;
    }

    //-----MULTIPART METHODS-----

    public void fillMultipartMessage(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        String boundary = "";
        for (int i = firstLine; i < messageLines.size(); i++) {
            if (i == firstLine) {
                boundary = messageLines.get(i);
            } else if (messageLines.get(i).equals(boundary) && messageLines.get(i - 1).substring(0, 1).equals("-")) {
                extractMessage((ArrayList<String>) (messageLines.subList(firstLine + 1, i)));
                getFiles((ArrayList<String>) (messageLines.subList(i, messageLines.size() - 1)));
            } else {
                extractMessage((ArrayList<String>) (messageLines.subList(firstLine, messageLines.size())));
            }
        }
        cleanMsgBody(); //Converts any \r\n.\r\n to \r\n>.\r\n, preventing data breakage
        ArrayList<String> headerLines = new ArrayList<String>(messageLines.subList(0, firstLine - 1));
        messageHeaderStore = new HeaderStore(headerLines); //Create HeaderStore from headers in ArrayList
    }

    private void extractMessage(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        String boundary = messageLines.get(firstLine);
        int startPart = firstLine;
        for (int i = firstLine + 1; i < messageLines.size(); i++) {
            if (messageLines.get(i).equals(boundary) || messageLines.get(i).equals(boundary + "--")) {
                messageBody += getBody((ArrayList<String>) (messageLines.subList(startPart + 1, i)));
                startPart = i;
            }
        }
    }

    private void getFiles(ArrayList<String> fileLines) {
        int firstLine = findBlankLine(fileLines) + 1; //First line of messageBody
        String boundary = fileLines.get(firstLine);
        int startPart = firstLine;
        String msgBody = "";
        for (int i = firstLine + 1; i < fileLines.size(); i++) {
            if (fileLines.get(i).equals(boundary) || fileLines.get(i).equals(boundary + "--")) {
                getFiles((ArrayList<String>) (fileLines.subList(startPart + 1, i)));
                startPart = i;
            }
        }
    }

    private String getBody(ArrayList<String> messageLines) {
        String msgBody = "";
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        for (int i = firstLine; i < messageLines.size(); i++) {
            msgBody += messageLines.get(i) + "\r\n"; //Add lines from ArrayList, appending \n
        }
        return msgBody;
    }

    private void getFile(ArrayList<String> fileLines) {
        MIMEInfo = new HashMap<String, String>();
        int firstLine = findBlankLine(fileLines) + 1; //First line of messageBody
        for (int i = firstLine; i < fileLines.size(); i++) {
            fileBody += fileLines.get(i); //Add lines from ArrayList, appending \n
        }
        byte[] utfBytes = Base64.decodeBase64(fileBody);
        String utfStr = "";
        try {
            utfStr = new String(utfBytes, "UTF_8");
        } catch (UnsupportedEncodingException e) {
            //Do nothing
        }
        fileBody = utfStr;
        ArrayList<String> MIMEHeaderLines = new ArrayList<String>(fileLines.subList(0, firstLine - 1));
        for (int i = 0; i < MIMEHeaderLines.size(); i++) {
            int space = MIMEHeaderLines.get(i).indexOf(' ');
            MIMEInfo.put(MIMEHeaderLines.get(i).substring(0, space), MIMEHeaderLines.get(i).substring(space + 1, MIMEHeaderLines.get(i).length()));
        }
        fileName = "Data/";
        fileName += MIMEInfo.get("Content-Disposition:").substring(MIMEInfo.get("Content-Disposition:").indexOf('=') + 1, MIMEInfo.get("Content-Disposition:").length() - 1);
        Storage s1 = new Storage();
        s1.saveFile(fileBody, fileName);
    }
    //private void cleanText(String text)

    //-----Message Number-----

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int newMsgNum) {
        messageNum = newMsgNum;
    }
}
