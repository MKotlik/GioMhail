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
    
    public String sendMail(Message newMsg){
	header=newMsg.getHeaderStore();
	if(! header.getTo()=null){
	    writeServer("MAIL From: " + header.getFrom());
	    String serverInput=read(false).get(0);
	    if(checkResponseCode(serverInput, "250")){
		if(! header.getTo()=null){
		    writeServer("RCPT To: " + header.getTo());
		    serverInput=read(false).get(0);
		    if(checkResponseCode(serverInput, "250")){
			writeServer("DATA");
			serverInput=read(false).get(0);
			if(checkResponseCode(serverInput, "354")){
			    String head="";
			    String[] headersAvailable=header.getKeyArray();
			    for (int i=0;i<headerAvailable.length;i++){
				head+=headerAvailable[i]+": "+header.getHeaderValue(headerAvailable[i])+"\n";
			    }
			    writeServer(head+newMsg.getMessageBody()+"\n.");
			    serverInput=read(true).get(0);
			    if(checkResponseCode(serverInput, "250")){
				return "Mail sent";
			    }else{
				return "Please enter valid message body";
			    }
			}else{
			    return "Connection issues";
			}
		    }else{
			return "Please enter valid recipient";
		    }
		}else{
		    return "Please enter a recpipient";
		}
	    }else{
		return "Please enter valid sender information";
	    }
	}else{
	    return "Please enter sender info";
	}
    }
}
