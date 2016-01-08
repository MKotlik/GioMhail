import java.net.*;
import java.io.*;
public class SMTP{
    InetAddress mailHost;
    InetAddress localHost;
    BufferedReader in;
    PrintWriter out;
    public SMTP(String host){
	try{
	    mailHost=InetAddress.getByName(host);
	    localHost=InetAddress.getLocalHost();
	    System.out.println("mailHost: "+mailHost);	
	    System.out.println("localHost: "+localHost);
	}catch(UnknownHostException e){
	    System.out.println("unknown host");
	}
    }
    /*    public boolean send(String message, String to, string from){
	  }*/
}