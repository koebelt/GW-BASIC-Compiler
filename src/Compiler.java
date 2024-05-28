

public class Compiler {
    public void compile(GWBASICParser parser) {
        // print the Tokens type

        Token token;
        do {
            token = GWBASICParser.getNextToken();
            System.out.println(GWBASICParserConstants.tokenImage[token.kind] + " " + token.image);
        } while (token.kind != GWBASICParserConstants.EOF);
    }
}
