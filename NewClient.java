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
    private String initError; //Whether or not error on Client instantiation
    private boolean quitUser; //Whether or not user has quit
    private String mode; //Screen mode/stage

    //Frame vars (used in printFrame())
    private String menuMap;
    private String menuTitle;
    private String optField; //Optional field for additional content/output
    private String menuInstructions;
    private String cmdList;
    private String statusMsg; //Any error or help messages
    private String waitMsg; //Displayed during server operations
    //Logos
    private String bigLogoAdress = "data/biglogo.txt";
    private String smallLogoAdress = "data/smalllogo.txt";
    private ArrayList<String> smallLogoLines;
    private ArrayList<String> bigLogoLines;

    //POP vars
    private POPSession POP;
    private int msgCount; //updated number of msgs in inbox
    private int minMsg = 0; //Oldest message being listed in inbox
    private int maxMsg = 0; //Newest message being listed in inbox
    private int viewMsgNum = 0; //msgNum (ID) of message to view
    //Later, add vars like delMsgNum, viewMsgLclID, and others for more features

    //SMTP vars
    private SMTPSession SMTP;
    private ArrayList<String> msgBodyArray;
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
        initError = "";
        quitUser = false;
        mode = "WELCOME";
        //Frame
        menuMap = "";
        menuTitle = "";
        optField = "";
        menuInstructions = "";
        cmdList = "";
        statusMsg = "";
        waitMsg = "Please wait! Communicating with server...";
        //Logos
        String bigLogoResult = setBigLogo();
        String smallLogoResult = setSmallLogo();
        if (!bigLogoResult.equals("SUCCESS")) {
            initError = "Could not start client. ERROR: " + bigLogoResult;
            quitUser = true;
        }
        if (!smallLogoResult.equals("SUCCESS")) {
            initError = "Could not start client. ERROR: " + smallLogoResult;
            quitUser = true;
        }
        //POP vars
        POP = new POPSession();
        //SMTP vars
        SMTP = new SMTPSession();
        msgBodyArray = new ArrayList<String>();
    }

    //-----MAIN-----
    public static void main(String[] args) {
        NewClient mailApp = new NewClient(); //create a new NewClient object
        mailApp.runLoop(); //Runs program loop
    }

    //-----LOOP METHODS-----
    private void runLoop() {
        //Display initError if any
        if (!initError.isEmpty()) {
            System.out.println(initError);
        }
        //Run loop if no errors
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
                modePopInbox();
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
    /*
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
    */

    private void modeWelcome() {
        //Change frame vars & print
        //No need for menumap and menutitle in welcome menu
        optField = "Welcome to GioMhail, your go-to email client!";
        menuInstructions = "Would you like to continue [y] or exit [exit]?";
        cmdList = "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)";
        printWelcomeMenu();
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
            SMTP.setHost(SMTPHost);
            SMTP.setPort(SMTPPort);
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
        optField = "";
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
        optField = "";
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
        optField = getDraftDisplay();
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
        optField = getDraftDisplay();
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
            if (ParseUtils.checkFrom(userInput).equals("GOOD FROM")) {
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
        optField = getDraftDisplay();
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
            if (ParseUtils.checkTo(userInput).equals("GOOD TO")) {
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
        optField = getDraftDisplay();
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
            if (ParseUtils.checkTo(userInput).equals("GOOD TO")) {
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
        optField = getDraftDisplay();
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
            if (ParseUtils.checkTo(userInput).equals("GOOD TO")) {
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
        optField = getDraftDisplay();
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
        optField = getDraftDisplay();
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
        optField = "";
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
            POP.setHost(POPHost);
            POP.setPort(POPPort);
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
        optField = "";
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
        optField = "";
        printFrameHeader(); //Print just top of menu
        //Server interaction begins
        System.out.print(waitMsg);
        if (POP.connect()) { //connection success
            if (POP.POPLogin()) { //login success
                msgCount = POP.getMessageCount(); //Update msgCount
                //get HeaderStore of latest message here
                POP.disconnect();
                optField = "You have " + msgCount + " messages.";
                //Add summaryLine of newest message here.
                menuInstructions = "How many messages would you to list in Inbox? [list <num>]";
                cmdList = "Cmds: [list <num>], [back], [exit]";
            } else { //login failed
                POP.disconnect();
                eraseFromConsole(waitMsg);
                connFailed = true;
                optField = "";
                menuInstructions = "Connection issue (login failure).\n" +
                        "Would you like to retry connection?";
                cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
            }
        } else { //connection failed
            POP.close();
            eraseFromConsole(waitMsg);
            connFailed = true;
            optField = "";
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
        menuMap = "Menu Map: ... > Choose Read\\View > Read: > Setup > Login > Main > Inbox";
        menuTitle = "Read: Inbox";
        optField = "";
        printFrameHeader(); //Print just top of menu
        //Server interaction begins
        System.out.print(waitMsg);
        if (POP.connect()) { //connection success
            if (POP.POPLogin()) { //login success
                msgCount = POP.getMessageCount(); //Update msgCount
                ArrayList<HeaderStore> HeaderStoreList = POP.getHeaderStoreList(minMsg, maxMsg);
                POP.disconnect();
                optField = getMessageSummaryList(HeaderStoreList);
                menuInstructions = "List different emails, choose an email to view or delete, or download email(s).";
                cmdList = "Cmds: [view <msgNum>], [del <msgNum>], [save <msgNum>], [save], [list <numMsgs>],\n" +
                        "[list <minMsg> <maxMsg>], [back], [exit]";
                quitUser = true;
            } else { //login failed
                POP.disconnect();
                eraseFromConsole(waitMsg);
                connFailed = true;
                optField = "";
                //Add summaryLine of newest message here.
                menuInstructions = "Connection issue (login failure).\n" +
                        "Would you like to retry connection?";
                cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
            }
        } else { //connection failed
            POP.close();
            eraseFromConsole(waitMsg);
            connFailed = true;
            optField = "";
            menuInstructions = "Connection issue (server not connected).\n" +
                    "Would you like to retry connection?";
            cmdList = "Cmds: [y], [back], [exit]"; //cmdList stays same
        }
        //Server enteraction ended
        clearScreen(); //Clear screen from before results got processed (header + waitMsg)
        printFrame(); //Display result
        getUserInput();
        quitUser = true;
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
        //Print top bar
        System.out.println("========================================================================================" +
                "================================");
        System.out.println(""); //cushion
        //Print logo
        for (int i = 0; i < smallLogoLines.size(); i++) {
            if (i == smallLogoLines.size() - 2) { //print menuTitle
                String line = smallLogoLines.get(i) +
                        getSpacing(menuTitle, "CENTER", smallLogoLines.get(i).length(), 120) + menuTitle;
                System.out.println(line);
            } else if (i == smallLogoLines.size() - 1) {
                String line = smallLogoLines.get(i) +
                        getSpacing(menuMap, "CENTER", smallLogoLines.get(i).length(), 120) + menuMap;
                System.out.println(line);
            } else {
                System.out.println(smallLogoLines.get(i));
            }
        }
        System.out.println("----------------------------------------------------------------------------------------" +
                "--------------------------------"); //Header separator
        //Print server & user info if in POP or SMTP
        if (mode.toUpperCase().startsWith("POP")) {
            if ((!POP.getUser().equals("")) && (!mode.toUpperCase().equals("POP_LOGIN"))) { //right user has been set
                String serverInfo = POP.getHost() + " | " + POP.getPort();
                String line = POP.getUser() + getSpacing(serverInfo, "RIGHT", POP.getUser().length(), 120) + serverInfo;
                System.out.println(line);
            } else if ((!POP.getHost().equals("")) && (!mode.toUpperCase().equals("POP_SETUP"))) { //host & ! user
                String serverInfo = POP.getHost() + " | " + POP.getPort();
                String line = getSpacing(serverInfo, "RIGHT", 0, 120) + serverInfo;
                System.out.println(line);
            }
        } else if (mode.toUpperCase().startsWith("SMTP")) {
            if ((!SMTP.getUser().equals("")) && (!mode.toUpperCase().equals("SMTP_LOGIN"))) { //right user has been set
                String serverInfo = SMTP.getHost() + " | " + SMTP.getPort();
                String line = SMTP.getUser() + getSpacing(serverInfo, "RIGHT", SMTP.getUser().length(), 120) + serverInfo;
                System.out.println(line);
            } else if ((!SMTP.getHost().equals("")) && (!mode.toUpperCase().equals("SMTP_SETUP"))) { //host & ! user
                String serverInfo = SMTP.getHost() + " | " + SMTP.getPort();
                String line = getSpacing(serverInfo, "RIGHT", 0, 120) + serverInfo;
                System.out.println(line);
            }
        }
        System.out.println(""); //cushion
        if (!optField.equals("")) {
            System.out.println(optField);
            System.out.println(""); //cushion
            System.out.println("____________________________________________________________________________________" +
                    "____________________________________");
        }
        System.out.println(menuInstructions);
        System.out.println(cmdList);
        System.out.println(""); //Blank line
        if (!statusMsg.equals("")) {
            System.out.println(statusMsg);
            statusMsg = "";
        }
        System.out.print("|> "); //Prompt
    }

    //Prints the top of a menu to a screen
    //Several sections: Logo, menu map, menu title, optional field
    private void printFrameHeader() {
        //Print top bar
        System.out.println("========================================================================================" +
                "================================");
        System.out.println(""); //cushion
        //Print logo
        for (int i = 0; i < smallLogoLines.size(); i++) {
            if (i == smallLogoLines.size() - 2) { //print menuTitle
                String line = smallLogoLines.get(i) +
                        getSpacing(menuTitle, "CENTER", smallLogoLines.get(i).length(), 120) + menuTitle;
                System.out.println(line);
            } else if (i == smallLogoLines.size() - 1) {
                String line = smallLogoLines.get(i) +
                        getSpacing(menuMap, "CENTER", smallLogoLines.get(i).length(), 120) + menuMap;
                System.out.println(line);
            } else {
                System.out.println(smallLogoLines.get(i));
            }
        }
        System.out.println("----------------------------------------------------------------------------------------" +
                "--------------------------------"); //Header separator
        //Print server & user info if in POP or SMTP
        if (mode.toUpperCase().startsWith("POP")) {
            if ((!POP.getUser().equals("")) && (!mode.toUpperCase().equals("POP_LOGIN"))) { //right user has been set
                String serverInfo = POP.getHost() + " | " + POP.getPort();
                String line = POP.getUser() + getSpacing(serverInfo, "RIGHT", POP.getUser().length(), 120) + serverInfo;
                System.out.println(line);
            } else if ((!POP.getHost().equals("")) && (!mode.toUpperCase().equals("POP_SETUP"))) { //host & ! user
                String serverInfo = POP.getHost() + " | " + POP.getPort();
                String line = getSpacing(serverInfo, "RIGHT", 0, 120) + serverInfo;
                System.out.println(line);
            }
        } else if (mode.toUpperCase().startsWith("SMTP")) {
            if ((!SMTP.getUser().equals("")) && (!mode.toUpperCase().equals("SMTP_LOGIN"))) { //right user has been set
                String serverInfo = SMTP.getHost() + " | " + SMTP.getPort();
                String line = SMTP.getUser() + getSpacing(serverInfo, "RIGHT", SMTP.getUser().length(), 120) + serverInfo;
                System.out.println(line);
            } else if ((!SMTP.getHost().equals("")) && (!mode.toUpperCase().equals("SMTP_SETUP"))) { //host & ! user
                String serverInfo = SMTP.getHost() + " | " + SMTP.getPort();
                String line = getSpacing(serverInfo, "RIGHT", 0, 120) + serverInfo;
                System.out.println(line);
            }
        }
        System.out.println(""); //cushion
        if (!optField.equals("")) {
            System.out.println(optField);
            System.out.println(""); //cushion
            //System.out.println("____________________________________________________________________________________" +
            //        "____________________________________");
        }
    }

    private void printWelcomeMenu() {
        System.out.println("========================================================================================" +
                "================================");
        System.out.println(""); //cushion
        //Print big logo
        for (int i = 0; i < bigLogoLines.size(); i++) {
            String line = getSpacing(bigLogoLines.get(i), "CENTER", 0, 120) + bigLogoLines.get(i);
            System.out.println(line);
        }
        System.out.println(""); //cushion
        //Print centered welcome message
        System.out.println(getSpacing(optField, "CENTER", 0, 120) + optField);
        System.out.println(""); //cushion
        //Print instructions & cmds separator
        System.out.println("________________________________________________________________________________________" +
                "________________________________");
        System.out.println(menuInstructions);
        System.out.println(cmdList);
        System.out.println(""); //Blank line
        if (!statusMsg.equals("")) {
            System.out.println(statusMsg);
            statusMsg = "";
        }
        System.out.print("|> "); //Prompt
    }

    //Compose string for displaying message builder with printFrame
    private String getDraftDisplay() {
        String draftDisplay = "";
        draftDisplay += "Subject: " + newMsg.getHeaderStore().getSubject() + "\n";
        draftDisplay += "From: " + newMsg.getHeaderStore().getFrom() + "\n";
        draftDisplay += "To: " + newMsg.getHeaderStore().getTo() + "\n";
        draftDisplay += "CC: " + newMsg.getHeaderStore().getCC() + "\n";
        draftDisplay += "BCC: " + newMsg.getHeaderStore().getBCC() + "\n";
        draftDisplay += " \n";
        draftDisplay += "Body:\n";
        draftDisplay += "----------------------------------------------------------------------------------------" +
                "--------------------------------\n";
        for (int i = 0; i < msgBodyArray.size(); i++) {
            draftDisplay += msgBodyArray.get(i) + "\n";
        }
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

    //Give text to be aligned, with a type of alignment (CENTER, RIGHT), length of initial left offset,
    //and the total length of the line, returns a spacer String to be used for alignment
    //Returns
    private static String getSpacing(String text, String type, int leftOffset, int totalLength) {
        String spacer = "";
        int numSpaces = 0;
        if (leftOffset < 0 || totalLength < 0) {
            return "BAD INPUT";
        }
        if (type.equalsIgnoreCase("CENTER")) {
            numSpaces = (totalLength - text.length()) / 2 - leftOffset;

        } else if (type.equalsIgnoreCase("RIGHT")) {
            numSpaces = totalLength - text.length() - leftOffset;
        } else {
            return "BAD TYPE";
        }
        if (numSpaces < 0) {
            return "BAD FIT";
        }
        for (int i = 0; i < numSpaces; i++) {
            spacer += " ";
        }
        return spacer;
    }

    //-----LOGO METHODS-----
    private String setBigLogo() {
        ArrayList<String> logoLines = Storage.getFileStrings(bigLogoAdress);
        if (logoLines.get(0).equals("FILE NOT FOUND")) {
            return "BIG LOGO FILE NOT FOUND";
        } else if (logoLines.get(0).equals("READ ERROR")) {
            return "BIG LOGO READ ERROR";
        } else {
            bigLogoLines = logoLines;
            return "SUCCESS";
        }
    }

    private String setSmallLogo() {
        ArrayList<String> logoLines = Storage.getFileStrings(smallLogoAdress);
        if (logoLines.get(0).equals("FILE NOT FOUND")) {
            return "SMALL LOGO FILE NOT FOUND";
        } else if (logoLines.get(0).equals("READ ERROR")) {
            return "SMALL LOGO READ ERROR";
        } else {
            smallLogoLines = logoLines;
            return "SUCCESS";
        }
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

    private boolean checkInputMatch(String cmd, String argType1, String argType2) {
        return ParseUtils.checkInputMatch(userInput, cmd, argType1, argType2);
    }

    //Counts the number of target chars in given input string
    //0 if none
    private static int countChar(String text, char target) {
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
    private static String getMessageSummaries(ArrayList<HeaderStore> HeaderStoreList) {
        String retStr = "";
        for (int i = HeaderStoreList.size() - 1; i >= 0; i--) {
            retStr += getMessageSummary(HeaderStoreList.get(i)) + "\n";
        }
        return retStr;
    }

    private static String getMessageSummaryList(ArrayList<HeaderStore> HeaderStoreList) {
        String[][] summaryArray = new String[HeaderStoreList.size()][];
        for (int i = HeaderStoreList.size() - 1; i >= 0; i--) {
            summaryArray[i] = getMessageSummary(HeaderStoreList.get(i));
        }
        //Each subarray has msgNum in 0, date in 1, from in 2, and subject in 3
        int msgNumMax = 5;
        int dateMax = 4;
        int fromLimit = 30; //max allowed length of from field
        int fromMax = 4;
        int subjectLimit = 0; //gets calculated later
        int subjectMax = 7;
        //Find maximum length of msgNum Strings
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][0].length() > msgNumMax) {
                msgNumMax = summaryArray[i][0].length();
            }
        }
        //Find max length of date Strings
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][1].length() > dateMax) {
                dateMax = summaryArray[i][1].length();
            }
        }
        int dateLimit = dateMax;
        //Find max length of from Strings
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][2].length() > fromMax) {
                fromMax = summaryArray[i][2].length();
            }
        }
        if (fromMax < fromLimit) {
            subjectLimit = 120 - 4 - msgNumMax - 2 - 2 - 5 - dateMax - 2 - fromMax - 2 - 2;
        } else {
            subjectLimit = 120 - 4 - msgNumMax - 2 - 2 - 5 - dateMax - 2 - fromLimit - 2 - 2;
        }
        // = total - dividerCount - msgNumMax - spacers - spacers - SAVED - dataMax - spacers - fromMax - spacers
        //- subjectSpacers
        //Find max length of subject Strings
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][3].length() > subjectMax) {
                if (summaryArray[i][3].length() > subjectLimit) {
                    subjectMax = subjectLimit;
                    break;
                } else {
                    subjectMax = summaryArray[i][3].length();
                }
            }
        }
        //Correct fromLimit if subjectMax < subjectLimit and fromMax > fromLimit
        if (subjectMax < subjectLimit && fromMax > fromLimit) {
            if (fromMax - fromLimit < subjectLimit - subjectMax) {
                subjectLimit = (fromMax - fromLimit);
                fromLimit = fromMax;
            } else {
                fromLimit = subjectLimit - subjectMax;
                subjectLimit = subjectMax;
            }
        }
        //Shorten date Strings if need be (limit is already known)
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][1].length() > dateLimit) {
                summaryArray[i][1] = appendElipse(summaryArray[i][1], dateLimit);
            }
        }
        //Shorten from Strings if need be (limit is already known)
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][2].length() > fromLimit) {
                summaryArray[i][2] = appendElipse(summaryArray[i][2], fromLimit);
            }
        }
        //Shorten subject Strings if need be (limit is already known)
        for (int i = 0; i < summaryArray.length; i++) {
            if (summaryArray[i][3].length() > subjectLimit) {
                summaryArray[i][3] = appendElipse(summaryArray[i][3], subjectLimit);
            }
        }
        //Create header line of Inbox list
        String LHMsgNum = " " + getSpacing("Msg #", "CENTER", 1, msgNumMax + 2) + "Msg #";
        String RHMsgNum = addEndSpacer(LHMsgNum, msgNumMax + 2) + "|";
        String LHDate = " " + getSpacing("Date", "CENTER", 1, dateLimit + 2) + "Date";
        String RHDate = addEndSpacer(LHDate, dateLimit + 2) + "|";
        String LHFrom = " " + getSpacing("From", "CENTER", 1, fromLimit + 2) + "From";
        String RHFrom = addEndSpacer(LHFrom, fromLimit + 2) + "|";
        String LHSubject = " " + getSpacing("Subject", "CENTER", 1, subjectLimit + 2) + "Subject";
        String RHSubject = addEndSpacer(LHSubject, subjectLimit + 2) + "|";
        String LHSaved = " Saved \n";
        String headerLine = LHMsgNum + RHMsgNum + LHDate + RHDate + LHFrom + RHFrom + LHSubject + RHSubject + LHSaved;
        String totalList = headerLine + "---------------------------------------------------------------------------" +
                "---------------------------------------------\n";
        //Generate spaced summary lines and append to list
        for (int i = 0; i < summaryArray.length; i++) {
            String leftMsgNum = " " + getSpacing(summaryArray[i][0], "CENTER", 0, msgNumMax + 2) + summaryArray[i][0];
            String rightMsgNum = addEndSpacer(leftMsgNum, msgNumMax + 2) + "|";
            String leftDate = " " + getSpacing(summaryArray[i][1], "CENTER", 1, dateLimit + 2) + summaryArray[i][1];
            String rightDate = addEndSpacer(leftDate, dateLimit + 2) + "|";
            String leftFrom = " " + getSpacing(summaryArray[i][2], "CENTER", 1, fromLimit + 2) + summaryArray[i][2];
            String rightFrom = addEndSpacer(leftFrom, fromLimit + 2) + "|";
            String leftSubject = " " + getSpacing(summaryArray[i][3], "CENTER", 1, subjectLimit + 2) + summaryArray[i][3];
            String rightSubject = addEndSpacer(leftSubject, subjectLimit + 2) + "|";
            String leftSaved = " " + getSpacing("Y", "CENTER", 1, " Saved ".length()) + "Y\n";
            String msgLine = leftMsgNum + rightMsgNum + leftDate + rightDate + leftFrom + rightFrom + leftSubject +
                    rightSubject + leftSaved;
            totalList += msgLine;
        }
        return totalList;
    }

    //Returns string of spacers needed to bring a text to total length
    private static String addEndSpacer(String text, int totalLength) {
        String spacer = "";
        if (totalLength - text.length() < 0) {
            return "BAD FIT";
        }
        for (int i = 0; i < totalLength - text.length(); i++) {
            spacer += " ";
        }
        return spacer;
    }

    //Trims text if over limit and appends ...
    private static String appendElipse(String text, int limit) {
        return text.substring(0, limit - 3) + "...";
    }

    //Builds a summary line from a HeaderStore
    private static String[] getMessageSummary(HeaderStore msgHeader) {
        String msgNum = Integer.toString(msgHeader.getMessageNum()); //this is alsways present
        String date = msgHeader.getDate(); //may be null
        if (date.equals("")) {
            date = "NO DATE";
        } else {
            date = ParseUtils.parseEmailDate(date);
        }
        String from = msgHeader.getFrom(); //may be null
        if (from.equals("")) {
            from = "NO FROM ADDRESS";
        }
        String subject = msgHeader.getSubject(); //may be null
        if (subject.equals("")) {
            subject = "NO SUBJECT";
        }
        return new String[]{msgNum, date, from, subject};
    }
}