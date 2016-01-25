public class DisplayTest {
    public static void main(String[] args) {
        String line = "";
        for (int i = 0; i < 100; i++) {
            line += "=";
        }
        System.out.println(line);
        String logo = "GioMhail";
        String leftSide = logo + getSpacing("READ: MAIN", "CENTER", logo.length(), 100) + "READ: MAIN";
        System.out.println(leftSide + getSpacing("MISHA", "RIGHT", leftSide.length(), 100) + "MISHA");
    }

    //Give text to be aligned, with a type of alignment (CENTER, RIGHT), length of initial left offset,
    //and the total length of the line, returns a spacer String to be used for alignment
    //Returns
    public static String getSpacing(String text, String type, int leftOffset, int totalLength) {
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
        for (int i = 0 ; i < numSpaces; i++) {
            spacer += " ";
        }
        return spacer;
    }


}