/* GioMhail by Coolgle
 - HeaderStore
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

//Note, values should be ArrayList<String> b/c there can be duplicate headers
//All methods getting headers should take this into account

/* TODO
 - [DONE] public void fillHeaderStore(ArrayList<String> ALSHeaders)
 - [DONE] public String getHeaderValue(String headerKey)
 - [DONE] public void setHeader(String headerKey, String headerValue)
 - [DONE] public void getTo()
 - [DONE] public void getFrom()
 - [DONE] public void getSubject()
 - [DONE] public void getDate()
 - [DONE] public void getCC()
 - [DONE] public void getBCC()
 - [DONE] Modify above shortcut methods to return String
 - [DONE] Rename and refactor Header to HeaderStore
 */

import java.util.*;

public class HeaderStore {
    //This should be private
    private HashMap<String, ArrayList<String>> headerMap;
    private int messageNum; //The message number of the email in the inbox

    //Sending constructor
    public HeaderStore() {
        headerMap = new HashMap<String, ArrayList<String>>();
    }

    //Receiving Constructor
    public HeaderStore(ArrayList<String> headerLines) {
        headerMap = new HashMap<String, ArrayList<String>>();
        fillHeaderStore(headerLines);
    }

    //Receiving Constructor + Message Number
    public HeaderStore(ArrayList<String> headerLines, int msgNum) {
        this(headerLines);
        messageNum = msgNum;
    }

    //Fill in all header values from ArrayList<String> of HeaderStore output
    public void fillHeaderStore(ArrayList<String> HeaderLines) {
        String headerKey = "";
        String headerValue = "";
        for (int i = 0; i < HeaderLines.size(); i++) {
            if (i == 0) { //Spec. case, start headerKey and headerValue lcl storage
                headerKey = findHeaderKey(HeaderLines.get(i));
                headerValue = findHeaderValue(HeaderLines.get(i));
            } else if (i == HeaderLines.size() - 1) { //Spec. case, always put prev. and/or new header key/value pair
                if (HeaderLines.get(i).startsWith(" ") ||
                        HeaderLines.get(i).startsWith("\t")) { //If continuation of multi-line headerValue
                    headerValue += HeaderLines.get(i).substring(1, HeaderLines.get(i).length()); //append to cur. value
                    setHeader(headerKey, headerValue); //Add pair to headerMap, or append to existing pair
                } else { //If new header attribute
                    setHeader(headerKey, headerValue); //Put the old header key/value pair in headerMap
                    headerKey = findHeaderKey(HeaderLines.get(i)); //Get new header key/value pair
                    headerValue = findHeaderValue(HeaderLines.get(i));
                    setHeader(headerKey, headerValue); //Put new header key/value pair in headerMap
                }
            } else { //Any line but first and last
                if (HeaderLines.get(i).startsWith(" ") ||
                        HeaderLines.get(i).startsWith("\t")) { //If continuation of multi-line headerValue
                    headerValue += HeaderLines.get(i).substring(1, HeaderLines.get(i).length()); //append to cur. value
                } else { //If new header attribute
                    setHeader(headerKey, headerValue); //Put old header key/value pair in store
                    headerKey = findHeaderKey(HeaderLines.get(i)); //Get new header key/value pair
                    headerValue = findHeaderValue(HeaderLines.get(i));
                }
            }
        }
    }

    //Returns header key from a header response line
    private String findHeaderKey(String line) {
        int colonPos = line.indexOf(':');
        if (colonPos == -1) { //No colon found
            return null; //or ""?
        } else {
            return line.substring(0, colonPos);
        }
    }

    //Returns header value from a header response line
    private String findHeaderValue(String line) {
        int colonPos = line.indexOf(':');
        if (colonPos == -1) { //No colon found
            return null; //or ""?
        } else {
            return line.substring(colonPos + 2, line.length()); //Start from 1st char after : and space
        }
    }

    //Get ArrayList<String> containing header attributes for specified key
    //Returns null if no such headerKey in headerMap
    public ArrayList<String> getHeaderValue(String headerKey) {
        return headerMap.get(headerKey);
    }

    //Set/add header value for specified key
    public void setHeader(String headerKey, String headerValue) {
        if (headerMap.get(headerKey) == null) { //No values prev associated with headerKey
            ArrayList<String> headerValueList = new ArrayList<String>();
            headerValueList.add(headerValue);
            headerMap.put(headerKey, headerValueList);
        } else { //headerKey already in headerMap
            headerMap.get(headerKey).add(headerValue);
        }
    }

    //Get HashSet of all headerKeys
    public HashSet getKeyHashSet() {
        return (HashSet) headerMap.keySet();
    }

    //Get array of all headerKeys
    public String[] getKeyArray() {
        return (String[]) headerMap.keySet().toArray();
    }

    //-----GET SHORTCUTS-----

    //Get value of To header
    public String getTo() {
        if (getHeaderValue("To") != null) {
            return getHeaderValue("To").get(0);
        }
        return null;
    }

    //Get value of From header
    public String getFrom() {
        if (getHeaderValue("From") != null) {
            return getHeaderValue("From").get(0);
        }
        return null;
    }

    //Get value of Subject header
    public String getSubject() {
        if (getHeaderValue("Subject") != null) {
            return getHeaderValue("Subject").get(0);
        }
        return null;
    }

    //Get value of Date header
    public String getDate() {
        if (getHeaderValue("Date") != null) {
            return getHeaderValue("Date").get(0);
        }
        return null;
    }

    //Get value of CC header
    public String getCC() {
        if (getHeaderValue("CC") != null) {
            return getHeaderValue("CC").get(0);
        }
        return null;
    }

    //Get value of BCC header
    public String getBCC() {
        if (getHeaderValue("BCC") != null) {
            return getHeaderValue("BCC").get(0);
        }
        return null;
    }

    //-----Message Number-----

    public int getMessageNum() {
        return messageNum;
    }

    public void setMessageNum(int newMsgNum) {
        messageNum = newMsgNum;
    }
}
