import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

enum VariableType {
    INT,
    STRING,
    DOUBLE,
}

public class PCLCompiler {
    private ArrayList<Integer> gotoLines = new ArrayList<Integer>();
    private ArrayList<String> pcl = new ArrayList<String>();
    private Map<Integer, ArrayList<String>> functions = new HashMap<>();
    private ArrayList<Integer> functionQueue = new ArrayList<Integer>();
    private Map<String, String> globalVariables = new HashMap<String, String>();


    PCLCompiler(String filename) {
        File myObj = new File(filename);

        try {
            myObj.createNewFile();
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    void writePCL() {
        try {
            java.io.FileWriter myWriter = new java.io.FileWriter("output.c");
            myWriter.write("#include <stdio.h>\n");
            myWriter.write("#include <math.h>\n");
            myWriter.write("#include <string.h>\n");
            myWriter.write("#include <stdlib.h>\n");
            myWriter.write("#define ARRAY_LENGTH(array) (sizeof(array) / sizeof((array)[0]))\n\n");
            myWriter.write("double my_sin(double x) { return sin(x); }\n");
            myWriter.write("double my_cos(double x) { return cos(x); }\n");
            myWriter.write("double my_tan(double x) { return tan(x); }\n");
            myWriter.write("double my_log(double x) { return log(x); }\n");
            myWriter.write("double my_exp(double x) { return exp(x); }\n");
            myWriter.write("double my_sqrt(double x) { return sqrt(x); }\n");
            myWriter.write("char* read_line() { size_t size = 1024; char* buffer = malloc(size); if (fgets(buffer, size, stdin) != NULL) { buffer[strcspn(buffer, \"\\n\")] = '\\0'; return buffer; } free(buffer); return NULL; }\n");
            myWriter.write("char* my_concat(const char* str1, const char* str2) { char* result = malloc(strlen(str1) + strlen(str2) + 1); strcpy(result, str1); strcat(result, str2); return result; }\n");
            myWriter.write("char* my_dstr(double value) { char* buffer = malloc(32); snprintf(buffer, 32, \"%f\", value); return buffer; }\n");
            myWriter.write("char* my_istr(int value) { char* buffer = malloc(12); snprintf(buffer, 12, \"%d\", value); return buffer; }\n");
            myWriter.write("int my_dint(double value) { return (int)value; }\n");
            myWriter.write("int my_sint(const char* str) { return atoi(str); }\n");
            myWriter.write("double my_idbl(int value) { return (double)value; }\n");
            myWriter.write("double my_sdbl(const char* str) { return atof(str); }\n\n");

            for (String variable : globalVariables.values()) {
                // System.err.println("Variable: " + variable + "key: " + globalVariables.keySet());
                myWriter.write(variable + "\n");
            }
                myWriter.write("\n");

            for (Map.Entry<Integer, ArrayList<String>> entry : functions.entrySet()) {
                myWriter.write("void sub_" + entry.getKey() + "() {\n");
                for (String line : entry.getValue()) {
                    myWriter.write("    " + line + "\n");
                }
                myWriter.write("}\n\n");
            }
            for (String line : pcl) {
                myWriter.write(line + "\n");
            }
            myWriter.close();
        } catch (IOException e) {
            System.err.println("An error occurred.");
            e.printStackTrace();
        }
    }

    String generatePCL(SimpleNode node) {
        // System.err.println(node.toString());
        switch (node.toString()) {
            case "Program":
                this.pcl.add("int main() {");
                for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                    String line = generatePCL((SimpleNode) node.jjtGetChild(i));
                    if (this.functionQueue != null && !this.functionQueue.isEmpty()) {
                        this.functions.get(this.functionQueue.getFirst()).add(line);
                    } else {
                        this.pcl.add("    " + line);
                    }
                }
                this.pcl.add("}");
                break;
            case "Line":
                int lineNum = Integer.parseInt(node.jjtGetValue().toString());
                if (this.gotoLines.contains(lineNum)) {
                    this.pcl.add("line_" + lineNum + ":");
                }
                if (this.functions.containsKey(lineNum)) {
                    this.functionQueue.add(lineNum);
                }
                return generatePCL((SimpleNode) node.jjtGetChild(0)) + ";";
            case "Statement":
                return generatePCL((SimpleNode) node.jjtGetChild(0));
            case "InputStatement":
                VariableType variableType = getChildVariableType(node);
                    return generatePCL((SimpleNode) node.jjtGetChild(0)) + " = read_line()";

                // switch (variableType) {
                //     case INT:
                //         return "fgets(\"%d\", &" + generatePCL((SimpleNode) node.jjtGetChild(0)) + ")";
                //     case STRING:
                //     case DOUBLE:
                //         return "fgets(\"%lf\", &" + generatePCL((SimpleNode) node.jjtGetChild(0)) + ")";
                // }
                // break;
            case "PrintStatement":
                return PrintStatement(node);
            case "PrintItem":
                return "//" + generatePCL((SimpleNode) node.jjtGetChild(0));
            case "Expression":
                if (node.jjtGetValue() != null) {
                    return generatePCL((SimpleNode) node.jjtGetChild(0)) + node.jjtGetValue().toString() + generatePCL((SimpleNode) node.jjtGetChild(1));
                } else {
                    return generatePCL((SimpleNode) node.jjtGetChild(0));
                }
            case "Term":
                if (node.jjtGetValue() != null) {
                    return generatePCL((SimpleNode) node.jjtGetChild(0)) + node.jjtGetValue().toString() + generatePCL((SimpleNode) node.jjtGetChild(1));
                } else {
                    return generatePCL((SimpleNode) node.jjtGetChild(0));
                }
            case "Factor":
                if (node.jjtGetValue() != null) {
                    return node.jjtGetValue().toString() + generatePCL((SimpleNode) node.jjtGetChild(0));
                } else {
                    return generatePCL((SimpleNode) node.jjtGetChild(0));
                }
            case "Base":
                return generatePCL((SimpleNode) node.jjtGetChild(0));
            case "Number":
                return node.jjtGetValue().toString();
            case "String":
                return node.jjtGetValue().toString();
            case "ComparisonOperator":
                switch (node.jjtGetValue().toString()) {
                    case "=":
                        return "==";
                    case "<>":
                        return "!=";
                    default:
                        return node.jjtGetValue().toString();
                }
            case "Variable":
                if (!globalVariables.containsKey(getVariableNameExtentionString(node))) {
                char lastChar = node.jjtGetValue().toString().charAt(node.jjtGetValue().toString().length() - 1);

                globalVariables.put(getVariableNameExtentionString(node),
                    switch(lastChar) {
                        case '%' -> "int " + node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1)  + "_int";
                        case '$' -> "char *" + node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1) + "_string";
                        case '#' -> "double " + node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1) + "_double";
                        default -> null;
                    } + ";"
                );
                }
                return getVariableNameExtentionString(node);
            case "Assignment":
                String variableName = generatePCL((SimpleNode) node.jjtGetChild(0)).split("\\[") != null ? generatePCL((SimpleNode) node.jjtGetChild(0)).split("\\[")[0] : generatePCL((SimpleNode) node.jjtGetChild(0));
                if (globalVariables.containsKey(variableName))
                    return  generatePCL((SimpleNode) node.jjtGetChild(0)) + " = " + generatePCL((SimpleNode) node.jjtGetChild(1));
                variableType = getChildVariableType(node);
                System.err.println("Variable type: " + variableType + " Variable: " + variableName);
                switch (variableType) {
                    case INT:
                        return  "int " + generatePCL((SimpleNode) node.jjtGetChild(0)) + " = " + generatePCL((SimpleNode) node.jjtGetChild(1));
                    case STRING:
                        return  "char *" + generatePCL((SimpleNode) node.jjtGetChild(0)) + " = " + generatePCL((SimpleNode) node.jjtGetChild(1));
                    case DOUBLE:
                        return  "double " + generatePCL((SimpleNode) node.jjtGetChild(0)) + " = " + generatePCL((SimpleNode) node.jjtGetChild(1));
                }
                break;
            case "VariableAccess":
                return generatePCL((SimpleNode) node.jjtGetChild(0));
            case "DimStatement":
                String toReturn = "";
                if (!globalVariables.containsKey(generatePCL((SimpleNode) node.jjtGetChild(0)))) {
                    variableType = getChildVariableType((SimpleNode) node.jjtGetChild(0));
                    switch (variableType) {
                        case INT:
                            toReturn = "int " + generatePCL((SimpleNode) node.jjtGetChild(0));
                            break;
                        case STRING:
                            toReturn =  "char *" + generatePCL((SimpleNode) node.jjtGetChild(0));
                            break;
                        case DOUBLE:
                            toReturn =  "double " + generatePCL((SimpleNode) node.jjtGetChild(0));
                            break;
                    }
                    toReturn += ";";
                    globalVariables.put(getVariableNameExtentionString(node), toReturn);
                }
                return "";
            case "ArrayCall":
                return generatePCL((SimpleNode) node.jjtGetChild(0)) + "[" + generatePCL((SimpleNode) node.jjtGetChild(1)) + "]";
            case "StopStatement":
                return "exit(0)";
            case "GotoStatement":
                int line = Integer.parseInt(generatePCL((SimpleNode) node.jjtGetChild(0)));
                this.gotoLines.add(line);
                return "goto " + "line_" +line;
            case "FunctionCallStatement":
                String function_name = switch(node.jjtGetValue().toString()) {
                    case "SIN" -> "my_sin";
                    case "COS" -> "my_cos";
                    case "TAN" -> "my_tan";
                    case "LOG" -> "my_log";
                    case "EXP" -> "my_exp";
                    case "SQRT" -> "my_sqrt";
                    case "CONCAT" -> "my_concat";
                    case "DSTR" -> "my_dstr";
                    case "ISTR" -> "my_istr";
                    case "IDBL" -> "my_idbl";
                    case "SINT" -> "my_sint";
                    case "SDBL" -> "my_sdbl";
                    case "DINT" -> "my_dint";
                    case "ITOS" -> "my_istr";
                    case "STOI" -> "my_sint";
                    case "LENGTH" -> "ARRAY_LENGTH";
                    default -> node.jjtGetValue().toString();
                };

