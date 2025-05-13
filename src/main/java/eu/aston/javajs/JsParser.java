package eu.aston.javajs;

import java.util.ArrayList;
import java.util.List;

import eu.aston.javajs.AstNodes.ASTNode;
import eu.aston.javajs.AstNodes.ArrayLiteralNode;
import eu.aston.javajs.AstNodes.AssignmentExpressionNode;
import eu.aston.javajs.AstNodes.BinaryExpressionNode;
import eu.aston.javajs.AstNodes.BlockNode;
import eu.aston.javajs.AstNodes.BreakStatementNode;
import eu.aston.javajs.AstNodes.CallExpressionNode;
import eu.aston.javajs.AstNodes.CatchClauseNode;
import eu.aston.javajs.AstNodes.ConditionalExpressionNode;
import eu.aston.javajs.AstNodes.ConstantNode;
import eu.aston.javajs.AstNodes.ContinueStatementNode;
import eu.aston.javajs.AstNodes.DestructuringArrayNode;
import eu.aston.javajs.AstNodes.DestructuringObjectNode;
import eu.aston.javajs.AstNodes.DoWhileStatementNode;
import eu.aston.javajs.AstNodes.EmptyStatementNode;
import eu.aston.javajs.AstNodes.ExecuteWithReturn;
import eu.aston.javajs.AstNodes.ForInStatementNode;
import eu.aston.javajs.AstNodes.ForOfStatementNode;
import eu.aston.javajs.AstNodes.ForStatementNode;
import eu.aston.javajs.AstNodes.FunctionDeclarationNode;
import eu.aston.javajs.AstNodes.IdentifierNode;
import eu.aston.javajs.AstNodes.IfStatementNode;
import eu.aston.javajs.AstNodes.MemberExpressionNode;
import eu.aston.javajs.AstNodes.ObjectLiteralNode;
import eu.aston.javajs.AstNodes.OptionalNode;
import eu.aston.javajs.AstNodes.ProgramNode;
import eu.aston.javajs.AstNodes.PropertyNode;
import eu.aston.javajs.AstNodes.ReturnStatementNode;
import eu.aston.javajs.AstNodes.StringConcatExpressionNode;
import eu.aston.javajs.AstNodes.SwitchCaseNode;
import eu.aston.javajs.AstNodes.SwitchDefaultNode;
import eu.aston.javajs.AstNodes.SwitchStatementNode;
import eu.aston.javajs.AstNodes.ThrowStatementNode;
import eu.aston.javajs.AstNodes.TryStatementNode;
import eu.aston.javajs.AstNodes.UnaryExpressionNode;
import eu.aston.javajs.AstNodes.VariableDeclarationNode;
import eu.aston.javajs.AstNodes.VariableStatementNode;
import eu.aston.javajs.AstNodes.WhileStatementNode;
import eu.aston.javajs.types.JsTypes;
import eu.aston.javajs.types.Undefined;

public class JsParser {
    private Token currentToken;
    private int tokenPosition;
    private final List<Token> tokens;
    private final VariablesAnalyzer variablesAnalyzer;

    public JsParser(List<Token> tokens) {
        this.tokens = tokens;
        this.tokenPosition = 0;
        this.variablesAnalyzer = new VariablesAnalyzer();
        advance();
    }

