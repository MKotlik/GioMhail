/* GioMhail by Coolgle
 - Header
 - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
 - APCS Term 1 Final Project, Stuyvesant High School
 */

//Note, values should be ArrayList<String> b/c there can be duplicate headers
//All methods getting headers should take this into account

/* TODO
 - [DONE] public void fillHeader(ArrayList<String> ALSHeaders)
 - [DONE] public String getHeader(String headerKey)
 - [DONE] public void setHeader(String headerKey, String headerValue)
 - public void getTo()
 - public void getFrom()
 - public void getSubject()
 - public void getDate()
 - public void getCC()
 - public void getBCC()
 */

import java.util.*;


public class Header {
    //This should be private
    private HashMap<String, ArrayList<String>> headerStore;

    //Constructor
    public Header(ArrayList<String> headerLines) {
        fillHeader(headerLines);
    }

    //Fill in all header values from ArrayList<String> of Header output
    public void fillHeader(ArrayList<String> HeaderLines) {
        String headerKey = "";
        String headerValue = "";
        for (int i = 0; i < HeaderLines.size(); i++) {
            if (i == 0) { //Spec. case, start headerKey and headerValue lcl storage
                headerKey = findHeaderKey(HeaderLines.get(i));
                headerValue = findHeaderValue(HeaderLines.get(i));
            } else if (i == HeaderLines.size() - 1) { //Spec. case, always put prev. and/or new header key/value pair
                if (HeaderLines.get(i).startsWith(" ")) { //If continuation of multi-line headerValue
                    headerValue += HeaderLines.get(i).substring(1, HeaderLines.get(i).length()); //append to cur. value
                    setHeader(headerKey, headerValue); //Add pair to headerStore, or append to existing pair
                } else { //If new header attribute
                    setHeader(headerKey, headerValue); //Put the old header key/value pair in headerStore
                    headerKey = findHeaderKey(HeaderLines.get(i)); //Get new header key/value pair
                    headerValue = findHeaderValue(HeaderLines.get(i));
                    setHeader(headerKey, headerValue); //Put new header key/value pair in headerStore
                }
            } else { //Any line but first and last
                if (HeaderLines.get(i).startsWith(" ")) { //If continuation of multi-line headerValue
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
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ':') { //Find colon at end of header key
                return line.substring(0, i); //Return header key up to colon
            }
        }
        return null; //or ""?
    }

    //Returns header value from a header response line
    private String findHeaderValue(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') { //Find space before first char of header value
                return line.substring(i + 1, line.length()); //Return header value from 1st char to end
            }
        }
        return null; //or ""?
    }

    //Get ArrayList<String> containing header attributes for specified key
    //Returns null if no such headerKey in headerStore
    public ArrayList<String> getHeader(String headerKey) {
        return headerStore.get(headerKey);
    }

    //Set/add header value for specified key
    public void setHeader(String headerKey, String headerValue) {
        if (headerStore.get(headerKey) == null) { //No values prev associated with headerKey
            ArrayList<String> headerValueList = new ArrayList<String>();
            headerValueList.add(headerValue);
            headerStore.put(headerKey, headerValueList);
        } else { //headerKey already in headerStore
            headerStore.get(headerKey).add(headerValue);
        }
    }

    //Get value of To header
    public ArrayList<String> getTo() {
	return getHeader("To");
    }
    //Get value of From header
    public ArrayList<String> getFrom() {
	return getHeader("From");
    }
    //Get value of Subject header
    public ArrayList<String> getSubject() {
	return getHeader("Subject");
    }
    //Get value of Date header
    public ArrayList<String> getDate() {
	return getHeader("Date");
    }
    //Get value of CC header
    public ArrayList<String> getCC() {
	return getHeader("CC");
    }
    //Get value of BCC header
    public ArrayList<String> getBCC() {
	return getHeader("BCC");
    }
}
