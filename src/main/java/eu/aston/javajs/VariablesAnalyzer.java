package eu.aston.javajs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class VariablesAnalyzer {

    private static final List<String> futuredReservedWords = List.of("class", "debugger", "delete", "enum", "export",
                                                                     "eval", "extends", "import", "in", "instanceof",
                                                                     "interface", "new", "super", "package", "private",
                                                                     "protected", "public", "var", "void", "with");

    public static class FnVar {
        public String access;
        public String name;
        public int index;
        public int varLevel;
        public List<AstNodes.ASTNode> nodes = new ArrayList<>();
        public FnVar parent;
        public List<FnVar> children;
        public int childLevel = 0;
        public int[] ext;

        public FnVar(String access, String name, int varLevel) {
            this.access = access;
            this.name = name;
            this.varLevel = varLevel;
        }
    }

    public record ExtVar(int deep, FnVar var) {
    }

    private final FnVar root;
    private FnVar parent;

    public VariablesAnalyzer() {
        this.parent = new FnVar("root", "root", 0);
        parent.children = new ArrayList<>();
        this.root = parent;
    }

    public void startBlock() {
        parent.childLevel++;
    }

    public void endBlock() {
        parent.childLevel--;
    }

    public void startFunction(String name, TokenPos tokenPos) {
        List<AstNodes.ASTNode> migrate = null;
        if (name != null) {
            if (futuredReservedWords.contains(name)) {
                throw new JsParser.SyntaxError(
                        "SyntaxError: Function name '" + name + "' use future reserved word at line" + tokenPos.line() +
                                " column " + tokenPos.column());
            }
            FnVar prev = findLocal(name, (v) -> v.varLevel >= parent.childLevel);
            if (prev != null) {
                if (prev.access == null) {
                    //pouzitie bez deklaracie na tej istej urovni
                    migrate = prev.nodes;
                    parent.children.remove(prev);
                } else if (prev.varLevel == parent.childLevel) {
                    throw new JsParser.SyntaxError(
                            "SyntaxError: Function name '" + name + "' already been declared at line " +
                                    tokenPos.line() + " column " + tokenPos.column());
                }
            }
        }
        FnVar v2 = new FnVar(name != null ? "const" : "function", name, parent.childLevel);
        if (migrate != null) {
            v2.nodes.addAll(migrate);
        }
        parent.children.add(v2);

        v2.parent = parent;
        v2.children = new ArrayList<>();
        parent = v2;
        param("this", tokenPos);
        param("arguments", tokenPos);
    }

    public void endFunction() {
        this.parent = parent.parent;
    }

    public Scope.ScopeDef stackDef() {
        return new Scope.ScopeDef(parent);
    }

    public void param(String name, TokenPos tokenPos) {
        if (futuredReservedWords.contains(name)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: Function name '" + name + "' use future reserved word at line" + tokenPos.line() +
                            " column " + tokenPos.column());
        }
        FnVar newVar = new FnVar("let", name, parent.childLevel);
        if (findLocal(name, null) != null) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: duplicated param '" + name + "' at line " + tokenPos.line() + " column " +
                            tokenPos.column());
        }
        parent.children.add(newVar);
    }

    public AstNodes.VariableDeclarationNode var(AstNodes.VariableDeclarationNode node) {
        if (futuredReservedWords.contains(node.identifier)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: variable '" + node.identifier + "' use future reserved word at line" +
                            node.tokenPos.line() + " column " + node.tokenPos.column());
        }
        FnVar prev = findLocal(node.identifier, (v) -> v.varLevel == parent.childLevel);
        if (prev != null) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: variable '" + node.identifier + "' already been declared at line " +
                            node.tokenPos.line() + " column " + node.tokenPos.column());
        }
        FnVar newVar = new FnVar(node.access, node.identifier, parent.childLevel);
        newVar.nodes.add(node);
        parent.children.add(newVar);
        return node;
    }

    public AstNodes.FunctionDeclarationNode var(AstNodes.FunctionDeclarationNode node) {
        parent.nodes.add(node);
        return node;
    }

    public AstNodes.IdentifierNode var(AstNodes.IdentifierNode node) {
        if (futuredReservedWords.contains(node.name)) {
            throw new JsParser.SyntaxError(
                    "SyntaxError: variable name '" + node.name + "' use future reserved word at line" +
                            node.tokenPos.line() + " column " + node.tokenPos.column());
        }
        FnVar prev = findLocal(node.name, v -> v.varLevel <= parent.childLevel);
        if (prev != null) {
            prev.nodes.add(node);
        } else {
            FnVar newVar = new FnVar(null, node.name, parent.childLevel);
            newVar.nodes.add(node);
            parent.children.add(newVar);
        }
        return node;
    }

    private FnVar findLocal(String name, Predicate<FnVar> eq) {
        List<FnVar> vars = parent.children;
        for (int i = vars.size() - 1; i >= 0; i--) {
            FnVar v = vars.get(i);
            if (name.equals(v.name) && (eq == null || eq.test(v))) {
                return v;
            }
        }
        return null;
    }

    public void recalc() {
        recalcVarChildren(root);
    }

    private void recalcVarChildren(FnVar var) {
        for (int i = 0; i < var.children.size(); i++) {
            FnVar v = var.children.get(i);
            v.index = i;
            if (v.access != null) {
                for (AstNodes.ASTNode node : v.nodes) {
                    nodeIndex(node, i);
                }
                if ("const".equals(v.access)) {
                    checkChangingConst(v);
                }
            } else {
                ExtVar ext = findExt(var.parent, var, v.name, 0, i);
                if (ext != null) {
                    for (AstNodes.ASTNode node : v.nodes) {
                        nodeIndex(node, i);
                    }
                    v.ext = new int[]{i, ext.deep, ext.var.index};
                    if ("const".equals(ext.var.access)) {
                        checkChangingConst(v);
                    }
                }
            }
        }
        for (FnVar v : var.children) {
            if (v.children != null) {
                recalcVarChildren(v);
            }
        }
    }

    private void checkChangingConst(FnVar v) {
        for (AstNodes.ASTNode n : v.nodes) {
            if (n instanceof AstNodes.IdentifierNode in && in.wasAssigned) {
                throw new RuntimeException("ReferenceError: rewrite constant variable '" + v.name + "'");
            }
        }
    }

    private void nodeIndex(AstNodes.ASTNode node, int index) {
        if (node instanceof AstNodes.VariableDeclarationNode vn) {
            vn.index = index;
        } else if (node instanceof AstNodes.FunctionDeclarationNode fn) {
            fn.index = index;
        } else if (node instanceof AstNodes.IdentifierNode in) {
            in.index = index;
        }
    }

    private ExtVar findExt(FnVar parent, FnVar v, String name, int deep, int stackIndex) {
        if (parent == null || v == null) {
            return null;
        }
        List<FnVar> l = parent.children;
        int varPos = l.indexOf(v);
        for (int pos = l.size() - 1; pos >= 0; pos--) {
            FnVar v2 = l.get(pos);
            if (pos > varPos) {
                continue;
            }
            if (Objects.equals(v2.name, name) && v2.access != null) {
                return new ExtVar(deep, v2);
            }
        }
        return findExt(parent.parent, parent, name, deep + 1, stackIndex);
    }

    public void deepPrint() {
        deepPrint(root, 0);
    }

    private void deepPrint(FnVar var, int level) {

        System.out.println(
                "  ".repeat(level) + var.access + " " + var.name + " - " + var.varLevel + " " + var.nodes.size() + " " +
                        (var.ext != null ? Arrays.toString(var.ext) : ""));
        if (var.children != null) {
            for (FnVar v2 : var.children) {
                deepPrint(v2, level + 1);
            }
        }
    }
}
