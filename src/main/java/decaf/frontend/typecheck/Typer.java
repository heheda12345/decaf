package decaf.frontend.typecheck;

import decaf.driver.Config;
import decaf.driver.Phase;
import decaf.driver.error.*;
import decaf.frontend.scope.ScopeStack;
import decaf.frontend.symbol.ClassSymbol;
import decaf.frontend.symbol.LambdaSymbol;
import decaf.frontend.symbol.MethodSymbol;
import decaf.frontend.symbol.Symbol;
import decaf.frontend.symbol.VarSymbol;
import decaf.frontend.tree.Pos;
import decaf.frontend.tree.Tree;
import decaf.frontend.tree.Tree.Lambda;
import decaf.frontend.tree.Tree.NewArray;
import decaf.frontend.tree.Tree.NewClass;
import decaf.frontend.tree.Tree.VarSel;
import decaf.frontend.tree.Tree.Lambda.LambdaType;
import decaf.frontend.type.ArrayType;
import decaf.frontend.type.BuiltInType;
import decaf.frontend.type.ClassType;
import decaf.frontend.type.FunType;
import decaf.frontend.type.Type;
import decaf.lowlevel.log.IndentPrinter;
import decaf.printing.PrettyScope;

import java.util.Optional;

/**
 * The typer phase: type check abstract syntax tree and annotate nodes with inferred (and checked) types.
 */
public class Typer extends Phase<Tree.TopLevel, Tree.TopLevel> implements TypeLitVisited {

    public Typer(Config config) {
        super("typer", config);
    }

    @Override
    public Tree.TopLevel transform(Tree.TopLevel tree) {
        // System.out.println("Type begin!");
        var ctx = new ScopeStack(tree.globalScope);
        tree.accept(this, ctx);
        return tree;
    }

    @Override
    public void onSucceed(Tree.TopLevel tree) {
        if (config.target.equals(Config.Target.PA2)) {
            var printer = new PrettyScope(new IndentPrinter(config.output));
            printer.pretty(tree.globalScope);
            printer.flush();
        }
    }

    @Override
    public void visitTopLevel(Tree.TopLevel program, ScopeStack ctx) {
        for (var clazz : program.classes) {
            clazz.accept(this, ctx);
        }
    }

    @Override
    public void visitClassDef(Tree.ClassDef clazz, ScopeStack ctx) {
        ctx.open(clazz.symbol.scope);
        for (var field : clazz.fields) {
            field.accept(this, ctx);
        }
        ctx.close();
    }

    @Override
    public void visitMethodDef(Tree.MethodDef method, ScopeStack ctx) {
        ctx.open(method.symbol.scope);
        if (method.body.isPresent()) {
            method.body.get().accept(this, ctx);
            if (!method.symbol.type.returnType.isVoidType() && !method.body.get().returns) {
                issue(new MissingReturnError(method.body.get().pos));
            }
        }
        ctx.close();
    }

    /**
     * To determine if a break statement is legal or not, we need to know if we are inside a loop, i.e.
     * loopLevel {@literal >} 1?
     * <p>
     * Increase this counter when entering a loop, and decrease it when leaving a loop.
     */
    private int loopLevel = 0;

    @Override
    public void visitBlock(Tree.Block block, ScopeStack ctx) {
        ctx.open(block.scope);
        for (var stmt : block.stmts) {
            stmt.accept(this, ctx);
        }
        ctx.close();
        block.returns = !block.stmts.isEmpty() && block.stmts.get(block.stmts.size() - 1).returns;
        if (block.returns)
            block.returnType = block.stmts.get(block.stmts.size() - 1).returnType;
    }

    @Override
    public void visitAssign(Tree.Assign stmt, ScopeStack ctx) {
        stmt.lhs.accept(this, ctx);
        stmt.rhs.accept(this, ctx);
        var lt = stmt.lhs.symbol.type;
        var rt = stmt.rhs.symbol.type;

        if (lt.noError() && (lt.isFuncType() || !rt.subtypeOf(lt))) {
            issue(new IncompatBinOpError(stmt.pos, lt.toString(), "=", rt.toString()));
        }
    }

    @Override
    public void visitExprEval(Tree.ExprEval stmt, ScopeStack ctx) {
        stmt.expr.accept(this, ctx);
    }


