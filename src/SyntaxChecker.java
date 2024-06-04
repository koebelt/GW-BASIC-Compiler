

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
        if (statement.toString().equals("GotoStatement")) {
            checkGotoStatement(statement, line, root);
        } else if (statement.toString().equals("ForStatement")) {
            checkForStatement(statement, line, root);
        } else if (statement.toString().equals("NextStatement")) {
            checkNextStatement(statement, line, root);
        } else if (statement.toString().equals("WhileStatement")){
            checkWhileStatement(statement, line, root);
        } else if (statement.toString().equals("WendStatement")){
            checkWendStatement(statement, line, root);
        } else if (statement.toString().equals("GosubStatement")) {
            checkGosubStatement(statement, line, root);
        } else if (statement.toString().equals("ReturnStatement")) {
            checkReturnStatement(statement, line, root);
        }

        // Other statements should not require syntax checking
    }


    static void checkGotoStatement(SimpleNode gotoStatement, SimpleNode line, SimpleNode root) {
        try {
            Integer.parseInt(gotoStatement.value.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid GOTO statement: Line " + line.value + " references non-integer line number");
        }
        int lineNumber = Integer.parseInt(gotoStatement.value.toString());

        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                if (Integer.parseInt(node.value.toString()) == lineNumber) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid GOTO statement: Line " + line.value + " references non-existent line " + lineNumber);
        }
    }

    static void checkGosubStatement(SimpleNode gosubStatement, SimpleNode line, SimpleNode root) {
        try {
            Integer.parseInt(gosubStatement.value.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid GOSUB statement: Line " + line.value + " references non-integer line number");
        }
        int lineNumber = Integer.parseInt(gosubStatement.value.toString());

        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                if (Integer.parseInt(node.value.toString()) == lineNumber) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid GOSUB statement: Line " + line.value + " references non-existent line " + lineNumber);
        }
    }

    static void checkForStatement(SimpleNode forStatement, SimpleNode line, SimpleNode root) {
        String variable = (String) forStatement.value;

        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                if (node.jjtGetNumChildren() > 0) {
                    SimpleNode statement = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0);
                    if (statement.toString().equals("NextStatement")) {
                        if (statement.value.equals(variable)) {
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid FOR statement: Line " + line.value + " references non-existent next statement for variable " + variable);
        }
    }


    static void checkNextStatement(SimpleNode nextStatement, SimpleNode line, SimpleNode root) {
        String variable = (String) nextStatement.value;

        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                if (node.jjtGetNumChildren() > 0) {
                    SimpleNode statement = (SimpleNode) node.jjtGetChild(0).jjtGetChild(0);
                    if (statement.toString().equals("ForStatement")) {
                        if (statement.value.equals(variable)) {
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid NEXT statement: Line " + line.value + " references non-existent for statement for variable " + variable);
        }
    }

    static void checkWhileStatement(SimpleNode whileStatement, SimpleNode line, SimpleNode root) {
        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                    SimpleNode statement = (SimpleNode) node.jjtGetChild(j).jjtGetChild(0);
                    if (statement.toString().equals("WendStatement")) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid WHILE statement: Line " + line.value + " references non-existent WEND statement");
        }
    }

    static void checkWendStatement(SimpleNode wendStatement, SimpleNode line, SimpleNode root) {
        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                    SimpleNode statement = (SimpleNode) node.jjtGetChild(j).jjtGetChild(0);
                    if (statement.toString().equals("WhileStatement")) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid WEND statement: Line " + line.value + " references non-existent WHILE statement");
        }
    }

    static void checkReturnStatement(SimpleNode returnStatement, SimpleNode line, SimpleNode root) {

        boolean found = false;

        for (int i = 0; i < root.jjtGetNumChildren(); i++) {
            SimpleNode node = (SimpleNode) root.jjtGetChild(i);
            if (node.toString().equals("Line")) {
                for (int j = 0; j < node.jjtGetNumChildren(); j++) {
                    SimpleNode statement = (SimpleNode) node.jjtGetChild(j).jjtGetChild(0);
                    if (statement.toString().equals("GosubStatement")) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            throw new RuntimeException("Invalid RETURN statement: Line " + line.value + " references non-existent GOSUB statement");
        }
    }



}
