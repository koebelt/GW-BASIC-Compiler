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
            dumpAST(root, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void generatePCL(SimpleNode root) {
        traverse(root);
    }

    public static void traverse(SimpleNode node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            traverse((SimpleNode) node.jjtGetChild(i));
        }
    }

    public static void dumpAST(SimpleNode node, int indentLevel) {
        String indentation = "";

        for (int i = 0; i < indentLevel; i++) {
            indentation += " ";
        }
        System.out.println(indentation + node + (node.jjtGetValue() != null ? " = " + node.jjtGetValue() : ""));
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            dumpAST((SimpleNode) node.jjtGetChild(i), indentLevel + 1);
        }
    }
}

