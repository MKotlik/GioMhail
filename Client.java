import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Client{
    public static void main(String[]args){
	BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
	boolean quitUser=false;
	String mode="WELCOME";
	POPSession POP;
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
	    }
	}else if(mode.equals("POP_SETUP")){
	    sysOut.println("Setup POP Session");
	    sysOut.println("Please enter host and what port you would like to connect to, or enter back [back] to return to send/view, or enter exit [exit] to exit");
	    sysOut.println("<host port>, [back], [exit]");
	    sysOut.println();
	    sysOut.println(">");
	    userInput=sysIn.readLine();
	    if(userInput.equalsIgnoreCase("back")){
		mode="PROT_CHOOSE";
	    }else if(userInput.equalsIgnoreCase("exit")){
		quitUser=false;
	    }else{
		int space=findSpace(userInput);
		POP=new POPSession(userInput.substring(0,space),userInput.substring(space+1,userInput.length()));
		if(POP.connect()){
		    mode="POP_LOGIN";
		}else{
		    sysOut("Connection failed, please try again");
		}
	    }
	}else if(mode.equals("POP_LOGIN")){
	    sysOut.println("Login Screen");
	    sysOut.println("please enter username and password or enter back [back] to go back or enter exit [exit] to exit");
	    sysOut.println("<user pass>, [back], [exit]");
	    sysOut.println();
	    sysOut.println(">");
	    userInput=sysIn.readLine();
	    if(userInput.equalsIgnoreCase("back")){
		mode="POP_SETUP";
	    }else if(userInput.equalsIgnoreCase("exit")){
		quitUser=false;
	    }else{
		int space=findSpace(userInput);
		if(POP.login(userInput.substring(0,space),userInput.substring(space+1,userInput.length()))){
		    sysOut("Login succesful");
		    mode="POP_ACTIONS";
		}else{
		    sysOut("Login failed, please try again");
		}
	    }
	}else if(mode.equals("POP_ACTIONS")){
	    sysOut.println("Main POP Screen");
	    sysOut.println("You have "+POP.unreadMessages()+" unread messages");
	    printMessages();
	    sysOut.println("Would you like to read [read + <number of message you want to read>], go back [back] or exit [exit]?");
	    sysOut.println("[read <num>], [back], [exit]");
	    sysOut.println();
	    sysOut.println(">");
	}
    }
    public int findSpace(String input){
	for (int i=0;i<input.length;i++){
	    if (input.substring(i,i+1).equals(" ")){
		return i;
	    }
	}
	return -1;
    }
    public void printMessages(){
	for (int i=POP.getMessageCount();i>-1;i--){
	}
    }
}
