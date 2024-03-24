public class Application {
    public static void main(String [] args) {
        BufferPool bP = new BufferPool(Integer.parseInt(args[0]));
        System.out.println("The program is ready for the next command.");
    }
}
