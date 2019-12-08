package decaf.frontend.tacgen;

import decaf.frontend.symbol.LambdaSymbol;
import decaf.frontend.symbol.MethodSymbol;
import decaf.frontend.symbol.VarSymbol;
import decaf.frontend.tree.Tree;
import decaf.frontend.tree.Visitor;
import decaf.frontend.tree.Tree.Lambda;
import decaf.frontend.tree.Tree.VarSel;
import decaf.frontend.tree.Tree.Lambda.LambdaType;
import decaf.frontend.type.BuiltInType;
import decaf.frontend.type.FunType;
import decaf.lowlevel.instr.Temp;
import decaf.lowlevel.label.Label;
import decaf.lowlevel.tac.ClassInfo;
import decaf.lowlevel.tac.FuncVisitor;
import decaf.lowlevel.tac.Intrinsic;
import decaf.lowlevel.tac.ProgramWriter;
import decaf.lowlevel.tac.RuntimeError;
import decaf.lowlevel.tac.TacInstr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * TAC emitter. Traverse the tree and emit TAC.
 * <p>
 * When emitting TAC, we use utility methods from {@link FuncVisitor}, so that we don't bother
 * ourselves understanding the underlying format of TAC instructions.
 * <p>
 * See {@link #emitIfThen} for the usage of {@link Consumer}.
 */
public interface TacEmitter extends Visitor<FuncVisitor> {

    /**
     * Record the exit labels of loops entered so far. In this way, when we encounter a break statement, we know the
     * exact label we will jump to.
     * <p>
     * Push a label when entering a loop, and pop when leaving a loop.
     */
    Stack<Label> loopExits = new Stack<>();

    @Override
    default void visitBlock(Tree.Block block, FuncVisitor mv) {
        for (var stmt : block.stmts) {
            stmt.accept(this, mv);
        }
    }

    @Override
    default void visitLocalVarDef(Tree.LocalVarDef def, FuncVisitor mv) {
        def.symbol.temp = mv.freshTemp();
        if (def.initVal.isEmpty()) return;
        var initVal = def.initVal.get();

        initVal.accept(this, mv);
        mv.visitAssign(def.symbol.temp, initVal.symbol.temp);
    }

    @Override
    default void visitAssign(Tree.Assign assign, FuncVisitor mv) {
        if (assign.lhs instanceof Tree.IndexSel) {
            var indexSel = (Tree.IndexSel) assign.lhs;
            indexSel.array.accept(this, mv);
            indexSel.index.accept(this, mv);
            var addr = emitArrayElementAddress(indexSel.array.symbol.temp, indexSel.index.symbol.temp, mv);
            assign.rhs.accept(this, mv);
            mv.visitStoreTo(addr, assign.rhs.symbol.temp);
        } else if (assign.lhs instanceof Tree.VarSel) {
            var v = (Tree.VarSel) assign.lhs;
            assert(v.symbol instanceof VarSymbol);
            var symbol = (VarSymbol) v.symbol;
            if (symbol.isMemberVar()) {
                var object = v.receiver.get();
                object.accept(this, mv);
                assign.rhs.accept(this, mv);
                assert(v.name.isPresent());
                mv.visitMemberWrite(object.symbol.temp, symbol.getOwner().name, v.name.get(), assign.rhs.symbol.temp);
            } else { // local or param
                assign.rhs.accept(this, mv);
                mv.visitAssign(symbol.temp, assign.rhs.symbol.temp);
            }
        }
    }

    @Override
    default void visitExprEval(Tree.ExprEval eval, FuncVisitor mv) {
        eval.expr.accept(this, mv);
    }

    @Override
    default void visitIf(Tree.If stmt, FuncVisitor mv) {
        stmt.cond.accept(this, mv);
        Consumer<FuncVisitor> trueBranch = v -> stmt.trueBranch.accept(this, v);

        if (stmt.falseBranch.isEmpty()) {
            emitIfThen(stmt.cond.symbol.temp, trueBranch, mv);
        } else {
            Consumer<FuncVisitor> falseBranch = v -> stmt.falseBranch.get().accept(this, v);
            emitIfThenElse(stmt.cond.symbol.temp, trueBranch, falseBranch, mv);
        }
    }

    @Override
    default void visitWhile(Tree.While loop, FuncVisitor mv) {
        var exit = mv.freshLabel();
        Function<FuncVisitor, Temp> test = v -> {
            loop.cond.accept(this, v);
            return loop.cond.symbol.temp;
        };
        Consumer<FuncVisitor> body = v -> {
            loopExits.push(exit);
            loop.body.accept(this, v);
            loopExits.pop();
        };
        emitWhile(test, body, exit, mv);
    }

    @Override
    default void visitFor(Tree.For loop, FuncVisitor mv) {
        var exit = mv.freshLabel();
        loop.init.accept(this, mv);
        Function<FuncVisitor, Temp> test = v -> {
            loop.cond.accept(this, v);
            return loop.cond.symbol.temp;
        };
        Consumer<FuncVisitor> body = v -> {
            loopExits.push(exit);
            loop.body.accept(this, v);
            loopExits.pop();
            loop.update.accept(this, v);
        };
        emitWhile(test, body, exit, mv);
    }

    @Override
    default void visitBreak(Tree.Break stmt, FuncVisitor mv) {
        mv.visitBranch(loopExits.peek());
    }

    @Override
    default void visitReturn(Tree.Return stmt, FuncVisitor mv) {
        if (stmt.expr.isEmpty()) {
            mv.visitReturn();
        } else {
            var expr = stmt.expr.get();
            expr.accept(this, mv);
            mv.visitReturn(expr.symbol.temp);
        }
    }

    @Override
    default void visitPrint(Tree.Print stmt, FuncVisitor mv) {
        for (var expr : stmt.exprs) {
            expr.accept(this, mv);
            if (expr.symbol.type.eq(BuiltInType.INT)) {
                mv.visitIntrinsicCall(Intrinsic.PRINT_INT, expr.symbol.temp);
            } else if (expr.symbol.type.eq(BuiltInType.BOOL)) {
                mv.visitIntrinsicCall(Intrinsic.PRINT_BOOL, expr.symbol.temp);
            } else if (expr.symbol.type.eq(BuiltInType.STRING)) {
                mv.visitIntrinsicCall(Intrinsic.PRINT_STRING, expr.symbol.temp);
            }
        }
    }

    // Expressions

    @Override
    default void visitIntLit(Tree.IntLit expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitLoad(expr.value);
    }

    @Override
    default void visitBoolLit(Tree.BoolLit expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitLoad(expr.value);
    }

    @Override
    default void visitStringLit(Tree.StringLit expr, FuncVisitor mv) {
        // Remember to unquote the string literal
        var unquoted = expr.value.substring(1, expr.value.length() - 1)
                .replaceAll("\\\\r", "\r")
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t")
                .replaceAll("\\\\\\\\", "\\")
                .replaceAll("\\\\\"", "\"");
                expr.symbol.temp = mv.visitLoad(unquoted);
    }

    @Override
    default void visitNullLit(Tree.NullLit expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitLoad(0);
    }

    @Override
    default void visitReadInt(Tree.ReadInt expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitIntrinsicCall(Intrinsic.READ_INT, true);
    }

    @Override
    default void visitReadLine(Tree.ReadLine expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitIntrinsicCall(Intrinsic.READ_LINE, true);
    }

    @Override
    default void visitUnary(Tree.Unary expr, FuncVisitor mv) {
        var op = switch (expr.op) {
            case NEG -> TacInstr.Unary.Op.NEG;
            case NOT -> TacInstr.Unary.Op.LNOT;
        };

        expr.operand.accept(this, mv);
        expr.symbol.temp = mv.visitUnary(op, expr.operand.symbol.temp);
    }

    @Override
    default void visitBinary(Tree.Binary expr, FuncVisitor mv) {
        if ((expr.op.equals(Tree.BinaryOp.EQ) || expr.op.equals(Tree.BinaryOp.NE)) &&
                expr.lhs.symbol.type.eq(BuiltInType.STRING)) { // string eq/ne
            expr.lhs.accept(this, mv);
            expr.rhs.accept(this, mv);
            expr.symbol.temp = mv.visitIntrinsicCall(Intrinsic.STRING_EQUAL, true, expr.lhs.symbol.temp, expr.rhs.symbol.temp);
            if (expr.op.equals(Tree.BinaryOp.NE)) {
                mv.visitUnarySelf(TacInstr.Unary.Op.LNOT, expr.symbol.temp);
            }
            return;
        }

        var op = switch (expr.op) {
            case ADD -> TacInstr.Binary.Op.ADD;
            case SUB -> TacInstr.Binary.Op.SUB;
            case MUL -> TacInstr.Binary.Op.MUL;
            case DIV -> TacInstr.Binary.Op.DIV;
            case MOD -> TacInstr.Binary.Op.MOD;
            case EQ -> TacInstr.Binary.Op.EQU;
            case NE -> TacInstr.Binary.Op.NEQ;
            case LT -> TacInstr.Binary.Op.LES;
            case LE -> TacInstr.Binary.Op.LEQ;
            case GT -> TacInstr.Binary.Op.GTR;
            case GE -> TacInstr.Binary.Op.GEQ;
            case AND -> TacInstr.Binary.Op.LAND;
            case OR -> TacInstr.Binary.Op.LOR;
        };
        expr.lhs.accept(this, mv);
        expr.rhs.accept(this, mv);
        if (op ==  TacInstr.Binary.Op.DIV || op == TacInstr.Binary.Op.MOD) {
            var zero = mv.visitLoad(0);
            var error = mv.visitBinary(TacInstr.Binary.Op.EQU, expr.rhs.symbol.temp, zero);
            var handler = new Consumer<FuncVisitor>() {
                @Override
                public void accept(FuncVisitor v) {
                    v.visitPrint(RuntimeError.DIVISION_BY_ZERO);
                    v.visitIntrinsicCall(Intrinsic.HALT);
                }
            };
            emitIfThen(error, handler, mv);
        }
        
        expr.symbol.temp = mv.visitBinary(op, expr.lhs.symbol.temp, expr.rhs.symbol.temp);
    }

    @Override
    default void visitLambda(Lambda lambda, FuncVisitor mv) {
        // System.out.println("capture-" + lambda.name + " " + lambda.scope.getCapturedName());
        var capturedExpr = lambda.scope.getCapturedExpr();
        int totCap = capturedExpr.size();
        assert(lambda.symbol.isLambdaSymbol());
        var symbol = (LambdaSymbol) lambda.symbol;
        int numArgs = symbol.type.arity();
        var mvFunc = mv.freshFunc(lambda.name, numArgs + 1);
        var funcObj = mvFunc.getArgTemp(0);
        // parameter
        int cnt = 0;
        for (var param : lambda.params) {
            param.symbol.temp = mvFunc.getArgTemp(cnt + 1);
            cnt++;
        }

        // parse the lambda
        var oldVal = new ArrayList<Temp>();
        cnt = 4;
        for (var expr: capturedExpr) {
            oldVal.add(expr.symbol.temp);
            expr.symbol.temp = mvFunc.visitLoadFrom(funcObj, cnt);
            if (expr.symbol.isClassSymbol()) {
                mvFunc.thisAt = cnt;
                // System.out.println("[lambda] this is at " + (cnt >> 2));
            }
            cnt += 4;
        }
        // System.out.println("oldVal " +oldVal);
        lambda.ret.accept(this, mvFunc);
        if (lambda.ty == LambdaType.EXPR) {
            Tree.Expr expr = (Tree.Expr) lambda.ret;
            mvFunc.visitReturn(expr.symbol.temp);
        }
        mvFunc.visitEnd();
        cnt = 0;
        for (var expr: capturedExpr) {
            expr.symbol.temp = oldVal.get(cnt);
            cnt++;
        }

        var addr = mv.visitLoadVTable(mvFunc.funcLabel.clazz);
        var funcPointer = mv.visitLoadFrom(addr, 8);

        var a = mv.visitIntrinsicCall(Intrinsic.ALLOCATE, true, mv.visitLoad((totCap + 1) * 4));
        mv.visitStoreTo(a, 0, funcPointer);
        cnt = 4;
        for (var expr: lambda.scope.getCapturedExpr()) {
            if (expr.symbol.isClassSymbol()) {
                mv.visitStoreTo(a, cnt, getThisTemp(mv));
            } else {
                assert(expr.symbol.isVarSymbol());
                mv.visitStoreTo(a, cnt, expr.symbol.temp);
            }
            cnt += 4;
        }
        ((LambdaSymbol)lambda.symbol).temp = a;
    }

    @Override
    default void visitVarSel(Tree.VarSel expr, FuncVisitor mv) {
        // System.out.println("visitVarSel " + expr.pos + expr.symbol.getClass() + " " + expr.symbol.type);
        if (expr.symbol.isMethodSymbol()) {
            var symbol = (MethodSymbol) expr.symbol;
            assert(expr.name.isPresent());
            int numArgs = symbol.type.arity();
            if (expr.receiver.isPresent()) {
                expr.receiver.get().accept(this, mv);
            }
            if (symbol.isStatic()) {
                // translate the new function
                var mvFunc = mv.freshFunc(expr.name.get(), numArgs + 1);
                var args = new ArrayList<Temp>();
                for (int i=0; i<numArgs; i++)
                    args.add(mvFunc.getArgTemp(i+1));
                // System.out.println("methodsymbol " + symbol.type + " " + symbol.type.returnType.isVoidType());
                if (symbol.type.returnType.isVoidType()) {
                    mvFunc.visitStaticCall(symbol.owner.name, expr.name.get(), args);
                    mvFunc.visitReturn();
                } else {
                    var ret = mvFunc.visitStaticCall(symbol.owner.name, expr.name.get(), args, true);
                    mvFunc.visitReturn(ret);
                }
                mvFunc.visitEnd();

                // translate the function variable
                var funcPointer = mv.visitNewClass(mvFunc.funcLabel.clazz);
                funcPointer = mv.visitLoadFrom(funcPointer, 0);
                funcPointer = mv.visitLoadFrom(funcPointer, 8);
                var four = mv.visitLoad(4);
                var a = mv.visitIntrinsicCall(Intrinsic.ALLOCATE, true, four);
                mv.visitStoreTo(a, 0, funcPointer);
                expr.symbol.temp = a;
            } else {
                // translate the new function
                var mvFunc = mv.freshFunc(expr.name.get(), numArgs + 1);
                var funcObj = mvFunc.getArgTemp(0);
                funcObj = mvFunc.visitLoadFrom(funcObj, 4);
                var args = new ArrayList<Temp>();
                for (int i=0; i<numArgs; i++)
                    args.add(mvFunc.getArgTemp(i+1));
                // System.out.println("methodsymbol " + symbol.type + " " + symbol.type.returnType.isVoidType());
                if (symbol.type.returnType.isVoidType()) {
                    mvFunc.visitMemberCall(funcObj, symbol.owner.name, expr.name.get(), args);
                    mvFunc.visitReturn();
                } else {
                    var ret = mvFunc.visitMemberCall(funcObj, symbol.owner.name, expr.name.get(), args, true);
                    mvFunc.visitReturn(ret);
                }
                mvFunc.visitEnd();
    
                // translate the function variable
                var funcPointer = mv.visitNewClass(mvFunc.funcLabel.clazz);
                funcPointer = mv.visitLoadFrom(funcPointer, 0);
                funcPointer = mv.visitLoadFrom(funcPointer, 8);
                var eight = mv.visitLoad(8);
                var a = mv.visitIntrinsicCall(Intrinsic.ALLOCATE, true, eight);
                mv.visitStoreTo(a, 0, funcPointer);
                assert(expr.receiver.isPresent());
                mv.visitStoreTo(a, 4, expr.receiver.get().symbol.temp);
                expr.symbol.temp = a;
            }
        } else if (expr.symbol.isClassSymbol()) {
            return;
        } else {
            assert(expr.symbol instanceof VarSymbol);
            assert(expr.name.isPresent());
            var symbol = (VarSymbol)expr.symbol;
            if (symbol.isMemberVar()) {
                var object = expr.receiver.get();
                object.accept(this, mv);
                expr.symbol.temp = mv.visitMemberAccess(object.symbol.temp, symbol.getOwner().name, expr.name.get());
            } else { // local or param
                expr.symbol.temp = symbol.temp;
            }
        }
    }

    @Override
    default void visitIndexSel(Tree.IndexSel expr, FuncVisitor mv) {
        expr.array.accept(this, mv);
        expr.index.accept(this, mv);
        var addr = emitArrayElementAddress(expr.array.symbol.temp, expr.index.symbol.temp, mv);
        expr.symbol.temp = mv.visitLoadFrom(addr);
    }

    @Override
    default void visitNewArray(Tree.NewArray expr, FuncVisitor mv) {
        expr.length.accept(this, mv);
        expr.symbol.temp = emitArrayInit(expr.length.symbol.temp, mv);
    }

    @Override
    default void visitNewClass(Tree.NewClass expr, FuncVisitor mv) {
        expr.symbol.temp = mv.visitNewClass(expr.symbol.name);
    }

    @Override
    default void visitThis(Tree.This expr, FuncVisitor mv) {
        expr.symbol.temp = getThisTemp(mv);
    }

    @Override
    default void visitCall(Tree.Call expr, FuncVisitor mv) {
        if (expr.isArrayLength) { // special case for array.length()
            var array = ((VarSel)expr.caller).receiver.get();
            array.accept(this, mv);
            expr.symbol.temp = mv.visitLoadFrom(array.symbol.temp, -4);
            return;
        }

        expr.args.forEach(arg -> arg.accept(this, mv));
        var temps = new ArrayList<Temp>();
        expr.args.forEach(arg -> temps.add(arg.symbol.temp));

        // System.out.println("visitCall " + expr.pos + " " + expr.caller.symbol.getClass());
        if (expr.caller.symbol.isMethodSymbol() || expr.caller.symbol.isLambdaSymbol()) {
            var symbol = (MethodSymbol)expr.caller.symbol;
            if (symbol.isStatic()) {
                if (symbol.type.returnType.isVoidType()) {
                    mv.visitStaticCall(symbol.owner.name, symbol.name, temps);
                } else {
                    expr.symbol.temp = mv.visitStaticCall(symbol.owner.name, symbol.name, temps, true);
                }
            } else {
                assert(((VarSel)expr.caller).receiver.isPresent());
                var object = ((VarSel)expr.caller).receiver.get();
                object.accept(this, mv);
                if (symbol.type.returnType.isVoidType()) {
                    mv.visitMemberCall(object.symbol.temp, symbol.owner.name, symbol.name, temps);
                } else {
                    expr.symbol.temp = mv.visitMemberCall(object.symbol.temp, symbol.owner.name, symbol.name, temps, true);
                }
            }
        } else {
            // System.out.println("type " + expr.caller.symbol.type.getClass() + " " + expr.caller.symbol.type);
            assert(expr.caller.symbol.type.isFuncType());
            expr.caller.accept(this, mv);
            if (((FunType)expr.caller.symbol.type).returnType.isVoidType()) {
                mv.visitExtendCall(expr.caller.symbol.temp, temps);
            } else {
                expr.symbol.temp = mv.visitExtendCall(expr.caller.symbol.temp, temps, true);
            }
        }
    }

    @Override
    default void visitClassTest(Tree.ClassTest expr, FuncVisitor mv) {
        // Accelerate: when obj.type <: class.type, then the test must be successful!
        if (expr.obj.symbol.type.subtypeOf(expr.symbol.type)) {
            expr.symbol.temp = mv.visitLoad(1);
            return;
        }

        expr.obj.accept(this, mv);
        expr.symbol.temp = emitClassTest(expr.obj.symbol.temp, expr.symbol.name, mv);
    }

    @Override
    default void visitClassCast(Tree.ClassCast expr, FuncVisitor mv) {
        expr.obj.accept(this, mv);
        expr.symbol.temp = expr.obj.symbol.temp;

        // Accelerate: when obj.type <: class.type, then the test must success!
        if (expr.obj.symbol.type.subtypeOf(expr.symbol.type)) {
            return;
        }
        var result = emitClassTest(expr.obj.symbol.temp, expr.symbol.name, mv);

        /* Pseudo code:
         * <pre>
         *     if (result != 0) branch exit  // cast success
         *     print "Decaf runtime error: " // RuntimeError.CLASS_CAST_ERROR1
         *     vtbl1 = *obj                  // vtable of obj
         *     fromClass = *(vtbl1 + 4)      // name of obj's class
         *     print fromClass
         *     print " cannot be cast to "   // RuntimeError.CLASS_CAST_ERROR2
         *     vtbl2 = load vtbl of the target class
         *     toClass = *(vtbl2 + 4)        // name of target class
         *     print toClass
         *     print "\n"                    // RuntimeError.CLASS_CAST_ERROR3
         *     halt
         * exit:
         * </pre>
         */
        var exit = mv.freshLabel();
        mv.visitBranch(TacInstr.CondBranch.Op.BNEZ, result, exit);
        mv.visitPrint(RuntimeError.CLASS_CAST_ERROR1);
        var vtbl1 = mv.visitLoadFrom(expr.obj.symbol.temp);
        var fromClass = mv.visitLoadFrom(vtbl1, 4);
        mv.visitIntrinsicCall(Intrinsic.PRINT_STRING, fromClass);
        mv.visitPrint(RuntimeError.CLASS_CAST_ERROR2);
        var vtbl2 = mv.visitLoadVTable(expr.symbol.name);
        var toClass = mv.visitLoadFrom(vtbl2, 4);
        mv.visitIntrinsicCall(Intrinsic.PRINT_STRING, toClass);
        mv.visitPrint(RuntimeError.CLASS_CAST_ERROR3);
        mv.visitIntrinsicCall(Intrinsic.HALT);
        mv.visitLabel(exit);
    }

    /**
     * Emit code for the following conditional statement:
     * <pre>
     *     if (cond) {
     *         action
     *     }
     * </pre>
     * <p>
     * Implementation in pseudo code:
     * <pre>
     *     if (cond == 0) branch skip;
     *     action
     * skip:
     * </pre>
     * <p>
     * Why {@link Consumer} for the true branch? Because the method visitor will append TAC code <em>in order</em>.
     * Since the instructions of the true branch go AFTER the conditional branch instruction, we must first append the
     * conditional branch, and then the true branch. So instead of appending the code first, which is wrong, we must
     * wrap the <em>process</em> which emits the actual code as a function {@link FuncVisitor} {@literal ->} void,
     * expressed by {@link Consumer} in Java. Same story for the helper methods below.
     *
     * @param cond   temp of condition
     * @param action code (to be generated) of the true branch
     * @param mv     current method visitor
     */
    private void emitIfThen(Temp cond, Consumer<FuncVisitor> action, FuncVisitor mv) {
        var skip = mv.freshLabel();
        mv.visitBranch(TacInstr.CondBranch.Op.BEQZ, cond, skip);
        action.accept(mv);
        mv.visitLabel(skip);
    }

    /**
     * Emit code for the following conditional statement:
     * <pre>
     *     if (cond) {
     *         trueBranch
     *     } else {
     *         falseBranch
     *     }
     * </pre>
     * <p>
     * Implementation in pseudo code:
     * <pre>
     *     if (cond == 0) branch skip
     *     trueBranch
     *     branch exit
     * skip:
     *     falseBranch
     * exit:
     * </pre>
     *
     * @param cond        temp of condition
     * @param trueBranch  code (to be generated) of the true branch
     * @param falseBranch code (to be generated) of the false branch
     * @param mv          current method visitor
     */
    private void emitIfThenElse(Temp cond, Consumer<FuncVisitor> trueBranch, Consumer<FuncVisitor> falseBranch,
                                FuncVisitor mv) {
        var skip = mv.freshLabel();
        var exit = mv.freshLabel();
        mv.visitBranch(TacInstr.CondBranch.Op.BEQZ, cond, skip);
        trueBranch.accept(mv);
        mv.visitBranch(exit);
        mv.visitLabel(skip);
        falseBranch.accept(mv);
        mv.visitLabel(exit);
    }

    /**
     * Emit code for the following loop:
     * <pre>
     *     while (cond) {
     *         block
     *     }
     * </pre>
     * <p>
     * Implementation in pseudo code:
     * <pre>
     * entry:
     *     cond = do test
     *     if (cond == 0) branch exit
     *     do block
     *     branch entry
     * exit:
     * </pre>
     *
     * @param test  code (to be generated) of the loop condition
     * @param block code (to be generated) of the loop body
     * @param exit  label of loop exit
     * @param mv    current method visitor
     */
    private void emitWhile(Function<FuncVisitor, Temp> test, Consumer<FuncVisitor> block,
                           Label exit, FuncVisitor mv) {
        var entry = mv.freshLabel();
        mv.visitLabel(entry);
        var cond = test.apply(mv);
        mv.visitBranch(TacInstr.CondBranch.Op.BEQZ, cond, exit);
        block.accept(mv);
        mv.visitBranch(entry);
        mv.visitLabel(exit);
    }

    /**
     * Emit code for initializing a new array.
     * <p>
     * In memory, an array of length {@code n} takes {@code (n + 1) * 4} bytes:
     * - the first 4 bytes: length
     * - the rest bytes: data
     * <p>
     * Pseudo code:
     * <pre>
     *     error = length {@literal <} 0
     *     if (error) {
     *         throw RuntimeError.NEGATIVE_ARR_SIZE
     *     }
     *
     *     units = length + 1
     *     size = units * 4
     *     a = ALLOCATE(size)
     *     *(a + 0) = length
     *     p = a + size
     *     p -= 4
     *     while (p != a) {
     *         *(p + 0) = 0
     *         p -= 4
     *     }
     *     ret = (a + 4)
     * </pre>
     *
     * @param length temp of array length
     * @param mv     current method visitor
     * @return a temp storing the address of the first element of the array
     */
    private Temp emitArrayInit(Temp length, FuncVisitor mv) {
        var zero = mv.visitLoad(0);
        var error = mv.visitBinary(TacInstr.Binary.Op.LES, length, zero);
        var handler = new Consumer<FuncVisitor>() {
            @Override
            public void accept(FuncVisitor v) {
                v.visitPrint(RuntimeError.NEGATIVE_ARR_SIZE);
                v.visitIntrinsicCall(Intrinsic.HALT);
            }
        };
        emitIfThen(error, handler, mv);

        var units = mv.visitBinary(TacInstr.Binary.Op.ADD, length, mv.visitLoad(1));
        var four = mv.visitLoad(4);
        var size = mv.visitBinary(TacInstr.Binary.Op.MUL, units, four);
        var a = mv.visitIntrinsicCall(Intrinsic.ALLOCATE, true, size);
        mv.visitStoreTo(a, length);
        var p = mv.visitBinary(TacInstr.Binary.Op.ADD, a, size);
        mv.visitBinarySelf(TacInstr.Binary.Op.SUB, p, four);
        Function<FuncVisitor, Temp> test = v -> v.visitBinary(TacInstr.Binary.Op.NEQ, p, a);
        var body = new Consumer<FuncVisitor>() {
            @Override
            public void accept(FuncVisitor v) {
                v.visitStoreTo(p, zero);
                v.visitBinarySelf(TacInstr.Binary.Op.SUB, p, four);
            }
        };
        emitWhile(test, body, mv.freshLabel(), mv);
        return mv.visitBinary(TacInstr.Binary.Op.ADD, a, four);
    }

    /**
     * Emit code for computing the address of an array element.
     * <p>
     * Pseudo code:
     * <pre>
     *     length = *(array - 4)
     *     error1 = index {@literal <} 0
     *     error2 = index {@literal >=} length
     *     error = error1 || error2
     *     if (error) {
     *         throw RuntimeError.ARRAY_INDEX_OUT_OF_BOUND
     *     }
     *
     *     offset = index * 4
     *     ret = array + offset
     * </pre>
     *
     * @param array temp of the array
     * @param index temp of the index
     * @return a temp storing the address of the element
     */
    private Temp emitArrayElementAddress(Temp array, Temp index, FuncVisitor mv) {
        var length = mv.visitLoadFrom(array, -4);
        var zero = mv.visitLoad(0);
        var error1 = mv.visitBinary(TacInstr.Binary.Op.LES, index, zero);
        var error2 = mv.visitBinary(TacInstr.Binary.Op.GEQ, index, length);
        var error = mv.visitBinary(TacInstr.Binary.Op.LOR, error1, error2);
        var handler = new Consumer<FuncVisitor>() {
            @Override
            public void accept(FuncVisitor v) {
                v.visitPrint(RuntimeError.ARRAY_INDEX_OUT_OF_BOUND);
                v.visitIntrinsicCall(Intrinsic.HALT);
            }
        };
        emitIfThen(error, handler, mv);

        var four = mv.visitLoad(4);
        var offset = mv.visitBinary(TacInstr.Binary.Op.MUL, index, four);
        return mv.visitBinary(TacInstr.Binary.Op.ADD, array, offset);
    }

    /**
     * Emit code for testing if an object is an instance of class.
     * <p>
     * Pseudo code:
     * <pre>
     *     target = LoadVtbl(clazz)
     *     t = *object
     * loop:
     *     ret = t == target
     *     if (ret != 0) goto exit
     *     t = *t
     *     if (t != 0) goto loop
     *     ret = 0 // t == null
     * exit:
     * </pre>
     *
     * @param object temp of the object/instance
     * @param clazz  name of the class
     * @return a temp storing the result (1 for true, and 0 for false)
     */
    private Temp emitClassTest(Temp object, String clazz, FuncVisitor mv) {
        var target = mv.visitLoadVTable(clazz);
        var t = mv.visitLoadFrom(object);

        var loop = mv.freshLabel();
        var exit = mv.freshLabel();
        mv.visitLabel(loop);
        var ret = mv.visitBinary(TacInstr.Binary.Op.EQU, t, target);
        mv.visitBranch(TacInstr.CondBranch.Op.BNEZ, ret, exit);
        mv.visitRaw(new TacInstr.Memory(TacInstr.Memory.Op.LOAD, t, t, 0));
        mv.visitBranch(TacInstr.CondBranch.Op.BNEZ, t, loop);
        var zero = mv.visitLoad(0);
        mv.visitAssign(ret, zero);
        mv.visitLabel(exit);

        return ret;
    }

    private Temp getThisTemp(FuncVisitor mv) {
        return mv.thisAt > 0 ?
            mv.visitLoadFrom(mv.getArgTemp(0), mv.thisAt) : mv.getArgTemp(0);
    }
}
