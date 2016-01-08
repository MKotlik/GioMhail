import java.net.*;
import java.io.*;
public class SMTP{
    public final static int SMTP_PORT=25;
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
    public boolean send(String message, String to, String from){
	try{
	    Socket smtpPipe=new Socket(mailHost,SMTP_PORT);
	    if(smtpPipe==null){
		return false;
	    }
	    InputStream is=smtpPipe.getInputStream();
	    OutputStream os=smtpPipe.getOutputStream();
	    in=new BufferedReader(new InputStreamReader(is));
	    out=new PrintWriter(new OutputStreamWriter(os),true);
	    out.print("HELO "+localHost.getHostName());
	    String response=in.readLine();
	    System.out.println("response: "+response);
	    if(not response.substring(0,1).equals("2")){
		return false;
	    }
	    out.print("MAIL <"+to+">");
	}catch(IOException e){
	    System.out.println("IOException");
	}
    }
}