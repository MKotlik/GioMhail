import java.util.*;
import java.io.*;

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
    private String hashCode;
    private PrintWriter out;
    
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

    //Fill messageHeaderStore and messageBody from trimmed output of RETR call
    public void fillMessage(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        for (int i = firstLine; i < messageLines.size(); i++) {
            messageBody += messageLines.get(i) + "\r\n"; //Add lines from ArrayList, appending \n
        }
        cleanMsgBody(); //Converts any \r\n.\r\n to \r\n>.\r\n, preventing data breakage
        ArrayList<String> headerLines = new ArrayList<String>(messageLines.subList(0, firstLine - 1));
        messageHeaderStore = new HeaderStore(headerLines); //Create HeaderStore from headers in ArrayList
    }

    //----------Multipart Messages--------------
    public void fillMultipartMessage(ArrayList<String> messageLines){
	int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
	String boundary = "";
	for (int i = firstLine; i < messageLines.size(); i++){
	    if (i==firstLine){
		boundary=messageLines.get(i);
	    }else if(messageLines.get(i).equals(boundary)){
		extractMessage((ArrayList<String>)(messageLines.subList(firstLine+1, i)));
		getFile((ArrayList<String>)(messageLines.subList(i, messageLines.size()-1)));
	    }
	}
	cleanMsgBody(); //Converts any \r\n.\r\n to \r\n>.\r\n, preventing data breakage
        ArrayList<String> headerLines = new ArrayList<String>(messageLines.subList(0, firstLine - 1));
        messageHeaderStore = new HeaderStore(headerLines); //Create HeaderStore from headers in ArrayList
    }

    private void extractMessage(ArrayList<String> messageLines){
	int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody	
	String boundary = messageLines.get(firstLine);
	int startPart = firstLine;
	String msgBody = "";
	for (int i=firstLine+1;i<messageLines.size();i++){
	    if (messageLines.get(i).equals(boundary)||messageLines.get(i).equals(boundary+"--")){
		msgBody+=getBody((ArrayList<String>)(messageLines.subList(startPart+1,i)));
		startPart=i;
	    }
	}
    }

    private String getBody(ArrayList<String> messageLines){
	int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        for (int i = firstLine; i < messageLines.size(); i++) {
            messageBody += messageLines.get(i) + "\r\n"; //Add lines from ArrayList, appending \n
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

    //Get the message's HeaderStore
    public HeaderStore getHeaderStore() {
        return messageHeaderStore;
    }

    ///Set the message's HeaderStore
    public void setHeaderStore(HeaderStore newMsgHeaderStore) {
        messageHeaderStore = newMsgHeaderStore;
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

    public String saveMessage(Message newMsg){
	createFileName();
	try{
	    out=new PrintWriter(hashCode);
	    writeHeader();
	    writeMessage();
	    out.close();
	    return "SUCCESS";
	}catch(FileNotFoundException e){
	    return "ERROR";
	}
    }

    private void writeHeader(){
        String[] headerKeys = messageHeaderStore.getKeyArray();
        for (int i = 0; i < headerKeys.length; i++) {
            ArrayList<String> headerValueList = messageHeaderStore.getHeaderValue(headerKeys[i]);
            for (int j = 0; j < headerValueList.size(); j++) {
                String line = headerKeys[i] + ": " + headerValueList.get(j);
                out.println(line);
            }
        }
    }

    private void writeMessage(){
	out.print(messageBody);
    }

    private void createFileName(){
	String unHashed = "";
	String To = messageHeaderStore.getTo();
	String From = messageHeaderStore.getFrom();
	String Date = messageHeaderStore.getDate();
	String Subject = messageHeaderStore.getSubject();
	unHashed = To+":"+From+":"+Date+":"+Subject;
	hashCode = ""+unHashed.hashCode();
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
