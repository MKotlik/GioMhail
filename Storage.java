/* GioMhail by Coolgle
   - Storage
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

import java.util.*;
import java.io.*;

public class Storage {
    private PrintWriter out;

    public String saveMessage(Message newMsg, String fileName) {
        try {
            out = new PrintWriter("saved_emails/"+fileName+"/"+newMsg.getHashID());
            writeHeader(newMsg.getHeaderStore());
            out.println(""); //print blank line to separate headers from body
            writeMessage(newMsg);
            out.close();
            return "SUCCESS";
        } catch (FileNotFoundException e) {
            return "ERROR";
        }
    }

    public String saveFile(String fileBody, String fileName) {
        try {
            out = new PrintWriter(fileName);
            out.print(fileBody);
            out.close();
            return "SUCCESS";
        } catch (FileNotFoundException e) {
            return "ERROR";
        }
    }

    private void writeHeader(HeaderStore msgHeaders) {
        String[] headerKeys = msgHeaders.getKeyArray();
        for (int i = 0; i < headerKeys.length; i++) {
            ArrayList<String> headerValueList = msgHeaders.getHeaderValue(headerKeys[i]);
            for (int j = 0; j < headerValueList.size(); j++) {
                String line = headerKeys[i] + ": " + headerValueList.get(j);
                out.println(line);
            }
        }
    }

    private void writeMessage(Message newMsg) {
        out.print(newMsg.getMessageBody());
    }

    public ArrayList<String> getMsg(String fileName, String folderName){
	ArrayList<String> al1 = new ArrayList<String>();
	try{
	    Scanner sc = new Scanner(new File("saved_emails/"+folderName+"/"+fileName));
	    while (sc.hasNextLine()){
		al1.add(sc.nextLine());
	    }
	    return al1;
	}catch(FileNotFoundException e){
	    al1.add("FILENOTFOUND");
	    return al1;
	}
    }
    //-----FILE/LOGO METHODS-----

    public static ArrayList<String> getFileStrings(String fileName) {
        ArrayList<String> fileLines = new ArrayList<String>();
        try {
            BufferedReader readFromFile = new BufferedReader(new FileReader(fileName));
            String line = null;
            while((line = readFromFile.readLine()) != null) {
                fileLines.add(line);
            }
            readFromFile.close();
        } catch(FileNotFoundException e) {
            fileLines.add("");
            fileLines.set(0, "FILE NOT FOUND");
        } catch(IOException e) {
            fileLines.add("");
            fileLines.set(0, "READ ERROR");
        }
        return fileLines;
    }
}
