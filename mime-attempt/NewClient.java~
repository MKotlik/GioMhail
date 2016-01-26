/* GioMhail by Coolgle
   - NewClient
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

/* This is a remake of the original Client!
   It is to be renamed after completion.

   Map of modes in Resources/newclient_mode_map.txt
   Remake plan in Resources/client_remake_plan.txt
 */

/* TODO
 - [DONE] Copy imports
 - [DONE] Define instance vars
 - [DONE] Default constructor
 - [DONE] Main function
 - [DONE] Create function for each SMTP mode
 - Create function for each POP mode
 - Create helper function to compile inbox string
 - Create helper function to compile msg view string
 - ... See client_remake_plan.txt in Resources for more info
 */

//Frame template
/*
public void modeNAME() {
        //Change frame vars & print
        menuMap = ;
        menuTitle = ;
        optField = ;
        menuInstructions = ;
        cmdList = ;
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
        clearScreen();
        System.out.println("Unknown console error detected (Unable to read input).\n" +
        "Program exiting.");
        quitUser = true;
        } else if (userInput.equalsIgnoreCase("y")) {
        mode = "MODE";
        } else if (userInput.equalsIgnoreCase("exit")) {
        quitUser = true;
        } else {
        statusMsg = "Please enter a valid command!";
        }
        }
        */

import java.io.*;
import java.util.*;

public class NewClient {
    //User IO vars
    private BufferedReader sysIn; //Read from console
    private PrintStream sysOut; //leave out for now to test try/catch necessity
    private String userInput;
    private boolean consoleError; //if IOException on console read

    //Loop/Mode vars
    private boolean quitUser; //Whether or not user has quit
    private String mode; //Screen mode/stage

    //Frame vars (used in printFrame())
    private String[] logoLines; //String lines for logo
    private String menuMap;
    private String menuTitle;
    private String optField; //Optional field for additional content/output
    private String menuInstructions;
    private String cmdList;
    private String statusMsg; //Any error or help messages
    private String waitMsg; //Displayed during server operations

    //POP vars
    private POPSession POP = null;
    private int msgCount; //updated number of msgs in inbox
    private int minMsg = 0; //Oldest message being listed in inbox
    private int maxMsg = 0; //Newest message being listed in inbox
    private int viewMsgNum = 0; //msgNum (ID) of message to view
    //Later, add vars like delMsgNum, viewMsgLclID, and others for more features

    //SMTP vars
    private SMTPSession SMTP = null;
    private ArrayList<String> msgBodyArray = new ArrayList<String>();
    private Message newMsg = null; //This shall be a new message to send
    //Later, add vars for attachments, for localMsgID, for sending from text file

    //Default constructor
    public NewClient() {
        //User IO
        sysIn = new BufferedReader(new InputStreamReader(System.in));
        sysOut = System.out;
        userInput = "";
        consoleError = false;
        //Looping
        quitUser = false;
        mode = "WELCOME";
        //Frame
        logoLines = new String[]{" __                     ",
                "/__ o  _ |V||_  _  o  | ",
                "\\_| | (_)| || |(_| |  | "};
        menuMap = "";
        menuTitle = "";
        optField = "";
        menuInstructions = "";
        cmdList = "";
        statusMsg = "";
        waitMsg = "Please wait! Communicating with server...";
    }

    //-----MAIN-----
    public static void main(String[] args) {
        NewClient mailApp = new NewClient(); //create a new NewClient object
        mailApp.runLoop(); //Runs program loop
    }