    @Override
    public void visitIf(Tree.If stmt, ScopeStack ctx) {
        checkTestExpr(stmt.cond, ctx);
        stmt.trueBranch.accept(this, ctx);
        stmt.falseBranch.ifPresent(b -> b.accept(this, ctx));
        // if-stmt returns a value iff both branches return
        stmt.returns = stmt.trueBranch.returns && stmt.falseBranch.isPresent() && stmt.falseBranch.get().returns;
        if (stmt.returns)
            stmt.returnType = stmt.trueBranch.returnType;
    }

    @Override
    public void visitWhile(Tree.While loop, ScopeStack ctx) {
        checkTestExpr(loop.cond, ctx);
        loopLevel++;
        loop.body.accept(this, ctx);
        loopLevel--;
    }

    @Override
    public void visitFor(Tree.For loop, ScopeStack ctx) {
        ctx.open(loop.scope);
        loop.init.accept(this, ctx);
        checkTestExpr(loop.cond, ctx);
        loop.update.accept(this, ctx);
        loopLevel++;
        for (var stmt : loop.body.stmts) {
            stmt.accept(this, ctx);
        }
        loopLevel--;
        ctx.close();
    }

    @Override
    public void visitBreak(Tree.Break stmt, ScopeStack ctx) {
        if (loopLevel == 0) {
            issue(new BreakOutOfLoopError(stmt.pos));
        }
    }

    @Override
    public void visitReturn(Tree.Return stmt, ScopeStack ctx) {
        var expected = ctx.currentMethod().type.returnType;
        stmt.expr.ifPresent(e -> e.accept(this, ctx));
        var actual = stmt.expr.map(e -> e.symbol.type).orElse(BuiltInType.VOID);
        if (actual.noError() && !actual.subtypeOf(expected)) {
            issue(new BadReturnTypeError(stmt.pos, expected.toString(), actual.toString()));
        }
        stmt.returns = stmt.expr.isPresent();
        if (stmt.returns)
            stmt.returnType = stmt.expr.get().symbol.type;
    }

    @Override
    public void visitPrint(Tree.Print stmt, ScopeStack ctx) {
        int i = 0;
        for (var expr : stmt.exprs) {
            expr.accept(this, ctx);
            i++;
            if (expr.symbol.type.noError() && !expr.symbol.type.isBaseType()) {
                issue(new BadPrintArgError(expr.pos, Integer.toString(i), expr.symbol.type.toString()));
            }
        }
    }

    private void checkTestExpr(Tree.Expr expr, ScopeStack ctx) {
        expr.accept(this, ctx);
        if (expr.symbol.type.noError() && !expr.symbol.type.eq(BuiltInType.BOOL)) {
            issue(new BadTestExpr(expr.pos));
        }
    }

    // Expressions

    @Override
    public void visitIntLit(Tree.IntLit that, ScopeStack ctx) {
        that.symbol = new VarSymbol("NONAMEINTLIT", BuiltInType.INT, that.pos);
    }

    @Override
    public void visitBoolLit(Tree.BoolLit that, ScopeStack ctx) {
        that.symbol = new VarSymbol("NONAMEBOOLLIT", BuiltInType.BOOL, that.pos);
    }

    @Override
    public void visitStringLit(Tree.StringLit that, ScopeStack ctx) {
        that.symbol = new VarSymbol("NONAMESTRINGLIT", BuiltInType.STRING, that.pos);
    }

    @Override
    public void visitNullLit(Tree.NullLit that, ScopeStack ctx) {
        that.symbol = new VarSymbol("NONAMENULLLIT", BuiltInType.NULL, that.pos);
    }

    @Override
    public void visitReadInt(Tree.ReadInt readInt, ScopeStack ctx) {
        readInt.symbol = new VarSymbol("NONAMEREADINT", BuiltInType.INT, readInt.pos);
    }

    @Override
    public void visitReadLine(Tree.ReadLine readStringExpr, ScopeStack ctx) {
        readStringExpr.symbol = new VarSymbol("NONAMEREADLINE", BuiltInType.STRING, readStringExpr.pos);
    }

