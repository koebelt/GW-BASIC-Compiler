import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <source-file>");
            return;
        }

        try {
            FileInputStream in = new FileInputStream(args[0]);
            GWBasicParser parser = new GWBasicParser(in);
            SimpleNode root = parser.Program();
            root.dump(""); // Print the AST
            generatePCL(root);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void generatePCL(SimpleNode root) {
        // Placeholder for PCL generation logic
        System.out.println("Generating PCL code...");
        // Example: Traverse the AST and generate PCL code
        traverse(root);
    }

    public static void traverse(SimpleNode node) {
        // Example: Print node information and recursively traverse children
        System.out.println(node.toString());
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            traverse((SimpleNode) node.jjtGetChild(i));
        }
    }
}