    //-----LOOP METHODS-----
    private void runLoop() {
        while (!quitUser) { //Until user quits
            clearScreen(); //Clear the screen
            if (mode.equals("WELCOME")) {
                modeWelcome();
            } else if (mode.equals("PROT_CHOOSE")) {
                modeProtChoose();
            } else if (mode.equals("SMTP_SETUP")) {
                modeSmtpSetup();
            } else if (mode.equals("SMTP_LOGIN")) {
                modeSmtpLogin();
            } else if (mode.equals("SMTP_MAIN")) {
                modeSmtpMain();
            } else if (mode.equals("SMTP_SUBJECT")) {
                modeSmtpSubject();
            } else if (mode.equals("SMTP_FROM")) {
                modeSmtpFrom();
            } else if (mode.equals("SMTP_TO")) {
                modeSmtpTo();
            } else if (mode.equals("SMTP_CC")) {
                modeSmtpCC();
            } else if (mode.equals("SMTP_BCC")) {
                modeSmtpBCC();
            } else if (mode.equals("SMTP_BODY")) {
                modeSmtpBody();
            } else if (mode.equals("SMTP_CONFIRM")) {
                modeSmtpConfirm();
            } else if (mode.equals("SMTP_RESULT")) {
                modeSmtpResult();
            } else if (mode.equals("POP_SETUP")) {
                modePopSetup();
            } else if (mode.equals("POP_LOGIN")) {
                modePopLogin();
            } else if (mode.equals("POP_MAIN")) {
                modePopMain();
            } else if (mode.equals("POP_INBOX")) {
                modePopMain();
            } else if (mode.equals("POP_VIEW")) {
                modePopView();
            } else {
                sysOut.println("What have you done?!?!");
                quitUser = true;
            }
        }
        System.out.println(">>GOODBYE<<");
    }

