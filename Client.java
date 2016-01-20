/* GioMhail by Coolgle
   - Client
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* TODO
   - Improve/finalize menu formats
   - [DONE] Add temporary Please Wait! messages
   - [DONE] Fix list 2 arg reading
   - [DONE] Sanitize inputs with more than allowed # of args!!! (currently exception)
   - [DONE] Fix view cmd
   - [DONE] Add menu map to top of all screens
   - [DONE] Make server info + username persistent on all relevant screens
   - [DONE] Add space after prompt
   - [DONE] Add BCC in same fashion as CC
   - [DONE] Add a short (matches === line) dashed line below Body: on all SMTP screens

   - [DONE] Add BODY menu, which after every enter takes a userInput line and adds it to a local variables ArrayList<String>
     of the main method. There should also be an int var linePos that signifies which index of
     the ArrayList is currently getting filled. Every time BODY is looped, where the screen says Body:,
     there should be a for loop that prints out all the lines that are in the ArrayList.  There should be a carrot '>'
     on the screen directly below the last line printed, corresponding to the line currently being filled,
     and to the linePos. The prompt should stay as it was, except that commands sohuld be prefixed with $,
     to distinguish them from regular text (as in $back, $exit). There should be a new command, $delPrev, which deletes
     the previous line form the ArrayList (thus it should also not be printed to screen on next iteration), and lowers
     the linePos var. This allows the user to retype a bad line if they already entered it.
     Note, there is no way to edit text like in a text editor with java (using arrow keys to go between lines), so we
     have to make do with this cmd.

     Ex. screen:

     GioMhail
     Menu Map: ... > Login > Main > Subject > From > To > CC > BCC > Body
     Send: Body
     smtp.mail.yahoo.com | 465
     giokotlik
     =====================================================================

     ---New Email---

     Subject: test email
     From: Mikhail Kotlik <giokotlik@yahoo.com>
     To: Rando One <rando.one@gmail.com>, Rando Two <rando.two@gmail.com>
     CC:
     BCC:

     Body:
     ---------------------------------------------------------------------
     Dear Randos,

     Get lost.

     Sincerely,
     >

     =====================================================================
     Enter the body of your message line by line. Use $delPrev to jump back to the prev line.
     Note, $delPrev will also delete your prev line.
     Cmds: <line text>, [$delPrev], [$back], [$exit]

     |> $delPrev (or Mikhail Kotlik, etc.)

     =====================================================================
     TODO Cont.:

   - Make HeaderStore shortcuts return "" on non-existant headers
   - Make getMessageSummary check for "" instead of null
   - Make all message displays (top of SMTP menus) pull respective headers

   - Fixed integer parsing attempts on non-integer args (bad input)
   - Catch errors that are thrown in session methods.

   - parseFrom and parseTo: Check that no stray characters after last '>'
   - parseFrom and parseTo: Check that no spaces within brackets
   - parseTo: Check that list is properly separated by commas

   - Improve position of wait msgs (some are currently after prompt)
   - Make GioMhail logo persistent on all screens
   - Line up field separators in inbox (field separators should all be in pos after longest field content)
   - Have better, graphical dividers of menu parts

   - Turn client into an object based thing, instantiating the client and running methods in main
   - ^Make the variables instance or static variables

   - Change Client to read known String statuses based on errors/successes from Session API methods
*/

