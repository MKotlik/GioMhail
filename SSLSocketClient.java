/* SslSocketClient.java
 - Copyright (c) 2014, HerongYang.com, All Rights Reserved.
 */
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
public class SSLSocketClient {
   public static void main(String[] args) {
	  java.lang.System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "false");
      BufferedReader in = new BufferedReader(
         new InputStreamReader(System.in));
      PrintStream out = System.out;
      SSLSocketFactory f = 
         (SSLSocketFactory) SSLSocketFactory.getDefault();
      try {
         SSLSocket c =
           (SSLSocket) f.createSocket("smtp.gmail.com", 465);
         printSocketInfo(c);
         //c.startHandshake();
		 printConnectionInfo(c);
         BufferedWriter w = new BufferedWriter(
            new OutputStreamWriter(c.getOutputStream()));
         BufferedReader r = new BufferedReader(
            new InputStreamReader(c.getInputStream()));
         String m = null;
         while ((m=r.readLine())!= null) {
            out.println(m);
            m = in.readLine();
            w.write(m,0,m.length());
            w.newLine();
            w.flush();
         }
         w.close();
         r.close();
         c.close();
      } catch (IOException e) {
         System.err.println(e.toString());
      }
   }
   private static void printSocketInfo(SSLSocket s) {
      System.out.println("Socket class: "+s.getClass());
      System.out.println("   Remote address = "
         +s.getInetAddress().toString());
      System.out.println("   Remote port = "+s.getPort());
      System.out.println("   Local socket address = "
         +s.getLocalSocketAddress().toString());
      System.out.println("   Local address = "
         +s.getLocalAddress().toString());
      System.out.println("   Local port = "+s.getLocalPort());
      System.out.println("   Need client authentication = "
         +s.getNeedClientAuth());
      SSLSession ss = s.getSession();
      System.out.println("   Cipher suite = "+ss.getCipherSuite());
      System.out.println("   Protocol = "+ss.getProtocol());
   }
   
   private static void printConnectionInfo(SSLSocket s) {
	   SSLSession currentSession = s.getSession();
	   System.out.println("Protocol: " + currentSession.getProtocol());
	   System.out.println("Cipher Suite: " + currentSession.getCipherSuite());
	   System.out.println("Host: " + currentSession.getPeerHost());
	   System.out.println("Host Port: " + currentSession.getPeerPort());
	   
   }
}