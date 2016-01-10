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
	
    public boolean send(String sub, String message, String to, String from){
		try{
			Socket smtpPipe=new Socket(mailHost,SMTP_PORT);
			if(smtpPipe==null){
				return false;
			}
			InputStream is=smtpPipe.getInputStream();
			OutputStream os=smtpPipe.getOutputStream();
			in=new BufferedReader(new InputStreamReader(is));
			out=new PrintWriter(new OutputStreamWriter(os),true);
			System.out.print("HELO "+localHost.getHostName());
			out.print("HELO "+localHost.getHostName());
			String response=in.readLine();
			System.out.println("response: "+response);
			if(! response.substring(0,1).equals("2")){
				return false;
			}
			System.out.print("MAIL From:<"+from+">");
			out.println("MAIL From:<"+from+">");
			response=in.readLine();
			System.out.println("response: "+response);
			if(! response.substring(0,1).equals("2")){
				return false;
			}
			System.out.println("RCPT To:<"+to+">");
			out.println("RCPT To:<"+to+">");
			response=in.readLine();
			System.out.println("response: "+response);
			System.out.println("DATA");
			out.println("DATA");
			response=in.readLine();
			if(! response.substring(0,3).equals("354")){
				return false;
			}
			out.println("Subject: "+sub);
			out.println(message);
			out.println(".");
		}catch(IOException e){
			System.out.println("IOException");
		}
    }
}