/* GioMhail by Coolgle
   - Client
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
   - Improve/finalize menu formats
   - [DONE] Add temporary Please Wait! messages
   - Improve position of wait msgs (some are currently after prompt)
   - [DONE] Fix list 2 arg reading
   - [DONE] Sanitize inputs with more than allowed # of args!!! (currently exception)
   - [DONE] Fix view cmd
   - [DONE] Add menu map to top of all screens
   - Make GioMhail logo persistent on all screens
   - [DONE] Make server info + username persistent on all relevant screens
   - [DONE] Add space after prompt
   - Line up field separators in inbox (field separators should all be in pos after longest field content)
*/

import java.io.*;
import java.util.*;
import java.net.*;
import javax.net.ssl.*;

public class Client {
    public static void main(String[] args) {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
        boolean quitUser = false; //Whether or not user has quit
        String mode = "WELCOME"; //Screen mode/stage
        String statusMsg = ""; //Any error or help messages
        String cmds = ""; //Variable command list, used in some screens
        int msgCount = 0; //updated number of msgs in inbox
        int minMsg = 0; //Oldest message being listed in inbox
        int maxMsg = 0; //Newest message being listed in inbox
        int viewMsgNum = 0; //Number of message to view

        POPSession POP = null;
        try {
            while (!quitUser) {
                //Clear the screen
                System.out.print("\033[H\033[2J");
                System.out.flush();
                //Print/check according to mode
                if (mode.equals("WELCOME")) {
                    sysOut.println("GioMhail\n" +
                            "Menu Map: Welcome\n" +
                            "\n" +
                            "Welcome to GioMhail, your go-to email client!\n" +
                            "Would you like to continue [y] or exit [exit]?\n" +
                            "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)");
                    sysOut.println(""); //Blank line
                    if (!statusMsg.equals("")) {
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> ");
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("y")) {
                        mode = "PROT_CHOOSE";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("PROT_CHOOSE")) {
                    System.out.println("GioMhail\n" +
                            "Menu Map: Welcome > Choose Read\\Send\n" +
                            "Choose Read\\Send\n" +
                            "\n" +
                            "Would you like to read [read], send [send], or exit [exit]?\n" +
                            "Cmds: [read], [send], [exit]");
                    sysOut.println("");
                    if (!statusMsg.equals("")) {
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> ");
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("send")) {
                        mode = "SMTP_SETUP";
                    } else if (userInput.equalsIgnoreCase("read")) {
                        mode = "POP_SETUP";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("SMTP_SETUP")) {
                    //SMTP Setup Here
                } else if (mode.equals("POP_SETUP")) {
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: Setup");
                    System.out.println("Read: Setup");
                    sysOut.println("");
                    System.out.println("Please enter the address and port of your POP3 server.");
                    System.out.println("Cmds: <address port>, [back], [exit]");
                    //NOTE: server info format should be address, then port (Misha)
                    System.out.println("");
                    if (!statusMsg.equals("")) {
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> ");
                    String userInput = sysIn.readLine();
		    if (checkInput(1, userInput)){
			if (userInput.equalsIgnoreCase("back")) {
			    mode = "PROT_CHOOSE";
			} else if (userInput.equalsIgnoreCase("exit")) {
			    quitUser = true;
			} else {
			    int space = findSpace(userInput);
			    if (space == -1) { //Address and port unseparated
				statusMsg = "Please enter server address and port correctly (without brackets, with space).";
			    } else {
				String POPhost = userInput.substring(0, space); //address
				int POPPort = Integer.parseInt(userInput.substring(space + 1, userInput.length())); //port
				String waitMsg = "Please Wait! Communicating with server...";
				sysOut.print(waitMsg);
				POP = new POPSession(POPhost, POPPort); //New format is host then port
				if (POP.connect()) {
				    mode = "POP_LOGIN";
				    POP.disconnect(); //Disconnect (QUIT) successful connection
				} else {
				    statusMsg = "Connection failed. Ensure correct server address and port, then try again.";
				    POP.close(); //Server resources might be open, so close()
				}
				eraseFromConsole(waitMsg);
			    }
			}
		    }else{
			statusMsg = "Please enter a valid command!";
		    }
                } else if (mode.equals("POP_LOGIN")) {
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login");
                    System.out.println("Read: Login");
		    sysOut.println(POP.getHost()+" | "+POP.getPort());
                    sysOut.println("");
                    System.out.println("Please enter username and password.");
                    System.out.println("<user pass>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) {
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|>");
                    String userInput = sysIn.readLine();
		    if (checkInput(1, userInput)){
			if (userInput.equalsIgnoreCase("back")) {
			    mode = "POP_SETUP";
			} else if (userInput.equalsIgnoreCase("exit")) {
			    quitUser = true;
			} else {
			    int spaceInd = findSpace(userInput);
			    if (spaceInd == -1) { //Address and port unseparated
				statusMsg = "Please enter username and password correctly (without brackets, with space).";
			    } else {
				String waitMsg = "Please Wait! Communicating with server...";
				sysOut.print(waitMsg);
				if (POP.connect()) { //Successfully connected to server
				    String user = userInput.substring(0, spaceInd);
				    String pass = userInput.substring(spaceInd + 1, userInput.length());
				    POP.setUser(user);
				    POP.setPass(pass);
				    if (POP.POPLogin()) { //Use login method spec. for POP, successful
					mode = "POP_MAIN";
				    } else {
					statusMsg = "Login failed. Ensure correct username and password, then try again.";
				    }
				    POP.disconnect();
				} else {
				    POP.close();
				    statusMsg = "Connection failed. Please try again.";
				    POP.close(); //Close just in case, but stay in this mode.
				}
				eraseFromConsole(waitMsg);
			    }
			}
		    }else{
			statusMsg = "Please enter a valid command!";
		    }
                } else if (mode.equals("POP_MAIN")) {
                    msgCount = 0; //Needed for displaying messages in INBOX
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Main");
                    System.out.println("Read: Main Menu");
		    sysOut.println(POP.getHost()+" | "+POP.getPort());
		    sysOut.println(POP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (POP.connect()) { //connection success
                        if (POP.POPLogin()) { //login success
                            msgCount = POP.getMessageCount();
                            eraseFromConsole(waitMsg);
                            POP.disconnect();
                            sysOut.println("You have " + msgCount + " messages.");
                            //Should print out latest message summary (Num, Date, From, Subject) here
                            sysOut.println("How many messages would you like listed? [list <num>]");
                            sysOut.println("[list <num>], [back], [exit]");
                            sysOut.println("");
                        } else { //login failed
                            eraseFromConsole(waitMsg);
                            POP.disconnect();
                            sysOut.println("Would you like to retry connection?");
                            sysOut.println("Cmds: [y], [back], [exit]");
                            sysOut.println("");
                            statusMsg = "Connection issue (login failure). Please try again.";
                        }
                    } else { //connection failed
                        eraseFromConsole(waitMsg);
                        POP.close();
                        sysOut.println("Would you like to retry connection?");
                        sysOut.println("Cmds: [y], [back], [exit]");
                        sysOut.println("");
                        statusMsg = "Connection issue (server not connected). Please try again.";
                    }
                    if (!statusMsg.equals("")) {
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> ");
                    String userInput = sysIn.readLine();
		    if (checkInput(1, userInput)){
			if (userInput.equalsIgnoreCase("y")) {
			    mode = "POP_MAIN";
			} else if (userInput.equalsIgnoreCase("back")) {
			    mode = "POP_LOGIN";
			} else if (userInput.equalsIgnoreCase("exit")) {
			    quitUser = true;
			} else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("list")) { //list cmd
			    int spaceInd = findSpace(userInput);
			    if (spaceInd == -1) { //List and number unseparated
				statusMsg = "Please enter list, followed by space, followed by number of messages to list.";
			    } else { //Correct format
				int numMsgs = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
				if (numMsgs > msgCount) {
				    numMsgs = msgCount;
				}
				minMsg = msgCount - numMsgs + 1;
				maxMsg = msgCount;
				mode = "POP_INBOX";
			    }
			} else {
			    statusMsg = "Please enter a valid command!";
			}
		    }else{
			statusMsg = "Please enter a valid command!";
		    }
                } else if (mode.equals("POP_INBOX")) {
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Menu > Inbox");
                    System.out.println("Read: Inbox");
		    sysOut.println(POP.getHost()+" | "+POP.getPort());
		    sysOut.println(POP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (POP.connect()) { //connection success
                        if (POP.POPLogin()) { //login success
                            msgCount = POP.getMessageCount(); //update msg count
                            ArrayList<HeaderStore> HeaderStoreList = POP.getHeaderStoreList(minMsg, maxMsg);
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            sysOut.println("==================================================");
                            sysOut.println("Message Number | Date & Time Sent | From | Subject");
                            sysOut.println("__________________________________________________");
                            printMessageSummaries(HeaderStoreList);
                            sysOut.println("==================================================");
                            sysOut.println("Choose message to view [view <msgNum>], or choose number of messages to list.");
                            sysOut.println("Cmds: [view <msgNum>], [list <numMsgs>], [list <minMsg> <maxMsg>], [back], [exit]");
                            sysOut.println("");
                        } else { //login failure
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            sysOut.println("Would you like to retry connection?");
                            sysOut.println("Cmds: [y], [back], [exit]");
                            sysOut.println("");
                            statusMsg = "Connection issue (login failure). Please try again.";
                        }
                    } else { //connection failure
                        POP.close();
                        eraseFromConsole(waitMsg);
                        sysOut.println("Would you like to retry connection?");
                        sysOut.println("Cmds: [y], [back], [exit]");
                        sysOut.println("");
                        statusMsg = "Connection issue (server not connected). Please try again.";
                    }
                    if (!statusMsg.equals("")) {
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> ");
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("y")) {
                        mode = "POP_INBOX";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_MAIN";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("list")) { //list cmd
			if (checkInput(2, userInput)){
			    int spaceInd1 = findSpace(userInput);
			    if (spaceInd1 == -1) { //List and number unseparated
				statusMsg = "Please enter list command as: list <numMsgs> or list <minMsg> <maxMsg>.\n" +
                                    "Separate list and all numbers with spaces, and do not write brackets.";
			    } else { //Correct format
				if (findSpace(userInput.substring(spaceInd1 + 1, userInput.length())) != -1) { //if two args
				    int spaceInd2 = findSpace(userInput.substring(spaceInd1 + 1, userInput.length()))+spaceInd1+1;
				    sysOut.println(spaceInd1);
				    sysOut.println(spaceInd2);
				    minMsg = Integer.parseInt(userInput.substring(spaceInd1 + 1, spaceInd2)); //first arg
				    maxMsg = Integer.parseInt(userInput.substring(spaceInd2 + 1, userInput.length())); //2nd arg
				    //Sanitize minMsg and maxMsg, don't allow user to break with stupid things
				    if (minMsg < 1) {
					minMsg = 1;
				    }
				    if (minMsg > msgCount) {
					minMsg = msgCount;
				    }
				    if (minMsg > maxMsg) {
					minMsg = 1;
				    }
				    if (maxMsg < 1) {
					maxMsg = 1;
				    }
				    if (maxMsg > msgCount) {
					maxMsg = msgCount;
				    }
				    if (maxMsg < minMsg) {
					maxMsg = msgCount;
				    }
				    mode = "POP_INBOX";
				} else { //if 1 arg (list numMsgs)
				    int numMsgs = Integer.parseInt(userInput.substring(spaceInd1 + 1, userInput.length()));
				    //Sanitize numMsgs
				    if (numMsgs < 0) {
					numMsgs = 0;
				    }
				    if (numMsgs > msgCount) {
					numMsgs = msgCount;
				    }
				    minMsg = msgCount - numMsgs + 1;
				    maxMsg = msgCount;
				    mode = "POP_INBOX";
				}
			    }
			}else{
			    statusMsg = "Please enter a valid command!";
			}			    
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("view")) {
			if (checkInput(1, userInput)){
			    int spaceInd = findSpace(userInput);
			    if (spaceInd == -1) { //View and number unseparated
				statusMsg = "Please enter view, followed by space, followed by the number of the message to view.";
			    } else { //Proper format
				viewMsgNum = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
				//Check that viewMsgNum is within range
				if (viewMsgNum < 1 || viewMsgNum > msgCount) { //If chosen msg is out of range
				    statusMsg = "Please choose a message that is between 1 and " + msgCount + ", inclusive.";
				} else { //Number is in range
				    mode = "POP_VIEW";
				}
			    }
			} else { //unrecognized cmd
			    statusMsg = "Please enter a valid command!";
			}
		    }
                } else if (mode.equals("POP_VIEW")) {
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Menu > Inbox > View");
                    System.out.println("Read: View Message");
		    sysOut.println(POP.getHost()+" | "+POP.getPort());
		    sysOut.println(POP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (POP.connect()) { //connection success
                        if (POP.POPLogin()) { //login success
                            Message chosenMsg = POP.getMessage(viewMsgNum);
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            if (chosenMsg == null) { //Message not found
                                sysOut.println("Please return to Inbox, choose a different message number.");
                                sysOut.println("Cmds: [back], [exit]");
                                sysOut.println("");
                                statusMsg = "No message found with number " + viewMsgNum;
                            } else { //Msg found
                                sysOut.println("==================================================");
                                sysOut.println("Message Number | Date & Time Sent | From | Subject");
                                sysOut.println("__________________________________________________");
                                sysOut.println(getMessageSummary(chosenMsg.getHeaderStore())); //print msg summary
                                sysOut.println("==================================================");
                                sysOut.println(chosenMsg.getMessageBody());
                                sysOut.println("==================================================");
                                sysOut.println("View a different message, or return to inbox.");
                                sysOut.println("Cmds: [view <msgNum>], [back], [exit]");
                                sysOut.println("");
                            }
                        } else { //login failure
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            sysOut.println("Would you like to retry connection?");
                            sysOut.println("Cmds: [y], [back], [exit]");
                            sysOut.println("");
                            statusMsg = "Connection issue (login failure). Please try again.";
                        }
                    } else { //connection failure
                        POP.close();
                        eraseFromConsole(waitMsg);
                        sysOut.println("Would you like to retry connection?");
                        sysOut.println("Cmds: [y], [back], [exit]");
                        sysOut.println("");
                        statusMsg = "Connection issue (server not connected). Please try again.";
                    }
                    if (!statusMsg.equals("")) {
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> ");
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_INBOX";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("view")) {
			if (checkInput(1, userInput)){
			    int spaceInd = findSpace(userInput);
			    if (spaceInd == -1) { //View and number unseparated
				statusMsg = "Please enter view, followed by space, followed by the number of the message to view.";
			    } else { //Proper format
				viewMsgNum = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
				//Check that viewMsgNum is within range
				if (viewMsgNum < 1 || viewMsgNum > msgCount) { //If chosen msg is out of range
				    statusMsg = "Please choose a message that is between 1 and " + msgCount + ", inclusive.";
				} else { //Number is in range
				    mode = "POP_VIEW";
				}
			    }
			}else{
			    statusMsg = "Please enter a valid command!";
			}
                    } else { //unrecognized cmd
                        statusMsg = "Please enter a valid command!";
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR: Your console is not working properly.");
        }
    }

    public static int findSpace(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i + 1).equals(" ")) {
                return i;
            }
        }
        return -1;
    }

    public static void printMessageSummaries(ArrayList<HeaderStore> HeaderStoreList) {
        for (int i = HeaderStoreList.size() - 1; i >= 0; i--) {
            System.out.println(getMessageSummary(HeaderStoreList.get(i)));
        }
    }

    public static String getMessageSummary(HeaderStore msgHeader) {
        int msgNum = msgHeader.getMessageNum();
        String date = msgHeader.getDate();
        String from = msgHeader.getFrom();
        String subject = msgHeader.getSubject();
        return msgNum + " | " + date + " | " + from + " | " + subject;
    }

    public static void eraseFromConsole(String waitMsg) {
        String eraser = "";
	String clearer = "";
        for (int i = 0; i < waitMsg.length(); i++) {
            eraser += "\b"; //Add backspace character
	    clearer += " ";
        }
        System.out.print(eraser);
        System.out.print(clearer);
        System.out.print(eraser);
    }
    public static boolean checkInput(int maxSpaces, String input){
	int numSpaces=0;
	for (int i=0;i<input.length();i++){
	    if (input.substring(i,i+1).equals(" ")){
		numSpaces+=1;
	    }
	}
	return numSpaces<=maxSpaces;
    }
}

