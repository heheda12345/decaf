package decaf.driver.error;

import decaf.frontend.tree.Pos;

/**
 * example：string is not a callable type
 * PA2
 */
public class NotCallableError extends DecafError {

    public NotCallableError(Pos pos, String type) {
        super(pos);
        this.type = type;
    }

    private String type;

    @Override
    protected String getErrMsg() {
        return type + " is not a callable type";
    }

}