import java.io.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
        boolean quitUser = false; //Whether or not user has quit
        String mode = "WELCOME"; //Screen mode/stage
        String statusMsg = ""; //Any error or help messages
        int msgCount = 0; //updated number of msgs in inbox
        int minMsg = 0; //Oldest message being listed in inbox
        int maxMsg = 0; //Newest message being listed in inbox
        int viewMsgNum = 0; //Number of message to view
        ArrayList<String> msgBodyArray = new ArrayList<String>();
        POPSession POP = null;
        SMTPSession SMTP = null;
        Message newMsg = null; //This shall be a new message to send
        try {
            while (!quitUser) {
                //Clear the screen
                System.out.print("\033[H\033[2J");
                System.out.flush();
                //Print/check according to mode
                if (mode.equals("WELCOME")) {
                    //--Print menu header
                    sysOut.println("GioMhail\n" +
                            "Menu Map: Welcome\n" +
                            "\n" +
                            "Welcome to GioMhail, your go-to email client!\n" +
                            "Would you like to continue [y] or exit [exit]?\n" +
                            "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)");
                    sysOut.println(""); //Blank line
                    if (!statusMsg.equals("")) { //Print statusMsg
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("y")) {
                        mode = "PROT_CHOOSE";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("PROT_CHOOSE")) {
                    //--Print menu header
                    System.out.println("GioMhail\n" +
                            "Menu Map: Welcome > Choose Read\\Send\n" +
                            "Choose Read\\Send\n" +
                            "\n" +
                            "Would you like to read [read], send [send], or exit [exit]?\n" +
                            "Cmds: [read], [send], [exit]");
                    sysOut.println("");
                    if (!statusMsg.equals("")) { //Print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check User Input
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
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Send: Setup");
                    System.out.println("Send: Setup");
                    sysOut.println("");
                    System.out.println("Please enter the address and port of your SMTP server.");
                    System.out.println("Cmds: <address port>, [back], [exit]");
                    //NOTE: server info format should be address, then port (Misha)
                    System.out.println("");
                    if (!statusMsg.equals("")) { //Print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "PROT_CHOOSE";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (checkSpaces(1, userInput)) { //1 space, matches <address port> format
                        int space = findSpace(userInput);
                        String SMTPhost = userInput.substring(0, space); //address
                        int SMTPPort = Integer.parseInt(userInput.substring(space + 1, userInput.length())); //port
                        String waitMsg = "Please Wait! Communicating with server...";
                        sysOut.print(waitMsg);
                        SMTP = new SMTPSession(SMTPhost, SMTPPort); //New format is host then port
                        if (SMTP.connect()) {
                            mode = "SMTP_LOGIN";
                            SMTP.disconnect(); //Disconnect (QUIT) successful connection
                        } else {
                            statusMsg = "Connection failed. Ensure correct server address and port, then try again.";
                            SMTP.close(); //Server resources might be open, so close()
                        }
                        eraseFromConsole(waitMsg);
                    } else {
                        statusMsg = "Please enter server address and port correctly (without brackets, with space).";
                    }
                } else if (mode.equals("POP_SETUP")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: Setup");
                    System.out.println("Read: Setup");
                    sysOut.println("");
                    System.out.println("Please enter the address and port of your POP3 server.");
                    System.out.println("Cmds: <address port>, [back], [exit]");
                    //NOTE: server info format should be address, then port (Misha)
                    System.out.println("");
                    if (!statusMsg.equals("")) { //Print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "PROT_CHOOSE";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (checkSpaces(1, userInput)) { //1 space, matches <address port> format
                        int space = findSpace(userInput);
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
                    } else {
                        statusMsg = "Please enter server address and port correctly (without brackets, with space).";
                    }
                } else if (mode.equals("SMTP_LOGIN")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Send: > Setup > Login");
                    System.out.println("Send: Login");
                    sysOut.println("Server: " + SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println("");
                    System.out.println("Please enter username and password.");
                    System.out.println("Cmds: <user pass>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_SETUP";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (checkSpaces(1, userInput)) { //1 space, matches <user pass> format
                        int spaceInd = findSpace(userInput);
                        String waitMsg = "Please Wait! Communicating with server...";
                        sysOut.print(waitMsg);
                        if (SMTP.connect()) { //Successfully connected to server
                            String user = userInput.substring(0, spaceInd);
                            String pass = userInput.substring(spaceInd + 1, userInput.length());
                            SMTP.setUser(user);
                            SMTP.setPass(pass);
                            if (SMTP.SMTPLogin()) { //Use login method spec. for SMTP, successful
                                mode = "SMTP_MAIN";
                            } else {
                                statusMsg = "Login failed. Ensure correct username and password, then try again.";
                            }
                            SMTP.disconnect();
                        } else {
                            SMTP.close(); //Close just in case, but stay in this mode.
                            statusMsg = "Connection failed. Please try again.";
                        }
                        eraseFromConsole(waitMsg);
                    } else { //Unrecognized format/cmd
                        statusMsg = "Please enter username and password correctly (without brackets, with space).";
                    }
                } else if (mode.equals("POP_LOGIN")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login");
                    System.out.println("Read: Login");
                    sysOut.println("Server: " + POP.getHost() + " | " + POP.getPort());
                    sysOut.println("");
                    System.out.println("Please enter username and password.");
                    System.out.println("Cmds: <user pass>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_SETUP";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (checkSpaces(1, userInput)) { //1 space, matches <user pass> format
                        int spaceInd = findSpace(userInput);
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
                            POP.close(); //Close just in case, but stay in this mode.
                            statusMsg = "Connection failed. Please try again.";
                        }
                        eraseFromConsole(waitMsg);
                    } else { //Unrecognized format/cmd
                        statusMsg = "Please enter username and password correctly (without brackets, with space).";
                    }
                } else if (mode.equals("SMTP_MAIN")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Send: > Setup > Login > Main");
                    System.out.println("Send: Main Menu");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    sysOut.println("");
                    System.out.println("Would you like to send an email?");
                    System.out.println("Cmds: [y], [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("y")) {
                        newMsg = new Message();
                        mode = "SMTP_SUBJECT";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_LOGIN";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("SMTP_SUBJECT")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Send: > Setup > Login > Main > Subject");
                    System.out.println("Send: Subject");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("Please enter a subject");
                    System.out.println("Cmds: <subject>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_MAIN";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else { //Assume that its subject
                        newMsg.getHeaderStore().setHeader("Subject", userInput);
                        mode = "SMTP_FROM";
                    }
                } else if (mode.equals("SMTP_FROM")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Send: > Setup > Login > Main > Subject > From");
                    System.out.println("Send: From");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("Please enter your From name & address in the format " +
                            "First Last <user@server.com>, with brackets.");
                    System.out.println("Note, the address should correspond to your account on this server.");
                    System.out.println("Cmds: <First Last <from address>>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_SUBJECT";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else { //Assume that its the from address
                        if (parseFrom(userInput).equals("GOOD FROM")) {
                            newMsg.getHeaderStore().setHeader("From", userInput);
                            mode = "SMTP_TO";
                        } else {
                            statusMsg = "Please enter 1 name & 1 email address with brackets in the format " +
                                    "First Last <user@server.com>.";
                        }
                    }
                } else if (mode.equals("SMTP_TO")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > ... > Send: > Setup > Login > Main > Subject > From > To");
                    System.out.println("Send: To");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("Please enter a list of 'to' names & addresses.");
                    System.out.println("Follow the format: First Last <user@server.com>, " +
                            "First Last <user@server.com>, ... Use brackets.");
                    System.out.println("Cmds: <list of (First Last <to address>)>, [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_FROM";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else { //Assume that its the from address
                        if (parseTo(userInput).equals("GOOD TO")) {
                            newMsg.getHeaderStore().setHeader("To", userInput);
                            mode = "SMTP_CC";
                        } else {
                            statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                                    "Each set should be in the format: First Last <user@server.com>";
                        }
                    }
                } else if (mode.equals("SMTP_CC")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: ... > Send: > Setup > Login > Main > Subject > From > To > CC");
                    System.out.println("Send: CC");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("You may choose to enter a list of CC names & addresses, or enter [n] to skip.");
                    System.out.println("Follow the format: First Last <user@server.com>, " +
                            "First Last <user@server.com>, ... Use brackets.");
                    System.out.println("Cmds: <list of (First Last <CC address>)>, [n], [back], [exit]");
                    System.out.println("");
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("n")) {
                        mode = "SMTP_BCC";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_TO";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else { //Assume that its the from address
                        if (parseTo(userInput).equals("GOOD TO")) {
                            newMsg.getHeaderStore().setHeader("To", userInput);
                            mode = "SMTP_BCC";
                        } else {
                            statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                                    "Each set should be in the format: First Last <user@server.com>";
                        }
                    }
                } else if (mode.equals("SMTP_BCC")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: ... > Setup > Login > Main > Subject > From > To > CC > BCC");
                    System.out.println("Send: BCC");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("You may choose to enter a list of BCC names & addresses, or enter [n] to skip.");
                    System.out.println("Follow the format: First Last <user@server.com>, " +
                            "First Last <user@server.com>, ... Use brackets.");
                    System.out.println("Cmds: <list of (First Last <BCC address>)>, [n], [back], [exit]");
                    System.out.println("");
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("n")) {
                        mode = "SMTP_BODY";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_CC";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else { //Assume that its the from address
                        if (parseTo(userInput).equals("GOOD TO")) {
                            newMsg.getHeaderStore().setHeader("To", userInput);
                            mode = "SMTP_BODY";
                        } else {
                            statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                                    "Each set should be in the format: First Last <user@server.com>";
                        }
                    }
                } else if (mode.equals("SMTP_BODY")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: ... > Setup > Login > Main > Subject > From > To > CC > BCC > Body");
                    System.out.println("Send: Message Body");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("");
                    System.out.println("Enter the body of your message line by line.\n" +
                            "Use [$del] to jump back and delete the previous line.\n");
                    System.out.println("Cmds: <line text>, [$del], [$back], [$exit], [$send]");
                    System.out.println("");
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("$del")) {
                        if (msgBodyArray.size() > 0) {
                            eraseFromConsole(msgBodyArray.get(msgBodyArray.size() - 1));
                            msgBodyArray.remove(msgBodyArray.size() - 1);
                        } else {
                            statusMsg = "Cannot delete a line. No lines remaining.";
                        }
                    } else if (userInput.equalsIgnoreCase("$back")) {
                        mode = "SMTP_BCC";
                    } else if (userInput.equalsIgnoreCase("$exit")) {
                        quitUser = true;
                    } else if (userInput.equalsIgnoreCase("$send")) {
                        String msgBodyStr = "";
                        for (int i = 0; i < msgBodyArray.size(); i++) {
                            msgBodyStr += msgBodyArray.get(i) + "\r\n";
                        }
                        newMsg.setMessageBody(msgBodyStr);
                        mode = "SMTP_CONFIRM";
                    } else { //Assume that its the line of text
                        msgBodyArray.add(userInput);
                    }
                } else if (mode.equals("SMTP_CONFIRM")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: ... > Login > Main > Subject > From > To > CC > BCC > Body > Confirm");
                    System.out.println("Send: Confirm");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    System.out.println("=====================================================================");
                    sysOut.println("");
                    System.out.println("---New Email---");
                    System.out.println("");
                    System.out.println("Subject: " + newMsg.getHeaderStore().getSubject());
                    System.out.println("From: " + newMsg.getHeaderStore().getFrom());
                    System.out.println("To: " + newMsg.getHeaderStore().getTo());
                    System.out.println("CC: " + newMsg.getHeaderStore().getCC());
                    System.out.println("BCC: " + newMsg.getHeaderStore().getBCC());
                    System.out.println("");
                    System.out.println("Body: ");
                    System.out.println("---------------------------------------------------------------------");
                    for (int i = 0; i < msgBodyArray.size(); i++) {
                        System.out.println(msgBodyArray.get(i));
                    }
                    System.out.println(""); //blank line as cushion
                    System.out.println("=====================================================================");
                    System.out.println("");
                    System.out.println("Are you ready to send this email?");
                    System.out.println("Cmds: [y], [back], [exit]");
                    System.out.println("");
                    if (!statusMsg.equals("")) { //print statusMsg
                        System.out.println(statusMsg);
                        statusMsg = "";
                    }
                    System.out.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    if (userInput.equalsIgnoreCase("y")) {
                        mode = "SMTP_RESULT";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "SMTP_BODY";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                }else if (mode.equals("SMTP_RESULT")) {
                    boolean connFailed = false; //Used to check for [y] after failed connection
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: ... > Main > Subject > From > To > CC > BCC > Body > Confirm > Result");
                    System.out.println("Send: Result");
                    sysOut.println(SMTP.getHost() + " | " + SMTP.getPort());
                    sysOut.println(SMTP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (SMTP.connect()) { //connection success
                        if (SMTP.SMTPLogin()) { //login success
                            System.out.println("");
                            System.out.println(SMTP.sendMessage(newMsg));
                        } else { //login failed
                            SMTP.disconnect();
                            eraseFromConsole(waitMsg);
                            connFailed = true;
                            sysOut.println("Would you like to retry connection?");
                            sysOut.println("Cmds: [y], [back], [exit]");
                            sysOut.println("");
                            statusMsg = "Connection issue (login failure). Please try again.";
                        }
                    } else { //connection failed
                        SMTP.close();
                        eraseFromConsole(waitMsg);
                        connFailed = true;
                        sysOut.println("Would you like to retry connection?");
                        sysOut.println("Cmds: [y], [back], [exit]");
                        sysOut.println("");
                        statusMsg = "Connection issue (server not connected). Please try again.";
                    }
                    if (!statusMsg.equals("")) { //Print statusMsg
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    //Optional line below. Not needed if sending user to SMTP_MAIN to send another message
                    newMsg = new Message(); //This clears the message cache after its sent.
                    quitUser = true;
                    //All the usual checking stuff goes here
                    //sysOut.print("|> "); //Prompt
                    //String userInput = sysIn.readLine();
                }else if (mode.equals("POP_MAIN")) {
                    boolean connFailed = false; //Used to check for [y] after failed connection
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Main");
                    System.out.println("Read: Main Menu");
                    sysOut.println(POP.getHost() + " | " + POP.getPort());
                    sysOut.println(POP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (POP.connect()) { //connection success
                        if (POP.POPLogin()) { //login success
                            msgCount = POP.getMessageCount(); //Update msgCount
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            sysOut.println("You have " + msgCount + " messages.");
                            //Should print out latest message summary (Num, Date, From, Subject) here
                            sysOut.println("How many messages would you like listed? [list <num>]");
                            sysOut.println("Cmds: [list <num>], [back], [exit]");
                            sysOut.println("");
                        } else { //login failed
                            POP.disconnect();
                            eraseFromConsole(waitMsg);
                            connFailed = true;
                            sysOut.println("Would you like to retry connection?");
                            sysOut.println("Cmds: [y], [back], [exit]");
                            sysOut.println("");
                            statusMsg = "Connection issue (login failure). Please try again.";
                        }
                    } else { //connection failed
                        POP.close();
                        eraseFromConsole(waitMsg);
                        connFailed = true;
                        sysOut.println("Would you like to retry connection?");
                        sysOut.println("Cmds: [y], [back], [exit]");
                        sysOut.println("");
                        statusMsg = "Connection issue (server not connected). Please try again.";
                    }
                    if (!statusMsg.equals("")) { //Print statusMsg
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("y") && connFailed) { //Appears if retry connection, otherwise bad
                        mode = "POP_MAIN";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_LOGIN";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("list")) { //list
                        if (checkSpaces(1, userInput)) { //Correct format (list <numMsgs>)
                            int spaceInd = findSpace(userInput);
                            int numMsgs = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
                            if (numMsgs > msgCount) {
                                numMsgs = msgCount;
                            }
                            minMsg = msgCount - numMsgs + 1;
                            maxMsg = msgCount;
                            mode = "POP_INBOX";
                        } else { //List and number unseparated
                            statusMsg = "Please enter list, followed by space, followed by number of messages to list.";
                        }
                    } else {
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("POP_INBOX")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Menu > Inbox");
                    sysOut.println("Read: Inbox");
                    sysOut.println("Server: " + POP.getHost() + " | " + POP.getPort());
                    sysOut.println("User: " + POP.getUser());
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
                    if (!statusMsg.equals("")) { //print statusMsg
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //Check user input
                    if (userInput.equalsIgnoreCase("y")) {
                        mode = "POP_INBOX";
                    } else if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_MAIN";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("list")) { //list cmd
                        if (checkSpaces(2, userInput)) { //if two args
                            int spaceInd1 = findSpace(userInput);
                            int spaceInd2 = findSpace(userInput.substring(spaceInd1 + 1, userInput.length())) + spaceInd1 + 1;
                            //sysOut.println(spaceInd1);
                            //sysOut.println(spaceInd2);
                            minMsg = Integer.parseInt(userInput.substring(spaceInd1 + 1, spaceInd2)); //first arg
                            maxMsg = Integer.parseInt(userInput.substring(spaceInd2 + 1, userInput.length())); //2nd arg
                            //Sanitize minMsg and maxMsg, don't allow user to break with stupid things
                            if (minMsg > maxMsg) {
                                int store = maxMsg;
                                maxMsg = minMsg;
                                minMsg = store;
                            }
                            if (minMsg < 1) {
                                minMsg = 1;
                            }
                            if (minMsg > msgCount) {
                                minMsg = msgCount;
                            }
                            if (maxMsg < 1) {
                                maxMsg = 1;
                            }
                            if (maxMsg > msgCount) {
                                maxMsg = msgCount;
                            }
                            mode = "POP_INBOX";
                        } else if (checkSpaces(1, userInput)) { //if 1 arg (list numMsgs)
                            int spaceInd1 = findSpace(userInput);
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
                        } else {
                            statusMsg = "Please enter list command as: list <numMsgs> or list <minMsg> <maxMsg>.\n" +
                                    "Separate list and all numbers with spaces, and do not write brackets.";
                        }
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("view")) { //view cmd
                        if (checkSpaces(1, userInput)) {//Proper format
                            int spaceInd = findSpace(userInput);
                            viewMsgNum = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
                            //Check that viewMsgNum is within range
                            if (viewMsgNum < 1 || viewMsgNum > msgCount) { //If chosen msg is out of range
                                statusMsg = "Please choose a message that is between 1 and " + msgCount + ", inclusive.";
                            } else { //Number is in range
                                mode = "POP_VIEW";
                            }
                        } else { //improper format
                            statusMsg = "Please enter view, followed by space, followed by the number of the message to view.";
                        }
                    } else { //Unrecognized cmd
                        statusMsg = "Please enter a valid command!";
                    }
                } else if (mode.equals("POP_VIEW")) {
                    //--Print menu header
                    sysOut.println("GioMhail");
                    sysOut.println("Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Menu > Inbox > View");
                    System.out.println("Read: View Message");
                    sysOut.println("Server: " + POP.getHost() + " | " + POP.getPort());
                    sysOut.println("User: " + POP.getUser());
                    sysOut.println("");
                    String waitMsg = "Please Wait! Communicating with server...";
                    sysOut.print(waitMsg);
                    if (POP.connect()) { //connection success
                        if (POP.POPLogin()) { //login success
                            msgCount = POP.getMessageCount(); //update msgCount
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
                    if (!statusMsg.equals("")) { //print statusMsg
                        sysOut.println(statusMsg);
                        statusMsg = "";
                    }
                    sysOut.print("|> "); //Prompt
                    String userInput = sysIn.readLine();
                    //--Check user input
                    if (userInput.equalsIgnoreCase("back")) {
                        mode = "POP_INBOX";
                    } else if (userInput.equalsIgnoreCase("exit")) {
                        quitUser = true;
                    } else if (userInput.length() > 3 && userInput.substring(0, 4).equalsIgnoreCase("view")) {
                        if (checkSpaces(1, userInput)) {//Proper format
                            int spaceInd = findSpace(userInput);
                            viewMsgNum = Integer.parseInt(userInput.substring(spaceInd + 1, userInput.length()));
                            //Check that viewMsgNum is within range
                            if (viewMsgNum < 1 || viewMsgNum > msgCount) { //If chosen msg is out of range
                                statusMsg = "Please choose a message that is between 1 and " + msgCount + ", inclusive.";
                            } else { //Number is in range
                                mode = "POP_VIEW";
                            }
                        } else {
                            statusMsg = "Please enter view, followed by space, followed by the number of the message to view.";
                        }
                    } else { //unrecognized cmd
                        statusMsg = "Please enter a valid command!";
                    }
                } //Close pop_view
            } //Close while (!quitUser)
            System.out.println(">>Goodbye!<<");
        } catch (IOException e) { //Close try
            System.out.println("ERROR: Houston, we have a broken console.");
        }
    } //Close main

    //Helper methods

    //ParseFrom, returns String msg signifying/error or success in From address
    //Currently only returns "GOOD FROM", and "BAD FROM"
    //Later expand to support different error msgs
    public static String parseFrom(String fromHeader) {
        if (countChar(fromHeader, '<') != 1 || countChar(fromHeader, '>') != 1) {
            return "BAD FROM";
        }
        int bracket1Ind = fromHeader.indexOf('<');
        int bracket2Ind = fromHeader.indexOf('>');
        if (bracket2Ind <= bracket1Ind) {
            return "BAD FROM";
        }
        if (!(bracket1Ind + 4 <= bracket2Ind)) { //At least three chars between brackets <...>
            return "BAD FROM";
        }
        if (countChar(fromHeader.substring(bracket1Ind, bracket2Ind + 1), '@') != 1) { //Must be 1 @
            return "BAD FROM";
        }
        return "GOOD FROM";
    }

    //ParseTo, returns String msg signifying/error or success in To address list
    //Currently only returns "GOOD TO", and "BAD TO"
    //Later expand to support different error msgs
    public static String parseTo(String toHeader) {
        int LBrCount = countChar(toHeader, '<');
        int RBrCount = countChar(toHeader, '>');
        if ((LBrCount < 1) || (RBrCount < 1) || (LBrCount != RBrCount)) {
            return "BAD TO";
        }
        String addressLine = toHeader;
        for (int i = 0; i < LBrCount; i++) {
            int bracket1Ind = addressLine.indexOf('<');
            int bracket2Ind = addressLine.indexOf('>');
            if (!(bracket1Ind + 4 <= bracket2Ind)) { //At least three chars between brackets <...>
                return "BAD TO";
            }
            if (countChar(addressLine.substring(bracket1Ind, bracket2Ind + 1), '@') != 1) { //Must be a @
                return "BAD TO";
            }
            if (bracket2Ind != addressLine.length() - 1) { //If > isn't at the end
                addressLine = addressLine.substring(bracket2Ind + 1, addressLine.length()); //Get string after >
            }
        }
        return "GOOD TO";
    }

    //Counts the number of target chars in given input string
    //0 if none
    public static int countChar(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    //Prints summary lines from an ArrayList of HeaderStores
    public static void printMessageSummaries(ArrayList<HeaderStore> HeaderStoreList) {
        for (int i = HeaderStoreList.size() - 1; i >= 0; i--) {
            System.out.println(getMessageSummary(HeaderStoreList.get(i)));
        }
    }

    //Builds a summary line from a HeaderStore
    public static String getMessageSummary(HeaderStore msgHeader) {
        int msgNum = msgHeader.getMessageNum(); //this is alsways present
        String date = msgHeader.getDate(); //may be null
        if (date.equals("")) {
            date = "NO DATE";
        }
        String from = msgHeader.getFrom(); //may be null
        if (from.equals("")) {
            from = "NO FROM ADDRESS";
        }
        String subject = msgHeader.getSubject(); //may be null
        if (subject.equals("")) {
            subject = "NO SUBJECT";
        }
        return msgNum + " | " + date + " | " + from + " | " + subject;
    }

    //NOTE: Use to erase waitMsg ONLY after disconnected/closed
    //Erases a line of text from the console (had to be printed as S.o.print(...))
    public static void eraseFromConsole(String text) {
        String eraser = "";
        String clearer = "";
        for (int i = 0; i < text.length(); i++) {
            eraser += "\b"; //Add backspace character
            clearer += " ";
        }
        System.out.print(eraser);
        System.out.print(clearer);
        System.out.print(eraser);
    }

    //Returns location of space char in String
    //-1 if not found
    public static int findSpace(String input) {
        return input.indexOf(' ');
    }

    //Returns boolean, true if # of spaces found in input String matches # reqSpaces
    public static boolean checkSpaces(int reqSpaces, String input) {
        int numSpaces = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.substring(i, i + 1).equals(" ")) {
                numSpaces += 1;
            }
        }
        return numSpaces == reqSpaces;
    }
}

