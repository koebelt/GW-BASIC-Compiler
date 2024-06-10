

public class SyntaxChecker {
    static void checkSyntax(SimpleNode node, SimpleNode root) {
        if (node.toString().equals("Line")) {
            checkLine(node, root);
        } else {
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                checkSyntax((SimpleNode) node.jjtGetChild(i), root);
            }
        }
    }

    static void checkLine(SimpleNode line, SimpleNode root) {
        if (line.jjtGetChild(0).toString().equals("Statement")) {
            checkStatement((SimpleNode) line.jjtGetChild(0).jjtGetChild(0), line, root);
        } else {
            throw new RuntimeException("Invalid Line: " + line.toString() + " " + line.value);
        }
    }

    static void checkStatement(SimpleNode statement, SimpleNode line, SimpleNode root) {
        // if (statement.toString().equals("GotoStatement")) {
        //     checkGotoStatement(statement, line, root);
        // } else if (statement.toString().equals("ForStatement")) {
        //     checkForStatement(statement, line, root);
        // } else if (statement.toString().equals("NextStatement")) {
        //     checkNextStatement(statement, line, root);
        // } else if (statement.toString().equals("WhileStatement")){
        //     checkWhileStatement(statement, line, root);
        // } else if (statement.toString().equals("WendStatement")){
        //     checkWendStatement(statement, line, root);
        // } else if (statement.toString().equals("GosubStatement")) {
        //     checkGosubStatement(statement, line, root);
        // } else if (statement.toString().equals("ReturnStatement")) {
        //     checkReturnStatement(statement, line, root);
        // }

        // Other statements should not require syntax checking
    }


}
