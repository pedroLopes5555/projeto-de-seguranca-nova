package BusinessLogic;

public class Utils {

    public static String stringInRed(String value){
        // ANSI escape code for red text
        final String RED = "\u001B[31m";
        // ANSI escape code to reset text color
        final String RESET = "\u001B[0m";

        // Print text in red
        return RED + value + RESET;

    }
}
