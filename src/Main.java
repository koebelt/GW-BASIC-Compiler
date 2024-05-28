
public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <source-file>");
            return;
        }

        try (java.io.FileInputStream fis = new java.io.FileInputStream(args[0])) {
            GWBASICParser parser = new GWBASICParser(fis);
            System.out.println("Parsing completed successfully.");

            Compiler compiler = new Compiler();
            compiler.compile(parser);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
