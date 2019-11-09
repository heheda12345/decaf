package decaf.frontend.scope;

import decaf.frontend.symbol.LambdaSymbol;
import decaf.frontend.symbol.Symbol;
import decaf.frontend.tree.Pos;

/**
 * Lambda scope: stores parameter variable symbols. It is owned by a lambda symbol.
 */
public class LambdaScope extends Scope {

    public LambdaScope(Scope parent) {
        super(Kind.LAMBDA);
        assert parent.isLocalScope();
        ((LocalScope) parent).nestedScopes().add(this);
    }

    public LambdaSymbol getOwner() {
        return owner;
    }

    public void setOwner(LambdaSymbol owner) {
        this.owner = owner;
    }

    @Override
    public boolean isLambdaScope() {
        return true;
    }

    /**
     * Get the local scope associated with the method body.
     *
     * @return local scope
     */
    public LocalScope nestedLocalScope() {
        return nested;
    }

    /**
     * Set the local scope.
     *
     * @param scope local scope
     */
    void setNested(LocalScope scope) {
        nested = scope;
    }

    private LambdaSymbol owner;

    private LocalScope nested;
}
