package eu.aston.javajs;

import java.util.ArrayList;
import java.util.List;

import eu.aston.javajs.AstNodes.*;

public class JsParser {
    private Token currentToken;
    private int tokenPosition;
    private final List<Token> tokens;

    public JsParser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenPosition = 0;
        advance();
    }

    // Entry point for parsing
    public ASTNode parse() {
        return parseProgram();
    }

    private void advance() {
        if (tokenPosition < tokens.size()) {
            currentToken = tokens.get(tokenPosition++);
        } else {
            currentToken = new Token(TokenType.EOF, "", 0, 0);
        }
    }

    private boolean matchAdvance(TokenType type, String value) {
        if (currentToken.getType() == type && currentToken.getValue().equals(value)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean match(TokenType type, String value) {
        return currentToken.getType() == type && currentToken.getValue().equals(value);
    }

    private boolean matchPos(int pos, TokenType type, String value) {
        if (pos >= tokens.size()) {
            return false;
        }
        Token token2 = tokens.get(pos);
        return token2.getType() == type && token2.getValue().equals(value);
    }

    private void expect(TokenType type) {
        if (currentToken.getType() != type) {
            throw new SyntaxError("Expected " + type + " but got " + currentToken.getType()
                                          + " at line " + currentToken.getLine()
                                          + ", column " + currentToken.getColumn());
        }
        advance();
    }

    private void expect(TokenType type, String value) {
        if (currentToken.getType() != type || !currentToken.getValue().equals(value)) {
            throw new SyntaxError("Expected " + type + " with value '" + value + "' but got "
                                          + currentToken.getType() + " with value '" + currentToken.getValue() + "'"
                                          + " at line " + currentToken.getLine()
                                          + ", column " + currentToken.getColumn());
        }
        advance();
    }

    // Program = Statement*
    private ASTNode parseProgram() {
        ProgramNode program = new ProgramNode();

        while (currentToken.getType() != TokenType.EOF) {
            program.addStatement(parseStatement());
        }

        return program;
    }

    // Statement = Block | VariableStatement | EmptyStatement | ExpressionStatement
    //           | IfStatement | IterationStatement | ContinueStatement | BreakStatement
    //           | ReturnStatement | LabelledStatement | SwitchStatement
    //           | ThrowStatement | TryStatement | FunctionDeclaration
    private ASTNode parseStatement() {
        switch (currentToken.getType()) {
            case PUNCTUATION:
                if (currentToken.getValue().equals("{")) {
                    return parseBlock();
                } else if (currentToken.getValue().equals(";")) {
                    advance(); // Skip semicolon
                    return new EmptyStatementNode();
                }
                break;

            case KEYWORD:
                switch (currentToken.getValue()) {
                    case "var":
                    case "let":
                    case "const":
                        return parseVariableStatement();
                    case "if":
                        return parseIfStatement();
                    case "do":
                    case "while":
                    case "for":
                        return parseIterationStatement();
                    case "continue":
                        return parseContinueStatement();
                    case "break":
                        return parseBreakStatement();
                    case "return":
                        return parseReturnStatement();
                    case "switch":
                        return parseSwitchStatement();
                    case "throw":
                        return parseThrowStatement();
                    case "try":
                        return parseTryStatement();
                    case "function":
                        return parseFunctionDeclaration();
                }
                break;
        }

        // If no specific statement was recognized, default to expression statement
        return parseExpressionStatement();
    }

    // Block = "{" Statement* "}"
    public BlockNode parseBlock() {
        BlockNode block = new BlockNode();
        expect(TokenType.PUNCTUATION, "{");

        while (!match(TokenType.PUNCTUATION, "}")) {
            block.addStatement(parseStatement());
        }

        expect(TokenType.PUNCTUATION, "}");
        return block;
    }

    private boolean variableAccess() {
        return currentToken.getType()==TokenType.KEYWORD &&
               (currentToken.getValue().equals("var") || currentToken.getValue().equals("let") || currentToken.getValue().equals("const"));
    }

    // VariableStatement = ("var"|"let"|"const") VariableDeclarationList ";"
    private ASTNode parseVariableStatement() {
        if(!variableAccess()) {
            throw new SyntaxError("Expected variable declaration but got " + currentToken.getType()
                    + " at line " + currentToken.getLine()
                    + ", column " + currentToken.getColumn());
        }
        String keyword = currentToken.getValue();
        VariableStatementNode node = new VariableStatementNode(keyword);
        advance();

        // Parse first variable declaration
        node.addDeclaration(parseVariableDeclaration());

        // Parse additional variable declarations separated by commas
        while (matchAdvance(TokenType.PUNCTUATION, ",")) {
            node.addDeclaration(parseVariableDeclaration());
        }

        matchAdvance(TokenType.PUNCTUATION, ";");
        return node;
    }

    // VariableDeclaration = Identifier Initializer?
    private VariableDeclarationNode parseVariableDeclaration() {
        if (currentToken.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxError("Expected identifier but got " + currentToken.getType()
                                          + " at line " + currentToken.getLine()
                                          + ", column " + currentToken.getColumn());
        }

        String identifier = currentToken.getValue();
        advance();

        ASTNode initializer = null;
        if (matchAdvance(TokenType.OPERATOR, "=")) {
            initializer = parseAssignmentExpression();
        }

        return new VariableDeclarationNode(identifier, initializer);
    }

    // ExpressionStatement = Expression ";"
    private ASTNode parseExpressionStatement() {
        ASTNode expression = parseExpression();

        // Optional semicolon
        matchAdvance(TokenType.PUNCTUATION, ";");
        return expression;
    }

    // IfStatement = "if" "(" Expression ")" Statement ("else" Statement)?
    private ASTNode parseIfStatement() {
        expect(TokenType.KEYWORD, "if");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode condition = parseExpression();
        expect(TokenType.PUNCTUATION, ")");

        ASTNode thenStatement = parseStatement();

        ASTNode elseStatement = null;
        if (currentToken.getType() == TokenType.KEYWORD && currentToken.getValue().equals("else")) {
            advance();
            elseStatement = parseStatement();
        }

        return new IfStatementNode(condition, thenStatement, elseStatement);
    }

    // IterationStatement = DoWhileStatement | WhileStatement | ForStatement
    private ASTNode parseIterationStatement() {
        if (currentToken.getType() == TokenType.KEYWORD) {
            if (currentToken.getValue().equals("do")) {
                return parseDoWhileStatement();
            } else if (currentToken.getValue().equals("while")) {
                return parseWhileStatement();
            } else if (currentToken.getValue().equals("for")) {
                return parseForStatement();
            }
        }
        throw new SyntaxError("Expected iteration statement but got " + currentToken.getValue()
                                      + " at line " + currentToken.getLine()
                                      + ", column " + currentToken.getColumn());
    }

    // DoWhileStatement = "do" Statement "while" "(" Expression ")" ";"
    private ASTNode parseDoWhileStatement() {
        expect(TokenType.KEYWORD, "do");
        ASTNode body = parseStatement();
        expect(TokenType.KEYWORD, "while");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode condition = parseExpression();
        expect(TokenType.PUNCTUATION, ")");

        // Optional semicolon
        matchAdvance(TokenType.PUNCTUATION, ";");

        return new DoWhileStatementNode(condition, body);
    }

    // WhileStatement = "while" "(" Expression ")" Statement
    private ASTNode parseWhileStatement() {
        expect(TokenType.KEYWORD, "while");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode condition = parseExpression();
        expect(TokenType.PUNCTUATION, ")");
        ASTNode body = parseStatement();

        return new WhileStatementNode(condition, body);
    }

    // ForStatement = "for" "(" (ExpressionNoIn? | "var" VariableDeclarationListNoIn) ";" Expression? ";" Expression? ")" Statement
    //              | "for" "(" (LeftHandSideExpression | "var" VariableDeclarationNoIn) "in" Expression ")" Statement
    private ASTNode parseForStatement() {
        expect(TokenType.KEYWORD, "for");
        expect(TokenType.PUNCTUATION, "(");

        if(variableAccess() && tokens.get(tokenPosition).getType() == TokenType.IDENTIFIER &&
           matchPos(tokenPosition+1, TokenType.KEYWORD,"in")) {
            // Handle for-in loop
            String access = currentToken.getValue();
            advance();
            String identifier = currentToken.getValue();
            advance();
            expect(TokenType.KEYWORD, "in");
            ASTNode iterable = parseExpression();
            expect(TokenType.PUNCTUATION, ")");
            ASTNode body = parseStatement();
            return new ForInStatementNode(identifier, iterable, body);
        }
        if(variableAccess() && tokens.get(tokenPosition).getType() == TokenType.IDENTIFIER &&
                matchPos(tokenPosition+1, TokenType.KEYWORD,"of")) {
            // Handle for-of loop
            String access = currentToken.getValue();
            advance();
            String identifier = currentToken.getValue();
            advance();
            expect(TokenType.KEYWORD, "of");
            ASTNode iterable = parseExpression();
            expect(TokenType.PUNCTUATION, ")");
            ASTNode body = parseStatement();
            return new ForOfStatementNode(identifier, iterable, body);
        }

        ASTNode init = null;
        ASTNode condition = null;
        ASTNode update = null;

        // Check for var declaration
        if (currentToken.getType() == TokenType.KEYWORD && (currentToken.getValue().equals("var") || currentToken.getValue().equals("let") || currentToken.getValue().equals("const"))) {
            String access = currentToken.getValue();
            advance();
            init = parseVariableDeclaration();
            VariableDeclarationNode variableDeclarationNode = (VariableDeclarationNode) init;
            variableDeclarationNode.setAccess(access);
        } else if (match(TokenType.PUNCTUATION, ";")) {
            // Check for semicolon (empty initialization)
            // No initialization
        } else {
            // Otherwise it's an expression
            init = parseExpression();
        }

        expect(TokenType.PUNCTUATION, ";");

        // Parse condition (optional)
        if (!match(TokenType.PUNCTUATION, ";")) {
            condition = parseExpression();
        }

        expect(TokenType.PUNCTUATION, ";");

        // Parse update (optional)
        if (!match(TokenType.PUNCTUATION, ")")) {
            update = parseExpression();
        }

        expect(TokenType.PUNCTUATION, ")");
        ASTNode body = parseStatement();
        return new ForStatementNode(init, condition, update, body);
    }

    // ContinueStatement = "continue" Identifier? ";"
    private ASTNode parseContinueStatement() {
        expect(TokenType.KEYWORD, "continue");

        String label = null;
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            label = currentToken.getValue();
            advance();
        }

        matchAdvance(TokenType.PUNCTUATION, ";");
        return new ContinueStatementNode(label);
    }

    // BreakStatement = "break" Identifier? ";"
    private ASTNode parseBreakStatement() {
        expect(TokenType.KEYWORD, "break");

        String label = null;
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            label = currentToken.getValue();
            advance();
        }

        // Optional semicolon
        matchAdvance(TokenType.PUNCTUATION, ";");


        return new BreakStatementNode(label);
    }

    // ReturnStatement = "return" Expression? ";"
    private ASTNode parseReturnStatement() {
        expect(TokenType.KEYWORD, "return");

        ASTNode expression = null;
        if (!(currentToken.getType() == TokenType.PUNCTUATION &&
                (currentToken.getValue().equals(";") || currentToken.getValue().equals("}")))) {
            expression = parseExpression();
        }

        // Optional semicolon
        matchAdvance(TokenType.PUNCTUATION, ";");


        return new ReturnStatementNode(expression);
    }

    // Expression parsing would implement precedence climbing for operators
    private ASTNode parseExpression() {
        return parseAssignmentExpression();
    }

    // AssignmentExpression = ConditionalExpression | LeftHandSideExpression AssignmentOperator AssignmentExpression
    private ASTNode parseAssignmentExpression() {
        ASTNode left = parseConditionalExpression();

        if (currentToken.getType() == TokenType.OPERATOR) {
            String operator = currentToken.getValue();
            if (operator.equals("=") || operator.equals("+=") || operator.equals("-=") || operator.equals("*=") || operator.equals("/=") || operator.equals("%=")){
                advance();
                ASTNode right = parseAssignmentExpression();
                return new AssignmentExpressionNode(left, operator, right);
            }
        }
        return left;
    }

    // ConditionalExpression = LogicalORExpression ("?" AssignmentExpression ":" AssignmentExpression)? || "??" AssignmentExpression
    private ASTNode parseConditionalExpression() {
        ASTNode condition = parseLogicalORExpression();

        if(matchAdvance(TokenType.OPERATOR, "??")) {
            ASTNode right = parseAssignmentExpression();
            return new NullCoalescingNode(condition, right);
        }

        if (matchAdvance(TokenType.PUNCTUATION, "?")) {
            ASTNode trueExpression = parseAssignmentExpression();
            expect(TokenType.PUNCTUATION, ":");
            ASTNode falseExpression = parseAssignmentExpression();
            return new ConditionalExpressionNode(condition, trueExpression, falseExpression);
        }

        return condition;
    }

    // LogicalORExpression = LogicalANDExpression ("||" LogicalANDExpression)*
    private ASTNode parseLogicalORExpression() {
        ASTNode left = parseLogicalANDExpression();

        while (matchAdvance(TokenType.OPERATOR,"||")) {
            ASTNode right = parseLogicalANDExpression();
            left = new BinaryExpressionNode(left, "||", right);
        }

        return left;
    }

    // LogicalANDExpression = EqualityExpression ("&&" EqualityExpression)*
    private ASTNode parseLogicalANDExpression() {
        ASTNode left = parseEqualityExpression();

        while (matchAdvance(TokenType.OPERATOR,"&&")) {
            ASTNode right = parseEqualityExpression();
            left = new BinaryExpressionNode(left, "&&", right);
        }

        return left;
    }

    // EqualityExpression = RelationalExpression (("=="|"!="|"==="|"!==") RelationalExpression)*
    private ASTNode parseEqualityExpression() {
        ASTNode left = parseRelationalExpression();

        while (currentToken.getType() == TokenType.OPERATOR && 
              (currentToken.getValue().equals("==") || currentToken.getValue().equals("!=") ||
               currentToken.getValue().equals("===") || currentToken.getValue().equals("!=="))) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseRelationalExpression();
            left = new BinaryExpressionNode(left, operator, right);
        }

        return left;
    }

    // RelationalExpression = AdditiveExpression (("<"|">"|"<="|">=") AdditiveExpression )*
    private ASTNode parseRelationalExpression() {
        ASTNode left = parseAdditiveExpression();

        while (currentToken.getType() == TokenType.OPERATOR && 
              (currentToken.getValue().equals("<") || currentToken.getValue().equals(">") ||
               currentToken.getValue().equals("<=") || currentToken.getValue().equals(">=")) ||
              (currentToken.getType() == TokenType.KEYWORD)) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseAdditiveExpression();
            left = new BinaryExpressionNode(left, operator, right);
        }

        return left;
    }

    // AdditiveExpression = MultiplicativeExpression (("+"|"-") MultiplicativeExpression)*
     private ASTNode parseAdditiveExpression() {
        ASTNode left = parseMultiplicativeExpression();

        while (currentToken.getType() == TokenType.OPERATOR &&
              (currentToken.getValue().equals("+") || currentToken.getValue().equals("-"))) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseMultiplicativeExpression();
            left = new BinaryExpressionNode(left, operator, right);
        }
        if(left instanceof BinaryExpressionNode binaryExpressionNode){
            List<ASTNode> items = checkStringConcat(binaryExpressionNode);
            if(items!=null){
                left = new StringConcatExpressionNode(items);
            }
        }
        return left;
    }

    // MultiplicativeExpression = UnaryExpression (("*"|"/"|"%") UnaryExpression)*
    private ASTNode parseMultiplicativeExpression() {
        ASTNode left = parseUnaryExpression();

        while (currentToken.getType() == TokenType.OPERATOR && 
              (currentToken.getValue().equals("*") || currentToken.getValue().equals("/") ||
               currentToken.getValue().equals("%")) || currentToken.getValue().equals("**")) {
            String operator = currentToken.getValue();
            advance();
            ASTNode right = parseUnaryExpression();
            left = new BinaryExpressionNode(left, operator, right);
        }

        return left;
    }

    // UnaryExpression = PostfixExpression | (("typeof"|"++"|"--"|"+"|"-"|"!") UnaryExpression)
    private ASTNode parseUnaryExpression() {
        if(matchAdvance(TokenType.KEYWORD, "typeof")){
            ASTNode operand = parseUnaryExpression();
            return new UnaryExpressionNode("typeof", operand);
        }
        if(matchAdvance(TokenType.OPERATOR, "++")) {
                ASTNode operand = parseUnaryExpression();
                return new UnaryExpressionNode("var++", operand);
        }
        if(matchAdvance(TokenType.OPERATOR, "--")) {
            ASTNode operand = parseUnaryExpression();
            return new UnaryExpressionNode("var--", operand);
        }
        if(currentToken.getType()==TokenType.OPERATOR){
            String value = currentToken.getValue();
            if (value.equals("+") || value.equals("-") || value.equals("!")) {
                advance();
                ASTNode operand = parseUnaryExpression();
                return new UnaryExpressionNode(value, operand);
            }
        }
        return parsePostfixExpression();
    }
    
    // PostfixExpression = LeftHandSideExpression ("++" | "--")?
    private ASTNode parsePostfixExpression() {
        ASTNode expression = parseCallExpression();
        
        if (matchAdvance(TokenType.OPERATOR, "++")) {
            return new UnaryExpressionNode("++var", expression);
        }
        if (matchAdvance(TokenType.OPERATOR, "--")) {
            return new UnaryExpressionNode("--var", expression);
        }

        return expression;
    }

    // CallExpression = MemberExpression Arguments | CallExpression Arguments 
    //                | CallExpression "." Identifier | CallExpression "[" Expression "]"
    private ASTNode parseCallExpression() {
        ASTNode expression = parseMemberExpression();
        
        // Parse any call, property access, or indexed access that follows
        while (true) {
            if (match(TokenType.PUNCTUATION, "(")) {
                // Function call
                List<ASTNode> arguments = parseArguments();
                expression = new CallExpressionNode(expression, arguments);
                continue;
            }
            ASTNode next = parseNextMember(expression);
            if(next != null) {
                expression = next;
            } else {
                break;
            }
        }
        return expression;
    }

    private ASTNode parseNextMember(ASTNode parent){
        if(matchAdvance(TokenType.OPERATOR,"?.")) {
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                String property = currentToken.getValue();
                advance();
                return new MemberExpressionNode(new OptionalNode(parent), property);
            }
            if (matchAdvance(TokenType.PUNCTUATION,"[")) {
                // Indexed access
                ASTNode property = parseExpression();
                expect(TokenType.PUNCTUATION, "]");
                return new OptionalNode(new MemberExpressionNode(parent, property));
            }
            if (match(TokenType.PUNCTUATION, "(")) {
                // Function call
                List<ASTNode> arguments = parseArguments();
                return new CallExpressionNode(new OptionalNode(parent), arguments);
            }
            throw new SyntaxError("Expected identifier after '?.' but got " + currentToken.getType()
                                          + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
        } else if (matchAdvance(TokenType.PUNCTUATION,".")) {
            // Property access
            if (currentToken.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxError("Expected identifier after '.' but got " + currentToken.getType()
                                              + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
            }
            String property = currentToken.getValue();
            advance();
            return new MemberExpressionNode(parent, property);
        } else if (matchAdvance(TokenType.PUNCTUATION,"[")) {
            // Indexed access
            ASTNode property = parseExpression();
            expect(TokenType.PUNCTUATION, "]");
            return new MemberExpressionNode(parent, property);
        }
        return null;
    }
    
    // MemberExpression = PrimaryExpression | MemberExpression "." Identifier | MemberExpression "." Identifier? | MemberExpression "[" Expression "]"
    private ASTNode parseMemberExpression() {
        // Start with primary expression
        ASTNode expression = parsePrimaryExpression();
        
        // Parse any property access or indexed access that follows
        while (true) {
            ASTNode next = parseNextMember(expression);
            if(next != null) {
                expression = next;
            } else {
                break;
            }
        }
        return expression;
    }
    
    // Arguments = "(" (AssignmentExpression ("," AssignmentExpression)*)? ")"
    private List<ASTNode> parseArguments() {
        expect(TokenType.PUNCTUATION, "(");
        
        List<ASTNode> arguments = new ArrayList<>();
        
        // Parse arguments if present
        if (!match(TokenType.PUNCTUATION, ")")) {
            // Parse first argument
            arguments.add(parseAssignmentExpression());
            
            // Parse additional arguments separated by commas
            while (matchAdvance(TokenType.PUNCTUATION, ",")) {
                arguments.add(parseAssignmentExpression());
            }
        }
        
        expect(TokenType.PUNCTUATION, ")");
        return arguments;
    }

    // PrimaryExpression = arrow | this | Identifier | Literal | ArrayLiteral | ObjectLiteral | "(" Expression ")"
    private ASTNode parsePrimaryExpression() {

        if (match(TokenType.PUNCTUATION, "(")) {
            //possible arrow function
            int pos = tokenPosition;
            // check parameters
            while (tokens.get(pos).getType()==TokenType.IDENTIFIER){
                pos++;
                if (!matchPos(pos, TokenType.PUNCTUATION,",")) {
                    break;
                }
                pos++;
            }
            // check arrow
            if (matchPos(pos, TokenType.PUNCTUATION,")") && matchPos(pos+1, TokenType.OPERATOR,"=>")) {
                // Handle arrow function
                return parseArrowFunction();
            }
        }

        if(currentToken.getType()==TokenType.IDENTIFIER && matchPos(tokenPosition, TokenType.OPERATOR,"=>")){
            // Handle single param arrow function
            return parseArrowFunction();
        }

        switch (currentToken.getType()) {
            case KEYWORD:
                if (currentToken.getValue().equals("this")) {
                    advance();
                    return new ThisNode();
                } else if (currentToken.getValue().equals("function")) {
                    // Function expression (anonymous function)
                    return parseFunctionExpression();
                }
                break;

            case IDENTIFIER:
                String identifier = currentToken.getValue();
                advance();
                return new IdentifierNode(identifier);

            case NUMBER:
            case STRING:
            case BOOLEAN:
            case NULL:
            case UNDEFINED:
            case STRING_TEMPLATE:
                return parseLiteral();

            case PUNCTUATION:
                if (currentToken.getValue().equals("[")) {
                    return parseArrayLiteral();
                } else if (currentToken.getValue().equals("{")) {
                    return parseObjectLiteral();
                } else if (currentToken.getValue().equals("(")) {
                    advance();
                    ASTNode expression = parseExpression();
                    expect(TokenType.PUNCTUATION, ")");
                    return expression;
                }
                break;
        }

        throw new SyntaxError("Unexpected token " + currentToken.getValue()
                                      + " at line " + currentToken.getLine()
                                      + ", column " + currentToken.getColumn());
    }

    // Literal = NullLiteral | BooleanLiteral | NumericLiteral | StringLiteral | StringTemplateLiteral
    private ASTNode parseLiteral() {
        switch (currentToken.getType()) {
            case NULL:
                advance();
                return new ConstantNode(null);

            case BOOLEAN:
                String value = currentToken.getValue();
                advance();
                return new ConstantNode(value.equals("true"));

            case NUMBER:
                String numStr = currentToken.getValue();
                advance();
                return new ConstantNode(JsTypes.toNumber(numStr));

            case STRING:
                String str = currentToken.getValue();
                advance();
                return new ConstantNode(JsTypes.unescapeString(str));

            case STRING_TEMPLATE:
                return parseStringTemplate();

            case UNDEFINED:
                advance();
                return new ConstantNode(Undefined.INSTANCE);

            default:
                throw new SyntaxError("Expected literal but got " + currentToken.getType()
                                              + " at line " + currentToken.getLine()
                                              + ", column " + currentToken.getColumn());
        }
    }

    // SwitchStatement = "switch" "(" Expression ")" CaseBlock
    private ASTNode parseSwitchStatement() {
        expect(TokenType.KEYWORD, "switch");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode discriminant = parseExpression();
        expect(TokenType.PUNCTUATION, ")");

        SwitchStatementNode switchNode = new SwitchStatementNode(discriminant);

        // CaseBlock = "{" CaseClauses? DefaultClause? CaseClauses? "}"
        expect(TokenType.PUNCTUATION, "{");

        // Parse case clauses until we find default or closing brace
        while (!match(TokenType.PUNCTUATION, "}") &&
                !match(TokenType.KEYWORD, "default")) {
            switchNode.addCase(parseCaseClause());
        }

        // Parse default clause if present
        if (match(TokenType.KEYWORD, "default")) {
            switchNode.setDefaultCase(parseDefaultClause());
        }

        // Parse any remaining case clauses
        while (!match(TokenType.PUNCTUATION, "}")) {
            switchNode.addCase(parseCaseClause());
        }

        expect(TokenType.PUNCTUATION, "}");
        return switchNode;
    }

    // CaseClause = "case" Expression ":" StatementList?
    private ASTNode parseCaseClause() {
        expect(TokenType.KEYWORD, "case");
        ASTNode test = parseExpression();
        expect(TokenType.PUNCTUATION, ":");

        List<ASTNode> consequent = new ArrayList<>();

        // Parse statements until next case, default, or end of switch
        while (!match(TokenType.KEYWORD, "case")
                && !match(TokenType.KEYWORD, "default")
                && !match(TokenType.PUNCTUATION, "}")) {
            consequent.add(parseStatement());
        }

        return new SwitchCaseNode(test, consequent);
    }

    // DefaultClause = "default" ":" StatementList?
    private ASTNode parseDefaultClause() {
        expect(TokenType.KEYWORD, "default");
        expect(TokenType.PUNCTUATION, ":");

        List<ASTNode> consequent = new ArrayList<>();

        // Parse statements until next case or end of switch
        while (!(currentToken.getType() == TokenType.KEYWORD && currentToken.getValue().equals("case")) &&
                !(currentToken.getType() == TokenType.PUNCTUATION && currentToken.getValue().equals("}"))) {
            consequent.add(parseStatement());
        }

        return new SwitchDefaultNode(consequent);
    }

    // ThrowStatement = "throw" Expression ";"
    private ASTNode parseThrowStatement() {
        expect(TokenType.KEYWORD, "throw");
        ASTNode expression = parseExpression();

        // Optional semicolon
        matchAdvance(TokenType.PUNCTUATION, ";");

        return new ThrowStatementNode(expression);
    }

    // TryStatement = "try" Block Catch Finally | "try" Block (Catch | Finally)
    private ASTNode parseTryStatement() {
        expect(TokenType.KEYWORD, "try");
        ASTNode block = parseBlock();

        CatchClauseNode catchBlock = null;
        ASTNode finallyBlock = null;

        // Check for catch block
        if (match(TokenType.KEYWORD, "catch")) {
            expect(TokenType.KEYWORD, "catch");
            expect(TokenType.PUNCTUATION, "(");

            if (currentToken.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxError("Expected identifier in catch clause but got " + currentToken.getType()
                                              + " at line " + currentToken.getLine()
                                              + ", column " + currentToken.getColumn());
            }

            String param = currentToken.getValue();
            advance();

            expect(TokenType.PUNCTUATION, ")");
            ASTNode catchBody = parseBlock();
            catchBlock = new CatchClauseNode(param, catchBody);
        }

        // Check for finally block
        if (match(TokenType.KEYWORD, "finally")) {
            expect(TokenType.KEYWORD, "finally");
            finallyBlock = parseBlock();
        }

        // At least one of catch or finally must be present
        if (catchBlock == null && finallyBlock == null) {
            throw new SyntaxError("Expected catch or finally clause after try at line "
                                          + currentToken.getLine() + ", column " + currentToken.getColumn());
        }

        return new TryStatementNode(block, catchBlock, finallyBlock);
    }

    // FunctionDeclaration = "function" Identifier "(" FormalParameterList? ")" Block
    private ASTNode parseFunctionDeclaration() {
        return parseFunctionExpression();
    }

    // ArrayLiteral = "[" ElementList? "]"
    private ASTNode parseArrayLiteral() {
        expect(TokenType.PUNCTUATION, "[");

        List<ASTNode> elements = new ArrayList<>();

        // Parse elements if present
        if (!match(TokenType.PUNCTUATION, "]")) {
            // Handle first element (which might be elided)
            if (match(TokenType.PUNCTUATION, ",")) {
                elements.add(null); // Elided element
            } else {
                elements.add(parseAssignmentExpression());
            }

            // Parse additional elements separated by commas
            while (matchAdvance(TokenType.PUNCTUATION, ",")) {
                if (match(TokenType.PUNCTUATION, "]")) {
                    break; // Trailing comma
                } else if (match(TokenType.PUNCTUATION, ",")) {
                    elements.add(null); // Elided element
                } else {
                    elements.add(parseAssignmentExpression());
                }
            }
        }

        expect(TokenType.PUNCTUATION, "]");
        return new ArrayLiteralNode(elements);
    }

    // ObjectLiteral = "{" PropertyNameAndValueList? "}"
    private ASTNode parseObjectLiteral() {
        expect(TokenType.PUNCTUATION, "{");

        List<PropertyNode> properties = new ArrayList<>();

        // Parse properties if present
        if (!match(TokenType.PUNCTUATION, "}")) {
            // Parse first property
            properties.add(parsePropertyAssignment());

            // Parse additional properties separated by commas
            while (matchAdvance(TokenType.PUNCTUATION, ",")) {
                if (match(TokenType.PUNCTUATION, "}")) {
                    break; // Trailing comma
                }
                properties.add(parsePropertyAssignment());
            }
        }

        expect(TokenType.PUNCTUATION, "}");
        return new ObjectLiteralNode(properties);
    }

    // PropertyAssignment = PropertyName ":" AssignmentExpression
    private PropertyNode parsePropertyAssignment() {
        String key = parsePropertyName();
        expect(TokenType.PUNCTUATION, ":");
        ASTNode value = parseAssignmentExpression();

        return new PropertyNode(key, value);
    }

    // PropertyName = Identifier | StringLiteral | NumericLiteral
    private String parsePropertyName() {
        if (currentToken.getType() == TokenType.IDENTIFIER || currentToken.getType() == TokenType.STRING || currentToken.getType() == TokenType.NUMBER) {
            String name = currentToken.getValue();
            advance();
            return name;
        } else {
            throw new SyntaxError("Expected property name but got " + currentToken.getType()
                                          + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
        }
    }

    // FunctionExpression = "function" Identifier? "(" FormalParameterList? ")" Block
    private ASTNode parseFunctionExpression() {
        expect(TokenType.KEYWORD, "function");

        String name = null;
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            name = currentToken.getValue();
            advance();
        }

        expect(TokenType.PUNCTUATION, "(");

        List<String> params = new ArrayList<>();
        while(currentToken.getType() == TokenType.IDENTIFIER) {
            String paramName = currentToken.getValue();
            params.add(paramName);
            advance();
            if (match(TokenType.PUNCTUATION, ",")) {
                advance(); // Skip comma
            } else {
                break; // No more parameters
            }
        }
        expect(TokenType.PUNCTUATION, ")");
        ASTNode body = parseBlock();

        return new FunctionDeclarationNode(name, params, body, false);
    }

    // Parse arrow expression - (param1, param2, ...) => expression | block
    private ASTNode parseArrowFunction() {
        List<String> params = new ArrayList<>();

        // Check for parameter syntax
        if (matchAdvance(TokenType.PUNCTUATION,"(")) {

            while(currentToken.getType() == TokenType.IDENTIFIER) {
                params.add(currentToken.getValue());
                advance();
                if (match(TokenType.PUNCTUATION, ",")) {
                    advance(); // Skip comma
                } else {
                    break; // No more parameters
                }
            }
            // Check for closing parenthesis
            expect(TokenType.PUNCTUATION, ")");
        } else if (currentToken.getType() == TokenType.IDENTIFIER) {
            // Single parameter without parentheses: x => ...
            params.add(currentToken.getValue());
            advance();
        } else {
            throw new SyntaxError("Expected parameter list for lambda function but got " + currentToken.getType()
                                  + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
        }

        expect(TokenType.OPERATOR, "=>");
        ASTNode body;

        // Parse the function body
        if (match(TokenType.PUNCTUATION, "{")) {
            // Block body
            body = parseBlock();
        } else {
            // Expression body with implicit return
            body = parseAssignmentExpression();
            body = new ReturnStatementNode(body);
        }
        
        return new FunctionDeclarationNode(null, params, body, true);
    }

    public List<ASTNode> checkStringConcat(BinaryExpressionNode node){
        List<ASTNode> items = new ArrayList<>();
        if(checkStringConcat(items, node)){
            for(ASTNode n: items){
                if(n instanceof ConstantNode constantNode && constantNode.value instanceof String){
                    return items.reversed();
                }
            }
            return null;
        }
        return null;
    }

    //sring tempalate
    public ASTNode parseStringTemplate(){
        List<ASTNode> items = new ArrayList<>();
        if(currentToken.getSubtokens()!=null){
            for(Token subtoken : currentToken.getSubtokens()){
                String tokenStr = subtoken.getValue();
                if(tokenStr.isEmpty()) continue;
                if(tokenStr.startsWith("${")){
                    JsScriptLexer lexer = new JsScriptLexer(tokenStr.substring(1), subtoken.getLine(), subtoken.getColumn());
                    JsParser parser = new JsParser(lexer.tokenize());
                    BlockNode block = parser.parseBlock();
                    if(block.statements.size()==1 && block.statements.getFirst() instanceof ExecuteWithReturn){
                        items.add(block.statements.getFirst());
                    } else {
                        throw new RuntimeException("string template invalid expression "+subtoken);
                    }
                } else {
                    items.add(new ConstantNode(tokenStr));
                }
            }
        }
        advance();
        if(items.isEmpty()){
            return new ConstantNode("");
        } else if(items.size()==1){
            return items.getFirst();
        }
        return new StringConcatExpressionNode(items);
    }

    public static boolean checkStringConcat(List<ASTNode> items, BinaryExpressionNode node){
        if(!node.operator.equals("+")) return false;
        if(node.right instanceof StringConcatExpressionNode stringConcatExpressionNode){
            items.addAll(stringConcatExpressionNode.items);
        }
        if(node.right instanceof ExecuteWithReturn){
            items.add(node.right);
        }
        if(node.left instanceof BinaryExpressionNode binaryExpressionNode){
            return checkStringConcat(items, binaryExpressionNode);
        }
        if(node.left instanceof ExecuteWithReturn){
            items.add(node.left);
            return true;
        }
        return false;
    }

    // Custom error class for syntax errors
    public static class SyntaxError extends RuntimeException {
        public SyntaxError(String message) {
            super(message);
        }
    }
}