    @Override
    public void visitUnary(Tree.Unary expr, ScopeStack ctx) {
        expr.operand.accept(this, ctx);
        var t = expr.operand.symbol.type;
        if (t.noError() && !compatible(expr.op, t)) {
            // Only report this error when the operand has no error, to avoid nested errors flushing.
            issue(new IncompatUnOpError(expr.pos, Tree.opStr(expr.op), t.toString()));
        }

        // Even when it doesn't type check, we could make a fair guess based on the operator kind.
        // Let's say the operator is `-`, then one possibly wants an integer as the operand.
        // Once he/she fixes the operand, according to our type inference rule, the whole unary expression
        // must have type int! Thus, we simply _assume_ it has type int, rather than `NoType`.
        expr.symbol = new VarSymbol("NONAMEUNARY", resultTypeOf(expr.op), expr.pos);
    }

    public boolean compatible(Tree.UnaryOp op, Type operand) {
        return switch (op) {
            case NEG -> operand.eq(BuiltInType.INT); // if e : int, then -e : int
            case NOT -> operand.eq(BuiltInType.BOOL); // if e : bool, then !e : bool
        };
    }

    public Type resultTypeOf(Tree.UnaryOp op) {
        return switch (op) {
            case NEG -> BuiltInType.INT;
            case NOT -> BuiltInType.BOOL;
        };
    }

    @Override
    public void visitBinary(Tree.Binary expr, ScopeStack ctx) {
        expr.lhs.accept(this, ctx);
        expr.rhs.accept(this, ctx);
        var t1 = expr.lhs.symbol.type;
        var t2 = expr.rhs.symbol.type;
        if (t1.noError() && t2.noError() && !compatible(expr.op, t1, t2)) {
            issue(new IncompatBinOpError(expr.pos, t1.toString(), Tree.opStr(expr.op), t2.toString()));
        }
        expr.symbol = new VarSymbol("NONAMEBINARY", resultTypeOf(expr.op), expr.pos);
    }

    public boolean compatible(Tree.BinaryOp op, Type lhs, Type rhs) {
        if (op.compareTo(Tree.BinaryOp.ADD) >= 0 && op.compareTo(Tree.BinaryOp.MOD) <= 0) { // arith
            // if e1, e2 : int, then e1 + e2 : int
            return lhs.eq(BuiltInType.INT) && rhs.eq(BuiltInType.INT);
        }

        if (op.equals(Tree.BinaryOp.AND) || op.equals(Tree.BinaryOp.OR)) { // logic
            // if e1, e2 : bool, then e1 && e2 : bool
            return lhs.eq(BuiltInType.BOOL) && rhs.eq(BuiltInType.BOOL);
        }

        if (op.equals(Tree.BinaryOp.EQ) || op.equals(Tree.BinaryOp.NE)) { // eq
            // if e1 : T1, e2 : T2, T1 <: T2 or T2 <: T1, then e1 == e2 : bool
            return lhs.subtypeOf(rhs) || rhs.subtypeOf(lhs);
        }

        // compare
        // if e1, e2 : int, then e1 > e2 : bool
        return lhs.eq(BuiltInType.INT) && rhs.eq(BuiltInType.INT);
    }

    public Type resultTypeOf(Tree.BinaryOp op) {
        if (op.compareTo(Tree.BinaryOp.ADD) >= 0 && op.compareTo(Tree.BinaryOp.MOD) <= 0) { // arith
            return BuiltInType.INT;
        }
        return BuiltInType.BOOL;
    }

    @Override
    public void visitNewArray(Tree.NewArray expr, ScopeStack ctx) {
        expr.elemType.accept(this, ctx);
        expr.length.accept(this, ctx);
        var et = expr.elemType.type;
        var lt = expr.length.symbol.type;

        if (et.isVoidType()) {
            issue(new BadArrElementError(expr.elemType.pos));
            expr.symbol = new VarSymbol("NONAMENEWARRAY", BuiltInType.ERROR, expr.pos);
        } else {
            expr.symbol = new VarSymbol("NONAMENEWARRAY", new ArrayType(et), expr.pos);
        }

        if (lt.noError() && !lt.eq(BuiltInType.INT)) {
            issue(new BadNewArrayLength(expr.length.pos));
        }
    }

