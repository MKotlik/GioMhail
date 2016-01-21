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
    //private PrintStream sysOut; //leave out for now to test try/catch necessity

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
        //sysOut = System.out;
        quitUser = false;
        mode = "WELCOME";
        statusMsg = "";
    }

    //-----MAIN-----
    private static void main(String[]args) {
        NewClient mailApp = NewClient(); //create a new NewClient object

        while (! mailApp.isQuitUser()) { //Until user quits
            mailApp.clearScreen(); //Clear the screen
            if (mailApp.getMode().equals("WELCOME")) {
                mailApp.modeWelcome();
            } else if (mailApp.getMode().equals("PROT_CHOOSE")) {
                mailApp.modeProtChoose();
            } else if (mailApp.getMode().equals("SMTP_SETUP")) {
                mailApp.modeSmtpSetup();
            } else if (mailApp.getMode().equals("SMTP_LOGIN")) {
                mailApp.modeSmtpLogin();
            } else if (mailApp.getMode().equals("SMTP_MAIN")) {
                mailApp.modeSmtpMain();
            } else if (mailApp.getMode().equals("SMTP_FROM")) {
                mailApp.modeSmtpFrom();
            } else if (mailApp.getMode().equals("SMTP_TO")) {
                mailApp.modeSmtpTo();
            } else if (mailApp.getMode().equals("SMTP_CC")) {
                mailApp.modeSmtpCC();
            } else if (mailApp.getMode().equals("SMTP_BCC")) {
                mailApp.modeSmtpBCC();
            } else if (mailApp.getMode().equals("SMTP_BODY")) {
                mailApp.modeSmtpBody();
            } else if (mailApp.getMode().equals("SMTP_CONFIRM")) {
                mailApp.modeSmtpConfirm();
            } else if (mailApp.getMode().equals("SMTP_RESULT")) {
                mailApp.modeSmtpResult();
            } else if (mailApp.getMode().equals("POP_SETUP")) {
                mailApp.modePopSetup();
            } else if (mailApp.getMode().equals("POP_LOGIN")) {
                mailApp.modePopLogin();
            } else if (mailApp.getMode().equals("POP_MAIN")) {
                mailApp.modePopMain();
            } else if (mailApp.getMode().equals("POP_INBOX")) {
                mailApp.modePopMain();
            } else if (mailApp.getMode().equals("POP_VIEW")) {
                mailApp.modePopView();
            } else {
                System.out.println("What have you done?!?!");
            }
        }
    }

    //-----LOOP METHODS-----
    public boolean isQuitUser() {
        return quitUser;
    }

    public String getMode() {
        return mode;
    }

    //-----MODE METHODS-----
    public void modeWelcome(){
        //
    }

    public void modeProtChoose(){
        //
    }

    public void modeSmtpSetup(){
        //
    }

    public void modeSmtpLogin(){
        //
    }

    public void modeSmtpMain(){
        //
    }

    public void modeSmtpSubject(){
        //
    }

    public void modeSmtpFrom(){
        //
    }

    public void modeSmtpTo(){
        //
    }

    public void modeSmtpCC(){
        //
    }

    public void modeSmtpBCC(){
        //
    }

    public void modeSmtpBody(){
        //
    }

    public void modeSmtpConfirm(){
        //
    }

    public void modeSmtpResult(){
        //
    }

    public void modePopSetup(){
        //
    }

    public void modePopLogin(){
        //
    }

    public void modePopMain(){
        //
    }

    public void modePopInbox(){
        //
    }

    public void modePopView(){
        //
    }

    //-----TUI METHODS-----
    public void clearScreen() {
        System.out.println("\033[H\033[2J");
        System.out.flush();
    }

}