import java.io.*;
import java.net.*;
import javax.net.ssl.*;
public class POPSession{
    public int POP_Port;
    public String host;
    public BufferedWriter serverWriter;
    public BufferedReader serverReader;
    public POPSession(int port, String tHost){
	POP_Port=port;
	host=tHost;
    }
    public boolean connect(){
	SSLSocketFactory mainFactory = (SSLSocketFactory) SSLSocketFactory.getDefault(); //Get default SSL socket factory
	try {
            SSLSocket clientSocket = (SSLSocket) mainFactory.createSocket("pop.mail.yahoo.com", 995); //create, connect, start handshake
	    if (clientSocket==null){
		return false;
	    }
            BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())); //Write to server
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //Read from server
	}catch(IOException e){
	    System.err.println(e.toString());
	}
	return true;
    }
    public boolean login(String user, String pass){
	try{
	    String serverInput=null;
	    serverWriter.write("user "+user,0,5+user.length());
	    serverWriter.newLine();
	    serverWriter.flush();
	    serverInput=serverReader.readLine();
	    if (! serverInput.substring(0,3).equalsIgnoreCase("+OK")){
		return false;
	    }
	    serverWriter.write("pass "+pass,0,5+pass.length());
	    serverWriter.newLine();
	    serverWriter.flush();
	    serverWriter.flush();
	    serverInput=serverReader.readLine();
	    if (! serverInput.substring(0,3).equalsIgnoreCase("+OK")){
		return false;
	    }
	}catch(IOException e){
	    System.err.println(e.toString());
	}
	return true;	    
    }
}
