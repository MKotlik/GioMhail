/* GioMhail by Coolgle
   - POPSession
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

import java.util.*;

public class SMTPSession extends Session {
    private HeaderStore header;
    
    //SMTPSession constructor
    public SMTPSession(String host, int port) {
        super(host, port, "SMTP");
    }
    
    public boolean sendMail(Message newMsg){
	header=newMsg.getHeaderStore();
	writeServer("MAIL From: " + header.getFrom());
	String serverInput=read(false).get(0);
	if(checkResponseCode(serverInput, "250")){
	    writeServer("RCPT To: " + header.getTo());
	    serverInput=read(false).get(0);
	    if(checkResponseCode(serverInput, "250")){
		writeServer("DATA");
		serverInput=read(false).get(0);
		if(checkResponseCode(serverInput, "354")){
		    writeServer(newMsg.getMessageBody());
		    serverInput=read(true).get(0);
		    if(checkResponseCode(serverInput, "250")){
			return true;
		    }else{
			return false;
		    }
		}else{
		    return false;
		}
	    }else{
		return false;
	    }
	}else{
	    return false;
	}
    }

}