    @Override
    public void visitNewClass(Tree.NewClass expr, ScopeStack ctx) {
        var clazz = ctx.lookupClass(expr.clazz.name);
        if (clazz.isPresent()) {
            expr.symbol = clazz.get();
            if (clazz.get().isAbstract)
                issue(new UseAbstractClassError(expr.pos, expr.clazz.name));
        } else {
            issue(new ClassNotFoundError(expr.pos, expr.clazz.name));
            expr.symbol = new VarSymbol("NONAMENEWCLASS", BuiltInType.ERROR, expr.pos);
            // use var symbol as it is easy
        }
    }

    @Override
    public void visitThis(Tree.This expr, ScopeStack ctx) {
        if (ctx.currentMethod().isStatic()) {
            issue(new ThisInStaticFuncError(expr.pos));
        }
        expr.symbol = ctx.currentClass();
    }

    private boolean allowClassNameVar = false;

    @Override
    public void visitVarSel(Tree.VarSel expr, ScopeStack ctx) {
        // System.out.println(expr.pos + "visit varsel " + expr);
        assert expr.name.isPresent();
        if (expr.receiver.isEmpty()) {
            // Variable, which should be complicated since a legal variable could refer to a local var,
            // a visible member var, and a class name.
            // System.out.println("no receiver, search " + expr.name);
            expr.symbol = new VarSymbol("NONAMESELERRNORECIVER", BuiltInType.ERROR, expr.pos);
            var symbol = ctx.lookupBefore(expr.name.get(), localVarDefPos.orElse(expr.pos));
            if (symbol.isPresent()) {
                // System.out.println("symbol present " + symbol.get());
                if (symbol.get().isVarSymbol()) {
                    var var = (VarSymbol) symbol.get();
                    if (var.isMemberVar()) {
                        if (ctx.currentMethod().isStatic()) {
                            issue(new RefNonStaticError(expr.pos, ctx.currentMethod().name, expr.name.get()));
                        }
                    }
                    expr.symbol = symbol.get();
                    return;
                }

                if (symbol.get().isClassSymbol()) {
                    if (allowClassNameVar) { // special case: a class name
                        expr.isClassName = true;
                        expr.symbol = symbol.get();
                        return;
                    } // else undeclvarerror
                }

                if (symbol.get().isMethodSymbol()) { // from visitCall
                    // System.out.println("typer: find method symbol " + symbol.get().name + symbol.get().type);
                    var method = (MethodSymbol)symbol.get();
                    if (ctx.currentMethod().isStatic() && !method.isStatic()) {
                        issue(new RefNonStaticError(expr.pos, ctx.currentMethod().name, method.name));
                        return;
                    }
                    expr.symbol = symbol.get();
                    return;
                }

                if (symbol.get().isLambdaSymbol()) {
                    // maybe should check as method?
                    // System.out.println("typer: find lambda symbol " + symbol.get().name + symbol.get().type);
                    expr.symbol = symbol.get();
                    return;
                    // expr.accept(this, ctx);
                }   
            }
            // System.out.println("symbol not present");
            issue(new UndeclVarError(expr.pos, expr.name.get()));
            return;
        } else {
            // has receiver
            var receiver = expr.receiver.get();
            allowClassNameVar = true;
            receiver.accept(this, ctx);
            allowClassNameVar = false;
            // System.out.println("has receiver " + receiver.symbol.type + receiver.symbol.name);
            var rt = receiver.symbol.type;
            expr.symbol = new VarSymbol("NONAMESELERRNORECIVER", BuiltInType.ERROR, expr.pos);
            
            if (rt.noError()) {
                if (!rt.isClassType()) {
                    issue(new NotClassFieldError(expr.pos, expr.name.get(), rt.toString()));
                    return;
                }
                var ct = (ClassType) rt;
                // System.out.println("search name " + ct.name + " " + expr.name.get());
                var field = ctx.getClass(ct.name).scope.lookup(expr.name.get());
                if (!field.isPresent()) {
                    issue(new FieldNotFoundError(expr.pos, expr.name.get(), ct.toString()));
                    return;
                }
                var var = field.get();
                // System.out.println("find field " + var.type + var.name);
                if (receiver instanceof Tree.VarSel) {
                    var v1 = (Tree.VarSel) receiver;
                    if (v1.isClassName) {
                        // special case like MyClass.foo: report error cannot access field 'foo' from 'class : MyClass'
                        if (!(var.isMethodSymbol() &&
                            ((MethodSymbol)var).isStatic())) {
                            issue(new NotClassFieldError(expr.pos, expr.name.get(), v1.name.get()));
                            return;
                        }
                    }
                }
                if (var.isVarSymbol()) {
                    if (((VarSymbol)var).isMemberVar()) {
                        if (!ctx.currentClass().type.subtypeOf(((VarSymbol)var).getOwner().type)) {
                            // member vars are protected
                            issue(new FieldNotAccessError(expr.pos, expr.name.get(), ct.toString()));
                            return;
                        }
                    }
                    expr.symbol = var;
                    return;
                }

                if (var.isMethodSymbol()) {
                    // System.out.println("typer: find method symbol " + var.name + var.type);
                    expr.symbol = var;
                    return;
                }

                if (var.isLambdaSymbol()) {
                    // maybe should check as method?
                    // System.out.println("typer: find lambda symbol " + var.name + var.type);
                    expr.symbol = var;
                    return;
                }

                if (var.isClassSymbol()) {
                    // System.out.println("typer: find class symbol " + var.name + var.type);
                    expr.symbol = var;
                    return;
                }
                
                issue(new NotClassFieldError(expr.pos, expr.name.get(), ct.toString()));
            } 
        }
    }

