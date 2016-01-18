/* GioMhail by Coolgle
   - POPSession
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
   - [DONE] private boolean AuthPlain()
   - [DONE] private boolean AuthLogin()
   - [DONE] private boolean UserPassLogin()
   - [DONE] Modify login() to reflect changes
   - [DONE] public boolean isConnected() (based on isClosed of socket)
   - [DONE] public boolean disconnect() (convert from close())
   - Implememt server connection checks in write/read functions
   - Implement exception throwing/catching (needs to work withg Client's simplified error output)
   - [DONE] remove sysIn and sysOut
   - [DONE] Comment all methods (at least w/ function headers)
   - [DONE] organize code (group methods)

   MAJOR:
   - [DONE] Create new class Session, transfer common SMTP & POP methods/variables to it
   - [DONE] Create new class Header, acting as container for message header
   - [DONE] Create new class Message, acting as container for a Header obj and body
   - [DONE] Modify retrieve & getHeader to return a Message and a Header respectively
   - [DONE] Create getHeaderList and getMessageList to return ArrayList<Header> and ArrayList<Messages>
   - [DONE] Modify read to return ArrayList<String>
   - [DONE] Modify all methods using read to support new return method
*/

/* CODE Structure
 - Constructor
 - HeaderStore methods
 - Message methods
 - messageCount
 - delete
 */

import java.util.*;

public class POPSession extends Session{

    //POPSession constructor
    public POPSession(String host, int port) {
        super(host, port, "POP");
    }

    //-----Getting Header-----

    //Returns ArrayList of the latest numMessages of HeaderStores
    public ArrayList<HeaderStore> getHeaderStoreList(int numMessages) {
        int totalMsgs = getMessageCount();
        return getHeaderStoreList(totalMsgs - numMessages, totalMsgs);
    }

    //Returns ArrayList of HeaderStores between minMsg and maxMsg, inclusive
    public ArrayList<HeaderStore> getHeaderStoreList(int minMsg, int maxMsg) {
        ArrayList<HeaderStore> HeaderStoreList = new ArrayList<HeaderStore>();
        for (int i = minMsg; i <= maxMsg; i++) {
            HeaderStoreList.add(getHeaderStore(i));
        }
        return HeaderStoreList;
    }

    //Gets and returns a HeaderStore object for an email by num in inbox
    //Attempts to use TOP, then RETR
    //Returns null if error
    public HeaderStore getHeaderStore(int messageNum) {
        //Attempt TOP for header
        writeServer("TOP " + messageNum + " 0");
        ArrayList<String> serverInput = read(true);
        if (serverInput == null) {
            return null; //Return null if error in read
        }
        if (checkOK(serverInput.get(0))) { //If TOP supported, create Header object
            trimHeaders(serverInput);
            HeaderStore retHeaderStore = new HeaderStore(serverInput, messageNum);
            return retHeaderStore;
        } else { //Use RETR if TOP unsupported by server
            //Attempt RETR for header
            writeServer("RETR " + messageNum);
            serverInput = read(true);
            if (serverInput == null) {
                return null; //Return null if error in read
            }
            if (checkOK(serverInput.get(0))) {
                trimHeaders(serverInput);
                HeaderStore retHeaderStore = new HeaderStore(serverInput, messageNum);
                return retHeaderStore;
            } else {
                return null; //Return null if TOP and RETR failed. Client must check.
            }
        }
    }

    //Trims TOP & RETR responses down to the header
    //Removes +OK lines and any lines following last header attribute
    //Should just modify the argument, with no need for returning
    private void trimHeaders(ArrayList<String> longResponse) {
        //Trim first line if +OK ...
        if (longResponse.get(0).startsWith("+OK")) {
            longResponse.remove(0);
        }
        //Starting from first line/index, find blank line and remove all lines from it to end
        int i = 0;
        boolean trimmed = false;
        while (!trimmed && i < longResponse.size() - 1) {
            if (longResponse.get(i).equals("\n") || longResponse.get(i).equals("\r\n")) {
                longResponse.subList(i, longResponse.size()).clear();
                trimmed = true;
            }
            i++;
        }
        //Just in case, check if last line is "." and trim accordingly
        if (longResponse.get(longResponse.size() - 1).equals(".")) {
            longResponse.remove(longResponse.size() - 1);
        }
    }

    //-----GETTING MESSAGE-----

    //Returns ArrayList of the latest numMessages of Messages
    public ArrayList<Message> getMessageList(int numMessages) {
        int totalMsgs = getMessageCount();
        return getMessageList(totalMsgs - numMessages, totalMsgs);
    }

    //Returns ArrayList of Messages between minMsg and maxMsg, inclusive
    public ArrayList<Message> getMessageList(int minMsg, int maxMsg) {
        ArrayList<Message> MessageList = new ArrayList<Message>();
        for (int i = minMsg; i <= maxMsg; i++) {
            MessageList.add(getMessage(i));
        }
        return MessageList;
    }

    //Returns Message object for email specified by messageNum using RETR
    public Message getMessage(int messageNum) {
        writeServer("RETR " + messageNum);
        ArrayList<String> serverInput = read(true);
        if (checkERR(serverInput.get(0))) {
            return null; //return null if error in read/getting email
        }
        trimMessage(serverInput);
        Message retEmail = new Message(serverInput, messageNum);
        return retEmail;
    }

    //Trims RETR response down to header attributes and message body
    //Removes first +OK line and last . line
    private void trimMessage(ArrayList<String> longMsg) {
        if (longMsg.get(0).startsWith("+OK")) {
            longMsg.remove(0);
        }
        if (longMsg.get(longMsg.size() - 1).equals(".")) {
            longMsg.remove(longMsg.size() - 1);
        }
    }

    //-----MESSAGE COUNT & DELETE-----

    //Uses STAT to get number of messages in inbox
    public int getMessageCount() {
        writeServer("STAT");
        ArrayList<String> serverInput = read(false);
        int end = 0;
        for (int i = 4; i < serverInput.get(0).length(); i++) { //Find where msgCount ends
            if (serverInput.get(0).substring(i, i + 1).equals(" ")) {
                end = i;
            }
        }
        return Integer.parseInt(serverInput.get(0).substring(4, end)); //Get the msgCount as int
    }

    //Deletes a message specified by num in inbox
    public boolean delete(int messageNum) {
        writeServer("DELE " + messageNum);
        ArrayList<String> serverInput = read(false);
        return checkOK(serverInput.get(0));
    }
}