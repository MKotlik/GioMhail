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
    public void modeWelcome() {
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

    public void modeProtChoose() {
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

    public void modeSmtpSetup() {
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
                return argType1.equals("INT");
            } else {
                return argType1.equals("STRING");
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
                return match1 && argType2.equals("INT");
            } else {
                return match1 && argType2.equals("STRING");
            }
        }
        return false; //something strange has happened
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

}