    @Override
    public void visitIndexSel(Tree.IndexSel expr, ScopeStack ctx) {
        expr.array.accept(this, ctx);
        expr.index.accept(this, ctx);
        var at = expr.array.symbol.type;
        var it = expr.index.symbol.type;
        expr.symbol = new VarSymbol("NONAMEINDEXSELERR", BuiltInType.ERROR, expr.pos);
        if (!at.hasError()) {
            if (!at.isArrayType()) {
                issue(new NotArrayError(expr.array.pos));
                return;
            }
            expr.symbol = new VarSymbol("NONAMEINDEXSEL", ((ArrayType) at).elementType, expr.pos);
            if (!it.hasError() && !it.eq(BuiltInType.INT)) {
                issue(new SubNotIntError(expr.pos));
            }
        }
    }

    @Override
    public void visitCall(Tree.Call expr, ScopeStack ctx) {
        // System.out.println(expr.pos + "visitCall" + expr);
        expr.caller.accept(this, ctx);
        expr.symbol = new VarSymbol("NonameCallErr", BuiltInType.ERROR, expr.pos);
        // System.out.println(expr.pos + "visitcallafter" + expr.caller.symbol.type + expr.caller.name);
        if (expr.caller.symbol.type.noError()) {
            if (!expr.caller.symbol.type.isFuncType()) {
                issue(new NotCallableError(expr.pos, expr.caller.symbol.type.toString()));
                return;
            }

            String callerName = expr.name.isPresent() ? "function '"+expr.name.get()+"'" : "lambda expression";
            expr.symbol = new VarSymbol("NonameCanCallErr",  ((FunType)expr.caller.symbol.type).returnType, expr.pos);
            // System.out.println(expr.pos + "call, set rettype " + expr.symbol.type);
            if (expr.caller instanceof VarSel) {
                VarSel caller = (VarSel) expr.caller;
                if (caller.receiver.isPresent()) {
                    var rt = caller.receiver.get().symbol.type;
                    if (rt.isArrayType() && expr.name.isPresent() && expr.name.get().equals("length")) { // Special case: array.length()
                        if (!expr.args.isEmpty()) {
                            issue(new BadLengthArgError(expr.pos, expr.args.size()));
                        }
                        expr.isArrayLength = true;
                        expr.symbol = new VarSymbol("NonameCallLength", BuiltInType.INT, expr.pos);
                        return;
                    }
                }
            }

            var args = expr.args;
            for (var arg : args) {
                arg.accept(this, ctx);
            }
            assert expr.caller.symbol.isLambdaSymbol() || expr.caller.symbol.isMethodSymbol();
            FunType et = (FunType) expr.caller.symbol.type;
            // check signature compatibility
            if (et.arity() != args.size()) {
                issue(new BadArgCountError(expr.pos, callerName, et.arity(), args.size()));
            }
            var iter1 = et.argTypes.iterator();
            var iter2 = expr.args.iterator();
            for (int i = 1; iter1.hasNext() && iter2.hasNext(); i++) {
                Type t1 = iter1.next();
                Tree.Expr e = iter2.next();
                Type t2 = e.symbol.type;
                if (t2.noError() && !t2.subtypeOf(t1)) {
                    issue(new BadArgTypeError(e.pos, i, t2.toString(), t1.toString()));
                }
            }
            expr.symbol = new VarSymbol("NoNameVisCallSucc", et.returnType, expr.pos);
        } 
    }

