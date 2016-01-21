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
 - Copy imports
 - Define instance vars
 - Default constructor
 - Main function
 - Create function for each mode
 - ... See client_remake_plan.txt in Resources for more info
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

    //Menu output vars
    private String statusMsg; //Any error or help messages

    //POP vars
    private POPSession POP = null;
    private int msgCount = 0; //updated number of msgs in inbox
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
        sysIn = new BufferedReader(new InputStreamReader(System.in));
        sysOut = System.out;
        userInput = "";
        consoleError = false;
        quitUser = false;
        mode = "WELCOME";
        statusMsg = "";
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
            if (getMode().equals("WELCOME")) {
                modeWelcome();
            } else if (getMode().equals("PROT_CHOOSE")) {
                modeProtChoose();
            } else if (getMode().equals("SMTP_SETUP")) {
                modeSmtpSetup();
            } else if (getMode().equals("SMTP_LOGIN")) {
                modeSmtpLogin();
            } else if (getMode().equals("SMTP_MAIN")) {
                modeSmtpMain();
            } else if (getMode().equals("SMTP_FROM")) {
                modeSmtpFrom();
            } else if (getMode().equals("SMTP_TO")) {
                modeSmtpTo();
            } else if (getMode().equals("SMTP_CC")) {
                modeSmtpCC();
            } else if (getMode().equals("SMTP_BCC")) {
                modeSmtpBCC();
            } else if (getMode().equals("SMTP_BODY")) {
                modeSmtpBody();
            } else if (getMode().equals("SMTP_CONFIRM")) {
                modeSmtpConfirm();
            } else if (getMode().equals("SMTP_RESULT")) {
                modeSmtpResult();
            } else if (getMode().equals("POP_SETUP")) {
                modePopSetup();
            } else if (getMode().equals("POP_LOGIN")) {
                modePopLogin();
            } else if (getMode().equals("POP_MAIN")) {
                modePopMain();
            } else if (getMode().equals("POP_INBOX")) {
                modePopMain();
            } else if (getMode().equals("POP_VIEW")) {
                modePopView();
            } else {
                sysOut.println("What have you done?!?!");
                quitUser = true;
            }
        }
        System.out.println(">>GOODBYE<<");
    }

    public boolean isQuitUser() {
        return quitUser;
    }

    public String getMode() {
        return mode;
    }

    //-----MODE METHODS-----
    public void modeWelcome() {
        //--Print menu header
        System.out.println("GioMhail\n" +
                "Menu Map: Welcome\n" +
                "\n" +
                "Welcome to GioMhail, your go-to email client!\n" +
                "Would you like to continue [y] or exit [exit]?\n" +
                "Cmds: [y] (y + <ENTER>), [exit] (exit + <ENTER>)");
        System.out.println(""); //Blank line
        printStatusMsg(); //Print statusMsg
        promptGetUserInput(); //Print prompt, get input
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (userInput.equalsIgnoreCase("y")) {
            mode = "PROT_CHOOSE";
        } else if (userInput.equalsIgnoreCase("exit")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    public void modeProtChoose() {
        //--Print menu header
        System.out.println("GioMhail\n" +
                "Menu Map: Welcome > Choose Read\\Send\n" +
                "Choose Read\\Send\n" +
                "\n" +
                "Would you like to read [read], send [send], or exit [exit]?\n" +
                "Cmds: [read], [send], [exit]");
        System.out.println(""); //Blank line
        printStatusMsg(); //Print statusMsg
        promptGetUserInput(); //Print prompt, get input
        //--Check user input
        if (consoleError) { //If exception on reading console
            clearScreen();
            System.out.println("Unknown console error detected (Unable to read input).\n" +
                    "Program exiting.");
            quitUser = true;
        } else if (userInput.equalsIgnoreCase("read")) {
            mode = "POP_SETUP";
        } else if (userInput.equalsIgnoreCase("send")) {
            mode = "SMTP_SETUP";
        } else if (userInput.equalsIgnoreCase("exit")) {
            quitUser = true;
        } else {
            statusMsg = "Please enter a valid command!";
        }
    }

    public void modeSmtpSetup() {
        //
    }

    public void modeSmtpLogin() {
        //
    }

    public void modeSmtpMain() {
        //
    }

    public void modeSmtpSubject() {
        //
    }

    public void modeSmtpFrom() {
        //
    }

    public void modeSmtpTo() {
        //
    }

    public void modeSmtpCC() {
        //
    }

    public void modeSmtpBCC() {
        //
    }

    public void modeSmtpBody() {
        //
    }

    public void modeSmtpConfirm() {
        //
    }

    public void modeSmtpResult() {
        //
    }

    public void modePopSetup() {
        //
    }

    public void modePopLogin() {
        //
    }

    public void modePopMain() {
        //
    }

    public void modePopInbox() {
        //
    }

    public void modePopView() {
        //
    }

    //-----TUI METHODS-----
    public void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

    //Prints the statusMsg and resets
    private void printStatusMsg() {
        if (!statusMsg.equals("")) {
            System.out.println(statusMsg);
        }
        statusMsg = "";
    }

    //Prompt and read user input
    private void promptGetUserInput() {
        try {
            System.out.print("|> ");
            userInput = sysIn.readLine();
        } catch (IOException e) {
            consoleError = true;
        }
    }
}