import java.util.*;
import java.io.*;

public class Storage{
    private PrintWriter out;

    public String saveMessage(Message newMsg){
	try{
	    out=new PrintWriter("file.txt");
	    writeHeader(newMsg, newMsg.getHeaderStore());
	    writeMessage(newMsg);
	    out.close();
	    return "SUCCESS";
	}catch(FileNotFoundException e){
	    return "ERROR";
	}
    }

    private void writeHeader(Message newMsg, HeaderStore msgHeaders){
        String[] headerKeys = msgHeaders.getKeyArray();
        for (int i = 0; i < headerKeys.length; i++) {
            ArrayList<String> headerValueList = msgHeaders.getHeaderValue(headerKeys[i]);
            for (int j = 0; j < headerValueList.size(); j++) {
                String line = headerKeys[i] + ": " + headerValueList.get(j);
                out.println(line);
            }
        }
    }

    private void writeMessage(Message newMsg){
	out.print(newMsg.getMessageBody());
    }

    private int createFileName(HeaderStore msgHeaders){
	String unHashed = "";
	String To = msgHeaders.getTo();
	String From = msgHeaders.getFrom();
	String Date = msgHeaders.getDate();
	String Subject = msgHeaders.getSubject();
	unHashed = To+":"+From+":"+Date+":"+Subject;
	return unHashed.hashCode();
    }

}