    //-----MODE METHODS-----
    private void modeWelcome() {
        //Change frame vars & print
        menuMap = "Welcome";
        menuTitle = "Welcome";
        optField = "Welcome to GioMhail, your go-to email client!";
        menuInstructions = "Would you like to continue [y] or exit [exit]?";
        cmdList = "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("y", "NONE", "NONE")) {
            mode = "PROT_CHOOSE";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modeProtChoose() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send";
        menuTitle = "Choose Read\\Send";
        optField = "";
        menuInstructions = "Would you like to read [read], send [send], or exit [exit]?";
        cmdList = "Cmds: [read], [send], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("read", "NONE", "NONE")) {
            mode = "POP_SETUP";
        } else if (checkInputMatch("send", "NONE", "NONE")) {
            mode = "SMTP_SETUP";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modeSmtpSetup() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Send: > Setup";
        menuTitle = "Send: Setup";
        optField = "";
        menuInstructions = "Please enter the address and port of your SMTP server.";
        cmdList = "Cmds: <address port>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "PROT_CHOOSE";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("NONE", "STRING", "INT")) {
            String SMTPHost = getStrElementUserInput(1);
            int SMTPPort = getIntElementUserInput(2);
            System.out.print(waitMsg); //Wait on server operations
            SMTP = new SMTPSession(SMTPHost, SMTPPort);
            if (SMTP.connect()) {
                SMTP.disconnect(); //Disconnect (QUIT) successful connection
                mode = "SMTP_LOGIN";
            } else {
                SMTP.close(); //Server resources might be open, so close()
                statusMsg = "Connection failed. Ensure correct server address and port, then try again.";
            }
        } else {
            statusMsg = "Please enter a text server address followed by a port number (w/ space, w/o brackets).";
        }
    }

    private void modeSmtpLogin() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Send: > Setup > Login";
        menuTitle = "Send: Login";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort();
        menuInstructions = "Please enter your username and password.";
        cmdList = "Cmds: <user pass>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_SETUP";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("NONE", "STRING", "STRING") || checkInputMatch("NONE", "STRING", "INT")) {
            String user = getStrElementUserInput(1);
            String pass = getStrElementUserInput(2);
            SMTP.setUser(user);
            SMTP.setPass(pass);
            System.out.print(waitMsg); //Wait on server operations
            if (SMTP.connect()) {
                if (SMTP.SMTPLogin()) {
                    mode = "SMTP_MAIN";
                } else {
                    statusMsg = "Login failed. Ensure correct username and password, then try again.";
                }
                SMTP.disconnect(); //Disconnect (QUIT) successful connection
            } else {
                SMTP.close(); //Server resources might be open, so close()
                statusMsg = "Connection failed. Please try again.";
            }
        } else {
            statusMsg = "Please enter username and password correctly (w/ space, w/o brackets).";
        }
    }

    private void modeSmtpMain() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Send: > Setup > Login > Main";
        menuTitle = "Send: Main Menu";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser();
        menuInstructions = "Would you like to send an email?";
        cmdList = "Cmds: [y], [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("y", "NONE", "NONE")) {
            mode = "SMTP_SUBJECT";
            newMsg = new Message();
            msgBodyArray = new ArrayList<String>();
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_LOGIN";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modeSmtpSubject() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Send: > Setup > Login > Main > Subject";
        menuTitle = "Send: Subject";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "Please enter a subject";
        cmdList = "Cmds: <subject>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_MAIN";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else { //Assume that its subject
            newMsg.getHeaderStore().setHeaderForce("Subject", userInput);
            mode = "SMTP_FROM";
        }
    }

    private void modeSmtpFrom() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Choose Read\\Send > Send: > Setup > Login > Main > Subject > From";
        menuTitle = "Send: From";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "Please enter your From name & address in the format: " +
                "First Last <user@server.com>, with brackets.\n" +
                "Note, the address should correspond to your account on this server.";
        cmdList = "Cmds: <First Last <from address>>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_SUBJECT";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else { //Assume that its from address
            if (parseFrom(userInput).equals("GOOD FROM")) {
                newMsg.getHeaderStore().setHeaderForce("From", userInput);
                mode = "SMTP_TO";
            } else {
                statusMsg = "Please enter 1 name & 1 email address with brackets in the format: " +
                        "First Last <user@server.com>.";
            }
        }
    }

    private void modeSmtpTo() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Send: > Setup > Login > Main > Subject > From > To";
        menuTitle = "Send: To";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "Please enter a list of 'to' names & addresses.\n" +
                "Follow the format: First Last <user@server.com>, " +
                "First Last <user@server.com>, ... Use brackets.";
        cmdList = "Cmds: <list of (First Last <to address>)>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_FROM";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else { //Assume that its the to addresses
            if (parseTo(userInput).equals("GOOD TO")) {
                newMsg.getHeaderStore().setHeaderForce("To", userInput);
                mode = "SMTP_CC";
            } else {
                statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                        "Each set should be in the format: First Last <user@server.com>";
            }
        }
    }

    private void modeSmtpCC() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Setup > Login > Main > Subject > From > To > CC";
        menuTitle = "Send: CC";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "You may choose to enter a list of CC names & addresses, or enter [n] to skip.\n" +
                "Follow the format: First Last <user@server.com>, " +
                "First Last <user@server.com>, ... Use brackets.";
        cmdList = "Cmds: <list of (First Last <CC address>)>, [n], [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("n", "NONE", "NONE")) {
            mode = "SMTP_BCC";
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_TO";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else { //Assume that its the CC addresses
            if (parseTo(userInput).equals("GOOD TO")) {
                newMsg.getHeaderStore().setHeaderForce("CC", userInput);
                mode = "SMTP_BCC";
            } else {
                statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                        "Each set should be in the format: First Last <user@server.com>";
            }
        }
    }

    private void modeSmtpBCC() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Login > Main > Subject > From > To > CC > BCC";
        menuTitle = "Send: BCC";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "You may choose to enter a list of BCC names & addresses, or enter [n] to skip.\n" +
                "Follow the format: First Last <user@server.com>, " +
                "First Last <user@server.com>, ... Use brackets.";
        cmdList = "Cmds: <list of (First Last <BCC address>)>, [n], [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("n", "NONE", "NONE")) {
            mode = "SMTP_BODY";
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_CC";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else { //Assume that its the BCC addresses
            if (parseTo(userInput).equals("GOOD TO")) {
                newMsg.getHeaderStore().setHeaderForce("BCC", userInput);
                mode = "SMTP_BODY";
            } else {
                statusMsg = "Please enter a comma-separated list of names & email address with brackets.\n" +
                        "Each set should be in the format: First Last <user@server.com>";
            }
        }
    }

    private void modeSmtpBody() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Main > Subject > From > To > CC > BCC > Body";
        menuTitle = "Send: Message Body";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "Enter the body of your message line by line.\n" +
                "Use [$del] to jump back and delete the previous line.\n";
        cmdList = "Cmds: <line text>, [$del], [$back], [$exit], [$send]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("$del", "NONE", "NONE")) {
            if (msgBodyArray.size() > 0) {
                //eraseFromConsole(msgBodyArray.get(msgBodyArray.size() - 1)); //Unnecessary
                msgBodyArray.remove(msgBodyArray.size() - 1);
            } else {
                statusMsg = "Cannot delete a line. No lines remaining.";
            }
        } else if (checkInputMatch("$back", "NONE", "NONE")) {
            mode = "SMTP_BCC";
        } else if (checkInputMatch("$exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("$send", "NONE", "NONE")) {
            String msgBodyStr = "";
            for (int i = 0; i < msgBodyArray.size(); i++) {
                msgBodyStr += msgBodyArray.get(i) + "\r\n";
            }
            newMsg.setMessageBody(msgBodyStr);
            mode = "SMTP_CONFIRM";
        } else { //Assume that its a line of text
            msgBodyArray.add(userInput);
        }
    }

    private void modeSmtpConfirm() {
        //Change frame vars & print
        menuMap = "Menu Map: ... > Subject > From > To > CC > BCC > Body > Confirm";
        menuTitle = "Send: Confirm";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser() + "\n" + getDraftDisplay();
        menuInstructions = "Are you ready to send this email?";
        cmdList = "Cmds: [y], [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("y", "NONE", "NONE")) {
            mode = "SMTP_RESULT";
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_BODY";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modeSmtpResult() {
        //Result vars
        boolean connFailed = false; //Used to check for [y] after failed connection
        boolean unknownStatus = false; //Use in case we add a sendMessage status and forget to update Client
        String sendResult = ""; //Store result of sending msg
        //Frame vars
        menuMap = "Menu Map: ... > From > To > CC > BCC > Body > Confirm > Result";
        menuTitle = "Send: Result";
        optField = "Server: " + SMTP.getHost() + " | " + SMTP.getPort() + "\n" +
                "User: " + SMTP.getUser();
        cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
        printFrameHeader(); //Print just top of menu
        //Server interaction begins
        System.out.print(waitMsg);
        if (SMTP.connect()) { //connection success
            if (SMTP.SMTPLogin()) { //login success
                sendResult = SMTP.sendMessage(newMsg);
                SMTP.disconnect();
                //eraseFromConsole(waitMsg);
                //Display different prompts based on result
                if (sendResult.equals("SUCCESS")) {
                    menuInstructions = "Message sent!\n" +
                            "Would you like to send another email?";
                } else if (sendResult.equals("NO FROM") || sendResult.equals("BAD FROM")) {
                    menuInstructions = "Message not sent due to bad 'From' formatting.\n" +
                            "Would you like to edit your 'From' address?";
                } else if (sendResult.equals("NO TO") || sendResult.equals("BAD TO")) {
                    menuInstructions = "Message not sent due to bad formatting of 'To' addresses.\n" +
                            "Would you like to edit your 'To' addresses?";
                } else if (sendResult.equals("MISMATCHED FROM")) {
                    menuInstructions = "Message not sent.\n" +
                            "Your 'From' address doesn't match your account on this server.\n" +
                            "Would you like to edit your 'From' address?";
                } else if (sendResult.equals("BAD DATA") || sendResult.equals("DATA REFUSED")) {
                    menuInstructions = "Message not sent due to server error. (" + sendResult + ")\n" +
                            "Would you like to retry sending this message?";
                } else { //sendResult doesn't match a known code
                    unknownStatus = true;
                    menuInstructions = "Unknown status message: " + sendResult + "\n" +
                            "Would you like to retry sending this message?";
                }
            } else { //login failed
                SMTP.disconnect();
                eraseFromConsole(waitMsg);
                connFailed = true;
                menuInstructions = "Connection issue (login failure).\n" +
                        "Would you like to retry connection?";
            }
        } else { //connection failed
            SMTP.close();
            eraseFromConsole(waitMsg);
            connFailed = true;
            menuInstructions = "Connection issue (server not connected).\n" +
                    "Would you like to retry connection?";
        }
        //Server enteraction ended
        clearScreen(); //Clear screen from before results got processed (header + waitMsg)
        printFrame(); //Display result
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("y", "NONE", "NONE")) {
            if (connFailed || unknownStatus) { //Connection failed or unknown status from sendMessage
                mode = "SMTP_RESULT"; //Retry sending
            } else if (sendResult.equals("NO FROM") || sendResult.equals("BAD FROM") ||
                    sendResult.equals("MISMATCHED FROM")) {
                mode = "SMTP_FROM";
            } else if (sendResult.equals("NO TO") || sendResult.equals("BAD TO")) {
                mode = "SMTP_TO";
            } else if (sendResult.equals("BAD DATA") || sendResult.equals("DATA REFUSED")) { //server error
                mode = "SMTP_RESULT"; //Retry sending
            } else if (sendResult.equals("SUCCESS")) { //user wants to send another msg
                mode = "SMTP_MAIN"; //Alternatively use SMTP_MAIN
            } else {
                mode = "SMTP_RESULT";
                statusMsg = "Uncaught status msg from sendMessage. This shouldn't be happening...";
            }
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "SMTP_CONFIRM";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modePopSetup() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Read: > Setup";
        menuTitle = "Read: Setup";
        optField = "";
        menuInstructions = "Please enter the address and port of your POP server.";
        cmdList = "Cmds: <address port>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "PROT_CHOOSE";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("NONE", "STRING", "INT")) {
            String POPHost = getStrElementUserInput(1);
            int POPPort = getIntElementUserInput(2);
            System.out.print(waitMsg); //Wait on server operations
            POP = new POPSession(POPHost, POPPort);
            if (POP.connect()) {
                POP.disconnect(); //Disconnect (QUIT) successful connection
                mode = "POP_LOGIN";
            } else {
                POP.close(); //Server resources might be open, so close()
                statusMsg = "Connection failed. Ensure correct server address and port, then try again.";
            }
        } else {
            statusMsg = "Please enter a text server address followed by a port number (w/ space, w/o brackets).";
        }
    }

    private void modePopLogin() {
        //Change frame vars & print
        menuMap = "Menu Map: Welcome > Choose Read\\Send > Read: > Setup > Login";
        menuTitle = "Read: Login";
        optField = "Server: " + POP.getHost() + " | " + POP.getPort();
        menuInstructions = "Please enter your username and password.";
        cmdList = "Cmds: <user pass>, [back], [exit]";
        printFrame();
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "POP_SETUP";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("NONE", "STRING", "STRING") || checkInputMatch("NONE", "STRING", "INT")) {
            String user = getStrElementUserInput(1);
            String pass = getStrElementUserInput(2);
            POP.setUser(user);
            POP.setPass(pass);
            System.out.print(waitMsg); //Wait on server operations
            if (POP.connect()) {
                if (POP.POPLogin()) {
                    mode = "POP_MAIN";
                } else {
                    statusMsg = "Login failed. Ensure correct username and password, then try again.";
                }
                POP.disconnect(); //Disconnect (QUIT) successful connection
            } else {
                POP.close(); //Server resources might be open, so close()
                statusMsg = "Connection failed. Please try again.";
            }
        } else {
            statusMsg = "Please enter username and password correctly (w/ space, w/o brackets).";
        }
    }

    private void modePopMain() {
        //Result vars
        boolean connFailed = false; //Used to check for [y] after failed connection
        //Frame vars
        menuMap = "Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Main";
        menuTitle = "Read: Main";
        optField = "Server: " + POP.getHost() + " | " + POP.getPort() + "\n" +
                "User: " + POP.getUser();
        printFrameHeader(); //Print just top of menu
        //Server interaction begins
        System.out.print(waitMsg);
        if (POP.connect()) { //connection success
            if (POP.POPLogin()) { //login success
                msgCount = POP.getMessageCount(); //Update msgCount
                //get HeaderStore of latest message here
                POP.disconnect();
                menuInstructions = "How many messages would you to list in Inbox? [list <num>]";
                cmdList = "Cmds: [list <num>], [back], [exit]";
            } else { //login failed
                POP.disconnect();
                eraseFromConsole(waitMsg);
                connFailed = true;
                optField = "You have " + msgCount + " messages.";
                //Add summaryLine of newest message here.
                menuInstructions = "Connection issue (login failure).\n" +
                        "Would you like to retry connection?";
                cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
            }
        } else { //connection failed
            POP.close();
            eraseFromConsole(waitMsg);
            connFailed = true;
            menuInstructions = "Connection issue (server not connected).\n" +
                    "Would you like to retry connection?";
            cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
        }
        //Server enteraction ended
        clearScreen(); //Clear screen from before results got processed (header + waitMsg)
        printFrame(); //Display result
        getUserInput();
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (checkInputMatch("y", "NONE", "NONE") && connFailed) {
            mode = "POP_MAIN"; //retry access
        } else if (checkInputMatch("back", "NONE", "NONE")) {
            mode = "POP_LOGIN";
        } else if (checkInputMatch("exit", "NONE", "NONE")) {
            quitUser = true;
        } else if (checkInputMatch("LIST", "INT", "NONE")) {
            int numMsgs = getIntElementUserInput(2);
            if (numMsgs > msgCount) {
                numMsgs = msgCount;
            }
            minMsg = msgCount - numMsgs + 1;
            maxMsg = msgCount;
            mode = "POP_INBOX";
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    private void modePopInbox() {
        //Result vars
        boolean connFailed = false; //Used to check for [y] after failed connection
        //Frame vars
        menuMap = "Menu Map: Welcome > Choose Read\\View > Read: > Setup > Login > Main > Inbox";
        menuTitle = "Read: Inbox";
        optField = "Server: " + POP.getHost() + " | " + POP.getPort() + "\n" +
                "User: " + POP.getUser();
        printFrameHeader(); //Print just top of menu
        //Server interaction begins
        System.out.print(waitMsg);
        if (POP.connect()) { //connection success
            if (POP.POPLogin()) { //login success
                msgCount = POP.getMessageCount(); //Update msgCount
                ArrayList<HeaderStore> HeaderStoreList = POP.getHeaderStoreList(minMsg, maxMsg);
                POP.disconnect();
                outField = "=====================================================================\n";
                outField += "Msg # |      Date & Time Sent       |    From     |      Subject\n";
                outField = getMessageSummaries();


                menuInstructions = "How many messages would you to list in Inbox? [list <num>]";
                cmdList = "Cmds: [list <num>], [back], [exit]";
            } else { //login failed
                POP.disconnect();
                eraseFromConsole(waitMsg);
                connFailed = true;
                optField = "You have " + msgCount + " messages.";
                //Add summaryLine of newest message here.
                menuInstructions = "Connection issue (login failure).\n" +
                        "Would you like to retry connection?";
                cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
            }
        } else { //connection failed
            POP.close();
            eraseFromConsole(waitMsg);
            connFailed = true;
            menuInstructions = "Connection issue (server not connected).\n" +
                    "Would you like to retry connection?";
            cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
        }
        //Server enteraction ended
        clearScreen(); //Clear screen from before results got processed (header + waitMsg)
        printFrame(); //Display result
        getUserInput();
    }

    private void modePopView() {
        //
    }

    //-----TUI METHODS-----
    private void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    //Prints the entire frame to the screen
    //Several sections: Logo, menu map, menu title, optional field, instructions, cmd lines, prompt
    private void printFrame() {
        //Print logo
        for (int i = 0; i < logoLines.length; i++) {
            System.out.println(logoLines[i]);
        }
        System.out.println(""); //blank line
        System.out.println("Menu Map: " + menuMap);
        System.out.println(menuTitle);
        System.out.println("----------------------------------------------------------------------"); //70
        System.out.println(""); //blank line
        if (! optField.equals("")) {
            System.out.println(optField);
            //System.out.println("----------------------------------------------------------------------"); //70
        }
        System.out.println(""); //Blank line
        System.out.println(menuInstructions);
        System.out.println(cmdList);
        System.out.println(""); //Blank line
        if (! statusMsg.equals("")) {
            System.out.println(statusMsg);
            statusMsg = "";
        }
        System.out.print("|> "); //Prompt
    }

    //Prints the top of a menu to a screen
    //Several sections: Logo, menu map, menu title, optional field
    private void printFrameHeader() {
        //Print logo
        for (int i = 0; i < logoLines.length; i++) {
            System.out.println(logoLines[i]);
        }
        System.out.println(""); //blank line
        System.out.println("Menu Map: " + menuMap);
        System.out.println(menuTitle);
        System.out.println("----------------------------------------------------------------------"); //70
        if (! optField.equals("")) {
            System.out.println(optField);
            //System.out.println("----------------------------------------------------------------------"); //70
        }
    }

    //Compose string for displaying message builder with printFrame
    private String getDraftDisplay() {
        String draftDisplay = "";
        draftDisplay += "=====================================================================\n";
        draftDisplay += " \n";
        draftDisplay += "------------------------------New Email------------------------------\n";
        draftDisplay += "Subject: " + newMsg.getHeaderStore().getSubject() + "\n";
        draftDisplay += "From: " + newMsg.getHeaderStore().getFrom() + "\n";
        draftDisplay += "To: " + newMsg.getHeaderStore().getTo() + "\n";
        draftDisplay += "CC: " + newMsg.getHeaderStore().getCC() + "\n";
        draftDisplay += "BCC: " + newMsg.getHeaderStore().getBCC() + "\n";
        draftDisplay += " \n";
        draftDisplay += "Body:\n";
        draftDisplay += "---------------------------------------------------------------------\n";
        for (int i = 0; i < msgBodyArray.size(); i++) {
            draftDisplay += msgBodyArray.get(i) + "\n";
        }
        draftDisplay += " \n";
        draftDisplay += "=====================================================================\n";
        return draftDisplay;
    }

    //NOTE: Use to erase waitMsg ONLY after disconnected/closed
    //Erases a line of text from the console (had to be printed as S.o.print(...))
    private void eraseFromConsole(String text) {
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

    //-----USER INPUT METHODS-----

    //Prompt and read user input
    private void getUserInput() {
        try {
            userInput = sysIn.readLine();
        } catch (IOException e) {
            consoleError = true;
        }
    }

    private int getIntElementUserInput(int elementNum) {
        Scanner intScan = new Scanner(userInput.trim());
        for (int i = 0; i < elementNum - 1; i++) { //advance scanner until reach elementNum
            intScan.next();
        }
        if (intScan.hasNextInt(10)) {
            return intScan.nextInt();
        } else {
            return -1;
        }
    }

    private String getStrElementUserInput(int elementNum) {
        Scanner intScan = new Scanner(userInput.trim());
        for (int i = 0; i < elementNum - 1; i++) { //advance scanner until reach elementNum
            intScan.next();
        }
        if (intScan.hasNext()) {
            return intScan.next(); //get a string
        } else {
            return "NONE";
        }
    }

    //Checks if userInput matches expected Cmd and/or argTypes
    //Takes in <cmdName> <type of arg1> <type of arg2>
    //cmd can be the name of a command or NONE
    //argTypes can be INT, STRING, or NONE
    private boolean checkInputMatch(String cmd, String argType1, String argType2) {
        if (argType1.equals("NONE")) { //Must be cmd
            return cmd.equalsIgnoreCase(userInput.trim());
        }
        int reqSpaces = 0;
        if ((! cmd.equals("NONE")) && (! argType2.equals("NONE"))) {
            reqSpaces = 2; //cmd arg arg
        } else if (cmd.equals("NONE") || argType2.equals("NONE")) {
            reqSpaces = 1; //cmd arg or arg arg
        } else if (cmd.equals("NONE") && argType2.equals("NONE")) {
            reqSpaces = 0; //arg
        } else {
            return false; //NONE NONE NONE
        }
        if (reqSpaces != countChar(userInput, ' ')) {
            return false; //Number of elements doesn't meet expectations
        }
        Scanner intScan = new Scanner(userInput.trim());
        if (reqSpaces == 0) { //arg1
            if (intScan.hasNextInt(10)) { //first element is int
                intScan.close();
                return argType1.equals("INT");
            } else {
                intScan.close();
                return argType1.equals("STRING");
            }
        } else if (reqSpaces == 1 && (! argType2.equals("NONE"))) { //arg1 arg2
            boolean match1 = false;
            if (intScan.hasNextInt(10)) { //first element is int
                match1 = argType1.equals("INT");
            } else {
                match1 = argType1.equals("STRING");
            }
            intScan.next(); //move to 2nd element
            if (intScan.hasNextInt(10)) { //second element is int
                return match1 && argType2.equals("INT");
            } else {
                return match1 && argType2.equals("STRING");
            }
        } else if (reqSpaces == 1 && (argType2.equals("NONE"))) { //cmd arg1
            intScan.next(); //skip cmd
            if (intScan.hasNextInt(10)) { //second element is int
                return userInput.toUpperCase().startsWith(cmd) && argType1.equals("INT");
            } else {
                return userInput.toUpperCase().startsWith(cmd) && argType1.equals("STRING");
            }
        } else if (reqSpaces == 2) { //cmd arg1 arg2
            boolean match1 = false;
            intScan.next(); //skip cmd (1st element)
            if (intScan.hasNextInt(10)) { //second element is int
                match1 = argType1.equals("INT");
            } else {
                match1 = argType1.equals("STRING");
            }
            intScan.next(); //move to 3rd element
            if (intScan.hasNextInt(10)) { //third element is int
                return userInput.toUpperCase().startsWith(cmd) && match1 && argType2.equals("INT");
            } else {
                return userInput.toUpperCase().startsWith(cmd) && match1 && argType2.equals("STRING");
            }
        }
        return false; //something strange has happened
    }

    //ParseFrom, returns String msg signifying/error or success in From address
    //Currently only returns "GOOD FROM", and "BAD FROM"
    //Later expand to support different error msgs
    private String parseFrom(String fromHeader) {
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
    private String parseTo(String toHeader) {
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
    private int countChar(String text, char target) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == target) {
                count++;
            }
        }
        return count;
    }

    //-----MESSAGE METHODS-----

    //Prints summary lines from an ArrayList of HeaderStores
    private String getMessageSummaries(ArrayList<HeaderStore> HeaderStoreList) {
        String retStr = "";
        for (int i = HeaderStoreList.size() - 1; i >= 0; i--) {
            retStr += getMessageSummary(HeaderStoreList.get(i)) + "\n";
        }
        return retStr;
    }

    //Builds a summary line from a HeaderStore
    private String getMessageSummary(HeaderStore msgHeader) {
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
}