import java.util.*;

//Note, values should be ArrayList<String> b/c there can be duplicate headers
//All methods getting headers should take this into account

/* TODO
 - public void fillHeader(ArrayList<String> ALSHeaders)
 - public String getHeader(String headerKey)
 - public void setHeader(String headerKey, String headerValue)
 - public void getTo()
 - public void getFrom()
 - public void getSubject()
 - public void getDate()
 - public void getCC()
 - public void getBCC()
 */

public class Header {
    //This should be private
    public HashMap<String, ArrayList<String>> headerStore;

    //Constructor
    public Header(ArrayList<String> top) {
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).substring(0, 3).equals("Dat")) {
                header.put("Date", top.get(i).substring(6, top.get(i).length()));
            } else if (top.get(i).substring(0, 3).equals("Fro")) {
                header.put("From", top.get(i).substring(6, top.get(i).length()));
            } else if (top.get(i).substring(0, 3).equals("To:")) {
                header.put("To", top.get(i).substring(4, top.get(i).length()));
            } else if (top.get(i).substring(0, 3).equals("Sub")) {
                header.put("Subject", top.get(i).substring(8, top.get(i).length()));
            } else if (top.get(i).substring(0, 3).equals("CC:")) {
                header.put("CC", top.get(i).substring(4, top.get(i).length()));
            } else if (top.get(i).substring(0, 3).equals("BCC")) {
                header.put("BCC", top.get(i).substring(5, top.get(i).length()));
            }
        }
    }

    //Fill in all header values from ArrayList<String> of Header output
    public void fillHeader(ArrayList<String> HeaderList) {}
    //Get ArrayList<String> containig header attributes for specified key
    public ArrayList<String> getHeader(String headerKey) {}
    
    //Set/add header value for specified key
    public void setHeader(String headerKey, String headerValue) {
        if (headerStore.get(headerKey) == null) { //No values prev associated with headerKey
            ArrayList<String> headerValueList = ArrayList<String>();
            headerValueList.add(headerValue);
            headerStore.put(headerKey, headerValueList);
        } else { //headerKey already in headerStore
            headerStore.get(headerKey).add(headerValue);
        }
    }
    //Get value of To header
    public void getTo() {}
    //Get value of From header
    public void getFrom() {}
    //Get value of Subject header
    public void getSubject() {}
    //Get value of Date header
    public void getDate() {}
    //Get value of CC header
    public void getCC() {}
    //Get value of BCC header
    public void getBCC() {}
}
