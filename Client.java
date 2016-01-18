/* GioMhail by Coolgle
   - Client
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
   - Improve/finalize menu formats

*/

import java.io.*;
import java.util.*;
import java.net.*;
import javax.net.ssl.*;

public class Client {
    private Message message;
    private HeaderStore header;
    
    public static void main(String[] args) {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
        boolean quitUser = false;
        String mode = "WELCOME";
        String statusMsg = "";

        POPSession POP;
        while (!quitUser) {
            if (mode.equals("WELCOME")) {
                sysOut.println("Welcome to GioMhail, your go-to email client!\n" +
			       "Would you like to continue [y] or exit [exit]?\n" +
			       "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)");
                sysOut.println(""); //Blank line
                if (! statusMsg.equals("")) {
                    sysOut.println(statusMsg);
		    statusMsg="";
                }
                sysOut.print("|>");
                String userInput = sysIn.readLine();
                if (userInput.equalsIgnoreCase("y")) {
                    mode = "PROT_CHOOSE";
                } else if (userInput.equalsIgnoreCase("exit")) {
                    quitUser = true;
                } else{
                    statusMsg="Please enter a valid command!";
                }
            } else if (mode.equals("PROT_CHOOSE")) {
		System.out.println("Welcome to GioMhail, your go-to email client!\n" +
			       "Would you like to read [read], send [send] or exit [exit]?\n" +
			       "Cmds: [read], [send], [exit]");
		if (! statusMsg.equals("")) {
		    System.out.println(statusMsg);
		    statusMsg="";
		}
		System.out.println("|>");
		String userInput = sysIn.readLine();
		if (userInput.equalsIgnoreCase("send")) {
		    mode = "SMTP_SETUP";
		} else if (userInput.equalsIgnoreCase("view")) {
		    mode = "POP_SETUP";
		} else if (userInput.equalsIgnoreCase("exit")) {
                    quitUser = true;
		} else {
		    statusMsg="Please enter a valid command";
		}
            } else if (mode.equals("SMTP_SETUP")) {
            }else if (mode.equals("POP_SETUP")) {
		System.out.println("Setup POP Session");
		System.out.println("Please enter port and what host you would like to connect to, or enter back [back] to return to send/view, or enter exit [exit] to exit");
		System.out.println("Cmds: <port host>, [back], [exit]");
		System.out.println("");
		if (! statusMsg.equals("")) {
		    System.out.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		String userInput = sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")) {
		    mode = "PROT_CHOOSE";
		} else if (userInput.equalsIgnoreCase("exit")) {
		    quitUser = true;
		} else {
		    int space = findSpace(userInput);
		    POP = new POPSession(Integer.parseInt(userInput.substring(0, space)), userInput.substring(space + 1, userInput.length()));
		    if (POP.connect()) {
			mode = "POP_LOGIN";
			POP.disconnect();
		    } else {
			statusMsg = "Connection failed, please try again";
		    }
		}
	    } else if (mode.equals("POP_LOGIN")) {
		System.out.println("Login Screen");
		System.out.println("please enter username and password or enter back [back] to go back or enter exit [exit] to exit");
		System.out.println("<user pass>, [back], [exit]");
		System.out.println();
		if (! statusMsg.equals("")) {
		    System.out.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println(">");
		String userInput = sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")) {
		    mode = "POP_LOGIN";
		} else if (userInput.equalsIgnoreCase("exit")) {
		    quitUser = true;
		} else {
		    int space = findSpace(userInput);
		    if (POP.connect()){
			POP.setUser(userInput.substring(0, space));
			POP.setPass(userInput.substring(space + 1, userInput.length()));
			if (POP.login(POP.getUser(), POP.getPass())){
			    mode = "POP_ACTIONS";
			}else{
			    statusMsg="Login failed";
			}
			POP.disconnect();
		    } else {
			statusMsg="Connection issues";
			mode="POP_SETUP";
		    }
		}
	    } else if (mode.equals("POP_MAIN")) {
		sysOut.println("Main POP Screen");
		if (POP.connect()){
		    if (POP.login()){
			sysOut.println("You have " + POP.getMessageCount() + " messages");
			POP.disconnect();
			sysOut.println("How many messages would you like to see [<number of messages you want to read(less than messages you have)>], go back [back] or exit [exit]?");
			sysOut.println("[<num>], [back], [exit]");
			sysOut.println();
		    }else{
			statusMsg="Login failed";
			mode="POP_LOGIN";
		    }
		    POP.disconnect();
		}else{
		    statusMsg="Connection issues";
		    mode="POP_SETUP";
		}
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		String userInput=sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")){
		    mode="POP_SETUP";
		}else if(userInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    if(POP.connect()){
			if (POP.login()){
			    ArrayList<HeaderStore> headers=POP.getHeaderStoreList(Integer.parseInt(userInput));
			    printMessages(headers, Integer.parseInt(userInput));
			    mode="POP_MESSAGE_SELECT";
			}else{
			    statusMsg="Login failed";
			    mode="POP_LOGIN";
			}
			POP.disconnect();
		    }else{
			statusMsg="Connection issues";
			mode="POP_SETUP";
		    }
		}
	    }else if (mode.equals("POP_MESSAGE_SELECT")){
		sysOut.println("Please enter number of message you want to read [<num less than number of messages you have>], back [back] or exit [exit]");
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		String userInput=sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")){
		    mode="POP_MAIN";
		}else if(userInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    if (POP.connect()){
			if(POP.login()){
			    message=POP.getMessage(Integer.parseInt(userInput));
			    header=message.getHeaderStore();
			    mode="READ_MESSAGE";
			}else{
			    statusMsg="Login failed";
			    mode="POP_LOGIN";
			}
			POP.disconnect();
		    }
		}
	    }else if(mode.equals("READ_MESSAGE")){
		System.out.println("Message\n");
		printMessage();
		System.out.println("Enter back [back] or exit [exit]");
		if (! statusMsg.equals("")) {
		    System.out.println(statusMsg);
		    statusMsg="";
		}
		System.out.println("|>");
		String userInput=sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")){
		    mode="POP_MESSAGE_SELECTOR";
		}else if(userInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    statusMsg="Input valid command";
		}
	    }
	}
    }
    public static int findSpace(String input){
	for (int i = 0; i < input.length(); i++) {
	    if (input.substring(i, i + 1).equals(" ")) {
		return i;
	    }
	}
	return -1;
    }
    
    public static void printMessages(ArrayList<HeaderStore> headers, int num) {
	for (int i = headers.size(); i > -1; i--) {
	    System.out.println(i+" From: "+headers.get(i).getFrom()+"   Subject: "+headers.get(i).getSubject());
	}
    }
    public static void printMessage() {
	System.out.println("From: "+header.getFrom()+"\n"+"Subject: "+header.getSubject());
	System.out.println(message.getMessageBody());
    }
}