                toReturn = function_name + "(" ;
                for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                    toReturn += generatePCL((SimpleNode) node.jjtGetChild(i));
                    if (i != node.jjtGetNumChildren() - 1) {
                        toReturn += ", ";
                    }
                }
                toReturn += ")";
                return toReturn;
            case "IfStatement":
                return "if (" + generatePCL((SimpleNode) node.jjtGetChild(0)) + ") { " + generatePCL((SimpleNode) node.jjtGetChild(1)) + "; }" + (node.jjtGetNumChildren() > 2 ? " else { " + generatePCL((SimpleNode) node.jjtGetChild(2)) + "; }" : "");
            case "Condition":
                toReturn = "";
                for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                    toReturn += generatePCL((SimpleNode) node.jjtGetChild(i));
                }
                return toReturn;
            case "ForStatement":
                String variable = getChildVariable((SimpleNode) node.jjtGetChild(0));
                return "for (" + generatePCL((SimpleNode) node.jjtGetChild(0)) + "; " + variable + "!=" + generatePCL((SimpleNode) node.jjtGetChild(1)) + "; " + variable + "+=" + generatePCL((SimpleNode) node.jjtGetChild(2)) + ") { ";
            case "NextStatement":
                return "}";
            case "WhileStatement":
                return "while (" + generatePCL((SimpleNode) node.jjtGetChild(0)) + ") { ";
            case "WendStatement":
                return "}";
            case "GosubStatement":
                this.functions.put(Integer.parseInt(generatePCL((SimpleNode) node.jjtGetChild(0))), new ArrayList<String>());
                return "sub_" + generatePCL((SimpleNode) node.jjtGetChild(0)) + "()";
            case "ReturnStatement":
            this.functions.get(this.functionQueue.getFirst()).add("return;");
                this.functionQueue.remove(0);
                return "";
            default:
                return "/* " + node.toString() + " */";
        }
        return null;
    }

    static String getChildVariable(SimpleNode node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i).toString().equals("Variable")) {
                return getVariableNameExtentionString(node);
            }
            else {
                return getChildVariable((SimpleNode) node.jjtGetChild(i));
            }
        }
        return null;
    }

    static String getVariableNameExtentionString(SimpleNode node) {
        if (node.toString().equals("Variable")) {
            char lastChar = node.jjtGetValue().toString().charAt(node.jjtGetValue().toString().length() - 1);
            switch(lastChar) {
                case '%':
                    return node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1) + "_int";
                case '#':
                    return node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1) + "_double";
                case '$':
                    return node.jjtGetValue().toString().substring(0, node.jjtGetValue().toString().length() - 1) + "_string";
            }
        }
        return getVariableNameExtentionString((SimpleNode) node.jjtGetChild(0));
    }

    static VariableType getChildVariableType(SimpleNode node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i).toString().equals("Variable")) {
                SimpleNode child = (SimpleNode) node.jjtGetChild(i);
                char lastChar = child.jjtGetValue().toString().charAt(child.jjtGetValue().toString().length() - 1);
                switch(lastChar) {
                    case '%':
                        return VariableType.INT;
                    case '$':
                        return VariableType.STRING;
                    case '#':
                        return VariableType.DOUBLE;
                }
            }
            else {
                return getChildVariableType((SimpleNode) node.jjtGetChild(i));
            }
        }
        return null;
    }

    String PrintStatement(SimpleNode printStatement) {
        String toReturn = "printf(\"%s\\n\", ";

        toReturn += generatePCL((SimpleNode) printStatement.jjtGetChild(0));

        toReturn += ")";
        return toReturn;
    }

    static String ArrayDeclaration(int size) {
        return "[" + size + "]";
    }
}
