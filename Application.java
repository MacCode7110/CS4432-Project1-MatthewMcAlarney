import java.io.IOException;
import java.util.Scanner;

/**
 * The driver class of this Java application
 */
public class Application {

    /**
     * Start of the execution of the Java program; parses and handles user input, which includes GET, SET, PIN, and UNPIN commands
     * @param args the list of command line arguments passed to main
     * @throws IOException
     */
    public static void main(String [] args) throws IOException {
        BufferPool bP = new BufferPool();
        bP.initialize(Integer.parseInt(args[0]));
        System.out.println("The program is ready for the next command.");
        Scanner sc = new Scanner(System.in);
        while (true) {
            //Include exit command to escape the program.
            String userInput = sc.nextLine();
            if (userInput.substring(0,4).equalsIgnoreCase("EXIT")) {
                System.out.println("Program Exited.");
                break;
            }
            if (userInput.substring(0,3).equalsIgnoreCase("GET")) {
                bP.GET(Integer.parseInt(userInput.substring(4)));
            }
            if (userInput.substring(0,3).equalsIgnoreCase("SET")) {
                bP.SET(Integer.parseInt(userInput.substring(4,
                                (userInput.indexOf(" ",userInput.indexOf(" ") + " ".length())))),
                        userInput.substring(userInput.indexOf(" ", userInput.indexOf(" ")
                                + " ".length() + 2), userInput.length() - 1));
            }
            if (userInput.substring(0,3).equalsIgnoreCase("PIN")) {
                bP.PIN(Integer.parseInt(userInput.substring(4)));
            }
            if (userInput.substring(0,5).equalsIgnoreCase("UNPIN")) {
                bP.UNPIN(Integer.parseInt(userInput.substring(6)));
            }
        }
    }
}
