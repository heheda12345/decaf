package decaf.driver.error;

import decaf.frontend.tree.Pos;

// incompatible return types in blocked expression (in lambda's block)

public class BadInferTypeError extends DecafError {

    public BadInferTypeError(Pos pos) {
        super(pos);
    }

    @Override
    protected String getErrMsg() {
        return "incompatible return types in blocked expression";
    }
}