    public JsParser(List<Token> tokens, VariablesAnalyzer variablesAnalyzer) {
        this.tokens = tokens;
        this.tokenPosition = 0;
        this.variablesAnalyzer = variablesAnalyzer;
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

    private void expect(TokenType type, String value) {
        if (currentToken.getType() != type || !currentToken.getValue().equals(value)) {
            throw new SyntaxError("Expected " + type + " with value '" + value + "' but got " + currentToken.getType() +
                                          " with value '" + currentToken.getValue() + "'" + " at line " +
                                          currentToken.getLine() + ", column " + currentToken.getColumn());
        }
        advance();
    }

    private void expectEndStatement() {
        if (!(matchAdvance(TokenType.PUNCTUATION, ";") || match(TokenType.PUNCTUATION, "}") ||
                tokenPosition >= tokens.size() || currentToken.getLine() < tokens.get(tokenPosition).getLine())) {
            throw new SyntaxError("Expected end statement but got " + currentToken.getType() + " with value '" +
                                          currentToken.getValue() + "'" + " at line " + currentToken.getLine() +
                                          ", column " + currentToken.getColumn());
        }
    }

    // Program = Statement*
    private ASTNode parseProgram() {
        BlockNode blockNode = new BlockNode();
        try {
            while (currentToken.getType() != TokenType.EOF) {
                blockNode.addStatement(parseStatement());
            }
        } catch (SyntaxError e) {
            throw e;
        } catch (Exception e) {
            throw new SyntaxError(e.getMessage() + " " + " at line " + currentToken.getLine() + ", column " +
                                          currentToken.getColumn());
        }
        variablesAnalyzer.pairAll();
        return new ProgramNode(blockNode, variablesAnalyzer.stackDef());
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
                } else if (currentToken.getValue().equals("[")) {
                    ASTNode node = parseDestructingArray(null);
                    expectEndStatement();
                    return node;
                } else if (currentToken.getValue().equals("(") && matchPos(tokenPosition, TokenType.PUNCTUATION, "{")) {
                    matchAdvance(TokenType.PUNCTUATION, "(");
                    ASTNode node = parseDestructingObject(null);
                    matchAdvance(TokenType.PUNCTUATION, ")");
                    expectEndStatement();
                    return node;
                }
                break;

            case KEYWORD:
                switch (currentToken.getValue()) {
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
        variablesAnalyzer.startBlock();
        while (!match(TokenType.PUNCTUATION, "}")) {
            block.addStatement(parseStatement());
        }
        expect(TokenType.PUNCTUATION, "}");
        variablesAnalyzer.endBlock();
        return block;
    }

    private boolean variableAccess() {
        return currentToken.getType() == TokenType.KEYWORD &&
                (currentToken.getValue().equals("let") || currentToken.getValue().equals("const"));
    }

    // VariableStatement = ("let"|"const") VariableDeclarationList ";"
    private ASTNode parseVariableStatement() {
        if (!variableAccess()) {
            throw new SyntaxError("Expected variable declaration but got " + currentToken.getType() + " at line " +
                                          currentToken.getLine() + ", column " + currentToken.getColumn());
        }
        String access = currentToken.getValue();
        VariableStatementNode node = new VariableStatementNode();
        advance();

        if (match(TokenType.PUNCTUATION, "[")) {
            //destructing array
            return parseDestructingArray(access);
        }
        if (match(TokenType.PUNCTUATION, "{")) {
            //destructing object
            return parseDestructingObject(access);
        }

        // Parse first variable declaration
        node.addDeclaration(parseVariableDeclaration(access));

        // Parse additional variable declarations separated by commas
        while (matchAdvance(TokenType.PUNCTUATION, ",")) {
            node.addDeclaration(parseVariableDeclaration(access));
        }

        expectEndStatement();
        if (node.declarations.size() == 1) {
            return node.declarations.getFirst();
        }
        return node;
    }

    // VariableDeclaration = Identifier Initializer?
    private VariableDeclarationNode parseVariableDeclaration(String access) {
        if (currentToken.getType() != TokenType.IDENTIFIER) {
            throw new SyntaxError(
                    "Expected identifier but got " + currentToken.getType() + " at line " + currentToken.getLine() +
                            ", column " + currentToken.getColumn());
        }

        String identifier = currentToken.getValue();
        advance();

        ASTNode initializer = null;
        if (matchAdvance(TokenType.OPERATOR, "=")) {
            initializer = parseAssignmentExpression();
        } else if (access.equals("const")) {
            throw new SyntaxError(
                    "Missing initializer in const declaration at line " + currentToken.getLine() + ", column " +
                            currentToken.getColumn());
        }

        return variablesAnalyzer.var(
                new VariableDeclarationNode(access, identifier, initializer, currentToken.tokenPos()));
    }

    private ASTNode parseDestructingArray(String access) {
        List<VariableDeclarationNode> variables = new ArrayList<>();
        VariableDeclarationNode restVariable = null;

        expect(TokenType.PUNCTUATION, "[");
        while (true) {
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                variables.add(variablesAnalyzer.var(
                        new VariableDeclarationNode(access, currentToken.getValue(), currentToken.tokenPos())));
                advance();
                if (matchAdvance(TokenType.PUNCTUATION, ",")) {
                    continue;
                }
                if (matchAdvance(TokenType.PUNCTUATION, "]")) {
                    break;
                }
                throw new SyntaxError(
                        "Expected ] but got " + currentToken.getType() + " with value '" + currentToken.getValue() +
                                "'" + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
            }
            if (currentToken.getType() == TokenType.REST_IDENTIFIER) {
                String identifier = currentToken.getValue().substring(3);
                restVariable = variablesAnalyzer.var(
                        new VariableDeclarationNode(access, identifier, currentToken.tokenPos()));
                advance();
                expect(TokenType.PUNCTUATION, "]");
                break;
            }
            if (matchAdvance(TokenType.PUNCTUATION, ",")) {
                variables.add(null);
                continue;
            }
            throw new SyntaxError(
                    "Expected ] but got " + currentToken.getType() + " with value '" + currentToken.getValue() + "'" +
                            " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
        }
        expect(TokenType.OPERATOR, "=");
        ASTNode right = parseAssignmentExpression();
        return new DestructuringArrayNode(variables, restVariable, right);
    }

    private ASTNode parseDestructingObject(String access) {
        List<VariableDeclarationNode> variables = new ArrayList<>();
        VariableDeclarationNode restVariable = null;

        expect(TokenType.PUNCTUATION, "{");
        while (true) {
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                variables.add(variablesAnalyzer.var(
                        new VariableDeclarationNode(access, currentToken.getValue(), currentToken.tokenPos())));
                advance();
                if (matchAdvance(TokenType.PUNCTUATION, ",")) {
                    continue;
                }
                if (matchAdvance(TokenType.PUNCTUATION, "}")) {
                    break;
                }
                throw new SyntaxError(
                        "Expected } but got " + currentToken.getType() + " with value '" + currentToken.getValue() +
                                "'" + " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
            }
            if (currentToken.getType() == TokenType.REST_IDENTIFIER) {
                String identifier = currentToken.getValue().substring(3);
                restVariable = variablesAnalyzer.var(
                        new VariableDeclarationNode(access, identifier, currentToken.tokenPos()));
                advance();
                expect(TokenType.PUNCTUATION, "}");
                break;
            }
            throw new SyntaxError(
                    "Expected } but got " + currentToken.getType() + " with value '" + currentToken.getValue() + "'" +
                            " at line " + currentToken.getLine() + ", column " + currentToken.getColumn());
        }
        expect(TokenType.OPERATOR, "=");
        ASTNode right = parseAssignmentExpression();
        return new DestructuringObjectNode(variables, restVariable, right);
    }

    // ExpressionStatement = Expression ";"
    private ASTNode parseExpressionStatement() {
        ASTNode expression = parseExpression();

        expectEndStatement();
        return expression;
    }

    // IfStatement = "if" "(" Expression ")" Statement ("else" Statement)|("else" "if" "(" Expression ")" Statement)
    private ASTNode parseIfStatement() {
        expect(TokenType.KEYWORD, "if");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode condition = parseExpression();
        expect(TokenType.PUNCTUATION, ")");

        ASTNode thenStatement = parseStatement();
        if (thenStatement instanceof VariableDeclarationNode || thenStatement instanceof VariableStatementNode) {
            throw new SyntaxError("Variable declaration not allowed directly in if statement at line " + 
                currentToken.getLine() + ", column " + currentToken.getColumn());
        }

        ASTNode elseStatement = null;
        if (currentToken.getType() == TokenType.KEYWORD && currentToken.getValue().equals("else")) {
            advance();
            // Check if this is an else-if
            if (currentToken.getType() == TokenType.KEYWORD && currentToken.getValue().equals("if")) {
                // Recursively parse the else-if as a new if statement
                elseStatement = parseIfStatement();
            } else {
                // Regular else clause
                elseStatement = parseStatement();
                if (elseStatement instanceof VariableDeclarationNode || elseStatement instanceof VariableStatementNode) {
                    throw new SyntaxError("Variable declaration not allowed directly in else statement at line " + 
                        currentToken.getLine() + ", column " + currentToken.getColumn());
                }
            }
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
        throw new SyntaxError("Expected iteration statement but got " + currentToken.getValue() + " at line " +
                                      currentToken.getLine() + ", column " + currentToken.getColumn());
    }

    // DoWhileStatement = "do" Statement "while" "(" Expression ")" ";"
    private ASTNode parseDoWhileStatement() {
        expect(TokenType.KEYWORD, "do");
        ASTNode body = parseStatement();
        expect(TokenType.KEYWORD, "while");
        expect(TokenType.PUNCTUATION, "(");
        ASTNode condition = parseExpression();
        expect(TokenType.PUNCTUATION, ")");

        expectEndStatement();

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

    // ForStatement = "for" "(" (ExpressionNoIn? | "let|const" VariableDeclarationListNoIn) ";" Expression? ";" Expression? ")" Statement
    //              | "for" "(" (LeftHandSideExpression | "let|const" VariableDeclarationNoIn) "in" Expression ")" Statement
    private ASTNode parseForStatement() {
        expect(TokenType.KEYWORD, "for");
        expect(TokenType.PUNCTUATION, "(");

        if (variableAccess() && tokens.get(tokenPosition).getType() == TokenType.IDENTIFIER &&
                matchPos(tokenPosition + 1, TokenType.KEYWORD, "in")) {
            // Handle for-in loop
            variablesAnalyzer.startBlock();
            String access = currentToken.getValue();
            advance();
            String identifier = currentToken.getValue();
            advance();
            VariableDeclarationNode forVar = variablesAnalyzer.var(
                    new VariableDeclarationNode(access, identifier, currentToken.tokenPos()));
            expect(TokenType.KEYWORD, "in");
            ASTNode iterable = parseExpression();
            expect(TokenType.PUNCTUATION, ")");
            ASTNode body = parseStatement();
            variablesAnalyzer.endBlock();
            return new ForInStatementNode(forVar, iterable, body);
        }
        if (variableAccess() && tokens.get(tokenPosition).getType() == TokenType.IDENTIFIER &&
                matchPos(tokenPosition + 1, TokenType.KEYWORD, "of")) {
            // Handle for-of loop
            variablesAnalyzer.startBlock();
            String access = currentToken.getValue();
            advance();
            String identifier = currentToken.getValue();
            advance();
            VariableDeclarationNode forVar = variablesAnalyzer.var(
                    new VariableDeclarationNode(access, identifier, currentToken.tokenPos()));
            expect(TokenType.KEYWORD, "of");
            ASTNode iterable = parseExpression();
            expect(TokenType.PUNCTUATION, ")");
            ASTNode body = parseStatement();
            variablesAnalyzer.endBlock();
            return new ForOfStatementNode(forVar, iterable, body);
        }

        variablesAnalyzer.startBlock();
        ASTNode init = null;
        ASTNode condition = null;
        ASTNode update = null;

        // Check for var declaration
        if (variableAccess()) {
            String access = currentToken.getValue();
            advance();
            init = parseVariableDeclaration(access);
            expect(TokenType.PUNCTUATION, ";");
        } else if (match(TokenType.PUNCTUATION, ";")) {
            // Check for semicolon (empty initialization)
            // No initialization
            advance();
        } else {
            // Otherwise it's an expression
            init = parseExpression();
            expect(TokenType.PUNCTUATION, ";");
        }


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
        variablesAnalyzer.endBlock();
        return new ForStatementNode(init, condition, update, body);
    }

    // ContinueStatement = "continue" Identifier? ";"
    private ASTNode parseContinueStatement() {
        expect(TokenType.KEYWORD, "continue");

        expectEndStatement();
        return new ContinueStatementNode();
    }

    // BreakStatement = "break" Identifier? ";"
    private ASTNode parseBreakStatement() {
        expect(TokenType.KEYWORD, "break");

        expectEndStatement();
        return new BreakStatementNode();
    }

    // ReturnStatement = "return" Expression? ";"
    private ASTNode parseReturnStatement() {
        expect(TokenType.KEYWORD, "return");

        ASTNode expression = null;
        if (!(currentToken.getType() == TokenType.PUNCTUATION &&
                (currentToken.getValue().equals(";") || currentToken.getValue().equals("}")))) {
            expression = parseExpression();
        }

        expectEndStatement();
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
            if (operator.equals("=") || operator.equals("+=") || operator.equals("-=") || operator.equals("*=") ||
                    operator.equals("/=") || operator.equals("%=")) {
                advance();
                ASTNode right = parseAssignmentExpression();
                if (left instanceof IdentifierNode in) {
                    in.wasAssigned = true;
                }
                return new AssignmentExpressionNode(left, operator, right);
            }
        }
        return left;
    }

    // ConditionalExpression = LogicalORExpression ("?" AssignmentExpression ":" AssignmentExpression)? || "??" AssignmentExpression
    private ASTNode parseConditionalExpression() {
        ASTNode condition = parseLogicalORExpression();

        if (matchAdvance(TokenType.OPERATOR, "??")) {
            ASTNode right = parseAssignmentExpression();
            return new BinaryExpressionNode(condition, "??", right);
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

        while (matchAdvance(TokenType.OPERATOR, "||")) {
            ASTNode right = parseLogicalANDExpression();
            left = new BinaryExpressionNode(left, "||", right);
        }

        return left;
    }

    // LogicalANDExpression = EqualityExpression ("&&" EqualityExpression)*
    private ASTNode parseLogicalANDExpression() {
        ASTNode left = parseEqualityExpression();

        while (matchAdvance(TokenType.OPERATOR, "&&")) {
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
        if (left instanceof BinaryExpressionNode binaryExpressionNode) {
            List<ASTNode> items = checkStringConcat(binaryExpressionNode);
            if (items != null) {
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
        if (matchAdvance(TokenType.KEYWORD, "typeof")) {
            ASTNode operand = parseUnaryExpression();
            return new UnaryExpressionNode("typeof", operand);
        }
        if (matchAdvance(TokenType.OPERATOR, "++")) {
            ASTNode operand = parseUnaryExpression();
            return new UnaryExpressionNode("var++", operand);
        }
        if (matchAdvance(TokenType.OPERATOR, "--")) {
            ASTNode operand = parseUnaryExpression();
            return new UnaryExpressionNode("var--", operand);
        }
        if (currentToken.getType() == TokenType.OPERATOR) {
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
                TokenPos tokenPos = currentToken.tokenPos();
                List<ASTNode> arguments = parseArguments();
                expression = new CallExpressionNode(expression, arguments, tokenPos);
                continue;
            }
            ASTNode next = parseNextMember(expression);
            if (next != null) {
                expression = next;
            } else {
                break;
            }
        }
        return expression;
    }

    private ASTNode parseNextMember(ASTNode parent) {
        if (matchAdvance(TokenType.OPERATOR, "?.")) {
            if (currentToken.getType() == TokenType.IDENTIFIER) {
                String property = currentToken.getValue();
                advance();
                return new MemberExpressionNode(new OptionalNode(parent), property, currentToken.tokenPos());
            }
            if (matchAdvance(TokenType.PUNCTUATION, "[")) {
                // Indexed access
                ASTNode property = parseExpression();
                expect(TokenType.PUNCTUATION, "]");
                return new OptionalNode(new MemberExpressionNode(parent, property, currentToken.tokenPos()));
            }
            if (match(TokenType.PUNCTUATION, "(")) {
                // Function call
                TokenPos tokenPos = currentToken.tokenPos();
                List<ASTNode> arguments = parseArguments();
                return new CallExpressionNode(new OptionalNode(parent), arguments, tokenPos);
            }
            throw new SyntaxError("Expected identifier after '?.' but got " + currentToken.getType() + " at line " +
                                          currentToken.getLine() + ", column " + currentToken.getColumn());
        } else if (matchAdvance(TokenType.PUNCTUATION, ".")) {
            // Property access
            if (currentToken.getType() != TokenType.IDENTIFIER) {
                throw new SyntaxError("Expected identifier after '.' but got " + currentToken.getType() + " at line " +
                                              currentToken.getLine() + ", column " + currentToken.getColumn());
            }
            String property = currentToken.getValue();
            advance();
            return new MemberExpressionNode(parent, property, currentToken.tokenPos());
        } else if (matchAdvance(TokenType.PUNCTUATION, "[")) {
            // Indexed access
            ASTNode property = parseExpression();
            expect(TokenType.PUNCTUATION, "]");
            return new MemberExpressionNode(parent, property, currentToken.tokenPos());
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
            if (next != null) {
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
            while (tokens.get(pos).getType() == TokenType.IDENTIFIER) {
                pos++;
                if (!matchPos(pos, TokenType.PUNCTUATION, ",")) {
                    break;
                }
                pos++;
            }
            // check arrow
            if (matchPos(pos, TokenType.PUNCTUATION, ")") && matchPos(pos + 1, TokenType.OPERATOR, "=>")) {
                // Handle arrow function
                return parseArrowFunction();
            }
        }

        if (currentToken.getType() == TokenType.IDENTIFIER && matchPos(tokenPosition, TokenType.OPERATOR, "=>")) {
            // Handle single param arrow function
            return parseArrowFunction();
        }

        switch (currentToken.getType()) {
            case KEYWORD:
                if (currentToken.getValue().equals("this")) {
                    advance();
                    return variablesAnalyzer.var(new IdentifierNode("this", currentToken.tokenPos()));
                } else if (currentToken.getValue().equals("function")) {
                    // Function expression (anonymous function)
                    return parseFunctionExpression(false);
                }
                break;

            case IDENTIFIER:
                String identifier = currentToken.getValue();
                advance();
                return variablesAnalyzer.var(new IdentifierNode(identifier, currentToken.tokenPos()));

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

        throw new SyntaxError(
                "Unexpected token " + currentToken.getValue() + " at line " + currentToken.getLine() + ", column " +
                        currentToken.getColumn());
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
                throw new SyntaxError(
                        "Expected literal but got " + currentToken.getType() + " at line " + currentToken.getLine() +
                                ", column " + currentToken.getColumn());
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
        while (!match(TokenType.PUNCTUATION, "}") && !match(TokenType.KEYWORD, "default")) {
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
    private SwitchCaseNode parseCaseClause() {
        expect(TokenType.KEYWORD, "case");
        ASTNode test = parseExpression();
        expect(TokenType.PUNCTUATION, ":");

        List<ASTNode> consequent = new ArrayList<>();

        // Parse statements until next case, default, or end of switch
        while (!match(TokenType.KEYWORD, "case") && !match(TokenType.KEYWORD, "default") &&
                !match(TokenType.PUNCTUATION, "}")) {
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
        TokenPos tokenPos = currentToken.tokenPos();
        ASTNode expression = parseExpression();

        expectEndStatement();
        return new ThrowStatementNode(expression, tokenPos);
    }

    // TryStatement = "try" Block Catch Finally | "try" Block (Catch | Finally)
    private ASTNode parseTryStatement() {
        expect(TokenType.KEYWORD, "try");
        ASTNode block = parseBlock();

        CatchClauseNode catchBlock = null;
        ASTNode finallyBlock = null;


        // Check for catch block
        if (matchAdvance(TokenType.KEYWORD, "catch")) {

            variablesAnalyzer.startBlock();
            VariableDeclarationNode errVar = null;
            if (matchAdvance(TokenType.PUNCTUATION, "(")) {

                if (currentToken.getType() != TokenType.IDENTIFIER) {
                    throw new SyntaxError("Expected identifier but got " + currentToken.getType() + " at line " +
                                                  currentToken.getLine() + ", column " + currentToken.getColumn());
                }
                errVar = variablesAnalyzer.var(
                        new VariableDeclarationNode("let", currentToken.getValue(), currentToken.tokenPos()));
                advance();
                expect(TokenType.PUNCTUATION, ")");
            }
            ASTNode catchBody = parseBlock();
            catchBlock = new CatchClauseNode(errVar, catchBody);
            variablesAnalyzer.endBlock();
        }

        // Check for finally block
        if (match(TokenType.KEYWORD, "finally")) {
            expect(TokenType.KEYWORD, "finally");
            finallyBlock = parseBlock();
        }

        // At least one of catch or finally must be present
        if (catchBlock == null && finallyBlock == null) {
            throw new SyntaxError(
                    "Expected catch or finally clause after try at line " + currentToken.getLine() + ", column " +
                            currentToken.getColumn());
        }

        return new TryStatementNode(block, catchBlock, finallyBlock);
    }

    // FunctionDeclaration = "function" Identifier "(" FormalParameterList? ")" Block
    private ASTNode parseFunctionDeclaration() {
        return parseFunctionExpression(true);
    }

    // ArrayLiteral = "[" ElementList? "]"
    private ASTNode parseArrayLiteral() {
        expect(TokenType.PUNCTUATION, "[");

        List<ASTNode> elements = new ArrayList<>();

        // Parse elements if present
        if (!match(TokenType.PUNCTUATION, "]")) {
            // Handle first element (which might be elided)
            if (match(TokenType.PUNCTUATION, ",")) {
                elements.add(new ConstantNode(Undefined.INSTANCE)); // Elided element
            } else {
                elements.add(parseAssignmentExpression());
            }

            // Parse additional elements separated by commas
            while (matchAdvance(TokenType.PUNCTUATION, ",")) {
                if (match(TokenType.PUNCTUATION, "]")) {
                    break; // Trailing comma
                } else if (match(TokenType.PUNCTUATION, ",")) {
                    elements.add(new ConstantNode(Undefined.INSTANCE)); // Elided element
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
        if (match(TokenType.PUNCTUATION, ",") || match(TokenType.PUNCTUATION, "}")) {
            return new PropertyNode(key, variablesAnalyzer.var(new IdentifierNode(key, currentToken.tokenPos())));
        }
        expect(TokenType.PUNCTUATION, ":");
        ASTNode value = parseAssignmentExpression();

        return new PropertyNode(key, value);
    }

    // PropertyName = Identifier | StringLiteral | NumericLiteral
    private String parsePropertyName() {
        if (currentToken.getType() == TokenType.IDENTIFIER || currentToken.getType() == TokenType.STRING ||
                currentToken.getType() == TokenType.NUMBER) {
            String name = currentToken.getValue();
            advance();
            return name;
        } else {
            throw new SyntaxError(
                    "Expected property name but got " + currentToken.getType() + " at line " + currentToken.getLine() +
                            ", column " + currentToken.getColumn());
        }
    }

    // FunctionExpression = "function" Identifier? "(" FormalParameterList? ")" Block
    private ASTNode parseFunctionExpression(boolean requiredName) {
        expect(TokenType.KEYWORD, "function");
        String name = null;
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            name = currentToken.getValue();
            advance();
        } else if (requiredName) {
            throw new SyntaxError(
                    "Expected function name in function expression at line " + currentToken.getLine() + ", column " +
                            currentToken.getColumn());
        }
        try {
            variablesAnalyzer.startFunction(name, currentToken.tokenPos());
            expect(TokenType.PUNCTUATION, "(");
            TokenPos tokenPos = currentToken.tokenPos();
            List<String> params = new ArrayList<>();
            while (currentToken.getType() == TokenType.IDENTIFIER) {
                String paramName = currentToken.getValue();
                params.add(paramName);
                variablesAnalyzer.param(paramName, currentToken.tokenPos());
                advance();
                if (match(TokenType.PUNCTUATION, ",")) {
                    advance(); // Skip comma
                } else {
                    break; // No more parameters
                }
            }
            expect(TokenType.PUNCTUATION, ")");
            ASTNode body = parseBlock();
            return variablesAnalyzer.var(
                    new FunctionDeclarationNode(name, tokenPos, params, body, variablesAnalyzer.stackDef(), false));
        } finally {
            variablesAnalyzer.endFunction();
        }
    }

    // Parse arrow expression - (param1, param2, ...) => expression | block
    private ASTNode parseArrowFunction() {
        try {
            variablesAnalyzer.startFunction(null, currentToken.tokenPos());
            List<String> params = new ArrayList<>();
            TokenPos tokenPos = currentToken.tokenPos();

            // Check for parameter syntax
            if (matchAdvance(TokenType.PUNCTUATION, "(")) {

                while (currentToken.getType() == TokenType.IDENTIFIER) {
                    params.add(currentToken.getValue());
                    variablesAnalyzer.param(currentToken.getValue(), currentToken.tokenPos());
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
                variablesAnalyzer.param(currentToken.getValue(), currentToken.tokenPos());
                advance();
            } else {
                throw new SyntaxError(
                        "Expected parameter list for lambda function but got " + currentToken.getType() + " at line " +
                                currentToken.getLine() + ", column " + currentToken.getColumn());
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
            return variablesAnalyzer.var(
                    new FunctionDeclarationNode(null, tokenPos, params, body, variablesAnalyzer.stackDef(), true));
        } finally {
            variablesAnalyzer.endFunction();
        }
    }

    public List<ASTNode> checkStringConcat(BinaryExpressionNode node) {
        List<ASTNode> items = new ArrayList<>();
        if (checkStringConcat(items, node)) {
            for (ASTNode n : items) {
                if (n instanceof ConstantNode constantNode && constantNode.value instanceof String) {
                    return items.reversed();
                }
            }
            return null;
        }
        return null;
    }

    //sring tempalate
    public ASTNode parseStringTemplate() {
        List<ASTNode> items = new ArrayList<>();
        if (currentToken.getSubtokens() != null) {
            for (Token subtoken : currentToken.getSubtokens()) {
                String tokenStr = subtoken.getValue();
                if (tokenStr.isEmpty()) {
                    continue;
                }
                if (tokenStr.startsWith("${")) {
                    JsLexer lexer = new JsLexer(tokenStr.substring(1), subtoken.getLine(), subtoken.getColumn());
                    JsParser parser = new JsParser(lexer.tokenize(), variablesAnalyzer);
                    BlockNode block = parser.parseBlock();
                    if (block.statements.size() == 1 && block.statements.getFirst() instanceof ExecuteWithReturn) {
                        items.add(block.statements.getFirst());
                    } else {
                        throw new SyntaxError("string template invalid expression " + subtoken);
                    }
                } else {
                    items.add(new ConstantNode(tokenStr));
                }
            }
        }
        advance();
        if (items.isEmpty()) {
            return new ConstantNode("");
        } else if (items.size() == 1) {
            return items.getFirst();
        }
        return new StringConcatExpressionNode(items);
    }

    public static boolean checkStringConcat(List<ASTNode> items, BinaryExpressionNode node) {
        if (!node.operator.equals("+")) {
            return false;
        }
        if (node.right instanceof StringConcatExpressionNode stringConcatExpressionNode) {
            items.addAll(stringConcatExpressionNode.items);
        }
        if (node.right instanceof ExecuteWithReturn) {
            items.add(node.right);
        }
        if (node.left instanceof BinaryExpressionNode binaryExpressionNode) {
            return checkStringConcat(items, binaryExpressionNode);
        }
        if (node.left instanceof ExecuteWithReturn) {
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