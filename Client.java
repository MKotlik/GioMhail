/* GioMhail by Coolgle
   - Client
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
   - Improve/finalize menu formats

*/

import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Client {
    private Message message;
    
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
                userInput = sysIn.readLine();
                if (userInput.equalsIgnoreCase("y")) {
                    mode = "PROT_CHOOSE";
                } else if (userInput.equalsIgnoreCase("exit")) {
                    quitUser = true;
                } else{
                    statusMsg="Please enter a valid command!";
                }
            } else if (mode.equals("PROT_CHOOSE")) {
		sysOut.println("Welcome to GioMhail, your go-to email client!\n" +
			       "Would you like to read [read], send [send] or exit [exit]?\n" +
			       "Cmds: [read], [send], [exit]");
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		userInput = sysIn.readLine();
		if (userInput.equalsIgnorecCase("send")) {
		    mode = "SMTP_SETUP";
		} else if (userInput.equalsIgnorecCase("view")) {
		    mode = "POP_SETUP";
		} else if (userInput.equalsIgnorecCase("exit")) {
                    quitUser = true;
		} else {
		    statusMsg="Please enter a valid command";
		}
            } else if (mode.equals("SMTP_SETUP")) {
            }else if (mode.equals("POP_SETUP")) {
		sysOut.println("Setup POP Session");
		sysOut.println("Please enter host and what port you would like to connect to, or enter back [back] to return to send/view, or enter exit [exit] to exit");
		sysOut.println("Cmds: <host port>, [back], [exit]");
		sysOut.println("");
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		userInput = sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")) {
		    mode = "PROT_CHOOSE";
		} else if (userInput.equalsIgnoreCase("exit")) {
		    quitUser = true;
		} else {
		    int space = findSpace(userInput);
		    POP = new POPSession(userInput.substring(0, space), userInput.substring(space + 1, userInput.length()));
		    if (POP.connect()) {
			mode = "POP_LOGIN";
			POP.disconnect();
		    } else {
			statusMsg = "Connection failed, please try again";
		    }
		}
	    } else if (mode.equals("POP_LOGIN")) {
		sysOut.println("Login Screen");
		sysOut.println("please enter username and password or enter back [back] to go back or enter exit [exit] to exit");
		sysOut.println("<user pass>, [back], [exit]");
		sysOut.println();
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println(">");
		userInput = sysIn.readLine();
		if (userInput.equalsIgnoreCase("back")) {
		    mode = "POP_LOGIN";
		} else if (userInput.equalsIgnoreCase("exit")) {
		    quitUser = true;
		} else {
		    int space = findSpace(userInput);
		    if (POP.connect()){
			setUser(userInput.substring(0, space));
			setPass(userInput.substring(space + 1, userInput.length()));
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
		if (serverInput.equalsIgnoreCase("back")){
		    mode="POP_SETUP";
		}else if(serverInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    if(POP.connect()){
			if (POP.login()){
			    if(showInbox(serverInput)){
				mode="POP_MESSAGE_SELECT";
			    }else{
				statusMsg="Please enter valid command";
			    }
			}else{
			    statusMsg="Login failed";
			    mode="POP_LOGIN";
			}
			POP.disconnect();
		    }else{
			statusMs="Connection issues";
			mode="POP_SETUP";
		    }
		}
	    }else if (mode.equals("POP_MESSAGE_SELECT")){
		sysOut.println("Please enter number of message you want to read [<num less than number of messages you have>], back [back] or exit [exit]")
		if (! statusMsg.equals("")) {
		    sysOut.println(statusMsg);
		    statusMsg="";
		}
		sysOut.println("|>");
		if (serverInput.equalsIgnoreCase("back")){
		    mode="POP_MAIN";
		}else if(serverInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    if (POP.connect()){
			if(POP.login()){
			    message=POP.getMessage(serverInput);
			    mode="READ_MESSAGE";
			}else{
			    msgStatus="Login failed";
			    mode="POP_LOGIN";
			}
			POP.disconnect();
		    }
		}
	    }else if(mode.equals("READ_MESSAGE")){
		sysOut.println("Message\n");
		printMessage();
		sysOut.println("Enter back [back] or exit [exit] \n|>");
		serverInput=sysIn.readLine();
		if (serverInput.equalsIgnoreCase("back")){
		    mode="POP_MESSAGE_SELECTOR";
		}else if(serverInput.equalsIgnoreCase("exit")){
		    quitUser=true;
		}else{
		    statusMsg="Input valid command";
		}
	    }
	}
    }
    public int findSpace(String input){
	for (int i = 0; i < input.length; i++) {
	    if (input.substring(i, i + 1).equals(" ")) {
		return i;
	    }
	}
	return -1;
    }
    
    public void printMessages() {
	for (int i = POP.getMessageCount(); i > -1; i--) {
	}
    }
}

