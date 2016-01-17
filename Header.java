import java.util.*;

public class Header {
    public HashMap<String, String> header;

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
}
