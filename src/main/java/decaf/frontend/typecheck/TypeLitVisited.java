package decaf.frontend.typecheck;

import java.util.ArrayList;

import decaf.driver.ErrorIssuer;
import decaf.driver.error.BadArrElementError;
import decaf.driver.error.ClassNotFoundError;
import decaf.frontend.scope.ScopeStack;
import decaf.frontend.tree.Tree;
import decaf.frontend.tree.Visitor;
import decaf.frontend.tree.Tree.TLambda;
import decaf.frontend.type.BuiltInType;
import decaf.frontend.type.Type;

/**
 * Infer the types of type literals in the abstract syntax tree.
 * <p>
 * These visitor methods are shared by {@link Namer} and {@link Typer}.
 */
public interface TypeLitVisited extends Visitor<ScopeStack>, ErrorIssuer {

    // visiting types
    @Override
    default void visitTInt(Tree.TInt that, ScopeStack ctx) {
        that.type = BuiltInType.INT;
    }

    @Override
    default void visitTBool(Tree.TBool that, ScopeStack ctx) {
        that.type = BuiltInType.BOOL;
    }

    @Override
    default void visitTString(Tree.TString that, ScopeStack ctx) {
        that.type = BuiltInType.STRING;
    }

    @Override
    default void visitTVoid(Tree.TVoid that, ScopeStack ctx) {
        that.type = BuiltInType.VOID;
    }

    @Override
    default void visitTVar(Tree.TVar that, ScopeStack ctx) {
        that.type = BuiltInType.VAR;
    }

    @Override
    default void visitTClass(Tree.TClass typeClass, ScopeStack ctx) {
        var c = ctx.lookupClass(typeClass.id.name);
        if (c.isEmpty()) {
            issue(new ClassNotFoundError(typeClass.pos, typeClass.id.name));
            typeClass.type = BuiltInType.ERROR;
        } else {
            typeClass.type = c.get().type;
        }
    }

    @Override
    default void visitTArray(Tree.TArray typeArray, ScopeStack ctx) {
        typeArray.elemType.accept(this, ctx);
        if (typeArray.elemType.type.eq(BuiltInType.ERROR)) {
            typeArray.type = BuiltInType.ERROR;
        } else if (typeArray.elemType.type.eq(BuiltInType.VOID)) {
            issue(new BadArrElementError(typeArray.pos));
            typeArray.type = BuiltInType.ERROR;
        } else {
            typeArray.type = new decaf.frontend.type.ArrayType(typeArray.elemType.type);
        }
    }

    @Override
    default void visitTLambda(TLambda typeLambda, ScopeStack ctx) {
        boolean isError = false;
        ArrayList<Type> argTypes = new ArrayList<>();
        for (var type: typeLambda.argTypes) {
            type.accept(this, ctx);
            argTypes.add(type.type);
            if (type.type.eq(BuiltInType.ERROR))
                isError = true;
        }
        typeLambda.retType.accept(this, ctx);
        if (typeLambda.retType.type.eq(BuiltInType.ERROR))
            isError = true;
        for (var type: typeLambda.argTypes) {
            if (type.type.eq(BuiltInType.VOID)) {
                issue(new BadArrElementError(type.pos));
                isError = true;
            }
        }
        if (isError)
            typeLambda.type = BuiltInType.ERROR;
        else
            typeLambda.type = new decaf.frontend.type.FunType(typeLambda.retType.type, argTypes);
    }

}
