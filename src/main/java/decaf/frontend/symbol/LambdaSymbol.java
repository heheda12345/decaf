package decaf.frontend.symbol;

import decaf.frontend.scope.ClassScope;
import decaf.frontend.scope.LambdaScope;
import decaf.frontend.tree.Pos;
import decaf.frontend.type.FunType;
import decaf.lowlevel.instr.Temp;

/**
 * Lambda symbol, representing a lambda definition.
 */
public final class LambdaSymbol extends Symbol {

    public final FunType type;

    /**
     * Associated lambda scope of the lambda parameters.
     */
    public final LambdaScope scope;

    public LambdaSymbol(String name, FunType type, LambdaScope scope, Pos pos) {
        super(name, type, pos);
        this.type = type;
        this.scope = scope;
    }

    @Override
    public boolean isLambdaSymbol() {
        return true;
    }

    @Override
    protected String str() {
        return String.format("function %s : %s", name, type);
    }

    public boolean isLocalLambda() {
        return definedIn.isLocalScope();
    }

    public boolean isParam() {
        return definedIn.isFormalScope();
    }

    public boolean isMemberLambda() {
        return definedIn.isClassScope();
    }

    public boolean isLambdaLambda() {
        return definedIn.isLambdaScope();
    }

    public ClassSymbol getOwner() {
        if (!isMemberLambda()) {
            throw new IllegalArgumentException("this var symbol is not a member var");
        }
        return ((ClassScope) definedIn).getOwner();
    }

    public Temp temp;
}