    @Override
    public void visitLambda(Lambda lambda, ScopeStack ctx) {
        ctx.open(lambda.scope);
        if (lambda.ty == LambdaType.EXPR) {
            ctx.open(lambda.scope.nestedLocalScope());
            Tree.Expr expr = (Tree.Expr) lambda.ret;
            lambda.ret.accept(this, ctx);
            lambda.symbol = new VarSymbol("NonameExprLambda", new FunType(expr.symbol.type, ((FunType)lambda.symbol.type).argTypes), lambda.pos);
            ctx.close();
        } else {
            Tree.Block blk = (Tree.Block) lambda.ret;
            lambda.ret.accept(this, ctx);
            // System.out.println("visit lambda, blk ret " + blk.returnType);
            lambda.symbol = new VarSymbol("NonameBlockLambda", new FunType(blk.returnType, ((FunType)lambda.symbol.type).argTypes), lambda.pos);
        }
        ctx.close();
    }

    @Override
    public void visitClassTest(Tree.ClassTest expr, ScopeStack ctx) {
        expr.obj.accept(this, ctx);
        expr.symbol = new VarSymbol("nonameClassTest", BuiltInType.BOOL, expr.pos);

        if (!expr.obj.symbol.type.isClassType()) {
            issue(new NotClassError(expr.obj.symbol.type.toString(), expr.pos));
        }
        var clazz = ctx.lookupClass(expr.is.name);
        if (clazz.isEmpty()) {
            issue(new ClassNotFoundError(expr.pos, expr.is.name));
        } else {
            expr.symbol = clazz.get();
        }
    }

    @Override
    public void visitClassCast(Tree.ClassCast expr, ScopeStack ctx) {
        expr.obj.accept(this, ctx);

        if (!expr.obj.symbol.type.isClassType()) {
            issue(new NotClassError(expr.obj.symbol.type.toString(), expr.pos));
        }

        var clazz = ctx.lookupClass(expr.to.name);
        if (clazz.isEmpty()) {
            issue(new ClassNotFoundError(expr.pos, expr.to.name));
            expr.symbol = new VarSymbol("nonameClassCastErr", BuiltInType.ERROR, expr.pos);
        } else {
            expr.symbol = clazz.get();
        }
    }

    @Override
    public void visitLocalVarDef(Tree.LocalVarDef stmt, ScopeStack ctx) {
        if (stmt.initVal.isEmpty()) return;
        var initVal = stmt.initVal.get();
        localVarDefPos = Optional.ofNullable(stmt.id.pos);
        initVal.accept(this, ctx);
        localVarDefPos = Optional.empty();
        
        var lt = stmt.symbol.type;
        var rt = initVal.symbol.type;
        // System.out.println(stmt.pos + "in localvardef: lt " + lt + "rt " + rt + initVal.symbol.name);
        if (lt.eq(BuiltInType.VAR)) {
            VarSymbol ns = new VarSymbol(stmt.symbol.name, rt, stmt.symbol.pos);
            ns.setDomain(stmt.symbol.domain());
            stmt.symbol = ns;
            ctx.updateSymbol(stmt.symbol.name, ns);
            lt = rt;
            if (rt.eq(BuiltInType.VOID))
                issue(new BadVarTypeError(stmt.pos, stmt.name));
        }

        if (lt.noError() && !rt.subtypeOf(lt)) {
            issue(new IncompatBinOpError(stmt.assignPos, lt.toString(), "=", rt.toString()));
        }
    }

    // Only usage: check if an initializer cyclically refers to the declared variable, e.g. var x = x + 1
    private Optional<Pos> localVarDefPos = Optional.empty();
}
