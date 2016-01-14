import java.io.*;
import java.net.*;
import javax.net.ssl.*;

public class Client{
    public static void main(String[]args){
	BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in)); //Read from console
        PrintStream sysOut = System.out; //Print to console
	boolean quitUser=false;
	String mode="START_MENU";
	while (! quitUser){
	    if (mode.equals("START_MENU")){
		sysOut.println("Welcome to GioMhail, your go to email client");
		sysOut.println("");	
		sysOut.println("Do you want to send or read emails?");