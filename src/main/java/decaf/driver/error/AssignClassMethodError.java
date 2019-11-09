package decaf.driver.error;

import decaf.frontend.tree.Pos;

/**
 * example：cannot assign value to class member method 'f'
 * PA2
 */
public class AssignClassMethodError extends DecafError {

    private String method;

    public AssignClassMethodError(Pos pos, String method) {
        super(pos);
        this.method = method;
    }

    @Override
    protected String getErrMsg() {
        return "cannot assign value to class member method '" + method + "'";
    }
}
