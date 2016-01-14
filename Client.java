import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Client{
    public static void main(String[]args){
	BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
	boolean quitUser=false;
	String mode="WELCOME";
	while (! quitUser){
	    if (mode.equals("WELCOME")){
		sysOut.println("Welcome to GioMhail, your go to email client");
		sysOut.println("Would you like to continue [y] or exit [exit]?");
		userInput=sysIn.readLine();
		if (userInput.equalsIgnoreCase("y")){
		    mode="PROT_CHOOSE";
		}if else(userInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    sysOut.println("please enter valid text");
		}
	    }else if(mode.equals("PROT_CHOOSE")){
		sysOut.println("Would you like to send [send], view [view] or exit [exit]?");
		userInput=sysIn.readLine();
		if (userInput.equalsIgnorecCase("send")){
		    mode="SMTP_SETUP";
		}else if(userInput.equalsIgnorecCase("view")){
		    mode="POP_SETUP";
		}else if(userInput.equalsIgnorecCase("exit")){
		    quitUser=true;
		}else{
		    sysOut.println("please enter valid text");
		}
	    }else if(mode.equals("SMTP_SETUP")){
		sysOut.println("Please enter host and what port you would like to connect to, or enter back [back] to return to raed/send, or enter exit [exit] to exit");
		
		
