package decaf.driver.error;

import decaf.frontend.tree.Pos;

/**
 * example：unterminated string constant: "this is str"<br>
 * PA1
 */
public class UseAbstractClassError extends DecafError {

    private String name;

    public UseAbstractClassError(Pos pos, String name) {
        super(pos);
        this.name = name;
    }

    @Override
    protected String getErrMsg() {
        return "cannot instantiate abstract class '" + name + "'";
    }

}
