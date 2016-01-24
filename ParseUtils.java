/* GioMhail by Coolgle
   - ParseUtils
   - Copyright (c) 2016, Giovanni Topa and Mikhail Kotlik, All Rights Reserved.
   - APCS Term 1 Final Project, Stuyvesant High School
*/

import java.util.Date;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;

public class ParseUtils {
    public static void main(String[] args) {
        System.out.println(parseEmailDate("Wed, 20 Jan 2016 16:16:02 -0800 (PST)"));
    }
    
    //-----DATE-----
    public static String parseEmailDate(String emailDate) {
        String trimmed = "";
        if (emailDate.contains(" (")) {
            trimmed = emailDate.substring(0, emailDate.indexOf(" (")); //substring to " ("
        } else {
            trimmed = emailDate;
        }
        String emailPattern = "EEE, dd MMM yyyy HH:mm:ss Z";
        SimpleDateFormat emailFormat = new SimpleDateFormat(emailPattern, Locale.US);
        ParsePosition startParse = new ParsePosition(0);
        Date localeDate = emailFormat.parse(trimmed, startParse);
        return localeDate.toString();
    }
    
    //-----TO-----
    
    //CheckTo, returns String msg signifying/error or success in To address list
    //Currently only returns "GOOD TO", and "BAD TO"
    //Later expand to support different error msgs
    public static String checkTo(String toHeader) {
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

    //This method parses the toHeader
    //The header normally looks like: "Gio Topa <giotopa@gmail.com>, Gio Kotlik <giokotlik@yahoo.com>"
    //Header must have addresses as <...>, with at least three characters within <...> and one @
    //If the above condition isn't met, return "BAD TO" as first element of ArrayList
    //Otherwise, add the addresses with the brackets to an ArrayList and return it
    public static ArrayList<String> parseTo(String toHeader) {
        ArrayList<String> toAddresses = new ArrayList<String>();
        int LBrCount = countChar(toHeader, '<');
        int RBrCount = countChar(toHeader, '>');
        if ((LBrCount < 1) || (RBrCount < 1) || (LBrCount != RBrCount)) {
            toAddresses.add("BAD TO");
            return toAddresses;
        }
        String addressLine = toHeader;
        for (int i = 0; i < LBrCount; i++) {
            int bracket1Ind = addressLine.indexOf('<');
            int bracket2Ind = addressLine.indexOf('>');
            if (!(bracket1Ind + 4 <= bracket2Ind)) { //At least three chars between brackets <...>
                toAddresses.add("BAD TO"); //Need this to insure always has 0 index
                toAddresses.set(0, "BAD TO");
                return toAddresses;
            }
            if (countChar(addressLine.substring(bracket1Ind, bracket2Ind + 1), '@') != 1) { //Must be a @
                toAddresses.add("BAD TO"); //Need this to insure always has 0 index
                toAddresses.set(0, "BAD TO");
                return toAddresses;
            }
            toAddresses.add(addressLine.substring(bracket1Ind, bracket2Ind + 1));
            if (bracket2Ind != addressLine.length() - 1) { //If > isn't at the end
                addressLine = addressLine.substring(bracket2Ind + 1, addressLine.length()); //Get string after >
            }
        }
        return toAddresses;
    }
    
    //-----FROM-----

    //checkFrom, returns String msg signifying/error or success in From address
    //Currently only returns "GOOD FROM", and "BAD FROM"
    //Later expand to support different error msgs
    public static String checkFrom(String fromHeader) {
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
    
    //This method parses the fromHeader
    //The header normally looks like: "Misha Kotlik <mikekotlik@gmail.com>"
    //This function has to count the number of brackets. If it doesn't match 2, return "BAD FROM"
    //Otherwise, the address with the brackets
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
        return fromHeader.substring(bracket1Ind, bracket2Ind + 1);
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

    //-----USER INPUT-----

    //Checks if userInput matches expected Cmd and/or argTypes
    //Takes in <cmdName> <type of arg1> <type of arg2>
    //cmd can be the name of a command or NONE
    //argTypes can be INT, STRING, or NONE
    public static boolean checkInputMatch(String input, String cmd, String argType1, String argType2) {
        if (argType1.equals("NONE")) { //Must be cmd
            return cmd.equalsIgnoreCase(input.trim());
        }
        int reqSpaces = 0;
        if ((!cmd.equals("NONE")) && (!argType2.equals("NONE"))) {
            reqSpaces = 2; //cmd arg arg
        } else if (cmd.equals("NONE") || argType2.equals("NONE")) {
            reqSpaces = 1; //cmd arg or arg arg
        } else if (cmd.equals("NONE") && argType2.equals("NONE")) {
            reqSpaces = 0; //arg
        } else {
            return false; //NONE NONE NONE
        }
        if (reqSpaces != countChar(input, ' ')) {
            return false; //Number of elements doesn't meet expectations
        }
        Scanner intScan = new Scanner(input.trim());
        if (reqSpaces == 0) { //arg1
            if (intScan.hasNextInt(10)) { //first element is int
                intScan.close();
                return argType1.equals("INT");
            } else {
                intScan.close();
                return argType1.equals("STRING");
            }
        } else if (reqSpaces == 1 && (!argType2.equals("NONE"))) { //arg1 arg2
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
                return input.toUpperCase().startsWith(cmd) && argType1.equals("INT");
            } else {
                return input.toUpperCase().startsWith(cmd) && argType1.equals("STRING");
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
                return input.toUpperCase().startsWith(cmd) && match1 && argType2.equals("INT");
            } else {
                return input.toUpperCase().startsWith(cmd) && match1 && argType2.equals("STRING");
            }
        }
        return false; //something strange has happened
    }
}