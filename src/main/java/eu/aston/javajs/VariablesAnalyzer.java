package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VariablesAnalyzer {

    private static final List<String> futuredReservedWords = List.of("async", "await", "class", "debugger", "delete",
                                                                     "enum", "export", "eval", "extends", "import",
                                                                     "in", "instanceof", "interface", "new", "super",
                                                                     "package", "private", "protected", "public", "var",
                                                                     "void", "with");

    public static class Var {
        public String access;
        public String name;
        public int index;

        public Var(String access, String name, int index) {
            this.access = access;
            this.name = name;
            this.index = index;
        }
    }

    public static class ExtRef {
        public String name;
        public int index;
        public int deep;
        public int varIndex;

        public ExtRef(String name, int index, int deep, int varIndex) {
            this.name = name;
            this.index = index;
            this.deep = deep;
            this.varIndex = varIndex;
        }
    }

    public static class Fn {
        public String name;
        public int deepLevel;
        public List<Var> vars = new ArrayList<>();
        public List<ExtRef> extRefs = new ArrayList<>();
        public Link varLink;

        public Fn(String name, int deepLevel) {
            this.name = name;
            this.deepLevel = deepLevel;
        }
    }

    public static class Link {
        public int pos;
        public String linkId;
        public String name;
        public Fn parent;
        public AstNodes.ASTNode node;
        public Var var;

        public Link(int pos, String linkId, String name, Fn parent, AstNodes.ASTNode node) {
            this.pos = pos;
            this.linkId = linkId;
            this.name = name;
            this.parent = parent;
            this.node = node;
        }

        public Link(int pos, String linkId, Var var, Fn parent, AstNodes.ASTNode node) {
            this.pos = pos;
            this.linkId = linkId;
            this.name = var.name;
            this.parent = parent;
            this.var = var;
            this.node = node;
        }
    }

    int counterBlock = 1;
    int counterVar = 1;
    final Fn root = new Fn(null, 0);
    Map<String, Link> varMap = new HashMap<>();
    List<Link> links = new ArrayList<>();
    List<Integer> blockStack = new ArrayList<>();
    List<Fn> functionStack = new ArrayList<>();

    public VariablesAnalyzer() {
        functionStack.add(root);
    }

    private String varId(List<Integer> blockStack, String name) {
        if (blockStack.isEmpty()) {
            return name;
        }
        return name + "/" + blockStack.stream().map(Object::toString).collect(Collectors.joining("/"));
    }

    public void startBlock() {
        blockStack.add(counterBlock++);
    }

    public void endBlock() {
        blockStack.removeLast();
    }

    public void startFunction(String name, TokenPos tokenPos) {
        Fn parent = functionStack.getLast();
        Fn fn = new Fn(name, parent.deepLevel + 1);
        if (name != null) {
            fn.varLink = addVar("const", name, tokenPos, null);
        }
        functionStack.add(fn);
        blockStack.add(counterBlock++);
        param("this", tokenPos);
        param("arguments", tokenPos);
    }

    public void endFunction() {
        blockStack.removeLast();
        functionStack.removeLast();
    }

    public Scope.ScopeDef stackDef() {
        return new Scope.ScopeDef(functionStack.getLast());
    }

    public void param(String name, TokenPos tokenPos) {
        addVar("let", name, tokenPos, null);
    }

    public AstNodes.VariableDeclarationNode var(AstNodes.VariableDeclarationNode node) {
        if (node.access != null) {
            //declaration
            addVar(node.access, node.identifier, node.tokenPos, node);
        } else {
            addLink(node.identifier, node.tokenPos, node);
        }
        return node;
    }

    public AstNodes.FunctionDeclarationNode var(AstNodes.FunctionDeclarationNode node) {
        Fn akt = functionStack.getLast();
        if (akt.varLink != null) {
            akt.varLink.node = node;
            node.scopeGetSet = new Scope.LocalGetSet(akt.varLink.var.index);
        }
        return node;
    }

    public AstNodes.IdentifierNode var(AstNodes.IdentifierNode node) {
        addLink(node.name, node.tokenPos, node);
        return node;
    }

    private Link addVar(String access, String name, TokenPos tokenPos, AstNodes.ASTNode node) {
        if (futuredReservedWords.contains(name)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: Function name '" + name + "' use future reserved word at line " + tokenPos.line() +
                            " column " + tokenPos.column());
        }
        String varId = varId(blockStack, name);
        if (varMap.containsKey(varId)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: duplicated param '" + name + "' at line " + tokenPos.line() + " column " +
                            tokenPos.column());
        }
        Fn parent = functionStack.getLast();
        Var v = new Var(access, name, parent.vars.size());
        parent.vars.add(v);
        Link link = new Link(counterVar++, varId(blockStack, name), v, parent, node);
        varMap.put(varId, link);
        nodeIndex(node, new Scope.LocalGetSet(v.index));
        return link;
    }

    private void addLink(String name, TokenPos tokenPos, AstNodes.ASTNode node) {
        if (futuredReservedWords.contains(name)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: variable name '" + name + "' use future reserved word at line " + tokenPos.line() +
                            " column " + tokenPos.column());
        }
        Fn parent = functionStack.getLast();
        links.add(new Link(counterVar++, varId(blockStack, name), name, parent, node));
    }

    public void pairAll() {
        for (Link link : links) {
            String id = link.linkId;
            Link varLink = varMap.get(id);
            while (true) {
                varLink = varMap.get(id);
                if (varLink != null &&
                        (link.pos > varLink.pos || varLink.node instanceof AstNodes.FunctionDeclarationNode)) {
                    if ("const".equals(varLink.var.access)) {
                        checkChangingConst(link.node);
                    }
                    if (link.parent == varLink.parent) {
                        nodeIndex(link.node, new Scope.LocalGetSet(varLink.var.index));
                    } else {
                        ExtRef ref = createExtRef(link.parent, link.name,
                                                  link.parent.deepLevel - varLink.parent.deepLevel - 1,
                                                  varLink.var.index);
                        nodeIndex(link.node, new Scope.ExtGetSet(ref.index));
                    }
                    break;
                }
                int pos = id.lastIndexOf('/');
                if (pos < 0) {
                    nodeIndex(link.node, new Scope.MapGetSet(link.name));
                    break;
                }
                id = id.substring(0, pos);
            }
        }
        System.out.println("ok");
    }

    private ExtRef createExtRef(Fn parent, String name, int deep, int varIndex) {
        for (ExtRef ref : parent.extRefs) {
            if (ref.name.equals(name)) {
                return ref;
            }
        }
        ExtRef ref = new ExtRef(name, parent.extRefs.size(), deep, varIndex);
        parent.extRefs.add(ref);
        return ref;
    }

    public void checkChangingConst(AstNodes.ASTNode node) {
        if (node instanceof AstNodes.IdentifierNode in && in.wasAssigned) {
            throw new JsParser.SyntaxError(
                    "TypeError: Assignment to constant variable '" + in.name + "' in line " + in.tokenPos.line() +
                            " column " + in.tokenPos.column());
        }
    }

    private void nodeIndex(AstNodes.ASTNode node, Scope.IGetSet scopeGetSet) {
        if (node instanceof AstNodes.VariableDeclarationNode vn) {
            vn.scopeGetSet = scopeGetSet;
        } else if (node instanceof AstNodes.FunctionDeclarationNode fn) {
            fn.scopeGetSet = scopeGetSet;
        } else if (node instanceof AstNodes.IdentifierNode in) {
            in.scopeGetSet = scopeGetSet;
        }
    }

}
