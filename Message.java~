import java.util.*;

/* TODO
 - [DONE] public void fillMessage(ArrayList<String> messageLines)
 - [DONE] Modify constructor to call fillMessage
 - [DONE] Add blank message constructor
 - [DONE] public String getMessageBody()
 - [DONE] public void setMessageBody(String newBody)
 - [DONE] public HeaderStore getHeaderStore()
 - [DONE] public void setHeaderStore()
 - [DONE] Make variables private
*/

public class Message {
    private HeaderStore messageHeaderStore; //HeaderStore for headers
    private String messageBody; //Actual message content

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

    //Fill messageHeaderStore and messageBody from trimmed output of RETR call
    public void fillMessage(ArrayList<String> messageLines) {
        int firstLine = findBlankLine(messageLines) + 1; //First line of messageBody
        for (int i = firstLine; i < messageLines.size(); i++) {
            messageBody += messageLines.get(i) + "\n"; //Add lines from ArrayList, appending \n
        }
        ArrayList<String> headerLines = (ArrayList<String>) messageLines.subList(0, firstLine - 1);
        messageHeaderStore = new HeaderStore(headerLines); //Create HeaderStore from headers in ArrayList
    }

    //Find the first blank like between the email headers and the body
    private int findBlankLine(ArrayList<String> messageLines) {
        for (int i = 0; i < messageLines.size(); i++) {
            if (messageLines.get(i).equals("\n") || messageLines.get(i).equals("\r\n")) { //Blank line is \n or \r\n
                return i;
            }
        }
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
    }
}
