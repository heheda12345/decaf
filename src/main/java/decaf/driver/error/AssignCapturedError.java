package decaf.driver.error;

import decaf.frontend.tree.Pos;

/**
 * example：cannot assign value to class member method 'f'
 * PA2
 */
public class AssignCapturedError extends DecafError {

    public AssignCapturedError(Pos pos) {
        super(pos);
    }

    @Override
    protected String getErrMsg() {
        return "cannot assign value to captured variables in lambda expression";
    }
}
