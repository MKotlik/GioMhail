public class ConsoleTest {
    public static void main(String[]args) {
        //System.out.print("\u001b[2J");
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}